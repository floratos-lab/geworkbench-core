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
 * @version $Id: APSerializable.java,v 1.2 2009-11-02 22:42:08 jiz Exp $
 */
public class APSerializable implements Serializable {
	private static final long serialVersionUID = 6455427625940524515L;

	DSDataSet<? extends DSBioObject> currentDataSet = null;
	Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes = new HashMap<DSDataSet<? extends DSBioObject>, String>();
	Map<String, ListOrderedMap<String, Map<String, String>>> chipTypeToAnnotations = new HashMap<String, ListOrderedMap<String, Map<String, String>>>();

	HashMap<String, String> chiptypeMap = new HashMap<String, String>();

	Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap = new HashMap<String, ListOrderedMap<String, Vector<String>>>();
	ArrayList<String> chipTypes = new ArrayList<String>();
	MultiMap<String, String> GOIDToAffy = null;
	MultiMap<String, String> affyToGOID = null;

	public APSerializable(
			DSDataSet<? extends DSBioObject> currentDataSet2,
			Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes2,
			Map<String, ListOrderedMap<String, Map<String, String>>> chipTypeToAnnotations,
			HashMap<String, String> chiptypeMap,
			Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap,
			ArrayList<String> chipTypes, MultiMap<String, String> GOIDToAffy,
			MultiMap<String, String> affyToGOID) {
		this.currentDataSet = currentDataSet2;
		this.datasetToChipTypes = datasetToChipTypes2;
		this.chipTypeToAnnotations = chipTypeToAnnotations;
		this.chiptypeMap = chiptypeMap;
		this.geneNameMap = geneNameMap;
		this.chipTypes = chipTypes;
		this.GOIDToAffy = GOIDToAffy;
		this.affyToGOID = affyToGOID;
	}
}
