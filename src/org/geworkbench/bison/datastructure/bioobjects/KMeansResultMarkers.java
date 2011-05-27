package org.geworkbench.bison.datastructure.bioobjects;

import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.Cluster;
import org.geworkbench.bison.model.clusters.DSSOMClusterDataSet;
import org.geworkbench.bison.model.clusters.SOMCluster;

public class KMeansResultMarkers extends KMeansResult implements DSSOMClusterDataSet {
	
	private static final long serialVersionUID = 4763004042149705168L;
	
	private int rows, columns;
    private SOMCluster[][] clusters;
    private DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> parentSet;
	
	public KMeansResultMarkers(DSMicroarraySet<DSMicroarray> maSet,
			String name, DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> dataSetView,
			SOMCluster[][] clusters, int clusterBy,
			ArrayList<List<String[]>> resultList) {
		super(maSet, name, clusters, clusterBy, resultList);

		rows = clusters.length;
        if (rows > 0) {
            columns = clusters[0].length;
        }
        this.clusters = clusters;
        this.parentSet = dataSetView;
	}

	
	 public SOMCluster[][] getClusters() {
	        return clusters;
	    }

	    public void writeToFile(String fileName) {
	        // No-op
	    }


		@SuppressWarnings("rawtypes")
		public DSDataSetView getDataSetView() {
	        return parentSet;
	    }

	    public Cluster getCluster(int index) {
	        if (columns == 0) {
	            throw new IndexOutOfBoundsException("Invalid cluster index.");
	        }
	        int row = index / columns;
	        int col = index % columns;
	        return getCluster(row, col);
	    }

	    public int getRows() {
	        return rows;
	    }

	    public int getColumns() {
	        return columns;
	    }

	    public SOMCluster getCluster(int row, int column) {
	        return clusters[row][column];
	    }

	    public int getNumberOfClusters() {
	        return rows * columns;
	    }

}
