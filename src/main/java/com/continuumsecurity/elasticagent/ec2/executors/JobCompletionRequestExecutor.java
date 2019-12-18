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
import com.continuumsecurity.elasticagent.ec2.requests.JobCompletionRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Arrays;
import java.util.List;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class JobCompletionRequestExecutor implements RequestExecutor {
    private final JobCompletionRequest jobCompletionRequest;
    private final AgentInstances<Ec2Instance> agentInstances;
    private final PluginRequest pluginRequest;

    public JobCompletionRequestExecutor(JobCompletionRequest jobCompletionRequest, AgentInstances<Ec2Instance> agentInstances, PluginRequest pluginRequest) {
        this.jobCompletionRequest = jobCompletionRequest;
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        ClusterProfileProperties clusterProfileProperties = jobCompletionRequest.getClusterProfileProperties();
        String elasticAgentId = jobCompletionRequest.getElasticAgentId();
        Agent agent = new Agent(elasticAgentId);
        LOG.info("[Job Completion] Terminating elastic agent with id {} on job completion {} in cluster {}.", agent.elasticAgentId(), jobCompletionRequest.jobIdentifier(), clusterProfileProperties);
        List<Agent> agents = Arrays.asList(agent);
        pluginRequest.disableAgents(agents);
        agentInstances.terminate(agent.elasticAgentId(), clusterProfileProperties);
        pluginRequest.deleteAgents(agents);
        return DefaultGoPluginApiResponse.success("");
    }
}
