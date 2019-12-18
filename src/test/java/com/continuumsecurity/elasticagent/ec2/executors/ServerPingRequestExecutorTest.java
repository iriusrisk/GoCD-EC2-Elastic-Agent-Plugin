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
import com.continuumsecurity.elasticagent.ec2.requests.ServerPingRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.*;

import static org.mockito.Mockito.*;

public class ServerPingRequestExecutorTest extends BaseTest {

    private Map<String, String> properties = createProperties();

    @Test
    public void testShouldDisableIdleAgents() throws Exception {
        String agentId = UUID.randomUUID().toString();
        final Agents agents = new Agents(Arrays.asList(new Agent(agentId, Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled)));
        Ec2AgentInstances agentInstances = new Ec2AgentInstances();

        PluginRequest pluginRequest = mock(PluginRequest.class);
        ServerPingRequest serverPingRequest = mock(ServerPingRequest.class);
        when(serverPingRequest.allClusterProfileProperties()).thenReturn(Arrays.asList(createClusterProfiles()));
        when(pluginRequest.listAgents()).thenReturn(agents);
        verifyNoMoreInteractions(pluginRequest);

        final Collection<Agent> values = agents.agents();
        HashMap<String, Ec2AgentInstances> instances = new HashMap<String, Ec2AgentInstances>() {{
            put(createClusterProfiles().uuid(), agentInstances);
        }};

        new ServerPingRequestExecutor(serverPingRequest, instances, pluginRequest).execute();
        verify(pluginRequest).disableAgents(argThat(collectionMatches(values)));
    }

    private ArgumentMatcher<Collection<Agent>> collectionMatches(final Collection<Agent> values) {
        return argument -> new ArrayList<>(argument).equals(new ArrayList<>(values));
    }

    @Test
    public void testShouldTerminateDisabledAgents() throws Exception {
        String agentId = UUID.randomUUID().toString();
        final Agents agents = new Agents(Arrays.asList(new Agent(agentId, Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Disabled)));
        Ec2AgentInstances agentInstances = new Ec2AgentInstances();

        PluginRequest pluginRequest = mock(PluginRequest.class);
        ServerPingRequest serverPingRequest = mock(ServerPingRequest.class);
        when(serverPingRequest.allClusterProfileProperties()).thenReturn(Arrays.asList(createClusterProfiles()));
        when(pluginRequest.listAgents()).thenReturn(agents);
        verifyNoMoreInteractions(pluginRequest);
        HashMap<String, Ec2AgentInstances> instances = new HashMap<String, Ec2AgentInstances>() {{
            put(createClusterProfiles().uuid(), agentInstances);
        }};

        new ServerPingRequestExecutor(serverPingRequest, instances, pluginRequest).execute();
        final Collection<Agent> values = agents.agents();
        verify(pluginRequest, atLeast(1)).deleteAgents(argThat(collectionMatches(values)));
    }

//    @Test
//    public void testShouldTerminateInstancesThatNeverAutoRegistered() throws Exception {
//        PluginRequest pluginRequest = mock(PluginRequest.class);
//        ServerPingRequest serverPingRequest = mock(ServerPingRequest.class);
//        when(serverPingRequest.allClusterProfileProperties()).thenReturn(Arrays.asList(createClusterProfiles()));
//        when(pluginRequest.listAgents()).thenReturn(new Agents());
//        verifyNoMoreInteractions(pluginRequest);
//
//        Ec2AgentInstances agentInstances = new Ec2AgentInstances();
//        agentInstances.clock = new Clock.TestClock().forward(Period.minutes(11));
//        Ec2Instance instance = agentInstances.create(new CreateAgentRequest(null, properties, mock(JobIdentifier.class), createClusterProfiles()), pluginRequest, mock(ConsoleLogAppender.class));
//        instances.add(instance.id());
//
//        HashMap<String, Ec2AgentInstances> instances = new HashMap<String, Ec2AgentInstances>() {{
//            put(createClusterProfiles().uuid(), agentInstances);
//        }};
//
//        new ServerPingRequestExecutor(serverPingRequest, instances, pluginRequest).execute();
//
//        assertFalse(agentInstances.hasInstance(instance.id()));
//    }

    @Test
    public void shouldDeleteAgentFromConfigWhenCorrespondingContainerIsNotPresent() throws Exception {
        PluginRequest pluginRequest = mock(PluginRequest.class);
        ServerPingRequest serverPingRequest = mock(ServerPingRequest.class);
        when(serverPingRequest.allClusterProfileProperties()).thenReturn(Arrays.asList(createClusterProfiles()));
        when(pluginRequest.listAgents()).thenReturn(new Agents(Arrays.asList(new Agent("foo", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled))));
        verifyNoMoreInteractions(pluginRequest);

        Ec2AgentInstances agentInstances = new Ec2AgentInstances();
        HashMap<String, Ec2AgentInstances> instances = new HashMap<String, Ec2AgentInstances>() {{
            put(createClusterProfiles().uuid(), agentInstances);
        }};

        ServerPingRequestExecutor serverPingRequestExecutor = new ServerPingRequestExecutor(serverPingRequest, instances, pluginRequest);
        serverPingRequestExecutor.execute();
    }
}
