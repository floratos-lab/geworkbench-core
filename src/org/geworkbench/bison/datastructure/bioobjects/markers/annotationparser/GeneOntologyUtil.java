package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GOTerm;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GeneOntologyTree;

/**
 * 
 * @version $Id$
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

}
