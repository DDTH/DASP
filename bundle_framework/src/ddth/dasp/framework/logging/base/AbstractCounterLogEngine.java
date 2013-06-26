package ddth.dasp.framework.logging.base;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

import ddth.dasp.common.utils.OsgiUtils;
import ddth.dasp.framework.stats.RateCounter;
import ddth.dasp.framework.stats.RateCounterFactory;

public class AbstractCounterLogEngine implements BundleContextAware {

	private final static String SERVICE_RATE_COUNTER_FACTORY = RateCounterFactory.class
			.getName();

	private BundleContext bundleContext;
	// private Logger LOGGER = LoggerFactory.getLogger(AbstractLogEngine.class);
	private Timer timer = new Timer(this.getClass().getName(), true);
	private String counterName = getClass().getName();

	/**
	 * Initializing method.
	 */
	public void init() {
		// EMPTY
	}

	/**
	 * Destroy method.
	 */
	public void destroy() {
		timer.cancel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * Gets the {@link BundleContext} instance.
	 * 
	 * @return BundleContext
	 */
	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Increases counter.
	 */
	protected void incCounter() {
		if (bundleContext != null) {
			ServiceReference sref = OsgiUtils.getServiceReference(
					bundleContext, SERVICE_RATE_COUNTER_FACTORY);
			if (sref != null) {
				try {
					RateCounterFactory rateCounterFactory = OsgiUtils
							.getService(bundleContext, sref,
									RateCounterFactory.class);
					if (rateCounterFactory != null) {
						RateCounter rateCounter = rateCounterFactory
								.getCounter(counterName);
						if (rateCounter != null) {
							rateCounter.incCounter();
						}
					}
				} finally {
					OsgiUtils.ungetServiceReference(bundleContext, sref);
				}
			}
		}
	}

	/**
	 * Schedules a task to run immediately.
	 * 
	 * @param task
	 *            TimerTask
	 */
	protected void scheduleTask(TimerTask task) {
		timer.schedule(task, 0);
	}
}
