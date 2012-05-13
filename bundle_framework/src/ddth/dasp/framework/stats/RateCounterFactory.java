package ddth.dasp.framework.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Factory to create {@link RateCounter} instances.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class RateCounterFactory {

	/*
	 * Number of slots should be power of 2 (e.g. 2, 4, 8, 16, etc)
	 */
	private int defaultNumSlots;

	/*
	 * Resolution is best to be power of 2 (e.g. 2, 4, 8, 16, etc)
	 */
	private int defaultSlotResolution;

	private String name;

	private Map<String, RateCounter> cacheCounters = new HashMap<String, RateCounter>();

	private Timer timer;

	/**
	 * Initializing method.
	 */
	public void init() {
		timer = new Timer(name != null ? name : this.getClass().getName(), true);
	}

	/**
	 * Destruction method.
	 */
	public void destroy() {
		timer.cancel();
	}

	/**
	 * Getter for defaultNumSlots
	 * 
	 * @return defaultNumSlots int
	 */
	public int getDefaultNumSlots() {
		return defaultNumSlots;
	}

	/**
	 * Setter for defaultNumSlots
	 * 
	 * @param defaultNumSlots
	 *            int
	 */
	public void setDefaultNumSlots(int defaultNumSlots) {
		this.defaultNumSlots = defaultNumSlots;
	}

	/**
	 * Getter for defaultSlotResolution
	 * 
	 * @return defaultSlotResolution int
	 */
	public int getDefaultSlotResolution() {
		return defaultSlotResolution;
	}

	/**
	 * Setter for defaultSlotResolution
	 * 
	 * @param defaultSlotResolution
	 *            int
	 */
	public void setDefaultSlotResolution(int defaultSlotResolution) {
		this.defaultSlotResolution = defaultSlotResolution;
	}

	/**
	 * Getter for name
	 * 
	 * @return name String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * 
	 * @param name
	 *            String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets a counter by name.
	 * 
	 * @param name
	 *            String
	 * @return RateCounter
	 */
	public RateCounter getCounter(String name) {
		synchronized (cacheCounters) {
			RateCounter result = cacheCounters.get(name);
			if (result == null) {
				result = new RateCounter();
				result.setTimer(timer);
				result.setName(this.name + "-" + name);
				result.setNumSlots(defaultNumSlots);
				result.setSlotResolution(getDefaultSlotResolution());
				result.init();
				cacheCounters.put(name, result);
			}
			return result;
		}
	}

	/**
	 * Gets all current counters.
	 * 
	 * @return RateCounter[]
	 */
	public RateCounter[] getAllCounters() {
		synchronized (cacheCounters) {
			return cacheCounters.values().toArray(new RateCounter[0]);
		}
	}
}
