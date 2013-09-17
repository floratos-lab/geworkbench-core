package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id$
 */

public class ProjectNodeRenamedEvent extends Event {
    protected DSDataSet<? extends DSBioObject> dataSet = null;
    protected String message = null;
    protected String oldName = null;
    protected String newName = null;

    public ProjectNodeRenamedEvent(String message, DSDataSet<? extends DSBioObject> dataSet, String oldName, String newName) {
        super(null);
        this.dataSet = dataSet;
        this.message = message;
        this.oldName = oldName;
        this.newName = newName;
    }

    public DSDataSet<? extends DSBioObject> getDataSet() {
        return dataSet;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public String getMessage() {
        return message;
    }
}
