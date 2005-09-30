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
 * @version $Id: CommentsEvent.java,v 1.2 2005-09-30 21:55:32 watkin Exp $
 */

public class CommentsEvent extends Event {
    String _text = null;

    public CommentsEvent(String text) {
        super(null);
        _text = text;
    }

    public String getText() {
        return (_text);
    }
}
