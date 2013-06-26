package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

 

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class TitrationCurveEvent extends Event {

     
    long titrationId = 0;    

    public TitrationCurveEvent(long titrationId) {
        super(null);
        this.titrationId = titrationId;
       
    }

    public long getTitrationId() {
        return titrationId;
    }

    

}