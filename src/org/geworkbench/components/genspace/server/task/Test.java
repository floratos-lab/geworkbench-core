package org.geworkbench.components.genspace.server.task;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Test {
	public static void main(String[] args) {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			ManualRunnerRemote r = (ManualRunnerRemote) ctx.lookup("org.geworkbench.components.genspace.server.task.ManualRunnerRemote");
			r.runWorkflowStats();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
