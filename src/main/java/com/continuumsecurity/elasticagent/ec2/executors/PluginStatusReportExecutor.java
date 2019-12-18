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
import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstances;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.requests.PluginStatusReportRequest;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Make changes as needed
public class PluginStatusReportExecutor implements RequestExecutor {

    private final PluginStatusReportRequest request;
    private final Map<String, Ec2AgentInstances> allClusterInstances;
    private final ViewBuilder viewBuilder;
    private static final Logger LOG = Logger.getLoggerFor(AgentStatusReportExecutor.class);

    public PluginStatusReportExecutor(PluginStatusReportRequest request, Map<String, Ec2AgentInstances> allClusterInstances, ViewBuilder viewBuilder) {
        this.request = request;
        this.allClusterInstances = allClusterInstances;
        this.viewBuilder = viewBuilder;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        LOG.info("[status-report] Generating status report");

        List<String> reports = new ArrayList<>();

        for (ClusterProfileProperties profile : request.allClusterProfileProperties()) {
            AgentInstances agentInstances = allClusterInstances.get(profile.uuid());
            StatusReport statusReport = agentInstances.getStatusReport(profile);
            reports.add(viewBuilder.build(viewBuilder.getTemplate("status-report.template.ftlh"), statusReport));
        }

        // aggregate reports for different cluster into one
        JsonObject responseJSON = new JsonObject();
        responseJSON.addProperty("view", reports.stream().collect(Collectors.joining("<hr/>")));
        return DefaultGoPluginApiResponse.success(responseJSON.toString());
    }
}
