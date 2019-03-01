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

import com.continuumsecurity.elasticagent.ec2.Agent;
import com.continuumsecurity.elasticagent.ec2.Agents;
import com.continuumsecurity.elasticagent.ec2.BaseTest;
import com.continuumsecurity.elasticagent.ec2.Clock;
import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstance;
import com.continuumsecurity.elasticagent.ec2.Ec2Instance;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifierMother;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;

import org.joda.time.Period;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ServerPingRequestExecutorTest extends BaseTest {

    private final JobIdentifier jobIdentifier = JobIdentifierMother.get();

    @Test
    public void testShouldDisableIdleAgents() throws Exception {
        String agentId = UUID.randomUUID().toString();
        final Agents agents = new Agents(Arrays.asList(new Agent(agentId, Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled)));
        AgentInstance agentInstances = new Ec2AgentInstance();

        PluginRequest pluginRequest = mock(PluginRequest.class);
        when(pluginRequest.getPluginSettings()).thenReturn(createSettings());
        when(pluginRequest.listAgents()).thenReturn(agents);
        verifyNoMoreInteractions(pluginRequest);

        final Collection<Agent> values = agents.agents();
        new ServerPingRequestExecutor(agentInstances, pluginRequest).execute();
        verify(pluginRequest).disableAgents(argThat(collectionMatches(values)));
    }

    private ArgumentMatcher<Collection<Agent>> collectionMatches(final Collection<Agent> values) {
        return argument -> new ArrayList<>(argument).equals(new ArrayList<>(values));
    }

    @Test
    public void testShouldTerminateDisabledAgents() throws Exception {
        String agentId = UUID.randomUUID().toString();
        final Agents agents = new Agents(Arrays.asList(new Agent(agentId, Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Disabled)));
        AgentInstance agentInstances = new Ec2AgentInstance();

        PluginRequest pluginRequest = mock(PluginRequest.class);
        when(pluginRequest.getPluginSettings()).thenReturn(createSettings());
        when(pluginRequest.listAgents()).thenReturn(agents);
        verifyNoMoreInteractions(pluginRequest);

        new ServerPingRequestExecutor(agentInstances, pluginRequest).execute();
        final Collection<Agent> values = agents.agents();
        verify(pluginRequest).deleteAgents(argThat(collectionMatches(values)));
    }

    @Test
    public void testShouldTerminateInstancesThatNeverAutoRegistered() throws Exception {
        PluginRequest pluginRequest = mock(PluginRequest.class);
        when(pluginRequest.getPluginSettings()).thenReturn(createSettings());
        when(pluginRequest.listAgents()).thenReturn(new Agents());
        verifyNoMoreInteractions(pluginRequest);

        Ec2AgentInstance agentInstances = new Ec2AgentInstance();
        agentInstances.clock = new Clock.TestClock().forward(Period.minutes(11));
        Ec2Instance instance = agentInstances.create(new CreateAgentRequest("24184-asd7s548sd-45a4dsa", createProperties(), "test", jobIdentifier), createSettings());
        instances.add(instance.id());

        ServerPingRequestExecutor serverPingRequestExecutor = new ServerPingRequestExecutor(agentInstances, pluginRequest);
        serverPingRequestExecutor.execute();

        assertFalse(agentInstances.hasInstance(instance.id()));
    }

    @Test
    public void shouldDeleteAgentFromConfigWhenCorrespondingContainerIsNotPresent() throws Exception {
        PluginRequest pluginRequest = mock(PluginRequest.class);
        when(pluginRequest.getPluginSettings()).thenReturn(createSettings());
        when(pluginRequest.listAgents()).thenReturn(new Agents(Arrays.asList(new Agent("foo", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled))));
        verifyNoMoreInteractions(pluginRequest);

        Ec2AgentInstance agentInstances = new Ec2AgentInstance();
        ServerPingRequestExecutor serverPingRequestExecutor = new ServerPingRequestExecutor(agentInstances, pluginRequest);
        serverPingRequestExecutor.execute();
    }
}
