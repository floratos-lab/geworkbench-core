package org.geworkbench.builtin.projects.remoteresources.carraydata;

import java.io.Serializable;
import java.util.Map;

public class CaArray2Experiment implements Comparable<CaArray2Experiment>,
		Serializable {
	private static final long serialVersionUID = 6474502004023481576L;

	private String name;
	private Map<String, String> hybridizations;
	private String description;
	private String[] QuantitationTypes;

	public CaArray2Experiment(String name, String description,
			Map<String, String> hybridizations, String[] QuantitationTypes) {
		this.name = name;
		this.description = description;
		this.hybridizations = hybridizations;
		this.QuantitationTypes = QuantitationTypes;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getHybridizations() {
		return hybridizations;
	}

	public String getDescription() {
		return description;
	}

	public String[] getQuantitationTypes() {
		return QuantitationTypes;
	}

	public int compareTo(CaArray2Experiment o) {
		return name.compareTo(((CaArray2Experiment) o).getName());
	}
}
