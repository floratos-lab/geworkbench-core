package org.geworkbench.bison.algorithm;

import org.geworkbench.bison.datastructure.complex.panels.DSAnnotatedPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * An interface for algorithms.
 *
 * @todo - watkin - Currently unused.
 */
public interface DSAlgorithm <T extends DSNamed,U> {
    DSAnnotatedPanel<T, U> execute();
}
