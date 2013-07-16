package ddth.dasp.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

public class SerializeUtils {
    /**
     * Serializes an object.
     * 
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        JBossObjectOutputStream joos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            joos = new JBossObjectOutputStream(baos);
            joos.writeObject(obj);
            joos.flush();
            baos.flush();
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(joos);
        }
    }

    /**
     * Deserializes an object.
     * 
     * @param serializedBytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] serializedBytes) throws IOException,
            ClassNotFoundException {
        if (serializedBytes == null) {
            return null;
        }
        JBossObjectInputStream jois = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
            jois = new JBossObjectInputStream(bais);
            return jois.readObject();
        } finally {
            IOUtils.closeQuietly(jois);
        }
    }

    /**
     * Generic version of {@link #deserialize(byte[])}.
     * 
     * Note: if the deserialized object is not of type T, this method returns
     * <code>null</code>.
     * 
     * @param serializedBytes
     * @param clazz
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] serializedBytes, Class<T> clazz) throws IOException,
            ClassNotFoundException {
        Object result = deserialize(serializedBytes);
        if (result == null) {
            return null;
        }
        if (clazz.isAssignableFrom(result.getClass())) {
            return (T) result;
        }
        return null;
    }
}
