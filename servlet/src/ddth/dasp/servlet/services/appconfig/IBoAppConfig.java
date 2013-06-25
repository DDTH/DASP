package ddth.dasp.servlet.services.appconfig;

public interface IBoAppConfig {
    public String getKey();

    public Character getValueBit();

    public Long getValueInt();

    public Double getValueReal();

    public String getValueStr();

    public byte[] getValueBin();
}
