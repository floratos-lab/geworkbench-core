package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

public class CSAlignmentResultSet extends CSAncillaryDataSet<DSBioObject>
		implements DSAlignmentResultSet {

	private static final long serialVersionUID = 3284079552620104447L;

	private File fastaFile = null;
	private File resultFile = null;

	final private List<Vector<BlastObj>> blastDataSet;
	final private String summary;
	final private int totalHitCount;

	/**
	 * @param fileName
	 * @param inputFile
	 * @param dataSet
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public CSAlignmentResultSet(String resultFile,
			List<Vector<BlastObj>> blastDataSet, String fastaFile,
			DSSequenceSet<? extends DSSequence> blastedParentdataSet,
			DSSequenceSet<? extends DSSequence> parentDataSet) {
		super((DSDataSet) parentDataSet, "BLAST Result");
		this.resultFile = new File(resultFile);
		this.blastDataSet = blastDataSet;

		int totalHitCount = 0;
		for (Vector<BlastObj> v : blastDataSet) {
			totalHitCount += v.size();
		}
		
		this.summary = "Total hits for all sequences: " + totalHitCount + ".";
		this.totalHitCount = totalHitCount;
		this.fastaFile = new File(fastaFile);
		this.blastedParentDataSet = blastedParentdataSet;

	}

	public DSSequenceSet<? extends DSSequence> getBlastedParentDataSet() {
		return blastedParentDataSet;
	}

	public void setBlastedParentDataSet(
			DSSequenceSet<? extends DSSequence> blastedParentDataSet) {
		this.blastedParentDataSet = blastedParentDataSet;
	}

	private DSSequenceSet<? extends DSSequence> blastedParentDataSet;

	/**
	 * isDirty
	 * 
	 * @return boolean
	 * @todo Implement this geaw.bean.microarray.MAMemoryStatus method
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * setDirty
	 * 
	 * @param boolean0
	 *            boolean
	 * @todo Implement this geaw.bean.microarray.MAMemoryStatus method
	 */
	public void setDirty(boolean boolean0) {
	}

	/**
	 * getDataSetName
	 * 
	 * @return String
	 * @todo Implement this medusa.components.projects.IDataSet method
	 */
	public String getDataSetName() {
		return resultFile.getName();
	}

	@Override
	public List<Vector<BlastObj>> getBlastDataSet() {
		return blastDataSet;
	}

	public File getDataSetFile() {

		return fastaFile;
	}

	public void setDataSetFile(File _file) {
		fastaFile = _file;
	}

	/**
	 * @param ads
	 *            IAncillaryDataSet
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object ads) {
		if (ads instanceof DSAncillaryDataSet) {
			return getDataSetName().equals(
					((DSAncillaryDataSet<DSBioObject>) ads).getDataSetName());
		} else {
			return false;
		}
	}

	/**
	 * getFile
	 * 
	 * @return File
	 */
	public File getFile() {
		return resultFile;
	}

	public String getSummary() {
		return summary;
	}

	public int getHitCount() {
		return totalHitCount;
	}

}
