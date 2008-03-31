package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;
import java.util.*;
public class CaArrayRequestEvent extends Event {
	public static final String EXPERIMENT = "EXP";
	public static final String BIOASSAY = "BIOASSAY";
	public static String searchcritia = "selection";
	private boolean populated = false;
	private boolean succeed = true;
	private String infoType;
	private String parentName;
	private HashMap <String, String[]> filterCrit;
	private DSDataSet dataSet = null;
	private String url;
	private int port;
	private String requestItem;
	private String qType;
	
	
	public CaArrayRequestEvent(String _url, int _port){
		super(null);
		url = _url;
		port = _port;
	} 
	public CaArrayRequestEvent(String type, String name){
		super(null);
		infoType = type;
		parentName = name;
	}
	
	public CaArrayRequestEvent(String type, String name, HashMap<String, String[]> filters){
		this(type, name);
		filterCrit = filters;
	}

	
	public String getQType() {
		return qType;
	}
	public void setQType(String type) {
		qType = type;
	}
	public HashMap<String, String[]> getFilterCrit() {
		return filterCrit;
	}
	public void setFilterCrit(HashMap<String, String[]> filterCrit) {
		this.filterCrit = filterCrit;
	}
	public String getRequestItem() {
		return requestItem;
	}
	public void setRequestItem(String requestItem) {
		this.requestItem = requestItem;
	}
	public CaArrayRequestEvent(String type, String name, DSDataSet  data){
		this(type, name);
		dataSet = data;
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
	
	
}
