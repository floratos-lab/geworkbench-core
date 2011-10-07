package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.Serializable;

class AnnotationFields implements Serializable {
	private static final long serialVersionUID = -3571880185587329070L;

	String getMolecularFunction() {
		return molecularFunction;
	}

	void setMolecularFunction(String molecularFunction) {
		this.molecularFunction = molecularFunction;
	}

	String getCellularComponent() {
		return cellularComponent;
	}

	void setCellularComponent(String cellularComponent) {
		this.cellularComponent = cellularComponent;
	}

	String getBiologicalProcess() {
		return biologicalProcess;
	}

	void setBiologicalProcess(String biologicalProcess) {
		this.biologicalProcess = biologicalProcess;
	}

	String getUniGene() {
		return uniGene;
	}

	void setUniGene(String uniGene) {
		this.uniGene = uniGene;
	}

	String getDescription() {
		return description;
	}

	void setDescription(String description) {
		this.description = description;
	}

	String getGeneSymbol() {
		return geneSymbol;
	}

	void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	String getLocusLink() {
		return locusLink;
	}

	void setLocusLink(String locusLink) {
		this.locusLink = locusLink;
	}

	String getSwissProt() {
		return swissProt;
	}

	void setSwissProt(String swissProt) {
		this.swissProt = swissProt;
	}

	public void setRefSeq(String refSeq) {
		this.refSeq = refSeq;
	}

	public String getRefSeq() {
		return refSeq;
	}

	private String molecularFunction, cellularComponent, biologicalProcess;
	private String uniGene, description, geneSymbol, locusLink, swissProt;
	private String refSeq;
}