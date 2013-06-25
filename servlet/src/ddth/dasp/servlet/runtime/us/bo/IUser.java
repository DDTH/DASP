package ddth.dasp.servlet.runtime.us.bo;

public interface IUser {
    public int getId();
    
    public String getDomain();
    
    public boolean isDisabled();
    
    public String getLoginName();
    
    public String getPassword();
}
