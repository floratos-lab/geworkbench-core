package org.geworkbench.events;

import java.util.Set;
import java.util.TreeMap;

import org.geworkbench.engine.config.events.Event;

/**
 * 
 * @author zji
 * @version $Id$
 */

public class CaArrayQueryResultEvent extends Event {
	private boolean succeed = true;
	private TreeMap<String, Set<String>> queryPairs;

	private String errorMessage;

	public CaArrayQueryResultEvent() {
		super(null);
	}

	public boolean isSucceed() {
		return succeed;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public TreeMap<String, Set<String>> getQueryPairs() {
		return queryPairs;
	}

	public void setQueryPairs(TreeMap<String, Set<String>> queryPairs) {
		this.queryPairs = queryPairs;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
