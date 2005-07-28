package edu.ksu.cis.bnj.bbn.learning;

/*
 * Created on Wed 19 Feb 2003
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

import edu.ksu.cis.bnj.bbn.BBNConstant;
import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tally;

import java.util.*;

/**
 * Score-based learning classes
 *
 * @author Roby Joehanes
 */
public abstract class ScoreBasedLearner extends Learner {

    public static final int defaultParentLimit = 5;

    protected LearnerScore candidateScorer;
    protected LearnerScore structureScorer;
    protected Set[] parentTable;
    public static final int NA = -1;

    public double getNetworkScore() {
        double score = 0;
        for (int node = 0; node < parentTable.length; node++)
            score += candidateScorer.getScore(node, NA, parentTable);
        return score;
    }


    /**
     * Set the upper bound on parent limits. Defaults to 5
     */
    protected int parentLimit = defaultParentLimit;

    public ScoreBasedLearner() {
    }

    /**
     * Constructor for ScoreBasedLearning.
     *
     * @param t
     */
    public ScoreBasedLearner(Data t) {
        super(t);
    }

    /**
     * Constructor for ScoreBasedLearning.
     *
     * @param t
     */
    public ScoreBasedLearner(Data t, LearnerScore s) {
        super(t);
        setCandidateScorer(s);
    }

    /**
     * Constructor for ScoreBasedLearning.
     *
     * @param t
     */
    public ScoreBasedLearner(Data t, LearnerScore s, LearnerScore s2) {
        this(t, s);
        setStructureScorer(s2);
    }

    /**
     * Sets the candidate scorer.
     *
     * @param score The score to set
     */
    public void setCandidateScorer(LearnerScore scorer) {
        this.candidateScorer = scorer;
        candidateScorer.setOwner(this);
    }

    /**
     * Sets the structure scorer.
     *
     * @param structureScorer The structureScorer to set
     */
    public void setStructureScorer(LearnerScore structureScorer) {
        this.structureScorer = structureScorer;
        structureScorer.setOwner(this);
    }

    public void setTable(Table tuples) {
        super.setData(tuples);
        if (candidateScorer != null) candidateScorer.setData(tuples);
        if (structureScorer != null) structureScorer.setData(tuples);
    }

    /**
     * @return int
     */
    public int getParentLimit() {
        return parentLimit;
    }

    /**
     * Sets the parentLimit.
     *
     * @param parentLimit The parentLimit to set
     */
    public void setParentLimit(int parentLimit) {
        this.parentLimit = parentLimit;
    }

    private Tally tally;
    private Hashtable arityTable;

    protected void computeCPT(BBNGraph g) {
        if (!calculateCPT) return;
        if (candidateScorer != null) {
            tally = candidateScorer.getTallyer();
        } else if (structureScorer != null) {
            tally = structureScorer.getTallyer();
        } else {
            throw new RuntimeException("This shouldn't happen!");
        }
        arityTable = new Hashtable();

        int max = bbnNodes.length;
        for (Iterator i = tally.getUnderlyingData().getAttributes().iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            arityTable.put(attr.getName(), new Integer(attr.getArity()));
        }

        for (int i = 0; i < max; i++) {
            BBNNode child = bbnNodes[i];
            if (child == null) continue;
            List parents = child.getParents();
            int parentSize = 0;
            if (parents != null) parentSize = parents.size();
            int[] nodeList = new int[parentSize + 1];
            if (parents != null) {
                int idx = 0;
                for (Iterator j = parents.iterator(); j.hasNext(); idx++) {
                    BBNNode parent = (BBNNode) j.next();
                    nodeList[idx] = ((Integer) indexTable.get(parent.getLabel())).intValue();
                }
            }
            nodeList[parentSize] = i;

            iterateUniqueInstantiation(0, nodeList, new int[nodeList.length], new Hashtable());
        }
    }

    /**
     * <P>Iterate through unique instantiation of children given parents. To invoke:
     * <tt>iterateUniqueInstantiation(nodeList, new Hashtable(), tallyer);</tt>
     *
     * @param nodes   List of BBNNodes being iterated. The last one must be child node
     * @param curInst Current instantiation in progress (just needed during the recursive loop)
     * @param tallyer The tallyer
     */
    protected void iterateUniqueInstantiation(int depth, int[] nodes, int[] valInts, Hashtable curQuery) {
        BBNNode node = bbnNodes[nodes[depth]];
        int sum = 0;
        int nodeSize = nodes.length - depth - 1;
        Hashtable cache = null;
        if (nodeSize == 0) {
            cache = new Hashtable();
        }

        String[] values = (String[]) ((BBNDiscreteValue) node.getValues()).toArray(new String[0]);
        int max = values.length;

        for (int i = 0; i < max; i++) {
            String value = values[i];
            String nodeName = node.getLabel();
            curQuery.put(nodeName, value);
            //valInts[depth] = ((Integer) arityTable.get(nodeName)).intValue(); // tally.getValueIndex(nodeName, value);
            valInts[depth] = i;
            //curInst.put(new Integer(nodes[depth]), new Integer(tally.getValueIndex(nodeName, value)));
            if (nodeSize == 0) { // at the end of recursion
                //set.add(curInst.clone());
                //int count = tally.tally(curInst);
                int count = tally.tally(nodes, valInts);
                sum += count;
                cache.put(curQuery.clone(), new Integer(count));
            } else {
                //iterateUniqueInstantiation(depth+1, nodes, curInst, curQuery);
                iterateUniqueInstantiation(depth + 1, nodes, valInts, curQuery);
            }
        }

        if (nodeSize == 0) {
            for (Enumeration e = cache.keys(); e.hasMoreElements();) {
                Hashtable queryTable = (Hashtable) e.nextElement();
                int tally = ((Integer) cache.get(queryTable)).intValue();
                if (sum > 0)
                    node.putCPFValue(queryTable, new BBNConstant(((double) tally) / sum));
                else
                    node.putCPFValue(queryTable, new BBNConstant(0));
            }
        }
    }
}
