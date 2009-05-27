package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
/**
 * Class containing MINDY run results.
 * @author mhall
 * @author oshteynb
 * @version $Id: MindyData.java,v 1.19 2009-05-27 15:31:22 oshteynb Exp $
 */
@SuppressWarnings("serial")
public class MindyData implements Serializable {

    static Log log = LogFactory.getLog(MindyData.class);

    // all arrays,
    @SuppressWarnings("unchecked")
	private CSMicroarraySet arraySet;
    // selected arrays
    private ArrayList<DSMicroarray> arrayForMindyRun;

	private DSGeneMarker transcriptionFactor;

    private HashMap<DSGeneMarker, TargetInfo> targetInfoMap = new HashMap<DSGeneMarker, TargetInfo>();

    // can go to global key repository, maybe related to a file
    private HashMap<DSGeneMarker, MindyGeneMarker> geneSortkeyMap = new HashMap<DSGeneMarker, MindyGeneMarker>();

    private HashMap<DSGeneMarker, ModulatorInfo> modulatorInfoMap = new HashMap<DSGeneMarker, ModulatorInfo>();

	/*  needed to display stats after markers filtering  */
    private HashMap<DSGeneMarker, ModulatorInfo> filteredModulatorInfoMap = new HashMap<DSGeneMarker, ModulatorInfo>();

    private float setFraction;
    private boolean annotated = false;

	public MindyData(CSMicroarraySet arraySet, ArrayList<DSMicroarray> arrayForMindyRun, float setFraction, DSGeneMarker transFac) {

        this.arraySet = arraySet;

        this.transcriptionFactor = transFac;

    	if ( arrayForMindyRun == null){
    		this.arrayForMindyRun = MindyData.createArrayForMindyRun(arraySet, null);
    	} else {
            this.arrayForMindyRun = arrayForMindyRun;
    	}

        this.setFraction = setFraction;
	}


	public void setFilteredModulatorInfoMap(
			HashMap<DSGeneMarker, ModulatorInfo> filteredModulatorInfoMap) {
		this.filteredModulatorInfoMap = filteredModulatorInfoMap;
	}

	public void initFilteredModulatorInfoMap() {
		this.filteredModulatorInfoMap = this.modulatorInfoMap;
	}

	public HashMap<DSGeneMarker, ModulatorInfo> getModulatorInfoMap() {
		return modulatorInfoMap;
	}

	/**
	 * used for sorting
	 *
	 * @param myCollator
	 * @param target
	 */
	public void addToSortkeyMap(Collator myCollator, DSGeneMarker target) {
		if (!geneSortkeyMap.containsKey(target)) {
			geneSortkeyMap.put(target, new MindyGeneMarker(target, myCollator
					.getCollationKey(target.getLabel()), myCollator
					.getCollationKey(target.getDescription())));
		}
	}

	/**
	 * used for sorting
	 *
	 * @param myCollator
	 * @param target
	 */
	public void addToTargetInfoMap(double correlation, DSGeneMarker target) {
		if (!targetInfoMap.containsKey(target)) {
			targetInfoMap.put(target, new TargetInfo(target, correlation));
		}
	}

	/**
	 * Pearson correlation between target and TF
	 *
	 * @param target
	 */
	public double calcPearsonCorrelation(DSGeneMarker target) {
		SimpleRegression sr = new SimpleRegression();

		int sizeArraySet = this.arraySet.size();
		for(int i = 0; i < sizeArraySet; i++){
			DSMicroarray ma = (DSMicroarray)this.arraySet.get(i);
			sr.addData(ma.getMarkerValue(target).getValue(), ma
					.getMarkerValue(getTranscriptionFactor())
					.getValue());
		}

		return sr.getR();
	}

@SuppressWarnings("unchecked")
public static ArrayList<DSMicroarray> createArrayForMindyRun(
			DSMicroarraySet<DSMicroarray> inSet, DSPanel arraySet) {
		ArrayList<DSMicroarray> arrayListForMindyRun = new ArrayList<DSMicroarray>();

		if ((arraySet != null) && (arraySet.size() > 0)) {
			int size = arraySet.size();
			for (int i = 0; i < size; i++) {
				DSMicroarray ma = (DSMicroarray) arraySet.get(i);
				arrayListForMindyRun.add(ma);
			}
		} else {
			for (DSMicroarray microarray : inSet) {
				arrayListForMindyRun.add(microarray);
			}
		}

		return arrayListForMindyRun;
	}

    public boolean isAnnotated(){
    	return this.annotated;
    }

    public void setAnnotated(boolean annotated){
    	this.annotated = annotated;
    }

    @SuppressWarnings("unchecked")
	public CSMicroarraySet getArraySet() {
        return arraySet;
    }

    public ArrayList<DSMicroarray> getArrayForMindyRun() {
		return arrayForMindyRun;
	}

    public ArrayList<MindyGeneMarker> convertToMindyGeneMarker(List<DSGeneMarker> list){
    	ArrayList<MindyGeneMarker> result = new ArrayList<MindyGeneMarker>(list.size());
    	for(DSGeneMarker m: list){
    		result.add(this.geneSortkeyMap.get(m));
    	}
    	return result;
    }

    public ArrayList<DSGeneMarker> convertToDSGeneMarker(List<MindyGeneMarker> list){
    	ArrayList<DSGeneMarker> result = new ArrayList<DSGeneMarker>(list.size());
    	for(MindyGeneMarker m: list){
    		result.add(m.getGeneMarker());
    	}
    	return result;
    }

    public int getDataSize(){
    	int size = 0;

        for (Map.Entry<DSGeneMarker, ModulatorInfo> entry : modulatorInfoMap.entrySet()) {
            size += entry.getValue().getDataSize();
        }

        return size;
    }

    public int getFilteredDataSize(){
    	int size = 0;

        for (Map.Entry<DSGeneMarker, ModulatorInfo> entry : filteredModulatorInfoMap.entrySet()) {
            size += entry.getValue().getDataSize();
        }

        return size;
    }

    /**
     * Get the statics for the specified modulator.
     *
     * @param modulator - modulator for which to get the statistics
     * @return - ModulatorStatistics object
     */
    public ModulatorStatistics getStatistics(DSGeneMarker modulator) {
        return modulatorInfoMap.get(modulator).getModStat();
    }

    public ModulatorStatistics getFilteredStatistics(DSGeneMarker modulator) {
        return filteredModulatorInfoMap.get(modulator).getModStat();
    }

    /**
     * Get the transcription factor specified for MINDY data.
     *
     * @return the transcription factor gene marker
     */
    public DSGeneMarker getTranscriptionFactor() {
        return transcriptionFactor;
    }

    /**
     * Get the fraction of the sample to display on the heat map.
     *
     * @return fraction of the sample to display
     */
    public float getSetFraction(){
    	return this.setFraction;
    }

    /**
     * Get the list of mondulators.
     *
     * @return list of modulators
     */
    public List<DSGeneMarker> getModulators() {
        ArrayList<DSGeneMarker> modulators = new ArrayList<DSGeneMarker>();
        for (Map.Entry<DSGeneMarker, ModulatorInfo> entry : modulatorInfoMap.entrySet()) {
            modulators.add(entry.getKey());
        }
        return modulators;
    }

    /**
     * Get the list of MINDY result rows
     *
     * @param modulator
     * @return list of MINDY result rows
     */
    public List<MindyResultRow> getRows(DSGeneMarker modulator) {
    	return modulatorInfoMap.get(modulator).getData();
    }

    public boolean isEmpty() {
		if (modulatorInfoMap.size() <= 0) {
			return true;
		} else {
			return false;
		}
    }

    /**
     * Get the list of MINDY result rows
     *
     * @param modulator
     * @param limitTargets - target marker set being displayed
     * @return list of MINDY result rows one row for each marker, all rows for modulator if  limitTargets is null
     */
    public List<MindyResultRow> getRows(DSGeneMarker modulator,
			List<DSGeneMarker> limitTargets) {
		List<MindyResultRow> results;

		if (limitTargets == null) {
			results = getRows(modulator);
		} else {
			results = new ArrayList<MindyResultRow>();
			ModulatorInfo modulatorInfo = modulatorInfoMap.get(modulator);
			for (DSGeneMarker marker : limitTargets) {
				MindyResultRow row = modulatorInfo.getRow(marker);
				if (row != null) {
					results.add(row);
				}
			}
		}

		return results;
	}


    /**
     * Pearson correlation between the transcription factor and the target gene.
     * Used primarily for the heat map.
     * @return result of Pearson correlation
     */
    public double getCorrelation(DSGeneMarker target){
    	return this.targetInfoMap.get(target).getCorrelation();
    }

    /**
     * Gene name CollationKey that is used for sorting
     *
     * @return CollationKey
     */
    public CollationKey getGeneNameSortKey(DSGeneMarker target){
    	return this.geneSortkeyMap.get(target).getNameSortKey();
    }

    /**
     * Get a list of targets for one modulator
     *
     * @param modulator
     * @return list of targets
     */
    public List<DSGeneMarker> getTargets(DSGeneMarker modulator) {
        List<DSGeneMarker> targets = new ArrayList<DSGeneMarker>();
        List<MindyResultRow> rows = getRows(modulator);
        for (MindyResultRow mindyResultRow : rows) {
            targets.add(mindyResultRow.getTarget());
        }
        return targets;
    }

    /**
     * Get a list of targets for all modulator in the list
     *
     * @param modulatorsList
     * @return list of targets
     */
    public List<DSGeneMarker> getTargets(List<DSGeneMarker> modulatorsList) {
        HashSet<DSGeneMarker> targetsSet = new HashSet<DSGeneMarker>();
        for (DSGeneMarker mod : modulatorsList) {
        	HashSet<DSGeneMarker>  tmpTargets =  new HashSet<DSGeneMarker>( getTargets(mod));
        	targetsSet.addAll(tmpTargets);
        }

        List<DSGeneMarker> targets = new ArrayList<DSGeneMarker>(targetsSet);
        return targets;
    }


    /**
     * Get the score
     *
     * @param modulator
     * @param target
     *
     * @return the score used in MINDY data
     */
    public float getScore(DSGeneMarker modulator, DSGeneMarker target) {
    	float result = modulatorInfoMap.get(modulator).getScore(target);
    	return result;
    }


//////////////////////////////////////////////////////////////////////////////////////
///    refactoring stuff that is here for legacy code and should eventually go away or changed
////////////////////////////////////////////////////////////////////////


    // tmp for refactoring two ctr, just for compiling
    // used by MindyResultsParser, probably testing
    /**
     * Constructor.
     *
     * @param arraySet - microarray set
     * @param data - list of MINDY result rows
     * @param setFraction - Sample per Condition in fraction
     */
    @SuppressWarnings("unchecked")
	public MindyData(CSMicroarraySet arraySet, List<MindyResultRow> data, float setFraction) {
        this.arraySet = arraySet;
//        this.data = data;
        this.setFraction = setFraction;
//        calculateModulatorInfo(false);


    }

    @SuppressWarnings("unchecked")
	public MindyData(CSMicroarraySet arraySet, List<MindyResultRow> data) {
        this.arraySet = arraySet;
//        this.data = data;
//        calculateModulatorInfo(false);
    }

    // end - tmp for refactoring two ctr
}
