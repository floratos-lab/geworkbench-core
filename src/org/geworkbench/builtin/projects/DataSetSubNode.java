package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id$
 */

public class DataSetSubNode extends ProjectTreeNode {
	private static final long serialVersionUID = 790047443850868022L;
	
	@SuppressWarnings("rawtypes")
	public DSAncillaryDataSet _aDataSet = null;

    @SuppressWarnings("rawtypes")
	public DataSetSubNode(DSAncillaryDataSet ads) {
        _aDataSet = ads;
        super.setUserObject(ads);
    }
}
