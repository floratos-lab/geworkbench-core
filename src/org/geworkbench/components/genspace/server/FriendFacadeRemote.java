package org.geworkbench.components.genspace.server;

import java.util.List;

import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.Friend;
import org.geworkbench.components.genspace.entity.User;

@Remote
public interface FriendFacadeRemote {
    /**
     * 
     * Find all users who requested to be friends with the requesting user
     * @return List of friends
     */
	public List<User> getFriendRequests();
	
	/**
	 * Request a user to become friends with the current user
	 * @param selectedValue User to ask to be friends with
	 */
	public void addFriend(int uid);
	
	/**
	 * Reject a friend request from a user
	 * @param selectedValue User to reject from being friends
	 */
	public void rejectFriend(int uid);

	/**
	 * Mark a friend as visible or not (ie, that they can or can't see this user's profile)
	 * @param friend
	 * @param boolean1
	 */
	public void updateFriendVisibility(int friend, Boolean boolean1);
	
	/**
	 * Get the list of all users who are friends with the logged in user
	 * @return List of users
	 */
	public List<User> getFriendsProfiles();
	
	/**
	 * Defriend a user
	 * @param u User to defriend
	 */
	public void removeFriend(int u);
}
