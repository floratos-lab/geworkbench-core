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
        return parent; 
    }
    
    public void setParent(DSDataSet parent){ 
    	this.parent=parent;
    }
}
