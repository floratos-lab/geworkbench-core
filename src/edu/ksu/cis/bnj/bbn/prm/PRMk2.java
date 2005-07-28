package edu.ksu.cis.bnj.bbn.prm;

/*
 * Created on Thu 12 Jun 2003
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
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.bnj.bbn.learning.LearnerScore;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2OptionGUI;
import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.data.Tally;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.gui.OptionGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

/**
 * @author Prashanth Boddhireddy (pbo8844@ksu.edu)
 * @author Roby Joehanes
 */
public class PRMk2 extends Learner {
    //protected JDBCTally mJDBCTally;
    protected Tally mJDBCTally;
    protected Hashtable mIndexTable;
    protected int mParentLimit;
    protected BBNNode bbnNodes[];
    BBNGraph graph;
    public static int defaultParentLimit = 3;
    private String mGraphName = "ToyUniversity";
    private int mNoOfNodes = 0, mAttributeIndices[], mAttributeOrderArr[];
    private List mAttributeList[];
    LinkedList[] mParentTable;
    private static int defaultableSize = 3000;
    double lnFactorialCache[];
    public BBNGraph mBBNGraph;
    public LinkedList mAttributeOrderList;
    public boolean orderStringProper = true;
    public LinkedList mAttributeNames = new LinkedList();

    LinkedList mAllTables;
    Database mDatabase;

    public PRMk2(Database db, LinkedList attributeOrderList) {
        mDatabase = db;
        mAttributeOrderList = attributeOrderList;
        mJDBCTally = db.getTallyer(); // new JDBCTally(db);
        //((JDBCTally) mJDBCTally).dumpTallyStatus();  // robbyjo's patch
        //System.out.println("Break here"); // robbyjo's patch
        mParentLimit = defaultParentLimit;
        //		mAttributeIndices = mJDBCTally.getAttributeIndices();
        //		mNoOfNodes = mJDBCTally.getAttributeIndices().length;
        mAttributeIndices = mJDBCTally.getRelevantAttributeIndices();
        mNoOfNodes = mAttributeIndices.length;
        processOrderString();
        initializeCache();
    }

    /**
     * checks whether the orderString given by user contains all the attributes
     * and if so are the attributes are same as that of remote database.
     * and makes orderAttributeArr of int
     */
    private void processOrderString() {
        for (int i = 0; i < mNoOfNodes; i++) {
            Attribute attr = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[i]);
            mAttributeNames.add(i, attr.getName());
        }
        if (mAttributeOrderList != null) {
            for (Iterator i = mAttributeOrderList.iterator(); i.hasNext();) {
                String s = (String) i.next();
                if (!mAttributeNames.contains(s)) {
                    orderStringProper = false;
                    break;
                }
            }
        } else
            orderStringProper = false;
        mAttributeOrderArr = new int[mNoOfNodes];
        if (!orderStringProper) {
            System.out.println(":OrderString does not contain all the attributes");
            System.out.println("Processing default ordering ");
            for (int i = 0; i < mNoOfNodes; i++)
                mAttributeOrderArr[i] = i;
        } else {
            for (int i = 0; i < mNoOfNodes; i++)
                mAttributeOrderArr[i] = getAttributeIndex(i);
        }
    }

    /**
     * @param index
     * @return actual ordered int
     */
    private int getAttributeIndex(int index) {
        int orderedIndex = index;
        String attributeNameFromOrder = (String) mAttributeOrderList.get(index);
        int indexFromDatabase = getAttributeIndexFromDataBase(attributeNameFromOrder);
        return indexFromDatabase;
    }

    private int getAttributeIndexFromDataBase(String string) {
        int index = 0;
        for (int i = 0; i < mNoOfNodes; i++) {
            Attribute attr = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[i]);
            if (string.equals(attr.getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void initializeCache() {
        lnFactorialCache = new double[defaultableSize];
        for (int i = 2; i < defaultableSize; i++) {
            lnFactorialCache[i] = lnFactorialCache[i - 1] + Math.log(i);
        }
        mBBNGraph = getBBNGraph();
        applyK2();
        //System.out.println(mBBNGraph);
    }

    public void displayTable(LinkedList[] listArray) {
        LinkedList[] parentList = new LinkedList[listArray.length];
        for (int i = 0; i < listArray.length; i++) {
            parentList[i] = new LinkedList();
            for (Iterator j = listArray[i].iterator(); j.hasNext();) {
                int value = ((Integer) j.next()).intValue();
                Attribute attr = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[value]);
                parentList[i].add(attr.getName());
            }
        }

        //System.out.println(   " " + " parentsList for node <---- node");
        for (int i = 0; i < listArray.length; i++) {
            Attribute attr = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[i]);
            //System.out.println(i + " "+  parentList[i]+ "<---- " +  attr.getLabel());
        }
    }

    public BBNGraph getGraph() {
        return mBBNGraph;
    }

    protected BBNGraph getBBNGraph() {
        BBNGraph graph = new BBNGraph();
        graph.setName(mGraphName);
        mIndexTable = new Hashtable();
        bbnNodes = new BBNNode[mNoOfNodes];
        for (int j = 0; j < mNoOfNodes; j++) {
            BBNNode node = new BBNNode();
            Attribute attr = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[j]);
            List attributeValues = attr.getValues();
            node.setName(attr.getName());
            HashSet valueSet = new HashSet();
            for (Iterator l = attributeValues.iterator(); l.hasNext();)
                valueSet.add((String) l.next());
            node.setValues(new BBNDiscreteValue(valueSet));
            mIndexTable.put(node.getLabel(), new Integer(j));
            bbnNodes[j] = node;
            graph.add(node);
        }
        return graph;
    }


    private void applyK2() {
        int nodes[] = mAttributeOrderArr;
        mParentTable = new LinkedList[mNoOfNodes];
        for (int i = 0; i < mNoOfNodes; i++)
            mParentTable[i] = new LinkedList();
        for (int i = 0; i < mNoOfNodes; i++) {
            int node_i = nodes[i];
            double p_old = getScore(node_i, LearnerScore.NO_CANDIDATES, mParentTable[node_i]);
            while (mParentTable[node_i].size() < mParentLimit) {
                int z = -1;
                double p_new = p_old;
                for (int j = 0; j < i; j++) {
                    if (i == j) continue;
                    int current_z = nodes[j];
                    if (mParentTable[node_i].contains(new Integer(current_z))) continue;

                    double current_p = getScore(node_i, current_z, mParentTable[node_i]);
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

            BBNNode child = bbnNodes[node_i];
            for (Iterator j = mParentTable[node_i].iterator(); j.hasNext();) {
                int idx = ((Integer) j.next()).intValue();
                BBNNode parent = bbnNodes[idx];
                try {
                    mBBNGraph.addEdge(parent, child);
                } catch (Exception e) {
                    System.err.println("Error on adding parent " + parent);
                }
            }
        }

    }

    public double getScore(int nodeIndex, int candidateParent, LinkedList parentTable) {
        double score = 0;
        int startIndex = 1;
        int size = 1;
        if (candidateParent != -1) {
            startIndex++;
            size++;
        }
        size += parentTable.size();
        int attributeIndexArr[] = new int[size];
        attributeIndexArr[0] = mAttributeIndices[nodeIndex];
        //System.out.print(attributeIndexArr[0]);
        if (startIndex == 2) {
            attributeIndexArr[1] = mAttributeIndices[candidateParent];
            //System.out.print("," + attributeIndexArr[1]);
        }
        for (int i = startIndex; i < size; i++) {
            attributeIndexArr[i] = mAttributeIndices[((Integer) parentTable.get(i - startIndex)).intValue()];
            //System.out.print("," + attributeIndexArr[i]);
        }
        System.out.println();
        List linkedList = (List) mJDBCTally.groupedTally(attributeIndexArr);
        Attribute attribute = (Attribute) mJDBCTally.getUnderlyingData().getAttributes().get(mAttributeIndices[nodeIndex]);
        //Attribute attribute = mJDBCTally.getAtttribute(mAttributeIndices[nodeIndex]);
        int nodeArity = attribute.getArity();
        int count = 0;
        int N_ij = 0;
        double logValue = 0;
        for (int i = 0; i < linkedList.size(); i++) {
            int N_ijk = ((Integer) linkedList.get(i)).intValue();
            N_ij += N_ijk;
            logValue += calculateLogFactorial(N_ijk);
            count++;
            if ((count > 0) && (count % nodeArity == 0)) {
                N_ij += nodeArity - 1;
                logValue += calculateLogFactorial(N_ij);
                logValue -= calculateLogFactorial(nodeArity - 1);
                score += logValue;
                logValue = 0;
                N_ij = 0;
            }
        }
        return score;
    }

    private double calculateLogFactorial(int number) {
        if (number < defaultableSize)
            return lnFactorialCache[number];
        double logFactorial = lnFactorialCache[defaultableSize - 1];
        for (int i = defaultableSize; i <= number; i++)
            logFactorial += Math.log(i);
        return logFactorial;
    }

    /*   protected void computeCPT(BBNGraph g)
       {
          for (int i = 0; i < mNoOfNodes; i++)
          {
               BBNNode child = bbnNodes[i];
               List parents = (child.getParents();
               int parentSize = 0;
               if (parents != null)
                   parentSize = parents.size();
               int[] nodeList = new int[parentSize+1];
               if (parents == null)
               {

               }
               nodeList[parentSize] = i;
           }
       }*/

    public OptionGUI getOptionsDialog() {
        return new K2OptionGUI(this);
    }

    public Data getData() {
        return mDatabase;
    }

    public static void main(String[] args) {
        ParameterTable params = Parameter.process(args);
        String inputFile = params.getString("-i");
        String outputFile = params.getString("-o");
        String driver = params.getString("-d");
        String login = params.getString("-l");
        String passwd = params.getString("-p");
        String url = params.getString("-u");
        String orderString = params.getString("-s");
        String tableListString = params.getString("-t");

        if (driver == null || url == null || login == null || passwd == null || (inputFile != null && outputFile != null) || (tableListString == null)) {
            System.out.println("This program's is to export local file to database server OR");
            System.out.println("to import remote database contents to local file");
            System.out.println();
            System.out.println("Usage: edu.ksu.cis.kdd.data.DatabaseLoader (-i:inputfile | -o:outputfile [-t:tablenamelist,...]) -d:drivername -l:login -p:passwd -u:serverurl");
            System.out.println();
            System.out.println("Example: To load a local file to server:");
            System.out.println("java edu.ksu.cis.kdd.data.DatabaseLoader -i:myfile.arff -d:org.postgresql.Driver -l:mylogon -p:mypasswd -u:jdbc:postgresql://localhost/mydb");
            System.out.println();
            System.out.println("Example: To import remote database contents (only table1, table2, and table3) to a local file:");
            System.out.println("java edu.ksu.cis.kdd.data.DatabaseLoader -o:myoutputfile.arff -t:table1,table2,table3 -d:org.postgresql.Driver -l:mylogon -p:mypasswd -u:jdbc:postgresql://localhost/mydb");
            System.out.println("Note: To import ALL tables' contents, omit option -t. However, take note that some databases (like Oracle) may contain some system tables that are not loadable by everyone.");
            System.out.println();
            System.out.println("NOTE: In some databases, table names are CASE SENSITIVE. " + "Some even behave strangely, like Oracle, who treats table names as case insensitive, but when " + "it comes to handling metadata, table names are case sensitive.");
            return;
        }

        LinkedList tableList = null;

        if (tableListString != null) {
            tableList = new LinkedList();
            StringTokenizer tok = new StringTokenizer(tableListString, ","); // $NON-NLS-1$
            while (tok.hasMoreTokens()) {
                tableList.add(tok.nextToken());
            }
        }
        LinkedList attributeOrderList = null;
        if (orderString != null) {
            attributeOrderList = new LinkedList();
            StringTokenizer tok = new StringTokenizer(orderString, ",");
            while (tok.hasMoreTokens())
                tableList.add(tok.nextToken());
        }

        try {
            Class.forName(driver);
            Connection c = DriverManager.getConnection(url, login, passwd);
            if (inputFile != null) { // Exporting local file to server
                Database db = Database.load(inputFile);
                db.exportToServer(c);
            }   // Importing database contents to local file
            Database db = Database.importRemoteSchema(c, tableList);
            long origTime = System.currentTimeMillis();
            new PRMk2(db, attributeOrderList);
            System.out.println("Learning time = " + ((System.currentTimeMillis() - origTime) / 1000.0));
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
