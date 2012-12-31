package ddth.dasp.hetty.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.google.protobuf.ByteString;

public class ResponseUtils {

    public final static DateFormat DF_HEADER = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z");
    public final static String SERVER = "Hetty Server v0.1.0";
    private final static ByteString EMPTY_CONTENT = ByteString.EMPTY;

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            String contentString) {
        return response200(request, contentString, "text/html; charset=utf-8");
    }

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            byte[] contentBin) {
        return response200(request, contentBin, "text/html; charset=utf-8");
    }

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            String contentString, String contentType) {
        return response200(request, contentString, contentType, null, null);
    }

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            String contentString, String contentType, HettyProtoBuf.NameValue[] headers,
            HettyProtoBuf.Cookie[] cookies) {
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(200);
        builder.addHeaders(newHeader("Content-Type", contentType));
        ByteString content = ByteString.copyFromUtf8(contentString);
        builder.addHeaders(newHeader("Content-Length", content.size()));
        builder.setContent(content);
        // headers
        if (headers != null) {
            for (HettyProtoBuf.NameValue header : headers) {
                builder.addHeaders(header);
            }
        }
        // cookies
        if (cookies != null) {
            for (HettyProtoBuf.Cookie cookie : cookies) {
                builder.addCookies(cookie);
            }
        }
        return builder;
    }

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            byte[] contentBin, String contentType) {
        return response200(request, contentBin, contentType, null, null);
    }

    public static HettyProtoBuf.Response.Builder response200(HettyProtoBuf.Request request,
            byte[] contentBin, String contentType, HettyProtoBuf.NameValue[] headers,
            HettyProtoBuf.Cookie[] cookies) {
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(200);
        builder.addHeaders(newHeader("Content-Type", contentType));
        ByteString content = ByteString.copyFrom(contentBin);
        builder.addHeaders(newHeader("Content-Length", content.size()));
        builder.setContent(content);
        // headers
        if (headers != null) {
            for (HettyProtoBuf.NameValue header : headers) {
                builder.addHeaders(header);
            }
        }
        // cookies
        if (cookies != null) {
            for (HettyProtoBuf.Cookie cookie : cookies) {
                builder.addCookies(cookie);
            }
        }
        return builder;
    }

    /**
     * Helper method to create a 301 response.
     * 
     * @param request
     * @param url
     * @return
     */
    public static HettyProtoBuf.Response.Builder response301(HettyProtoBuf.Request request,
            String url) {
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(301);
        builder.addHeaders(newHeader("Location", url));
        return builder;
    }

    private static String loadContent(String path) {
        InputStream is = ResponseUtils.class.getResourceAsStream(path);
        try {
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static String content404 = null;

    /**
     * Helper method to create a 404 response.
     * 
     * @param request
     * @return
     */
    public static HettyProtoBuf.Response.Builder response404(HettyProtoBuf.Request request) {
        return response404(request, "Not found");
    }

    /**
     * Helper method to create a 404 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static HettyProtoBuf.Response.Builder response404(HettyProtoBuf.Request request,
            String message) {
        if (content404 == null) {
            content404 = loadContent("/ddth/dasp/hetty/message/404.tpl");
        }
        String referer = HettyProtoBufUtils.getHeader(request, "Referer");
        String htmlContent = content404.replace("${referer}", referer != null ? referer : "")
                .replace("${message}", message != null ? message : "");
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(404);
        builder.addHeaders(newHeader("Content-Type", "text/html; charset=UTF-8"));
        // ByteString content = ByteString.copyFromUtf8(message);
        ByteString content = ByteString.copyFromUtf8(htmlContent);
        builder.addHeaders(newHeader("Content-Length", content.size()));
        builder.setContent(content);
        return builder;
    }

    private static String content500 = null;

    /**
     * Helper method to create a 500 response.
     * 
     * @param request
     * @return
     */
    public static HettyProtoBuf.Response.Builder response500(HettyProtoBuf.Request request) {
        return response500(request, null, null);
    }

    /**
     * Helper method to create a 500 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static HettyProtoBuf.Response.Builder response500(HettyProtoBuf.Request request,
            String message) {
        return response500(request, message, null);
    }

    /**
     * Helper method to create a 500 response.
     * 
     * @param request
     * @param message
     * @param t
     * @return
     */
    public static HettyProtoBuf.Response.Builder response500(HettyProtoBuf.Request request,
            String message, Throwable t) {
        if (content500 == null) {
            content500 = loadContent("/ddth/dasp/hetty/message/500.tpl");
        }
        String referer = HettyProtoBufUtils.getHeader(request, "Referer");
        if (message == null && t != null) {
            message = t.getMessage();
        }
        StringBuilder exception = null;
        if (t != null) {
            exception = new StringBuilder("<h5 class=\"alert-heading\">Stacktrace:</h5>");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(baos);
            writer.print("<pre>");
            t.printStackTrace(writer);
            writer.print("</pre>");
            writer.println("<p>&nbsp;</p>");

            Throwable cause = t.getCause();
            while (cause != null) {
                writer.println("<h6 class=\"alert-heading\">Cause:</h6>");
                writer.println("<pre>");
                cause.printStackTrace(writer);
                writer.println("</pre>");
                cause = cause.getCause();
            }
            writer.flush();

            try {
                exception.append(baos.toString("utf-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        String htmlContent = content500.replace("${referer}", referer != null ? referer : "")
                .replace("${message}", message != null ? message : "")
                .replace("${exception}", exception != null ? exception.toString() : "");
        HettyProtoBuf.Response.Builder builder = newResponse(request).setStatus(500);
        builder.addHeaders(newHeader("Content-Type", "text/html; charset=UTF-8"));
        ByteString content = ByteString.copyFromUtf8(htmlContent);
        builder.addHeaders(newHeader("Content-Length", content.size()));
        builder.setContent(content);
        return builder;
    }

    public static HettyProtoBuf.NameValue newHeader(String name, String value) {
        return newHeader(name, value, null);
    }

    public static HettyProtoBuf.NameValue newHeader(String name, String value,
            HettyProtoBuf.Response.Builder responseBuilder) {
        HettyProtoBuf.NameValue header = HettyProtoBuf.NameValue.newBuilder().setName(name)
                .setValue(value).build();
        if (responseBuilder != null) {
            responseBuilder.addHeaders(header);
        }
        return header;
    }

    public static HettyProtoBuf.NameValue newHeader(String name, Date value) {
        return newHeader(name, value, null);
    }

    public static HettyProtoBuf.NameValue newHeader(String name, Date value,
            HettyProtoBuf.Response.Builder responseBuilder) {
        HettyProtoBuf.NameValue header = HettyProtoBuf.NameValue.newBuilder().setName(name)
                .setValue(DF_HEADER.format(value)).build();
        if (responseBuilder != null) {
            responseBuilder.addHeaders(header);
        }
        return header;
    }

    public static HettyProtoBuf.NameValue newHeader(String name, int value) {
        return newHeader(name, value, null);
    }

    public static HettyProtoBuf.NameValue newHeader(String name, int value,
            HettyProtoBuf.Response.Builder responseBuilder) {
        HettyProtoBuf.NameValue header = HettyProtoBuf.NameValue.newBuilder().setName(name)
                .setValue(String.valueOf(value)).build();
        if (responseBuilder != null) {
            responseBuilder.addHeaders(header);
        }
        return header;
    }

    public static HettyProtoBuf.NameValue newHeader(String name, long value) {
        return newHeader(name, value, null);
    }

    public static HettyProtoBuf.NameValue newHeader(String name, long value,
            HettyProtoBuf.Response.Builder responseBuilder) {
        HettyProtoBuf.NameValue header = HettyProtoBuf.NameValue.newBuilder().setName(name)
                .setValue(String.valueOf(value)).build();
        if (responseBuilder != null) {
            responseBuilder.addHeaders(header);
        }
        return header;
    }

    public static HettyProtoBuf.Response.Builder newResponse(HettyProtoBuf.Request request) {
        return HettyProtoBuf.Response.newBuilder().setRequestId(request.getId())
                .setRequestTimestamp(request.getTimestamp()).setChannelId(request.getChannelId())
                .setStatus(200).addHeaders(newHeader("Server", SERVER)).setContent(EMPTY_CONTENT)
                .addHeaders(newHeader("Date", new Date()));
    }
}
