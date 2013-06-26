package ddth.dasp.framework.logging.fs;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String format(LogRecord record) {
		String msg = record.getMessage();
		return msg != null ? msg + LINE_SEPARATOR : LINE_SEPARATOR;
	}
}
