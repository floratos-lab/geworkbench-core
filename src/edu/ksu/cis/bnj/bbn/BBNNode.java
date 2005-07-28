package edu.ksu.cis.bnj.bbn;

/*
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
 *
 */

import edu.ksu.cis.kdd.util.graph.Graph;
import edu.ksu.cis.kdd.util.graph.Node;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <P>A wrapper for BBNNode. Contains query-related functions.
 * <p/>
 * <P>The constants (NORMAL, UTILITY, DECISION) here define the node types. Note
 * that we still allow evidence values to be loaded in the node in any type.
 *
 * @author Roby Joehanes
 */
public class BBNNode extends Node implements Cloneable {

    /**
     * Denotes whether this node is a normal node
     */
    public static final int NORMAL = 0;
    /**
     * Denotes whether this node is a utility node
     */
    public static final int UTILITY = 1;
    /**
     * Denotes whether this node is a decision node
     */
    public static final int DECISION = 2;

    /**
     * This is the node's evidence value. If null, then the node is not an
     * evidence node.
     */
    protected Object evidenceValue = null;

    /**
     * Set of possible values of the node (or a function that denotes the range
     * of values, if it is continuous case). For example: { yes, no }
     */
    protected BBNValue values = null;

    /**
     * Sets the node type. Defaults to normal.
     */
    protected int type = NORMAL;

    /**
     * We store the PDF in a hashtable.
     */
    protected BBNCPF cpf;

    public BBNNode(Graph owner, String name) {
        super(owner, name);
    }

    /**
     *
     */
    public BBNNode() {
        super();
    }


    /**
     * Equals
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof BBNNode)) return false;
        BBNNode n = (BBNNode) o;
        return property.equals(n.property) && getName().equals(n.getLabel());
    }

    /**
     * Returns the evidence value. null means this node is not an evidence node
     *
     * @return Object
     */
    public Object getEvidenceValue() {
        return evidenceValue;
    }

    /**
     * Sets the evidence value.
     *
     * @param evidenceValue The evidence value to set
     */
    public void setEvidenceValue(Object evidenceValue) {
        if (isUtility())
            throw new RuntimeException("Cannot set evidence to a utility node!");
        if (evidenceValue != null && !values.contains(evidenceValue))
            throw new RuntimeException("The evidence value must be in one of the possible values!");
        this.evidenceValue = evidenceValue;
    }

    /**
     * Unsets the evidence value.
     */
    public void unsetEvidenceValue() {
        evidenceValue = null;
    }

    /**
     * Check whether this node is an evidence node or not
     *
     * @return boolean
     */
    public boolean isEvidence() {
        return evidenceValue != null;
    }

    public Hashtable getParentsEvidenceSet() {
        Hashtable parentEvidenceSet = new Hashtable();
        for (Iterator parents = (getParents()).iterator(); parents.hasNext();) {
            BBNNode parentNode = (BBNNode) parents.next();
            if (parentNode.isEvidence())
                parentEvidenceSet.put(parentNode.getLabel(), parentNode.getEvidenceValue());
            else
                throw new RuntimeException("Evidence is not yet set to " + parentNode);
        }
        return parentEvidenceSet;
    }

    /**
     * Check whether this node is a query node or not. Basically a negation of
     * isEvidence.
     *
     * @return boolean
     */
    public boolean isQuery() {
        return evidenceValue == null;
    }

    public void reconstructCPF() {
        Set set = getParentNames();
        set.add(getName());
        cpf = new BBNCPF(set);
    }

    /**
     * Returns the value(s).
     *
     * @return BBNValue
     */
    public BBNValue getValues() {
        return values;
    }

    /**
     * Sets the value(s).
     *
     * @param value The value to set
     */
    public void setValues(BBNValue value) {
        this.values = value;
    }

    /**
     * Reset the cache
     */
    public void resetCache() {

    }

    /**
     * Queries to the CPT (or CPF, in case of continuous value).
     *
     * @param values The query values in a hash table
     * @return double the return value.
     */
    public double getCPFValue(Hashtable values) {
        if (cpf == null) reconstructCPF();

        // if the node is an evidence node
        if (evidenceValue != null) {
            if (!isDecision()) {
                // if it is the correct evidence value, immediately return 1.0
                Object val = values.get(getName());
                if (val != null && val.equals(evidenceValue))
                    return 1.0;
                // otherwise, return 0.0
                return 0.0;
            } else {
                // We don't know how to handle a decision node with an evidence value
                throw new RuntimeException("Don't know how to handle a decision node with an evidence value yet");
            }
        }

        return cpf.get(values);
    }

    /**
     * An alias to getCPFValue
     *
     * @param v
     * @return double
     * @see getCPFValue(Hashtable)
     */
    public double query(Hashtable v) {
        return getCPFValue(v);
    }


    /**
     * Populating the conditional probability function (CPF)
     *
     * @param q The query
     * @param v The actual value
     */
    public void putCPFValue(Hashtable q, BBNPDF v) {
        if (cpf == null) reconstructCPF();
        cpf.put(q, v);
    }

    /**
     * Removing a specific entry from CPF.
     *
     * @param q the query
     */
    public void removeCPFValue(Hashtable q) {
        if (cpf == null) reconstructCPF();
        cpf.remove(q);
    }

    /* added by prashanth */
    public List queryColumn(Hashtable values) {
        if (cpf == null) reconstructCPF();
        return cpf.queryColumn(values);
    }


    /**
     * Returns the type.
     *
     * @return int
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type The type to set
     */
    public void setType(int type) {
        if (type != NORMAL && type != UTILITY && type != DECISION)
            throw new RuntimeException("Invalid node type");
        if (type == UTILITY && evidenceValue != null)
            throw new RuntimeException("Cannot assign an evidence node as a utility_node!");
        this.type = type;
    }

    /**
     * To enquire whether this node is a decision node
     *
     * @return boolean
     */
    public boolean isDecision() {
        return type == DECISION;
    }

    /**
     * To enquire whether this node is a utility node
     *
     * @return boolean
     */
    public boolean isUtility() {
        return type == UTILITY;
    }

    /**
     * Returns the CPF.
     *
     * @return Hashtable
     */
    public BBNCPF getCPF() {
        if (cpf == null) reconstructCPF(); // Bug fix courtesy of Anshu Sakseena
        return cpf;
    }

    /**
     * Sets the CPF.
     *
     * @param cpf The cpf to set
     */
    public void setCPF(Hashtable cpf) {
        reconstructCPF();
        this.cpf.setTable(cpf);
    }

    /**
     * Sets the CPF.
     *
     * @param cpf The cpf to set
     */
    public void setCPF(BBNCPF cpf) {
        this.cpf = cpf;
    }

    /**
     * Resets the CPF to null
     */

    public void resetCPF() {
        this.cpf = null;
    }

    /**
     * To dump more elaborate info. For debugging
     *
     * @return String the info
     */
    public String toVerboseString() {
        StringBuffer buf = new StringBuffer();
        String ln = System.getProperty("line.separator"); //$NON-NLS-1$

        String attr = ""; //$NON-NLS-1$
        if (evidenceValue != null) attr += "Evidence = " + evidenceValue + ",";
        if (isDecision())
            attr += "Decision";
        else if (isUtility()) attr += "Utility";
        if (!"".equals(attr)) attr = ", (" + attr + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buf.append("Node " + getName() + attr + ln);
        buf.append("Parents " + getParents() + ln);
        if (!isUtility()) buf.append("Values = " + getValues() + ln);

        if (!isDecision()) {
            buf.append("CPT:" + ln);
            buf.append(cpf + ln);
        }

        return buf.toString();
    }

    /**
     * Clone the node. Cache is not cloned. The owner graph is empty (you have
     * to set this manually).
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        BBNNode node = new BBNNode();
        if (cpf != null) node.cpf = (BBNCPF) cpf.clone();
        if (values != null) node.values = (BBNValue) values.clone();
        if (property != null) node.property = (Hashtable) property.clone();
        node.type = type;
        node.name = name;
        node.setLabel(getLabel());
        return node;
    }


}
