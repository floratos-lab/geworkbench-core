package org.geworkbench.bison.datastructure.complex.pattern.matrix;

public class CSMatrixReduceExperiment implements DSMatrixReduceExperiment {

	private double pvalue;

	private double tvalue;

	private double coeff;

	private String psamId;

	private String name;
	
	private String experimentId;

	public CSMatrixReduceExperiment() {
		this(0, 0, 0, "0", "0");
	}

	public CSMatrixReduceExperiment(double pvalue, double tvalue, double coeff,
			String psamId, String experimentId) {
		this.pvalue = pvalue;
		this.tvalue = tvalue;
		this.coeff = coeff;
		this.psamId = psamId;
		this.experimentId = experimentId;
	}

	public double getPValue() {
		return pvalue;
	}

	public void setPValue(double pvalue) {
		this.pvalue = pvalue;
	}

	public double getTValue() {
		return tvalue;
	}

	public void setTValue(double tvalue) {
		this.tvalue = tvalue;
	}

	public double getCoeff() {
		return coeff;
	}

	public void setCoeff(double coeff) {
		this.coeff = coeff;
	}

	public String getPsamId() {
		return psamId;
	}

	public void setPsamId(String id) {
		psamId = id;
	}

	public String getLabel() {
		return name;
	}

	public void setLabel(String label) {
		name = label;
	}

	public void addNameValuePair(String name, Object value) {
	}

	public Object[] getValuesForName(String name) {
		return new Object[0];
	}

	public void forceUniqueValue(String name) {
	}

	public void allowMultipleValues(String name) {
	}

	public boolean isUniqueValue(String name) {
		return false;
	}

	public void clearName(String name) {
	}

	public void addDescription(String description) {
	}

	public String[] getDescriptions() {
		return new String[0];
	}

	public void removeDescription(String description) {
	}

	public String getID() {
		return experimentId;
	}

	public void setID(String id) {
		experimentId = id;
	}

	public int getSerial() {
		return 0;
	}

	public void setSerial(int serial) {
	}

	public boolean enabled() {
		return false;
	}

	public void enable(boolean status) {
	}

}
