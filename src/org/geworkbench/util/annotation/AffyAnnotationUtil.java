/**
 * 
 */
package org.geworkbench.util.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

/**
 * @author zji
 * @version $Id$
 * 
 */
// CellularNetworkKnowledgeWidget became extremely bloated, so I put more code
// out of it.
// more logical class design is preferred in long term
public class AffyAnnotationUtil {

	public static List<DSGeneMarker> getMarkersForGivenGeneId(
			DSMicroarraySet microarraySet, String gene) {

		List<DSGeneMarker> list = new ArrayList<DSGeneMarker>();

		for (DSGeneMarker marker : microarraySet.getMarkers()) {
			if (marker != null && marker.getLabel() != null) {
				Set<String> geneSet = AffyAnnotationUtil.getGeneIDs(marker
						.getLabel());
				if (geneSet.contains(gene)) {
					list.add(marker);
				}
			}
		}
		return list;
	}

	public static Map<String, List<Integer>> getGeneNameToMarkerIDMapping(
			DSMicroarraySet microarraySet) {
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		DSItemList<DSGeneMarker> markers = microarraySet.getMarkers();
		int index = 0;
		for (DSGeneMarker marker : markers) {
			if (marker != null && marker.getLabel() != null) {			 
				try {
					
					Set<String> geneNames = getGeneNames(marker.getLabel());							
					for (String s : geneNames) {
						List<Integer> list = map.get(s);
						if(list==null) {
							list = new ArrayList<Integer>();
							list.add(index);
							map.put(s, list);
						} else {
							list.add(index);
						}
					}
					index++;
				} catch (Exception e) {					 
					continue;
				}
			}
		}
		return map;
	}
	
	private static Set<String> getGeneNames(String markerID) {
		HashSet<String> set = new HashSet<String>();
		String[] ids = AnnotationParser.getInfo(markerID, AnnotationParser.GENE_SYMBOL);
		for (String s : ids) {
			set.add(s.trim());
		}
		return set;
	}

	public static Set<String> getGeneIDs(String markerID) {
		HashSet<String> set = new HashSet<String>();
		String[] ids = AnnotationParser.getInfo(markerID, AnnotationParser.LOCUSLINK);
		for (String s : ids) {
			set.add(s.trim());
		}
		return set;
	}
}
