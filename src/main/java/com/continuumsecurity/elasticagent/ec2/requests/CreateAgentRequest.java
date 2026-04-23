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

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.AgentInstances;
import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.CreateAgentRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.utils.Util;

import java.util.Map;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.GSON;

public class CreateAgentRequest {

    private String autoRegisterKey;
    private JobIdentifier jobIdentifier;
    private String environment;
    private Map<String, String> elasticAgentProfileProperties;
    private ClusterProfileProperties clusterProfileProperties;

    public CreateAgentRequest() {
    }

    public CreateAgentRequest(String autoRegisterKey,
                              Map<String, String> elasticAgentProfileProperties,
                              JobIdentifier jobIdentifier,
                              Map<String, String> clusterProfileProperties,
                              String environment) {
        this.autoRegisterKey = autoRegisterKey;
        this.jobIdentifier = jobIdentifier;
        this.elasticAgentProfileProperties = elasticAgentProfileProperties;
        this.clusterProfileProperties = ClusterProfileProperties.fromConfiguration(clusterProfileProperties);
        this.environment = environment;
    }

    public CreateAgentRequest(String autoRegisterKey,
                              Map<String, String> elasticAgentProfileProperties,
                              JobIdentifier jobIdentifier,
                              ClusterProfileProperties clusterProfileProperties) {
        this.autoRegisterKey = autoRegisterKey;
        this.elasticAgentProfileProperties = elasticAgentProfileProperties;
        this.jobIdentifier = jobIdentifier;
        this.clusterProfileProperties = clusterProfileProperties;
    }

    public String autoRegisterKey() {
        return autoRegisterKey;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    public String environment() {
        return environment;
    }

    public boolean hasEnvironment() {
        return !Util.isBlank(environment);
    }

    public static CreateAgentRequest fromJSON(String json) {
        return GSON.fromJson(json, CreateAgentRequest.class);
    }

    public RequestExecutor executor(AgentInstances agentInstances, PluginRequest pluginRequest) {
        return new CreateAgentRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Map<String, String> properties() {
        return elasticAgentProfileProperties;
    }

    public ClusterProfileProperties getClusterProfileProperties() {
        return clusterProfileProperties;
    }

}
