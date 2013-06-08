package ddth.dasp.statushetty.actionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.framework.scheduletask.ITaskRegistry;

public class TaskStatusActionHandler extends BaseActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel() {
        Map<String, Object> model = super.buildViewModel();
        model.put("TASK_REGISTRY", buildModelTask());
        return model;
    }

    @SuppressWarnings("unchecked")
    private Object buildModelTask() {
        List<Object> model = new ArrayList<Object>();
        Object temp = DaspGlobal.getGlobalVar(ITaskRegistry.GLOBAL_KEY);
        if (!(temp instanceof Map)) {
            temp = new HashMap<String, ITaskRegistry>();
        }
        Map<String, ITaskRegistry> allTaskRegistries = (Map<String, ITaskRegistry>) temp;
        Map<String, Object> modelEntry;
        for (Entry<String, ITaskRegistry> entry : allTaskRegistries.entrySet()) {
            modelEntry = new HashMap<String, Object>();
            modelEntry.put("id", entry.getKey());
            modelEntry.put("taskRegistry", entry.getValue());
            model.add(modelEntry);
        }
        return model;
    }
}
