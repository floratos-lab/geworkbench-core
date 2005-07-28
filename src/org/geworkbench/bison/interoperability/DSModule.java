package org.geworkbench.bison.interoperability;


import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
public class DSModule {
    @Retention(RetentionPolicy.RUNTIME) public @interface DSService {
        Class value();
    }

    interface foo {
        void test();
    }

    class moduleA implements foo {
        public void test() {
            System.out.println("tested");
        }
    }

    @DSService(DSMicroarraySet.class) DSMicroarraySet moduleA = null;

    DSModule() {

    }

}
