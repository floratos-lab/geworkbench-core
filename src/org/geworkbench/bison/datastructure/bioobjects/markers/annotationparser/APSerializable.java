package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author John Watkinson
 */
public class APSerializable implements Serializable {

    DSDataSet currentDataSet = null;
    Map<DSDataSet, String> datasetToChipTypes = new HashMap<DSDataSet, String>();
    Map<String, ListOrderedMap<String, Map<String, String>>> chipTypeToAnnotations = new HashMap<String, ListOrderedMap<String, Map<String, String>>>();

    HashMap<DSDataSet, AnnotationParser.CustomAnnotations> customAnnotations = new HashMap<DSDataSet, AnnotationParser.CustomAnnotations>();

    HashMap chiptypeMap = new HashMap();

    Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap = new HashMap<String, ListOrderedMap<String, Vector<String>>>();
    ArrayList<String> chipTypes = new ArrayList<String>();
    MultiMap<String, String> GOIDToAffy = null;
    MultiMap<String, String> affyToGOID = null;

    public APSerializable(DSDataSet currentDataSet, Map<DSDataSet, String> datasetToChipTypes, Map<String, ListOrderedMap<String, Map<String, String>>> chipTypeToAnnotations, HashMap<DSDataSet, AnnotationParser.CustomAnnotations> customAnnotations, HashMap chiptypeMap, Map<String, ListOrderedMap<String, Vector<String>>> geneNameMap, ArrayList<String> chipTypes, MultiMap<String, String> GOIDToAffy, MultiMap<String, String> affyToGOID) {
        this.currentDataSet = currentDataSet;
        this.datasetToChipTypes = datasetToChipTypes;
        this.chipTypeToAnnotations = chipTypeToAnnotations;
        this.customAnnotations = customAnnotations;
        this.chiptypeMap = chiptypeMap;
        this.geneNameMap = geneNameMap;
        this.chipTypes = chipTypes;
        this.GOIDToAffy = GOIDToAffy;
        this.affyToGOID = affyToGOID;
    }
}
