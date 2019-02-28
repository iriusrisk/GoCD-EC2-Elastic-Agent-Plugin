package com.continuumsecurity.elasticagent.ec2.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Tag;
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


public class AgentStatusReport {

    private final JobIdentifier jobIdentifier;
    private final String instanceId;
    private final String instanceType;
    private final String imageId;
    private final String availabilityZone;
    private final String keyName;
    private final String architecture;
    private final String hypervisor;
    private final String rootDeviceName;
    private final String rootDeviceType;
    private final String virtualizationType;
    private final int coreCount;
    private final int threadsPerCore;
    private final String privateDnsName;
    private final String privateIpAddress;
    private final String publicDnsName;
    private final String publicIpAddress;
    private final String subnetId;
    private final String vpcId;
    private final String state;
    private final List<SecurityGroup> securityGroups;
    private final List<CustomTag> tags;
    private final Long launchTime;

    public AgentStatusReport(JobIdentifier jobIdentifier, Instance instance, Long launchTime) {
        this.jobIdentifier = jobIdentifier;
        this.instanceId = instance.instanceId();
        this.state = instance.state().nameAsString();
        this.instanceType = instance.instanceTypeAsString();
        this.imageId = instance.imageId();
        this.availabilityZone = instance.placement().availabilityZone();
        this.keyName = instance.keyName();
        this.architecture = instance.architectureAsString();
        this.hypervisor = instance.hypervisorAsString();
        this.rootDeviceName = instance.rootDeviceName();
        this.rootDeviceType = instance.rootDeviceTypeAsString();
        this.virtualizationType = instance.virtualizationTypeAsString();
        this.coreCount = instance.cpuOptions().coreCount();
        this.threadsPerCore = instance.cpuOptions().threadsPerCore();
        this.privateDnsName = instance.privateDnsName();
        this.privateIpAddress = instance.privateIpAddress();
        this.publicDnsName = instance.publicDnsName();
        this.publicIpAddress = instance.publicIpAddress();
        this.subnetId = instance.subnetId();
        this.vpcId = instance.vpcId();
        this.securityGroups = new ArrayList<>();
        for(GroupIdentifier groupIdentifier : instance.securityGroups()) {
            this.securityGroups.add(new SecurityGroup(groupIdentifier.groupId(), groupIdentifier.groupName()));
        }
        this.tags = new ArrayList<>();
        for(Tag tag : instance.tags()) {
            this.tags.add(new CustomTag(tag.key(), tag.value()));
        }
        this.launchTime = launchTime;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public String getElasticAgentId() {
        return instanceId;
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

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getHypervisor() {
        return hypervisor;
    }

    public String getRootDeviceName() {
        return rootDeviceName;
    }

    public String getRootDeviceType() {
        return rootDeviceType;
    }

    public String getVirtualizationType() {
        return virtualizationType;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public int getThreadsPerCore() {
        return threadsPerCore;
    }

    public String getPrivateDnsName() {
        return privateDnsName;
    }

    public Long getLaunchTime() {
        return launchTime;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public String getPublicDnsName() {
        return publicDnsName;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public String getState() {
        return state;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public String getVpcId() {
        return vpcId;
    }

    public List<SecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    public List<CustomTag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentStatusReport that = (AgentStatusReport) o;
        return Objects.equals(jobIdentifier, that.jobIdentifier) &&
                Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(launchTime, that.launchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobIdentifier, instanceId, launchTime);
    }

    public class SecurityGroup {

        private final String groupId;
        private final String groupName;

        private SecurityGroup(String groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }
    }

    public class CustomTag {

        private final String key;
        private final String value;

        private CustomTag(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
