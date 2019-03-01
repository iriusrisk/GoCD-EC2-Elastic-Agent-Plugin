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

import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collection;

import com.continuumsecurity.elasticagent.ec2.Agent;
import com.continuumsecurity.elasticagent.ec2.AgentInstance;
import com.continuumsecurity.elasticagent.ec2.Agents;
import com.continuumsecurity.elasticagent.ec2.PluginSettings;
import com.continuumsecurity.elasticagent.ec2.ServerRequestFailedException;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class ServerPingRequestExecutor implements RequestExecutor {

    private final AgentInstance agentInstances;
    private final PluginRequest pluginRequest;

    public ServerPingRequestExecutor(AgentInstance agentInstances, PluginRequest pluginRequest) {
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        PluginSettings pluginSettings = pluginRequest.getPluginSettings();

        Agents allAgents = pluginRequest.listAgents();
        Agents missingAgents = new Agents();

        for (Agent agent : allAgents.agents()) {
            if (agentInstances.find(agent.elasticAgentId()) == null) {
                LOG.warn("Was expecting an instance with name " + agent.elasticAgentId() + ", but it was missing!");
                missingAgents.add(agent);
            }
        }

        Agents agentsToDisable = agentInstances.instancesCreatedAfterTimeout(pluginSettings, allAgents);
        agentsToDisable.addAll(missingAgents);

        disableIdleAgents(agentsToDisable);

        allAgents = pluginRequest.listAgents();
        terminateDisabledAgents(allAgents, pluginSettings);

        agentInstances.terminateUnregisteredInstances(pluginSettings, allAgents);

        return DefaultGoPluginApiResponse.success("");
    }

    private void disableIdleAgents(Agents agents) throws ServerRequestFailedException {
        pluginRequest.disableAgents(agents.findInstancesToDisable());
    }

    private void terminateDisabledAgents(Agents agents, PluginSettings pluginSettings) throws Exception {
        Collection<Agent> toBeDeleted = agents.findInstancesToTerminate();

        for (Agent agent : toBeDeleted) {
            agentInstances.terminate(agent.elasticAgentId(), pluginSettings);
        }

        pluginRequest.deleteAgents(toBeDeleted);
    }

}
