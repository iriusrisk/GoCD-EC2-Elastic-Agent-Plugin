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

import com.continuumsecurity.elasticagent.ec2.requests.ProfileValidateRequest;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Collections;

public class ProfileValidateRequestExecutorTest {
    @Test
    public void shouldBarfWhenUnknownKeysArePassed() throws Exception {
        ProfileValidateRequestExecutor executor = new ProfileValidateRequestExecutor(new ProfileValidateRequest(Collections.singletonMap("foo", "bar")));
        String json = executor.execute().responseBody();
        JSONAssert.assertEquals("[{\"message\":\"ec2_ami must not be blank.\",\"key\":\"ec2_ami\"},{\"message\":\"ec2_instance_type must not be blank.\",\"key\":\"ec2_instance_type\"},{\"message\":\"ec2_sg must not be blank.\",\"key\":\"ec2_sg\"},{\"message\":\"ec2_subnets must not be blank.\",\"key\":\"ec2_subnets\"},{\"message\":\"ec2_key must not be blank.\",\"key\":\"ec2_key\"},{\"key\":\"foo\",\"message\":\"Is an unknown property\"}]", json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        ProfileValidateRequestExecutor executor = new ProfileValidateRequestExecutor(new ProfileValidateRequest(Collections.<String, String>emptyMap()));
        String json = executor.execute().responseBody();
        JSONAssert.assertEquals("[{\"message\":\"ec2_ami must not be blank.\",\"key\":\"ec2_ami\"},{\"message\":\"ec2_instance_type must not be blank.\",\"key\":\"ec2_instance_type\"},{\"message\":\"ec2_sg must not be blank.\",\"key\":\"ec2_sg\"},{\"message\":\"ec2_subnets must not be blank.\",\"key\":\"ec2_subnets\"},{\"message\":\"ec2_key must not be blank.\",\"key\":\"ec2_key\"}]", json, JSONCompareMode.NON_EXTENSIBLE);
    }
}