package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * AdjacencyMatrix. <p>
 * 
 * @author not attributable
 * @version $Id$
 */

public class AdjacencyMatrix implements Serializable {

	private static final long serialVersionUID = -4163326138016520666L;

	private static Log log = LogFactory.getLog(AdjacencyMatrix.class);

	// TODO xxxRows and xxxInteractionRow should be merged.
	private HashMap<Integer, HashMap<Integer, Float>> geneRows = new HashMap<Integer, HashMap<Integer, Float>>();
	private HashMap<Integer, HashMap<Integer, String>> geneInteractionRows = new HashMap<Integer, HashMap<Integer, String>>();
	private HashMap<String, HashMap<String, Float>> geneRowsNotInMicroarray = new HashMap<String, HashMap<String, Float>>();
	private HashMap<String, HashMap<String, String>> geneInteractionRowsNotInMicroarray = new HashMap<String, HashMap<String, String>>();

	private Map<String, Integer> idToGeneMapper = new HashMap<String, Integer>();
	private Map<String, Integer> snToGeneMapper = new HashMap<String, Integer>();

	private final DSMicroarraySet<DSMicroarray> maSet;

	private final String name;

	private Map<String, String> interactionTypeSifMap = null;

	public AdjacencyMatrix(String name, final DSMicroarraySet<DSMicroarray> microarraySet) {
		this.name = name;
		maSet = microarraySet;
		log.debug("AdjacencyMatrix created with label "+name+" and microarray set "+maSet.getDataSetName());
	}

	public HashMap<Integer, HashMap<Integer, Float>> getGeneRows() {
		return this.geneRows;
	}

	public HashMap<String, HashMap<String, Float>> getGeneRowsNotInMicroarray() {
		return this.geneRowsNotInMicroarray;
	}

	public HashMap<Integer, HashMap<Integer, String>> getInteractionMap() {
		return this.geneInteractionRows;
	}

	public HashMap<String, HashMap<String, String>> getInteractionNotInMicroarrayMap() {
		return this.geneInteractionRowsNotInMicroarray;
	}

	/**
	 * Returns a map with all the edges to geneId.
	 * This is only used by MRA analysis.
	 * 
	 * @param geneId
	 *            int
	 * @return HashMap
	 */
	public HashMap<Integer, Float> get(int geneId) {
		geneId = getMappedId(geneId);
		if (geneId > 0) {
			return geneRows.get(new Integer(geneId));
		} else {
			return null;
		}
	}

	public void addGeneRow(int geneId) {
		HashMap<Integer, Float> row = geneRows.get(new Integer(geneId));
		if (row == null) {
			row = new HashMap<Integer, Float>();
			geneRows.put(new Integer(geneId), row);
		}
	}

	public String getLabel() {
		return name;
	}

	/**
	 * Adds and edge between geneId1 and geneId2
	 * 
	 * @param geneId1
	 *            int
	 * @param geneId2
	 *            int
	 * @param edge
	 *            float
	 */
	public void add(int geneId1, int geneId2, float edge) {
		geneId1 = getMappedId(geneId1);
		geneId2 = getMappedId(geneId2);

		HashMap<Integer, Float> row = geneRows.get(new Integer(geneId1));
		if (row == null) {
			row = new HashMap<Integer, Float>();
			geneRows.put(new Integer(geneId1), row);
		}
		row.put(new Integer(geneId2), new Float(edge));

		// doing it both ways; [gene2 -> (gene1, edge)]
		row = geneRows.get(new Integer(geneId2));
		if (row == null) {
			row = new HashMap<Integer, Float>();
			geneRows.put(new Integer(geneId2), row);
		}
		row.put(new Integer(geneId1), new Float(edge));

	}

	/**
	 * Adds and edge between geneId1 and geneId2
	 * 
	 * @param geneId1
	 *            String
	 * @param geneId2
	 *            String
	 * @param edge
	 *            float
	 */
	public void add(String geneId1, String geneId2,
			boolean isGene1InMicroarray, boolean isGene2InMicroarray, float edge) {

		if (isGene1InMicroarray == true)
			geneId1 = String.valueOf(getMappedId(new Integer(geneId1)));
		if (isGene2InMicroarray == true)
			geneId2 = String.valueOf(getMappedId(new Integer(geneId2)));

		HashMap<String, Float> row = (HashMap<String, Float>) geneRowsNotInMicroarray
				.get(geneId1);
		if (row == null) {
			row = new HashMap<String, Float>();
			geneRowsNotInMicroarray.put(geneId1, row);
		}
		row.put(new String(geneId2), new Float(edge));

		// doing it both ways; [gene2 -> (gene1, edge)]
		row = (HashMap<String, Float>) geneRowsNotInMicroarray.get(geneId2);
		if (row == null) {
			row = new HashMap<String, Float>();
			geneRowsNotInMicroarray.put(geneId2, row);
		}
		row.put(geneId1, new Float(edge));

	}

	/**
	 * 
	 * FIXME: this should handle both directions instead of letting the caller to call twice as it is now.
	 * @param geneId1
	 *            int
	 * @param geneId2
	 *            int
	 * @param interaction
	 *            String
	 */
	public void addDirectional(int geneId1, int geneId2, String interaction) {
		geneId1 = getMappedId(geneId1);
		geneId2 = getMappedId(geneId2);

		HashMap<Integer, String> row = (HashMap<Integer, String>) geneInteractionRows
				.get(new Integer(geneId1));
		if (row == null) {
			row = new HashMap<Integer, String>();
			geneInteractionRows.put(new Integer(geneId1), row);
		}
		row.put(new Integer(geneId2), interaction);

	}

	/**
	 * 
	 * FIXME: this should handle both directions instead of letting the caller to call twice as it is now.
	 * @param geneId1
	 *            String
	 * @param geneId2
	 *            String
	 * @param interaction
	 *            String
	 */
	public void addDirectional(String geneId1, String geneId2,
			boolean isGene1InMicroarray, boolean isGene2InMicroarray,
			String interaction) {

		if (isGene1InMicroarray == true)
			geneId1 = String.valueOf(getMappedId(new Integer(geneId1)));
		if (isGene2InMicroarray == true)
			geneId2 = String.valueOf(getMappedId(new Integer(geneId2)));

		HashMap<String, String> row = (HashMap<String, String>) geneInteractionRowsNotInMicroarray
				.get(new String(geneId1));
		if (row == null) {
			row = new HashMap<String, String>();
			geneInteractionRowsNotInMicroarray.put(new String(geneId1), row);
		}
		row.put(new String(geneId2), interaction);
	}

	/**
	 * Return an index to the microarray set's marker list.
	 * 
	 * If the index points to a marker that has a previously seen label (probeset ID)
	 * or a previously seen gene name (gene symbol), return the index of the previously seen one;
	 * if it is a new one, return the input.
	 * 
	 * @param geneId - an index to the microarray set's marker list
	 * @return an different index if the probeset or the gene symbol was seen before.
	 */
	public int getMappedId(int geneId) {
		if (geneId < 0 || geneId >= maSet.getMarkers().size())
			return geneId; // garbage in, garbage out

		DSGeneMarker gm = maSet.getMarkers().get(geneId);
		// bug 2000, replaced getShortName() with getGeneName()
		String geneName = gm.getGeneName();
		if (geneName == null || geneName.trim().length() == 0 || geneName.equals("---")) {
			return geneId;
		}

		String label = gm.getLabel();
		// Test if a gene with the same label was mapped before.
		Integer prevId = (Integer) idToGeneMapper.get(label);
		if (prevId != null) {
			// This gene was mapped before. Replace with mapped one
			return prevId.intValue();
		} 
		
		// Test if a gene with the same name was reported before.
		prevId = (Integer) snToGeneMapper.get(geneName);
		if (prevId != null) {
			// There was a previous gene with the same name.
			// add a new mapping to idToGeneMapper
			idToGeneMapper.put(label, prevId);
			return prevId;
		} else { // new name never seen before
			snToGeneMapper.put(geneName, new Integer(geneId));
			idToGeneMapper.put(label, new Integer(geneId));
			return geneId;
		}
	}

	// TODO: the only caller (AracneAnalysis) is only interested in the zero case
	public int getConnectionNo() {
		int connectionNo = 0;
		Iterator<HashMap<Integer, Float>> valuesIt = geneRows.values()
				.iterator();
		while (valuesIt.hasNext()) {
			HashMap<Integer, Float> geneRow = valuesIt.next();
			connectionNo += geneRow.size();
		}

		return connectionNo;
	}

	public DSMicroarraySet<DSMicroarray> getMicroarraySet() {
		return maSet;
	}

	public Map<String, String> getInteractionTypeSifMap() {
		return interactionTypeSifMap;
	}

	public void setInteractionTypeSifMap(Map<String, String> map) {		 
		interactionTypeSifMap = map;
	}

}
