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

import com.google.gson.JsonObject;

import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import freemarker.template.Template;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;

public class StatusReportExecutor implements RequestExecutor {

    private final PluginRequest pluginRequest;
    private final AgentInstance agentInstances;
    private final ViewBuilder viewBuilder;

    public StatusReportExecutor(PluginRequest pluginRequest, AgentInstance agentInstances, ViewBuilder viewBuilder) {
        this.pluginRequest = pluginRequest;
        this.agentInstances = agentInstances;
        this.viewBuilder = viewBuilder;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        StatusReport statusReport = agentInstances.getStatusReport(pluginRequest.getPluginSettings());

        final Template template = viewBuilder.getTemplate("status-report.template.ftlh");
        final String statusReportView = viewBuilder.build(template, statusReport);

        JsonObject responseJSON = new JsonObject();
        responseJSON.addProperty("view", statusReportView);

        return DefaultGoPluginApiResponse.success(responseJSON.toString());
    }
}
