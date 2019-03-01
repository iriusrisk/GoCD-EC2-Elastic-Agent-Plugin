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

import com.google.gson.annotations.Expose;

public class InstanceStatusReport {

    @Expose
    private String instanceId;
    @Expose
    private String instanceType;
    @Expose
    private String imageId;
    @Expose
    private String state;
    @Expose
    private String privateIpAddress;
    @Expose
    private Long launchTime;
    @Expose
    private String pipeline;

    public InstanceStatusReport(String instanceId, String instanceType, String imageId, String state, String privateIpAddress, Long launchTime, String pipeline) {
        this.instanceId = instanceId;
        this.instanceType = instanceType;
        this.imageId = imageId;
        this.state = state;
        this.privateIpAddress = privateIpAddress;
        this.launchTime = launchTime;
        this.pipeline = pipeline;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public String getImageId() {
        return imageId;
    }

    public String getState() {
        return state;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public Long getLaunchTime() {
        return launchTime;
    }

    public String getPipeline() {
        return pipeline;
    }
}
