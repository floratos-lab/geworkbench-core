package org.geworkbench.components.genspace.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="User_Network")
public class UserNetwork implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2587552459317296299L;
	private int id;
	private User user;
	private Network network;
	private boolean verified;
	private boolean visible;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="user_id")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@ManyToOne
	@JoinColumn(name="network_id")
	public Network getNetwork() {
		return network;
	}
	public void setNetwork(Network network) {
		this.network = network;
	}
	
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	@Override
	public String toString() {
		return "UserNetwork [user = {"+user.toString()+"}, network={"+network.toString()+"}, visible="+(isVisible() ? "true" : "false")+"]";
	}
	
}
