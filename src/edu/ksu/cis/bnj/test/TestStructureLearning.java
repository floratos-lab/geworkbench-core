/*
 * @author Julie Thornton
 *
 */
package edu.ksu.cis.bnj.test;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Julie Thornton
 */

public class TestStructureLearning {

    private BBNGraph goldGraph;
    private BBNGraph inputGraph;
    private int size;
    private int[][] goldstandard;
    private int[][] current;
    private Hashtable nameToIndex;

    public TestStructureLearning(BBNGraph gold, BBNGraph input) {
        goldGraph = gold;
        inputGraph = input;
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

    public int countGraphErrors() {
        Set nodes = goldGraph.getNodeNames();
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            String nodeName = it.next().toString();
            BBNNode goldNode = (BBNNode) goldGraph.getNode(nodeName);
            BBNNode curNode = (BBNNode) inputGraph.getNode(nodeName);
            addChildren(goldNode, true);
            addChildren(curNode, false);
        }

        int addErrors = countErrorAddition();
        int revErrors = countErrorReversal();
        int delErrors = countErrorDeletion();

        return addErrors + revErrors + delErrors;
    }

    private void addChildren(BBNNode node, boolean addToGold) {
        String nodeName = node.getName();
        int nodeIndex = ((Integer) nameToIndex.get(nodeName)).intValue();
        Set children = node.getChildrenNames();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            String childName = it.next().toString();
            int childIndex = ((Integer) nameToIndex.get(childName)).intValue();
            if (addToGold) {
                goldstandard[nodeIndex][childIndex] = 1;
            } else {
                current[nodeIndex][childIndex] = 1;
            }
        }
    }

    private int countErrorAddition() {
        int count = 0;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (current[i][j] + current[j][i] > goldstandard[i][j] + goldstandard[j][i]) {
                    count++;
                }
            }
        }

        return count;
    }

    private int countErrorDeletion() {
        int count = 0;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (current[i][j] + current[j][i] < goldstandard[i][j] + goldstandard[j][i]) {
                    count++;
                }
            }
        }

        return count;
    }

    private int countErrorReversal() {
        int count = 0;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if ((current[i][j] == goldstandard[j][i] && goldstandard[j][i] == 1) || (current[j][i] == goldstandard[i][j] && goldstandard[i][j] == 1)) {
                    count++;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String goldFile = params.getString("-g");
        String inputFile = params.getString("-i");

        if (inputFile == null || goldFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.test.TestStructureLearning -g:goldFile -i:inputFile");
            return;
        }

        try {
            BBNGraph goldGraph = BBNGraph.load(goldFile);
            BBNGraph inputGraph = BBNGraph.load(inputFile);

            TestStructureLearning tsl = new TestStructureLearning(goldGraph, inputGraph);
            int numGraphErrors = tsl.countGraphErrors();

            System.out.println("Graph errors: " + numGraphErrors);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
