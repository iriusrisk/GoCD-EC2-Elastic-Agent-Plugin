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

package com.continuumsecurity.elasticagent.ec2.requests;

import com.continuumsecurity.elasticagent.ec2.ClusterProfileProperties;
import com.continuumsecurity.elasticagent.ec2.Ec2AgentInstances;
import com.continuumsecurity.elasticagent.ec2.PluginRequest;
import com.continuumsecurity.elasticagent.ec2.RequestExecutor;
import com.continuumsecurity.elasticagent.ec2.executors.ServerPingRequestExecutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.continuumsecurity.elasticagent.ec2.Ec2Plugin.GSON;

public class ServerPingRequest {

    private List<ClusterProfileProperties> allClusterProfileProperties;

    public ServerPingRequest() {
    }

    public ServerPingRequest(List<Map<String, String>> allClusterProfileProperties) {
        this.allClusterProfileProperties = allClusterProfileProperties.stream()
                .map(ClusterProfileProperties::fromConfiguration)
                .collect(Collectors.toList());
    }

    public static ServerPingRequest fromJSON(String json) {
        return GSON.fromJson(json, ServerPingRequest.class);
    }

    public List<ClusterProfileProperties> allClusterProfileProperties() {
        return allClusterProfileProperties;
    }

    @Override
    public String toString() {
        return "ServerPingRequest{" +
                "allClusterProfileProperties=" + allClusterProfileProperties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerPingRequest that = (ServerPingRequest) o;
        return Objects.equals(allClusterProfileProperties, that.allClusterProfileProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allClusterProfileProperties);
    }

    public RequestExecutor executor(Map<String, Ec2AgentInstances> clusterSpecificAgentInstances, PluginRequest pluginRequest) {
        return new ServerPingRequestExecutor(this, clusterSpecificAgentInstances, pluginRequest);
    }
}
