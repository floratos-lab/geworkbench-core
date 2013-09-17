package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.util.List;
import java.util.Vector;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * 
 * @author zji
 * @version $Id$
 *
 */
public class CSAlignmentResultSet extends CSAncillaryDataSet<DSBioObject>
		implements DSAlignmentResultSet {

	private static final long serialVersionUID = 3284079552620104447L;

	final private List<Vector<BlastObj>> blastDataSet;
	final private String summary;
	final private int totalHitCount;

	@SuppressWarnings( { "unchecked", "rawtypes" })
	public CSAlignmentResultSet(List<Vector<BlastObj>> blastDataSet,
			DSSequenceSet<? extends DSSequence> parentDataSet, String label) {
		super((DSDataSet) parentDataSet, label);
		this.blastDataSet = blastDataSet;

		int totalHitCount = 0;
		for (Vector<BlastObj> v : blastDataSet) {
			totalHitCount += v.size();
		}
		
		this.summary = "Total hits for all sequences: " + totalHitCount + ".";
		this.totalHitCount = totalHitCount;
	}

	@Override
	public List<Vector<BlastObj>> getBlastDataSet() {
		return blastDataSet;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public int getHitCount() {
		return totalHitCount;
	}

}
