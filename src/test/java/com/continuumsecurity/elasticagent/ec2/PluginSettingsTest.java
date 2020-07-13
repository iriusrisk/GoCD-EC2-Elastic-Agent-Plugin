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

package com.continuumsecurity.elasticagent.ec2;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.not;

public class PluginSettingsTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        PluginSettings pluginSettings = PluginSettings.fromJSON("{" +
                "\"go_server_url\": \"https://example.com/go\", " +
                "\"aws_access_key_id\": \"123456\", " +
                "\"aws_secret_access_key\": \"7890\" " +
                "}");

        assertThat(pluginSettings.getGoServerUrl(), is("https://example.com/go"));
        assertThat(pluginSettings.getAwsAccessKeyId(), is("123456"));
        assertThat(pluginSettings.getAwsSecretAccessKey(), is("7890"));
    }
    
    @Test
    public void shouldDeserializeFromJSONWithNullCredentials() throws Exception {
        PluginSettings pluginSettings = PluginSettings.fromJSON("{" +
                "\"go_server_url\": \"https://example.com/go\"" +
                "}");
        
        assertThat(pluginSettings.getGoServerUrl(), is("https://example.com/go"));
        assertThat(pluginSettings.getAwsAccessKeyId(), is(nullValue()));
        assertThat(pluginSettings.getAwsSecretAccessKey(), is(nullValue()));
    }
    
    @Test
    public void shouldSerializeProvidedCredentialsOnly() throws Exception {
      PluginSettings pluginSettings = PluginSettings.fromJSON("{" +
                "\"go_server_url\": \"https://example.com/go\", " +
                "\"aws_access_key_id\": \"123456\"" +
                "}");

        assertThat(pluginSettings.toString(), containsString("awsAccessKeyId"));
        assertThat(pluginSettings.toString(), containsString("123456"));
        assertThat(pluginSettings.toString(), not(containsString("awsSecretAccessKey")));
    }
}