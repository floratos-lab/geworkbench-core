package org.geworkbench.components.genspace.server;
import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.Tool;
import org.geworkbench.components.genspace.entity.ToolRating;
import org.geworkbench.components.genspace.entity.Workflow;
import org.geworkbench.components.genspace.entity.WorkflowRating;

@Remote
public interface UsageInformationRemote  extends ToolInformationProvider {

	
	public ToolRating getMyToolRating(int tool);
	public Tool saveToolRating(int tool, int rating);
	public WorkflowRating getMyWorkflowRating(int workflow);
	public Workflow saveWorkflowRating(int workflow, int rating);
	public Tool getTool(int id);
	public Workflow getWorkflow(int id);
}
