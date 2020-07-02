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

import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;

import static com.continuumsecurity.elasticagent.ec2.PluginSettings.GSON;

public class GetClusterProfileMetadataExecutor implements RequestExecutor {

    public static final Metadata GO_SERVER_URL = new GoServerURLMetadata();
    public static final Metadata AUTO_REGISTER_TIMEOUT = new NumberMetadata("auto_register_timeout", true);
    public static final Metadata MAX_ELASTIC_AGENTS = new NumberMetadata("max_elastic_agents", true);
    public static final Metadata AWS_ACCESS_KEY_ID = new Metadata("aws_access_key_id", false, false);
    public static final Metadata AWS_SECRET_ACCESS_KEY = new Metadata("aws_secret_access_key", false, true);
    public static final Metadata AWS_REGION = new Metadata("aws_region", true, false);

    public static final List<Metadata> CLUSTER_PROFILE_FIELDS = new ArrayList<>();

    static {
        CLUSTER_PROFILE_FIELDS.add(GO_SERVER_URL);
        CLUSTER_PROFILE_FIELDS.add(AUTO_REGISTER_TIMEOUT);
        CLUSTER_PROFILE_FIELDS.add(MAX_ELASTIC_AGENTS);
        CLUSTER_PROFILE_FIELDS.add(AWS_ACCESS_KEY_ID);
        CLUSTER_PROFILE_FIELDS.add(AWS_SECRET_ACCESS_KEY);
        CLUSTER_PROFILE_FIELDS.add(AWS_REGION);
    }

    @Override

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(CLUSTER_PROFILE_FIELDS));
    }
}