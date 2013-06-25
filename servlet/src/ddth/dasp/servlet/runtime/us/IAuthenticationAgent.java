package ddth.dasp.servlet.runtime.us;

import ddth.dasp.servlet.runtime.us.bo.IUser;

/**
 * Provides APIs to authenticate users.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IAuthenticationAgent {
	/**
	 * Authenticates a user against a password.
	 * 
	 * @param user
	 *            IUser
	 * @param password
	 *            String
	 * @return boolean
	 */
	public boolean authenticate(IUser user, String password);
}
