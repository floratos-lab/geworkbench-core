package org.geworkbench.bison.datastructure.biocollections.pca;

import java.io.File;
import java.util.HashMap;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

/**
 * 
 * @author keshav
 * @version $Id: DSPCADataSet.java,v 1.1 2008-03-14 20:06:21 keshav Exp $
 */
@SuppressWarnings("unchecked")
public interface DSPCADataSet extends DSAncillaryDataSet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet#getDataSetFile()
	 */
	public abstract File getDataSetFile();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet#setDataSetFile(java.io.File)
	 */
	public abstract void setDataSetFile(File file);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.DSDataSet#writeToFile(java.lang.String)
	 */
	public abstract void writeToFile(String fileName);

	/**
	 * 
	 * @return
	 */
	public abstract int getNumPCs();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap getEigenValues();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap getPercentVars();

	/**
	 * 
	 * @return
	 */
	public abstract HashMap getEigenVectors();

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