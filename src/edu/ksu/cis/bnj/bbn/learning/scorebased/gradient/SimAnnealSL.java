package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

/*
 * Created on Tue 24 Jun 2003
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
import edu.ksu.cis.bnj.bbn.analysis.GraphScorer;
import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.score.BDEScore;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.graph.Edge;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * @@author Julie Thornton
 */
public class SimAnnealSL extends ScoreBasedLearner {

    protected BBNGraph bbnGraph;
    private String goldGraph;
    public String inputFile;
    private String resultsFile;
    protected Set[] parentTable;
    private Set[] possibleParents;
    protected boolean path[][];
    private Vector[] errors;
    protected int numOfNodes, numOfChanges = 1, totalNumOfChanges = 0;
    protected double tempScore = 0, previousScore = -1000, maxScore = -1000;
    protected int recentChange;
    public static final boolean CYCLE = true;
    public boolean containsCycle = false;
    private int minNumChanges;
    private int maxParents;
    public static final int REVERSED = 1, REMOVED = 2, ADDED = 3, NA = -1;
    private Vector allGraphs;
    private Vector allGraphScores;
    private GraphScorer gs;
    private Vector curErrors;
    public String errorType;

    public SimAnnealSL() {
    }

    /**
     * @@param t
     */
    public SimAnnealSL(Data t) {
        super(t);
    }

    public BBNGraph getGraph() {
        if (candidateScorer == null) {
            candidateScorer = new BDEScore(this);
        }

        allGraphs = new Vector();
        allGraphScores = new Vector();
        //maxParents = 5;
        maxParents = 2;
        initializeNodes();
        errors = new Vector[5];

        // random restart, repeat for numOfNodes
        for (int i = 0; i < 5; i++) {
            curErrors = new Vector();
            initializeNodes();
            initializeNodeParents();
            initializePossibleParents();
            initializeGraph();
            learnGraph();
            errors[i] = curErrors;
            allGraphs.addElement(bbnGraph);
            allGraphScores.addElement(new Double(getNetworkScore()));
            computeCPT(bbnGraph);
            resetCPT();
            bbnGraph = new BBNGraph();
        }

        // now find best graph
        double bestScore = -10000000;
        int bestIndex = -1;
        for (int i = 0; i < 5; i++) {
            BBNGraph curGraph = (BBNGraph) allGraphs.elementAt(i);
            double curScore = ((Double) allGraphScores.elementAt(i)).doubleValue();
            if (curScore > bestScore) {
                bestScore = curScore;
                bestIndex = i;
            }
        }
        bbnGraph = (BBNGraph) allGraphs.elementAt(bestIndex);
        computeCPT(bbnGraph);
        return bbnGraph;
    }

    protected void learnGraph() {
        double t = 0.9;
        double prob = 0.0;
        double oldScore = -1000000;
        // while random node each time is still making positive changes
        while (t > 0.1) {
            int node = new Double(Math.random() * numOfNodes).intValue();
            boolean moved = false;
            int parentIndex = new Double(Math.random() * possibleParents[node].size()).intValue();
            int parent = ((Integer) (possibleParents[node].toArray())[parentIndex]).intValue();
            while (!moved) {
                double score = -100000;
                oldScore = getScore(node, parent);
                if (node == parent) continue;
                if (parentTable[node].contains(new Integer(parent)))
                    score = reverseOrRemoveEdgeScore(node, parent);
                else {
                    if (parentTable[node].size() < maxParents && possibleParents[node].contains(new Integer(parent)))
                        score = addEdgeScore(node, parent);
                }
                if (score != 0) {
                    moved = true;
                    maxScore = score;
                } else {
                    parentIndex = new Double(Math.random() * possibleParents[node].size()).intValue();
                    parent = ((Integer) (possibleParents[node].toArray())[parentIndex]).intValue();
                }
            }

            // now implement the random change if it had positive results
            // and implement it with decaying probability if it did not
            if (maxScore > 0) {
                if (parentTable[node].contains(new Integer(parent))) {
                    reverseOrRemoveEdge(node, parent);
                } else {
                    addEdge(node, parent);
                }
            } else {
                double rand = Math.random();
                prob = Math.exp((maxScore - oldScore) / t);
                if (rand < t) {
                    if (parentTable[node].contains(new Integer(parent))) {
                        reverseOrRemoveEdge(node, parent);
                    } else {
                        addEdge(node, parent);
                    }
                }
            }

            //update decay variable
            t = t / (1.0 + .01 * t);
            if (errorType != null) {
                addParentsToGraph();
                resetCPT();
                computeCPT(bbnGraph);
                gs = new GraphScorer(bbnGraph);
                if (errorType.equals("g")) {
                    gs.loadGoldGraph(goldGraph);
                    curErrors.addElement(new Integer(gs.getGraphErrors()));
                } else if (errorType.equals("b")) {
                    StringTokenizer st = new StringTokenizer(inputFile, ".");
                    st.nextToken();
                    String format = st.nextToken();
                    gs.loadData(inputFile, format);
                    gs.makeParentTable();
                    curErrors.addElement(new Double(gs.getNetworkScore()));
                } else if (errorType.equals("r")) {
                    StringTokenizer st = new StringTokenizer(inputFile, ".");
                    st.nextToken();
                    String format = st.nextToken();
                    gs.loadData(inputFile, format);
                    curErrors.addElement(new Double(gs.getRMSE()));
                } else if (errorType.equals("l")) {
                    StringTokenizer st = new StringTokenizer(inputFile, ".");
                    st.nextToken();
                    String format = st.nextToken();
                    gs.loadData(inputFile, format);
                    curErrors.addElement(new Double(gs.getLogLikelihood()));
                }
                resetCPT();
            }
        }
        //displayParentTable();
        addParentsToGraph();
    }

    private double addEdgeScore(int node, int parent) {
        previousScore = getScore(node, parent);
        parentTable[node].add(new Integer(parent));
        if (!isCyclic()) {
            tempScore = getScore(node, parent);
        } else {
            tempScore = tempScore - 10000;
        }
        parentTable[node].remove(new Integer(parent));

        return (tempScore - previousScore);
    }

    private void addEdge(int node, int parent) {
        previousScore = getScore(node, parent);
        parentTable[node].add(new Integer(parent));
        if (!isCyclic()) {
            tempScore = getScore(node, parent);
            if (tempScore > previousScore) {
                numOfChanges++;
                return;
            }
        }
        parentTable[node].remove(new Integer(parent));
    }

    private void addEdgeIgnoreScore(int node, int parent) {
        previousScore = getScore(node, parent);
        parentTable[node].add(new Integer(parent));
        if (!isCyclic()) {
            numOfChanges++;
            return;
        }
        parentTable[node].remove(new Integer(parent));
    }

    private double reverseOrRemoveEdgeScore(int node, int parent) {
        previousScore = getScore(node, parent);
        reverseEdge(node, parent);		// node <----parent to node----->parent
        if (!isCyclic()) {
            tempScore = getScore(node, parent);
            undoChange(node, parent);
        } else {
            reverseEdge(parent, node);
            removeEdge(node, parent); // since edge is already inversed
            tempScore = tempScore - 10000;
            addEdge(node, parent);
        }
        return (tempScore - previousScore);
    }

    private void reverseOrRemoveEdge(int node, int parent) {
        previousScore = getScore(node, parent);
        reverseEdge(node, parent);		// node <----parent to node----->parent
        if (!isCyclic()) {
            tempScore = getScore(node, parent);
        } else {
            reverseEdge(parent, node);
            removeEdge(node, parent); // since edge is already inversed
            tempScore = getScore(node, parent);
        }
        if (previousScore > tempScore)
            undoChange(node, parent);
        else
            numOfChanges++;
    }

    private void reverseEdge(int node, int parent) {
        parentTable[node].remove(new Integer(parent));
        parentTable[parent].add(new Integer(node));
        recentChange = REVERSED;
    }

    private void removeEdge(int node, int parent) {
        parentTable[node].remove(new Integer(parent));
        recentChange = REMOVED;
    }

    private void undoChange(int node, int parent) {
        if (recentChange == REVERSED)
            reverseEdge(parent, node);
        if (recentChange == REMOVED)
            parentTable[node].add(new Integer(parent));
    }

    private boolean isCyclic() {
        boolean cycleFound = false;
        findClosure();
        for (int i = 0; i < numOfNodes; i++) {
            if (path[i][i]) {
                cycleFound = true;
                break;
            }
        }
        return cycleFound;
    }


    private double getScore(int i, int j) {
        double score = candidateScorer.getScore(i, NA, parentTable);
        score += candidateScorer.getScore(j, NA, parentTable);
        return score;
    }

    public double getNetworkScore() {
        double score = 0;
        for (int node = 0; node < parentTable.length; node++)
            score += candidateScorer.getScore(node, NA, parentTable);
        return score;
    }

    public void initializeGraph() {

        //give nodes a random ordering
        int[] randNodes = new int[numOfNodes];
        Vector possibleNumbers = new Vector();

        for (int i = 0; i < numOfNodes; i++) {
            possibleNumbers.addElement(new Integer(i));
        }

        for (int i = 0; i < numOfNodes; i++) {
            boolean found = false;
            while (!found) {
                Integer index = new Integer((new Double(Math.random() * numOfNodes)).intValue());
                if (possibleNumbers.contains(index)) {
                    randNodes[i] = index.intValue();
                    possibleNumbers.removeElement(index);
                    possibleNumbers.trimToSize();
                    found = true;
                }
            }
        }

        for (int i = 1; i < numOfNodes; i++) {
            int numParents = (new Double(Math.ceil((Math.random() * maxParents)))).intValue();
            for (int j = 0; j < numParents; j++) {
                int oldChanges = numOfChanges;
                int loopCount = 0;
                while (numOfChanges == oldChanges && loopCount < 10 * randNodes[i]) {
                    int parentIndex = (new Double(Math.random() * (randNodes[i]))).intValue();
                    if (possibleParents[i].contains(new Integer(parentIndex)))
                        addEdgeIgnoreScore(i, parentIndex);
                    loopCount++;
                }
            }
        }
    }

    public void initializeNodes() {
        numOfNodes = data.getAttributes().size();
        int[] nodes = new int[numOfNodes];
        for (int i = 0; i < numOfNodes; i++) {
            nodes[i] = i;
        }
        bbnGraph = populateNodes();
        path = new boolean[numOfNodes][numOfNodes];
        for (int j = 0; j < numOfNodes; j++) {
            for (int i = 0; i < numOfNodes; i++)
                path[i][j] = false;
        }
    }

    public void initializePossibleParents() {
        for (int i = 0; i < numOfNodes; i++) {
            double[] scoreArray = new double[numOfNodes];
            for (int j = 0; j < numOfNodes; j++) {
                scoreArray[j] = addEdgeScore(i, j);
            }
            possibleParents[i].addAll(getBestScoreParents(scoreArray));
        }
    }

    private Set getBestScoreParents(double[] scoreArray) {
        Set bestParents = new TreeSet();
        double lastVal = 1000000;
        for (int i = 0; i < maxParents; i++) {
            int index = getClosestIndex(lastVal, scoreArray);
            bestParents.add(new Integer(index));
            lastVal = scoreArray[index];
        }

        return bestParents;
    }

    private int getClosestIndex(double lastVal, double[] scoreArray) {
        double closest = -1000000;
        int bestIndex = -1;
        for (int i = 0; i < scoreArray.length; i++) {
            double tempScore = scoreArray[i];
            if (tempScore < lastVal && tempScore >= closest) {
                bestIndex = i;
                closest = tempScore;
            }
        }

        return bestIndex;
    }

    public void initializeNodeParents() {
        parentTable = new Set[numOfNodes];
        possibleParents = new Set[numOfNodes];
        for (int i = 0; i < numOfNodes; i++) {
            parentTable[i] = new HashSet();
            possibleParents[i] = new HashSet();
        }
    }

    public void resetCPT() {
        for (int i = 0; i < numOfNodes; i++) {
            BBNNode node = bbnNodes[i];
            node.setCPF(new Hashtable());
        }
    }

    private void addParentsToGraph() {
        for (int node = 0; node < numOfNodes; node++) {
            BBNNode child = bbnNodes[node];
            try {
                bbnGraph.removeEdges(child);
            } catch (Exception e) {
                System.out.println("error deleting edges");
            }
            for (Iterator j = parentTable[node].iterator(); j.hasNext();) {
                int idx = ((Integer) j.next()).intValue();
                BBNNode parent = bbnNodes[idx];
                try {
                    bbnGraph.addEdge(new Edge(parent, child));
                } catch (Exception e) {
                    System.err.println("Error on adding parent " + parent + " to child " + child);
                }
            }
        }
    }

    private void findClosure() {
        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                path[i][j] = false;
                path[i][j] = parentTable[i].contains(new Integer(j));
            }
        }
        for (int k = 0; k < numOfNodes; k++) {
            for (int i = 0; i < numOfNodes; i++) {
                if (path[i][k]) {
                    for (int j = 0; j < numOfNodes; j++)
                        path[i][j] = path[i][j] || path[k][j];
                }
            }
        }
    }

    private void displayParentTable() {
        for (int node = 0; node < numOfNodes; node++) {
            System.out.println("  node " + node + ", parents " + parentTable[node]);
        }
    }

    private void setGoldGraph(String g) {
        goldGraph = g;
    }

    private void setResultsFile(String file) {
        resultsFile = file;
    }

    private void printErrors() {
        try {
            PrintWriter outfile = new PrintWriter(new FileWriter(resultsFile));
            for (int i = 0; i < 5; i++) {
                Vector v = this.errors[i];
                v.trimToSize();
                for (int j = 0; j < v.size(); j++) {
                    if (errorType.equals("g")) {
                        int error = ((Integer) v.get(j)).intValue();
                        outfile.print(error + " ");
                    } else {
                        double error = ((Double) v.get(j)).doubleValue();
                        outfile.print(error + " ");
                    }
                }
                outfile.println();
            }
            outfile.close();
        } catch (Exception e) {
            System.out.println("Could not open graph error output file");
        }
    }

    public static void main(String[] args) {

        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        int maxParent = params.getInt("-k", defaultParentLimit);
        boolean quiet = params.getBool("-q");
        String goldFile = params.getString("-g");
        String resultsFile = params.getString("-r");
        String errorType = params.getString("-t");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.greedy.SimAnnealSL -i:inputfile [-o:outputfile] [-f:outputformat] [-q] [-k:parentlimit] [-g:goldfile] [-t:errorType] [-r:resultsfile]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            System.out.println("-t: acceptable values are {g (graph errors), r (rmse), b(bde score), l (log-likelihood)}");
            System.out.println("must define -g (goldFile) if want graph error count");

            return;
        }

        if (errorType != null) {
            if (errorType.equals("g")) {
                if (goldFile == null) {
                    System.out.println("must define -g (goldFile) if want graph error count");
                    return;
                }
            }
        }

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            SimAnnealSL greedyLearner = new SimAnnealSL(tuples);
            greedyLearner.inputFile = inputFile;

            greedyLearner.setParentLimit(maxParent);
            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }
            if (errorType != null) {
                greedyLearner.setResultsFile(resultsFile);
                greedyLearner.errorType = errorType;
                if (errorType.equals("g")) {
                    greedyLearner.setGoldGraph(goldFile);
                }
            }

            BBNGraph g = greedyLearner.getGraph();
            System.out.println(g);
            if (errorType != null) greedyLearner.printErrors();
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after learning SimAnneal = " + freemem);
                System.out.println("Learning time = " + ((learnTime - afterLoadTime) / 1000.0));
            }

            if (outputFile != null) {
                if (outputFormat != null) {
                    g.save(outputFile, outputFormat);
                } else {
                    g.save(outputFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
