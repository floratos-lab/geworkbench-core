package org.geworkbench.bison.algorithm.classification.sequence;

import org.geworkbench.bison.algorithm.DSInput;
import org.geworkbench.bison.algorithm.DSOutput;
import org.geworkbench.bison.algorithm.classification.DSClassifier;
import org.geworkbench.bison.datastructure.biocollections.DataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.DSTrainingSet;
import org.geworkbench.bison.datastructure.bioobjects.DSClassification;
import org.geworkbench.bison.datastructure.bioobjects.DSParameters;
import org.geworkbench.bison.datastructure.bioobjects.classification.CSClassification;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A classifier that operates on sequences.
 */
public class SequenceClassifier implements DSClassifier<DSSequence> {
    /**
     * Creates a new SequenceClassifier.
     */
    public SequenceClassifier() {
    }

    /**
     * Runs the classifier on a given sequence.
     *
     * @param sequence the sequence to classify.
     * @return the classification for the given sequence.
     */
    public DSClassification<DSSequence> run(@DSInput("Sequence") DSSequence sequence) {
        return null;
        // @todo - watkin - Not implemented!
    }

    /**
     * Runs the classifier on an entire data set of sequences.
     *
     * @param seqDB the data set of sequences.
     * @return a Collection containing the classifications of the sequences.
     */
    @DSOutput("Collection<BSN_Classification<Sequence>>") public Collection<DSClassification<DSSequence>> run(@DSInput("BSN_DB<Sequence>") DataSet<DSSequence> seqDB) {
        // @todo - watkin - Not implemented!
        Collection<DSClassification<DSSequence>> results = new ArrayList<DSClassification<DSSequence>>();
        for (DSSequence s : seqDB) {
            DSClassification<DSSequence> sc = new CSClassification<DSSequence>(s);
            results.add(sc);
        }
        return results;
    }

    /**
     * Sets the parameters for this classifier.
     *
     * @param parameters the
     */
    public void setParameters(DSParameters parameters) {
    }

    /**
     * Initializes this sequence classifier with the specified training set.
     *
     * @param db the training set for this classifer.
     */
    public void init(DSTrainingSet<DSSequence> db) {
    }
}
