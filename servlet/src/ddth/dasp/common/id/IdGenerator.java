package ddth.dasp.common.id;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private final static Map<Long, IdGenerator> cache = new HashMap<Long, IdGenerator>();
    private static long macAddr = 0;
    private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    /**
     * Max radix: 46
     */
    public final static int MAX_RADIX = digits.length;

    /* copy from OpenJDK source code, modified by Thanh Nguyen */
    /**
     * Returns the numeric value of the character <code>ch</code> in the
     * specified radix.
     * 
     * @param ch
     * @param radix
     * @return
     * @see #MAX_RADIX
     */
    public static int digit(char ch, int radix) {
        for (int i = 0; i < MAX_RADIX && i < radix; i++) {
            if (digits[i] == ch) {
                return i;
            }
        }
        return -1;
    }

    /* copy from OpenJDK source code, modified by Thanh Nguyen */
    /**
     * Parses a long value from a string.
     * 
     * @param s
     * @param radix
     * @return
     * @throws NumberFormatException
     * @see #MAX_RADIX
     */
    public static long parseLong(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        if (radix < Character.MIN_RADIX) {
            throw new NumberFormatException("radix " + radix + " less than " + Character.MIN_RADIX);
        }
        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix + " greater than " + MAX_RADIX);
        }
        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else {
                    throw new NumberFormatException(s);
                }
                if (len == 1) {// Cannot have lone "-"
                    throw new NumberFormatException(s);
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = digit(s.charAt(i++), radix);
                if (digit < 0) {
                    throw new NumberFormatException(s);
                }
                if (result < multmin) {
                    throw new NumberFormatException(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException(s);
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException(s);
        }
        return negative ? result : -result;
    }

    /* copy from OpenJDK source code, modified by Thanh Nguyen */
    /**
     * Converts a long to string.
     * 
     * @param l
     * @param radix
     * @return
     * @see #MAX_RADIX
     */
    public static String toString(long l, int radix) {
        if (radix < Character.MIN_RADIX || radix > MAX_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return Long.toString(l);
        }
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (l < 0);

        if (!negative) {
            l = -l;
        }

        while (l <= -radix) {
            buf[charPos--] = digits[(int) (-(l % radix))];
            l = l / radix;
        }
        buf[charPos] = digits[(int) (-l)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (65 - charPos));
    }

    /* copy from OpenJDK source code, modified by Thanh Nguyen */
    /**
     * Converts a long to unsigned string.
     * 
     * @param i
     * @param shift
     * @return
     * @see #MAX_RADIX
     */
    public static String toUnsignedString(long i, int shift) {
        char[] buf = new char[64];
        int charPos = 64;
        int radix = 1 << shift;
        long mask = radix - 1;
        do {
            buf[--charPos] = digits[(int) (i & mask)];
            i >>>= shift;
        } while (i != 0);
        return new String(buf, charPos, (64 - charPos));
    }

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

    private final static long MASK_TIMESTAMP_48 = 0xFFFFFFFFL; // 32 bits
    private final static long MASK_NODE_ID_48 = 0x7L; // 3 bits
    private final static long MASK_SEQUENCE_48 = 0x1FFFL; // 13 bits
    private final static long SHIFT_TIMESTAMP_48 = 16L;
    private final static long SHIFT_NODE_ID_48 = 13L;

    private final static long MASK_TIMESTAMP_64 = 0x1FFFFFFFFFFL; // 41 bits
    private final static long MASK_NODE_ID_64 = 0x3FFL; // 10 bits
    private final static long MASK_SEQUENCE_64 = 0x1FFFL; // 13 bits
    private final static long SHIFT_TIMESTAMP_64 = 23L;
    private final static long SHIFT_NODE_ID_64 = 13L;
    // private final static long TIMESTAMP_EPOCH = 1330534800000L; // 1-Mar-2012
    public final static long TIMESTAMP_EPOCH = 1362070800000L; // 1-Mar-2013

    private final static long MASK_NODE_ID_128 = 0xFFFFFFFFFFFFL; // 48 bits
    private final static long MASK_SEQUENCE_128 = 0xFFFF; // 16 bits
    private final static long SHIFT_TIMESTAMP_128 = 64L;
    private final static long SHIFT_NODE_ID_128 = 16L;

    private long nodeId;
    private long template48, template64;
    private BigInteger template128;
    private AtomicLong sequence = new AtomicLong();
    private AtomicLong lastTimestamp = new AtomicLong();

    protected IdGenerator(long nodeId) {
        this.nodeId = nodeId;
    }

    protected void init() {
        this.template64 = (this.nodeId & MASK_NODE_ID_64) << SHIFT_NODE_ID_64;
        this.template48 = (this.nodeId & MASK_NODE_ID_48) << SHIFT_NODE_ID_48;
        this.template128 = BigInteger
                .valueOf((this.nodeId & MASK_NODE_ID_128) << SHIFT_NODE_ID_128);
    }

    protected void destroy() {
        // EMPTY
    }

    /**
     * Generates a 48-bit id.
     * 
     * Format of 48-bit: <32-bit: timestamp><3-bit: node id><13 bit: sequence
     * number>
     * 
     * @return
     */
    synchronized public long generateId48() {
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
        timestamp = (timestamp - TIMESTAMP_EPOCH) & MASK_TIMESTAMP_48;
        long result = timestamp << SHIFT_TIMESTAMP_48 | template48 | (sequence & MASK_SEQUENCE_48);
        return result;
    }

    /**
     * Generate a 48-bit id as hex string.
     * 
     * @return
     */
    public String generateId48Hex() {
        long id = generateId48();
        return Long.toHexString(id);
    }

    /**
     * Generate a 48-bit id as ASCII string (radix 36).
     * 
     * @return
     */
    public String generateId48Ascii() {
        long id = generateId48();
        return Long.toString(id, Character.MAX_RADIX);
    }

    /**
     * Extracts the (UNIX) timestamp from a 64-bit id.
     * 
     * @param id64
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp64(long id64) {
        long timestamp = (id64 >> SHIFT_TIMESTAMP_64) + TIMESTAMP_EPOCH;
        return timestamp;
    }

    /**
     * Extracts the (UNIX) timestamp from a 64-bit hex id.
     * 
     * @param id64hex
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp64(String id64hex) {
        long id64 = parseLong(id64hex, 16);
        return extractTimestamp64(id64);
    }

    /**
     * Extracts the (UNIX) timestamp from a 64-bit ASCII id (radix 36).
     * 
     * @param id64ascii
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp64Ascii(String id64ascii) {
        long id64 = Long.parseLong(id64ascii, Character.MAX_RADIX);
        return extractTimestamp64(id64);
    }

    /**
     * Generates a 64-bit id.
     * 
     * Format of 64-bit: <41-bit: timestamp><10-bit: node id><13 bit: sequence
     * number>
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
     * Generate a 64-bit id as hex string.
     * 
     * @return
     */
    public String generateId64Hex() {
        long id = generateId64();
        return Long.toHexString(id);
    }

    /**
     * Generate a 64-bit id as ASCII string (radix 36).
     * 
     * @return
     */
    public String generateId64Ascii() {
        long id = generateId64();
        return Long.toString(id, Character.MAX_RADIX);
    }

    /**
     * Generates a 128-bit id.
     * 
     * Format of 128-bit: <64-bit: timestamp><48-bit: node id><16 bit: sequence
     * number>
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

    /**
     * Generate a 128-bit id as hex string.
     * 
     * @return
     */
    public String generateId128Hex() {
        BigInteger id = generateId128();
        return id.toString(16);
    }

    /**
     * Generate a 128-bit id as ASCII string (radix 36).
     * 
     * @return
     */
    public String generateId128Ascii() {
        BigInteger id = generateId128();
        return id.toString(Character.MAX_RADIX);
    }

    /**
     * Extracts the (UNIX) timestamp from a 128-bit id.
     * 
     * @param id128
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp128(BigInteger id128) {
        BigInteger result = id128.shiftRight((int) SHIFT_TIMESTAMP_128);
        return result.longValue();
    }

    /**
     * Extracts the (UNIX) timestamp from a 128-bit hex id.
     * 
     * @param id128hex
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp128(String id128hex) {
        BigInteger id128 = new BigInteger(id128hex, 16);
        return extractTimestamp128(id128);
    }

    /**
     * Extracts the (UNIX) timestamp from a 128-bit ASCII id (radix 36).
     * 
     * @param id128ascii
     * @return the UNIX timestamp (milliseconds)
     */
    public static long extractTimestamp128Ascii(String id128ascii) {
        BigInteger id128 = new BigInteger(id128ascii, Character.MAX_RADIX);
        return extractTimestamp128(id128);
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
