package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
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
 * @version $Id: ProjectNodeAddedEvent.java,v 1.1.1.1 2005-07-28 22:36:26 watkin Exp $
 */

public class ProjectNodeAddedEvent extends Event {
    private final DSDataSet<? extends DSBioObject> dataSet;
    private final DSAncillaryDataSet<? extends DSBioObject> ancDataSet;
    private final String message;

    public ProjectNodeAddedEvent(final String message, final DSDataSet<? extends DSBioObject> dataSet, final DSAncillaryDataSet<? extends DSBioObject> ancDataSet) {
        super(null);
        this.dataSet = dataSet;
        this.ancDataSet = ancDataSet;
        this.message = message;
    }

    public DSDataSet<? extends DSBioObject> getDataSet() {
        return dataSet;
    }

    public DSAncillaryDataSet<? extends DSBioObject> getAncillaryDataSet() {
        return ancDataSet;
    }

    public String getMessage() {
        return message;
    }
}
