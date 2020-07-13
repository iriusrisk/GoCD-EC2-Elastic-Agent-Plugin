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

import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import org.joda.time.DateTime;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.*;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class Ec2Instance {
    private final DateTime createdAt;
    private final Map<String, String> properties;
    private final JobIdentifier jobIdentifier;
    private String id;

    public Ec2Instance(String id, Date createdAt, Map<String, String> properties, JobIdentifier jobIdentifier) {
        this.id = id;
        this.createdAt = new DateTime(createdAt);
        this.properties = properties;
        this.jobIdentifier = jobIdentifier;
    }

    public String id() {
        return id;
    }

    public DateTime createdAt() {
        return createdAt;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public static Ec2Instance create(CreateAgentRequest request, PluginSettings settings, ConsoleLogAppender consoleLogAppender) {

        LOG.debug("Creating new instance for " + request.jobIdentifier().getRepresentation());

        Ec2Client ec2 = createEc2Client(settings.getAwsAccessKeyId(), settings.getAwsSecretAccessKey(), settings.getAwsRegion());

        String userdata = "#!/bin/bash\n" +
                "sed -ri \"s,http[s]?://localhost:[0-9]+/go," + settings.getGoServerUrl() + ",g\" /usr/share/go-agent/wrapper-config/wrapper-properties.conf\n" +
                "echo \"wrapper.app.parameter.102=-sslVerificationMode\" >> /usr/share/go-agent/wrapper-config/wrapper-properties.conf\n" +
                "echo \"wrapper.app.parameter.103=NONE\" >> /usr/share/go-agent/wrapper-config/wrapper-properties.conf\n" +
                "mkdir -p /var/lib/go-agent/config\n" +
                "echo \"agent.auto.register.key=" + request.autoRegisterKey() + "\" > /var/lib/go-agent/config/autoregister.properties\n" +
                "echo \"agent.auto.register.hostname=EA_$(ec2-metadata --instance-id | cut -d \" \" -f 2)\" >> /var/lib/go-agent/config/autoregister.properties\n" +
                "echo \"agent.auto.register.elasticAgent.agentId=$(ec2-metadata --instance-id | cut -d \" \" -f 2)\" >> /var/lib/go-agent/config/autoregister.properties\n" +
                "echo \"agent.auto.register.elasticAgent.pluginId=" + Constants.PLUGIN_ID + "\" >> /var/lib/go-agent/config/autoregister.properties\n" +
                "chown -R go:go /var/log/go-agent/\n" +
                "chown -R go:go /var/lib/go-agent/\n" +
                "chown -R go:go /usr/share/go-agent/\n";

        if (request.properties().get("ec2_user_data") != null) {
            userdata += request.properties().get("ec2_user_data") + "\n";
        }
        userdata += "systemctl start go-agent.service\n";

        List<String> securityGroups = Arrays.asList(request.properties().get("ec2_sg").split("\\s*,\\s*"));
        List<String> subnets = Arrays.asList(request.properties().get("ec2_subnets").split("\\s*,\\s*"));
        // subnet is assigned randomly from all the subnets configured
        Collections.shuffle(subnets);

        boolean result = false;
        int i = 0;

        RunInstancesResponse response = null;
        // try create instance for each AZ if error
        while (!result && i < subnets.size()) {
            try {
                Tag tagName = Tag.builder()
                        .key("Name")
                        .value("GoCD EA "
                                + request.jobIdentifier().getPipelineName()
                                + "-" + request.jobIdentifier().getPipelineCounter().toString()
                                + "-" + request.jobIdentifier().getStageName()
                                + "-" + request.jobIdentifier().getJobName())
                        .build();
                Tag tagType = Tag.builder()
                        .key("type")
                        .value(Constants.ELASTIC_AGENT_TAG)
                        .build();
                Tag tagPipelineName = Tag.builder()
                        .key("pipelineName")
                        .value(request.jobIdentifier().getPipelineName())
                        .build();
                Tag tagPipelineCounter = Tag.builder()
                        .key("pipelineCounter")
                        .value(request.jobIdentifier().getPipelineCounter().toString())
                        .build();
                Tag tagPipelineLabel = Tag.builder()
                        .key("pipelineLabel")
                        .value(request.jobIdentifier().getPipelineLabel())
                        .build();
                Tag tagStageName = Tag.builder()
                        .key("stageName")
                        .value(request.jobIdentifier().getStageName())
                        .build();
                Tag tagStageCounter = Tag.builder()
                        .key("stageCounter")
                        .value(request.jobIdentifier().getStageCounter())
                        .build();
                Tag tagJobName = Tag.builder()
                        .key("jobName")
                        .value(request.jobIdentifier().getJobName())
                        .build();
                Tag tagJobId = Tag.builder()
                        .key("jobId")
                        .value(request.jobIdentifier().getJobId().toString())
                        .build();
                Tag tagJsonJobIdentifier = Tag.builder()
                        .key("JsonJobIdentifier")
                        .value(request.jobIdentifier().toJson())
                        .build();

                TagSpecification tagSpecification = TagSpecification.builder()
                        .tags(
                                tagName,
                                tagType,
                                tagPipelineName,
                                tagPipelineCounter,
                                tagPipelineLabel,
                                tagStageName,
                                tagStageCounter,
                                tagJobName,
                                tagJobId,
                                tagJsonJobIdentifier
                        )
                        .resourceType("instance")
                        .build();

                String iamProfileName = (request.properties().get("ec2_instance_profile") == null) ? "" : request.properties().get("ec2_instance_profile");
                
                RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                        .imageId(request.properties().get("ec2_ami"))
                        .instanceType(InstanceType.fromValue(request.properties().get("ec2_instance_type")))
                        .maxCount(1)
                        .minCount(1)
                        .keyName(request.properties().get("ec2_key"))
                        .securityGroupIds(securityGroups)
                        .subnetId(subnets.get(i))
                        .iamInstanceProfile(IamInstanceProfileSpecification.builder().name(iamProfileName).build())
                        .userData(Base64.getEncoder().encodeToString(userdata.getBytes()))
                        .tagSpecifications(tagSpecification)
                        .build();

                response = ec2.runInstances(runInstancesRequest);
                result = true;

                consoleLogAppender.accept("Successfully created new instance " + response.instances().get(0).instanceId() + " in " + response.instances().get(0).subnetId());
                LOG.info("Successfully created new instance " + response.instances().get(0).instanceId() + " in " + response.instances().get(0).subnetId());
            } catch (AwsServiceException | SdkClientException e) {
                consoleLogAppender.accept("Could not create instance. " + e.getMessage());
                LOG.error("Could not create instance", e);
                response = null;
            } finally {
                i++;
            }
        }

        if (i < subnets.size() && response != null) {
            Instance instance = response.instances().get(0);

            return new Ec2Instance(instance.instanceId(), Date.from(instance.launchTime()), request.properties(), request.jobIdentifier());
        } else {
            consoleLogAppender.accept("Could not create instance in any provided subnet!");
            LOG.error("Could not create instance in any provided subnet!");
        }

        return null;
    }

    public void terminate(ClusterProfileProperties clusterProfileProperties) {

        LOG.debug("Terminating instance " + this.id());

        Ec2Client ec2 = createEc2Client(
                clusterProfileProperties.getAwsAccessKeyId(),
                clusterProfileProperties.getAwsSecretAccessKey(),
                clusterProfileProperties.getAwsRegion());

        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(this.id).build();

        try {
            ec2.terminateInstances(request);

            LOG.info("Successfully terminated EC2 instance " + this.id + " in region " + clusterProfileProperties.getAwsRegion());
        } catch (AwsServiceException | SdkClientException e) {
            LOG.error("Could not terminate instance", e);
            System.exit(1);
        } finally {
            ec2.close();
        }
    }
    
    private static boolean isNotNullOrBlank(String testString) {
    	return testString!=null && !testString.isBlank();
    }
    
    private static AwsCredentialsProvider getCredentialsProvider(String awsAccessKeyId, String awsSecretAccessKey) {
        if (isNotNullOrBlank(awsAccessKeyId) && isNotNullOrBlank(awsSecretAccessKey)) {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId,awsSecretAccessKey);
            return StaticCredentialsProvider.create(awsCredentials);
        }
        else {
            return DefaultCredentialsProvider.create();
        }
    }
    protected static Ec2Client createEc2Client(String awsAccessKeyId, String awsSecretAccessKey, Region region) {
        return Ec2Client.builder()
                .region(region)
                .credentialsProvider(getCredentialsProvider(awsAccessKeyId,awsSecretAccessKey))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ec2Instance that = (Ec2Instance) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
