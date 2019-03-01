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
import com.google.gson.annotations.Expose;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;
import com.continuumsecurity.elasticagent.ec2.Ec2Instance;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.JobCompletionRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;

public class JobCompletionRequest {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    @Expose
    private String elasticAgentId;
    @Expose
    private JobIdentifier jobIdentifier;

    public JobCompletionRequest() {
    }

    public JobCompletionRequest(String elasticAgentId, JobIdentifier jobIdentifier) {
        this.elasticAgentId = elasticAgentId;
        this.jobIdentifier = jobIdentifier;
    }

    public static JobCompletionRequest fromJSON(String json) {
        JobCompletionRequest jobCompletionRequest = GSON.fromJson(json, JobCompletionRequest.class);
        return jobCompletionRequest;
    }

    public String getElasticAgentId() {
        return elasticAgentId;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    public RequestExecutor executor(AgentInstance<Ec2Instance> agentInstances, PluginRequest pluginRequest) {
        return new JobCompletionRequestExecutor(this, agentInstances, pluginRequest);
    }

    @Override
    public String toString() {
        return "JobCompletionRequest{" +
                "elasticAgentId='" + elasticAgentId + '\'' +
                ", jobIdentifier=" + jobIdentifier +
                '}';
    }
}
