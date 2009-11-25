package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author John Watkinson
 * @version $Id: APSerializable.java,v 1.3 2009-11-25 17:31:53 jiz Exp $
 */
public class APSerializable implements Serializable {
	private static final long serialVersionUID = 6455427625940524515L;

	DSDataSet<? extends DSBioObject> currentDataSet = null;
	Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes = new HashMap<DSDataSet<? extends DSBioObject>, String>();

	HashMap<String, String> chiptypeMap = new HashMap<String, String>();

	Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap = new HashMap<String, ListOrderedMap<String, Vector<String>>>();
	ArrayList<String> chipTypes = new ArrayList<String>();
	MultiMap<String, String> affyToGOID = null;
	
	Map<String, Map<String, String>> chipTypeToMoleculorFunctions = null;
	Map<String, Map<String, String>> chipTypeToUnigenes = null;
	Map<String, Map<String, String>> chipTypeToDescriptions = null;
	Map<String, Map<String, String>> chipTypeToGeneSymbols = null;
	Map<String, Map<String, String>> chipTypeToLocusLinks = null;
	Map<String, Map<String, String>> chipTypeToSwissProts = null;


	public APSerializable(
			DSDataSet<? extends DSBioObject> currentDataSet2,
			Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes2,
			HashMap<String, String> chiptypeMap,
			Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap,
			ArrayList<String> chipTypes,
			MultiMap<String, String> affyToGOID,
			Map<String, Map<String, String>> chipTypeToMoleculorFunctions,
			Map<String, Map<String, String>> chipTypeToUnigenes,
			Map<String, Map<String, String>> chipTypeToDescriptions,
			Map<String, Map<String, String>> chipTypeToGeneSymbols,
			Map<String, Map<String, String>> chipTypeToLocusLinks,
			Map<String, Map<String, String>> chipTypeToSwissProts) {
		this.currentDataSet = currentDataSet2;
		this.datasetToChipTypes = datasetToChipTypes2;
		this.chiptypeMap = chiptypeMap;
		this.geneNameMap = geneNameMap;
		this.chipTypes = chipTypes;
		this.affyToGOID = affyToGOID;
		
		this.chipTypeToMoleculorFunctions = chipTypeToMoleculorFunctions;
		this.chipTypeToUnigenes = chipTypeToUnigenes;
		this.chipTypeToDescriptions = chipTypeToDescriptions;
		this.chipTypeToGeneSymbols = chipTypeToGeneSymbols;
		this.chipTypeToLocusLinks = chipTypeToLocusLinks;
		this.chipTypeToSwissProts = chipTypeToSwissProts;

	}
}
