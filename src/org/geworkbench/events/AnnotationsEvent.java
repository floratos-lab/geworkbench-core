package org.geworkbench.events;

import org.geworkbench.util.annotation.Pathway;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Braodcast Event to encapsulate <code>Pathway</code> information retrieved
 * from caBIO queries
 */
public class AnnotationsEvent extends Event {
    public AnnotationsEvent(String message, Pathway pway) {
        super(null);
        pathway = pway;
        value = message;
    }

    /**
     * Gets the <code>Pathway</code> object associated with this
     * <code>Event</code>
     *
     * @return <code>Pathway</code> contained in this <code>Event</code>
     */
    public org.geworkbench.util.annotation.Pathway getPathway() {
        return pathway;
    }

    /**
     * The message that this <code>Event</code> was constructed with
     *
     * @return Message contained in this <code>Event</code>
     */
    public String getMessage() {
        return value;
    }

    private Pathway pathway = null;
    private String value = null;
}