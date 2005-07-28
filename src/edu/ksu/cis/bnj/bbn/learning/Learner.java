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

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.FileClassLoader;
import edu.ksu.cis.kdd.util.gui.OptionGUI;
import edu.ksu.cis.kdd.util.gui.Optionable;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Learning
 *
 * @author Roby Joehanes
 */
public abstract class Learner implements Optionable {
    public static final String learnerClassName = "edu.ksu.cis.bnj.bbn.learning.Learner";
    public static final String OPT_CALCULATE_RMSE = "rmse";
    protected Hashtable options = getDefaultOptions();

    protected Data data;

    // Whether we should compute CPT or only the edges
    // Just to save some time if we don't need the CPTs
    protected boolean calculateCPT = true;

    protected BBNNode[] bbnNodes;
    protected Hashtable indexTable;

    public Learner() {
    }

    /**
     * Constructor for Learner.
     */
    public Learner(Data t) {
        data = t;
    }

    /**
     * @return Table
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets the tuples.
     *
     * @param tuples The tuples to set
     */
    public void setData(Data tuples) {
        this.data = tuples;
    }

    /**
     * This method will be invoked prior to getGraph. The purpose is
     * to let the learner object to purge caches and stuff.
     */
    public void initialize() {
    }

    /**
     * The learning algorithm is here
     *
     * @return BBNGraph
     */
    public abstract BBNGraph getGraph();

    /**
     * @return boolean
     */
    public boolean isCalculateCPT() {
        return calculateCPT;
    }

    /**
     * Sets whether we should calculate the CPT.
     *
     * @param calculateCPT
     */
    public void setCalculateCPT(boolean calculateCPT) {
        this.calculateCPT = calculateCPT;
    }

    protected BBNGraph populateNodes() {
        BBNGraph graph = new BBNGraph();
        graph.setName(data.getName());
        indexTable = new Hashtable();
        LinkedList names = new LinkedList();
        Hashtable nameTable = new Hashtable();
        boolean isSingle = (data instanceof Table) || ((data instanceof Database) && ((Database) data).tableCount() == 1);
        for (Iterator i = data.getAttributes().iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            String attrName = (isSingle) ? attr.getName() : attr.getFullyQualifiedName();
            names.add(attrName);
            nameTable.put(attrName, attr);
        }
        String[] nodes = (String[]) names.toArray(new String[0]);
        //Attribute[] duh = (Attribute[]) data.getAttributes()
        int max = nodes.length;
        bbnNodes = new BBNNode[max];
        for (int i = 0; i < max; i++) {
            Attribute attr = (Attribute) nameTable.get(nodes[i]); //table.getAttribute(nodes[i]);
            if (attr.isPrimaryKey() || attr.isReferenceKey()) continue;
            BBNNode node = new BBNNode();
            node.setName(nodes[i]);
            node.setLabel(nodes[i]);
            HashSet valueSet = new HashSet();
            for (Iterator j = attr.getValues().iterator(); j.hasNext();) {
                valueSet.add(j.next().toString().trim());
            }
            node.setValues(new BBNDiscreteValue(valueSet));
            bbnNodes[i] = node;
            indexTable.put(node.getLabel(), new Integer(i));
            graph.add(node);
        }

        return graph;
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

    public void setOutputFile(String fn) {
        options.put(OPT_OUTPUT_FILE, fn);
    }

    public String getOutputFile() {
        return (String) options.get(OPT_OUTPUT_FILE);
    }

    public void setCalculateRMSE(boolean b) {
        options.put(OPT_CALCULATE_RMSE, new Boolean(b));
    }

    public boolean getCalculateRMSE() {
        return ((Boolean) options.get(OPT_CALCULATE_RMSE)).booleanValue();
    }

    public static Learner load(String className, Data t) {
        try {
            Learner l = (Learner) FileClassLoader.loadAndInstantiate(className, learnerClassName, null, new Class[]{Data.class}, new Object[]{t});
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void processParameters(String[] args) {
    }

    public String getName() {
        return "Unnamed";
    }
}
