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
 */

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstances;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.ClusterProfileChangedRequestExecutor;
import com.google.common.base.Strings;

import java.util.Map;
import java.util.Optional;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.GSON;

public class ClusterProfileChangedRequest {
    public enum ChangeStatus {
        CREATED("created"),
        UPDATED("updated"),
        DELETED("deleted");

        private final String status;

        ChangeStatus(String status) {
            this.status = status;
        }

        public static Optional<ChangeStatus> fromString(String status) {
            if (Strings.isNullOrEmpty(status)) {
                return Optional.empty();
            }

            switch (status) {
                case "created":
                    return Optional.of(CREATED);
                case "updated":
                    return Optional.of(UPDATED);
                case "deleted":
                    return Optional.of(DELETED);
            }

            return Optional.empty();
        }
    }


    private String status;
    private ChangeStatus changeStatus;
    private ClusterProfileProperties clusterProfilesProperties;
    private ClusterProfileProperties oldClusterProfilesProperties;

    public ClusterProfileChangedRequest() {
    }

    public ClusterProfileChangedRequest(ChangeStatus status, ClusterProfileProperties clusterProfilesProperties, ClusterProfileProperties oldClusterProfilesProperties) {
        this.changeStatus = status;
        this.clusterProfilesProperties = clusterProfilesProperties;
        this.oldClusterProfilesProperties = oldClusterProfilesProperties;
    }

    public static ClusterProfileChangedRequest fromJSON(String json) {

        ClusterProfileChangedRequest request = GSON.fromJson(json, ClusterProfileChangedRequest.class);
        Optional<ChangeStatus> changeStatus = ChangeStatus.fromString(request.status);

        if (changeStatus.isPresent()) {
            request.changeStatus = changeStatus.get();
            return request;
        }

        throw new RuntimeException("Invalid ChangeStatus specified '%s', valid values are [created, updated, deleted]");
    }

    public RequestExecutor executor(Map<String, Ec2AgentInstances> allClusterInstances) {
        return new ClusterProfileChangedRequestExecutor(this, allClusterInstances);
    }

    public ChangeStatus changeStatus() {
        return changeStatus;
    }

    public ClusterProfileProperties clusterProperties() {
        return clusterProfilesProperties;
    }

    public ClusterProfileProperties oldClusterProperties() {
        return oldClusterProfilesProperties;
    }
}
