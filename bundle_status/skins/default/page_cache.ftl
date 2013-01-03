<#include "inc_layout.ftl">
<@LAYOUT>
	<div class="container-fluid">
	    <div class="tabbable">
	        <ul class="nav nav-tabs">
	            <#assign ACTIVE_TAB="cache"><#include "inc_top_tabs.ftl">
	        </ul>
	        <div class="tab-content">
	            <div id="cache" class="tab-pane active">
	                <h4>Cache Information</h4>
	                <table class="table">
	                    <thead>
	                        <tr>
	                            <th>ID/Name</th>
	                            <th style="text-align: right">Size</th>
	                            <th style="text-align: right">Capacity</th>
	                            <th style="text-align: right">Hits</th>
	                        </tr>
	                    </thead>
	                    <tbody>
	                        <#list CACHE as cm>
	                            <tr>
	                                <td><strong>${cm.id?html} [${cm.cacheManager.class.name}]</strong></td>
	                                <td colspan="3">&nbsp;</td>
	                            </tr>
	                            <#list cm.cacheManager.caches as cache>
	                               <tr>
	                                   <td>
                                           &nbsp;&nbsp;&nbsp;&nbsp;+ ${cache.name?html}
                                           (Access: ${cache.expireAfterAccess} / Write: ${cache.expireAfterWrite})
                                       </td>
	                                   <td style="text-align: right">${cache.size}</td>
	                                   <td style="text-align: right">${cache.capacity}</td>
	                                   <td style="text-align: right">${cache.hits}<#if cache.hits+cache.misses gt 0> (${100*cache.hits/(cache.hits+cache.misses)}%)</#if></td>
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