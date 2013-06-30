package ddth.dasp.hetty.message.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import ddth.dasp.hetty.message.ICookie;
import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.IResponse;

/**
 * Response generator utility class.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 */
public class ResponseUtils {

    public final static DateFormat DF_HEADER = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z");
    static {
        DF_HEADER.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    public final static String SERVER = "Hetty Server v0.1.0";
    // private final static ByteString EMPTY_CONTENT = ByteString.EMPTY;
    private final static byte[] EMPTY_CONTENT = ArrayUtils.EMPTY_BYTE_ARRAY;

    /**
     * Helper method to create a "text/html; charset=utf-8"-200 response,
     * without cookies or any specific headers.
     * 
     * @param request
     * @param contentString
     * @return
     */
    public static IResponse response200(IRequest request, String contentString) {
        return response200(request, contentString, "text/html; charset=utf-8");
    }

    /**
     * Helper method to create a "text/html; charset=utf-8"-200 response,
     * without cookies or any specific headers.
     * 
     * @param request
     * @param contentBin
     * @return
     */
    public static IResponse response200(IRequest request, byte[] contentBin) {
        return response200(request, contentBin, "text/html; charset=utf-8");
    }

    /**
     * Helper method to create a 200 response with specified content type,
     * without cookies or any specific headers.
     * 
     * @param request
     * @param contentString
     * @param contentType
     * @return
     */
    public static IResponse response200(IRequest request, String contentString, String contentType) {
        return response200(request, contentString, contentType, null, null);
    }

    /**
     * Helper method to create a 200 response with specified content type,
     * cookies and headers.
     * 
     * @param request
     * @param contentString
     * @param contentType
     * @param headers
     * @param cookies
     * @return
     */
    public static IResponse response200(IRequest request, String contentString, String contentType,
            Map<String, String> headers, ICookie[] cookies) {
        IResponse response = newResponse(request).setStatus(200);
        response.addHeader("Content-Type", contentType);
        response.setContent(contentString);
        // headers
        if (headers != null) {
            for (Entry<String, String> header : headers.entrySet()) {
                response.addHeader(header.getKey(), header.getValue());
            }
        }
        // cookies
        if (cookies != null) {
            for (ICookie cookie : cookies) {
                response.addCookie(cookie);
            }
        }
        return response;
    }

    /**
     * Helper method to create a 200 response with specified content type,
     * without cookies or any specific headers.
     * 
     * @param request
     * @param contentBin
     * @param contentType
     * @return
     */
    public static IResponse response200(IRequest request, byte[] contentBin, String contentType) {
        return response200(request, contentBin, contentType, null, null);
    }

    /**
     * Helper method to create a 200 response with specified content type,
     * cookies and headers.
     * 
     * @param request
     * @param contentBin
     * @param contentType
     * @param headers
     * @param cookies
     * @return
     */
    public static IResponse response200(IRequest request, byte[] contentBin, String contentType,
            Map<String, String> headers, ICookie[] cookies) {
        IResponse response = newResponse(request).setStatus(200);
        response.addHeader("Content-Type", contentType);
        response.setContent(contentBin);
        // headers
        if (headers != null) {
            for (Entry<String, String> header : headers.entrySet()) {
                response.addHeader(header.getKey(), header.getValue());
            }
        }
        // cookies
        if (cookies != null) {
            for (ICookie cookie : cookies) {
                response.addCookie(cookie);
            }
        }
        return response;
    }

    /**
     * Helper method to create a 301 response.
     * 
     * @param request
     * @param url
     * @return
     */
    public static IResponse response301(IRequest request, String url) {
        IResponse response = newResponse(request).setStatus(301).addHeader("Location", url);
        return response;
    }

    /**
     * Helper method to create a 304 response.
     * 
     * @param request
     * @return
     */
    public static IResponse response304(IRequest request) {
        IResponse response = newResponse(request).setStatus(304);
        return response;
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
    public static IResponse response404(IRequest request) {
        return response404(request, "Not found");
    }

    /**
     * Helper method to create a 404 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static IResponse response404(IRequest request, String message) {
        if (content404 == null) {
            content404 = loadContent("/ddth/dasp/hetty/404.tpl");
        }
        String referer = request.getHeader("Referer");
        String htmlContent = content404.replace("${referer}", referer != null ? referer : "")
                .replace("${message}", message != null ? message : "");
        IResponse response = newResponse(request).setStatus(404)
                .addHeader("Content-Type", "text/html; charset=UTF-8").setContent(htmlContent);
        return response;
    }

    private static String content500 = null;

    /**
     * Helper method to create a 500 response.
     * 
     * @param request
     * @return
     */
    public static IResponse response500(IRequest request) {
        return response500(request, null, null);
    }

    /**
     * Helper method to create a 500 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static IResponse response500(IRequest request, String message) {
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
    public static IResponse response500(IRequest request, String message, Throwable t) {
        if (content500 == null) {
            content500 = loadContent("/ddth/dasp/hetty/500.tpl");
        }
        String referer = request.getHeader("Referer");
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
        IResponse response = newResponse(request).setStatus(500)
                .addHeader("Content-Type", "text/html; charset=UTF-8").setContent(htmlContent);
        return response;
    }

    private static String content503 = null;

    /**
     * Helper method to create a 503 response.
     * 
     * @param request
     * @return
     */
    public static IResponse response503(IRequest request) {
        return response503(request, null);
    }

    /**
     * Helper method to create a 503 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static IResponse response503(IRequest request, String message) {
        if (content503 == null) {
            content503 = loadContent("/ddth/dasp/hetty/503.tpl");
        }
        String referer = request.getHeader("Referer");
        String htmlContent = content503.replace("${referer}", referer != null ? referer : "")
                .replace("${message}", message != null ? message : "");
        IResponse response = newResponse(request).setStatus(500)
                .addHeader("Content-Type", "text/html; charset=UTF-8").setContent(htmlContent);
        return response;
    }

    private static String content504 = null;

    /**
     * Helper method to create a 504 response.
     * 
     * @param request
     * @return
     */
    public static IResponse response504(IRequest request) {
        return response504(request, null);
    }

    /**
     * Helper method to create a 504 response.
     * 
     * @param request
     * @param message
     * @return
     */
    public static IResponse response504(IRequest request, String message) {
        if (content504 == null) {
            content504 = loadContent("/ddth/dasp/hetty/504.tpl");
        }
        String referer = request.getHeader("Referer");
        String htmlContent = content504.replace("${referer}", referer != null ? referer : "")
                .replace("${message}", message != null ? message : "");
        IResponse response = newResponse(request).setStatus(500)
                .addHeader("Content-Type", "text/html; charset=UTF-8").setContent(htmlContent);
        return response;
    }

    /**
     * Helper method to create a new empty response.
     * 
     * The newly created response will have status of 200, and 2 headers
     * "Server" and "Date" added.
     * 
     * @param request
     * @return
     */
    public static IResponse newResponse(IRequest request) {
        HettyProtoBuf.Response.Builder responseBuilder = HettyProtoBuf.Response.newBuilder()
                .setRequestId(request.getId()).setRequestTimestampNano(request.getTimestampNano())
                .setChannelId(request.getChannelId()).setStatus(200);
        // .addHeaders(newHeader("Server", SERVER)).setContent(EMPTY_CONTENT)
        // .addHeaders(newHeader("Date", new Date()));
        IResponse response = new ProtoBufResponse(responseBuilder).addHeader("Server", SERVER)
                .addHeader("Date", new Date()).setContent(EMPTY_CONTENT);
        return response;
    }
}
