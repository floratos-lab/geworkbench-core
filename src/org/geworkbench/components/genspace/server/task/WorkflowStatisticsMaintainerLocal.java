package org.geworkbench.components.genspace.server.task;
import javax.ejb.Local;

@Local
public interface WorkflowStatisticsMaintainerLocal {
	void calculateWorkflowUsage();
	void calculateToolUsage();
}
