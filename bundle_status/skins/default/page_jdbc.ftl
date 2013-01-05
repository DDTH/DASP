<#include "inc_layout.ftl">
<@LAYOUT>
    <div class="container-fluid">
	    <div class="tabbable">
	        <ul class="nav nav-tabs">
	            <#assign ACTIVE_TAB="jdbc"><#include "inc_top_tabs.ftl">
	        </ul>
	        <div class="tab-content">
	            <div id="cache" class="tab-pane active">
	                <h4>JDBC Information</h4>
	                <table class="table">
	                    <thead>
	                        <tr>
                                <th>ID/Name</th>
                                <th style="text-align: right">DataSources</th>
                                <th style="text-align: right">OpenConns</th>
                                <th style="text-align: right">Actives</th>
                                <th style="text-align: right">Idles</th>
                                <th style="text-align: right">Timeout</th>
                                <th style="text-align: right">Stats</th>
	                        </tr>
	                    </thead>
                        <tbody>
	                        <#list JDBC as jdbc>
	                           <#assign jdbcFactory=jdbc.jdbcFactory />
                                <tr>
                                    <td><strong>${jdbc.id?html} [${jdbcFactory.class.name}]</strong></td>
                                    <td style="text-align: right">${jdbcFactory.countDataSources()}</td>
                                    <td style="text-align: right">${jdbcFactory.countOpenConnections()}</td>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <#list jdbcFactory.dataSources?keys as dsName>
                                    <#assign dsInfo=jdbcFactory.getDataSourceInfo(dsName) />
                                    <tr>
                                       <td>
                                           &nbsp;&nbsp;&nbsp;&nbsp;+ ${dsInfo.name?html}
                                       </td>
                                       <td colspan="2">&nbsp;</td>
                                       <td style="text-align: right" nowrap="nowrap">${dsInfo.numActives}/${dsInfo.maxActives}</td>
                                       <td style="text-align: right" nowrap="nowrap">${dsInfo.numIdles}/${dsInfo.minIdles} - ${dsInfo.maxIdles}</td>
                                       <td style="text-align: right" nowrap="nowrap">${dsInfo.maxWait}</td>
                                       <td style="text-align: right" nowrap="nowrap">${dsInfo.numOpens}/${dsInfo.numCloses}/${dsInfo.numLeakCloses}</td>
                                   </tr>
                                </#list>
                            </#list>
	                    </tbody>
	                </table>
	            </div>
	        </div>
	        <!-- /.tab-content -->
	    </div>
	    <!-- /.tabbable -->
	</div>
</@LAYOUT>