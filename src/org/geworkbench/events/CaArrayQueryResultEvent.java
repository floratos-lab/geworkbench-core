package org.geworkbench.events;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

public class CaArrayQueryResultEvent extends Event {
	private boolean populated = false;
	private boolean succeed = true;
	private String infoType;
	private TreeMap<String, Set<String>> queryPairs;
	private String url;
	private int port;
	private String username;
	private String password;
	private static String GOTVALIDVALUES = "getvalidvalues";
	private static String GOTEXPERIMENTS = "getexperiments";
	private String errorMessage;

	public CaArrayQueryResultEvent(EventSource s, String url, int port,
			String username, String password) {
		super(s);

		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
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

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public TreeMap<String, Set<String>> getQueryPairs() {
		return queryPairs;
	}

	public void setQueryPairs(TreeMap<String, Set<String>> queryPairs) {
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
