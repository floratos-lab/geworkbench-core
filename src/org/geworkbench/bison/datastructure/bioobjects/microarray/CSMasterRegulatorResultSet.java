package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.util.HashMap;
import java.util.Map;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

public class CSMasterRegulatorResultSet <T extends DSGeneMarker> extends CSAncillaryDataSet<DSMicroarray> implements DSMasterRagulatorResultSet<T> {
	private static final long serialVersionUID = -416598071322541982L;
	
	private HashMap<DSGeneMarker,DSItemList<DSGeneMarker>> TF2GenesInRegulon = new HashMap<DSGeneMarker,DSItemList<DSGeneMarker>>();
	private HashMap<DSGeneMarker,DSItemList<DSGeneMarker>> TF2GenesInTargetList = new HashMap<DSGeneMarker,DSItemList<DSGeneMarker>>();
	private HashMap<DSGeneMarker,Double> TF2PValue = new HashMap<DSGeneMarker,Double>();
	private HashMap<DSGeneMarker, Character> TF2Mode = new HashMap<DSGeneMarker, Character>();

	private final DSMicroarraySet maSet;
	private int markerCount = 0;

	private Map<DSGeneMarker, Double> values;
	private Map<DSGeneMarker, Integer> ranks;
	private double minVal = 0;
	private double maxVal = 0;

	public CSMasterRegulatorResultSet(final DSMicroarraySet parent, String label, int markerCount) {
		super(parent, label);
		this.maSet = parent;
		this.markerCount = markerCount;
	}
	
	public DSMicroarraySet getMicroarraySet(){
		return maSet;
	}
	
	public void setGenesInRegulon(DSGeneMarker TF, DSItemList<DSGeneMarker> markers) {
		TF2GenesInRegulon.put(TF, markers);
	}

	public void setGenesInTargetList(DSGeneMarker TF, DSItemList<DSGeneMarker> markers) {
		TF2GenesInTargetList.put(TF, markers);
	}

	public DSItemList<DSGeneMarker> getGenesInRegulon(DSGeneMarker TF) {
		return TF2GenesInRegulon.get(TF);
	}

	public DSItemList<DSGeneMarker> getGenesInTargetList(DSGeneMarker TF) {
		return TF2GenesInTargetList.get(TF);
	}

	public void setPValue(DSGeneMarker TF, double pValue) {
		TF2PValue.put(TF,pValue);
	}

	public double getPValue(DSGeneMarker TF) {
		return TF2PValue.get(TF).doubleValue();
	}

	public void setMode(DSGeneMarker TF, char mode) {
		TF2Mode.put(TF, mode);
	}

	public char getMode(DSGeneMarker TF){
		if (TF == null || TF2Mode.get(TF) == null) return 0;
		return TF2Mode.get(TF);
	}

	public DSItemList<DSGeneMarker> getActivators(){
		DSItemList<DSGeneMarker> result = new CSItemList<DSGeneMarker>();
		for (DSGeneMarker marker : TF2Mode.keySet()){
			if (TF2Mode.get(marker) == ACTIVATOR)
				result.add(marker);
		}
		return result;
	}
	public DSItemList<DSGeneMarker> getRepressors(){
		DSItemList<DSGeneMarker> result = new CSItemList<DSGeneMarker>();
		for (DSGeneMarker marker : TF2Mode.keySet()){
			if (TF2Mode.get(marker) == REPRESSOR)
				result.add(marker);
		}
		return result;
	}

	//here we assume user has already called setGenesInTargetList(), in that case, the keys will be the answer
	public DSItemList<DSGeneMarker> getTFs(){
		DSItemList<DSGeneMarker> result = new CSItemList<DSGeneMarker>();
		result.addAll(TF2GenesInTargetList.keySet());
		return result;
	}
	
	public int getMarkerCount() {
		return markerCount;
	}

	public void setValues(Map<DSGeneMarker, Double> values){
		this.values = values;
	}

	public double getValue(DSGeneMarker key) {
		if(values==null) return Double.NaN;
		
		Double v =  values.get(key);
		if(v==null) v = Double.NaN;
		return v;
	}
	
	public void setMinValue(double val)	{	minVal = val;	}

	public void setMaxValue(double val)	{	maxVal = val;	}

	public double getMinValue()			{	return minVal;	}

	public double getMaxValue()			{	return maxVal;	}

	public int getRank(DSGeneMarker marker) {
		if (ranks == null) return 0;
		Integer rank = ranks.get(marker);
		if (rank  == null) return 0;
		return rank;
	}

	public void setRanks(Map<DSGeneMarker, Integer> ranks) {
		this.ranks = ranks;
	}
	
}
