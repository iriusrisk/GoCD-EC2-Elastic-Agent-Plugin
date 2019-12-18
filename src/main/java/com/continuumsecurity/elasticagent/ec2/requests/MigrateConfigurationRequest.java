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
 */

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.ClusterProfile;
import com.continuumsecurity.elasticagent.ec2.ElasticAgentProfile;
import com.continuumsecurity.elasticagent.ec2.PluginSettings;
import com.continuumsecurity.elasticagent.ec2.executors.MigrateConfigurationRequestExecutor;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.GSON;

public class MigrateConfigurationRequest {

    @Expose
    @SerializedName("plugin_settings")
    private PluginSettings pluginSettings;

    @Expose
    @SerializedName("elastic_agent_profiles")
    private List<ElasticAgentProfile> elasticAgentProfiles;

    @Expose
    @SerializedName("cluster_profiles")
    private List<ClusterProfile> clusterProfiles;

    public MigrateConfigurationRequest() {
    }

    public MigrateConfigurationRequest(PluginSettings pluginSettings,
                                       List<ClusterProfile> clusterProfiles,
                                       List<ElasticAgentProfile> elasticAgentProfiles) {
        this.pluginSettings = pluginSettings;
        this.clusterProfiles = clusterProfiles;
        this.elasticAgentProfiles = elasticAgentProfiles;
    }

    public static MigrateConfigurationRequest fromJSON(String json) {
        return GSON.fromJson(json, MigrateConfigurationRequest.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public MigrateConfigurationRequestExecutor executor() {
        return new MigrateConfigurationRequestExecutor(this);
    }

    public PluginSettings getPluginSettings() {
        return pluginSettings;
    }

    public void setPluginSettings(PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public List<ClusterProfile> getClusterProfiles() {
        return clusterProfiles;
    }

    public void setClusterProfiles(List<ClusterProfile> clusterProfiles) {
        this.clusterProfiles = clusterProfiles;
    }

    public List<ElasticAgentProfile> getElasticAgentProfiles() {
        return elasticAgentProfiles;
    }

    public void setElasticAgentProfiles(List<ElasticAgentProfile> elasticAgentProfiles) {
        this.elasticAgentProfiles = elasticAgentProfiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrateConfigurationRequest that = (MigrateConfigurationRequest) o;
        return Objects.equals(pluginSettings, that.pluginSettings) &&
                Objects.equals(clusterProfiles, that.clusterProfiles) &&
                Objects.equals(elasticAgentProfiles, that.elasticAgentProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginSettings, clusterProfiles, elasticAgentProfiles);
    }

    @Override
    public String toString() {
        return "MigrateConfigurationRequest{" +
                "pluginSettings=" + pluginSettings +
                ", clusterProfiles=" + clusterProfiles +
                ", elasticAgentProfiles=" + elasticAgentProfiles +
                '}';
    }
}