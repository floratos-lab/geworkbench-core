package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSSignificanceResultSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSProbeIntensityArray;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSAlignmentResultSet;
import org.geworkbench.bison.datastructure.bioobjects.structure.DSProteinStructure;
import org.geworkbench.bison.datastructure.complex.pattern.SoapParmsDataSet;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.DSMatrixReduceSet;
import org.geworkbench.bison.model.clusters.DSHierClusterDataSet;
import org.geworkbench.bison.model.clusters.DSSOMClusterDataSet;
import org.geworkbench.bison.algorithm.classification.CSClassifier;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrixDataSet;

/**
 * @author John Watkinson
 */
public class DefaultIconAssignments {

    public static void initializeDefaultIconAssignments() {
        ProjectPanel.setIconForType(AdjacencyMatrixDataSet.class, Icons.NETWORK_ICON);
        ProjectPanel.setIconForType(DSMicroarraySet.class, Icons.MICROARRAYS_ICON);
        ProjectPanel.setIconForType(DSAlignmentResultSet.class, Icons.ALIGNMENT_ICON);
        ProjectPanel.setIconForType(DSHierClusterDataSet.class, Icons.DENDOGRAM_ICON);
        ProjectPanel.setIconForType(DSSOMClusterDataSet.class, Icons.SOM_ICON);
        ProjectPanel.setIconForType(DSSequenceSet.class, Icons.SEQUENCE_ICON);
        ProjectPanel.setIconForType(DSSignificanceResultSet.class, Icons.SIGNIFICANCE_ICON);
        ProjectPanel.setIconForType(ImageData.class, Icons.IMAGE_ICON);
        ProjectPanel.setIconForType(SoapParmsDataSet.class, Icons.PATTERN_ICON);
        ProjectPanel.setIconForType(CSClassifier.class, Icons.CLASSIFIER_ICON);
        ProjectPanel.setIconForType(DSProteinStructure.class, Icons.STRUCTURE_ICON);
        ProjectPanel.setIconForType(DSMatrixReduceSet.class, Icons.PSAM_ICON);
        ProjectPanel.setIconForType(DSProbeIntensityArray.class, Icons.CEL_ICON);
    }
}
