package ddth.dasp.common.config.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ddth.dasp.common.config.IConfigDao;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.common.utils.RegExpUtils;

/**
 * File-based {@link IConfigDao}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JsonFileBasedConfigDao extends AbstractConfigDao {

    public final static String OSGI_PROP_STORAGE = "osgi.dasp.config.dao.file.storage";

    private Logger LOGGER = LoggerFactory.getLogger(JsonFileBasedConfigDao.class);
    private File _storageDir;
    private Cache<String, Object> cache;

    /**
     * {@inheritDoc}
     */
    public void destroy(BundleContext bundleContext) {
        if (cache != null) {
            cache.cleanUp();
            cache = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init(BundleContext bundleContext) {
        String storageDir = bundleContext.getProperty(OSGI_PROP_STORAGE);
        if (LOGGER.isDebugEnabled()) {
            String msg = "Storage directory configuration: " + storageDir;
            LOGGER.debug(msg);
        }
        if (StringUtils.isBlank(storageDir)) {
            throw new RuntimeException("Storage directory is not set!");
        }
        Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher m = PATTERN.matcher(storageDir);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String replacement = System.getProperty(m.group(1), "");
            m.appendReplacement(sb, RegExpUtils.regexpReplacementEscape(replacement));
        }
        m.appendTail(sb);
        _storageDir = new File(sb.toString());
        if (LOGGER.isDebugEnabled()) {
            String msg = "Storage directory: " + _storageDir.getAbsolutePath();
            LOGGER.debug(msg);
        }
        if (!_storageDir.isDirectory()) {
            _storageDir.mkdirs();
        }
        if (!_storageDir.isDirectory()) {
            throw new RuntimeException("[" + _storageDir.getAbsolutePath()
                    + "] is not a directory or not writable!");
        }

        int numProcessores = Runtime.getRuntime().availableProcessors();
        CacheBuilder<Object, Object> cacheBuider = CacheBuilder.newBuilder();
        cacheBuider.concurrencyLevel(Math.max(numProcessores / 2, 2));
        long capacity = 100;
        long expireAfterAccess = 3600;
        long expireAfterWrite = 3600;
        if (expireAfterAccess > 0) {
            cacheBuider.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        } else if (expireAfterWrite > 0) {
            cacheBuider.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        }
        cacheBuider.maximumSize(capacity);
        cache = cacheBuider.build();
    }

    protected boolean validateModule(String module) {
        return !StringUtils.isBlank(module) && StringUtils.isAlphanumeric(module);
    }

    protected File moduleNameToFile(String module) {
        return new File(_storageDir, module.toLowerCase() + ".json");
    }

    @SuppressWarnings("unchecked")
    synchronized protected Map<String, Object> loadModuleConfig(String module) {
        if (!validateModule(module)) {
            throw new RuntimeException("Module [" + module + "] is invalid!");
        }
        File file = moduleNameToFile(module);
        try {
            String fileContent = FileUtils.readFileToString(file, "UTF-8");
            try {
                return JsonUtils.fromJson(fileContent, Map.class);
            } catch (Exception e) {
                return new HashMap<String, Object>();
            }
        } catch (IOException e) {
            return new HashMap<String, Object>();
        }
    }

    synchronized protected void storeModuleConfig(String module, Map<String, Object> moduleConfig) {
        if (!validateModule(module)) {
            throw new RuntimeException("Module [" + module + "] is invalid!");
        }
        if (moduleConfig != null) {
            File file = moduleNameToFile(module);
            try {
                String fileContent = JsonUtils.toJson(moduleConfig);
                FileUtils.write(file, fileContent, "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfig(String module, String key, Object value) {
        Map<String, Object> moduleConfig = loadModuleConfig(module);
        moduleConfig.put(key, value);
        storeModuleConfig(module, moduleConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getConfig(String module, String key) {
        Map<String, Object> moduleConfig = loadModuleConfig(module);
        return moduleConfig.get(key);
    }
}
