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

import com.google.gson.Gson;

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetPluginConfigurationExecutorTest {

    @Test
    public void shouldSerializeAllFields() {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();
        HashMap hashMap = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertEquals(hashMap.size(),
                GetPluginConfigurationExecutor.FIELDS.size(),
                "Are you using anonymous inner classes — see https://github.com/google/gson/issues/298"
        );
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetPluginConfigurationExecutor().execute();

        assertThat(response.responseCode(), is(200));
        String expectedJSON = "{\n" +
                "  \"go_server_url\": {\n" +
                "    \"display-name\": \"Go Server URL\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"0\"\n" +
                "  },\n" +
                "  \"auto_register_timeout\": {\n" +
                "    \"display-name\": \"Agent auto-register Timeout (in minutes)\",\n" +
                "    \"default-value\": \"10\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"1\"\n" +
                "  },\n" +
                "  \"max_elastic_agents\": {\n" +
                "    \"display-name\": \"Maximum EC2 elastic agents to run at any given point in time\",\n" +
                "    \"default-value\": \"1\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"2\"\n" +
                "  },\n" +
                "  \"aws_access_key_id\": {\n" +
                "    \"display-name\": \"AWS access key ID\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"3\"\n" +
                "  },\n" +
                "  \"aws_secret_access_key\": {\n" +
                "    \"display-name\": \"AWS secret access key\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": true,\n" +
                "    \"display-order\": \"4\"\n" +
                "  },\n" +
                "  \"aws_region\": {\n" +
                "    \"display-name\": \"AWS region\",\n" +
                "    \"required\": true,\n" +
                "    \"secure\": false,\n" +
                "    \"display-order\": \"5\"\n" +
                "  }\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);

    }
}
