package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Network implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 330141032033420545L;
	private int id;
	private String name;
	private User owner;
	
	
	private List<UserNetwork> members = new ArrayList<UserNetwork>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne
	@JoinColumn(name="owner")
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@OneToMany(mappedBy="network")
	public List<UserNetwork> getMembers() {
		return members;
	}
	public void setMembers(List<UserNetwork> members) {
		this.members = members;
	}
	
	@Override
	public String toString() {
		return name;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Network)
		{
			Network o = (Network) obj;
			return o.getId() == getId() && o.getName().equals(getName());
		}
		return false;
	}
}
