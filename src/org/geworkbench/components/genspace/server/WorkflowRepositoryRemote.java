package org.geworkbench.components.genspace.server;
import java.util.List;

import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.IncomingWorkflow;
import org.geworkbench.components.genspace.entity.UserWorkflow;
import org.geworkbench.components.genspace.entity.WorkflowComment;
import org.geworkbench.components.genspace.entity.WorkflowFolder;

@Remote
public interface WorkflowRepositoryRemote {

	boolean deleteMyWorkflow(int uw);

	WorkflowFolder addWorkflow(UserWorkflow uw, int folder);

	WorkflowFolder addWorkflow(byte[] uw, int folder);
	
	WorkflowFolder addFolder(WorkflowFolder folder);

	boolean deleteFromInbox(int wi);

	UserWorkflow addToRepository(int wi);

	boolean removeComment(int wc);

	WorkflowComment addComment(WorkflowComment wc);

	byte[] addComment(byte[] comment);
	
	UserWorkflow importWorkflow(UserWorkflow w);

	boolean sendWorkflow(IncomingWorkflow newW, String receiver);

	boolean sendWorkflowBytes(byte[] newWorkflow, String receiver);
	
	boolean deleteMyFolder(int folder);
	
	List<IncomingWorkflow> getIncomingWorkflows();
	
	byte[] getIncomingWorkflowsBytes();
	
}
