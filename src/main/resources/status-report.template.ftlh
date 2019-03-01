<style>
	.main-container {
		margin-top: 0;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .container {
		background: transparent !important;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .page_header {
		display:         flex;
		width:           100%;
		justify-content: space-between;
		align-items:     center;
		background:      #fff;
		box-shadow:      0 0 0 #000;
		height:          50px;
		box-sizing:      border-box;
		border-bottom:   1px solid #ccc;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .page_title {
		font-size:   17px;
		font-weight: 600;
		margin:      0;
		align-self:  center;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 {
		border:        1px solid #d1c4e9;
		padding:       25px;
		margin-top:    65px;
		background:    #fff;
		border-radius: 3px;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 table {
		width:     100%;
		border:    1px solid #D8D8D8;
		margin:    10px 0px 0px 0px;
		font-size: 13px;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 table thead {
		background: #b9f6ca;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 table thead th {
		padding:     10px 20px;
		font-weight: bold;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 table tbody tr {
		border-top:      1px solid #D8D8D8;
		border-bottom:   1px solid #D8D8D8;
		border-collapse: collapse;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 table tbody td {
		padding:       10px 20px;
		max-width:     190px;
		text-overflow: ellipsis;
		overflow:      hidden;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .message {
		font-size:   13px;
		font-weight: 600;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] dl.properties {
		overflow:       hidden;
		margin:         0;
		font-size:      13px;
		display:        inline-block;
		vertical-align: top;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] dt {
		float:       left;
		clear:       both;
		margin:      0;
		padding:     5px 0px;
		font-weight: 600;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] dt:after {
		content: ":";
		padding: 0px 5px 0px 2px;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] dd {
		float:        left;
		padding:      5px 0px;
		margin-right: 50px;
	}

	[data-plugin-style-id="ec2-elastic-plugin"] .ec2 .header {
		margin: 20px 0px 0px 0px;
	}
</style>

<div data-plugin-style-id="ec2-elastic-plugin">
	<div class="container">
		<div class="page_header">
			<h1 class="page_title">Plugin Status Report</h1>
		</div>
		<div class="ec2 row">
			<div>
				<dl class="properties">
					<dt>Number of instances</dt>
					<dd>${numberInstances!}</dd>
				</dl>
			</div>
			<h4 class="header">Instances</h4>
			<table>
				<thead>
				<tr>
					<th>Id</th>
					<th>Type</th>
					<th>AMI</th>
					<th>State</th>
					<th>Private IP</th>
					<th>Launched At</th>
					<th>Launched From</th>
				</thead>
				</tr>
				<tbody>
					<#if instanceStatusReportList?size != 0>
						<#list instanceStatusReportList as instance>
						<tr>
							<td><#if instance.state != "shutting-down">
								<a href="/go/admin/status_reports/${pluginId}/${instance.instanceId!''}"
								   title="Show details"
								   rel="nofollow noreferrer" target="_blank">${instance.instanceId!}</a>
								<#else>
									${instance.instanceId!}
								</#if></td>
							<td>${instance.instanceType!}</td>
							<td>${instance.imageId!}</td>
							<td>${instance.state!}</td>
							<td>${instance.privateIpAddress!}</td>
							<td>{{ ${instance.launchTime?long?c!} | date:"dd MMM yyyy HH:mm:ss"}}</td>
							<td>${instance.pipeline!}</td>
						</tr>
						</#list>
					<#else>
						<tr>
							<td colspan="7" class="message">No running instances.</td>
						</tr>
					</#if>
				</tbody>
			</table>
		</div>
	</div>
</div>
