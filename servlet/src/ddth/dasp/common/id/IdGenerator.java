package ddth.dasp.common.id;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static Map<Long, IdGenerator> cache = new HashMap<Long, IdGenerator>();
    private static long macAddr = 0;

    public static long getMacAddr() {
        if (macAddr == 0) {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
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
                // idGen.destroy();
                long nodeId = idGen.nodeId;
                IdGenerator temp = cache.get(nodeId);
                if (temp != null) {
                    // if (temp != idGen) {
                    temp.destroy();
                    // }
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
    private AtomicLong sequence = new AtomicLong();
    private AtomicLong lastTimestamp = new AtomicLong();

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
    }

    protected void destroy() {
        // EMPTY
    }

    /**
     * Generates a 64-bit id
     * 
     * @return
     */
    synchronized public long generateId64() {
        long timestamp = System.currentTimeMillis();
        long sequence = 0;
        if (timestamp == this.lastTimestamp.get()) {
            // increase sequence
            sequence = this.sequence.incrementAndGet();
        } else {
            // reset sequence
            this.sequence.set(sequence);
            this.lastTimestamp.set(timestamp);
        }
        timestamp = (timestamp - TIMESTAMP_EPOCH) & MASK_TIMESTAMP_64;
        long result = timestamp << SHIFT_TIMESTAMP_64 | template64 | (sequence & MASK_SEQUENCE_64);
        return result;
    }

    /**
     * Generates a 128-bit id
     * 
     * @return
     */
    synchronized public BigInteger generateId128() {
        long timestamp = System.currentTimeMillis();
        long sequence = 0;
        if (timestamp == this.lastTimestamp.get()) {
            // increase sequence
            sequence = this.sequence.incrementAndGet();
        } else {
            // reset sequence
            this.sequence.set(sequence);
            this.lastTimestamp.set(timestamp);
        }

        BigInteger biSequence = BigInteger.valueOf(sequence & MASK_SEQUENCE_128);
        BigInteger biResult = BigInteger.valueOf(timestamp);
        biResult = biResult.shiftLeft((int) SHIFT_TIMESTAMP_128);
        biResult = biResult.or(template128).or(biSequence);
        return biResult;
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
                System.out.println("Error: DATA[" + i + "] vs DATA[" + (i - 1) + "]: "
                        + TEST_DATA[i]);
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
