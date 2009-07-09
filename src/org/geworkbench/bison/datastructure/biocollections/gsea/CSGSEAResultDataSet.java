package org.geworkbench.bison.datastructure.biocollections.gsea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nazaire
 * Date: Jul 7, 2009
 */
public class CSGSEAResultDataSet extends CSAncillaryDataSet implements DSGSEAResultDataSet {

	private static final long serialVersionUID = 1L;

	static Log log = LogFactory.getLog(CSGSEAResultDataSet.class);
	private String reportFile;


	public CSGSEAResultDataSet(DSDataSet parent, String label, String reportFile) {
		super(parent, label);
		this.reportFile = reportFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet#getDataSetFile()
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getDataSetFile()
	 */
	public File getDataSetFile() {
		// no-op
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet#setDataSetFile(java.io.File)
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.gsea.DSGSEAResultDataSet#setDataSetFile(java.io.File)
	 */
	public void setDataSetFile(File file) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.gsea.DSGSEAResultDataSet#getReportFile()
	 */
	public String getReportFile()
    {
		return reportFile;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.geworkbench.bison.datastructure.biocollections.gsea.DSGSEAResultDataSet#writeToFile(java.lang.String)
	 */
	public void writeToFile(String fileName)
    {
		File file = new File(fileName);

		try
        {
			file.createNewFile();
			if (!file.canWrite())
            {
				JOptionPane.showMessageDialog(null,
						"Cannot write to specified file.");
				return;
			}
		} catch (IOException io) {
			log.error(io);
		}
	}
}

