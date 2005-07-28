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

/*
 * Copyright:    Copyright (c) 2002
 * Company:      KSU / KDD
 * Author: Prashanth R BoddhiReddy
 */

/**
 * <dl>
 * <dt>Purpose: Interface holding constants
 * <dd>
 * <p/>
 * <dt>Description:PRM stands for Probabilistic Relational Model.PRM aims at modeling a
 * relational database into a bayesian network.This is an interface for all constants used
 * in all of PRM code.
 * <dd>
 * <p/>
 * </dl>
 *
 * @author Prashanth R BoddhiReddy
 * @author Rengakrishnan Subramanian
 * @version 0.1.0
 * @see also PRMBBN
 * @see also PRMClass
 */

public interface PRMData {
    public static final int PRM_ATTRIBUTE = 1, PRM_RKEY = 2, PRM_PKEY = 3;
    public static final String kPrmClassName = "PRM_CLASSNAME", kPrmAttribute = "PRM_ATTRIBUTE", kPrmPKey = "PRM_PKEY", kRefferenceAttribute = "PRM_REFFERENCEATTRIBUTE";

}
