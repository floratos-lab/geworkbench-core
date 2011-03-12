package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

/**
 * 
 * @author zji
 * @version $Id$
 */
public interface DSPrtDBResultSet extends DSAncillaryDataSet<DSSequence> {

    public DSSequenceSet<DSSequence> getParentDataSet();

}