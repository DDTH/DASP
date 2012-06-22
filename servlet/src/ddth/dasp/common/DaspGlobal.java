package ddth.dasp.common;

import java.util.Timer;

import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.servlet.osgi.IOsgiBootstrap;

public class DaspGlobal {
	private static IOsgiBootstrap osgiBootstrap;
	private static TempDir contextTempDir;
	private static Timer contextTimer = new Timer(DaspGlobal.class.getName(),
			true);

	protected void setOsgiBootstrap(IOsgiBootstrap osgiBootstrap) {
		if (DaspGlobal.osgiBootstrap == null) {
			DaspGlobal.osgiBootstrap = osgiBootstrap;
		}
	}

	protected void setContextTempDir(TempDir contextTempDir) {
		if (DaspGlobal.contextTempDir == null) {
			DaspGlobal.contextTempDir = contextTempDir;
		}
	}

	public static Timer getContextTimer() {
		return contextTimer;
	}

	/**
	 * Gets the initialized instance of {@link IOsgiBootstrap}.
	 * 
	 * @return
	 */
	public static IOsgiBootstrap getOsgiBootstrap() {
		return osgiBootstrap;
	}

	/**
	 * Gets the context's {@link TempDir} instance.
	 * 
	 * @return
	 */
	public static TempDir getContextTempDir() {
		return contextTempDir;
	}
}
