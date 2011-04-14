package org.geworkbench.components.genspace.server;
import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.User;

@Remote
public interface PublicFacadeRemote extends ToolInformationProvider{
	/**
	 * Register an account
	 * @param u User to register
	 * @return Fully serialized registered user, or null in failure
	 */
	public User register(User u);
}
