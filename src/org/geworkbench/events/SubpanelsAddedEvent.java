package org.geworkbench.events;

import java.util.ArrayList;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.engine.config.events.Event;

public class SubpanelsAddedEvent <T extends DSNamed> extends Event{

	private ArrayList<DSPanel<T>> subpanels = null;
	private Class<T> type;
	private String label = null;
	
	public SubpanelsAddedEvent(Class<T> type, ArrayList<DSPanel<T>> subpanels, String label) {
        super(null);
        this.type = type;
        this.subpanels = subpanels;
        this.label = label;
    }

    public ArrayList<DSPanel<T>> getPanels() {
        return subpanels;
    }
    
    public DSPanel<T> getPanel(int i){
    	return subpanels.get(i);
    }

    public Class<T> getType() {
        return type;
    }
    
    public String getLabel(){
    	return label;
    }
    
    public void setLabel(String name){
    	label = name;
    }

}
