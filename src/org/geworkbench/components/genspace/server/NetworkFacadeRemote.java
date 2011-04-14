package org.geworkbench.components.genspace.server;
import java.util.List;

import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.Network;
import org.geworkbench.components.genspace.entity.User;
import org.geworkbench.components.genspace.entity.UserNetwork;

@Remote
public interface NetworkFacadeRemote {
	
	/**
	 * Return all (visible) users that are in a network
	 * @param networkFilter
	 * @return
	 */
	public List<User> getProfilesByNetwork(Network networkFilter);
	
	/**
	 * Request to join a network
	 * @param n
	 */
	public void joinNetwork(Network n);
	
	/**
	 * Create (and then join) a network
	 * @param text
	 */
	public void createNetwork(String text);
	
	/**
	 * Leave a network that the user is currently in
	 * @param selected
	 */
	public void leaveNetwork(UserNetwork selected);
	
	/**
	 * Get all networks that you are in
	 * @return
	 */
	public List<UserNetwork> getMyNetworks();
	
	/**
	 * Get a list of ALL networks (both those that you are in and are not
	 * @return
	 */
	public List<Network> getAllNetworks();
	
	/**
	 * Request to join a network
	 * @param toJoin name of network to join
	 */
	public void joinNetwork(String toJoin);
	
	/**
	 * Get a list of all requests by users to join a specified network (which you must be the creator of)
	 * @param nt Network to look for requests
	 * @return
	 */
	public List<UserNetwork> getNetworkRequests(Network nt);
	
	/**
	 * Accept a request by a user to join a network which you are the creator of
	 * @param request
	 */
	public void acceptNetworkRequest(UserNetwork request);
	/**
	 * Reject a request by a user to join a network which you are the creator of
	 * @param request
	 */
	public void rejectNetworkRequest(UserNetwork request);
	
	/**
	 * Set your visibility for a network
	 * @param network
	 * @param visibility
	 */
	public void updateNetworkVisibility(UserNetwork network,
			Boolean visibility);
	
	/**
	 * Get number of pending requests to join networks which you are the creator of
	 * @return
	 */
	public int getNumberOfNetworkRequests();
}
