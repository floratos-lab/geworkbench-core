package org.geworkbench.util.pathwaydecoder.bayes;

import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.GreedySL;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.HillClimbingSL;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.SimAnnealSL;
import edu.ksu.cis.kdd.data.Table;

public class BayesSLFactory {
    public BayesSLFactory() {
    }

    public static ScoreBasedLearner getLearner(String learner, Table data) {
        if ("GreedySL".equalsIgnoreCase(learner)) {
            return new GreedySL(data);
        } else if ("HillClimbingSL".equalsIgnoreCase(learner)) {
            return new HillClimbingSL(data);
        } else if ("SimAnnealSL".equalsIgnoreCase(learner)) {
            return new SimAnnealSL(data);
        }
        return null;
    }
}
