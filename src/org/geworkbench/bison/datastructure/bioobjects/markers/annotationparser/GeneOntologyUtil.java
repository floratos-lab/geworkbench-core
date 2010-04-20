package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GOTerm;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GeneOntologyTree;

// FIXME design of this class is problematic. probably we should re-do instead of maintain it. 
// cleaning-up for now to make other development easier because of the reference to AnnotationParser.

// this class is only referenced twice from cutenet.GenewaysRowInformation and GenewaysWidget
/**
 * Created by IntelliJ IDEA. User: xiaoqing Date: Feb 9, 2007 Time: 10:39:47 AM
 * To change this template use File | Settings | File Templates.
 * $Id: GeneOntologyUtil.java,v 1.1 2009-12-03 19:21:50 jiz Exp $
 */
public class GeneOntologyUtil {
	private static final String KINASE = "K";
	private static final String TF = "TF";
	private static final String PHOSPATASE = "P";
	private final static int KINASE_GOTERM_ID = 16301;
	private final static int TF_GOTERM_ID = 3700;
	private final static int PHOSPATASE_GOTERM_ID = 4721;
	
	private static GeneOntologyUtil geneOntologyUtil = new GeneOntologyUtil();
	private static GeneOntologyTree tree;
	
	static {
		try {
			tree = GeneOntologyTree.getInstance();

		} catch (Exception x) {
			x.printStackTrace();

		}
	}

	private GeneOntologyUtil() {

	}

	public String checkMarkerFunctions(DSGeneMarker dsGeneMarker) {
		String geneId = dsGeneMarker.getLabel();
		String[] goTerms = AnnotationParser.getInfo(geneId,
				AnnotationParser.GENE_ONTOLOGY_MOLECULAR_FUNCTION);

		if (goTerms != null) {
			for (String goTerm : goTerms) {
				String goIdStr = goTerm.split("/")[0].trim();

				try {
					if (!goIdStr.equalsIgnoreCase("---")) {
						Integer goId = new Integer(goIdStr);
						if (goId != null) {
							for(GOTerm goterm: tree.getAncestors(goId)) {
								int gotermId = goterm.getId();
								if (gotermId==KINASE_GOTERM_ID) {
									return KINASE;
								} else if (gotermId==TF_GOTERM_ID) {
									return TF;
								} else if (gotermId==PHOSPATASE_GOTERM_ID) {
									return PHOSPATASE;
								}
							}
						}
					}
				} catch (NumberFormatException ne) {
					ne.printStackTrace();
				}

			}
		}

		// all other cases
		return "";
	}

	public static GeneOntologyUtil getOntologyUtil() {
		if (geneOntologyUtil == null) {
			geneOntologyUtil = new GeneOntologyUtil();
		}
		return geneOntologyUtil;

	}

	public Set<GOTerm> getAllGOTerms(DSGeneMarker dsGeneMarker) {
        String geneId = dsGeneMarker.getLabel();
        String[] goTerms = AnnotationParser.getInfo(geneId, AnnotationParser.GOTERM);
        if (goTerms != null) {
            Set<GOTerm> set = new HashSet<GOTerm>();
            for (String goTerm : goTerms) {
                String goIdStr = goTerm.split("/")[0].trim();
                if (!goIdStr.equalsIgnoreCase("---")) {
                    int goId = new Integer(goIdStr);
                    if(tree.getTerm(goId)!=null)
                    set.add(tree.getTerm(goId));
                }
            }
            return set;
        }

        return null;
	}
	
    public TreeMap<String, Set<GOTerm>> getAllGoTerms(DSGeneMarker dsGeneMarker, String catagory) {
        String geneId = dsGeneMarker.getLabel();
        String[] goTerms = AnnotationParser.getInfo(geneId, catagory);

        TreeMap<String, Set<GOTerm>> treeMap = new TreeMap<String, Set<GOTerm>>();
        if (goTerms != null) {

            for (String goTerm : goTerms) {
                String goIdStr = goTerm.split("/")[0].trim();
                try {
                    if (!goIdStr.equalsIgnoreCase("---")) {
                        Integer goId = new Integer(goIdStr);
                        if (goId != null) {
                            treeMap.put(goTerm, tree.getAncestors(goId));
                        }
                    }
                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }

            }
        }
        return treeMap;
    }

}
