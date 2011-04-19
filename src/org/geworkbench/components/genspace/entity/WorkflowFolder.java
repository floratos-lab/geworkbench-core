package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class WorkflowFolder implements Serializable, Comparable<WorkflowFolder>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1365608467302705177L;
	private int id;
	private User owner;
	private String name;
	private WorkflowFolder parent;
	private List<UserWorkflow> workflows = new ArrayList<UserWorkflow>();
	
	private List<WorkflowFolder> children = new ArrayList<WorkflowFolder>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User ownerId) {
		this.owner = ownerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@OneToMany(mappedBy="parent")
	public List<WorkflowFolder> getChildren(){
		return children;
	}
	public void setChildren(List<WorkflowFolder> children) {
		this.children = children;
	}
	@ManyToOne
	public WorkflowFolder getParent() {
		return parent;
	}
	public void setParent(WorkflowFolder parent) {
		this.parent = parent;
	}
	@Override
	public int compareTo(WorkflowFolder o) {
		return this.getName().compareTo(o.getName());
	}
	@OneToMany(mappedBy="folder")
	public List<UserWorkflow> getWorkflows() {
		return workflows;
	}
	public void setWorkflows(List<UserWorkflow> workflows) {
		this.workflows = workflows;
	}
	@Override
	public String toString() {
		return this.getName();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WorkflowFolder)
			return ((WorkflowFolder) obj).getId() == getId();
		return false;
	}
	@Override
	public int hashCode() {
		return new Integer(id).hashCode();
	}
}
