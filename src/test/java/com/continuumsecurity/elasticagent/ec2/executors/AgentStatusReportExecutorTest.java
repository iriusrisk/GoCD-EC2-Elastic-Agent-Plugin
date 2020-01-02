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

import com.continuumsecurity.elasticagent.ec2.*;
import com.continuumsecurity.elasticagent.ec2.models.AgentStatusReport;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifier;
import com.continuumsecurity.elasticagent.ec2.models.NotRunningAgentStatusReport;
import com.continuumsecurity.elasticagent.ec2.requests.AgentStatusReportRequest;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import software.amazon.awssdk.services.ec2.model.CpuOptions;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.Placement;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AgentStatusReportExecutorTest extends BaseTest {

    @Mock
    private PluginRequest pluginRequest;
    @Mock
    private PluginSettings pluginSettings;
    @Mock
    private Ec2AgentInstances agentInstances;
    @Mock
    private ViewBuilder viewBuilder;
    @Mock
    private Template template;
    @Mock
    private Instance instance;
    @Mock
    private InstanceState instanceState;
    @Mock
    private Placement placement;
    @Mock
    private CpuOptions cpuOptions;
    private Map<String, String> clusterProfileConfigurations;
    private ClusterProfileProperties clusterProfile;

    @BeforeEach
    public void setup() {
        clusterProfileConfigurations = Collections.singletonMap("go_server_url", "http://go-server-url/go");
        clusterProfile = ClusterProfileProperties.fromConfiguration(clusterProfileConfigurations);
    }

    private void mockInstance() {
        when(instance.instanceId()).thenReturn("i-123456test");
        when(instanceState.nameAsString()).thenReturn("running");
        when(instance.state()).thenReturn(instanceState);
        when(instance.instanceTypeAsString()).thenReturn(Properties.TYPE);
        when(instance.imageId()).thenReturn(Properties.AMI_ID);
        when(placement.availabilityZone()).thenReturn("eu-west-1c");
        when(instance.placement()).thenReturn(placement);
        when(instance.keyName()).thenReturn(Properties.KEY);
        when(instance.architectureAsString()).thenReturn("x86");
        when(instance.hypervisorAsString()).thenReturn("xen");
        when(instance.rootDeviceName()).thenReturn("/dev/xvda");
        when(instance.rootDeviceTypeAsString()).thenReturn("ebs");
        when(instance.virtualizationTypeAsString()).thenReturn("hvm");
        when(cpuOptions.coreCount()).thenReturn(1);
        when(cpuOptions.threadsPerCore()).thenReturn(2);
        when(instance.cpuOptions()).thenReturn(cpuOptions);
        when(instance.privateDnsName()).thenReturn("ip-172-31-50-73.eu-west-1.compute.internal");
        when(instance.privateIpAddress()).thenReturn("172.31.50.73");
        when(instance.publicDnsName()).thenReturn("ec2-test.eu-west-1.compute.amazonaws.com");
        when(instance.publicIpAddress()).thenReturn("12.34.56.78");
        when(instance.subnetId()).thenReturn("subnet-123456test");
        when(instance.vpcId()).thenReturn("vpc-123456test");
    }

    @Test
    public void shouldGetAgentStatusReportWithElasticAgentId() throws Exception {
        mockInstance();

        String agentId = "elastic-agent-id";
        AgentStatusReportRequest agentStatusReportRequest = new AgentStatusReportRequest(agentId, null, clusterProfile);
        AgentStatusReport agentStatusReport = new AgentStatusReport(null, instance, null);

        Ec2Instance agentInstance = new Ec2Instance("id", new Date(), new HashMap<>(), new JobIdentifier());
        when(agentInstances.find(agentId)).thenReturn(agentInstance);
        when(agentInstances.getAgentStatusReport(clusterProfile, agentInstance)).thenReturn(agentStatusReport);
        when(viewBuilder.getTemplate("agent-status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(template, agentStatusReport)).thenReturn("agentStatusReportView");

        GoPluginApiResponse goPluginApiResponse = new AgentStatusReportExecutor(agentStatusReportRequest, pluginRequest, agentInstances, viewBuilder)
                .execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "agentStatusReportView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }

    @Test
    public void shouldGetAgentStatusReportWithJobIdentifier() throws Exception {
        mockInstance();

        JobIdentifier jobIdentifier = new JobIdentifier("up42", 2L, "label", "stage1", "1", "job", 1L);
        AgentStatusReportRequest agentStatusReportRequest = new AgentStatusReportRequest(null, jobIdentifier, clusterProfile);
        AgentStatusReport agentStatusReport = new AgentStatusReport(jobIdentifier, instance, null);

        Ec2Instance instance = new Ec2Instance("id", new Date(), new HashMap<>(), new JobIdentifier());
        when(agentInstances.find(jobIdentifier)).thenReturn(instance);
        when(agentInstances.getAgentStatusReport(clusterProfile, instance)).thenReturn(agentStatusReport);
        when(viewBuilder.getTemplate("agent-status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(template, agentStatusReport)).thenReturn("agentStatusReportView");

        GoPluginApiResponse goPluginApiResponse = new AgentStatusReportExecutor(agentStatusReportRequest, pluginRequest, agentInstances, viewBuilder)
                .execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "agentStatusReportView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }

    @Test
    public void shouldRenderContainerNotFoundAgentStatusReportViewWhenNoContainerIsRunningForProvidedJobIdentifier() throws Exception {
        JobIdentifier jobIdentifier = new JobIdentifier("up42", 2L, "label", "stage1", "1", "job", 1L);

        AgentStatusReportRequest agentStatusReportRequest = new AgentStatusReportRequest(null, jobIdentifier, clusterProfile);

        when(agentInstances.find(jobIdentifier)).thenReturn(null);
        when(viewBuilder.getTemplate("not-running-agent-status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(eq(template), any(NotRunningAgentStatusReport.class))).thenReturn("errorView");

        GoPluginApiResponse goPluginApiResponse = new AgentStatusReportExecutor(agentStatusReportRequest, pluginRequest, agentInstances, viewBuilder)
                .execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "errorView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }

    @Test
    public void shouldRenderContainerNotFoundAgentStatusReportViewWhenNoContainerIsRunningForProvidedElasticAgentId() throws Exception {
        String elasticAgentId = "elastic-agent-id";
        AgentStatusReportRequest agentStatusReportRequest = new AgentStatusReportRequest(elasticAgentId, null, clusterProfile);

        when(agentInstances.find(elasticAgentId)).thenReturn(null);
        when(viewBuilder.getTemplate("not-running-agent-status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(eq(template), any(NotRunningAgentStatusReport.class))).thenReturn("errorView");

        GoPluginApiResponse goPluginApiResponse = new AgentStatusReportExecutor(agentStatusReportRequest, pluginRequest, agentInstances, viewBuilder)
                .execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "errorView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }
}
