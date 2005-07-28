package org.geworkbench.bison.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class HashHashSet <K,V> extends HashMap<K, HashSet<V>> {
    public HashHashSet() {
        super();
    }

    public void addItem(K key, V item) {
        HashSet<V> values = get(key);
        if (values == null) {
            values = new HashSet<V>();
            put(key, values);
        }
        //        if (!values.contains(item)) {
        values.add(item);
        //        }
    }

    public int getNumValues(K key) {
        HashSet<V> values = get(key);
        if (values != null) {
            return values.size();
        }
        return 0;
    }

    //    public V getValue(K key, int index){
    //        HashSet<V> values = get(key);
    //        if ( (values != null) && (values.size() > index)) {
    //            return values.get(index);
    //        }
    //        return null;
    //    }

}
