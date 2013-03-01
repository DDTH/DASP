package ddth.dasp.framework.cache;

public class CacheUtils {
    public final static boolean isNullValue(Object value) {
        return (value == null) || (value instanceof short[] && ((short[]) value).length == 0);
    }
}
