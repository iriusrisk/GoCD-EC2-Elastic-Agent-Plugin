<style>
	.main-container {
		margin-top: 0;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2-elastic-agent {
        padding: 70px 20px 0 20px;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tabs {
        position: relative;
        clear: both;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tabs .tab {
        float: left;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tabs .tab .content {
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tab label {
        background-color: #d1c4e9;
        padding: 7px 15px;
        line-height: 19px;
        font-size: 13px;
        border-radius: 3px 3px 0 0;
        font-weight: 600;
        color: #333;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tab-content .logs {
        background-color: #383838;
        font-size: 13px;
        font-family: monaco;
        color: white;
        display: block;
        font-weight: 400;
        padding-left: 10px;
        height: calc(100vh - 290px);
        max-height: calc(100vh - 290px);
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tab-content {
        margin-top: 0;
        height: calc(100vh - 280px);
        overflow-x: hidden;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .tab-content-outer {
        background: white;
        padding: 20px;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .warning-message {
        background-color: #fdf5e2;
        color: #b88c34;
        font-weight: 600;
        font-size: 15px;
        padding: 10px 20px;
        border-radius: 3px;
        margin: 0 0 1rem 0;
        border: 1px solid rgba(10, 10, 10, 0.25);
        position: relative;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .entity_title span.label {
        position: absolute;
        margin-top: -10px;
        font-size: 0.625em;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .status-report-page-header {
        width: 100%;
        height: 50px;
        z-index: 10;
        position: fixed;
        background: #fff;
        box-shadow: 0 0 0 #000;
        border-bottom: 1px solid #ddd;
        background-size: cover;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .status-report-page-header .entity_title {
        top: 12px;
        left: 20px;
        position: absolute;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .status-report-page-header .entity_title .last h1 {
        margin-top: 2px !important;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .status-report-page-header .entity_title li a {
        font-size: 0.9375rem;
        font-weight: 600;
        line-height: normal;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] textarea[readonly="readonly"], textarea[readonly] {
        cursor: auto;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value {
        float: none;
        list-style-type: none;
        clear: both;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair {
        float: none;
        display: flex;
        border-bottom: 1px dotted #ddd;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair label {
        width: 225px;
        padding: 5px 10px 5px 0px;
        min-width: 150px;
        word-wrap: break-word;
        font-size: 13px;
        font-weight: 600;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair span {
        padding: 5px 10px 5px 0px;
        font-size: 13px;
        line-height: 20px;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair pre {
        line-height: 2.5;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] table {
        text-align: left;
        border-spacing: 0;
        border-collapse: collapse;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] table thead tr th {
        font-weight: 600;
        border-bottom: 2px solid #ddd;
        border-top: 1px solid #ddd;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] no-padding-left {
        padding: 8px 8px 8px 0px;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair table tr th,
    [data-plugin-style-id="ec2-elastic-plugin"] .name-value_pair table tr td {
        border-bottom: 1px dotted #ddd;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] table tr th,
    [data-plugin-style-id="ec2-elastic-plugin"] table tr td {
        padding: 8px;
        font-size: 13px;
        border-top: 0;
        line-height: 1.42857143;
        border-bottom: 1px solid #ddd;
        vertical-align: top;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .task-status {
        display: inline-block;
        width: 10px;
        height: 10px;
        margin-top: 6px;
        margin-right: 5px;
        background-color: #FFEB3B;
        border-radius: 100%;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .running
    [data-plugin-style-id="ec2-elastic-plugin"] .complete {
        background-color: #4CAF50;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] .failed,
    [data-plugin-style-id="ec2-elastic-plugin"] .rejected {
        background-color: #F44336;
    }

    [data-plugin-style-id="ec2-elastic-plugin"] td.message {
        padding: 5px;
    }
</style>

<div data-plugin-style-id="ec2-elastic-plugin">
    <div class="status-report-page-header">
        <ul class="entity_title">
            <li class="name">
                <span class="label">Pipeline</span>
                <a href="/go/tab/pipeline/history/${jobIdentifier.pipelineName!''}"
                   title="View this pipeline's activity"
                   rel="nofollow noreferrer" target="_blank">${jobIdentifier.pipelineName!''}</a>
            </li>
            <li class="pipeline_label">
                <span class="label">Instance</span>
                <span class="run_no">${jobIdentifier.pipelineCounter!''}</span>
                <a href="/go/pipelines/value_stream_map/${jobIdentifier.pipelineName!''}/${jobIdentifier.pipelineCounter!''}"
                   title="View this stage's jobs summary"
                   rel="nofollow noreferrer" target="_blank">VSM</a>
            </li>
            <li class="stage_name">
                <span class="label">Stage</span>
                <a
                        href="/go/pipelines/${jobIdentifier.pipelineName!''}/${jobIdentifier.pipelineCounter!''}/${jobIdentifier.stageName!''}/${jobIdentifier.stageCounter!''}"
                        title="View this stage's details"
                        rel="nofollow noreferrer" target="_blank">${jobIdentifier.stageName!''}
                    / ${jobIdentifier.stageCounter!''}</a>
            </li>
            <li class="job_label">
                <span class="label">Job</span>
                <a
                        href="/go/tab/build/detail/${jobIdentifier.representation!''}"
                        title="View this job's details"
                        rel="nofollow noreferrer" target="_blank">${jobIdentifier.jobName!''}</a>
            </li>

            <li class="last">
                <span class="label">Elastic Agent Id</span>
                <h1>${instanceId!}</h1>
            </li>
        </ul>
    </div>

    <div class="content_wrapper_inner ec2-elastic-agent">
        <div class="row">
            <div class="sub_tabs_container">
                <ul class="tabs" ng-init="currenttab=currentab || 'general'">
                    <li ng-class="{'current_tab' : currenttab == 'general' }"
                        ng-click="currenttab='general'">
                        <a href="#">General</a>
                    </li>
                    <li ng-class="{'current_tab' : currenttab == 'network' }" ng-click="currenttab='network'">
                        <a href="#">Network</a>
                    </li>
                    <li ng-class="{'current_tab' : currenttab == 'tags' }" ng-click="currenttab='tags'">
                        <a href="#">Tags</a>
                    </li>
                </ul>
            </div>

            <div class="tab-content-outer">
                <div class="tab-content" ng-show="currenttab == 'general'">
                    <ul class="name-value">
                        <li class="name-value_pair">
                            <label>Instance ID</label>
                            <span>${instanceId!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>State</label>
                            <span>${state!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Type</label>
                            <span>${instanceType!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>AMI</label>
                            <span>${imageId!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Availability Zone</label>
                            <span>${availabilityZone!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Key</label>
                            <span>${keyName!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Architecture</label>
                            <span>${architecture!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Hypervisor</label>
                            <span>${hypervisor!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Root Device Name</label>
                            <span>${rootDeviceName!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Root Device Type</label>
                            <span>${rootDeviceType!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Virtualization Type</label>
                            <span>${virtualizationType!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>CPU cores</label>
                            <span>${coreCount!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>CPU threads per core</label>
                            <span>${threadsPerCore!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Launch Time</label>
                            <span>{{ ${launchTime?long?c!} | date:"dd MMM yyyy HH:mm:ss"}}</span>
                        </li>
                    </ul>
                    <br/><br/>
                </div>
                <div class="tab-content network" ng-show="currenttab == 'network'">
                    <ul class="name-value">
                        <li class="name-value_pair">
                            <label>Private DNS Name</label>
                            <span>${privateDnsName!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Private IP Address</label>
                            <span>${privateIpAddress!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Public DNS Name</label>
                            <span>${publicDnsName!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Public IP Address</label>
                            <span>${publicIpAddress!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>Subnet ID</label>
                            <span>${subnetId!}</span>
                        </li>
                        <li class="name-value_pair">
                            <label>VPC ID</label>
                            <span>${vpcId!}</span>
                        </li>
                        <#if securityGroups?size != 0>
                            <li class="name-value_pair">
                                <label>Security Groups</label>
                                <span>
                                    <table>
                                        <tbody>
                                        <#list securityGroups as securityGroup>
                                            <tr>
                                                <td>${securityGroup.groupName!}</td>
                                                <td>${securityGroup.groupId!}</td>
                                            </tr>
                                        </#list>
                                        </tbody>
                                    </table>
                                </span>
                            </li>
                        </#if>
                    </ul>
                </div>
                <div class="tab-content tags" ng-show="currenttab == 'tags'">
                    <table>
                        <thead>
                        <tr>
                            <th>Key</th>
                            <th>Value</th>
                        </thead>
                        </tr>
                        <tbody>
                        <#if tags?size != 0>
                            <#list tags as tag>
                                <tr>
                                    <td>${tag.key!}</td>
                                    <td>${tag.value!}</td>
                                </tr>
                            </#list>
                        <#else>
                            <tr>
                                <td colspan="2" class="message">No tags defined.</td>
                            </tr>
                        </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>


