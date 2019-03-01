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

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import com.continuumsecurity.elasticagent.ec2.utils.Util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

public class GetViewRequestExecutorTest {

    @Test
    public void shouldRenderTheTemplateInJSON() throws Exception {
        GoPluginApiResponse response = new GetViewRequestExecutor().execute();
        assertThat(response.responseCode(), is(200));
        Map<String, String> hashSet = new Gson().fromJson(response.responseBody(), HashMap.class);
        assertThat(hashSet, hasEntry("template", Util.readResource("/plugin-settings.template.html")));
    }

    @Test
    public void allFieldsShouldBePresentInView() throws Exception {
        String template = Util.readResource("/plugin-settings.template.html");

        for (Map.Entry<String, Field> fieldEntry : GetPluginConfigurationExecutor.FIELDS.entrySet()) {
            assertThat(template, containsString("ng-model=\"" + fieldEntry.getKey() + "\""));
            assertThat(template, containsString("<span class=\"form_error\" ng-show=\"GOINPUTNAME[" + fieldEntry.getKey() +
                    "].$error.server\">\n" +
                    "            {{GOINPUTNAME[" + fieldEntry.getKey() +
                    "].$error.server}}</span>"));
        }
    }

}
