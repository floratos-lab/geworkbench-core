package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * @author John Watkinson
 * @version $Id$
 */
public abstract class CSAncillaryDataSet<T extends DSBioObject> extends CSDataSet<T> implements DSAncillaryDataSet<T> {

	private static final long serialVersionUID = -2828423786652899938L;
	
	private DSDataSet<T> parent;

    protected CSAncillaryDataSet(DSDataSet<T> parent, String label) {
        this.parent = parent;
        setLabel(label);
    }

    public DSDataSet<T> getParentDataSet() {
        return parent;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * This function is designed for injection, used on grid service, when ResultSet doesn't have parent information, ex:microarray data, panels data, etc. we need to assign a parent, then when we want to get it's parent's data, we can.
     * @param parent
     */
    public void setParent(DSDataSet<T> parent){ 
    	this.parent=parent;
    }
}
