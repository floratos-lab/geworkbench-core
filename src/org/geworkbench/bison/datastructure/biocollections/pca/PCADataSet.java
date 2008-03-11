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

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;

/**
 * @author: Marc-Danie Nazaire
 */
@SuppressWarnings("unchecked")
public class PCADataSet extends CSAncillaryDataSet implements
		DSAncillaryDataSet {

	private static final long serialVersionUID = 1L;
	
	static Log log = LogFactory.getLog(PCADataSet.class);
	private float[][] u_Matrix;
	private int numPCs;
	private HashMap eigenValues;
	private HashMap percentVariations;
	private HashMap eigenVectors;
	private String variables;

	public PCADataSet(DSDataSet parent, String label, String variables,
			int numPCs, float[][] u_Matrix, HashMap eigenValues,
			HashMap eigenVectors, HashMap percentVariations) {
		super(parent, label);
		// this.pcaData = pcaData;
		this.variables = variables;
		this.numPCs = numPCs;
		this.u_Matrix = u_Matrix;
		this.eigenValues = eigenValues;
		this.eigenVectors = eigenVectors;
		this.percentVariations = percentVariations;
	}

	public File getDataSetFile() {
		// no-op
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}

	public int getNumPCs() {
		return numPCs;
	}

	public HashMap getEigenValues() {
		return eigenValues;
	}

	public HashMap getPercentVars() {
		return percentVariations;
	}

	public HashMap getEigenVectors() {
		return eigenVectors;
	}

	public float[][] getUMatrix() {
		return u_Matrix;
	}

	public String getVariables() {
		return variables;
	}

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
