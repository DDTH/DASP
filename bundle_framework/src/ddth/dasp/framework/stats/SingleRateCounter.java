package ddth.dasp.framework.stats;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Single-slot rate (number of things/amount of time) counter.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class SingleRateCounter {
	private Timer timer;
	private TimerTask task;
	/*
	 * Resolution is best to be power of 2 (e.g. 2, 4, 8, 16, etc)
	 */
	private int slotResolution;
	private long slotResolutionNanoseconds;
	private int resolutionShift;

	private String name;
	private volatile long lastAccessTimestamp = System.nanoTime();
	private volatile long counter = 0;

	/**
	 * Constructs a new {@link SingleRateCounter} instance.
	 */
	public SingleRateCounter() {
	}

	/**
	 * Constructs a new {@link SingleRateCounter} instance.
	 */
	public SingleRateCounter(Timer timer) {
		setTimer(timer);
	}

	/**
	 * Setter for {@link #timer}.
	 * 
	 * @param timer
	 *            Timer
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * Getter for {@link #timer}.
	 * 
	 * @return Timer
	 */
	protected Timer getTimer() {
		return timer;
	}

	public void destroy() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	/**
	 * Initialzing method.
	 */
	public void init() {
		if (this.slotResolution < 1) {
			this.slotResolution = 1;
		}
		this.resolutionShift = 20;
		this.slotResolutionNanoseconds = 1 << this.resolutionShift;
		long temp = this.slotResolution * 1000000L;
		while (this.slotResolutionNanoseconds < temp) {
			this.resolutionShift++;
			this.slotResolutionNanoseconds <<= 1;
		}

		if (timer != null) {
			long delay = slotResolution;
			task = new ResetCounterTask(this);
			timer.scheduleAtFixedRate(task, delay, delay);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets slot's resolution in nanoseconds.
	 * 
	 * @return long the resolution in nanoseconds
	 */
	public long getSlotResolutionNano() {
		return this.slotResolutionNanoseconds;
	}

	/**
	 * Gets slot's resolution.
	 * 
	 * @return long the resolution in milliseconds
	 */
	public int getSlotResolution() {
		return this.slotResolution;
	}

	/**
	 * Sets slot's resolution.
	 * 
	 * @param resolution
	 *            int slot's resolution in milliseconds
	 */
	public void setSlotResolution(int resolution) {
		this.slotResolution = resolution;
	}

	/**
	 * Gets the counter's last access timestamp.
	 * 
	 * @return long last access timestamp in nanoseconds
	 */
	public long getLastAccessTimestamp() {
		return this.lastAccessTimestamp;
	}

	/**
	 * Gets the counter value.
	 * 
	 * @return long
	 */
	public long getCounter() {
		return counter;
	}

	/**
	 * Increases counter by 1 and returns the post-inc value.
	 * 
	 * @return
	 */
	public long incCounter() {
		return incCounter(1);
	}

	/**
	 * Increases counter by a specific value and returns the post-inc value.
	 * 
	 * @param value
	 *            long
	 * @return
	 */
	public long incCounter(long value) {
		long oldValue = counter;
		counter += value;
		return oldValue;
	}

	static class ResetCounterTask extends TimerTask {
		private SingleRateCounter rateCounter;

		public ResetCounterTask(SingleRateCounter rateCounter) {
			this.rateCounter = rateCounter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			rateCounter.counter = 0;
		}
	}
}
