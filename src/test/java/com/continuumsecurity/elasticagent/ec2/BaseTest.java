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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class BaseTest {

    protected static HashSet<String> instances = new HashSet<>();

    protected ClusterProfileProperties createClusterProfiles() {
        ClusterProfileProperties settings = new ClusterProfileProperties();
        settings.setGoServerUrl(Properties.SERVER_URL);
        settings.setAutoRegisterTimeout(Properties.AUTO_REGISTER_TIMEOUT);
        settings.setMaxElasticAgents(Properties.MAX_ELASTIC_AGENTS);
        settings.setAwsAccessKeyId(Properties.ACCESS_KEY_ID);
        settings.setAwsSecretAccessKey(Properties.SECRET_ACCESS_KEY);
        settings.setAwsRegion(Properties.REGION);

        return settings;
    }

    protected Map<String, String> createProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("ec2_ami", Properties.AMI_ID);
        properties.put("ec2_instance_type", Properties.TYPE);
        properties.put("ec2_sg", Properties.SG_IDS);
        properties.put("ec2_subnets", Properties.SUBNETS);
        properties.put("ec2_key", Properties.KEY);
        properties.put("ec2_user_data", Properties.USERDATA);
        properties.put("ec2_instance_profile", Properties.INSTANCE_PROFILE)
        return properties;
    }
}
