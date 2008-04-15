package org.geworkbench.events;

import java.util.HashMap;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;

public class CaArrayQueryEvent extends Event {
	private boolean populated = false;
	private boolean succeed = true;
	private String infoType;
 	private HashMap <String, String> queryPairs;
 	private String[] queries;
 	private String url;
	private int port;
	private String  username;
	private String password;
	public static final String GOTVALIDVALUES = "getvalidvalues";
	public static final String GOTEXPERIMENTS = "getexperiments";
	
	public boolean isPopulated() {
		return populated;
	}
	public void setPopulated(boolean populated) {
		this.populated = populated;
	}
	public boolean isSucceed() {
		return succeed;
	}
	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}
	public String getInfoType() {
		return infoType;
	}
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	public HashMap<String, String> getQueryPairs() {
		return queryPairs;
	}
	public void setQueryPairs(HashMap<String, String> queryPairs) {
		this.queryPairs = queryPairs;
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
	
	
	public String[] getQueries() {
		return queries;
	}
	public void setQueries(String[] queries) {
		this.queries = queries;
	}
	public CaArrayQueryEvent(String url, int port,
			String username, String password, String infoType) {
		super(null);
		this.infoType = infoType;
		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	
}
