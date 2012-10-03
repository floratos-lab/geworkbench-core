package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.io.Serializable;
import java.util.List;
import java.util.RandomAccess;
import java.util.Vector;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * @author John Watkinson
 * @version $Id$
 */
public interface DSAlignmentResultSet extends RandomAccess, Cloneable,
		DSAncillaryDataSet<DSBioObject>, Serializable {
	
	public DSSequenceSet<? extends DSSequence> getBlastedParentDataSet();
	public List<Vector<BlastObj>> getBlastDataSet(); 
	public String getSummary(); 
	public int getHitCount();

	 
}
