package org.geworkbench.bison.util;

import java.util.HashMap;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Annotator <L extends DSAnnotLabel, V> extends HashMap<L, V> implements DSAnnotator<L, V> {
    public Annotator() {
    }

    public V getAnnotation(L annotation) {
        return get(annotation);
    }

    public void setAnnotation(L annotation, V value) {
        put(annotation, value);
    }

}
