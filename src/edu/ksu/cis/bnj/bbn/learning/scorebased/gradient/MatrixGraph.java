package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

/*
 * Created on Thu 12 Jun 2003
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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * @author Prashanth Boddhireddy
 *         <p/>
 *         Note: row is a node and each column is the parent
 *         "Its not same as directed graph"
 *         0	1	 2	 3
 *         0 |0  0  0   0  |
 *         1 |X	0	 0 	 0  |
 *         2 |X	X	 0	 0	|
 *         3 |X 	X	 X	 0	|
 */
public class MatrixGraph {

    boolean directedGraph[][];
    int recentChangedEdge[] = new int[2];
    int noOfNodes;
    int parentLimit = 3;

    public MatrixGraph(int n) {
        noOfNodes = n;
        directedGraph = new boolean[n][n];
    }

    public MatrixGraph(boolean[][] graph) {
        directedGraph = graph;
    }

    public void initializeGraph() {
    }

    public void createRandomGraph() {
        Random rand = new Random();
        int[] randPerm = getRandomPermutation(noOfNodes);
        int randomNode = 0, randomParentNum = 0, randomParents[];
        for (int i = 1; i < noOfNodes; i++) {
            randomNode = randPerm[i];
            randomParentNum = rand.nextInt((i < parentLimit) ? i : parentLimit);
            randomParents = getRandomPermutation(randPerm, i);
            for (int j = 0; j < randomParentNum; j++)
                directedGraph[randomNode][randomParents[j]] = true;
        }
    }


    public Set[] matrixToAdjacencyList() {
        Set[] parentTable = new Set[noOfNodes];
        for (int i = 0; i < noOfNodes; i++) {
            parentTable[i] = new HashSet();
            for (int j = 0; j < i; j++) {
                if (directedGraph[i][j])
                    parentTable[i].add(new Integer(j));
            }
        }
        return parentTable;
    }

    public void display() {
        for (int i = 0; i < noOfNodes; i++) {
            System.out.print(i + " [");
            for (int j = 0; j < noOfNodes; j++) {
                if (directedGraph[i][j])
                    System.out.print(" " + j);
            }
            System.out.print("]" + "\n");
        }
    }

    public boolean isCycle() {
        boolean cycle = false;
        boolean path[][] = findClosure();
        for (int i = 0; i < noOfNodes; i++)
            if (path[i][i]) return true;
        return cycle;
    }

    private boolean[][] findClosure() {
        boolean path[][] = new boolean[noOfNodes][noOfNodes];
        for (int i = 0; i < noOfNodes; i++) {
            for (int j = 0; j < noOfNodes; j++) {
                path[i][j] = false;
                path[i][j] = directedGraph[i][j];
            }
        }
        for (int k = 0; k < noOfNodes; k++) {
            for (int i = 0; i < noOfNodes; i++) {
                if (path[i][k]) {
                    for (int j = 0; j < noOfNodes; j++)
                        path[i][j] = path[i][j] || path[k][j];
                }
            }
        }
        return path;
    }


    public void generateAllNeigbourStructures() {

    }

    public static void main(String args[]) {
        int n = 10;
        MatrixGraph graph = new MatrixGraph(5);
        graph.createRandomGraph();
        graph.display();
    }

    public int getNoOfEdges() {
        int noOfEdges = 0;
        for (int i = 0; i < noOfNodes; i++)
            for (int j = 0; j < noOfNodes; j++)
                if (directedGraph[i][j])
                    noOfEdges++;
        return noOfEdges;
    }

    public static int[] getRandomPermutation(int n) {
        int array[] = new int[n];
        for (int i = 0; i < n; i++)
            array[i] = i;
        return getRandomPermutation(array, array.length);
    }

    public static int[] getRandomPermutation(int[] array, int length) {
        Random rand = new Random();
        int[] randomPerm = new int[length];
        Vector indexVector = new Vector();
        for (int i = 0; i < length; i++)
            indexVector.add(new Integer(array[i]));
        for (int j = length; j > 0; j--) {
            int randomIndex = rand.nextInt(j);
            randomPerm[length - j] = ((Integer) indexVector.remove(randomIndex)).intValue();
        }
        return randomPerm;
    }

    public void setRecentChange(int i, int j) {
        recentChangedEdge[0] = i;
        recentChangedEdge[1] = j;
    }

}
