package ddth.dasp.servlet.rp;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.utils.TimerUtils;

/**
 * Abstract implementation of {@link IRequestParser}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class AbstractRequestParser implements IRequestParser {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(AbstractRequestParser.class);

	private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
	private int timeout = DEFAULT_TIMEOUT;
	private String requestContent;
	private boolean isParsed = false;
	private boolean isInterrupted = false;
	private boolean isMalformed = false;
	private boolean isDirty = true;
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream(4096);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.isParsed = false;
		this.requestContent = null;
		this.isInterrupted = false;
		this.isMalformed = false;
		this.isDirty = true;
		this.requestContent = null;
		this.buffer = new ByteArrayOutputStream(8192);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxContentLength() {
		return this.maxContentLength;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength < 1 ? DEFAULT_MAX_CONTENT_LENGTH
				: maxContentLength;
		if (this.maxContentLength < 1) {
			this.maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout < 1 ? DEFAULT_TIMEOUT : timeout;
		if (this.timeout < 1) {
			this.timeout = DEFAULT_TIMEOUT;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Note: This method returns the data from its internal buffer.
	 */
	@Override
	public byte[] getRawRequestContent() {
		return this.buffer.size() == 0 ? null : this.buffer.toByteArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestContent() {
		if (this.requestContent == null || this.isDirty) {
			try {
				this.requestContent = this.buffer.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				this.requestContent = null;
			}
			this.isDirty = false;
		}
		return this.requestContent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public String getRequestContent(String charset)
			throws UnsupportedEncodingException {
		return this.buffer.toString(charset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInterrupted() {
		return this.isInterrupted;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Note: this method sets its internal "is interrupted" flag to
	 * <code>true</code>.
	 */
	@Override
	public void interrupt() {
		this.isInterrupted = true;
		String logMessage = "Request parsing timed out [" + getTimeout() + "]!";
		LOGGER.warn(logMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMalformed() {
		return this.isMalformed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParsed() {
		return this.isParsed;
	}

	/**
	 * Marks the request as malformed.
	 */
	protected void markMalformedRequest() {
		this.isMalformed = true;
	}

	/**
	 * Writes data to the internal buffer.
	 * 
	 * @param data
	 *            byte[]
	 * @throws MalformedRequestException
	 *             thrown when the buffer is overload
	 */
	protected void write(byte[] data) throws MalformedRequestException {
		if (data != null) {
			write(data, 0, data.length);
		}
	}

	/**
	 * Writes data to the internal buffer.
	 * 
	 * @param data
	 *            byte[]
	 * @param offset
	 *            int
	 * @param length
	 *            int
	 * @throws MalformedRequestException
	 *             thrown when the buffer is overload
	 */
	protected void write(byte[] data, int offset, int length)
			throws MalformedRequestException {
		if (data != null) {
			this.isDirty = true;
			this.buffer.write(data, offset, length);
			if (this.buffer.size() > getMaxContentLength()) {
				String logMessage = "Request input exceeds maximum length limit ["
						+ getMaxContentLength() + "]!";
				LOGGER.error(logMessage);
				markMalformedRequest();
				throw new MalformedRequestException();
			}
		}
	}

	/**
	 * Writes data to the internal buffer.
	 * 
	 * @param data
	 *            String
	 * @throws MalformedRequestException
	 *             thrown when the buffer is overload
	 */
	protected void write(String data) throws MalformedRequestException {
		if (data != null) {
			try {
				write(data.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// IGNORE! It should never happen!
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Note: this method creates a "safeguard" thread and calls
	 * {@link #internalParseRequest()}. The safeguard thread monitors and
	 * notifies the parser if timeout has occurred.
	 */
	@Override
	public void parseRequest() throws RequestParsingInteruptedException,
			MalformedRequestException {
		if (this.isParsed) {
			return;
		}
		this.isParsed = true;
		TimerTask task = new SafeguardTask(this);
		TimerUtils.getTimer().schedule(task, getTimeout());
		try {
			internalPreParseRequest();
			internalParseRequest();
			internalPostParseRequest();
		} finally {
			((SafeguardTask) task).finish();
		}
	}

	static class SafeguardTask extends TimerTask {

		private boolean finish = false;
		private IRequestParser requestParser;

		public SafeguardTask(IRequestParser requestParser) {
			this.requestParser = requestParser;
		}

		public void finish() {
			this.finish = true;
		}

		@Override
		public void run() {
			if (!finish) {
				requestParser.interrupt();
			}
		}
	}

	/**
	 * This method is called before {@link #internalParseRequest()}. Sub-class
	 * may override it to perform pre-parsing work.
	 */
	protected void internalPreParseRequest() {
		// empty
	}

	/**
	 * Sub-class overrides this method to implement its own business.
	 * 
	 * Note: sub-class should check for {@link #isInterrupted()} flag and act
	 * accordingly.
	 * 
	 * @throws RequestParsingInteruptedException
	 * @throws MalformedRequestException
	 */
	protected abstract void internalParseRequest()
			throws RequestParsingInteruptedException, MalformedRequestException;

	/**
	 * This method is called after {@link #internalParseRequest()}. Sub-class
	 * may override it to perform post-parsing work.
	 */
	protected void internalPostParseRequest() {
		// empty
	}
}
