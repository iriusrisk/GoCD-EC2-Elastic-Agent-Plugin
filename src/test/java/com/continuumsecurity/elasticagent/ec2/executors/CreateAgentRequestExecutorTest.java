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

import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.PluginSettings;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;

import org.junit.jupiter.api.Test;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateAgentRequestExecutorTest {
    @Test
    public void shouldAskDockerContainersToCreateAnAgent() throws Exception {
        CreateAgentRequest request = new CreateAgentRequest();
        AgentInstance agentInstances = mock(AgentInstance.class);
        PluginRequest pluginRequest = mock(PluginRequest.class);
        PluginSettings settings = mock(PluginSettings.class);
        when(pluginRequest.getPluginSettings()).thenReturn(settings);
        new CreateAgentRequestExecutor(request, agentInstances, pluginRequest).execute();

        verify(agentInstances).create(request, settings);
    }
}
