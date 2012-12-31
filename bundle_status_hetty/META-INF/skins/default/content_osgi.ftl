<div class="container-fluid">
	<div class="tabbable">
		<ul class="nav nav-tabs">
			<#assign ACTIVE_TAB="osgi"><#include "inc_top_tabs.ftl">
		</ul>
		<div class="tab-content">
			<div id="osgi" class="tab-pane active">
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
