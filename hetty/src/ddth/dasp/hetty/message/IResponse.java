package ddth.dasp.hetty.message;

import java.util.Date;
import java.util.Map;

public interface IResponse {

    public byte[] serialize();

    public String getRequestId();

    public long getRequestTimestampNano();

    public int getChannelId();

    public int getStatus();

    public IResponse setStatus(int status);

    public IResponse addCookie(ICookie cookie);

    public ICookie[] getCookies();

    public IResponse addHeader(String name, String value);

    public IResponse addHeader(String name, Date value);

    public IResponse addHeader(String name, int value);

    public IResponse addHeader(String name, long value);

    public Map<String, String> getHeaders();

    public byte[] getContent();

    public IResponse setContent(byte[] content);

    public IResponse setContent(String content);

    public boolean isChunk();

    public int getChunkSequence();
}
