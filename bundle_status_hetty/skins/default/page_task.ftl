<#include "layout.ftl">
<@LAYOUT>
    <div class="container-fluid">
        <div class="tabbable">
            <ul class="nav nav-tabs">
                <#assign ACTIVE_TAB="task"><#include "inc_top_tabs.ftl">
            </ul>
            <div class="tab-content">
                <div id="task" class="tab-pane active">
                    <h4>Task Information</h4>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID/Name</th>
                                <th style="text-align: center">Status</th>
                                <th style="text-align: center">Scheduling</th>
                                <th style="text-align: center">Initial</th>
                                <th style="text-align: center">Fixed Rate</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list TASK_REGISTRY as tr>
                                <tr>
                                    <td><strong>${tr.id?html} [${tr.taskRegistry.class.name}]</strong></td>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <#list tr.taskRegistry.scheduledTasks as task>
                                   <tr>
                                       <td>
                                           &nbsp;&nbsp;&nbsp;&nbsp;+ ${task.id?html}
                                           (${task.class.name})
                                       </td>
                                       <td style="text-align: center"><#if task.status??>${task.status}<#else>-</#if></td>
                                       <td style="text-align: center"><#if task.scheduling??>${task.scheduling}<#else>-</#if></td>
                                       <td style="text-align: right"><#if task.initialTimeUnit??>${task.initialDelay}/${task.initialTimeUnit}<#else>-</#if></td>
                                       <td style="text-align: right"><#if task.fixedRateTimeUnit??>${task.fixedRateDelay}/${task.fixedRateTimeUnit}<#else>-</#if></td>
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