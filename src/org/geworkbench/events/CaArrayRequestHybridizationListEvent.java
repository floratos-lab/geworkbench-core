package org.geworkbench.events;

import org.geworkbench.builtin.projects.remoteresources.carraydata.CaArray2Experiment;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

public class CaArrayRequestHybridizationListEvent extends Event {
	private String url;
	private int port;
	private String username;
	private String password;

	private CaArray2Experiment experiment;

	public CaArray2Experiment getExperiment() { return experiment; }

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param s
	 * @param url
	 * @param port
	 * @param username
	 * @param password
	 * @param experiment
	 */
	public CaArrayRequestHybridizationListEvent(EventSource s, String url,
			int port, String username, String password,
			CaArray2Experiment experiment) {
		super(s);
		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
		this.experiment = experiment;
	}
}
