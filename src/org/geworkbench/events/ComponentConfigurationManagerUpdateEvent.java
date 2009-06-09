package org.geworkbench.events;

import java.util.HashMap;
import java.util.List;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.management.ComponentRegistry;


/**
 * An event for notification of a CCM update in order to update Project Panel Tree.
 * <p>Title: CCM Update Event</p>
 *
 * @author tg2321
 * @version $Id: ComponentConfigurationManagerUpdateEvent.java,v 1.2 2009-06-09 20:27:29 keshav Exp $
 * 
 */

public class ComponentConfigurationManagerUpdateEvent extends Event {
	ComponentRegistry componentRegistry = ComponentRegistry.getRegistry();
	HashMap<Class, List<Class>> acceptors = null;

    public ComponentConfigurationManagerUpdateEvent(HashMap<Class, List<Class>> acceptors) {
        super(null);
    	
    	ComponentRegistry componentRegistry = ComponentRegistry.getRegistry();
    	this.acceptors = componentRegistry.getAcceptorsHashMap();
    }

    public HashMap<Class, List<Class>>  getAcceptors(){
    	return this.acceptors;
    }
    
}
