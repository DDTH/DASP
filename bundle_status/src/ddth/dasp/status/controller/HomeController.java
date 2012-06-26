package ddth.dasp.status.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.OsgiUtils;
import ddth.dasp.status.DaspBundleConstants;

public class HomeController implements Controller {

    private final static String VIEW_NAME = DaspBundleConstants.MODULE_NAME + ":home";

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> model = buildModel();
        ModelAndView mav = new ModelAndView(VIEW_NAME, model);
        return mav;
    }

    private Map<String, Object> buildModel() {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("SERVER", buildModelServer());
        model.put("OSGI", buildModelOsgi());

        return model;
    }

    private Object buildModelOsgi() {
        List<Object> model = new ArrayList<Object>();

        IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
        BundleContext bc = osgiBootstrap.getBundleContext();
        Bundle[] bundles = bc.getBundles();

        for (Bundle bundle : bundles) {
            Map<String, Object> bundleModel = new HashMap<String, Object>();
            model.add(bundleModel);
            bundleModel.put("id", bundle.getBundleId());
            bundleModel.put("name", bundle.getSymbolicName() != null ? bundle.getSymbolicName()
                    : "null");
            bundleModel.put("version", bundle.getVersion());
            bundleModel.put("state", OsgiUtils.getBundleStateAsString(bundle));
        }

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
