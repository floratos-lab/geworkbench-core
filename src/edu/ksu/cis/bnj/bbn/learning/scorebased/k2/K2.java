package edu.ksu.cis.bnj.bbn.learning.scorebased.k2;

/*
 * This file is part of Bayesian Network for Java (BNJ).
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
import edu.ksu.cis.bnj.bbn.learning.BDEBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.LearnerScore;
import edu.ksu.cis.bnj.bbn.learning.score.BDEScore;
import edu.ksu.cis.kdd.data.*;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.gui.OptionGUI;

import java.io.File;
import java.util.*;

/**
 * K2 algorithm
 *
 * @author Roby Joehanes
 */
public class K2 extends BDEBasedLearner {

    /**
     * Set the upper bound on parent limits for K2. Defaults to 5
     */
    protected List ordering = null;
    protected Set[] parentTable;

    public K2() {
    }

    /**
     * Constructor for K2.
     *
     * @param t
     */
    public K2(Data t) {
        super(t);
    }

    /**
     * <code>
     * Procedure K2
     * for i:=1 downto n do
     * <p/>
     * pi(v[i]) = emptyset; // parent of v[i]
     * Pold = g(v[i], pi(v[i]));
     * <p/>
     * while | pi(v[i]) | &lt; limit do
     * <p/>
     * let z be the node in v[0..i-1] - pi(v[i]) that maximizes g(v[i], pi(v[i])
     * U {z});
     * <p/>
     * Pnew = g(v[i], pi(v[i]) U {z});
     * <p/>
     * if Pnew &lt;= Pold then break;
     * Pold := Pnew;
     * pi(v[i]) := pi(v[i]) U {z};
     * end while
     * <p/>
     * write("Parents of "+v[i]+" are :", pi(v[i]);
     * end for
     * end K2
     * </code>
     *
     * @see edu.ksu.cis.bnj.bbn.learning.Learning#getGraph()
     */
    public BBNGraph getGraph() {
        // if no scorer module mentioned, default to K2 standard scorer
        if (candidateScorer == null) candidateScorer = new BDEScore(this);
        // if there's no ordering, use default ordering of the tuples
        int max = data.getRelevantAttributes().size();
        List attrs = data.getAttributes();
        int numAttrs = attrs.size();
        int[] nodes;
        if (ordering == null) { // assume default ordering
            nodes = data.getTallyer().getRelevantAttributeIndices();
        } else {
            nodes = new int[max];
            Hashtable attrToIndex = new Hashtable();
            int idx = 0;
            for (Iterator i = attrs.iterator(); i.hasNext(); idx++) {
                Attribute attr = (Attribute) i.next();
                if (data instanceof Database) {
                    attrToIndex.put(attr.getFullyQualifiedName(), new Integer(idx));
                } else {
                    attrToIndex.put(attr.getName(), new Integer(idx));
                }
            }
            int ctr = 0;
            for (Iterator i = ordering.iterator(); i.hasNext(); ctr++) {
                Object val = i.next();
                if (val instanceof String) {
                    nodes[ctr] = ((Integer) attrToIndex.get((String) val)).intValue(); //table.getAttributeIndex((String) val);
                } else {
                    nodes[ctr] = ((Integer) val).intValue();
                }
            }
        }

        //scorer.getTallyer().setEnableQueryCache(false); // Turn off query cache

        // Preparation
        BBNGraph graph = populateNodes();

        parentTable = new Set[numAttrs];
        for (int i = 0; i < numAttrs; i++)
            parentTable[i] = new HashSet();

        for (int i = 0; i < max; i++) {
            int node_i = nodes[i];
            //System.out.print(i+": "+node_i+" = ");
            double p_old = candidateScorer.getScore(node_i, LearnerScore.NO_CANDIDATES, parentTable);
            while (parentTable[node_i].size() < parentLimit) {
                int z = -1;
                double p_new = p_old;
                for (int j = 0; j < i; j++) {
                    if (i == j) continue;
                    int current_z = nodes[j];
                    if (parentTable[node_i].contains(new Integer(current_z))) continue;

                    double current_p = candidateScorer.getScore(node_i, current_z, parentTable);

                    //System.out.println("Adding "+nodes[j]+" makes p = "+current_p);
                    if (p_new < current_p) {
                        p_new = current_p;
                        z = current_z;
                    }
                }
                if (p_new > p_old) {
                    p_old = p_new;
                    parentTable[node_i].add(new Integer(z));
                } else
                    break;
            }

            //System.out.println(parentTable[node_i].toString());

            // Setup the corresponding edge in the graph
            BBNNode child = bbnNodes[node_i];
            for (Iterator j = parentTable[node_i].iterator(); j.hasNext();) {
                int idx = ((Integer) j.next()).intValue();
                BBNNode parent = bbnNodes[idx];

                try {
                    graph.addEdge(parent, child);
                } catch (Exception e) {
                    System.err.println("Error on adding parent " + parent);
                }
            }
        } // end for i loop

        computeCPT(graph);

        return graph;
    }


    public String getName() {
        return "K2";
    }

    /**
     * @return List
     */
    public List getOrdering() {
        return ordering;
    }

    /**
     * Sets the ordering.
     *
     * @param ordering The ordering to set
     */
    public void setOrdering(List ordering) {
        this.ordering = ordering;
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.learning.Learning#initialize()
     */
    public void initialize() {
    }

    public void processParameters(String[] args) {
        Hashtable params = Parameter.process(args);
        String orderString = (String) params.get("-s");

        if (orderString == null) return;

        LinkedList order = new LinkedList();
        HashSet seenBefore = new HashSet();
        StringTokenizer tok = new StringTokenizer(orderString, ",");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            Integer i = new Integer(Integer.parseInt(token) - 1);
            if (seenBefore.contains(i)) throw new RuntimeException("Duplicate order #" + i);
            seenBefore.add(i);
            order.add(((Attribute) data.getAttributes().get(i.intValue())).getName());
        }
        setOrdering(order);
    }


    ///////////////////////////////////////////////////////////////////////////////

    /**
     * Get the toy example out from paper for testing
     *
     * @return Table
     */
    public static Table getToyExample() {
        Table data = new Table();
        String p = "present", a = "absent";

        for (int i = 1; i <= 3; i++) {
            Attribute attr = new Attribute("x" + i);
            attr.addValue(p);
            attr.addValue(a);
            data.addAttribute(attr);
        }

        Tuple t;
        t = new Tuple();
        t.addValue(p);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 1
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 2
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(p);
        data.addTuple(t); // 3
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 4
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 5
        t = new Tuple();
        t.addValue(a);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 6
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 7
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 8
        t = new Tuple();
        t.addValue(p);
        t.addValue(p);
        t.addValue(p);
        data.addTuple(t); // 9
        t = new Tuple();
        t.addValue(a);
        t.addValue(a);
        t.addValue(a);
        data.addTuple(t); // 10

        return data;
    }

    public OptionGUI getOptionsDialog() {
        return new K2OptionGUI(this);
    }

    /**
     * added by prashanth
     */
    public double getNetworkScore() {
        double score = 0;
        for (int node = 0; node < parentTable.length; node++)
            score += candidateScorer.getScore(node, -1, parentTable);
        return score;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFormat = params.getString("-f");
        String outputFile = params.getString("-o");
        String orderString = params.getString("-s");
        int maxParent = params.getInt("-k", defaultParentLimit);
        boolean quiet = params.getBool("-q");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.k2.K2 -i:inputfile [-o:outputfile] [-f:outputformat] [-q] [-k:parentlimit] [-s:sequenceorder]");
            System.out.println("-f: default=xml. Acceptable values are {xml, net, bif, xbn}");
            System.out.println("-k: parent limit. Default=" + defaultParentLimit);
            System.out.println("-q: quiet mode");
            System.out.println("-s: sequence ordering (e.g.:-s:1,2,3) NO SPACES and MUST SPECIFY ALL ATTRIBUTE NUMBERS!");
            return;
        }
        System.out.println("K2 learning");

        try {
            Runtime r = Runtime.getRuntime();
            long origfreemem = r.freeMemory();
            long freemem;
            long origTime = System.currentTimeMillis();
            Table tuples = Table.load(inputFile);
            //Database tuples = Database.load(inputFile);
            K2 k2 = new K2(tuples);
            //k2.setCalculateCPT(false);
            k2.setParentLimit(maxParent);

            if (orderString != null) {
                k2.processParameters(new String[]{"-s=" + orderString});
            }

            System.gc();
            long afterLoadTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after loading tuples = " + freemem);
                System.out.println("Loading time = " + ((afterLoadTime - origTime) / 1000.0));
            }

            BBNGraph g = k2.getGraph();
            long learnTime = System.currentTimeMillis();
            freemem = r.freeMemory() - origfreemem;

            if (!quiet) {
                System.out.println("Memory needed after learning K2 = " + freemem);
                System.out.println("Learning time = " + ((learnTime - afterLoadTime) / 1000.0));
            }

            if (outputFile != null) {
                String networkName = outputFile;
                int extpos = outputFile.indexOf('.');
                if (extpos > 0) networkName = networkName.substring(0, extpos);
                extpos = outputFile.lastIndexOf(File.separator);
                if (extpos > 0) networkName = networkName.substring(extpos);
                extpos = outputFile.lastIndexOf("/");
                if (extpos > 0) networkName = networkName.substring(extpos + 1);
                g.setName(networkName);
                if (outputFormat != null) {
                    g.save(outputFile, outputFormat);
                } else {
                    g.save(outputFile);
                }
            }
            //if (k2 != null) // A dummy statement to prevent K2 being garbage collected
            //System.out.println(g.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
