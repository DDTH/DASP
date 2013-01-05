package ddth.dasp.status.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.framework.springmvc.BaseAnnotationController;
import ddth.dasp.status.DaspBundleConstants;

public class BaseController extends BaseAnnotationController {
    @ModelAttribute(value = "urlServerStatus")
    public String modelUrlServerStatus() {
        return DaspGlobal.getServletContext().getContextPath() + "/"
                + DaspBundleConstants.MODULE_NAME + "/server";
    }

    @ModelAttribute(value = "urlOsgiStatus")
    public String modelUrlOsgiStatus() {
        return DaspGlobal.getServletContext().getContextPath() + "/"
                + DaspBundleConstants.MODULE_NAME + "/osgi";
    }

    @ModelAttribute(value = "urlCacheStatus")
    public String modelUrlCacheStatus() {
        return DaspGlobal.getServletContext().getContextPath() + "/"
                + DaspBundleConstants.MODULE_NAME + "/cache";
    }

    @ModelAttribute(value = "urlJdbcStatus")
    public String modelUrlJdbcStatus() {
        return DaspGlobal.getServletContext().getContextPath() + "/"
                + DaspBundleConstants.MODULE_NAME + "/jdbc";
    }
}
