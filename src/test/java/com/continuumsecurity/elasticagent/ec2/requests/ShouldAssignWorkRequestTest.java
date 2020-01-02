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

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.Agent;
import com.continuumsecurity.elasticagent.ec2.models.JobIdentifierMother;
import com.google.gson.JsonObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ShouldAssignWorkRequestTest {

    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        JsonObject agentJson = new JsonObject();
        agentJson.addProperty("agent_id", "42");
        agentJson.addProperty("agent_state", "Idle");
        agentJson.addProperty("build_state", "Idle");
        agentJson.addProperty("config_state", "Enabled");

        JsonObject propertiesJson = new JsonObject();
        propertiesJson.addProperty("property_name", "property_value");

        JsonObject json = new JsonObject();
        json.add("agent", agentJson);
        json.add("job_identifier", JobIdentifierMother.getJson());
        json.add("elastic_agent_profile_properties", propertiesJson);

        ShouldAssignWorkRequest request = ShouldAssignWorkRequest.fromJSON(json.toString());

        assertThat(request.agent(), equalTo(new Agent("42", Agent.AgentState.Idle, Agent.BuildState.Idle, Agent.ConfigState.Enabled)));
        HashMap<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("property_name", "property_value");
        assertThat(request.profileProperties(), Matchers.equalTo(expectedProperties));
        assertThat(request.jobIdentifier(), is(JobIdentifierMother.get()));
    }
}
