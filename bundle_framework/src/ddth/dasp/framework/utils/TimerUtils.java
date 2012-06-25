package ddth.dasp.framework.utils;

import java.util.Timer;

public class TimerUtils {
	private final static Timer timer = new Timer(TimerUtils.class.getName(),
			true);

	public static Timer getTimer() {
		return timer;
	}
}
