package ddth.dasp.hetty.message;

import java.util.Map;

public interface IRequest {

    public byte[] serialize();

    public String getId();

    public String getResponseTopic();

    public int getChannelId();

    public long getTimestampNano();

    public String getMethod();

    public String getUri();

    public String getDomain();

    public int getPort();

    public String getPath();

    public Map<String, String> getUrlParams();

    public String[] getPathParams();

    public ICookie getCookie(String name);

    public ICookie[] getCookies();

    public String getHeader(String name);

    public Map<String, String> getHeaders();

    public byte[] getContent();

    public boolean isChunk();

    public int getChunkSequence();
}
