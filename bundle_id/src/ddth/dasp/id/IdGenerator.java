package ddth.dasp.id;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import ddth.dasp.framework.stats.RateCounter;

public class IdGenerator {

	private static Map<Long, IdGenerator> cache = new HashMap<Long, IdGenerator>();
	private static long macAddr = 0;
	private static Timer timer = new Timer(IdGenerator.class.getName());

	public static long getMacAddr() {
		if (macAddr == 0) {
			try {
				InetAddress ip = InetAddress.getLocalHost();
				NetworkInterface network = NetworkInterface
						.getByInetAddress(ip);
				byte[] mac = network.getHardwareAddress();
				for (byte temp : mac) {
					macAddr = (macAddr << 8) | ((int) temp & 0xFF);
				}
			} catch (Exception e) {
				macAddr = System.currentTimeMillis();
			}
		}
		return macAddr;
	}

	/**
	 * Gets an {@link IdGenerator} instance for a node.
	 * 
	 * @param nodeId
	 * @return
	 */
	public static IdGenerator getInstance(long nodeId) {
		IdGenerator idGen = null;
		synchronized (cache) {
			idGen = cache.get(nodeId);
			if (idGen == null) {
				idGen = new IdGenerator(nodeId);
				idGen.init();
				cache.put(nodeId, idGen);
			}
		}
		return idGen;
	}

	/**
	 * Disposes an unused {@link IdGenerator}.
	 * 
	 * @param idGen
	 */
	public static void disposeInstance(IdGenerator idGen) {
		if (idGen != null) {
			synchronized (cache) {
				idGen.destroy();
				long nodeId = idGen.nodeId;
				IdGenerator temp = cache.get(nodeId);
				if (temp != null) {
					if (temp != idGen) {
						temp.destroy();
					}
					cache.remove(nodeId);
				}
			}
		}
	}

	private final static long MASK_TIMESTAMP_64 = 0x1FFFFFFFFFFL; // 41 bits
	private final static long MASK_NODE_ID_64 = 0x3FFL; // 10 bits
	private final static long MASK_SEQUENCE_64 = 0x1FFFL; // 13 bits
	private final static long SHIFT_TIMESTAMP_64 = 23L;
	private final static long SHIFT_NODE_ID_64 = 13L;
	private final static long TIMESTAMP_EPOCH = 1330534800000L; // 1-Mar-2012

	private final static long MASK_NODE_ID_128 = 0xFFFFFFFFFFFFL; // 48 bits
	private final static long MASK_SEQUENCE_128 = 0xFFFF; // 16 bits
	private final static long SHIFT_TIMESTAMP_128 = 64L;
	private final static long SHIFT_NODE_ID_128 = 16L;

	private long nodeId;
	private long template64;
	private BigInteger template128;
	private RateCounter rateCounter;

	/**
	 * Constructs a new {@link IdGenerator} instance.
	 * 
	 * @param nodeId
	 *            long
	 */
	protected IdGenerator(long nodeId) {
		this.nodeId = nodeId;
	}

	protected void init() {
		this.template64 = (this.nodeId & MASK_NODE_ID_64) << SHIFT_NODE_ID_64;
		this.template128 = BigInteger
				.valueOf((this.nodeId & MASK_NODE_ID_128) << SHIFT_NODE_ID_128);
		this.rateCounter = new RateCounter();
		this.rateCounter.setName(this.getClass().getName());
		this.rateCounter.setNumSlots(1000);
		this.rateCounter.setSlotResolution(2048);
		this.rateCounter.setTimer(timer);
		this.rateCounter.init();
	}

	protected void destroy() {
		this.rateCounter.destroy();
	}

	/**
	 * Generates a 64-bit id
	 * 
	 * @return
	 */
	public long generateId64() {
		long timestamp = (System.currentTimeMillis() - TIMESTAMP_EPOCH)
				& MASK_TIMESTAMP_64;
		long sequence = rateCounter.incCounter() & MASK_SEQUENCE_64;
		long result = timestamp << SHIFT_TIMESTAMP_64 | template64 | sequence;
		return result;
	}

	/**
	 * Generates a 128-bit id
	 * 
	 * @return
	 */
	public BigInteger generateId128() {
		BigInteger sequence = BigInteger.valueOf(rateCounter.incCounter()
				& MASK_SEQUENCE_128);
		BigInteger result = BigInteger.valueOf(System.currentTimeMillis());
		// System.out.println(sequence.toString(2));
		// System.out.println(result.toString(2));
		result = result.shiftLeft((int) SHIFT_TIMESTAMP_128);
		// System.out.println(result.toString(2));
		result = result.or(template128).or(sequence);
		// System.out.println(template128.toString(2));
		// System.out.println(result.toString(2));
		return result;
	}

	public static void main(String... args) throws InterruptedException {
		IdGenerator idGen = IdGenerator.getInstance(getMacAddr());
		int COUNT = 100000;
		long[] TEST_DATA = new long[COUNT];
		long id;
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			id = idGen.generateId64();
			TEST_DATA[i] = id;
		}
		long time2 = System.currentTimeMillis();
		System.out.println(time2 - time1);

		for (int i = 1; i < COUNT; i++) {
			if (TEST_DATA[i] == TEST_DATA[i - 1]) {
				System.out.println("Error: DATA[" + i + "] vs DATA[" + (i - 1)
						+ "]: " + TEST_DATA[i]);
			}
		}

		// Calendar cal = Calendar.getInstance();
		// cal.set(Calendar.MILLISECOND, 0);
		// cal.set(Calendar.SECOND, 0);
		// cal.set(Calendar.MINUTE, 0);
		// cal.set(Calendar.HOUR_OF_DAY, 0);
		// cal.set(Calendar.DAY_OF_MONTH, 29);
		// cal.set(Calendar.MONTH, Calendar.APRIL);
		// cal.set(Calendar.YEAR, 2012);
		// System.out.println(cal.getTimeInMillis());
		//
		// System.out.println(new Date(1330534800000L));
		//
		// InetAddress ip;
		// try {
		//
		// ip = InetAddress.getLocalHost();
		// System.out.println("Current IP address : " + ip.getHostAddress());
		//
		// NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		//
		// byte[] mac = network.getHardwareAddress();
		//
		// System.out.print("Current MAC address : ");
		//
		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < mac.length; i++) {
		// sb.append(String.format("%02X%s", mac[i],
		// (i < mac.length - 1) ? "-" : ""));
		// }
		// System.out.println(sb.toString());
		//
		// } catch (UnknownHostException e) {
		//
		// e.printStackTrace();
		//
		// } catch (SocketException e) {
		//
		// e.printStackTrace();
		//
		// }
	}
}
