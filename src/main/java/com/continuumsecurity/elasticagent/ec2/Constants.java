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

import com.continuumsecurity.elasticagent.ec2.utils.Util;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

import java.util.Collections;

public interface Constants {
    String PLUGIN_ID = Util.pluginId();

    // The type of this extension
    String EXTENSION_TYPE = "elastic-agent";

    // The extension point API version that this plugin understands
    String ELASTIC_PROCESSOR_API_VERSION = "1.0";
    String SERVER_INFO_PROCESSOR_API_VERSION = "1.0";
    String EXTENSION_API_VERSION = "5.0";

    String ELASTIC_AGENT_TAG = "gocd-elastic-agent";

    // the identifier of this plugin
    GoPluginIdentifier PLUGIN_IDENTIFIER = new GoPluginIdentifier(EXTENSION_TYPE, Collections.singletonList(EXTENSION_API_VERSION));

    // requests that the plugin makes to the server
    String REQUEST_SERVER_PREFIX = "go.processor";
    String REQUEST_SERVER_DISABLE_AGENT = REQUEST_SERVER_PREFIX + ".elastic-agents.disable-agents";
    String REQUEST_SERVER_DELETE_AGENT = REQUEST_SERVER_PREFIX + ".elastic-agents.delete-agents";
    String REQUEST_SERVER_LIST_AGENTS = REQUEST_SERVER_PREFIX + ".elastic-agents.list-agents";
    String REQUEST_SERVER_INFO = REQUEST_SERVER_PREFIX + ".server-info.get";
    String REQUEST_SERVER_SERVER_HEALTH_ADD_MESSAGES = REQUEST_SERVER_PREFIX + ".server-health.add-messages";
    String REQUEST_SERVER_APPEND_TO_CONSOLE_LOG = REQUEST_SERVER_PREFIX + ".console-log.append";
}
