package ddth.dasp.framework.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import ddth.dasp.framework.resource.AbstractResourceLoader;

/**
 * This class supports loading resources within a bundle.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundleResourceLoader extends AbstractResourceLoader implements BundleContextAware,
        ApplicationContextAware {

    private Bundle bundle;

    protected Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified(String path) {
        return bundle.getLastModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resourceExists(String path) {
        return bundle.getEntry(path) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream loadResource(String path) throws IOException {
        URL url = bundle.getEntry(path);
        return url.openStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getEntryPaths(String rootPath) {
        List<String> result = new ArrayList<String>();
        Enumeration<String> paths = bundle.getEntryPaths(rootPath);
        if (paths == null) {
            return null;
        }
        while (paths.hasMoreElements()) {
            String path = paths.nextElement();
            result.add(path);
        }
        return result.toArray(new String[0]);
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        if (bundle == null) {
            setBundle(bundleContext.getBundle());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        if (bundle == null) {
            OsgiBundleXmlApplicationContext osgiAc = (OsgiBundleXmlApplicationContext) ac;
            setBundle(osgiAc.getBundle());
        }
    }
}
