package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */

public class GeneSelectorEvent extends Event {

    public static final int MARKER_SELECTION = 1;
    public static final int PANEL_SELECTION = 2;

    private final DSPanel<DSGeneMarker> panel;
    private final DSGeneMarker genericMarker;

    public GeneSelectorEvent(final DSPanel<DSGeneMarker> p) {

        super(null);
        panel = p;
        genericMarker = null;

    }

    public GeneSelectorEvent(final DSGeneMarker mi) {
        super(null);
        genericMarker = mi;
        panel = null;

    }

    public DSPanel<DSGeneMarker> getPanel() {
        return panel;
    }

    public DSGeneMarker getGenericMarker() {
        return genericMarker;
    }
}
