package edu.ksu.cis.bnj.bbn.prm;

/*
 * Created on Wed 18 Jun 2003
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

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.learning.LearnerScore;
import oracle.jdbc.driver.OracleDriver;

import java.sql.*;
import java.util.*;

/**
 * @author Prashanth Boddhireddy (pbo8844@ksu.edu)
 */
public class PRMK2_handCoded {
    private int mNumberOfNodes = 0;
    private String mGraphName = "ToyUniversity", mAttributeTableNames[], mAttributeNames[], mTableNames[];
    private int mNodeArity[];
    private List mAttributeList[];
    LinkedList[] mParentTable;
    private int tableSize = 3000;
    double lnFactorialCache[];
    public BBNGraph mBBNGraph;
    public Connection con;
    public Statement stmt;


    public PRMK2_handCoded() {
        mAttributeNames = new String[8];
        mAttributeList = new List[8];
        mAttributeTableNames = new String[8];
        mNodeArity = new int[8];
        mTableNames = new String[4];
        for (int i = 0; i < 8; i++)
            mNodeArity[i] = 3;
        mTableNames[0] = "STUDENT1";
        mTableNames[1] = "INSTRUCTOR1";
        mTableNames[2] = "REGISTRATION1";
        mTableNames[3] = "COURSE1";
        mAttributeNames[0] = "INTELLIGENCE";
        mAttributeTableNames[0] = mTableNames[0];
        mAttributeList[0] = new LinkedList();
        mAttributeList[0].add(new String("High"));
        mAttributeList[0].add(new String("Medium"));
        mAttributeList[0].add(new String("Low"));
        mAttributeNames[1] = "RANKING";
        mAttributeTableNames[1] = mTableNames[0];
        mAttributeList[1] = new LinkedList();
        mAttributeList[1].add(new String("High"));
        mAttributeList[1].add(new String("Medium"));
        mAttributeList[1].add(new String("Low"));
        mAttributeNames[2] = "POPULARITY";
        mAttributeTableNames[2] = mTableNames[1];
        mAttributeList[2] = new LinkedList();
        mAttributeList[2].add(new String("High"));
        mAttributeList[2].add(new String("Medium"));
        mAttributeList[2].add(new String("Low"));
        mAttributeNames[3] = "TEACHINGABILITY";
        mAttributeTableNames[3] = mTableNames[1];
        mAttributeList[3] = new LinkedList();
        mAttributeList[3].add(new String("High"));
        mAttributeList[3].add(new String("Medium"));
        mAttributeList[3].add(new String("Low"));
        mAttributeNames[4] = "GRADE";
        mAttributeTableNames[4] = mTableNames[2];
        mAttributeList[4] = new LinkedList();
        mAttributeList[4].add(new String("A"));
        mAttributeList[4].add(new String("B"));
        mAttributeList[4].add(new String("C"));
        mAttributeNames[5] = "SATISFACTION";
        mAttributeTableNames[5] = mTableNames[2];
        mAttributeList[5] = new LinkedList();
        mAttributeList[5].add(new String("High"));
        mAttributeList[5].add(new String("Medium"));
        mAttributeList[5].add(new String("Low"));
        mAttributeNames[6] = "RATING";
        mAttributeTableNames[6] = mTableNames[3];
        mAttributeList[6] = new LinkedList();
        mAttributeList[6].add(new String("High"));
        mAttributeList[6].add(new String("Medium"));
        mAttributeList[6].add(new String("Low"));
        mAttributeNames[7] = "DIFFICULTY";
        mAttributeTableNames[7] = mTableNames[3];
        mAttributeList[7] = new LinkedList();
        mAttributeList[7].add(new String("High"));
        mAttributeList[7].add(new String("Medium"));
        mAttributeList[7].add(new String("Low"));
        //display(mAttributeNames);
        //display(mAttributeTableNames);
        mNumberOfNodes = 8;
        mBBNGraph = getBBNGraph();
        lnFactorialCache = new double[tableSize];
        for (int i = 2; i < tableSize; i++) {
            lnFactorialCache[i] = lnFactorialCache[i - 1] + Math.log(i);
        }
        try {
            getDatabaseConnection();
        } catch (SQLException e) {
            // TO DO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TO DO Auto-generated catch block
            e.printStackTrace();
        }
        applyK2();
        displayTable(mParentTable);
    }

    public void displayTable(LinkedList[] listArray) {
        LinkedList[] parentList = new LinkedList[listArray.length];
        for (int i = 0; i < listArray.length; i++) {
            parentList[i] = new LinkedList();
            for (Iterator j = listArray[i].iterator(); j.hasNext();) {
                int value = ((Integer) j.next()).intValue();
                parentList[i].add(mAttributeNames[value]);
            }
        }

        System.out.println(" " + " parentsList for node <---- node");
        for (int i = 0; i < listArray.length; i++) {
            System.out.println(i + " " + parentList[i] + "<---- " + mAttributeNames[i]);
        }
    }

    /*	public void display(String[] string)
        {
            for (int i = 0; i < string.length; i++)
                System.out.print(string[i] + " ");
            System.out.println();
        }*/


    private void applyK2() {
        int noOfNodes = mBBNGraph.getVerticesCount();
        int nodes[] = new int[noOfNodes];
        int parentLimit = 2;
        mParentTable = new LinkedList[noOfNodes];
        for (int i = 0; i < noOfNodes; i++)
            mParentTable[i] = new LinkedList();
        for (int i = 0; i < noOfNodes; i++)        // default ordering
            nodes[i] = i;
        for (int i = 0; i < noOfNodes; i++) {
            int node_i = nodes[i];
            double p_old = getScore(node_i, LearnerScore.NO_CANDIDATES, mParentTable[i]);
            System.out.println("old_score is " + p_old);
            while (mParentTable[node_i].size() < parentLimit) {
                int z = -1;
                double p_new = p_old;
                for (int j = 0; j < i; j++) {
                    if (i == j) continue;
                    int current_z = nodes[j];
                    if (mParentTable[node_i].contains(new Integer(current_z))) continue;

                    double current_p = getScore(node_i, current_z, mParentTable[node_i]);
                    //System.out.println("Adding "+nodes[j]+" makes p = "+current_p);
                    if (p_new < current_p) {
                        p_new = current_p;
                        z = current_z;
                    }
                }
                if ((p_new > p_old) && z > -1) {
                    p_old = p_new;
                    mParentTable[node_i].add(new Integer(z));
                } else
                    break;
            }
        }

    }

    public double getScore(int nodeIndex, int candidateParents, LinkedList parentTable) {
        double score = 0;
        try {
            score = getScorefromDatabase(nodeIndex, candidateParents, parentTable);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println("The currentAttribute is " + mAttributeNames[nodeIndex]);

        return score;
    }

    public double getScorefromDatabase(int nodeIndex, int currentParentNodeIndex, LinkedList parentSet) throws SQLException, ClassNotFoundException {
        String[] attributeNames = getQueryAttributeNames(nodeIndex, currentParentNodeIndex, parentSet);
        String tableNames[] = getQueryTableNames(nodeIndex, currentParentNodeIndex, parentSet);

        String attributeQuery = preprocessArrayToString(attributeNames);
        String tableNameQuery = preprocessArrayToString(tableNames);
        String sqlQuery = "SELECT ";
        sqlQuery += " COUNT(*) ";
        sqlQuery += "FROM " + tableNameQuery;
        sqlQuery += "GROUP BY " + attributeQuery;
        //	System.out.println(sqlQuery);
        ResultSet rs = stmt.executeQuery(sqlQuery);
        ResultSetMetaData instructorTable = rs.getMetaData();
        int numberOfColumns = instructorTable.getColumnCount();
        //	System.out.println("number Of Columns " + numberOfColumns);
        LinkedList integerList = new LinkedList();
        int nodeArity = mNodeArity[nodeIndex];
        double finalScore = 0;
        int count = 0;
        int N_ij = 0;
        double logValue = this.lnFactorialCache[nodeArity - 1];
        while (rs.next()) {
            String resultString = rs.getString(1);
            int N_ijk = Integer.parseInt(resultString);
            integerList.add(new Integer(N_ijk));
            N_ij += N_ijk;
            logValue += lnFactorialCache[N_ijk];
            count++;
            if ((count > 0) && (count % nodeArity == 0)) {
                N_ij += nodeArity - 1;
                logValue += lnFactorialCache[N_ij];
                logValue -= lnFactorialCache[nodeArity - 1];
                finalScore += logValue;
                logValue = 0;
                N_ij = 0;
            }
        }
        return finalScore;
    }

    public void displayList(LinkedList list) {
        for (Iterator i = list.iterator(); i.hasNext();) {
            int value = ((Integer) i.next()).intValue();
            System.out.println("Value is " + value);
        }
    }

    public String[] getQueryAttributeNames(int nodeIndex, int currentNodeIndex, LinkedList parentSet) {
        int size = 1 + parentSet.size();
        if (currentNodeIndex != -1)
            size++;
        String[] attributeNames = new String[size];
        int count = 0;
        if (currentNodeIndex != -1) {
            attributeNames[count++] = mAttributeTableNames[currentNodeIndex] + "." + mAttributeNames[currentNodeIndex];
        }

        if (parentSet != null) {
            for (Iterator i = parentSet.iterator(); i.hasNext();) {
                int attributeIndex = ((Integer) i.next()).intValue();
                attributeNames[count++] = mAttributeTableNames[attributeIndex] + "." + mAttributeNames[attributeIndex];
            }
        }
        attributeNames[count] = mAttributeTableNames[nodeIndex] + "." + mAttributeNames[nodeIndex];
        return attributeNames;
    }

    public String[] getQueryTableNames(int nodeIndex, int currentNodeIndex, LinkedList parentSet) {
        int size = 1 + parentSet.size();
        if (currentNodeIndex != -1)
            size++;
        String[] attributeNames = new String[size];
        int count = 0;
        if (currentNodeIndex != -1)
            attributeNames[count++] = mAttributeTableNames[currentNodeIndex];
        if (parentSet != null) {
            for (Iterator i = parentSet.iterator(); i.hasNext();) {
                int attributeIndex = ((Integer) i.next()).intValue();
                attributeNames[count] = mAttributeTableNames[attributeIndex];
                count++;
            }
        }
        attributeNames[count] = mAttributeTableNames[nodeIndex];
        HashSet setValues = new HashSet();
        for (int i = 0; i < size; i++) {
            setValues.add(attributeNames[i]);
        }
        int tableNameSize = setValues.size();
        String tableNames[] = (String[]) setValues.toArray(new String[0]);
        return tableNames;
    }


    public String preprocessArrayToString(String[] attributesNames) {
        String queryAttributes = " ";
        int stringLength = attributesNames.length;
        for (int i = 0; i < stringLength; i++) {
            if (i < stringLength - 1)
                queryAttributes += (attributesNames[i] + ", ");
            else
                queryAttributes += (attributesNames[i] + " ");
        }
        return queryAttributes;
    }

    public static void main(String args[]) {
        PRMK2_handCoded prmK2 = new PRMK2_handCoded();
        //prmK2.display();
    }

    protected BBNGraph getBBNGraph() {
        System.out.println("IN GET BBN graph");
        BBNGraph graph = new BBNGraph();
        graph.setName(mGraphName);
        Hashtable indexTable = new Hashtable();
        BBNNode bbnNodes[] = new BBNNode[mNumberOfNodes];
        for (int j = 0; j < mAttributeNames.length; j++) {
            BBNNode node = new BBNNode();
            node.setName(mAttributeNames[j]);
            List attr = (List) mAttributeList[j];
            HashSet valueSet = new HashSet();
            for (Iterator l = attr.iterator(); l.hasNext();) {
                valueSet.add((String) l.next());
            }
            node.setValues(new BBNDiscreteValue(valueSet));
            bbnNodes[j] = node;
            System.out.println(valueSet);
            indexTable.put(node.getLabel(), new Integer(j));
            System.out.println(node);
            graph.add(node);
        }
        return graph;
    }


    public void getDatabaseConnection() throws SQLException, ClassNotFoundException {
        String userName = "pbo8844", password = "cis761", url = "jdbc:oracle:thin:@zaurak.cis.ksu.edu:1521:PROD", insertC = "insert into carsTable ";
        DriverManager.registerDriver(new OracleDriver());
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection(url, userName, password);
        stmt = con.createStatement();
    }


}
