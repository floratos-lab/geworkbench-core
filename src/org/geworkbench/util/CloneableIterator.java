package org.geworkbench.util;

import java.util.Iterator;

public abstract class CloneableIterator implements Cloneable, Iterator {
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
