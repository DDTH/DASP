package ddth.dasp.test.id;

import ddth.dasp.common.id.IdGenerator;

public class TestIdGenerator {

    final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    public static String toString(long l, int radix) {
        if (radix < Character.MIN_RADIX || radix > digits.length) {
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

    /**
     * @param args
     */
    public static void main(String[] args) {
        long l = 12345;
        System.out.println(toString(l, 46));

        IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

        System.out.println(Character.MAX_RADIX);
        System.out.println();

        long value;

        value = idGen.generateId48();
        System.out.println(idGen.generateId48());
        System.out.println(idGen.generateId48Hex());
        System.out.println(idGen.generateId48Ascii());
        System.out.println(toString(value, 46));
        System.out.println();

        value = idGen.generateId64();
        System.out.println(idGen.generateId64());
        System.out.println(idGen.generateId64Hex());
        System.out.println(idGen.generateId64Ascii());
        System.out.println(toString(value, 46));
        System.out.println();

        System.out.println(idGen.generateId128());
        System.out.println(idGen.generateId128Hex());
        System.out.println(idGen.generateId128Ascii());
        System.out.println();
    }
}
