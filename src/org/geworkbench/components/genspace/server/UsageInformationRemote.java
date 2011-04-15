package org.geworkbench.components.genspace.server;
import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.Tool;
import org.geworkbench.components.genspace.entity.ToolRating;
import org.geworkbench.components.genspace.entity.Workflow;
import org.geworkbench.components.genspace.entity.WorkflowRating;

@Remote
public interface UsageInformationRemote  extends ToolInformationProvider {

	
	public ToolRating getMyToolRating(int tool);
	public Tool saveRating(ToolRating tr);
	public WorkflowRating getMyWorkflowRating(int workflow);
	public Workflow saveRating(WorkflowRating tr);
}
