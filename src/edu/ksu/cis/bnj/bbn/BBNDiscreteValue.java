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

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Set of possible discrete values of a node. E.g.: { yes, no } or { hot, warm,
 * cool, cold }. Essentially a HashSet. We already have contains method, so no
 * need to reimplement anything.
 *
 * @author Roby Joehanes
 */
public class BBNDiscreteValue extends LinkedHashSet implements BBNValue {
    /**
     * Inherited constructor for BBNDiscreteValue.
     */
    public BBNDiscreteValue() {
        super();
    }

    /**
     * Inherited constructor for BBNDiscreteValue.
     *
     * @param arg0
     * @param arg1
     */
    public BBNDiscreteValue(int arg0, float arg1) {
        super(arg0, arg1);
    }

    /**
     * Inherited constructor for BBNDiscreteValue.
     *
     * @param arg0
     */
    public BBNDiscreteValue(int arg0) {
        super(arg0);
    }

    /**
     * A shortcut, simply to allow easy cloning
     *
     * @see java.util.HashSet#HashSet(Collection)
     */
    public BBNDiscreteValue(Collection c) {
        addAll(c);
    }

    /**
     * A shortcut, simply to allow easy cloning
     *
     * @param c
     */
    public BBNDiscreteValue(Object[] c) {
        if (c == null) return;
        int max = c.length;
        for (int i = 0; i < max; i++)
            add(c[i]);
    }

    /**
     * A convenience alias to get the arity of the code. It's the same as
     * size().
     *
     * @return int
     */
    public int getArity() {
        return size();
    }

    /**
     * Simple cloning
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new BBNDiscreteValue(this);
    }

    /**
     * Simple equals
     *
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        return o instanceof BBNDiscreteValue && super.equals(o);
    }
}
