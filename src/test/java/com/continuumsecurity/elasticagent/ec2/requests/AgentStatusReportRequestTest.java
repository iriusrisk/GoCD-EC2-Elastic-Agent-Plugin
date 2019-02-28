package com.continuumsecurity.elasticagent.ec2.requests;

import com.google.gson.JsonObject;

import org.junit.jupiter.api.Test;

import com.continuumsecurity.elasticagent.ec2.models.JobIdentifierMother;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AgentStatusReportRequestTest {

    @Test
    public void shouldDeserializeFromJSON() {
        JsonObject jobIdentifierJson = JobIdentifierMother.getJson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("elastic_agent_id", "some-id");
        jsonObject.add("job_identifier", jobIdentifierJson);

        AgentStatusReportRequest agentStatusReportRequest = AgentStatusReportRequest.fromJSON(jsonObject.toString());

        AgentStatusReportRequest expected = new AgentStatusReportRequest("some-id", JobIdentifierMother.get());
        assertThat(agentStatusReportRequest, is(expected));
    }

}
