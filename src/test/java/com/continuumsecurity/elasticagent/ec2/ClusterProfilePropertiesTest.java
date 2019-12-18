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

import com.continuumsecurity.elasticagent.ec2.requests.CreateAgentRequest;
import com.continuumsecurity.elasticagent.ec2.requests.JobCompletionRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ClusterProfilePropertiesTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        ClusterProfileProperties pluginSettings = ClusterProfileProperties.fromJSON("{" +
                "\"go_server_url\": \"https://cloud.example.com:8154/go\", " +
                "\"auto_register_timeout\": \"5\", " +
                "\"max_elastic_agents\": \"50\", " +
                "\"aws_access_key_id\": \"ACCES_KEY_ID\", " +
                "\"aws_secret_access_key\": \"SECRET_ACCESS_KEY\", " +
                "\"aws_region\": \"eu-west-1\"" +
                "}");

        assertThat(pluginSettings.getGoServerUrl(), is("https://cloud.example.com:8154/go"));
        assertThat(pluginSettings.getAutoRegisterTimeout(), is("5"));
        assertThat(pluginSettings.getMaxElasticAgents(), is(50));
        assertThat(pluginSettings.getAwsAccessKeyId(), is("ACCES_KEY_ID"));
        assertThat(pluginSettings.getAwsSecretAccessKey(), is("SECRET_ACCESS_KEY"));
    }

    @Test
    public void shouldGenerateSameUUIDForClusterProfileProperties() {
        Map<String, String> clusterProfileConfigurations = Collections.singletonMap("go_server_url", "http://go-server-url/go");
        ClusterProfileProperties clusterProfileProperties = ClusterProfileProperties.fromConfiguration(clusterProfileConfigurations);

        assertThat(clusterProfileProperties.uuid(), is(clusterProfileProperties.uuid()));
    }

    @Test
    public void shouldGenerateSameUUIDForClusterProfilePropertiesAcrossRequests() {
        String createAgentRequestJSON = "{\n" +
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

        CreateAgentRequest createAgentRequest = CreateAgentRequest.fromJSON(createAgentRequestJSON);

        String jobCompletionRequestJSON = "{\n" +
                "  \"elastic_agent_id\": \"ea1\",\n" +
                "  \"elastic_agent_profile_properties\": {\n" +
                "    \"ec2_ami\": \"ami-123456\"\n" +
                "  },\n" +
                "  \"cluster_profile_properties\": {\n" +
                "    \"go_server_url\": \"https://foo.com/go\", \n" +
                "    \"auto_register_timeout\": \"10\"\n" +
                "  },\n" +
                "  \"job_identifier\": {\n" +
                "    \"pipeline_name\": \"test-pipeline\",\n" +
                "    \"pipeline_counter\": 1,\n" +
                "    \"pipeline_label\": \"Test Pipeline\",\n" +
                "    \"stage_name\": \"test-stage\",\n" +
                "    \"stage_counter\": \"1\",\n" +
                "    \"job_name\": \"test-job\",\n" +
                "    \"job_id\": 100\n" +
                "  }\n" +
                "}";

        JobCompletionRequest jobCompletionRequest = JobCompletionRequest.fromJSON(jobCompletionRequestJSON);
        assertThat(jobCompletionRequest.getClusterProfileProperties().uuid(), is(createAgentRequest.getClusterProfileProperties().uuid()));
    }
}
