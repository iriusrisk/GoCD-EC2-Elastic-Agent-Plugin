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

package com.continuumsecurity.elasticagent.ec2.executors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetClusterProfileMetadataExecutorTest {

    @Test
    public void shouldSerializeAllFields() throws Exception {
        GoPluginApiResponse response = new GetClusterProfileMetadataExecutor().execute();
        final Type type = new TypeToken<List<Metadata>>() {
        }.getType();

        List<Metadata> list = new Gson().fromJson(response.responseBody(), type);
        assertEquals(list.size(), GetClusterProfileMetadataExecutor.CLUSTER_PROFILE_FIELDS.size());
    }

    @Test
    public void assertJsonStructure() throws Exception {
        GoPluginApiResponse response = new GetClusterProfileMetadataExecutor().execute();

        assertThat(response.responseCode(), is(200));
        String expectedJSON = "[" +
                "{" +
                "\"key\":\"go_server_url\"," +
                "\"metadata\":{\"required\":true,\"secure\":false}" +
                "}," +
                "{" +
                "\"key\":\"auto_register_timeout\"," +
                "\"metadata\":{\"required\":true,\"secure\":false}" +
                "}," +
                "{" +
                "\"key\":\"max_elastic_agents\"," +
                "\"metadata\":{\"required\":true,\"secure\":false}" +
                "}," +
                "{" +
                "\"key\":\"aws_access_key_id\"," +
                "\"metadata\":{\"required\":false,\"secure\":false}" +
                "}," +
                "{" +
                "\"key\":\"aws_secret_access_key\"," +
                "\"metadata\":{\"required\":false,\"secure\":true}" +
                "}," +
                "{" +
                "\"key\":\"aws_region\"," +
                "\"metadata\":{\"required\":true,\"secure\":false}" +
                "}" +
                "]\n";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}
