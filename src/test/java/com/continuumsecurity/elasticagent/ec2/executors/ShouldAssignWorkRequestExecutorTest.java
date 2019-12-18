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
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifierMother;
import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import com.continuumsecurity.elasticagent.ec2.requests.ShouldAssignWorkRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ShouldAssignWorkRequestExecutorTest extends BaseTest {

    private Ec2AgentInstances agentInstances;
    private Ec2Instance instance;
    private Map<String, String> properties = createProperties();
    private final JobIdentifier jobIdentifier = JobIdentifierMother.get();

    @BeforeEach
    public void setUp() {
        agentInstances = new Ec2AgentInstances();
    }

    private void createTestInstance() {
        ClusterProfileProperties clusterProfiles = createClusterProfiles();

        instance = agentInstances.create(
                new CreateAgentRequest(UUID.randomUUID().toString(), properties, jobIdentifier, clusterProfiles),
                mock(PluginRequest.class),
                mock(ConsoleLogAppender.class));

        instances.add(instance.id());
    }

    // enable on your own risk, may incur in charges in AWS
//    @Test
//    public void shouldAssignWorkToContainerWithSameJobIdentifier() {
//        createTestInstance();
//
//        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent(instance.id(), null, null, null), jobIdentifier, properties, null);
//        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances).execute();
//        assertThat(response.responseCode(), is(200));
//        assertThat(response.responseBody(), is("true"));
//    }
//
//    @Test
//    public void shouldNotAssignWorkToContainerWithDifferentJobIdentifier() {
//        createTestInstance();
//
//        JobIdentifier otherJobId = new JobIdentifier("up42", 2L, "foo", "stage", "1", "job", 2L);
//        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent(instance.id(), null, null, null), otherJobId, properties, null);
//        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances).execute();
//        assertThat(response.responseCode(), is(200));
//        assertThat(response.responseBody(), is("false"));
//    }

    @Test
    public void shouldNotAssignWorkIfInstanceIsNotFound() {
        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent("unknown-name", null, null, null), jobIdentifier, properties, null);
        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances).execute();
        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), is("false"));
    }
}
