package org.geworkbench.components.genspace.server;

import java.util.List;

import org.geworkbench.components.genspace.entity.AnalysisEvent;
import org.geworkbench.components.genspace.entity.Tool;
import org.geworkbench.components.genspace.entity.Transaction;
import org.geworkbench.components.genspace.entity.User;
import org.geworkbench.components.genspace.entity.Workflow;

public interface ToolInformationProvider {
	
	/**
	 * Get a list of tools, ordered by popularity
	 * @return
	 */
	public List<Tool> getToolsByPopularity();

	/**
	 * Get a list of all workflows, ordered by popularity
	 * @return
	 */
	public List<Workflow> getWorkflowsByPopularity();

	/**
	 * Get the most popular tools for starting new workflows
	 * @return
	 */
	public List<Tool> getMostPopularWFHeads();

	public Tool getMostPopularNextTool(int toolID);

	public Tool getMostPopularPreviousTool(int toolId);
	public List<Tool> getAllTools();
	public List<Workflow> getMostPopularWorkflowStartingWith(int toolId);
	public List<Workflow> getMostPopularWorkflowIncluding(int toolId);
	public List<Workflow> getAllWorkflowsIncluding(int toolId);
	public List<Workflow> getToolSuggestion(int workflowID);
	
	public byte[] sendUsageSingleEvent(byte[] analysisEvent);
	public byte[] sendMultipeEvents(byte[] analysisEvent);
	
	public Transaction sendUsageEvent(AnalysisEvent e);
	public Transaction sendUsageLog(List<AnalysisEvent> e);
	public User getExpertUserFor(int toolId);
}
