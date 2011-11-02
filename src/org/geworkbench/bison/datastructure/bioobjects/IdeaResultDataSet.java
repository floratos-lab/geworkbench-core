package org.geworkbench.bison.datastructure.bioobjects;

import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * Result of IDEA analysis.
 * @author zm2165
 * @version $Id$
*/
public class IdeaResultDataSet extends CSAncillaryDataSet<DSMicroarray> {
	private static final long serialVersionUID = 1728642489420856774L;

	private List<IdeaGLoc> locList = null;
	private List<IdeaGLoc> gocList = null;
	private List<IdeaNode> nodeList=null;
	private List<IdeaModule> moduleList=null;
	private double pvalue=0.05;

	public IdeaResultDataSet(final DSMicroarraySet maSet, String string,
			List<IdeaGLoc> locList, List<IdeaGLoc> gocList, List<IdeaNode> nodeList, List<IdeaModule> moduleList,double pvalue) {
		super(maSet, string);
		
		this.locList = locList;
		this.gocList = gocList;
		this.nodeList = nodeList;
		this.moduleList=moduleList;
		this.pvalue=pvalue;
	}

	public List<IdeaGLoc> getLocList() {
		return locList;
	}
	public List<IdeaGLoc> getGocList() {
		return gocList;
	}

	public List<IdeaNode> getNodeList() {
		return nodeList;
	}
	
	public List<IdeaModule> getModuleList() {
		return moduleList;
	}
	
	public double getPvalue(){
		return pvalue;
	}
}
