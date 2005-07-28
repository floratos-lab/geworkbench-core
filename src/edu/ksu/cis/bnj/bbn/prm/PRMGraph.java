package edu.ksu.cis.bnj.bbn.prm;

/***********THIS CLASS IS NOT USED ANY MORE***************
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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * PRM Graph
 *
 * @author
 */
public class PRMGraph extends BBNGraph {
    private String mPRMNetworkName;
    private LinkedList mPRMClassList;

    public PRMGraph() {

    }

    public PRMGraph(LinkedList list, String networkName) {
        mPRMNetworkName = networkName;
        mPRMClassList = list;
    }


    public void setPRMClasses(LinkedList list) {
        mPRMClassList = list;
    }

    public void setName(String name) {
        mPRMNetworkName = name;
    }

    public void addClass(PRMClass prmClass) {
        mPRMClassList.add(prmClass);
    }

    public void addClass(PRMSchema prmSchema) {
        mPRMClassList.add(prmSchema);
    }

    public void addClassList(LinkedList prmClasses) {
        mPRMClassList = prmClasses;
    }

    public void display() {
        System.out.println("Network name is " + mPRMNetworkName);
        for (Iterator i = mPRMClassList.iterator(); i.hasNext();) {
            PRMSchema prmClass = (PRMSchema) i.next();
            prmClass.display();
        }
        System.out.println(this.getNodeList());
    }

    public LinkedList getClassList() {
        return mPRMClassList;
    }

    public int getNumberOfClasses() {
        return mPRMClassList.size();
    }

    public int getNoOfNodes() {
        int size = 0; /******still TO DO **********/
        return size;
    }


}
