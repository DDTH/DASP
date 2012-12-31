package ddth.dasp.hetty.message.protobuf;

import java.util.List;

public class HettyProtoBufUtils {
    public static String getHeader(HettyProtoBuf.Request request, String name) {
        List<HettyProtoBuf.NameValue> headerList = request.getHeadersList();
        for (HettyProtoBuf.NameValue header : headerList) {
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }
}
