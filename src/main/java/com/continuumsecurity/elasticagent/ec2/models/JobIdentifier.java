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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Objects;

import static com.continuumsecurity.elasticagent.ec2.utils.Util.isBlank;
import static java.text.MessageFormat.format;

public class JobIdentifier {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Expose
    private String pipelineName;

    @Expose
    private Long pipelineCounter;

    @Expose
    private String pipelineLabel;

    @Expose
    private String stageName;

    @Expose
    private String stageCounter;

    @Expose
    private String jobName;

    @Expose
    private Long jobId;

    private String representation;

    public JobIdentifier(Long jobId) {
        this.jobId = jobId;
    }

    public JobIdentifier() {
    }

    public JobIdentifier(String pipelineName, Long pipelineCounter, String pipelineLabel, String stageName, String stageCounter, String jobName, Long jobId) {
        this.pipelineName = pipelineName;
        this.pipelineCounter = pipelineCounter;
        this.pipelineLabel = pipelineLabel;
        this.stageName = stageName;
        this.stageCounter = stageCounter;
        this.jobName = jobName;
        this.jobId = jobId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public Long getPipelineCounter() {
        return pipelineCounter;
    }

    public String getPipelineLabel() {
        return pipelineLabel;
    }

    public String getStageName() {
        return stageName;
    }

    public String getStageCounter() {
        return stageCounter;
    }

    public String getRepresentation() {
        if (isBlank(representation)) {
            this.representation = format("{0}/{1}/{2}/{3}/{4}", pipelineName, pipelineCounter, stageName, stageCounter, jobName);
        }
        return representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobIdentifier that = (JobIdentifier) o;
        return Objects.equals(pipelineName, that.pipelineName) &&
                Objects.equals(pipelineCounter, that.pipelineCounter) &&
                Objects.equals(pipelineLabel, that.pipelineLabel) &&
                Objects.equals(stageName, that.stageName) &&
                Objects.equals(stageCounter, that.stageCounter) &&
                Objects.equals(jobName, that.jobName) &&
                Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pipelineName, pipelineCounter, pipelineLabel, stageName, stageCounter, jobName, jobId);
    }

    @Override
    public String toString() {
        return "JobIdentifier{" +
                "pipelineName='" + pipelineName + '\'' +
                ", pipelineCounter=" + pipelineCounter +
                ", pipelineLabel='" + pipelineLabel + '\'' +
                ", stageName='" + stageName + '\'' +
                ", stageCounter='" + stageCounter + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobId=" + jobId +
                '}';
    }

    public String represent() {
        return String.format("%s/%d/%s/%s/%s", pipelineName, pipelineCounter, stageName, stageCounter, jobName);
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static JobIdentifier fromJson(String json) {
        return GSON.fromJson(json, JobIdentifier.class);
    }
}
