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

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstances;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.PluginStatusReportExecutor;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;

import java.util.Map;

public class PluginStatusReportRequest extends ServerPingRequest {
    public static PluginStatusReportRequest fromJSON(String json) {
        return (PluginStatusReportRequest) ServerPingRequest.fromJSON(json);
    }

    public RequestExecutor executor(Map<String, Ec2AgentInstances> clusterSpecificAgentInstances, ViewBuilder instance) {
        return new PluginStatusReportExecutor(this, clusterSpecificAgentInstances, instance);
    }
}