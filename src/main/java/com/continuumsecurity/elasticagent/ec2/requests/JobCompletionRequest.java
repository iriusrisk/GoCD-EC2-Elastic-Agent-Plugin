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
import com.continuumsecurity.elasticagent.ec2.executors.JobCompletionRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class JobCompletionRequest {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Expose
    @SerializedName("elastic_agent_id")
    private String elasticAgentId;

    @Expose
    @SerializedName("job_identifier")
    private JobIdentifier jobIdentifier;

    @Expose
    @SerializedName("elastic_agent_profile_properties")
    private Map<String, String> elasticAgentProfileProperties;

    @Expose
    @SerializedName("cluster_profile_properties")
    private ClusterProfileProperties clusterProfileProperties;

    public JobCompletionRequest() {
    }

    public JobCompletionRequest(String elasticAgentId,
                                JobIdentifier jobIdentifier,
                                Map<String, String> elasticAgentProfileProperties,
                                Map<String, String> clusterProfile) {
        this.elasticAgentId = elasticAgentId;
        this.jobIdentifier = jobIdentifier;
        this.elasticAgentProfileProperties = elasticAgentProfileProperties;
        this.clusterProfileProperties = ClusterProfileProperties.fromConfiguration(clusterProfile);
    }

    public JobCompletionRequest(String elasticAgentId,
                                JobIdentifier jobIdentifier,
                                Map<String, String> elasticAgentProfileProperties,
                                ClusterProfileProperties clusterProfileProperties) {
        this.elasticAgentId = elasticAgentId;
        this.jobIdentifier = jobIdentifier;
        this.elasticAgentProfileProperties = elasticAgentProfileProperties;
        this.clusterProfileProperties = clusterProfileProperties;
    }

    public static JobCompletionRequest fromJSON(String json) {
        return GSON.fromJson(json, JobCompletionRequest.class);
    }

    public String getElasticAgentId() {
        return elasticAgentId;
    }

    public JobIdentifier jobIdentifier() {
        return jobIdentifier;
    }

    public RequestExecutor executor(AgentInstances<Ec2Instance> agentInstances, PluginRequest pluginRequest) {
        return new JobCompletionRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Map<String, String> getProperties() {
        return elasticAgentProfileProperties;
    }

    public ClusterProfileProperties getClusterProfileProperties() {
        return clusterProfileProperties;
    }

    @Override
    public String toString() {
        return "JobCompletionRequest{" +
                "elasticAgentId='" + elasticAgentId + '\'' +
                ", jobIdentifier=" + jobIdentifier +
                ", elasticAgentProfileProperties=" + elasticAgentProfileProperties +
                ", clusterProfileProperties=" + clusterProfileProperties +
                '}';
    }
}
