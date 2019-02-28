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

package com.continuumsecurity.elasticagent.ec2;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PluginSettingsTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        PluginSettings pluginSettings = PluginSettings.fromJSON("{" +
                "\"go_server_url\": \"https://cloud.example.com:8154/go\", " +
                "\"auto_register_timeout\": \"5\", " +
                "\"max_elastic_agents\": \"50\", " +
                "\"aws_access_key_id\": \"SDUI2910ASJDH1012H1P\", " +
                "\"aws_secret_access_key\": \"ASnkmasd872jas+asd11KJSHjks8nasd1n8sFQHd\", " +
                "\"aws_region\": \"eu-west-1\"" +
                "}");

        assertThat(pluginSettings.getGoServerUrl(), is("https://cloud.example.com:8154/go"));
        assertThat(pluginSettings.getAutoRegisterTimeout(), is("5"));
        assertThat(pluginSettings.getMaxElasticAgents(), is(50));
        assertThat(pluginSettings.getAwsAccessKeyId(), is("SDUI2910ASJDH1012H1P"));
        assertThat(pluginSettings.getAwsSecretAccessKey(), is("ASnkmasd872jas+asd11KJSHjks8nasd1n8sFQHd"));
    }
}
