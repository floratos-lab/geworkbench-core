package org.geworkbench.bison.datastructure.bioobjects;

import java.io.File;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
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

	public IdeaResult(final DSMicroarraySet<DSMicroarray> maSet, String string,
			List<IdeaEdge> locList, List<IdeaEdge> gocList, List<IdeaProbeGene> sigGeneList) {
		super(maSet, string);
		
		this.locList = locList;
		this.gocList = gocList;
		this.sigGeneList = sigGeneList;
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
}
