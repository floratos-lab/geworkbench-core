/*
 * Created on Apr 17, 2003
 *
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
package edu.ksu.cis.bnj.bbn.inference.cutset;

import edu.ksu.cis.bnj.bbn.BBNCPF;
import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.approximate.sampling.AIS;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Roby Joehanes
 */
//public class AISBCC extends ApproximateInference implements MCMCListener {
public class AISBCC {

    protected int aisUpdateFrequency = 20;
    protected BoundedCutset bcc;
    protected AIS ais;
    protected int updateCounter = 0;
    protected InferenceResult tempResult = null;
    private BBNGraph g;
    private int[] countArray;

    public AISBCC() {
    }

    /**
     * @param g
     */
    public AISBCC(BBNGraph g) {
        this.g = g;
    }

    //	/**
    //	 * @see edu.ksu.cis.bnj.bbn.inference.Inference#getMarginals()
    //	 */
    //	public InferenceResult getMarginals() {
    //        bcc = new BoundedCutset(graph);
    //        ais = new AIS(graph);
    //        ais.addListener(this);
    //
    //        // To thread or not to thread, that is the question.
    //        Thread t = new Thread(ais);
    //        t.start();
    //		return bcc.getMarginals();
    //	}
    //
    //    /**
    //     * @see edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMCListener#callback(edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMCEvent)
    //     */
    //    public Object callback(MCMCEvent event) {
    //        updateCounter++;
    //        if ((updateCounter % aisUpdateFrequency) != 0) return null;
    //
    //        tempResult = (InferenceResult) event.getTemporaryResult().clone();
    //        tempResult.normalize();
    //
    //        TreeSet newSortedInst = new TreeSet();
    //
    //        synchronized (bcc.getLock()) {
    ////            TreeSet sortedInst = bcc.getSortedInstantiation();
    ////
    ////            // resort here
    ////            for (Iterator i = sortedInst.iterator(); i.hasNext(); ) {
    ////                JointProbEntry entry = (JointProbEntry) i.next();
    ////                JointProbEntry newEntry = new JointProbEntry(entry.key, rescoreEntry(entry.key));
    ////                newSortedInst.add(newEntry);
    ////            }
    ////
    ////            // replace the sorted instantiations
    ////            bcc.setSortedInstantiation(newSortedInst);
    //        }
    //
    //        return newSortedInst;
    //    }
    //
    //    protected double rescoreEntry(Hashtable entry) {
    //        assert (tempResult != null && entry != null);
    //        double p = 1.0;
    //
    //        for (Enumeration e = entry.keys(); e.hasMoreElements(); ) {
    //            Object key = e.nextElement();
    //            Object value = entry.get(key);
    //            Hashtable tbl = (Hashtable) tempResult.get(key);
    //            Double val = (Double) tbl.get(value);
    //            if (val != null) {
    //                p *= ((Double) val).doubleValue();
    //            }
    //        }
    //
    //        return p;
    //    }

    /**
     * @see edu.ksu.cis.bnj.bbn.inference.Inference#getName()
     */
    public String getName() {
        return "AIS BCC Hybrid";
    }

    /**
     * @return
     */
    public int getAisUpdateFrequency() {
        return aisUpdateFrequency;
    }

    /**
     * @param i
     */
    public void setAisUpdateFrequency(int i) {
        aisUpdateFrequency = i;
    }

    public Hashtable getAISresult(Vector cutsetNodeNames, int numIterations) {
        Hashtable ht = new Hashtable();
        ais = new AIS(g, numIterations);
        ais.generateData(true);
        InferenceResult result = ais.getMarginals();
        BBNCPF[] icpt = ais.getICPT();
        BBNNode[] nodes = ais.getNodes();
        Vector cutsetNodeValues = new Vector();

        //for each possible instantiation of cutsetNodeNames,
        //make a new entry in ht
        //its value will be the product of ICPT entries
        //corresponding to cutset node instantiations
        //(average the row entries for the cutset value)

        int numInstants = 1;

        for (int i = 0; i < cutsetNodeNames.size(); i++) {
            BBNNode node = (BBNNode) g.getNode(cutsetNodeNames.elementAt(i).toString());
            BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();
            cutsetNodeValues.addElement(dval);
            numInstants *= dval.size();
        }

        countArray = new int[cutsetNodeNames.size()];
        for (int i = 0; i < countArray.length; i++) {
            countArray[i] = 0;
        }

        for (int i = 0; i < numInstants; i++) {
            Hashtable retEntry = getNextInstant(cutsetNodeValues, cutsetNodeNames, i);
            double val = 1.0;
            Enumeration e = retEntry.keys();
            while (e.hasMoreElements()) {
                String singleNodeName = e.nextElement().toString();
                String value = retEntry.get(singleNodeName).toString();
                Hashtable singleEntry = new Hashtable();
                singleEntry.put(singleNodeName, value);
                for (int k = 0; k < nodes.length; k++) {
                    BBNNode node = nodes[k];
                    String nodeName = node.getLabel();
                    if (nodeName.equals(singleNodeName)) {
                        double queryVal = icpt[k].query(retEntry);
                        int numICPTentries = icpt[k].getTable().size();
                        int numNodeValues = ((BBNDiscreteValue) node.getValues()).size();
                        queryVal /= (numICPTentries / numNodeValues);
                        val *= queryVal;
                    }
                }
            }
            ht.put(retEntry, new Double(val));
        }
        System.out.println(ht.toString());
        return ht;
    }

    private Hashtable getNextInstant(Vector values, Vector nodeNames, int iteration) {
        Hashtable ht = new Hashtable();
        int[] valueSizes = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            BBNDiscreteValue dv = (BBNDiscreteValue) values.elementAt(i);
            valueSizes[i] = dv.size();
        }

        if (iteration != 0) {

            for (int i = 0; i < values.size(); i++) {
                if (countArray[i] < (valueSizes[i] - 1)) {
                    countArray[i]++;
                    for (int j = 0; j < i; j++) {
                        countArray[j] = 0;
                    }
                    break;
                }
            }
        }

        //now return hashtable based on current countArray values
        for (int i = 0; i < values.size(); i++) {
            BBNDiscreteValue dv = (BBNDiscreteValue) values.elementAt(i);
            Iterator it = dv.iterator();
            int count = 0;
            String val = "";
            while (it.hasNext()) {
                val = it.next().toString();
                if (count == countArray[i]) {
                    break;
                }
                count++;
            }
            ht.put(nodeNames.elementAt(i), val);
        }

        return ht;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String evidenceFile = params.getString("-e");

        if (inputFile == null) {
            System.out.println("Usage: edu.ksu.cis.bnj.bbn.inference.cutset.AISBCC -i:inputfile [-e:evidencefile]");
            return;
        }

        BBNGraph g = BBNGraph.load(inputFile);
        if (evidenceFile != null) g.loadEvidence(evidenceFile);
        AISBCC bridge = new AISBCC(g);

        //testing call, for Asia
        Vector cutsetNodes = new Vector();
        cutsetNodes.addElement("VisitAsia");
        cutsetNodes.addElement("Smoking");
        cutsetNodes.trimToSize();

        Hashtable ht = bridge.getAISresult(cutsetNodes, 1000);
    }

}
