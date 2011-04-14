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

	public Tool getMostPopularNextTool(Tool tool);

	public Tool getMostPopularPreviousTool(Tool tool);
	public List<Tool> getAllTools();
	public List<Workflow> getMostPopularWorkflowStartingWith(Tool tool);
	public List<Workflow> getMostPopularWorkflowIncluding(Tool tool);
	public List<Workflow> getAllWorkflowsIncluding(Tool tool);
	public List<Workflow> getToolSuggestion(Workflow cwf);
	public Transaction sendUsageEvent(AnalysisEvent e);
	public Transaction sendUsageLog(List<AnalysisEvent> e);
	public User getExpertUserFor(Tool tn);
}
