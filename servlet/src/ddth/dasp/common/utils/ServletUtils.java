package ddth.dasp.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.utils.JsonUtils;

/**
 * Servlet related utilities.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ServletUtils {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ServletUtils.class);

	private static ServletContext servletContext;

	/**
	 * Sends a HTTP error response.
	 * 
	 * @param response
	 * @param errorCode
	 * @throws IOException
	 */
	public static void responseHttpError(HttpServletResponse response,
			int errorCode) throws IOException {
		responseHttpError(response, errorCode, null);
	}

	/**
	 * Sends a HTTP error response.
	 * 
	 * @param response
	 * @param errorCode
	 * @param message
	 * @throws IOException
	 */
	public static void responseHttpError(HttpServletResponse response,
			int errorCode, String message) throws IOException {
		if (message == null) {
			response.sendError(errorCode);
		} else {
			response.sendError(errorCode, message);
		}
	}

	/**
	 * Generates the error result.
	 * 
	 * @param errorCode
	 *            int
	 * @param errorMsg
	 *            String
	 * @return String the error result as a Json string
	 */
	public static String reportError(int errorCode, String errorMsg) {
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put(0, errorCode);
		if (errorMsg != null) {
			result.put(1, errorMsg);
		}
		try {
			return JsonUtils.toJson(result);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "{\"0\":500,\"1\":\"" + e.getMessage() + "\"}";
		}
	}

	/**
	 * Called to create http response for error case.
	 * 
	 * @param errorCode
	 *            int
	 * @param errorMsg
	 *            String
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	public static void responseError(int errorCode, String errorMsg,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String result = reportError(errorCode, errorMsg);
		PrintWriter pw = response.getWriter();
		pw.print(result);
		pw.flush();
		pw.close();
	}

	public static void response(Object result, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		String resultStr = null;
		try {
			resultStr = JsonUtils.toJson(result);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			resultStr = "{\"0\":500,\"1\":\"" + e.getMessage() + "\"}";
		}
		pw.print(resultStr);
		pw.flush();
		pw.close();
	}

	/**
	 * Retrieves the ServletContext instance.
	 * 
	 * @return ServletContext
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * Stores the ServletContext instance for public access latter.
	 * 
	 * @param servletConext
	 *            ServletContext
	 */
	public static void setServletContext(ServletContext servletConext) {
		ServletUtils.servletContext = servletConext;
	}

	/**
	 * Alias of getContentServletPathURL().
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getHomeURL(HttpServletRequest request) {
		return getContextServletPathURL(request);
	}

	/**
	 * Alias of getContextServletPathURI().
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getHomeURI(HttpServletRequest request) {
		return getContextServletPathURI(request);
	}

	/**
	 * Retrieves a servlet context's attribute.
	 * 
	 * @param name
	 *            String
	 * @return Object
	 */
	public static Object getContextAttribute(String name) {
		return getContextAttribute(getServletContext(), name);
	}

	/**
	 * Retrieves a servlet context's attribute.
	 * 
	 * @param context
	 *            ServletContext
	 * @param name
	 *            String
	 * @return Object
	 */
	public static Object getContextAttribute(ServletContext context, String name) {
		return context.getAttribute(name);
	}

	/**
	 * Sets a servlet context's attribute.
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            Object
	 */
	public static void setContextAttribute(String name, Object value) {
		setContextAttribute(getServletContext(), name, value);
	}

	/**
	 * Sets a servlet context's attribute.
	 * 
	 * @param context
	 *            ServletContext
	 * @param name
	 *            String
	 * @param value
	 *            Object
	 */
	public static void setContextAttribute(ServletContext context, String name,
			Object value) {
		context.setAttribute(name, value);
	}

	/**
	 * Retrieves a servlet context's initial parameter.
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public static String getContextInitParam(String name) {
		return getContextInitParam(getServletContext(), name);
	}

	/**
	 * Retrieves a servlet context's initial parameter.
	 * 
	 * @param context
	 *            ServletContext
	 * @param name
	 *            String
	 * @return String
	 */
	public static String getContextInitParam(ServletContext context, String name) {
		return context.getInitParameter(name);
	}

	/**
	 * Gets the relative URI of the current context.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getContextPathURI(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath != null ? contextPath : "";
	}

	/**
	 * Gets the absolute URL of the current context.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getContextPathURL(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (uri == null) {
			uri = "";
		}
		String url = request.getRequestURL().toString();
		url = url.substring(0, url.length() - uri.length());
		return url + getContextPathURI(request);
	}

	/**
	 * Gets the relative URI of the current servlet path
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getServletPath(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return servletPath != null ? servletPath : "";
	}

	/**
	 * Gets the context path uri, appended by the servlet path.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	static public String getContextServletPathURI(HttpServletRequest request) {
		String contextServletPath = getContextPathURI(request)
				+ getServletPath(request);
		if (!contextServletPath.equals("/")) {
			contextServletPath = contextServletPath.replaceAll("/$", "");
		}
		return contextServletPath;
	}

	/**
	 * Gets the context path url, appended by the servlet path.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	static public String getContextServletPathURL(HttpServletRequest request) {
		String contextServletPath = getContextPathURL(request)
				+ getServletPath(request);
		if (!contextServletPath.equals("/")) {
			contextServletPath = contextServletPath.replaceAll("/$", "");
		}
		return contextServletPath;
	}

	/**
	 * Loads a resource specified by a path and returns it as a URL.
	 * 
	 * @param servlet
	 *            GenericServlet uses this servlet to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return URL
	 * @throws MalformedURLException
	 */
	static public URL loadResourceAsURL(GenericServlet servlet,
			String resourcePath) throws MalformedURLException {
		return loadResourceAsURL(servlet.getServletContext(), resourcePath);
	}

	/**
	 * Loads a resource specified by a path and returns it as a URL.
	 * 
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return URL
	 * @throws MalformedURLException
	 */
	static public URL loadResourceAsURL(String resourcePath)
			throws MalformedURLException {
		return loadResourceAsURL(servletContext, resourcePath);
	}

	/**
	 * Loads a resource specified by a path and returns it as a URL.
	 * 
	 * @param servletContext
	 *            ServletContext uses this servlet contextto load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return URL
	 * @throws MalformedURLException
	 */
	static public URL loadResourceAsURL(ServletContext servletContext,
			String resourcePath) throws MalformedURLException {
		return servletContext.getResource(resourcePath);
	}

	/**
	 * Loads a resource specified by a path and returns it as an InputStream.
	 * 
	 * @param servlet
	 *            GenericServlet uses this servlet to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return InputStream
	 */
	static public InputStream loadResourceAsStream(GenericServlet servlet,
			String resourcePath) {
		return loadResourceAsStream(servlet.getServletContext(), resourcePath);
	}

	/**
	 * Loads a resource specified by a path and returns it as an InputStream.
	 * 
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return InputStream
	 */
	static public InputStream loadResourceAsStream(String resourcePath) {
		return loadResourceAsStream(servletContext, resourcePath);
	}

	/**
	 * Loads a resource specified by a path and returns it as an InputStream.
	 * 
	 * @param servletContext
	 *            ServletContext uses this servlet context to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return InputStream
	 */
	static public InputStream loadResourceAsStream(
			ServletContext servletContext, String resourcePath) {
		return servletContext.getResourceAsStream(resourcePath);
	}

	/**
	 * Loads a resource specified by a path using default character set and
	 * returns it as a String.
	 * 
	 * @param servlet
	 *            GenericServlet uses this servlet to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(GenericServlet servlet,
			String resourcePath) throws IOException {
		return loadResourceAsString(servlet.getServletContext(), resourcePath);
	}

	/**
	 * Loads a resource specified by a path using default character set and
	 * returns it as a String.
	 * 
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(String resourcePath)
			throws IOException {
		return loadResourceAsString(servletContext, resourcePath);
	}

	/**
	 * Loads a resource specified by a path using default character set and
	 * returns it as a String.
	 * 
	 * @param servletContext
	 *            ServletContext uses this servlet context to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(ServletContext servletContext,
			String resourcePath) throws IOException {
		return loadResourceAsString(servletContext, resourcePath, null);
	}

	/**
	 * Loads a resource specified by a path using specified character set and
	 * returns it as a String.
	 * 
	 * @param servlet
	 *            GenericServlet uses this servlet to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @param charset
	 *            String specify the character set to read the resource
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(GenericServlet servlet,
			String resourcePath, String charset) throws IOException {
		return loadResourceAsString(servlet.getServletContext(), resourcePath,
				charset);
	}

	/**
	 * Loads a resource specified by a path using specified character set and
	 * returns it as a String.
	 * 
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @param charset
	 *            String specify the character set to read the resource
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(String resourcePath,
			String charset) throws IOException {
		return loadResourceAsString(servletContext, resourcePath, charset);
	}

	/**
	 * Loads a resource specified by a path using specified character set and
	 * returns it as a String.
	 * 
	 * @param servletContext
	 *            ServletContext uses this servlet context to load the resource
	 * @param resourcePath
	 *            String must begin with a "/" and is interpreted as relative to
	 *            the servlet's context root
	 * @param charset
	 *            String specify the character set to read the resource
	 * @return String
	 * @throws IOException
	 */
	static public String loadResourceAsString(ServletContext servletContext,
			String resourcePath, String charset) throws IOException {
		InputStream is = loadResourceAsStream(servletContext, resourcePath);
		if (is == null) {
			return null;
		}

		InputStreamReader isr = charset == null ? new InputStreamReader(is)
				: new InputStreamReader(is, charset);
		BufferedReader br = new BufferedReader(isr);
		try {
			StringBuffer sb = new StringBuffer();
			char[] buff = new char[1024];
			int charsRead = br.read(buff);
			while (charsRead > 0) {
				sb.append(buff, 0, charsRead);
				charsRead = br.read(buff);
			}
			return sb.toString();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			}
			try {
				if (isr != null)
					isr.close();
			} catch (Exception e) {
			}
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Decodes an encoded-URL using UTF-8 charset.
	 * 
	 * @param url
	 *            String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	static public String decodeURL(String url)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(url, "UTF-8");
	}

	/**
	 * Decodes an encoded-URL using a specified charset.
	 * 
	 * @param url
	 *            String
	 * @param charset
	 *            String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	static public String decodeURL(String url, String charset)
			throws UnsupportedEncodingException {
		if (url == null) {
			return "";
		}
		return charset != null ? URLDecoder.decode(url, charset) : URLDecoder
				.decode(url, "UTF-8");
	}

	/**
	 * Wraps a Throwable object inside a ServletException object
	 * 
	 * @param e
	 *            Throwable
	 * @return ServletException
	 */
	public static ServletException asServletException(Throwable e) {
		if (e instanceof ServletException) {
			return (ServletException) e;
		}
		return (ServletException) new ServletException().initCause(e);
	}

	/**
	 * Sets a session a attribute.
	 * 
	 * @param session
	 * @param name
	 * @param value
	 */
	public static void setSessionAttribute(HttpSession session, String name,
			Object value) {
		session.setAttribute(name, value);
	}
}
