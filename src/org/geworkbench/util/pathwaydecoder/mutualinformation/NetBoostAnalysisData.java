package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;

public class NetBoostAnalysisData implements Serializable {
	/* VARIABLES */	
	private String edgelist = ""; // semicolon separated node pairs.  each node separated by a comma.  eg. "x1,y1;x2,y2;..."	
	private String selectedModels = ""; // comma separated model names  eg. "LPA,RDG,SMW"
	private String subgraphCounting = ""; // subgraph counting method:  "8 step walk" or "7 edges"
	
	private int trainingEx = 0;
	private int boostingIter = 0;
	private int crossValidFolds = 0;	
	
	
	/* CONSTRUCTORS */
	public NetBoostAnalysisData(){}	
	
	public NetBoostAnalysisData(String edgelist, String selectedModels, int numTrainingEx, int numBoostingIter, int crossValidationFolds, String subgraphCountMethod){
		this.edgelist = edgelist;
		this.selectedModels = selectedModels;
		this.trainingEx = numTrainingEx;
		this.boostingIter = numBoostingIter;
		this.crossValidFolds = crossValidationFolds;
		this.subgraphCounting = subgraphCountMethod;
	}
	
	
	/* METHODS */
	public String getEdgeList(){
		return this.edgelist;
	}
	
	public void setEdgeList(String edgelist){
		this.edgelist = edgelist;
	}
	
	public String getSelectedModels(){
		return this.selectedModels;
	}
	
	public void setSelectedModels(String selectedModels){
		this.selectedModels = selectedModels;
	}
	
	public int getNumberOfTrainingExamples(){
		return this.trainingEx;
	}
	
	public void setNumberOfTrainingExamples(int num){
		this.trainingEx = num;
	}
	
	public int getBoostingIterations(){
		return this.boostingIter;
	}
	
	public void setBoostingIterations(int iterations){
		this.boostingIter = iterations;
	}
	
	public int getCrossValidationFolds(){
		return this.crossValidFolds;
	}
	
	public void setCrossValidationFolds(int folds){
		this.crossValidFolds = folds;
	}
	
	public String getSubgraphCountingMethod(){
		return this.subgraphCounting;
	}
	
	public void setSubgraphCountingMethod(String method){
		this.subgraphCounting = method;
	}
}
