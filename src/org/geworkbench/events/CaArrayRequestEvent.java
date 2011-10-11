package org.geworkbench.events;

import java.util.Map;
import java.util.SortedMap;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.engine.config.events.Event;

public class CaArrayRequestEvent extends Event {
	public static final String CANCEL = "CANCEL";
	public static final String EXPERIMENT = "EXP";
	public static final String BIOASSAY = "BIOASSAY";
	public static String searchcritia = "selection";
	private boolean populated = false;
	private boolean succeed = true;
	private Map<String, String> filterCrit;
	private SortedMap<String, String> assayNameFilter;
	private DSDataSet<? extends DSBioObject> dataSet = null;
	private String url;
	private int port;
	private String requestItem;
	private String qType;
	private boolean queryExperiment = false;
	private boolean useFilterCrit;

	private String username;
	private String password;

	public CaArrayRequestEvent(String _url, int _port) {
		super(null);
		url = _url;
		port = _port;
	}

	public String getQType() {
		return qType;
	}

	public void setQType(String type) {
		qType = type;
	}

	public Map<String, String> getFilterCrit() {
		return filterCrit;
	}

	public void setFilterCrit(Map<String, String> filterCrit) {
		this.filterCrit = filterCrit;
	}

	public SortedMap<String, String> getAssayNameFilter() {
		return assayNameFilter;
	}

	public void setAssayNameFilter(
			SortedMap<String, String> assayNameFilter) {
		this.assayNameFilter = assayNameFilter;
	}

	public String getRequestItem() {
		return requestItem;
	}

	public void setRequestItem(String requestItem) {
		this.requestItem = requestItem;
	}

	public boolean isPopulated() {
		return populated;
	}

	public boolean isQueryExperiment() {
		return queryExperiment;
	}

	public void setQueryExperiment(boolean queryExperiment) {
		this.queryExperiment = queryExperiment;
	}

	public boolean isSucceed() {
		return succeed;
	}

	public String getUrl() {
		return url;
	}

	public int getPort() {
		return port;
	}

	public DSDataSet<? extends DSBioObject> getDataSet() {
		return dataSet;
	}

	public boolean isUseFilterCrit() {
		return useFilterCrit;
	}

	public void setUseFilterCrit(boolean useFilterCrit) {
		this.useFilterCrit = useFilterCrit;
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
}
