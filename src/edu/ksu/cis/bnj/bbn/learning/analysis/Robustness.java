package edu.ksu.cis.bnj.bbn.learning.analysis;

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

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.analysis.Analyzer;
import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Robustness analysis for structure learner.
 *
 * @author Roby Joehanes
 */
public class Robustness extends Analyzer {

    protected Learner base;
    protected Data tuples;
    protected int bootstrapSamples = 200;
    protected int subsample = 50;
    protected LinkedHashMap result;

    /**
     *
     */
    public Robustness(Learner b) {
        setBaseLearner(b);
    }

    public Robustness(Learner b, int it, int s) {
        setBaseLearner(b);
        setBootstrapSamples(it);
        setSubSample(s);
    }

    /**
     * Get the robustness result. The return type should be HashMap
     * but let's generalize for the moment since testing framework is
     * not yet available.
     *
     * @return Object
     */
    public Object getResult() {
        Hashtable tempResult = new Hashtable();

        System.out.print("Iteration: ");
        for (int i = 0; i < bootstrapSamples; i++) {
            System.out.print((i + 1) + " ");
            if (i % 20 == 19) System.out.println();
            Data newTuples = tuples.subsample(subsample);
            base.setData(newTuples);
            base.initialize();
            BBNGraph g = base.getGraph();
            for (Iterator j = g.getEdges().iterator(); j.hasNext();) {
                /* The .toString() here is very important
                   We're just storing the string of the edge, not the
                   actual edge itself so that the edge (and subsequently
                   the graph) can be garbage collected by Java to minimize
                   the memory requirement.
                */
                Object edge = j.next().toString();
                Integer tally = (Integer) tempResult.get(edge);
                if (tally == null) {
                    tempResult.put(edge, new Integer(1));
                } else {
                    tempResult.put(edge, new Integer(tally.intValue() + 1));
                }
            }
        }
        System.out.println();

        TreeMap map = new TreeMap();
        // Sort
        for (Iterator i = tempResult.keySet().iterator(); i.hasNext();) {
            Object edge = i.next();
            Integer tally = (Integer) tempResult.get(edge);
            Set tallySet = (Set) map.get(tally);
            if (tallySet == null) tallySet = new HashSet();
            tallySet.add(edge);
            map.put(tally, tallySet);
        }
        result = new LinkedHashMap();

        // Reverse it so that it is in descending order
        LinkedList reverse = new LinkedList();

        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            Object tally = i.next();
            reverse.addFirst(tally);
        }

        for (Iterator i = reverse.iterator(); i.hasNext();) {
            Object tally = i.next();
            Set edges = (Set) map.get(tally);
            for (Iterator j = edges.iterator(); j.hasNext();) {
                Object edge = j.next();
                result.put(edge, tally);
            }
        }

        return result;
    }

    public void dump(OutputStream out) {
        getResult();
        Writer w = new OutputStreamWriter(out);

        try {
            String ln = System.getProperty("line.separator");
            w.write("Robustness result:" + ln);
            w.write("==================" + ln);
            for (Iterator i = result.keySet().iterator(); i.hasNext();) {
                Object edge = i.next();
                int count = ((Integer) result.get(edge)).intValue();
                w.write(edge + " == " + (count * 1.0 / bootstrapSamples) + " ( " + count + " / " + bootstrapSamples + " )" + ln);
            }
            w.flush(); // Flush is important so that the output is not truncated. 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Learning
     */
    public Learner getBaseLearner() {
        return base;
    }

    /**
     * Sets the base learner.
     *
     * @param base The base to set
     */
    public void setBaseLearner(Learner base) {
        this.base = base;
        tuples = base.getData();
    }

    /**
     * @return int
     */
    public int getBootstrapSamples() {
        return bootstrapSamples;
    }

    /**
     * @return int
     */
    public int getSubSample() {
        return subsample;
    }

    /**
     * Sets the iteration.
     *
     * @param iteration The iteration to set
     */
    public void setBootstrapSamples(int iteration) {
        this.bootstrapSamples = iteration;
    }

    /**
     * Sets the subsample.
     *
     * @param subsample The subsample to set
     */
    public void setSubSample(int subsample) {
        this.subsample = subsample;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.processCurrentParams(args);
        String[] subargs = Parameter.spliceSubModuleParams(args);
        String moduleName = Parameter.getSubModuleName(args);

        try {

            int bootstrapSample = params.getInt("-b", -1);
            int subsample = params.getInt("-s", -1);
            String inputFile = params.getString("-i");
            String outputFile = params.getString("-o");

            if (moduleName == null || bootstrapSample < 1 || subsample < 1 || inputFile == null) {
                System.out.println("Usage: edu.ksu.cis.bnj.bbn.learning.Robustness -b:#bootstrap -s:#subsample -i:inputfile [-o:outputfile] modulename [module_parameters]");
                System.out.println("E.g.: Robustness -b:200 -s:76 -o:result.txt -i:filename.arff k2 -s:1,2,3");
                System.out.println("Caution: All robustness paramaters must be placed BEFORE the module name");
                return;
            }

            Table tuples = Table.load(inputFile);

            Learner l = null;

            if (moduleName.equals("k2")) {
                l = new K2(tuples);
            } else if (moduleName.equals("sparse")) {
                l = null; //l = new SparseCandidate(tuples);
            }

            l.processParameters(subargs);
            l.setCalculateCPT(false);

            Robustness robust = new Robustness(l, bootstrapSample, subsample);
            if (outputFile != null) {
                robust.dump(outputFile);
            } else {
                robust.dump();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
