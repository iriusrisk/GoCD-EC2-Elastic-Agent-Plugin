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

package com.continuumsecurity.elasticagent.ec2.requests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ServerPingRequestTest {
    @Test
    public void shouldDeserializeJSONBody() {
        String requestBody = "{\n " +
                " \"all_cluster_profile_properties\": [\n    " +
                "{\n      " +
                "      \"go_server_url\": \"foo\",\n" +
                "      \"auto_register_timeout\": \"10\",\n" +
                "      \"max_elastic_agents\": \"30\",\n" +
                "      \"aws_access_key_id\": \"123456\",\n" +
                "      \"aws_secret_access_key\": \"7890\",\n" +
                "      \"aws_region\": \"eu-west-1\"\n" +
                "    }\n" +
                "   ]" +
                "\n}";

        HashMap<String, String> configurations = new HashMap<>();
        configurations.put("go_server_url", "foo");
        configurations.put("auto_register_timeout", "10");
        configurations.put("max_elastic_agents", "30");
        configurations.put("aws_access_key_id", "123456");
        configurations.put("aws_secret_access_key", "7890");
        configurations.put("aws_region", "eu-west-1");
        assertThat(ServerPingRequest.fromJSON(requestBody), is(new ServerPingRequest(Arrays.asList(configurations))));
    }
}
