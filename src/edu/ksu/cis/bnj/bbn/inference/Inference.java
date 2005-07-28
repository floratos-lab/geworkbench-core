package edu.ksu.cis.bnj.bbn.inference;

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
import edu.ksu.cis.kdd.util.FileClassLoader;
import edu.ksu.cis.kdd.util.gui.OptionGUI;
import edu.ksu.cis.kdd.util.gui.Optionable;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Abstract Inference class
 *
 * @author robbyjo
 */
public abstract class Inference implements Optionable {
    public static final String inferenceClassName = "edu.ksu.cis.bnj.bbn.inference.Inference";
    public static final String OPT_RUN_TYPE = "inferenceType";
    public static final int MAP = 2;
    public static final int MPE = 1;
    public static final int MARGINALS = 0;

    protected Hashtable options = getDefaultOptions();
    protected InferenceResult marginalsResult;

    protected BBNGraph graph = null;

    public Inference() {
    }

    public Inference(BBNGraph g) {
        this();
        setGraph(g);
    }

    /**
     * Returns the graph.
     *
     * @return BBNGraph
     */
    public BBNGraph getGraph() {
        return graph;
    }

    /**
     * Sets the graph.
     *
     * @param graph The graph to set
     */
    public void setGraph(BBNGraph graph) {
        this.graph = graph;
    }

    /**
     * Getting the result of the marginals (i.e. the belief revision)
     *
     * @return InferenceResult The marginals
     */
    public abstract InferenceResult getMarginals();

    public abstract String getName();

    /**
     * Get the Most Probable Explanation (MPE) values for each nodes.
     *
     * @return Hashtable the MPE in the format of NodeName -> MostProbableValue
     */
    public Hashtable getMPE() {
        InferenceResult resultCache = getMarginals();
        Hashtable mpe = new Hashtable();

        for (Iterator i = resultCache.keySet().iterator(); i.hasNext();) {
            String nodeName = (String) i.next();
            Hashtable tbl = (Hashtable) resultCache.get(nodeName);
            String highestValue = null;
            double highestProb = 0.0;
            for (Iterator j = tbl.keySet().iterator(); j.hasNext();) {
                String value = (String) j.next();
                double p = ((Double) tbl.get(value)).doubleValue();
                if (p > highestProb) {
                    highestProb = p;
                    highestValue = value;
                }
            }
            mpe.put(nodeName, highestValue);
        }

        return mpe;
    }

    /**
     * Get the Maximum Aposteriori Probabilities (MAP)
     *
     * @return Hashtable the MAP in the format of NodeName -> MostProbableValue
     */
    public Hashtable getMAP() {
        return null;
    }

    public OptionGUI getOptionsDialog() {
        return null;
    }

    public void setOptions(Hashtable optionTable) {
        options = optionTable;
    }

    public void setOption(String key, Object val) {
        options.put(key, val);
    }

    public Hashtable getDefaultOptions() {
        return new Hashtable();
    }

    public Hashtable getCurrentOptions() {
        return options;
    }

    public void setRunType(int type) {
        options.put(OPT_RUN_TYPE, new Integer(type));
    }

    public int getRunType() {
        Integer i = (Integer) options.get(OPT_RUN_TYPE);
        if (i == null) return MARGINALS;
        return i.intValue();
    }

    public void setOutputFile(String fn) {
        options.put(OPT_OUTPUT_FILE, fn);
    }

    public String getOutputFile() {
        return (String) options.get(OPT_OUTPUT_FILE);
    }

    public void execute() { // TODO: This is a hack. I hate it. Do it later
        // generic execute
        assert(graph != null);
        switch (getRunType()) {
            case MARGINALS:
                marginalsResult = getMarginals();
                String outputFile = getOutputFile();
                if (outputFile == null) {
                    marginalsResult.save(outputFile);
                }
                break;
            case MPE:
            case MAP:
                throw new RuntimeException("Not done yet");
        }
    }

    public InferenceResult getMarginalsResult() {
        return marginalsResult;
    }

    public static Inference load(String className, BBNGraph g) {
        try {
            Inference inf = (Inference) FileClassLoader.loadAndInstantiate(className, inferenceClassName, null, new Class[]{BBNGraph.class}, new Object[]{g});
            return inf;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
