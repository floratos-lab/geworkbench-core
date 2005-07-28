package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

/*
 * Created on Tue 10 Jun 2003
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
 *
 */

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.score.BDEScore;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Prashanth Boddhireddy
 */
public class GreedySL extends ScoreBasedLearner {
    protected BBNGraph bbnGraph;
    protected Set[] parentTable;
    protected boolean path[][];
    protected int numOfNodes, numOfChanges = 1, totalNumOfChanges = 0;
    protected double tempScore = 0, previousScore = -1000, maxScore = -1000;
    protected int recentChange;
    public static final boolean CYCLE = true;
    public boolean containsCycle = false;
    public static final int MAXNUMCHANGES = 1000, REVERSED = 1, REMOVED = 2, ADDED = 3, NA = -1;

    public GreedySL() {
    }

    public GreedySL(Data data) {
        super(data);
    }

    public BBNGraph getGraph() {

        if (candidateScorer == null) {
            candidateScorer = new BDEScore(this);
        }
        initializeNodes();
        initializeNodeParents();
        learnGraph();
        computeCPT(bbnGraph);
        return bbnGraph;
    }

    protected void learnGraph() {
        while (numOfChanges > 0) {
            numOfChanges = 0;
            for (int node = 0; node < numOfNodes; node++) {
                for (int parent = 0; parent < numOfNodes; parent++) {
                    if (node == parent) {
                        continue;
                    }
                    if (parentTable[node].contains(new Integer(parent))) {
                        reverseOrRemoveEdge(node, parent);
                    } else {
                        addEdge(node, parent);
                    }
                }
            }
            totalNumOfChanges += numOfChanges;
            if (totalNumOfChanges > MAXNUMCHANGES) {
                break;
            }
        }
        //displayParentTable();
        addParentsToGraph();
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

    private void reverseOrRemoveEdge(int node, int parent) {
        previousScore = getScore(node, parent);
        reverseEdge(node, parent); // node <----parent to node----->parent
        if (!isCyclic()) {
            tempScore = getScore(node, parent);
        } else {
            reverseEdge(parent, node);
            removeEdge(node, parent); // since edge is already inversed
            tempScore = getScore(node, parent);
        }
        if (previousScore > tempScore) {
            undoChange(node, parent);
        } else {
            numOfChanges++;
        }
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
        if (recentChange == REVERSED) {
            reverseEdge(parent, node);
        }
        if (recentChange == REMOVED) {
            parentTable[node].add(new Integer(parent));
        }
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

    public void initializeNodes() {
        numOfNodes = data.getAttributes().size();
        int[] nodes = new int[numOfNodes];
        for (int i = 0; i < numOfNodes; i++) {
            nodes[i] = i;
        }
        bbnGraph = populateNodes();
        path = new boolean[numOfNodes][numOfNodes];
        for (int j = 0; j < numOfNodes; j++) {
            for (int i = 0; i < numOfNodes; i++) {
                path[i][j] = false;
            }
        }
    }

    public void initializeNodeParents() {
        parentTable = new Set[numOfNodes];
        for (int i = 0; i < numOfNodes; i++) {
            parentTable[i] = new HashSet();
        }
    }

    private void addParentsToGraph() {
        for (int node = 0; node < numOfNodes; node++) {
            BBNNode child = bbnNodes[node];
            for (Iterator j = parentTable[node].iterator(); j.hasNext();) {
                int idx = ((Integer) j.next()).intValue();
                BBNNode parent = bbnNodes[idx];
                try {
                    bbnGraph.addEdge(parent, child);
                } catch (Exception e) {
                    System.err.println("Error on adding parent " + parent);
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
                    for (int j = 0; j < numOfNodes; j++) {
                        path[i][j] = path[i][j] || path[k][j];
                    }
                }
            }
        }
    }

    private void displayParentTable() {
        for (int node = 0; node < numOfNodes; node++) {
            System.out.println("  node " + node + ", parents " + parentTable[node]);
        }
    }

    public double getNetworkScore() {
        double score = 0;
        for (int node = 0; node < parentTable.length; node++)
            score += candidateScorer.getScore(node, NA, parentTable);
        return score;
    }


    public static void main(String[] args) {

        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        int maxParent = params.getInt("-k", defaultParentLimit);
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.k2.K2 -i:inputfile [-o:outputfile] [-f:outputformat] [-q] [-k:parentlimit] [-s:sequenceorder]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            return;
        }

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            GreedySL greedyLearner = new GreedySL(tuples);
            //k2.setCalculateCPT(false);
            greedyLearner.setParentLimit(maxParent);
            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }

            BBNGraph g = greedyLearner.getGraph();
            System.out.println(g);
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after learning K2 = " + freemem);
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
