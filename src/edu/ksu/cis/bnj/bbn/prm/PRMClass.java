package edu.ksu.cis.bnj.bbn.prm;

/**********THIS CLASS IS NOT USED ANY MORE*************** 
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

/**
 *  Note:: There is no differece between this PRMClass and  PRMSchema
 *  I should change this later. *** TO DO *****
 *
 *
 * **/

import java.util.LinkedList;

/*
 * Copyright:    Copyright (c) 2002
 * Company:      KSU / KDD
 * Prashanth R BoddhiReddy
 */

/**
 * <dl>
 * <dt>Purpose: Abstract Relational Database table in a PRM class
 * <dd>
 * <p/>
 * <dt>Description:PRM stands for Probabilistic Relational Model.PRM aims at modeling a
 * relational database into a bayesian network.PRMClass is an object-oriented class modeling
 * of a typical relational database table.This class will represent a database table and
 * abstracts properties of a database table into member variables, like primary key, foreign
 * key, list of other attributes of the table.It only gives the structure/ schema of a
 * relational database table and not the actual tuples, i.e., it does not give the values of
 * rows of the table.
 * <dd>
 * <p/>
 * </dl>
 *
 * @author Prashanth R BoddhiReddy
 * @version 0.1.0
 * @see also PRMBBN
 * @see also PRMdata
 */


public class PRMClass {
    public PRMSchema mPRMSchema;
    public String mName;

    public PRMClass() {

    }

    public PRMClass(PRMSchema prmSchema) {
        mPRMSchema = prmSchema;
    }

    public void display() {
        mPRMSchema.display();
    }

    public void setClassName(String name) {
        mName = name;
    }


    /**
     * * There is no need of these methods
     * Most of these methods are already implemented in prmSchema
     * will try to either rename prmSchema to prmClass till then
     */
    public void setPrimaryKey(String pKeyName) {

    }

    public void addRefferenceAttribute(String rKeyName) {

    }

    public void addAttribute(String attribute) {

    }

    public String getPrimaryKey() {
        String s = "";
        return s;
    }

    public LinkedList getAttributeList(int x) {
        LinkedList list = new LinkedList();
        return list;
    }

}

