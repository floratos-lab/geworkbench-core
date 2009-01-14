package org.geworkbench.builtin.projects.remoteresources.carraydata;

import java.io.Serializable;
import java.util.Map;

public class CaArray2Experiment implements Comparable<CaArray2Experiment>,
		Serializable {
	private static final long serialVersionUID = 6474502004023481576L;

	private String name;
	private Map<String, Long> hybridizations;
	private String description;
	private String[] QuantitationTypes;
	private String url;
	private int portNumber;
	private String user;
	private String password;
	boolean populated;
	boolean hasData;
	boolean popSelection;

	public CaArray2Experiment(String _url, int _port) {
		this.url = _url;
		this.portNumber = _port;
	}

	public boolean isPopulated() {
		return populated;
	}

	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	public boolean isHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Long> getHybridizations() {
		return hybridizations;
	}

	public void setHybridizations(Map<String, Long> hybridizations) {
		this.hybridizations = hybridizations;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String describilization) {
		this.description = describilization;
	}

	public String[] getQuantitationTypes() {
		return QuantitationTypes;
	}

	public void setQuantitationTypes(String[] quantitationTypes) {
		QuantitationTypes = quantitationTypes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int compareTo(CaArray2Experiment o) {
		return name.compareTo(((CaArray2Experiment) o).getName());
	}

}
