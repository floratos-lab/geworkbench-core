package org.geworkbench.engine.management;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * User: matt
 * Date: Oct 14, 2005
 * Time: 12:45:32 PM
 */
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface AcceptTypes {
    Class<? extends DSDataSet>[] value();
}
