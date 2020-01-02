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

import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreateAgentRequestTest {

    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        String json = "{\n" +
                "  \"auto_register_key\": \"secret-key\",\n" +
                "  \"elastic_agent_profile_properties\": {\n" +
                "    \"key1\": \"value1\",\n" +
                "    \"key2\": \"value2\"\n" +
                "  },\n" +
                "  \"cluster_profile_properties\": {\n" +
                "    \"go_server_url\": \"https://foo.com/go\",\n" +
                "    \"auto_register_timeout\": \"10\"\n" +
                "  }\n" +
                "}";

        CreateAgentRequest request = CreateAgentRequest.fromJSON(json);
        assertThat(request.autoRegisterKey(), equalTo("secret-key"));
        HashMap<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("key1", "value1");
        expectedProperties.put("key2", "value2");
        assertThat(request.properties(), Matchers.<Map<String, String>>equalTo(expectedProperties));

        ClusterProfileProperties expectedClusterProfileProperties = new ClusterProfileProperties();
        expectedClusterProfileProperties.setGoServerUrl("https://foo.com/go");
        expectedClusterProfileProperties.setAutoRegisterTimeout("10");

        assertThat(request.getClusterProfileProperties(), is(expectedClusterProfileProperties));

    }
}
