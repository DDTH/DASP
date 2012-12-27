package ddth.dasp.hetty.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.protobuf.ByteString;

public class ResponseUtils {

    public final static DateFormat DF_HEADER = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z");
    public final static String SERVER = "Hetty Server v0.1.0";

    /**
     * Helper method to create a 404 response.
     * 
     * @param request
     * @return
     */
    public static HettyProtoBuf.Response response404(HettyProtoBuf.Request request) {
        return response404(request, "Not found");
    }

    /**
     * Helper method to create a 404 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static HettyProtoBuf.Response response404(HettyProtoBuf.Request request, String message) {
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(404);
        builder.addHeaders(newHeader("Date", new Date()));
        builder.addHeaders(newHeader("Content-Type", "text/html; charset=UTF-8"));
        ByteString content = ByteString.copyFromUtf8(message);
        builder.addHeaders(newHeader("Content-Length", content.size()));
        builder.setContent(content);
        return builder.build();
    }

    public static HettyProtoBuf.NameValue newHeader(String name, String value) {
        return HettyProtoBuf.NameValue.newBuilder().setName(name).setValue(value).build();
    }

    public static HettyProtoBuf.NameValue newHeader(String name, Date value) {
        return HettyProtoBuf.NameValue.newBuilder().setName(name).setValue(DF_HEADER.format(value))
                .build();
    }

    public static HettyProtoBuf.NameValue newHeader(String name, int value) {
        return HettyProtoBuf.NameValue.newBuilder().setName(name).setValue(String.valueOf(value))
                .build();
    }

    public static HettyProtoBuf.NameValue newHeader(String name, long value) {
        return HettyProtoBuf.NameValue.newBuilder().setName(name).setValue(String.valueOf(value))
                .build();
    }

    public static HettyProtoBuf.Response.Builder newResponse(HettyProtoBuf.Request request) {
        return HettyProtoBuf.Response.newBuilder().setRequestId(request.getId())
                .setDuration(System.currentTimeMillis() - request.getTimestamp())
                .setChannelId(request.getChannelId()).setStatus(200)
                .addHeaders(newHeader("Server", SERVER));
    }
}
