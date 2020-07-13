/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file incorporates changes by @continuumsecurity
 */

package com.continuumsecurity.elasticagent.ec2;

import com.continuumsecurity.elasticagent.ec2.models.AgentStatusReport;
import com.continuumsecurity.elasticagent.ec2.models.InstanceStatusReport;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class Ec2AgentInstances implements AgentInstances<Ec2Instance> {

    private final ConcurrentHashMap<String, Ec2Instance> instances = new ConcurrentHashMap<>();
    private List<JobIdentifier> jobsWaitingForAgentCreation = new ArrayList<>();
    private boolean refreshed;
    public Clock clock = Clock.DEFAULT;

    private final Semaphore semaphore = new Semaphore(0, true);
    @Override
    public Ec2Instance create(CreateAgentRequest request, PluginRequest pluginRequest, ConsoleLogAppender consoleLogAppender) {

        LOG.info(String.format("[Create Agent] Processing create agent request for %s", request.jobIdentifier()));
        ClusterProfileProperties settings = request.getClusterProfileProperties();

        final Integer maxAllowedAgents = settings.getMaxElasticAgents();
        synchronized (instances) {
            if (!jobsWaitingForAgentCreation.contains(request.jobIdentifier())) {
                jobsWaitingForAgentCreation.add(request.jobIdentifier());
            }
            doWithLockOnSemaphore(new SetupSemaphore(maxAllowedAgents, instances, semaphore));
            List<Map<String, String>> messages = new ArrayList<>();
            if (semaphore.tryAcquire()) {
                pluginRequest.addServerHealthMessage(messages);
                Ec2Instance instance = Ec2Instance.create(request, settings, consoleLogAppender);
                register(instance);
                jobsWaitingForAgentCreation.remove(request.jobIdentifier());
                return instance;
            } else {
                String maxLimitExceededMessage = String.format("The number of instances currently running is currently at the maximum permissible limit, \"%d\". Not creating more instances for jobs: %s.", instances.size(), jobsWaitingForAgentCreation.stream().map(JobIdentifier::getRepresentation)
                        .collect(Collectors.joining(", ")));
                Map<String, String> messageToBeAdded = new HashMap<>();
                messageToBeAdded.put("type", "warning");
                messageToBeAdded.put("message", maxLimitExceededMessage);
                messages.add(messageToBeAdded);
                pluginRequest.addServerHealthMessage(messages);
                consoleLogAppender.accept(maxLimitExceededMessage);
                LOG.warn(maxLimitExceededMessage);
                return null;
            }
        }
    }

    private void doWithLockOnSemaphore(Runnable runnable) {
        synchronized (semaphore) {
            runnable.run();
        }
    }

    @Override
    public void terminate(String agentId, ClusterProfileProperties clusterProfileProperties) throws Exception {
        Ec2Instance instance = instances.get(agentId);
        if (instance != null) {
            instance.terminate(clusterProfileProperties);
        } else {
            LOG.warn("Requested to terminate an instance that does not exist " + agentId);
        }

        doWithLockOnSemaphore(new Runnable() {
            @Override
            public void run() {
                semaphore.release();
            }
        });

        synchronized (instances) {
            instances.remove(agentId);
        }
    }

    @Override
    public void terminateUnregisteredInstances(ClusterProfileProperties clusterProfileProperties, Agents agents) throws Exception {

        Ec2AgentInstances toTerminate = unregisteredAfterTimeout(clusterProfileProperties, agents);
        if (toTerminate.instances.isEmpty()) {
            return;
        }

        LOG.warn("Terminating instances that did not register " + toTerminate.instances.keySet());
        for (Ec2Instance instance : toTerminate.instances.values()) {
            terminate(instance.id(), clusterProfileProperties);
        }
    }

    private Ec2AgentInstances unregisteredAfterTimeout(PluginSettings settings, Agents knownAgents) throws Exception {
        Period period = settings.getAutoRegisterPeriod();
        Ec2AgentInstances unregisteredContainers = new Ec2AgentInstances();

        for (String instanceId : instances.keySet()) {
            if (knownAgents.containsAgentWithId(instanceId)) {
                continue;
            }

            Ec2Instance instance = instances.get(instanceId);

            if (clock.now().isAfter(instance.createdAt().plus(period))) {
                unregisteredContainers.register(instance);
            }
        }
        return unregisteredContainers;
    }

    @Override
    public Agents instancesCreatedAfterTimeout(ClusterProfileProperties clusterProfileProperties, Agents agents) {
        ArrayList<Agent> oldAgents = new ArrayList<>();
        for (Agent agent : agents.agents()) {
            Ec2Instance instance = instances.get(agent.elasticAgentId());
            if (instance == null) {
                continue;
            }

            if (clock.now().isAfter(instance.createdAt().plus(clusterProfileProperties.getAutoRegisterPeriod()))) {
                oldAgents.add(agent);
            }
        }
        return new Agents(oldAgents);
    }

    @Override
    public void refreshAll(ClusterProfileProperties clusterProfileProperties) throws Exception {
        if (!refreshed) {
    
            Ec2Client ec2 = Ec2Instance.createEc2Client(
                               clusterProfileProperties.getAwsAccessKeyId(),
                               clusterProfileProperties.getAwsSecretAccessKey(),
                               clusterProfileProperties.getAwsRegion()
                            );

            DescribeInstancesResponse response = ec2.describeInstances(
                    DescribeInstancesRequest.builder()
                            .filters(
                                    Filter.builder()
                                            .name("instance-state-name")
                                            .values("pending", "running")
                                            .build(),
                                    Filter.builder()
                                            .name("tag:type")
                                            .values(Constants.ELASTIC_AGENT_TAG)
                                            .build()
                            )
                            .build()
            );

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    Map<String, String> properties = new HashMap<>();
                    properties.put("ec2_ami", instance.imageId());
                    properties.put("ec2_instance_type", instance.instanceTypeAsString());
                    properties.put("ec2_sg", StringUtils.join(instance.securityGroups(), ","));
                    properties.put("ec2_subnets", instance.subnetId());
                    properties.put("ec2_key", instance.keyName());

                    register(new Ec2Instance(instance.instanceId(),
                            Date.from(instance.launchTime()),
                            properties,
                            JobIdentifier.fromJson(getTag(instance.tags(), "JsonJobIdentifier")))
                    );
                    LOG.debug("Refreshed instance " + instance.instanceId());
                }
            }
            refreshed = true;
        }
    }

    @Override
    public Ec2Instance find(String agentId) {
        return instances.get(agentId);
    }

    @Override
    public Ec2Instance find(JobIdentifier jobIdentifier) {
        return instances.values()
                .stream()
                .filter(x -> x.getJobIdentifier().equals(jobIdentifier))
                .findFirst()
                .orElse(null);
    }

    @Override
    public StatusReport getStatusReport(ClusterProfileProperties clusterProfileProperties) throws Exception {
            Ec2Client ec2 = Ec2Instance.createEc2Client(
                               clusterProfileProperties.getAwsAccessKeyId(),
                               clusterProfileProperties.getAwsSecretAccessKey(),
                               clusterProfileProperties.getAwsRegion()
                            );

        DescribeInstancesResponse response = ec2.describeInstances(
                DescribeInstancesRequest.builder()
                        .filters(
                                Filter.builder()
                                        .name("instance-state-name")
                                        .values("pending", "running", "shutting-down", "stopping", "stopped")
                                        .build(),
                                Filter.builder()
                                        .name("tag:type")
                                        .values(Constants.ELASTIC_AGENT_TAG)
                                        .build()
                        )
                        .build()
        );

        List<InstanceStatusReport> instanceStatusReportList = new ArrayList<>();

        for (Reservation reservation : response.reservations()) {
            for (Instance instance : reservation.instances()) {
                instanceStatusReportList.add(new InstanceStatusReport(
                        instance.instanceId(),
                        instance.instanceTypeAsString(),
                        instance.imageId(),
                        instance.state().nameAsString(),
                        instance.privateIpAddress(),
                        Date.from(instance.launchTime()).getTime(),
                        extractPipelineNameFromTags(instance.tags())
                ));
            }
        }

        LOG.info("Status report " + instanceStatusReportList.size() + " instances");

        return new StatusReport(instanceStatusReportList.size(), instanceStatusReportList);
    }

    private String extractPipelineNameFromTags(List<Tag> tags) {
        for (Tag tag : tags) {
            if (tag.key().equals("pipelineName")) {
                return tag.value();
            }
        }
        return null;
    }

    @Override
    public AgentStatusReport getAgentStatusReport(ClusterProfileProperties clusterProfileProperties, Ec2Instance agentInstance) {
            Ec2Client ec2 = Ec2Instance.createEc2Client(
                               clusterProfileProperties.getAwsAccessKeyId(),
                               clusterProfileProperties.getAwsSecretAccessKey(),
                               clusterProfileProperties.getAwsRegion()
                            );


        DescribeInstancesResponse response = ec2.describeInstances(
                DescribeInstancesRequest.builder()
                        .filters(
                                Filter.builder()
                                        .name("instance-id")
                                        .values(agentInstance.id())
                                        .build(),
                                Filter.builder()
                                        .name("tag:type")
                                        .values(Constants.ELASTIC_AGENT_TAG)
                                        .build()
                        )
                        .build()
        );

        Instance instance = response.reservations().get(0).instances().get(0);

        return new AgentStatusReport(
                agentInstance.getJobIdentifier(),
                instance,
                agentInstance.createdAt().getMillis()
        );
    }

    // used by tests
    public boolean hasInstance(String agentId) {
        return instances.containsKey(agentId);
    }

    private void register(Ec2Instance instance) {
        instances.put(instance.id(), instance);
    }

    @Nullable
    private static String getTag(List<Tag> tags, String key) {
        for (Tag tag : tags) {
            if (tag.key().equals(key)) {
                return tag.value();
            }
        }
        return null;
    }

}
