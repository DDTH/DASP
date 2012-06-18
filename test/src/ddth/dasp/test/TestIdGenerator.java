package ddth.dasp.test;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.test.utils.Benchmark;
import ddth.dasp.test.utils.BenchmarkResult;
import ddth.dasp.test.utils.Operation;

public class TestIdGenerator {
	private static void testIdGen() {
		final Map<Object, Boolean> map = new ConcurrentHashMap<Object, Boolean>();
		final IdGenerator ID_GENERATOR = IdGenerator.getInstance(IdGenerator
				.getMacAddr());

		BenchmarkResult result = new Benchmark(new Operation() {
			@Override
			public void run(int runId) {
				// long commentId = ID_GENERATOR.generateId64();
				BigInteger commentId = ID_GENERATOR.generateId128();
				Object id = commentId;
				// final StringBuffer commentIdHex = new
				// StringBuffer(Long.toHexString(commentId));
				// while (commentIdHex.length() < 16) {
				// commentIdHex.insert(0, '0');
				// }
				// String id = commentIdHex.toString();
				if (map.containsKey(id)) {
					System.out.println("Was generated: " + commentId);
				} else {
					map.put(id, Boolean.TRUE);
				}
			}

		}, 1000000, 16).run();
		System.out.println(result.summarize());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			testIdGen();
		}
	}
}
