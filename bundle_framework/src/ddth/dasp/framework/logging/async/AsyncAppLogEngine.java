package ddth.dasp.framework.logging.async;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.framework.logging.AppLogEntry;
import ddth.dasp.framework.logging.IAppLogEngine;
import ddth.dasp.framework.logging.base.AbstractCounterLogEngine;

public class AsyncAppLogEngine extends AbstractCounterLogEngine implements IAppLogEngine {

    private Logger LOGGER = LoggerFactory.getLogger(AsyncAppLogEngine.class);
    private IAppLogEngine[] engines;

    public void setEngines(IAppLogEngine[] engines) {
        this.engines = engines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(AppLogEntry entry) {
        incCounter();
        WriteLogTask task = new WriteLogTask(entry);
        scheduleTask(task);
    }

    private class WriteLogTask extends TimerTask {

        private AppLogEntry logEntry;

        public WriteLogTask(AppLogEntry logEntry) {
            this.logEntry = logEntry;
        }

        @Override
        public void run() {
            if (engines != null) {
                for (IAppLogEngine engine : engines) {
                    try {
                        engine.log(logEntry);
                    } catch (Exception e) {
                        LOGGER.error("Error while writing log entry " + logEntry
                                + " using engine [" + engine + "]!");
                    }
                }
            }
        }
    }
}
