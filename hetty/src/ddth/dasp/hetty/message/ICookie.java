package ddth.dasp.hetty.message;

public interface ICookie {
    public String getName();

    public String getValue();

    public String getDomain();

    public int getPort();

    public String getPath();

    public int getMaxAge();
}
