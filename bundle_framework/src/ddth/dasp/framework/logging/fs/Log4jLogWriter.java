package ddth.dasp.framework.logging.fs;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ddth.dasp.framework.logging.ILogWriter;

/**
 * This implementation of {@link ILogWriter} utilizes log4j to write log to file
 * system.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class Log4jLogWriter implements ILogWriter {

	private Logger logger;

	public Log4jLogWriter() {
	}

	public Log4jLogWriter(Logger logger, Appender appender) {
		setLogger(logger);
		setAppender(appender);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeLog(String logMsg) {
		logger.log(Level.ALL, logMsg);
	}

	/**
	 * Sets the logger's appender.
	 * 
	 * @param appender
	 *            Appender
	 */
	public void setAppender(Appender appender) {
		logger.addAppender(appender);
	}

	/**
	 * Getter for {@link #logger}.
	 * 
	 * @return Logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Setter for {@link #logger}.
	 * 
	 * @param logger
	 *            Logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
		this.logger.setLevel(Level.ALL);
	}
}
