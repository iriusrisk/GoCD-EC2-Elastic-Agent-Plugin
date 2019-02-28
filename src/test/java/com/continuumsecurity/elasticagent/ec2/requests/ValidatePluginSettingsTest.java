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

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidatePluginSettingsTest {

    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        String json = "{\n" +
                "  \"plugin-settings\": {\n" +
                "    \"go_server_url\": {\n" +
                "      \"value\": \"https://cloud.example.com/\"\n" +
                "    },\n" +
                "    \"auto_register_timeout\": {\n" +
                "      \"value\": \"5\"\n" +
                "    },\n" +
                "    \"max_elastic_agents\": {\n" +
                "      \"value\": \"50\"\n" +
                "    },\n" +
                "    \"aws_access_key_id\": {\n" +
                "      \"value\": \"SDUI2910ASJDH1012H1P\"\n" +
                "    },\n" +
                "    \"aws_secret_access_key\": {\n" +
                "      \"value\": \"ASnkmasd872jas+asd11KJSHjks8nasd1n8sFQHd\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ValidatePluginSettings request = ValidatePluginSettings.fromJSON(json);
        HashMap<String, String> expectedSettings = new HashMap<>();
        expectedSettings.put("go_server_url", "https://cloud.example.com/");
        expectedSettings.put("auto_register_timeout", "5");
        expectedSettings.put("max_elastic_agents", "50");
        expectedSettings.put("aws_access_key_id", "SDUI2910ASJDH1012H1P");
        expectedSettings.put("aws_secret_access_key", "ASnkmasd872jas+asd11KJSHjks8nasd1n8sFQHd");
        assertThat(request, equalTo(expectedSettings));
    }
}
