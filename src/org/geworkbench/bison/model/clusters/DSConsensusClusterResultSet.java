package org.geworkbench.bison.model.clusters;

import java.io.File;
import java.util.ArrayList;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.CSConsensusClusterResultSet.CCData;

public interface DSConsensusClusterResultSet extends DSAncillaryDataSet<DSMicroarray> {
	public void addFile(String fname);
	public ArrayList<CCData> getDataList();
	public ArrayList<File> getCluFiles();
	public ArrayList<File> getSortedGctFiles();
}
