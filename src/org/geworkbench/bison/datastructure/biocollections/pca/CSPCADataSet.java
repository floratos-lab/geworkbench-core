/*
  The Broad Institute
  SOFTWARE COPYRIGHT NOTICE AGREEMENT
  This software and its documentation are copyright (2003-2007) by the
  Broad Institute/Massachusetts Institute of Technology. All rights are
  reserved.

  This software is supplied without any warranty or guaranteed support
  whatsoever. Neither the Broad Institute nor MIT can be responsible for its
  use, misuse, or functionality.
 */
package org.geworkbench.bison.datastructure.biocollections.pca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * @author: Marc-Danie Nazaire
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class CSPCADataSet extends CSAncillaryDataSet<DSBioObject> implements DSPCADataSet {

	private static final long serialVersionUID = 1L;

	static Log log = LogFactory.getLog(CSPCADataSet.class);
	private float[][] u_Matrix;
	private int numPCs;
	private HashMap<Integer, Double> eigenValues;
	private HashMap<Integer, String> percentVariations;
	private HashMap<Integer, List<String>> eigenVectors;
	private String variables;

	@SuppressWarnings("rawtypes")
	public CSPCADataSet(DSDataSet<? extends DSMicroarray> parent, String label, String variables,
			int numPCs, float[][] u_Matrix, HashMap<Integer, Double> eigenValues,
			HashMap<Integer, List<String>> eigenVectors, HashMap<Integer, String> percentVariations) {
		super((DSDataSet) parent, label);
		// this.pcaData = pcaData;
		this.variables = variables;
		this.numPCs = numPCs;
		this.u_Matrix = u_Matrix;
		this.eigenValues = eigenValues;
		this.eigenVectors = eigenVectors;
		this.percentVariations = percentVariations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getNumPCs()
	 */
	public int getNumPCs() {
		return numPCs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getEigenValues()
	 */
	public HashMap<Integer, Double> getEigenValues() {
		return eigenValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getPercentVars()
	 */
	public HashMap<Integer, String> getPercentVars() {
		return percentVariations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getEigenVectors()
	 */
	public HashMap<Integer, List<String>> getEigenVectors() {
		return eigenVectors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getUMatrix()
	 */
	public float[][] getUMatrix() {
		return u_Matrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#getVariables()
	 */
	public String getVariables() {
		return variables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.biocollections.pca.DSPCADataSet#writeToFile(java.lang.String)
	 */
	public void writeToFile(String fileName) {
		File file = new File(fileName);

		try {
			file.createNewFile();
			if (!file.canWrite()) {
				JOptionPane.showMessageDialog(null,
						"Cannot write to specified file.");
				return;
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String lineSeparator = System.getProperty("line.separator");
			for (int i = 1; i <= getNumPCs(); i++) {
				writer.write("Principal Component:\t" + i);
				writer.write(lineSeparator);
				writer.write("Eigenvalue:\t"
						+ getEigenValues().get(new Integer(i)));
				writer.write(lineSeparator);
				writer.write("Percentage Variation:\t"
						+ getPercentVars().get(new Integer(i)));
				writer.write(lineSeparator);
				writer.write("Eigenvector:\t"
						+ getEigenVectors().get(new Integer(i)));
				writer.write(lineSeparator);
				writer.write(lineSeparator);
			}

			writer.flush();
			writer.close();
		} catch (IOException io) {
			log.error(io);
		}
	}
}
