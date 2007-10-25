package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqRegistration;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Event thrown when a row is selected on the Sequence Discovery Panel</p>
 * <p>This event is thrown when a row is selected on the patternTable by
 * the SequenceDiscoveryViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author Saroja Hanasoge
 * @version $Id: SequenceDiscoveryTableEvent.java,v 1.3 2007-10-25 19:43:21 mkustagi Exp $
 */

public class SequenceDiscoveryTableEvent extends Event {

    public SequenceDiscoveryTableEvent(DSCollection<DSMatchedPattern<DSSequence, CSSeqRegistration>> patternMatches) {
        super(null);
        this.patternMatches = patternMatches;
    }

    private DSCollection<DSMatchedPattern<DSSequence, CSSeqRegistration>> patternMatches = null;

    public DSCollection<DSMatchedPattern<DSSequence, CSSeqRegistration>> getPatternMatchCollection() {
        return patternMatches;
    }


    /*
    private CSSequenceSet sequenceDB = null;
    private Pattern[]  patterns   = null;
    private Parameters parms = null;

    public SequenceDiscoveryTableEvent(EventSource source, Pattern[]  _patterns) {
        super(source);
        setPatterns(_patterns);
    }

    public Pattern[] getPatterns() {
        return patterns;
    }

    public void setPatterns(Pattern[] _patterns) {
        patterns = _patterns;
    }

    public CSSequenceSet getSequenceDB() {
        return sequenceDB;
    }

    public void setSequenceDB(CSSequenceSet seqDB) {
        sequenceDB = seqDB;
    }

    public void setParms(Parameters p) {
        parms = p;
    }

    public Parameters getParms() {
        return parms;
    }
    */
}
