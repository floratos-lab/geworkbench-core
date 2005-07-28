package org.geworkbench.bison.util;

import java.util.HashMap;

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
public class HashHashMap <K,V> extends HashMap<K, HashMap<Integer, V>> {
    public HashHashMap() {
        super();
    }

    public void addItem(K key, V item) {
        HashMap<Integer, V> values = get(key);
        if (values == null) {
            values = new HashMap<Integer, V>();
            put(key, values);
        }
        //        if (!values.contains(item)) {
        values.put(new Integer(item.hashCode()), item);
        //        }
    }

    public int getNumValues(K key) {
        HashMap<Integer, V> values = get(key);
        if (values != null) {
            return values.size();
        }
        return 0;
    }

    //    public V getValue(K key, int index){
    //        HashMap<V> values = get(key);
    //        if ( (values != null) && (values.size() > index)) {
    //            return values.get(index);
    //        }
    //        return null;
    //    }

}
