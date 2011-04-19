package org.geworkbench.components.genspace.server.task;
import javax.ejb.Remote;

@Remote
public interface ManualRunnerRemote {
	void runWorkflowStats();
}
