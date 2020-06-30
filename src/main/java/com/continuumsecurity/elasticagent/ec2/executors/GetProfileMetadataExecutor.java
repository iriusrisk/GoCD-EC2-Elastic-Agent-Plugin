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

import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;

public class GetProfileMetadataExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static final Metadata EC2_AMI = new Metadata("ec2_ami", true, false);
    private static final Metadata EC2_INSTANCE_TYPE = new Metadata("ec2_instance_type", true, false);
    private static final Metadata EC2_SECURITY_GROUPS = new Metadata("ec2_sg", true, false);
    private static final Metadata EC2_SUBNETS = new Metadata("ec2_subnets", true, false);
    private static final Metadata EC2_KEY = new Metadata("ec2_key", true, false);
    private static final Metadata EC2_USER_DATA = new Metadata("ec2_user_data", false, false);
    private static final Metadata EC2_INSTANCE_PROFILE = new Metadata("ec2_instance_profile", false, false)

    static final List<Metadata> FIELDS = new ArrayList<>();

    static {
        FIELDS.add(EC2_AMI);
        FIELDS.add(EC2_INSTANCE_TYPE);
        FIELDS.add(EC2_SECURITY_GROUPS);
        FIELDS.add(EC2_SUBNETS);
        FIELDS.add(EC2_KEY);
        FIELDS.add(EC2_USER_DATA;
        FIELDS.add(EC2_INSTANCE_PROFILE);
        );
    }

    @Override

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
