package ddth.dasp.framework.mls;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import ddth.dasp.common.mls.PropsBasedLanguage;

/**
 * 
 * This language pack loads language elements from within the bundle.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundlePropsBasedLanguage extends PropsBasedLanguage implements BundleContextAware {

    private String languageLocation;
    private Bundle bundle;

    public BundlePropsBasedLanguage() {
    }

    public BundlePropsBasedLanguage(Locale locale, String name) {
        super(locale, name);
    }

    public String getLanguageLocation() {
        return languageLocation;
    }

    public void setLanguageLocation(String languageDir) {
        this.languageLocation = languageDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        Enumeration<String> entryPaths = bundle.getEntryPaths(languageLocation);
        while (entryPaths.hasMoreElements()) {
            String entryPath = entryPaths.nextElement();
            if (entryPath.endsWith(".properties") || entryPath.endsWith(".xml")) {
                Properties props = new Properties();
                InputStream is = bundle.getResource(entryPath).openStream();
                try {
                    if (entryPath.endsWith(".properties")) {
                        props.load(is);
                    } else {
                        props.loadFromXML(is);
                    }
                    addLanguage(props);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }
    }

    public void destroy() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBundleContext(BundleContext bundleContext) {
        bundle = bundleContext.getBundle();
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected Bundle getBundle() {
        return bundle;
    }
}
