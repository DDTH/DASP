package ddth.dasp.framework.logging.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.framework.logging.AppLogEntry;
import ddth.dasp.framework.logging.IAppLogEngine;

/**
 * A convenient asynchronous task to log a {@link AppLogEntry}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class AppLogTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogTask.class);

    private AppLogEntry logEntry;
    private IAppLogEngine logEngine;

    public AppLogTask(AppLogEntry logEntry, IAppLogEngine logEngine) {
        this.logEngine = logEngine;
        this.logEntry = logEntry;
    }

    @Override
    public void run() {
        try {
            logEngine.log(logEntry);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }
}
