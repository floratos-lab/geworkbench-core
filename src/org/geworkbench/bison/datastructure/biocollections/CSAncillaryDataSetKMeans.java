package org.geworkbench.bison.datastructure.biocollections;

/**
 * @author John Watkinson
 * @author zm2165
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public abstract class CSAncillaryDataSetKMeans extends CSDataSet implements DSAncillaryDataSet {

	private static final long serialVersionUID = 4162559181267361303L;
	private DSDataSet parent;

    protected CSAncillaryDataSetKMeans(DSDataSet parent, String label) {
        this.parent = parent;
        setLabel(label);
    }

    public DSDataSet getParentDataSet() {
        return parent;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * This function is designed for injection, used on grid service, when ResultSet doesn't have parent information, ex:microarray data, panels data, etc. we need to assign a parent, then when we want to get it's parent's data, we can.
     * @param parent
     */
    public void setParent(DSDataSet parent){ 
    	this.parent=parent;
    }
}
