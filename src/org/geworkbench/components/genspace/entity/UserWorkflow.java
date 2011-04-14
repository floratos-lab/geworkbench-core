package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
public class UserWorkflow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6313031043276205912L;
	private int id;
	private User owner;
	private Workflow workflow;
	private String name;
	private Date createdAt;
	private WorkflowFolder folder;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Workflow getWorkflow() {
		return workflow;
	}
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@ManyToOne
	public WorkflowFolder getFolder() {
		return folder;
	}
	public void setFolder(WorkflowFolder folder) {
		this.folder = folder;
	}
	@Override
	public String toString() {
		return "UserWorkflow - name: " + name + ", " + owner.getUsername() + ", "
				+ workflow;
	}
	@Override
	public int hashCode() {
		return this.getId();
	}
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		try {
			UserWorkflow uw = (UserWorkflow) o;
			return owner.equals(uw.owner) && workflow.equals(uw.workflow);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return false;
	}
}
