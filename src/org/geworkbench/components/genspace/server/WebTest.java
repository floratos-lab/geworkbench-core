package org.geworkbench.components.genspace.server;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.geworkbench.components.genspace.entity.Workflow;

@WebService
public interface WebTest {

	@WebMethod
	public List<Workflow> getWorkflowsByPopularity();
	
	@WebMethod
	public String sayHello();
}
