package org.geworkbench.util.pathwaydecoder.bayes;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.kdd.data.Table;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.bayes.discretizers.LogDiscretizer;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class CustomGraphScorer {
    public CustomGraphScorer() {
    }

    public static void main(String[] args) {
        new CustomGraphScorer().doIt();
    }

    void doIt() {
        File expFile = new File("/users/aam2110/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp");
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        BayesUtil util = new BayesUtil();
        Table rawData = util.convertToTable(mArraySet);

        LogDiscretizer discretizer = new LogDiscretizer();
        Table discretizedData = discretizer.getDiscretizedData(rawData);

        CustomizableGraph custGraph = new CustomizableGraph(discretizedData);
        custGraph.setCandidateScorer(new BDEScoreDirPrior(1.0));

        //        BBNGraph graph = custGraph.getGraph();

        //        custGraph.clearStructure();
        BBNGraph graph = custGraph.getInitializedGraph();

        File edgeFile = new File("/users/aam2110/Simulations/Results_new/AGN-century/SF/SF-001/SF-001_true_pairedEdgeList.txt");
        Vector trueEdges = org.geworkbench.bison.util.FileUtil.readFile(edgeFile);
        Iterator trueEdgeIt = trueEdges.iterator();
        while (trueEdgeIt.hasNext()) {
            String[] edges = (String[]) trueEdgeIt.next();
            if (edges != null && edges.length > 1) {
                custGraph.addEdge("G" + edges[0].trim(), "G" + edges[1].trim());
            }
        }

        //
        //    //Hartemink model 1
        //    custGraph.addEdge(1, 0);
        //    custGraph.addEdge(0, 2);

        custGraph.computeCPT();

        System.out.println(graph.toString());

        double networkScore = custGraph.getNetworkScore();
        System.out.println("Score " + networkScore);
    }

}
