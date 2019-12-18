/*
 * Copyright 2019 ThoughtWorks, Inc.
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

import com.continuumsecurity.elasticagent.ec2.AgentInstances;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.requests.ClusterStatusReportRequest;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.LOG;

public class ClusterStatusReportExecutor implements RequestExecutor {

    private final ClusterStatusReportRequest clusterStatusReportRequest;
    private final AgentInstances agentInstances;
    private final ViewBuilder viewBuilder;

    public ClusterStatusReportExecutor(ClusterStatusReportRequest clusterStatusReportRequest, AgentInstances agentInstances, ViewBuilder viewBuilder) {
        this.clusterStatusReportRequest = clusterStatusReportRequest;
        this.agentInstances = agentInstances;
        this.viewBuilder = viewBuilder;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        LOG.info("[status-report] Generating status report");

        StatusReport statusReport = agentInstances.getStatusReport(clusterStatusReportRequest.getClusterProfile());

        final String statusReportView = viewBuilder.build(viewBuilder.getTemplate("status-report.template.ftlh"), statusReport);

        JsonObject responseJSON = new JsonObject();
        responseJSON.addProperty("view", statusReportView);

        return DefaultGoPluginApiResponse.success(responseJSON.toString());
    }
}