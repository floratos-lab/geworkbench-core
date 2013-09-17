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
 * @version $Id$
 */

public class ProjectNodeRemovedEvent extends Event {

    private DSAncillaryDataSet<? extends DSBioObject> ancDataSet = null;

    public ProjectNodeRemovedEvent(DSAncillaryDataSet<? extends DSBioObject> ancDataSet) {
        super(null);
        this.ancDataSet = ancDataSet;
    }

    // TODO this method is not doing what it was meant to do 
    // should be removed
    public DSDataSet<?> getDataSet() {
        return null;
    }

    public DSAncillaryDataSet<? extends DSBioObject> getAncillaryDataSet() {
        return ancDataSet;
    }

}
