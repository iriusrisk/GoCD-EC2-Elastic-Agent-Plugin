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

import com.continuumsecurity.elasticagent.ec2.executors.GetCapabilitiesExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.GetPluginConfigurationExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.GetPluginSettingsIconExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.GetProfileMetadataExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.GetProfileViewExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.GetViewRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.ServerPingRequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.StatusReportExecutor;
import com.continuumsecurity.elasticagent.ec2.requests.AgentStatusReportRequest;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import com.continuumsecurity.elasticagent.ec2.requests.JobCompletionRequest;
import com.continuumsecurity.elasticagent.ec2.requests.ProfileValidateRequest;
import com.continuumsecurity.elasticagent.ec2.requests.ShouldAssignWorkRequest;
import com.continuumsecurity.elasticagent.ec2.requests.ValidatePluginSettings;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

@Extension
public class Ec2Plugin implements GoPlugin {

    public static final Logger LOG = Logger.getLoggerFor(Ec2Plugin.class);

    private PluginRequest pluginRequest;
    private AgentInstance agentInstances;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        pluginRequest = new PluginRequest(accessor);
        agentInstances = new Ec2AgentInstance();
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            switch (Request.fromString(request.requestName())) {
                case REQUEST_SHOULD_ASSIGN_WORK:
                    refreshInstances();
                    return ShouldAssignWorkRequest.fromJSON(request.requestBody()).executor(agentInstances).execute();
                case REQUEST_CREATE_AGENT:
                    refreshInstances();
                    return CreateAgentRequest.fromJSON(request.requestBody()).executor(agentInstances, pluginRequest).execute();
                case REQUEST_SERVER_PING:
                    refreshInstances();
                    return new ServerPingRequestExecutor(agentInstances, pluginRequest).execute();
                case PLUGIN_SETTINGS_GET_VIEW:
                    return new GetViewRequestExecutor().execute();
                case REQUEST_GET_PROFILE_METADATA:
                    return new GetProfileMetadataExecutor().execute();
                case REQUEST_GET_PROFILE_VIEW:
                    return new GetProfileViewExecutor().execute();
                case REQUEST_VALIDATE_PROFILE:
                    return ProfileValidateRequest.fromJSON(request.requestBody()).executor().execute();
                case PLUGIN_SETTINGS_GET_ICON:
                    return new GetPluginSettingsIconExecutor().execute();
                case PLUGIN_SETTINGS_GET_CONFIGURATION:
                    return new GetPluginConfigurationExecutor().execute();
                case PLUGIN_SETTINGS_VALIDATE_CONFIGURATION:
                    return ValidatePluginSettings.fromJSON(request.requestBody()).executor().execute();
                case REQUEST_JOB_COMPLETION:
                    refreshInstances();
                    return JobCompletionRequest.fromJSON(request.requestBody()).executor(agentInstances, pluginRequest).execute();
                case REQUEST_STATUS_REPORT:
                    refreshInstances();
                    return new StatusReportExecutor(pluginRequest, agentInstances, ViewBuilder.instance()).execute();
                case REQUEST_AGENT_STATUS_REPORT:
                    refreshInstances();
                    return AgentStatusReportRequest.fromJSON(request.requestBody()).executor(pluginRequest, agentInstances, ViewBuilder.instance()).execute();
                case REQUEST_CAPABILITIES:
                    return new GetCapabilitiesExecutor().execute();
                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshInstances() {
        try {
            agentInstances.refreshAll(pluginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return Constants.PLUGIN_IDENTIFIER;
    }

}
