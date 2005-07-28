package edu.ksu.cis.bnj.bbn.analysis;

/*
 * Created on Thu 26 Jun 2003
 *
 * This file is part of Bayesian Networks in Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.score.BDEScore;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.HillClimbingSL;
import edu.ksu.cis.bnj.test.TestStructureLearning;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tally;
import edu.ksu.cis.kdd.data.Tuple;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.*;

/**
 * @author Julie Thornton
 *         <p/>
 *         Evaluation methods for structure learning
 *         including graph error count, BDe score, and RMSE
 */

public class GraphScorer {
    private BBNGraph goldGraph;
    private BBNGraph inputGraph;
    private int size;
    private int[][] goldstandard;
    private int[][] current;
    private Hashtable nameToIndex;
    private Set[] parentTable;
    private BDEScore candidateScorer;
    private Table tuples;
    private InferenceResult actualResult;

    public GraphScorer(BBNGraph input) {
        inputGraph = input;
    }

    public void makeParentTable() {
        int numOfNodes = inputGraph.getNodeNames().size();
        parentTable = new Set[numOfNodes];
        for (int i = 0; i < numOfNodes; i++) {
            parentTable[i] = new HashSet();
        }

        Hashtable nodeToNumber = mapNodeToNumber();
        Set nodes = inputGraph.getNodes();
        Iterator itNodes = nodes.iterator();

        while (itNodes.hasNext()) {
            BBNNode node = (BBNNode) itNodes.next();
            String nodeName = node.getLabel();
            int index = ((Integer) nodeToNumber.get(nodeName)).intValue();
            Set parents = node.getParentNames();
            Iterator itParents = parents.iterator();
            while (itParents.hasNext()) {
                String parentName = itParents.next().toString();
                Integer parentIndex = (Integer) nodeToNumber.get(parentName);
                parentTable[index].add(parentIndex);
            }
        }
    }

    private Hashtable mapNodeToNumber() {
        Hashtable map = new Hashtable();
        Set nodeNames = inputGraph.getNodeNames();
        int count = 0;
        Iterator it = nodeNames.iterator();

        while (it.hasNext()) {
            String curName = it.next().toString();
            map.put(curName, new Integer(count));
            count++;
        }

        return map;
    }

    public void loadData(String dataFile, String format) {
        tuples = Table.load(dataFile, format);
    }

    private void precache() {
        actualResult = new InferenceResult();
        Tally tally = tuples.getTallyer(); // new Tally(tuples);

        List attrs = tuples.getAttributes();
        int attributeSize = attrs.size();
        double total = tally.size();
        int index = 0;
        for (Iterator i = attrs.iterator(); i.hasNext(); index++) {
            Attribute attr = (Attribute) i.next();
            int arity = attr.getArity();
            Hashtable tbl = new Hashtable();
            List values = attr.getValues();
            for (int j = 0; j < arity; j++) {
                int t = tally.tally(index, j);
                tbl.put(values.get(j), new Double(t / total));
            }
            actualResult.put(attr.getName(), tbl);
        }
    }

    public double getRMSE() {
        if (actualResult == null) precache();
        LS ls = new LS(inputGraph);
        InferenceResult approxResult = ls.getMarginals();
        System.out.println("actual:");
        System.out.println(actualResult.toString());
        System.out.println("approx:");
        System.out.println(approxResult.toString());
        return actualResult.computeRMSE(approxResult);
    }

    public double getNetworkScore() {
        HillClimbingSL hc = new HillClimbingSL(tuples);
        candidateScorer = new BDEScore(hc);
        //candidateScorer.setTuples(tuples);

        double score = 0;
        for (int node = 0; node < parentTable.length; node++) {
            score += candidateScorer.getScore(node, -1, parentTable);
        }
        return score;
    }

    public void loadGoldGraph(String goldFile) {
        goldGraph = BBNGraph.load(goldFile);
        size = goldGraph.getNodes().size();
        goldstandard = new int[size][size];
        current = new int[size][size];
        nameToIndex = new Hashtable();

        Set nodes = goldGraph.getNodeNames();
        Iterator it = nodes.iterator();
        int count = 0;
        while (it.hasNext()) {
            String nodeName = it.next().toString();
            nameToIndex.put(nodeName, new Integer(count));
            count++;
        }
    }

    public int getGraphErrors() {
        TestStructureLearning tsl = new TestStructureLearning(goldGraph, inputGraph);
        return tsl.countGraphErrors();
    }

    public double getLogLikelihood() {
        LS ls = new LS(inputGraph);
        InferenceResult approxResult = ls.getMarginals();
        double loglike = 0;

        for (int i = 0; i < tuples.getTuples().size(); i++) {
            //get an InferenceResult for each tuple, calculate RMSE
            InferenceResult actualResult = getInferenceResult(i);
            double rmse = actualResult.computeRMSE(approxResult);
            loglike += Math.log(rmse);
        }

        return loglike;
    }

    private InferenceResult getInferenceResult(int i) {
        InferenceResult result = new InferenceResult();
        Tuple curTuple = tuples.getTuple(i);
        Table curTable = new Table();
        curTable.addTuple(curTuple);
        curTable.addAttributes(tuples.getAttributes());
        Tally tally = curTable.getTallyer(); //new Tally(curTable);
        List attrs = curTable.getAttributes();

        double total = tally.size();
        int index = 0;

        for (Iterator j = attrs.iterator(); j.hasNext(); index++) {
            Attribute attr = (Attribute) j.next();
            int arity = attr.getArity();
            Hashtable tbl = new Hashtable();
            List values = attr.getValues();
            for (int k = 0; k < arity; k++) {
                int t = tally.tally(index, k);
                tbl.put(values.get(k), new Double(t / total));
            }
            result.put(attr.getName(), tbl);
        }
        return result;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String goldFile = params.getString("-g");
        String inputFile = params.getString("-i");
        String errorType = params.getString("-t");
        String outputFile = params.getString("-o");
        String dataFile = params.getString("-d");

        if (inputFile == null || errorType == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.analysis.GraphScorer -i:inputFile -t:errorType -o:outputFile [-g:goldFile] [-d:dataFile]");
            System.out.println("errorType: [r (rmse) b (bde score) g (graph errors) l (log-likelihood)]");
            System.out.println("must define -g (gold graph) when wanting graph score");
            System.out.println("must define -d (data file) when wanting rmse");

            return;
        }

        BBNGraph inputGraph = BBNGraph.load(inputFile);
        GraphScorer gs = new GraphScorer(inputGraph);

        try {
            if (errorType.equals("r")) {
                StringTokenizer st = new StringTokenizer(dataFile, ".");
                st.nextToken();
                String format = st.nextToken();
                gs.loadData(dataFile, format);
                gs.precache();
                System.out.println("RMSE: " + gs.getRMSE());
            } else if (errorType.equals("g")) {
                gs.loadGoldGraph(goldFile);
                System.out.println("Graph errors: " + gs.getGraphErrors());
            } else if (errorType.equals("b")) {
                gs.makeParentTable();
                System.out.println("BDe score: " + gs.getNetworkScore());
            } else if (errorType.equals("l")) {
                StringTokenizer st = new StringTokenizer(dataFile, ".");
                st.nextToken();
                String format = st.nextToken();
                gs.loadData(dataFile, format);
                System.out.println("Log-likelihood: " + gs.getLogLikelihood());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
