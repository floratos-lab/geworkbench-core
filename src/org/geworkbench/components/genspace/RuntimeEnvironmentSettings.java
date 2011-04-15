package org.geworkbench.components.genspace;

import java.util.HashMap;

import org.geworkbench.components.genspace.entity.Tool;

public class RuntimeEnvironmentSettings {

	public static final String PROD_HOST = "genspace.cs.columbia.edu";
	public static final String DEVEL_HOST = "amos.cs.columbia.edu";
	public static final String LOCAL_HOST = "localhost";
	public static final String SERVER = DEVEL_HOST;
	public static final String XMPP_HOST = DEVEL_HOST;

	public static final String GS_WEB_ROOT_PROD = "http://bambi.cs.columbia.edu/";
	public static final String GS_WEB_ROOT_DEVEL = "http://lenox.cs.columbia.edu/genspace/";

	public static final String GS_WEB_ROOT = GS_WEB_ROOT_PROD;
	public static HashMap<Integer,Tool> tools = null;

}
