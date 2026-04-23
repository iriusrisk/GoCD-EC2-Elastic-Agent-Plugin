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

import com.continuumsecurity.elasticagent.ec2.*;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class CreateAgentRequestExecutorTest {

    @Test
    public void shouldAskAgentInstancesToCreateAnAgent() throws Exception {
        final HashMap<String, String> elasticAgentProfileProperties = new HashMap<>();
        elasticAgentProfileProperties.put("Image", "image1");
        final JobIdentifier jobIdentifier = new JobIdentifier("p1", 1L, "l1", "s1", "1", "j1", 1L);
        CreateAgentRequest request = new CreateAgentRequest("key1", elasticAgentProfileProperties, jobIdentifier, new HashMap<>(), "my_env");

        AgentInstances<Ec2Instance> agentInstances = mock(Ec2AgentInstances.class);
        PluginRequest pluginRequest = mock(PluginRequest.class);
        new CreateAgentRequestExecutor(request, agentInstances, pluginRequest).execute();

        verify(agentInstances).create(eq(request), eq(pluginRequest), any(ConsoleLogAppender.class));
        verify(pluginRequest).appendToConsoleLog(eq(jobIdentifier), contains("Received request to create an instance for"));
    }

    @Test
    public void shouldLogErrorMessageToConsoleIfAgentCreateFails() throws Exception {
        final HashMap<String, String> elasticAgentProfileProperties = new HashMap<>();
        elasticAgentProfileProperties.put("Image", "image1");
        final JobIdentifier jobIdentifier = new JobIdentifier("p1", 1L, "l1", "s1", "1", "j1", 1L);
        CreateAgentRequest request = new CreateAgentRequest("key1", elasticAgentProfileProperties, jobIdentifier, new HashMap<>(), "my_env");

        AgentInstances<Ec2Instance> agentInstances = mock(Ec2AgentInstances.class);
        PluginRequest pluginRequest = mock(PluginRequest.class);
        when(agentInstances.create(eq(request), eq(pluginRequest), any(ConsoleLogAppender.class))).thenThrow(new RuntimeException("Ouch!"));

        try {
            new CreateAgentRequestExecutor(request, agentInstances, pluginRequest).execute();
            fail("Should have thrown an exception");
        } catch (RuntimeException e) {
            // expected
        }

        verify(pluginRequest).appendToConsoleLog(eq(jobIdentifier), contains("Received request to create an instance for"));
        verify(pluginRequest).appendToConsoleLog(eq(jobIdentifier), contains("Failed while creating instance: Ouch!"));
    }
}
