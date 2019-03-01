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

package com.continuumsecurity.elasticagent.ec2.models;

public class NotRunningAgentStatusReport {
    private final String entity;

    public NotRunningAgentStatusReport(JobIdentifier jobIdentifier) {
        this.entity = String.format("Job Identifier: %s", jobIdentifier.represent());
    }

    public NotRunningAgentStatusReport(String elasticAgentId) {
        this.entity = String.format("Elastic Agent ID: %s", elasticAgentId);
    }

    public String getEntity() {
        return entity;
    }
}
