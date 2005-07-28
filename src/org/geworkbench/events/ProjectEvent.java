package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ProjectEvent extends Event {

    public static final String CLEARED = "Project Cleared";
    public static final String SELECTED = "Node Selected";

    private String value = null;
    private DSDataSet dataSet = null;

    public ProjectEvent(String message, DSDataSet dataSet) {
        super(null);
        this.value = message;
        this.dataSet = dataSet;
    }

    public String getMessage() {
        return value;
    }

    public DSDataSet getDataSet() {
        return dataSet;
    }

}
