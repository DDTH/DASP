package ddth.dasp.servlet.rp;

import java.io.UnsupportedEncodingException;

/**
 * This interface provides API to parse incoming requests into business tokens.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IRequestParser {

	/**
	 * Default timeout = 10000 ms
	 */
	public final static int DEFAULT_TIMEOUT = 10000;

	/**
	 * Default max content length ~128kb
	 */
	public final static int DEFAULT_MAX_CONTENT_LENGTH = 128000;

	/**
	 * Gets the raw request content as a binary array.
	 * 
	 * @return byte[]
	 */
	public byte[] getRawRequestContent();

	/**
	 * Gets the request content as a string (UTF-8 encoding).
	 * 
	 * @return String
	 */
	public String getRequestContent();

	/**
	 * Gets the request content as a string in the specified character set.
	 * 
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getRequestContent(String charset)
			throws UnsupportedEncodingException;

	/**
	 * Interrupts the parsing process.
	 */
	public void interrupt();

	/**
	 * Checks if the parsing process has been interrupted.
	 * 
	 * @return boolean
	 */
	public boolean isInterrupted();

	/**
	 * Checks if the parsing process has stopped because the request is
	 * malformed.
	 * 
	 * @return boolean
	 */
	public boolean isMalformed();

	/**
	 * Checks if the parsing process has been done.
	 * 
	 * @return boolean
	 */
	public boolean isParsed();

	/**
	 * Resets the request parser. This method must be called before the parsing
	 * takes place.
	 */
	public void reset();

	/**
	 * Parses the request.
	 * 
	 * @throws RequestParsingInteruptedException
	 * @throws MalformedRequestException
	 */
	public void parseRequest() throws RequestParsingInteruptedException,
			MalformedRequestException;
}
