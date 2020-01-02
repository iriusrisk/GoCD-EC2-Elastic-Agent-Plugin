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

import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstances;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.requests.ClusterStatusReportRequest;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import freemarker.template.Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClusterStatusReportExecutorTest {

    @Mock
    private ClusterStatusReportRequest clusterStatusReportRequest;

    @Mock
    private ClusterProfileProperties clusterProfile;

    @Mock
    private ViewBuilder viewBuilder;

    @Mock
    private Ec2AgentInstances agentInstances;

    @Mock
    private Template template;

    @Test
    public void shouldGetStatusReport() throws Exception {
        StatusReport statusReport = aStatusReport();
        when(clusterStatusReportRequest.getClusterProfile()).thenReturn(clusterProfile);
        when(agentInstances.getStatusReport(clusterProfile)).thenReturn(statusReport);
        when(viewBuilder.getTemplate("status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(template, statusReport)).thenReturn("statusReportView");
        ClusterStatusReportExecutor statusReportExecutor = new ClusterStatusReportExecutor(clusterStatusReportRequest, agentInstances, viewBuilder);

        GoPluginApiResponse goPluginApiResponse = statusReportExecutor.execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "statusReportView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }

    private StatusReport aStatusReport() {
        return new StatusReport(1, new ArrayList<>());
    }

}
