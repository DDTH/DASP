<div class="container-fluid">
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<li class="active"><a href="#server" data-toggle="tab">Server</a></li>
			<li><a href="#osgi" data-toggle="tab">OSGi</a></li>
		</ul>
		<div class="tab-content">
			<div id="server" class="tab-pane active">
				<div class="span6">
					<h4>Server Information</h4>
					<table class="table">
						<tbody>
							<tr>
								<td>CPU Processors</td>
								<td>${SERVER.cpu_processors}</td>
							</tr>
							<tr>
								<td>OS</td>
								<td>${SERVER.os}</td>
							</tr>
							<tr>
								<td>Memory</td>
								<td>
									<div class="progress" title="Used: ${SERVER.memory_used_percent}%"><div class="bar" style="width: ${SERVER.memory_used_percent}%;"></div></div>
									Used: ${SERVER.memory_used/1024/1024}Mb / ${SERVER.memory_available/1024/1024}Mb
								</td>
							</tr>
							<tr>
								<td>Java</td>
								<td>
									${SERVER.java}<br />
									${SERVER.java_spec}<br />
									${SERVER.java_vm}<br />
									${SERVER.java_vm_spec}<br />
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<div id="osgi" class="tab-pane">
				<h4>OSGi Information</h4>
				<table class="table">
					<thead>
						<tr>
							<th>ID</th>
							<th>State</th>
							<th>Version</th>
							<th>Name</th>
						</tr>
					</thead>
					<tbody>
						<#list OSGI as bundle>
							<tr>
								<td>${bundle.id}</td>
								<td>${bundle.state}</td>
								<td>${bundle.version}</td>
								<td>${bundle.name}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
		<!-- /.tab-content -->
	</div>
	<!-- /.tabbable -->
</div>
