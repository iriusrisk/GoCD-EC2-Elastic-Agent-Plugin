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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.Period;

import software.amazon.awssdk.regions.Region;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class PluginSettings {
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    @SerializedName("go_server_url")
    private String goServerUrl;

    @Expose
    @SerializedName("auto_register_timeout")
    private String autoRegisterTimeout;

    @Expose
    @SerializedName("max_elastic_agents")
    private String maxElasticAgents;

    @Expose
    @SerializedName("aws_access_key_id")
    private String awsAccessKeyId;

    @Expose
    @SerializedName("aws_secret_access_key")
    private String awsSecretAccessKey;

    @Expose
    @SerializedName("aws_region")
    private String awsRegion;

    private Period autoRegisterPeriod;

    public static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        if (goServerUrl != null ? !goServerUrl.equals(that.goServerUrl) : that.goServerUrl != null) return false;
        if (autoRegisterTimeout != null ? !autoRegisterTimeout.equals(that.autoRegisterTimeout) : that.autoRegisterTimeout != null) return false;
        if (maxElasticAgents != null ? !maxElasticAgents.equals(that.maxElasticAgents) : that.maxElasticAgents != null) return false;
        if (awsAccessKeyId != null ? !awsAccessKeyId.equals(that.awsAccessKeyId) : that.awsAccessKeyId != null) return false;
        if (awsSecretAccessKey != null ? !awsSecretAccessKey.equals(that.awsSecretAccessKey) : that.awsSecretAccessKey != null) return false;
        if (awsRegion != null ? !awsRegion.equals(that.awsRegion) : that.awsRegion != null) return false;
        return autoRegisterPeriod != null ? autoRegisterPeriod.equals(that.autoRegisterPeriod) : that.autoRegisterPeriod == null;
    }

    @Override
    public int hashCode() {
        int result = goServerUrl != null ? goServerUrl.hashCode() : 0;
        result = 31 * result + (autoRegisterTimeout != null ? autoRegisterTimeout.hashCode() : 0);
        result = 31 * result + (maxElasticAgents != null ? maxElasticAgents.hashCode() : 0);
        result = 31 * result + (awsAccessKeyId != null ? awsAccessKeyId.hashCode() : 0);
        result = 31 * result + (awsSecretAccessKey != null ? awsSecretAccessKey.hashCode() : 0);
        result = 31 * result + (awsRegion != null ? awsRegion.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        String pluginSettingsString = "PluginSettings{" +
                "goServerUrl='" + goServerUrl + '\'' +
                ", autoRegisterTimeout='" + autoRegisterTimeout + '\'' +
                ", maxElasticAgents='" + maxElasticAgents + '\'';
        if (awsAccessKeyId != null && !awsAccessKeyId.isBlank()) pluginSettingsString += ", awsAccessKeyId='" + awsAccessKeyId + '\'';
        if (awsSecretAccessKey != null && !awsSecretAccessKey.isBlank()) pluginSettingsString += ", awsSecretAccessKey='" + awsSecretAccessKey + '\'';
        pluginSettingsString += ", awsRegion='" + awsRegion + '\'' +
                ", autoRegisterPeriod=" + autoRegisterPeriod +
                '}';
        
        return pluginSettingsString;
    }

    public Period getAutoRegisterPeriod() {
        if (this.autoRegisterPeriod == null) {
            this.autoRegisterPeriod = new Period().withMinutes(Integer.parseInt(getAutoRegisterTimeout()));
        }
        return this.autoRegisterPeriod;
    }

    public String getAutoRegisterTimeout() {
        if (autoRegisterTimeout == null) {
            autoRegisterTimeout = "10";
        }
        return autoRegisterTimeout;
    }

    public String getGoServerUrl() {
        return goServerUrl;
    }

    public Integer getMaxElasticAgents() {
        return Integer.valueOf(maxElasticAgents);
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public Region getAwsRegion() throws IllegalArgumentException {
        return region(awsRegion);
    }

    private static Region region(String configuredRegion) {
        if (isBlank(configuredRegion)) {
            throw new IllegalArgumentException("Must provide `ec2_region` attribute.");
        }

        Region newRegion = Region.of(configuredRegion);

        if (!Region.regions().contains(newRegion)) {
            throw new IllegalArgumentException("Region does not exist.");
        }

        return newRegion;
    }

    public void setGoServerUrl(String goServerUrl) {
        this.goServerUrl = goServerUrl;
    }

    public void setAutoRegisterTimeout(String autoRegisterTimeout) {
        this.autoRegisterTimeout = autoRegisterTimeout;
    }

    public void setMaxElasticAgents(String maxElasticAgents) {
        this.maxElasticAgents = maxElasticAgents;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) {
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }
}
