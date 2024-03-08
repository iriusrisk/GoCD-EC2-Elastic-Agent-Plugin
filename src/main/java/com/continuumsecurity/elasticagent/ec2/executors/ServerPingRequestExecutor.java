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

package com.continuumsecurity.elasticagent.ec2.executors;

import com.continuumsecurity.elasticagent.ec2.*;
import com.continuumsecurity.elasticagent.ec2.requests.ServerPingRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.*;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class ServerPingRequestExecutor implements RequestExecutor {

    private final ServerPingRequest serverPingRequest;
    private final Map<String, Ec2AgentInstances> clusterSpecificAgentInstances;
    private final PluginRequest pluginRequest;

    public ServerPingRequestExecutor(ServerPingRequest serverPingRequest, Map<String, Ec2AgentInstances> clusterSpecificAgentInstances, PluginRequest pluginRequest) {
        this.serverPingRequest = serverPingRequest;
        this.clusterSpecificAgentInstances = clusterSpecificAgentInstances;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        Set<Agent> possiblyMissingAgents = new HashSet<>();
        List<ClusterProfileProperties> allClusterProfileProperties = serverPingRequest.allClusterProfileProperties();

        for (ClusterProfileProperties clusterProfileProperties : allClusterProfileProperties) {
            performCleanupForACluster(clusterProfileProperties, clusterSpecificAgentInstances.get(clusterProfileProperties.uuid()), possiblyMissingAgents);
        }

        refreshInstancesAgainToCheckForPossiblyMissingAgents(allClusterProfileProperties, possiblyMissingAgents);
        return DefaultGoPluginApiResponse.success("");
    }

    private void performCleanupForACluster(ClusterProfileProperties clusterProfileProperties, Ec2AgentInstances ec2AgentInstances, Set<Agent> possiblyMissingAgents) throws Exception {
        Agents allAgents = pluginRequest.listAgents();

        for (Agent agent : allAgents.agents()) {
            if (ec2AgentInstances.find(agent.elasticAgentId()) == null) {
                possiblyMissingAgents.add(agent);
            } else {
                possiblyMissingAgents.remove(agent);
            }
        }

        Agents agentsToDisable = ec2AgentInstances.instancesCreatedAfterTimeout(clusterProfileProperties, allAgents);
        disableIdleAgents(agentsToDisable);

        allAgents = pluginRequest.listAgents();
        terminateDisabledAgents(allAgents, clusterProfileProperties, ec2AgentInstances);

        ec2AgentInstances.terminateUnregisteredInstances(clusterProfileProperties, allAgents);
    }

    private void refreshInstancesAgainToCheckForPossiblyMissingAgents(List<ClusterProfileProperties> allClusterProfileProperties, Set<Agent> possiblyMissingAgents) throws Exception {
        Ec2AgentInstances ec2AgentInstances = new Ec2AgentInstances();
        for (ClusterProfileProperties clusterProfileProperties : allClusterProfileProperties) {
            ec2AgentInstances.refreshAll(clusterProfileProperties);
        }

        Agents missingAgents = new Agents();
        for (Agent possiblyMissingAgent : possiblyMissingAgents) {
            if (!ec2AgentInstances.hasInstance(possiblyMissingAgent.elasticAgentId())) {
                LOG.warn("[Server Ping] Was expecting an instance " + possiblyMissingAgent.elasticAgentId() + ", but it was missing!");
                missingAgents.add(possiblyMissingAgent);
            }
        }

        pluginRequest.disableAgents(missingAgents.agents());
        pluginRequest.deleteAgents(missingAgents.agents());
    }

    private void disableIdleAgents(Agents agents) throws ServerRequestFailedException {
        pluginRequest.disableAgents(agents.findInstancesToDisable());
    }

    private void terminateDisabledAgents(Agents agents, ClusterProfileProperties clusterProfileProperties, Ec2AgentInstances ec2AgentInstances) throws Exception {
        Collection<Agent> toBeDeleted = agents.findInstancesToTerminate();

        for (Agent agent : toBeDeleted) {
            LOG.info("INSTANCE_TERMINATION|DISABLED_AGENTS|instance_id={}&agent_state={}", agent.elasticAgentId(), agent.agentState().toString());
            ec2AgentInstances.terminate(agent.elasticAgentId(), clusterProfileProperties);
        }

        pluginRequest.deleteAgents(toBeDeleted);
    }

}
