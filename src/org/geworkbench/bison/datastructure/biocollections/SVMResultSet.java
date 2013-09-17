package org.geworkbench.bison.datastructure.biocollections;

import java.util.List;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

public class SVMResultSet extends CSAncillaryDataSet<DSMicroarray>{

	private static final long serialVersionUID = -6487530369595384222L;
	private String[] classifications; 
	private PredictionModel predModel;
	private PredictionModel trainPredResultModel;
	private PredictionModel testPredResultModel;
	private DSPanel<DSMicroarray> casePanel;
	private DSPanel<DSMicroarray> controlPanel;
	private List<float[]> data;
	private String[] rowNames;
	private String[] columnNames;
	private String password;

	public SVMResultSet(DSDataSet<DSMicroarray> parent, String label, String[] classifications, PredictionModel model, 
			List<float[]> data, String[] rowNames, String[] columnNames,
			DSPanel<DSMicroarray> casePanel, DSPanel<DSMicroarray> controlPanel) {
		super(parent, label);
		this.classifications = classifications;
		this.predModel = model;
		this.casePanel = casePanel;
		this.controlPanel = controlPanel;
		this.data = data;
		this.rowNames = rowNames;
		this.columnNames = columnNames;
	}

	public String[] getClassifications() {
        return classifications;
    }
    public PredictionModel getPredictionModel(){
    	return predModel;
    }
	public DSPanel<DSMicroarray> getCasePanel(){
		return casePanel;
	}
	public DSPanel<DSMicroarray> getControlPanel(){
		return controlPanel;
	}
	public List<float[]> getData(){
		return data;
	}
	public String[] getRowNames(){
		return rowNames;
	}
	public String[] getColumnNames(){
		return columnNames;
	}

	public void setTrainPredResultModel(PredictionModel train){
		trainPredResultModel = train;
	}

	public PredictionModel getTrainPredResultModel(){
		return trainPredResultModel;
	}

	public void setTestPredResultModel(PredictionModel test){
		testPredResultModel = test;
	}

	public PredictionModel getTestPredResultModel(){
		return testPredResultModel;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}
}
