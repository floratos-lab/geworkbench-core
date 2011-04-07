package org.geworkbench.bison.datastructure.biocollections.pca;

import java.util.HashMap;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * 
 * @author keshav
 * @version $Id$
 */
public interface DSPCADataSet extends DSAncillaryDataSet<DSBioObject> {

	/**
	 * 
	 * @return
	 */
	public abstract int getNumPCs();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap<Integer, Double> getEigenValues();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap<Integer, String> getPercentVars();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap<Integer, List<String>> getEigenVectors();

	/**
	 * 
	 * @return
	 */
	public abstract float[][] getUMatrix();

	/**
	 * 
	 * @return
	 */
	public abstract String getVariables();
}