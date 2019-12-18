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

import com.continuumsecurity.elasticagent.ec2.executors.*;
import com.continuumsecurity.elasticagent.ec2.requests.*;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class Ec2Plugin implements GoPlugin {

    public static final Logger LOG = Logger.getLoggerFor(Ec2Plugin.class);
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private PluginRequest pluginRequest;
    private Map<String, Ec2AgentInstances> clusterSpecificAgentInstances;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        pluginRequest = new PluginRequest(accessor);
        clusterSpecificAgentInstances = new HashMap<>();
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return Constants.PLUGIN_IDENTIFIER;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        ClusterProfileProperties clusterProfileProperties;
        try {
            switch (Request.fromString(request.requestName())) {
                case REQUEST_GET_ICON:
                    return new GetPluginIconExecutor().execute();

                case REQUEST_SHOULD_ASSIGN_WORK:
                    ShouldAssignWorkRequest shouldAssignWorkRequest = ShouldAssignWorkRequest.fromJSON(request.requestBody());
                    clusterProfileProperties = shouldAssignWorkRequest.getClusterProfileProperties();
                    refreshInstancesForCluster(clusterProfileProperties);
                    return shouldAssignWorkRequest.executor(getAgentInstancesFor(clusterProfileProperties)).execute();

                case REQUEST_CREATE_AGENT:
                    CreateAgentRequest createAgentRequest = CreateAgentRequest.fromJSON(request.requestBody());
                    clusterProfileProperties = createAgentRequest.getClusterProfileProperties();
                    refreshInstancesForCluster(clusterProfileProperties);
                    return createAgentRequest.executor(getAgentInstancesFor(clusterProfileProperties), pluginRequest).execute();

                case REQUEST_SERVER_PING:
                    ServerPingRequest serverPingRequest = ServerPingRequest.fromJSON(request.requestBody());
                    refreshInstancesForAllClusters(serverPingRequest.allClusterProfileProperties());
                    return serverPingRequest.executor(clusterSpecificAgentInstances, pluginRequest).execute();

                case REQUEST_GET_ELASTIC_AGENT_PROFILE_METADATA:
                    return new GetProfileMetadataExecutor().execute();

                case REQUEST_GET_ELASTIC_AGENT_PROFILE_VIEW:
                    return new GetProfileViewExecutor().execute();

                case REQUEST_VALIDATE_ELASTIC_AGENT_PROFILE:
                    return ValidateProfileRequest.fromJSON(request.requestBody()).executor().execute();

                case REQUEST_JOB_COMPLETION:
                    JobCompletionRequest jobCompletionRequest = JobCompletionRequest.fromJSON(request.requestBody());
                    clusterProfileProperties = jobCompletionRequest.getClusterProfileProperties();
                    refreshInstancesForCluster(clusterProfileProperties);
                    return jobCompletionRequest.executor(getAgentInstancesFor(clusterProfileProperties), pluginRequest).execute();

                case REQUEST_CAPABILITIES:
                    return new GetCapabilitiesExecutor().execute();

                case REQUEST_AGENT_STATUS_REPORT:
                    AgentStatusReportRequest agentStatusReportRequest = AgentStatusReportRequest.fromJSON(request.requestBody());
                    clusterProfileProperties = agentStatusReportRequest.getClusterProfile();
                    refreshInstancesForCluster(clusterProfileProperties);
                    return agentStatusReportRequest.executor(pluginRequest, clusterSpecificAgentInstances.get(clusterProfileProperties.uuid())).execute();

                case REQUEST_CLUSTER_STATUS_REPORT:
                    ClusterStatusReportRequest clusterStatusReportRequest = ClusterStatusReportRequest.fromJSON(request.requestBody());
                    clusterProfileProperties = clusterStatusReportRequest.getClusterProfile();
                    refreshInstancesForCluster(clusterProfileProperties);
                    return clusterStatusReportRequest.executor(clusterSpecificAgentInstances.get(clusterProfileProperties.uuid())).execute();

                case REQUEST_PLUGIN_STATUS_REPORT:
                    PluginStatusReportRequest pluginStatusReportRequest = PluginStatusReportRequest.fromJSON(request.requestBody());
                    refreshInstancesForAllClusters(pluginStatusReportRequest.allClusterProfileProperties());
                    return pluginStatusReportRequest.executor(clusterSpecificAgentInstances, ViewBuilder.instance()).execute();

                case REQUEST_GET_CLUSTER_PROFILE_METADATA:
                    return new GetClusterProfileMetadataExecutor().execute();

                case REQUEST_GET_CLUSTER_PROFILE_VIEW:
                    return new GetClusterProfileViewRequestExecutor().execute();

                case REQUEST_VALIDATE_CLUSTER_PROFILE_CONFIGURATION:
                    return ClusterProfileValidateRequest.fromJSON(request.requestBody()).executor().execute();

                case REQUEST_CLUSTER_PROFILE_CHANGED:
                    return ClusterProfileChangedRequest.fromJSON(request.requestBody()).executor(clusterSpecificAgentInstances).execute();

                case REQUEST_MIGRATE_CONFIGURATION:
                    return MigrateConfigurationRequest.fromJSON(request.requestBody()).executor().execute();

                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshInstancesForAllClusters(List<ClusterProfileProperties> listOfClusterProfileProperties) throws Exception {
        for (ClusterProfileProperties clusterProfileProperties : listOfClusterProfileProperties) {
            refreshInstancesForCluster(clusterProfileProperties);
        }
    }

    private void refreshInstancesForCluster(ClusterProfileProperties clusterProfileProperties) throws Exception {
        Ec2AgentInstances agentInstances = getAgentInstancesForCluster(clusterProfileProperties);
        agentInstances.refreshAll(clusterProfileProperties);
        clusterSpecificAgentInstances.put(clusterProfileProperties.uuid(), agentInstances);
    }

    private Ec2AgentInstances getAgentInstancesForCluster(ClusterProfileProperties clusterProfileProperties) {
        return clusterSpecificAgentInstances.get(clusterProfileProperties.uuid());
    }

    private AgentInstances<Ec2Instance> getAgentInstancesFor(ClusterProfileProperties clusterProfileProperties) {
        return clusterSpecificAgentInstances.get(clusterProfileProperties.uuid());
    }

}
