package ddth.dasp.hetty.message.protobuf;

import java.util.List;

/**
 * Utility class to work with the request.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 */
public class RequestUtils {
    public static String getPathParam(HettyProtoBuf.Request requestProtobuf, int index) {
        List<String> pathParams = requestProtobuf.getPathParamsList();
        String result = (0 <= index && index < pathParams.size()) ? pathParams.get(index) : null;
        return result;
    }

    public static String getUrlParam(HettyProtoBuf.Request requestProtobuf, String name) {
        List<HettyProtoBuf.NameValue> urlParams = requestProtobuf.getUrlParamsList();
        for (HettyProtoBuf.NameValue param : urlParams) {
            if (param.getName().equals(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    public static String getHeader(HettyProtoBuf.Request requestProtobuf, String name) {
        List<HettyProtoBuf.NameValue> headers = requestProtobuf.getHeadersList();
        for (HettyProtoBuf.NameValue header : headers) {
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public static HettyProtoBuf.Cookie getCookie(HettyProtoBuf.Request requestProtobuf, String name) {
        List<HettyProtoBuf.Cookie> cookies = requestProtobuf.getCookiesList();
        for (HettyProtoBuf.Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
