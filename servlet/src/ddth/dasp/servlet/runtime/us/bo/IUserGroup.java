package ddth.dasp.servlet.runtime.us.bo;

public interface IUserGroup {
    public int getId();
    
    public String getDomain();

    public boolean isGod();

    public boolean isSystem();

    public String getName();

    public String getDescription();
}
