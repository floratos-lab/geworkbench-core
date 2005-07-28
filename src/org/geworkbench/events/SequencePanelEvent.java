package org.geworkbench.events;

import org.geworkbench.util.sequences.SequenceDB;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Event thrown when a sequence file is loaded; it is thrown by
 * SequenceViewAppComponent</p>
 * <p>SequenceViewAppComponent throws this event right it loads a FASTA
 * file; listeners include the SequenceDiscoveryViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author Saroja Hanasoge
 * @version $Id: SequencePanelEvent.java,v 1.1.1.1 2005-07-28 22:36:26 watkin Exp $
 */

public class SequencePanelEvent extends Event {
    private String _value = null;
    private DSPanel<DSSequence> _panels = null;
    private SequenceDB sequenceDB = null;

    public SequencePanelEvent(EventSource source, DSPanel<DSSequence> p, SequenceDB db) {
        super(source);
        _panels = p;
        sequenceDB = db;
    }

    public DSPanel<DSSequence> getPanels() {
        return _panels;
    }

    public void setSequenceDB(SequenceDB sDB) {
        sequenceDB = sDB;
    }

    public SequenceDB getSequenceDB() {
        return sequenceDB;
    }
}
