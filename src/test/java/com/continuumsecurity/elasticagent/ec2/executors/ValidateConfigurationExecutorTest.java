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

import com.continuumsecurity.elasticagent.ec2.requests.ValidatePluginSettings;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ValidateConfigurationExecutorTest {
    @Test
    public void shouldValidateABadConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[\n" +
                "  {\n" +
                "    \"message\": \"Go Server URL must not be blank.\",\n" +
                "    \"key\": \"go_server_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Agent auto-register Timeout (in minutes) must be a positive integer.\",\n" +
                "    \"key\": \"auto_register_timeout\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Maximum EC2 elastic agents to run at any given point in time must be a positive integer.\",\n" +
                "    \"key\": \"max_elastic_agents\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"AWS access key ID must not be blank.\",\n" +
                "    \"key\": \"aws_access_key_id\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"AWS secret access key must not be blank.\",\n" +
                "    \"key\": \"aws_secret_access_key\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"AWS region must not be blank.\",\n" +
                "    \"key\": \"aws_region\"\n" +
                "  }\n" +
                "]", response.responseBody(), true);
    }

    @Test
    public void shouldValidateAGoodConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        settings.put("go_server_url", "https://ci.example.com:8154/go");
        settings.put("auto_register_timeout", "5");
        settings.put("max_elastic_agents", "50");
        settings.put("aws_access_key_id", "SDUI2910ASJDH1012H1P");
        settings.put("aws_secret_access_key", "ASnkmasd872jas+asd11KJSHjks8nasd1n8sFQHd");
        settings.put("aws_region", "eu-west-1");
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[]", response.responseBody(), true);
    }
}
