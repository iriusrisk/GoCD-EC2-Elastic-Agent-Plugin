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
import com.google.gson.GsonBuilder;

import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.continuumsecurity.elasticagent.ec2.executors.Field.next;

public class GetPluginConfigurationExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final String GO_SERVER_URL = "go_server_url";

    public static final String AUTOREGISTER_TIMEOUT = "auto_register_timeout";
    public static final String MAX_ELASTIC_AGENTS = "max_elastic_agents";

    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    public static final String AWS_REGION = "aws_region";

    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    static {
        FIELDS.put(GO_SERVER_URL,
                new GoServerURLField(next()));
        FIELDS.put(AUTOREGISTER_TIMEOUT,
                new PositiveNumberField(AUTOREGISTER_TIMEOUT, "Agent auto-register Timeout (in minutes)", "10", true, false, next()));
        FIELDS.put(MAX_ELASTIC_AGENTS,
                new PositiveNumberField(MAX_ELASTIC_AGENTS, "Maximum EC2 elastic agents to run at any given point in time", "1", true, false, next()));
        FIELDS.put(AWS_ACCESS_KEY_ID,
                new NonBlankField(AWS_ACCESS_KEY_ID, "AWS access key ID", null, true, false, next()));
        FIELDS.put(AWS_SECRET_ACCESS_KEY,
                new NonBlankField(AWS_SECRET_ACCESS_KEY, "AWS secret access key", null, true, true, next()));
        FIELDS.put(AWS_REGION,
                new NonBlankField(AWS_REGION, "AWS region", null, true, false, next()));
    }

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }

}
