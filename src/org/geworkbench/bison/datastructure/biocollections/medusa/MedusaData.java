package org.geworkbench.bison.datastructure.biocollections.medusa;

import java.io.Serializable;
import java.util.ArrayList;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * 
 * @author keshav
 * @version $Id: MedusaData.java,v 1.6 2007-07-10 20:09:56 keshav Exp $
 */
public class MedusaData implements Serializable {

	private static final long serialVersionUID = 1L;

	private DSMicroarraySet<DSMicroarray> arraySet;

	// private List<RulesBean> rulesBeans = null; TODO - get from Medusa team

	private ArrayList<DSGeneMarker> regulators = null;

	private ArrayList<DSGeneMarker> targets = null;

	private MedusaCommand medusaCommand = null;

	/**
	 * 
	 * @param arraySet
	 * @param regulators
	 * @param targets
	 */
	public MedusaData(DSMicroarraySet<DSMicroarray> arraySet, ArrayList<DSGeneMarker> regulators,
			ArrayList<DSGeneMarker> targets, MedusaCommand medusaCommand) {
		this.arraySet = arraySet;
		this.regulators = regulators;
		this.targets = targets;
		this.medusaCommand = medusaCommand;
	}

	/**
	 * 
	 * @return {@link DSMicroarraySet}
	 */
	public DSMicroarraySet<DSMicroarray> getArraySet() {
		return arraySet;
	}

	/**
	 * 
	 * @param arraySet
	 */
	public void setArraySet(CSMicroarraySet<DSMicroarray> arraySet) {
		this.arraySet = arraySet;
	}

	/**
	 * 
	 * @return List<DSGeneMarker>
	 */
	public ArrayList<DSGeneMarker> getRegulators() {
		return regulators;
	}

	/**
	 * 
	 * @return List<DSGeneMarker>
	 */
	public ArrayList<DSGeneMarker> getTargets() {
		return targets;
	}

	public MedusaCommand getMedusaCommand() {
		return medusaCommand;
	}
}
