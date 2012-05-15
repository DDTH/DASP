package ddth.dasp.servlet.utils;

import javax.servlet.http.HttpSession;

public class SessionUtils {
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
