package ddth.dasp.servlet.runtime.us.bo;

public interface IUsManager {

    /**
     * Gets a user account by id.
     * 
     * @param id
     * @return
     */
    public IUser getUser(int id);

    /**
     * Gets a user account by login name.
     * 
     * @param loginName
     * @return
     */
    public IUser getUser(String loginName);

    /**
     * Creates a new user group.
     * 
     * @param group
     * @return
     */
    public IUserGroup createUserGroup(IUserGroup group);

    /**
     * Deletes a user group.
     * 
     * @param group
     */
    public void deleteUserGroup(IUserGroup group);

    /**
     * Gets a user group by id.
     * 
     * @param id
     * @return
     */
    public IUserGroup getUserGroup(int id);

    /**
     * Gets all available user groups.
     * 
     * @return
     */
    public IUserGroup[] getAllUserGroups();

    /**
     * Updates an existing user group.
     * 
     * @param group
     * @return
     */
    public IUserGroup updateUserGroup(IUserGroup group);
}
