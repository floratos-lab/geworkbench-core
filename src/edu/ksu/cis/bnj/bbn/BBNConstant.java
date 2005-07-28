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

import java.util.Hashtable;

/**
 * @author Roby Joehanes
 */
public class BBNConstant extends BBNPDF implements Cloneable {

    public BBNConstant() {
    }

    public BBNConstant(double d) {
        super(d);
    }

    /**
     * @see edu.ksu.cis.bnj.bbn.BBNCPF#evaluate(edu.ksu.cis.bnj.bbn.BBNQuery)
     */
    public double evaluate(Hashtable q) {
        return getValue();
    }

    /**
     * Equality test
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        return o instanceof BBNConstant && super.equals(o);
    }

    /**
     * Standard toString
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.valueOf(getValue());
    }

    /**
     * Same as toString().hashCode()
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Clone
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new BBNConstant(getValue());
    }

}
