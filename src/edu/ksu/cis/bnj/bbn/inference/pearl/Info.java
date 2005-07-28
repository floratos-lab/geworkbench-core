package edu.ksu.cis.bnj.bbn.inference.pearl;

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

import edu.ksu.cis.bnj.bbn.BBNNode;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Class Info :
 * Stores and manages the Lambda Message ,Pi Message, Lambda Value,
 * Pi Value and Probability  given Evidence vectors for a node
 *
 * @author Siddarth Chandak
 */

public class Info implements Cloneable {
    int arity;
    protected int numberofparents, numberofchildren;
    protected Vector lambdaMessage[]; // message sent from child node to all of its parents
    protected Vector piMessage[]; // message sent from parent node to all of its children
    protected Vector piValue;    // Takes into account all probabilities "above" a node
    protected Vector lambdaValue; // Takes into account all probabilities "below" a node
    protected Vector probabilityGivenEvidence; // product of Lambda and Pi value
    protected Hashtable parentIndex;
    protected Hashtable childIndex;
    protected Hashtable valueTable;

    void initializeLambdaValue() {
        for (int i = 0; i < arity; i++) {
            lambdaValue.add(i, new Double(1.0));
        }
    }

    void initializeLambdaMessage(Vector parentarity) {
        for (int k = 0; k < numberofparents; k++) {
            Integer pArity = (Integer) parentarity.elementAt(k);
            lambdaMessage[k] = new Vector(pArity.intValue());

            for (int l = 0; l < pArity.intValue(); l++) {
                lambdaMessage[k].add(l, new Double(1.0));
            }
        }
    }

    void initializeProbabilityGivenEvidence() {
        for (int i = 0; i < arity; i++) {
            probabilityGivenEvidence.add(i, new Double(1.0));
        }
    }

    void initializePiMessage() {
        for (int k = 0; k < numberofchildren; k++) {
            piMessage[k] = new Vector(arity);
            for (int l = 0; l < arity; l++) {
                piMessage[k].add(l, new Double(1.0));
            }
        }
    }

    void initializePiValue() {
        for (int i = 0; i < arity; i++) {
            piValue.add(i, new Double(1.0));
        }
    }

    private Info() {
        // for cloning;
    }

    public Info(int currentarity, Vector parentarity, int parentnum, int childnum) {
        lambdaMessage = new Vector[parentnum];
        piMessage = new Vector[childnum];
        lambdaValue = new Vector(currentarity);
        piValue = new Vector(currentarity);
        probabilityGivenEvidence = new Vector(currentarity);
        numberofchildren = childnum;
        numberofparents = parentnum;
        arity = currentarity;
        valueTable = new Hashtable();
        parentIndex = new Hashtable(parentnum);
        childIndex = new Hashtable(childnum);

        initializeLambdaValue();
        initializePiMessage();

        if (parentnum == 0) {
            lambdaMessage = new Vector[0];
        } else {
            initializePiValue();
            initializeProbabilityGivenEvidence();
            initializeLambdaMessage(parentarity);
        }
    }

    public void setValueTable(String value, int index) {
        valueTable.put(value, new Integer(index));
    }

    public int getValueTable(String value) {
        //Integer index = ;
        return ((Integer) valueTable.get(value)).intValue();
    }

    public void setParentIndex(BBNNode parent, int index) {
        parentIndex.put(parent, new Integer(index));
    }

    public int getParentIndex(BBNNode parent) {
        //Integer index = ;
        return ((Integer) parentIndex.get(parent)).intValue();
    }

    public void setChildIndex(BBNNode child, int index) {
        childIndex.put(child, new Integer(index));
    }

    public int getChildIndex(BBNNode child) {
        //Integer index = ;
        return ((Integer) childIndex.get(child)).intValue();
    }

    //////////Error checking to be done////////////////////////////////////////

    public void setLambdaMessage(int parentIndex, int parentArityIndex, double value) {
        lambdaMessage[parentIndex].set(parentArityIndex, new Double(value));
    }

    public void setPiMessage(int childIndex, int arityIndex, double value) {
        piMessage[childIndex].set(arityIndex, new Double(value));
    }

    public void addPiValue(int arityIndex, double value) {
        piValue.add(arityIndex, new Double(value));
    }

    public void setPiValue(int arityIndex, double value) {
        piValue.set(arityIndex, new Double(value));
    }

    public void setLambdaValue(int arityIndex, double value) {
        lambdaValue.set(arityIndex, new Double(value));
    }

    public void addProbabilityGivenEvidence(int arityIndex, double value) {
        probabilityGivenEvidence.add(arityIndex, new Double(value));
    }

    public void setProbabilityGivenEvidence(int arityIndex, double value) {
        probabilityGivenEvidence.set(arityIndex, new Double(value));
    }

    public double getLambdaMessage(int parentIndex, int parentArityIndex) {
        Double value = (Double) lambdaMessage[parentIndex].get(parentArityIndex);
        return value.doubleValue();
    }

    public double getPiMessage(int childIndex, int arityIndex) {
        Double value = (Double) piMessage[childIndex].get(arityIndex);
        return value.doubleValue();
    }

    public double getPiValue(int arityIndex) {
        Double value = (Double) piValue.get(arityIndex);
        return value.doubleValue();
    }

    public double getLambdaValue(int arityIndex) {
        Double value = (Double) lambdaValue.get(arityIndex);
        return value.doubleValue();
    }

    public double getProbabilityGivenEvidence(int arityIndex) {
        Double value = (Double) probabilityGivenEvidence.get(arityIndex);
        return value.doubleValue();
    }

    public Object clone() {
        Info newInfo = new Info();
        newInfo.arity = arity;
        newInfo.childIndex = (Hashtable) childIndex.clone();

        newInfo.lambdaMessage = new Vector[lambdaMessage.length];
        for (int i = 0; i < lambdaMessage.length; i++) {
            newInfo.lambdaMessage[i] = (Vector) lambdaMessage[i].clone();
        }

        newInfo.lambdaValue = (Vector) lambdaValue.clone();
        newInfo.numberofchildren = numberofchildren;
        newInfo.numberofparents = numberofparents;
        newInfo.parentIndex = (Hashtable) parentIndex.clone();
        newInfo.probabilityGivenEvidence = (Vector) probabilityGivenEvidence.clone();
        newInfo.piValue = (Vector) piValue.clone();

        newInfo.piMessage = new Vector[piMessage.length];
        for (int i = 0; i < piMessage.length; i++) {
            newInfo.piMessage[i] = (Vector) piMessage[i].clone();
        }
        newInfo.valueTable = (Hashtable) valueTable.clone();

        return newInfo;
    }
}