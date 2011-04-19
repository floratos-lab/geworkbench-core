package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;


@Entity
public class Tool implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6971084517255287450L;
	private int id;

	private String name;
	private String description;
	private List<ToolComment> comments = new ArrayList<ToolComment>();
	private List<ToolRating> ratings = new ArrayList<ToolRating>();
	private String mostCommonParameters;
	private int mostCommonParametersCount;
	private int usageCount;
	private int wfCountHead;
	private int sumRating =0;
	private int numRating =0;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@OrderBy
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(mappedBy="tool")
	public List<ToolComment> getComments() {
		return comments;
	}
	public void setComments(List<ToolComment> comments) {
		this.comments = comments;
	}
	
	@OneToMany(mappedBy="tool")
	public List<ToolRating> getRatings() {
		return ratings;
	}
	public void setRatings(List<ToolRating> ratings) {
		this.ratings = ratings;
	}
	
	public String getMostCommonParameters() {
		return mostCommonParameters;
	}
	public void setMostCommonParameters(String mostCommonParameters) {
		this.mostCommonParameters = mostCommonParameters;
	}
	public int getMostCommonParametersCount() {
		return mostCommonParametersCount;
	}
	public void setMostCommonParametersCount(int mostCommonParametersCount) {
		this.mostCommonParametersCount = mostCommonParametersCount;
	}
	
	public int getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}
	public int getWfCountHead() {
		return wfCountHead;
	}
	public void setWfCountHead(int wfCountHead) {
		this.wfCountHead = wfCountHead;
	}
	public void setSumRating(int sumRating) {
		this.sumRating = sumRating;
	}
	public int getSumRating() {
		return sumRating;
	}
	public void setNumRating(int numRating) {
		this.numRating = numRating;
	}
	public int getNumRating() {
		return numRating;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Tool) {
			Tool t = (Tool) o;
			return t.name.equals(this.name) && t.id == this.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public void updateRatingCache()
	{
		//TODO make this called automatically on save of ratings?
		int numRating =0;
		int totalRating =0;
		for(ToolRating tr : getRatings())
		{
			numRating++;
			totalRating += tr.getRating();
		}
		setNumRating(numRating);
		setSumRating(totalRating);
	}
	
	public double getOverallRating() {
		if(getNumRating() == 0)
			return 0;
		else
			return (double) getSumRating() / (double) getNumRating();
	}
	public void incrementUsageCount() {
		setUsageCount(getUsageCount() + 1);
	}

}
