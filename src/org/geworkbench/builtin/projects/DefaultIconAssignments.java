package org.geworkbench.builtin.projects;

import org.geworkbench.bison.algorithm.classification.CSClassifier;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSProbeIntensityArray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSSignificanceResultSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSAlignmentResultSet;
import org.geworkbench.bison.datastructure.bioobjects.structure.DSProteinStructure;
import org.geworkbench.bison.datastructure.complex.pattern.PatternResult;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.DSMatrixReduceSet;
import org.geworkbench.bison.model.clusters.DSHierClusterDataSet;
import org.geworkbench.bison.model.clusters.DSSOMClusterDataSet;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrixDataSet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.EdgeListDataSet;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class DefaultIconAssignments {

    public static void initializeDefaultIconAssignments() {
    	TreeNodeRenderer.setIconForType(AdjacencyMatrixDataSet.class, Icons.NETWORK_ICON);
        TreeNodeRenderer.setIconForType(DSMicroarraySet.class, Icons.MICROARRAYS_ICON);
        TreeNodeRenderer.setIconForType(DSAlignmentResultSet.class, Icons.ALIGNMENT_ICON);
        TreeNodeRenderer.setIconForType(DSHierClusterDataSet.class, Icons.DENDOGRAM_ICON);
        TreeNodeRenderer.setIconForType(DSSOMClusterDataSet.class, Icons.SOM_ICON);
        TreeNodeRenderer.setIconForType(DSSequenceSet.class, Icons.SEQUENCE_ICON);
        TreeNodeRenderer.setIconForType(DSSignificanceResultSet.class, Icons.SIGNIFICANCE_ICON);
        TreeNodeRenderer.setIconForType(ImageData.class, Icons.IMAGE_ICON);
        TreeNodeRenderer.setIconForType(PatternResult.class, Icons.PATTERN_ICON);
        TreeNodeRenderer.setIconForType(CSClassifier.class, Icons.CLASSIFIER_ICON);
        TreeNodeRenderer.setIconForType(DSProteinStructure.class, Icons.STRUCTURE_ICON);
        TreeNodeRenderer.setIconForType(DSMatrixReduceSet.class, Icons.PSAM_ICON);
        TreeNodeRenderer.setIconForType(DSProbeIntensityArray.class, Icons.CEL_ICON);
        TreeNodeRenderer.setIconForType(EdgeListDataSet.class, Icons.EDGELIST_ICON);
    }
}
