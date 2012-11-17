package ddth.dasp.test.id;

import ddth.dasp.common.id.IdGenerator;

public class TestIdGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

		System.out.println(Character.MAX_RADIX);
		System.out.println();

		System.out.println(idGen.generateId48());
		System.out.println(idGen.generateId48Hex());
		System.out.println(idGen.generateId48Ascii());
		System.out.println();

		System.out.println(idGen.generateId64());
		System.out.println(idGen.generateId64Hex());
		System.out.println(idGen.generateId64Ascii());
		System.out.println();

		System.out.println(idGen.generateId128());
		System.out.println(idGen.generateId128Hex());
		System.out.println(idGen.generateId128Ascii());
		System.out.println();
	}
}
