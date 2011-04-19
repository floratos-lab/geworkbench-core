package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4341632811573425064L;
	private int id;
	private String clientID;
	private String hostname;
	private java.util.Date date;
	private User user;
	private List<AnalysisEvent> analysisEvents;
	private String dataSetName;
	private Workflow workflow;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@OneToMany(mappedBy="transaction")
	public List<AnalysisEvent> getAnalysisEvents() {
		return analysisEvents;
	}
	public void setAnalysisEvents(List<AnalysisEvent> analysisEvents) {
		this.analysisEvents = analysisEvents;
	}
	
	public String getDataSetName() {
		return dataSetName;
	}
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}
	
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	public Workflow getWorkflow() {
		return workflow;
	}
	
	private String userName;
	@Transient
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
