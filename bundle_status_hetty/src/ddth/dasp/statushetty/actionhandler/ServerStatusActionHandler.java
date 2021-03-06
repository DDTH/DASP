package ddth.dasp.statushetty.actionhandler;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.hetty.message.IRequest;

public class ServerStatusActionHandler extends BaseActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel(IRequest request) {
        Map<String, Object> model = super.buildViewModel(request);
        model.put("SERVER", buildModelServer());
        return model;
    }

    private Object buildModelServer() {
        Map<String, Object> model = new HashMap<String, Object>();
        Runtime rt = Runtime.getRuntime();
        model.put("cpu_processors", rt.availableProcessors());
        long memMax = rt.maxMemory();
        long memUsed = rt.totalMemory() - rt.freeMemory();
        long memFree = memMax - memUsed;
        model.put("memory_used", memUsed);
        model.put("memory_free", memFree);
        model.put("memory_used_percent", memUsed * 100 / memMax);
        model.put("memory_free_percent", memFree * 100 / memMax);
        model.put("memory_available", memMax);

        String os = System.getProperty("os.name") + " - " + System.getProperty("os.arch") + " - "
                + System.getProperty("os.version");
        model.put("os", os);

        String java = System.getProperty("java.vendor") + " - "
                + System.getProperty("java.version");
        String javaSpec = System.getProperty("java.specification.name") + " - "
                + System.getProperty("java.specification.vendor") + " - "
                + System.getProperty("java.specification.version");
        String javaVm = System.getProperty("java.vm.name") + " - "
                + System.getProperty("java.vm.vendor") + " - "
                + System.getProperty("java.vm.version");
        String javaVmSpec = System.getProperty("java.vm.specification.name") + " - "
                + System.getProperty("java.vm.specification.vendor") + " - "
                + System.getProperty("java.vm.specification.version");

        model.put("java", java);
        model.put("java_spec", javaSpec);
        model.put("java_vm", javaVm);
        model.put("java_vm_spec", javaVmSpec);

        return model;
    }
}
