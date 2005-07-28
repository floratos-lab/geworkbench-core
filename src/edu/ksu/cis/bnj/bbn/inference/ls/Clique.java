package edu.ksu.cis.bnj.bbn.inference.ls;

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

import edu.ksu.cis.bnj.bbn.BBNCPF;
import edu.ksu.cis.bnj.bbn.BBNConstant;
import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNNode;

import java.util.*;

/**
 * <P>Clique representation.
 * <p/>
 * <P>The main methods are lambdaPropagation, piPropagation, setLambdaMessage,
 * and setPiMessage.
 *
 * @author Roby Joehanes
 */
public class Clique extends BBNNode implements Cloneable {

    protected LinkedList cliques = new LinkedList();
    protected LinkedList s = new LinkedList();  // Clique's S
    protected LinkedList r = new LinkedList();
    protected LinkedList baseNodes = new LinkedList(); // The base nodes (i.e. the nodes that assigned to this clique)

    // caches
    protected LinkedList lambdaMessages;
    protected LinkedList sNames;
    protected int childSize = 0;
    protected int reportedChild = 0;
    protected boolean isRoot = false;


    public Clique(edu.ksu.cis.kdd.util.graph.Clique clique) {
        cliques.addAll(clique.getNodeSet());
        s.addAll(clique.getS());
        r.addAll(clique.getR());
        baseNodes.addAll(clique.getBaseNodes());
        setName(clique.getLabel());
        setLabel(clique.getLabel());

        if (cliques.size() == 0) {
            throw new RuntimeException("Something's wrong: Empty clique");
        }

        LinkedList nodeNames = new LinkedList();
        for (Iterator i = cliques.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            nodeNames.add(node.getLabel());
        }
        cpf = new BBNCPF(nodeNames);

        iterate((LinkedList) cliques.clone(), new Hashtable());

        //System.out.println("Initial Phi for cluster "+getName());
        //System.out.println(cpf.dumpPDF());

        // precache some stuff
        lambdaMessages = new LinkedList();
        List children = clique.getChildren();
        if (children != null) childSize = children.size();
        List parents = clique.getParents();
        if (parents == null || parents.size() == 0) isRoot = true;
    }

    protected void iterate(LinkedList nodeList, Hashtable curInst) {
        BBNNode node = (BBNNode) nodeList.removeFirst();
        BBNDiscreteValue dval = (BBNDiscreteValue) node.getValues();

        for (Iterator i = dval.iterator(); i.hasNext();) {
            Object value = i.next();
            curInst.put(node.getLabel(), value.toString());
            if (nodeList.size() == 0) {
                double result = 1.0;
                for (Iterator j = baseNodes.iterator(); j.hasNext();) {
                    BBNNode subNode = (BBNNode) j.next();
                    double temp;
                    if (subNode.isEvidence() && subNode.getEvidenceValue().equals(curInst.get(subNode.getLabel()))) {
                        temp = subNode.getCPF().query(curInst);
                    } else {
                        temp = subNode.query(curInst);
                    }

                    result *= temp;
                }

                cpf.put((Hashtable) curInst.clone(), new BBNConstant(result));
            } else {
                iterate(nodeList, curInst);
            }
        }
        nodeList.addFirst(node);
    }

    public void filterEvidenceNodes(Set nodes) {
        if (nodes != null) {
            LinkedList relevantNodes = new LinkedList();
            relevantNodes.addAll(nodes);
            relevantNodes.retainAll(cliques);

            if (relevantNodes.size() > 0) {
                s.removeAll(relevantNodes);
                r.removeAll(relevantNodes);
                Hashtable evidenceTable = new Hashtable();
                for (Iterator i = relevantNodes.iterator(); i.hasNext();) {
                    BBNNode node = (BBNNode) i.next();
                    evidenceTable.put(node.getLabel(), node.getEvidenceValue());
                }
                cpf.zeroEntryExcept(evidenceTable);
            }
        }

        sNames = new LinkedList();
        for (Iterator i = s.iterator(); i.hasNext();) {
            BBNNode node = (BBNNode) i.next();
            sNames.add(node.getLabel());
        }
    }

    public int getChildrenSize() {
        return childSize; // use precached data instead
    }

    public void lambdaPropagation() {
        BBNCPF lambda = null;


        if (s.size() > 0) {
            lambda = cpf.extract(sNames);

            Hashtable phiTable = cpf.getTable();
            for (Enumeration e = phiTable.keys(); e.hasMoreElements();) {
                Hashtable query = (Hashtable) e.nextElement();
                double lambdaValue = lambda.query(query);
                if (lambdaValue > 0) {
                    double result = ((BBNConstant) phiTable.get(query)).getValue();
                    phiTable.put(query, new BBNConstant(result / lambdaValue));
                }
            }
        }

        //System.out.println("Phi for cluster "+getName());
        //System.out.println(cpf.dumpPDF());
        //System.out.println("Lambda value is:");
        //System.out.println(lambda.dumpPDF());

        if (!isRoot) {
            ((Clique) getParents().get(0)).setLambdaMessage(lambda);
        }
    }

    public void piPropagation() {
        for (Iterator i = getChildren().iterator(); i.hasNext();) {
            Clique clique = (Clique) i.next();
            if (clique.sNames.size() > 0) {
                BBNCPF pi = cpf.extract(clique.sNames);
                clique.setPiMessage(pi);
                //System.out.println("Pi for cluster "+clique.getLabel());
                //System.out.println(pi.dumpPDF());
            } else {
                clique.setPiMessage(null);
            }
        }
    }

    public void setLambdaMessage(BBNCPF lambda) {
        reportedChild++;
        if (lambda != null) lambdaMessages.add(lambda);
        if (reportedChild < childSize) return;

        // multiply phi
        Hashtable phiTable = cpf.getTable();
        for (Enumeration e = phiTable.keys(); e.hasMoreElements();) {
            Hashtable query = (Hashtable) e.nextElement();
            double result = ((BBNConstant) phiTable.get(query)).getValue();
            for (Iterator i = lambdaMessages.iterator(); i.hasNext();) {
                BBNCPF scpt = (BBNCPF) i.next();
                result *= scpt.query(query);
            }
            phiTable.put(query, new BBNConstant(result));
        }

        lambdaPropagation();
        // clear the cache, avoid storing too many things into the memory
        lambdaMessages = new LinkedList();

        if (isRoot) {
            // p = cpf; // no need
            // check if p is all zeroes (not yet), I think it is not needed
            // unless some serious errors are found.
            piPropagation();
        }
    }

    public void setPiMessage(BBNCPF pi) {
        // multiply phi
        if (pi != null) {
            Hashtable phiTable = cpf.getTable();
            for (Enumeration e = phiTable.keys(); e.hasMoreElements();) {
                Hashtable query = (Hashtable) e.nextElement();
                double result = ((BBNConstant) phiTable.get(query)).getValue();
                result *= pi.query(query);
                phiTable.put(query, new BBNConstant(result));
            }
        }

        if (childSize > 0) {
            piPropagation();
        }
    }

    /**
     * @return LinkedList
     */
    public LinkedList getBaseNodes() {
        return baseNodes;
    }

}
