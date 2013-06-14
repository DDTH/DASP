package ddth.dasp.test.id;

import java.util.Date;

import ddth.dasp.common.id.IdGenerator;

public class TestIdGenerator {

    public static void main(String... args) {
        long current = System.currentTimeMillis();
        current = (current - IdGenerator.TIMESTAMP_EPOCH) / 10000L;
        System.out.println(Long.toBinaryString(current));

        long next10Years = System.currentTimeMillis() + 10L * 365L * 24L * 3600L * 1000L;
        next10Years = (next10Years - IdGenerator.TIMESTAMP_EPOCH) / 10000L;
        System.out.println(Long.toBinaryString(next10Years));

        long next100Years = System.currentTimeMillis() + 100L * 365L * 24L * 3600L * 1000L;
        next100Years = (next100Years - IdGenerator.TIMESTAMP_EPOCH) / 10000L;
        System.out.println(Long.toBinaryString(next100Years));

        long next500Years = System.currentTimeMillis() + 500L * 365L * 24L * 3600L * 1000L;
        next500Years = (next500Years - IdGenerator.TIMESTAMP_EPOCH) / 10000L;
        System.out.println(Long.toBinaryString(next500Years));

        System.out.println();

        IdGenerator idGen = IdGenerator.getInstance(System.currentTimeMillis());

        for (int i = 0; i < 10; i++) {
            long idTiny = idGen.generateIdTiny();
            String tinyHex = Long.toHexString(idTiny);
            String tinyAscii36 = Long.toString(idTiny, Character.MAX_RADIX);
            String tinyAscii62 = IdGenerator.toString(idTiny, IdGenerator.MAX_RADIX);
            System.out.println(idTiny);
            System.out.println(tinyHex);
            System.out.println(tinyAscii36);
            System.out.println(tinyAscii62);
            System.out.println(new Date(IdGenerator.extractTimestampTiny(idTiny)));
            System.out.println();
        }

        long idMini = idGen.generateIdMini();
        String miniHex = Long.toHexString(idMini);
        String miniAscii36 = Long.toString(idMini, Character.MAX_RADIX);
        String miniAscii62 = IdGenerator.toString(idMini, IdGenerator.MAX_RADIX);
        System.out.println(idMini);
        System.out.println(miniHex);
        System.out.println(miniAscii36);
        System.out.println(miniAscii62);
        System.out.println();
    }
}
