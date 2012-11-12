<div class="container-fluid">
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#assign ACTIVE_TAB="server"><#include "inc_top_tabs.ftl">
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
		</div>
		<!-- /.tab-content -->
	</div>
	<!-- /.tabbable -->
</div>
