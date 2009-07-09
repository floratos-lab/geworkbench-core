package org.geworkbench.bison.datastructure.biocollections.gsea;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: nazaire
 * Date: Jul 7, 2009
 */
public interface DSGSEAResultDataSet extends DSAncillaryDataSet 
{
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
	public abstract String getReportFile();
}
