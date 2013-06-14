package ddth.dasp.test.id;

import java.util.Date;

import ddth.dasp.common.id.IdGenerator;

public class TestIdGenerator {
    public static void main(String... args) {
        IdGenerator idGen = IdGenerator.getInstance(System.currentTimeMillis());
        long id48 = idGen.generateId48();
        String ascii = IdGenerator.toString(id48, IdGenerator.MAX_RADIX);
        System.out.println(id48);
        System.out.println(ascii);

        System.out.println();

        long idMini = idGen.generateIdMini();
        String asciiMini = IdGenerator.toString(idMini, IdGenerator.MAX_RADIX);
        System.out.println(idMini);
        System.out.println(asciiMini);

        long timestamp1 = IdGenerator.extractTimestampMini(idMini);
        long timestamp2 = IdGenerator.extractTimestampMini(IdGenerator.parseLong(asciiMini,
                IdGenerator.MAX_RADIX));
        System.out.println(timestamp1 + ":" + new Date(timestamp1));
        System.out.println(timestamp2 + ":" + new Date(timestamp2));
    }
}
