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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;
import com.continuumsecurity.elasticagent.ec2.Constants;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.CreateAgentRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;

public class CreateAgentRequest {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private String autoRegisterKey;
    private Map<String, String> properties;
    private String environment;
    private JobIdentifier jobIdentifier;


    public CreateAgentRequest() {
    }

    public CreateAgentRequest(String autoRegisterKey, Map<String, String> properties, String environment, JobIdentifier jobIdentifier) {
        this.autoRegisterKey = autoRegisterKey;
        this.properties = properties;
        this.environment = environment;
        this.jobIdentifier = jobIdentifier;
    }

    public String autoRegisterKey() {
        return autoRegisterKey;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public String environment() {
        return environment;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    public static CreateAgentRequest fromJSON(String json) {
        return GSON.fromJson(json, CreateAgentRequest.class);
    }

    public RequestExecutor executor(AgentInstance agentInstances, PluginRequest pluginRequest) {
        return new CreateAgentRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Properties autoregisterProperties(String elasticAgentId) {
        Properties properties = new Properties();

        if (StringUtils.isNotBlank(autoRegisterKey)) {
            properties.put("agent.auto.register.key", autoRegisterKey);
        }

        if (StringUtils.isNotBlank(environment)) {
            properties.put("agent.auto.register.environments", environment);
        }

        properties.put("agent.auto.register.elasticAgent.agentId", elasticAgentId);
        properties.put("agent.auto.register.elasticAgent.pluginId", Constants.PLUGIN_ID);

        return properties;
    }

    public String autoregisterPropertiesAsString(String elasticAgentId) {
        Properties properties = autoregisterProperties(elasticAgentId);

        StringWriter writer = new StringWriter();

        try {
            properties.store(writer, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    public String propertiesToJson() {
        return GSON.toJson(this.properties);
    }

    public static Map<String, String> propertiesFromJson(String json) {
        return GSON.fromJson(json, Map.class);
    }

}
