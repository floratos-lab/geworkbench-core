package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.DSPValued;

import javax.swing.*;

/**
 * Stores a position-specific affinity matrix (for example-- as produced by MatrixREDUCE).
 *
 * @author John Watkinson
 */
public interface DSPositionSpecificAffintyMatrix extends DSBioObject, DSPValued {

    ImageIcon getPsamImage();

    String getExperiment();

    String getSeedSequence();

    String getConsensusSequence();

    long getBonferroni();

    double[][] getScores();

    boolean isTrailingStrand();

    void setPsamImage(ImageIcon image);

    void setExperiment(String experiment);

    void setSeedSequence(String seedSequence);

    void setConsensusSequence(String consensusSequence);

    void setScores(double[][] scores);

    void setBonferroni(long bonferonni);

    void setTrailingStrand(boolean trailingStrand);
}
