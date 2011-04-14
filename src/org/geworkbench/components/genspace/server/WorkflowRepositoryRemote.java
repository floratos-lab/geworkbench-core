package org.geworkbench.components.genspace.server;
import javax.ejb.Remote;

import org.geworkbench.components.genspace.entity.IncomingWorkflow;
import org.geworkbench.components.genspace.entity.UserWorkflow;
import org.geworkbench.components.genspace.entity.WorkflowComment;
import org.geworkbench.components.genspace.entity.WorkflowFolder;

@Remote
public interface WorkflowRepositoryRemote {

	boolean deleteMyWorkflow(UserWorkflow uw);

	WorkflowFolder addWorkflow(UserWorkflow uw, WorkflowFolder folder);

	WorkflowFolder addFolder(WorkflowFolder folder);

	boolean deleteFromInbox(IncomingWorkflow wi);

	UserWorkflow addToRepository(IncomingWorkflow wi);

	boolean removeComment(WorkflowComment wc);

	WorkflowComment addComment(WorkflowComment wc);

	UserWorkflow importWorkflow(UserWorkflow w);

	boolean sendWorkflow(IncomingWorkflow newW, String receiver);

	boolean deleteMyFolder(WorkflowFolder folder);

}
