package ddth.dasp.statushetty.actionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.framework.dbc.IJdbcFactory;
import ddth.dasp.hetty.message.IRequest;

public class JdbcStatusActionHandler extends BaseActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel(IRequest request) {
        Map<String, Object> model = super.buildViewModel(request);
        model.put("JDBC", buildModelJdbc());
        return model;
    }

    @SuppressWarnings("unchecked")
    private Object buildModelJdbc() {
        List<Object> model = new ArrayList<Object>();
        Object temp = DaspGlobal.getGlobalVar(IJdbcFactory.GLOBAL_KEY);
        if (!(temp instanceof Map)) {
            temp = new HashMap<String, IJdbcFactory>();
        }
        Map<String, IJdbcFactory> allJdbcFactories = (Map<String, IJdbcFactory>) temp;
        Map<String, Object> modelEntry;
        for (Entry<String, IJdbcFactory> entry : allJdbcFactories.entrySet()) {
            modelEntry = new HashMap<String, Object>();
            modelEntry.put("id", entry.getKey());
            modelEntry.put("jdbcFactory", entry.getValue());
            model.add(modelEntry);
        }
        return model;
    }
}
