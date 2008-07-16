package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id: ProjectNodeAddedEvent.java,v 1.1.1.1 2005/07/28 22:36:26 watkin Exp $
 */

public class ProjectNodeRenamedEvent extends Event {
    protected DSDataSet dataSet = null;
    protected String message = null;
    protected String oldName = null;
    protected String newName = null;

    public ProjectNodeRenamedEvent(String message, DSDataSet dataSet, String oldName, String newName) {
        super(null);
        this.dataSet = dataSet;
        this.message = message;
        this.oldName = oldName;
        this.newName = newName;
    }

    public DSDataSet getDataSet() {
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
