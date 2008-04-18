package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.builtin.projects.remoteresources.carraydata.CaArray2Experiment;
import org.geworkbench.engine.config.events.Event;
import java.util.*;

public class CaArrayEvent extends Event {
	public static final String EXPERIMENT = "EXP";
	public static final String BIOASSAY = "BIOASSAY";
	public static String searchcritia = "selection";
	private boolean populated = false;
	private boolean succeed = true;
	private String infoType;
	private String parentName;
	private HashMap<String, String> filterCrit;
	private DSDataSet dataSet = null;
	private String url;
	private int port;
	private String username;
	private String password;
	private String errorMessage;
	
	// private TreeMap<String, String[]> treeMap;
	// private TreeMap<String, String> experimentDesciptions;
	private CaArray2Experiment[] experiments;

	public CaArrayEvent(String _url, int _port) {
		super(null);
		url = _url;
		port = _port;
		infoType = EXPERIMENT;
	}

	public CaArrayEvent(String type, String name) {
		super(null);
		infoType = type;
		parentName = name;
		infoType = EXPERIMENT;
	}

	public CaArrayEvent(String type, String name,
			HashMap<String, String> filters) {
		this(type, name);
		filterCrit = filters;
		infoType = BIOASSAY;
	}

	public CaArrayEvent(String type, String name, DSDataSet data) {
		this(type, name);
		dataSet = data;
		infoType = BIOASSAY;
	}

	public boolean isPopulated() {
		return populated;
	}

	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	public boolean isSucceed() {
		return succeed;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// public TreeMap<String, String[]> getTreeMap() {
	// return treeMap;
	// }
	// public void setTreeMap(TreeMap<String, String[]> treeMap) {
	// this.treeMap = treeMap;
	// }
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public DSDataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DSDataSet dataSet) {
		this.dataSet = dataSet;
	}

	// public TreeMap<String, String> getExperimentDesciptions() {
	// return experimentDesciptions;
	// }
	// public void setExperimentDesciptions(
	// TreeMap<String, String> experimentDesciptions) {
	// this.experimentDesciptions = experimentDesciptions;
	// }
	public CaArray2Experiment[] getExperiments() {
		return experiments;
	}

	public void setExperiments(CaArray2Experiment[] experiments) {
		this.experiments = experiments;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
