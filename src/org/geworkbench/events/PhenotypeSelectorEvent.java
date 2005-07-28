package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id: PhenotypeSelectorEvent.java,v 1.1.1.1 2005-07-28 22:36:26 watkin Exp $
 */

public class PhenotypeSelectorEvent <Q extends DSMicroarray> extends Event {
    private DSPanel<Q> panel;

    public PhenotypeSelectorEvent(DSPanel<Q> p) {
        super(null);
        panel = p;
    }

    public DSPanel<Q> getTaggedItemSetTree() {
        return panel;
    }
}
