package ddth.dasp.framework.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Factory to create {@link SingleRateCounter} instances.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class SingleRateCounterFactory {

	/*
	 * Resolution is best to be power of 2 (e.g. 2, 4, 8, 16, etc)
	 */
	private int defaultSlotResolution;

	private String name;

	private Map<String, SingleRateCounter> cacheCounters = new HashMap<String, SingleRateCounter>();

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
	 * @return
	 */
	public SingleRateCounter getCounter(String name) {
		synchronized (cacheCounters) {
			SingleRateCounter result = cacheCounters.get(name);
			if (result == null) {
				result = new SingleRateCounter();
				result.setTimer(timer);
				result.setName(this.name + "-" + name);
				result.setSlotResolution(getDefaultSlotResolution());
				result.init();
				cacheCounters.put(name, result);
			}
			return result;
		}
	}

	/**
	 * Gets a counter by name and specifies a slot resolution
	 * 
	 * @param name
	 * @param resolution
	 * @return
	 */
	public SingleRateCounter getCounter(String name, int resolution) {
		synchronized (cacheCounters) {
			SingleRateCounter result = cacheCounters.get(name);
			if (result == null) {
				result = new SingleRateCounter();
				result.setTimer(timer);
				result.setName(this.name + "-" + name);
				result.setSlotResolution(resolution);
				result.init();
				cacheCounters.put(name, result);
			}
			return result;
		}
	}

	private final static SingleRateCounter[] EMPTY_ARRAY = new SingleRateCounter[0];

	/**
	 * Gets all current counters.
	 * 
	 * @return RateCounter[]
	 */
	public SingleRateCounter[] getAllCounters() {
		synchronized (cacheCounters) {
			return cacheCounters.values().toArray(EMPTY_ARRAY);
		}
	}
}
