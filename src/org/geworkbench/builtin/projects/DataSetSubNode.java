package org.geworkbench.builtin.projects;

import java.awt.Component;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSTTestResultSet;

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
	final public DSAncillaryDataSet _aDataSet;
	
    @SuppressWarnings("rawtypes")
	public DataSetSubNode(DSAncillaryDataSet ads) {
        _aDataSet = ads;
        super.setUserObject(ads);
        
        dirPropertyKey = "subnodeDir";
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public DSDataSet<? extends DSBioObject> getDataset() { return _aDataSet; }

	@SuppressWarnings("unchecked")
	@Override
	protected void writeToFile(final boolean tabDelimited,
			final Component dialogParent) {

		if (_aDataSet instanceof CSTTestResultSet) { // special case for CSTTestResultSet
			CSTTestResultSet<? extends DSGeneMarker> tTestResultSet = (CSTTestResultSet<? extends DSGeneMarker>) _aDataSet;
			tTestResultSet.saveDataToCSVFile();
		} else {
			super.writeToFile(tabDelimited, dialogParent);
		}
	}
}
