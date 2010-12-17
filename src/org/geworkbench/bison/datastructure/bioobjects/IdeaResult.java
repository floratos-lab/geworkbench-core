package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.IdeaEdge.InteractionType;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of IDEA analysis.
 * @author zji
 * @version $Id$
*/
public class IdeaResult extends CSAncillaryDataSet<DSMicroarray> {
	private static final long serialVersionUID = 1728642489420856774L;

	private List<IdeaEdge> locList = null;
	private List<IdeaEdge> gocList = null;
	private List<IdeaProbeGene> sigGeneList=null;
	private double pvalue=0.05;

	public IdeaResult(final DSMicroarraySet<DSMicroarray> maSet, String string,
			List<IdeaEdge> locList, List<IdeaEdge> gocList, List<IdeaProbeGene> sigGeneList, double pvalue) {
		super(maSet, string);
		
		this.locList = locList;
		this.gocList = gocList;
		this.sigGeneList = sigGeneList;
		this.pvalue=pvalue;
	}

	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}

	public List<IdeaEdge> getLocList() {
		return locList;
	}
	public List<IdeaEdge> getGocList() {
		return gocList;
	}

	public List<IdeaProbeGene> getSignificantGeneList() {
		return sigGeneList;
	}
	public List<String[]> getNodeList(){		
		List<String[]> nodeRows=new ArrayList<String[]>();
		
		for (IdeaProbeGene p : sigGeneList) {// present significant node with its
			// edges
			if ((p.getCumLoc() < pvalue) || (p.getCumGoc() < pvalue)) {
				ArrayList<IdeaEdge> pe=p.getEdges();
				for (IdeaEdge e : pe) {
					String isLoc = "";
					String isGoc = "";
					String ppi = "";
					if (e.isLoc())
						isLoc = "X";
					if (e.isGoc())
						isGoc = "X";
					if (e.getPpi() == InteractionType.PROTEIN_PROTEIN)
						ppi = "ppi";
					else if (e.getPpi() == InteractionType.PROTEIN_DNA)
						ppi = "pdi";
					String[] rowString=new String[5];
					rowString[0]=e.getProbeId1();
					rowString[1]=e.getProbeId2();
					rowString[2]=ppi;
					rowString[3]=isLoc;
					rowString[4]=isGoc;
					nodeRows.add(rowString);
				}
			}
		}
		return nodeRows;
	}
	
	public double getPvalue(){
		return pvalue;
	}
}
