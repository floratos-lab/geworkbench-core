package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id: CommentsEvent.java,v 1.1.1.1 2005-07-28 22:36:26 watkin Exp $
 */

public class CommentsEvent extends Event {
    String _text = null;

    public CommentsEvent(EventSource source, String text) {
        super(source);
        _text = text;
    }

    public String getText() {
        return (_text);
    }
}
