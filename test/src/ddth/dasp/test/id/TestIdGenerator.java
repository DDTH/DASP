package ddth.dasp.test.id;

import java.util.Date;

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

    public static void main(String... args) {
        long l = 12345;
        System.out.println(toString(l, 46));

        IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

        System.out.println(Character.MAX_RADIX);
        System.out.println();

        long temp = (System.currentTimeMillis() - IdGenerator.TIMESTAMP_EPOCH) / 10;
        System.out.println(temp);
        System.out.println(IdGenerator.toString(temp, 16));
        System.out.println(IdGenerator.toString(temp, 36));
        System.out.println(IdGenerator.toString(temp, 46));

        System.out.println();

        long value;
        String hex, ascii36, ascii46;

        value = idGen.generateId64();
        hex = Long.toHexString(value);
        ascii36 = Long.toString(value, Character.MAX_RADIX);
        ascii46 = IdGenerator.toString(value, IdGenerator.MAX_RADIX);
        System.out.println(value);
        System.out.println(hex);
        System.out.println(ascii36);
        System.out.println(ascii46);

        System.out.println(IdGenerator.parseLong(hex, 16));
        System.out.println(IdGenerator.parseLong(ascii36, Character.MAX_RADIX));
        System.out.println(IdGenerator.parseLong(ascii46, IdGenerator.MAX_RADIX));

        long timestamp = IdGenerator.extractTimestamp64(value);
        System.out.println(timestamp);
        System.out.println(timestamp - IdGenerator.TIMESTAMP_EPOCH);
        System.out.println((timestamp - IdGenerator.TIMESTAMP_EPOCH) / 1000.0);

        System.out.println(new Date(timestamp));
        System.out.println(new Date(timestamp - IdGenerator.TIMESTAMP_EPOCH));
    }
}
