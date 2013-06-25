package ddth.dasp.servlet.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtils {
	/**
	 * Gets a free port to listen.
	 * 
	 * @param ports
	 * @return
	 */
	public static Integer getFreePort(int[] ports) {
		for (int port : ports) {
			try {
				ServerSocket ss = new ServerSocket(port);
				ss.close();
				return port;
			} catch (IOException e) {
				continue;
			}
		}

		// if we reach here, it means no free port available
		return null;
	}
}
