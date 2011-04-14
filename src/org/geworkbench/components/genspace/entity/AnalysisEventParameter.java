package org.geworkbench.components.genspace.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AnalysisEventParameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1559558337334158046L;
	private int id;
	private AnalysisEvent event;
	private String parameterKey;
	private String parameterValue;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@ManyToOne
	public AnalysisEvent getEvent() {
		return event;
	}
	public void setEvent(AnalysisEvent event) {
		this.event = event;
	}
	public String getParameterKey() {
		return parameterKey;
	}
	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	
}
