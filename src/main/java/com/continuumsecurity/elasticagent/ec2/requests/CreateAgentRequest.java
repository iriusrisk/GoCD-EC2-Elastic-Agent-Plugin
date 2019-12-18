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

import com.continuumsecurity.elasticagent.ec2.*;
import com.continuumsecurity.elasticagent.ec2.executors.CreateAgentRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.GSON;

public class CreateAgentRequest {

    private String autoRegisterKey;
    private JobIdentifier jobIdentifier;
    private Map<String, String> elasticAgentProfileProperties;
    private ClusterProfileProperties clusterProfileProperties;

    public CreateAgentRequest() {
    }

    public CreateAgentRequest(String autoRegisterKey,
                              Map<String, String> elasticAgentProfileProperties,
                              JobIdentifier jobIdentifier,
                              Map<String, String> clusterProfileProperties) {
        this.autoRegisterKey = autoRegisterKey;
        this.elasticAgentProfileProperties = elasticAgentProfileProperties;
        this.jobIdentifier = jobIdentifier;
        this.clusterProfileProperties = ClusterProfileProperties.fromConfiguration(clusterProfileProperties);
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

    public static CreateAgentRequest fromJSON(String json) {
        return GSON.fromJson(json, CreateAgentRequest.class);
    }

    public RequestExecutor executor(AgentInstances agentInstances, PluginRequest pluginRequest) {
        return new CreateAgentRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Properties autoregisterProperties(String elasticAgentId) {
        Properties properties = new Properties();

        if (StringUtils.isNotBlank(autoRegisterKey)) {
            properties.put("agent.auto.register.key", autoRegisterKey);
        }

        properties.put("agent.auto.register.elasticAgent.agentId", elasticAgentId);
        properties.put("agent.auto.register.elasticAgent.pluginId", Constants.PLUGIN_ID);

        return properties;
    }

    public String autoregisterPropertiesAsEnvironmentVars(String elasticAgentId) {
        Properties properties = autoregisterProperties(elasticAgentId);

        StringWriter writer = new StringWriter();

        try {
            properties.store(writer, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    public Map<String, String> properties() {
        return elasticAgentProfileProperties;
    }

    public ClusterProfileProperties getClusterProfileProperties() {
        return clusterProfileProperties;
    }

}
