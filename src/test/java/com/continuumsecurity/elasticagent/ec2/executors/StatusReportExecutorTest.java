package com.continuumsecurity.elasticagent.ec2.executors;

import com.google.gson.JsonObject;

import com.continuumsecurity.elasticagent.ec2.Ec2Instance;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.PluginSettings;
import com.continuumsecurity.elasticagent.ec2.models.StatusReport;
import com.continuumsecurity.elasticagent.ec2.views.ViewBuilder;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import freemarker.template.Template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;

import com.continuumsecurity.elasticagent.ec2.AgentInstance;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusReportExecutorTest {

    @Mock
    private PluginRequest pluginRequest;

    @Mock
    private PluginSettings pluginSettings;

    @Mock
    private ViewBuilder viewBuilder;

    @Mock
    private AgentInstance<Ec2Instance> agentInstances;

    @Mock
    private Template template;

    @Test
    public void shouldGetStatusReport() throws Exception {
        StatusReport statusReport = aStatusReport();
        when(pluginRequest.getPluginSettings()).thenReturn(pluginSettings);
        when(agentInstances.getStatusReport(pluginSettings)).thenReturn(statusReport);
        when(viewBuilder.getTemplate("status-report.template.ftlh")).thenReturn(template);
        when(viewBuilder.build(template, statusReport)).thenReturn("statusReportView");
        StatusReportExecutor statusReportExecutor = new StatusReportExecutor(pluginRequest, agentInstances, viewBuilder);

        GoPluginApiResponse goPluginApiResponse = statusReportExecutor.execute();

        JsonObject expectedResponseBody = new JsonObject();
        expectedResponseBody.addProperty("view", "statusReportView");
        assertThat(goPluginApiResponse.responseCode(), is(200));
        JSONAssert.assertEquals(expectedResponseBody.toString(), goPluginApiResponse.responseBody(), true);
    }

    private StatusReport aStatusReport() {
        return new StatusReport(2, new ArrayList<>());
    }
}
