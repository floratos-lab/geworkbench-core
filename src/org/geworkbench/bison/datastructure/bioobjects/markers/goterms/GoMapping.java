package org.geworkbench.bison.datastructure.bioobjects.markers.goterms;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.util.*;

/**
 * Represents a mapping between markers of a Microarray and GO Terms.
 *
 * @author John Watkinson
 */
public class GoMapping {

    private static final String GO_ANNOTATION_PREFIX = "Gene Ontology";

    private static final String EMPTY_ANNOTATION = "---";
    private static final String ANNOTATION_SEPARATOR = "///";

    private HashMap<Integer, Set<String>> goToMarker;
    private HashMap<String, Set<Integer>> markerToGo;

    private GeneOntologyTree tree;

    private void mapGoToMarker(int go, String marker) {
        Set<String> markers = goToMarker.get(go);
        if (markers == null) {
            markers = new HashSet<String>();
            goToMarker.put(go, markers);
        }
        markers.add(marker);
    }

    private void mapMarkerToGo(int go, String marker) {
        Set<Integer> goTerms = markerToGo.get(marker);
        if (goTerms == null) {
            goTerms = new HashSet<Integer>();
            markerToGo.put(marker, goTerms);
        }
        goTerms.add(go);
    }

    /**
     * Constructs a mapping of the markers in the microarray set and the GO Tree.
     * Custom annotations relating to the GO Ontology are sought and used for the mapping.
     */
    public GoMapping(GeneOntologyTree tree, DSMicroarraySet microarraySet) {
        this.tree = tree;
        goToMarker = new HashMap<Integer, Set<String>>();
        markerToGo = new HashMap<String, Set<Integer>>();
        DSItemList<DSGeneMarker> markers = microarraySet.getMarkers();
        Set<String> annotations = AnnotationParser.getCustomAnnotations(microarraySet);
        for (String annotation : annotations) {
            if (annotation.startsWith(GO_ANNOTATION_PREFIX)) {
                // It's a GO annotation
                // Iterate over all markers
                for (DSGeneMarker marker : markers) {
                    String item = marker.getLabel();
                    String value = AnnotationParser.getCustomAnnotationValue(annotation, item, microarraySet);
                    if (value == null) {
                        value = EMPTY_ANNOTATION;
                    }
                    if (value.startsWith(EMPTY_ANNOTATION)) {
                        // No GO terms
                        continue;
                    } else {
                        String[] terms = value.split("///");
                        for (String term : terms) {
                            StringTokenizer st = new StringTokenizer(term);
                            String idString = st.nextToken();
                            if (idString != null) {
                                try {
                                    int id = Integer.parseInt(idString);
                                    mapGoToMarker(id, item);
                                    mapMarkerToGo(id, item);
                                } catch (NumberFormatException nfe) {
                                    System.out.println("Warning: poorly formed GO annotation: " + term);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the GO Terms for the given marker ID. The GO Terms associated with the marker as well as the
     * ancestors of those GO Terms will be included.
     */
    public Set<GOTerm> getGOTermsForMarker(String markerID) {
        Set<Integer> directTerms = markerToGo.get(markerID);
        Set<GOTerm> terms = new HashSet<GOTerm>();
        if (directTerms != null) {
            for (Integer id : directTerms) {
                terms.addAll(tree.getAncestors(id));
            }
        }
        return terms;
    }

    /**
     * Retrieves the GO Terms for the given marker ID. Only the GO Terms that are directly associated with the marker
     * will be included.
     */
    public Set<GOTerm> getDirectGOTermsForMarker(String markerID) {
        Set<Integer> directTerms = markerToGo.get(markerID);
        Set<GOTerm> terms = new HashSet<GOTerm>();
        if (directTerms != null) {
            for (Integer id : directTerms) {
                terms.add(tree.getTerm(id));
            }
        }
        return terms;
    }

    /**
     * Retrieves the markers that are associated with the given GO Term ID. Markers that are associated with a child
     * GO Term of the given ID will also be included.
     */
    public Set<String> getMarkersForGOTerm(int goID) {
        Set<GOTerm> terms = tree.getChildren(goID);
        Set<String> markers = new HashSet<String>();
        for (GOTerm term : terms) {
            Set<String> relatedMarkers = goToMarker.get(term.getId());
            if (relatedMarkers != null) {
                markers.addAll(relatedMarkers);
            }
        }
        return markers;
    }

    /**
     * Retrieves the markers that are associated with the given GO Term ID. Only those markers that are directly
     * associated with the GO Term will be included.
     */
    public Set<String> getDirectMarkersForGOTerm(int goID) {
        Set<String> markers = goToMarker.get(goID);
        if (markers == null) {
            return Collections.EMPTY_SET;
        } else {
            return markers;
        }
    }
}
