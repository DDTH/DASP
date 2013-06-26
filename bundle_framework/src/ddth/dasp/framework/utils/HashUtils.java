package ddth.dasp.framework.utils;

import java.nio.charset.Charset;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class HashUtils {

	private final static HashFunction hashFunction = Hashing.murmur3_128(0);
	private final static Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Calculate hash value of an object using a fast non-cryptographic-strength
	 * hash function.
	 * 
	 * @param object
	 * @return
	 */
	public static long fastHashValue(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Boolean || object instanceof Number
				|| object instanceof String) {
			return hashFunction.hashString(object.toString(), UTF8).asLong();
		}
		return hashFunction.hashInt(object.hashCode()).asLong();
	}

	/**
	 * Maps an object to a slot, using linear hash method.
	 * 
	 * This method uses a fast non-cryptographic-strength hash function to
	 * calculate object's hash value.
	 * 
	 * @param object
	 * @param numSlots
	 * @return
	 */
	public static long linearHashingMap(Object object, long numSlots) {
		if (numSlots < 1) {
			String msg = "Number of slots must be equal or larger than 1!";
			throw new IllegalArgumentException(msg);
		}
		if (numSlots == 1 || object == null) {
			return 0;
		}
		return Math.abs(fastHashValue(object) % numSlots);
	}

	/**
	 * Maps an object to a slow, using consistent hash method.
	 * 
	 * This method uses a fast non-cryptographic-strength hash function to
	 * calculate object's hash value.
	 * 
	 * @param object
	 * @param numSlots
	 */
	public static long consistentHashingMap(Object object, long numSlots) {
		if (numSlots < 1) {
			String msg = "Number of slots must be equal or larger than 1!";
			throw new IllegalArgumentException(msg);
		}
		if (numSlots == 1 || object == null) {
			return 0;
		}
		long hashValue = fastHashValue(object);
		return Hashing.consistentHash(hashValue, (int) numSlots);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			long hash4 = consistentHashingMap(i, 4);
			long hash5 = consistentHashingMap(i, 5);
			long hash6 = consistentHashingMap(i, 6);
			System.out.println(hash4 + "/" + hash5 + "/" + hash6);
		}
	}
}
