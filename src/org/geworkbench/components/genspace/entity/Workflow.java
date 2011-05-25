package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.geworkbench.components.genspace.RuntimeEnvironmentSettings;

@Entity
public class Workflow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -344401848390541281L;
	private int id;
	private User creator;
	private java.util.Date createdAt;
	private Transaction creationTransaction;
	private	List<WorkflowTool> tools = new ArrayList<WorkflowTool>();
	private List<WorkflowComment> comments = new ArrayList<WorkflowComment>();
	private List<WorkflowRating> ratings = new ArrayList<WorkflowRating>();
	private int usageCount;
	private Workflow parent;
	private List<Workflow> children;

	private int numRating = 0;
	private int sumRating = 0;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public User getCreator() {
		return creator;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@OneToOne(fetch=FetchType.LAZY)
	public Transaction getCreationTransaction() {
		return creationTransaction;
	}
	public void setCreationTransaction(Transaction creationTransaction) {
		this.creationTransaction = creationTransaction;
	}

	@OneToMany(mappedBy="workflow")
	@OrderBy("order ASC")
	public List<WorkflowTool> getTools() {
		return tools;
	}
	public void setTools(List<WorkflowTool> tools) {
		this.tools = tools;
	}
	
	@OneToMany(mappedBy="parent")
	public List<Workflow> getChildren() {
		return children;
	}
	public void setChildren(List<Workflow> children) {
		this.children = children;
	}
	@OneToMany(mappedBy="workflow")
	public List<WorkflowComment> getComments() {
		return comments;
	}
	public void setComments(List<WorkflowComment> comments) {
		this.comments = comments;
	}
	
	@OneToMany(mappedBy="workflow")
	public List<WorkflowRating> getRatings() {
		return ratings;
	}
	public void setRatings(List<WorkflowRating> ratings) {
		this.ratings = ratings;
	}
	public int getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}
	@Transient
	public double getAvgRating() {
		double result = 0;
		if (ratings.size() > 0) {
			for (WorkflowRating r : ratings) {
				result += r.getRating();
			}
			result /= ratings.size();
		}
		return result;
	}
	
	@Override
	public String toString() {
		String r = "";
		for(WorkflowTool wt : getTools())
		{
			r += wt.getTool().getName() + ", ";
		}
		if(r.length() > 2)
			r = r.substring(0,r.length()-2);
		return r;
	}
	
	@OneToOne(fetch=FetchType.LAZY)
	public Workflow getParent() {
		return parent;
	}
	public void setParent(Workflow parent) {
		this.parent = parent;
	}
	public int getNumRating() {
		return numRating;
	}
	public void setNumRating(int numRating) {
		this.numRating = numRating;
	}
	public int getSumRating() {
		return sumRating;
	}
	public void setSumRating(int sumRating) {
		this.sumRating = sumRating;
	}
	
	@Transient
	public void updateRatingsCache()
	{
		//TODO make this called automatically
		int numRating =0;
		int totalRating =0;
		for(WorkflowRating tr : getRatings())
		{
			numRating++;
			totalRating += tr.getRating();
		}
		setNumRating(numRating);
		setSumRating(totalRating);
	}
	@Transient
	public Tool getLastTool()
	{
		return this.getTools().get(this.getTools().size() -1).getTool();
	}
	@Transient
	public String getLastToolName()
	{
		return this.getLastTool().getName();
	}
	@Transient
	public double getOverallRating() {
		if(getNumRating() == 0)
			return 0;
		else
			return (double) getSumRating() / (double) getNumRating();
	}
	@Transient
	public void loadToolsFromCache()
	{
		if(getToolIds() != null && RuntimeEnvironmentSettings.tools != null)
		{
			ArrayList<WorkflowTool> ret = new ArrayList<WorkflowTool>();
			int j = 1;
			for(int i : getToolIds())
			{
				WorkflowTool t = new WorkflowTool();
				t.setOrder(j);
				t.setTool(RuntimeEnvironmentSettings.tools.get(i));
				t.setWorkflow(this);
				ret.add(t);
				j++;
			}
			tools = ret;
		}
	}
	private int[] toolIds = null;
	@Transient
	public int[] getToolIds() {
		return toolIds;
	}
	public void setToolIds(int[] toolIds) {
		this.toolIds = toolIds;
	}

	private int cachedParentId = -1;
	@Transient
	public int getCachedParentId() {
		return cachedParentId;
	}
	public void setCachedParentId(int cachedParentId) {
		this.cachedParentId = cachedParentId;
	}
	
	private int cachedChildrenCount;
	@Transient
	public int getCachedChildrenCount() {
		return cachedChildrenCount;
	}
	public void setCachedChildrenCount(int cachedChildrenCount) {
		this.cachedChildrenCount = cachedChildrenCount;
	}
	@Transient
	public Workflow slimDown()
	{
		Workflow w = new Workflow();
		w.setId(this.getId());
		w.setRatings(this.getRatings());
		int[] temp = new int[getTools().size()];
    	for(WorkflowTool t : getTools())
    	{
    		temp[t.getOrder()-1] = t.getTool().getId();
    	}
    	getRatings().size();
    	w.setRatings(getRatings());
		w.setToolIds(temp);
		w.setComments(getComments());
		w.setUsageCount(getUsageCount());
		w.setNumRating(getNumRating());
		w.setSumRating(getSumRating());
		w.setCreator(getCreator());
		if(getCachedParentId() < 0 && getParent() != null)
			w.setCachedParentId(getParent().getId());
		return w;
	}
	
	@Transient
	public Workflow slimDownTiny()
	{
		Workflow w = new Workflow();
		w.setId(this.getId());
		w.setRatings(this.getRatings());
		int[] temp = new int[getTools().size()];
    	for(WorkflowTool t : getTools())
    	{
    		temp[t.getOrder()-1] = t.getTool().getId();
    	}
		w.setToolIds(temp);
		w.setUsageCount(getUsageCount());
		w.setNumRating(getNumRating());
		w.setSumRating(getSumRating());
		if(getCachedParentId() < 0 && getParent() != null)
			w.setCachedParentId(getParent().getId());
		return w;
	}
}
