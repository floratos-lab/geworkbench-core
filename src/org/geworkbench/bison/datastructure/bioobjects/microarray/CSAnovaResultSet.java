/**
 * 
 */
package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * @author yc2480
 * @version $Id$
 * 
 */
public class CSAnovaResultSet<T extends DSGeneMarker> extends
		CSSignificanceResultSet<T> implements DSAnovaResultSet<T> {

	private static final long serialVersionUID = -1727538221152553424L;
	
	private DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> microarraySetView;
	private double alpha = 0.05;
	public double[][] result2DArray; // columns: pval,
	private String[] groupNames;
	private String[] significantMarkerNames;
	private DSPanel<T> panel;

	// private ArrayList<String> list = new ArrayList<String>(); //store
	// significantMarkerNames in ArrayList for searching.
	private Map<String, Integer> map = new HashMap<String, Integer>();

	// constructor for local
	public CSAnovaResultSet(DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> microarraySetView,
			String label, String[] groupNames, String[] significantMarkerNames,
			double[][] result2DArray) {
		super(microarraySetView.getMicroarraySet(), label, groupNames,
				new String[0], 0.05);
		this.result2DArray = result2DArray;
		this.microarraySetView = microarraySetView;
		this.groupNames = groupNames;
		this.significantMarkerNames = significantMarkerNames;
		panel = new CSPanel<T>(label);
		// list.addAll(Arrays.asList(significantMarkerNames));
		for (int cx = 0; cx < significantMarkerNames.length; cx++) {
			map.put(significantMarkerNames[cx], new Integer(cx));
		}
	}

	public File getDataSetFile() {
		// not needed
		return null;
	}

	public void setDataSetFile(File file) {
		// no-op
	}

	// TODO: color mosaic use these one a lot, make it faster
	// TODO: we can use a HashMap to increase speed as in
	// CSSignificanceResultSet
	public Double getSignificance(DSGeneMarker marker) {
		// System.out.println("getSignificance");
		Integer I = map.get(marker.getLabel());
		if (I == null) {
			return null;
		} else {
			return result2DArray[0][I.intValue()];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.bioobjects.microarray.CSSignificanceResultSet#setSignificance(org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker,
	 *      double)
	 * 
	 * This function override it's parent's so it can also set result2DArray
	 * array.
	 */
	@Override
	public void setSignificance(T marker, double value) {
		super.setSignificance(marker, value);
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(significantMarkerNames));
		int index = list.indexOf(marker.getLabel());
		result2DArray[0][index] = value;
		// result2DArray[0][microarraySetView.getMicroarraySet().getMarkers().indexOf(marker)]=value;
		// significance.put(marker, value);
		if (value < alpha) {
			panel.add((T) marker);
		}
		// shouldn't we check value>alpha and remove marker?
	}

	public void saveToFile(String filename) {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(filename));
			int i = 0;
			for (T o : panel) {
				out.println(new StringBuilder().append(
						((CSExpressionMarker) o).getLabel() + "\t").append(
						Double.toString(result2DArray[0][microarraySetView
								.getMicroarraySet().getMarkers().indexOf(
										(CSExpressionMarker) o)])).toString());
				i++;
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.datastructure.bioobjects.microarray.CSSignificanceResultSet#getSignificantMarkers()
	 */
	@Override
	public DSPanel<T> getSignificantMarkers() {
		return panel;
	}

	public double getCriticalPValue() {
		return alpha;
	}

	public String[] getLabels(int index) {
		return groupNames;
		// return anovaResult.getSignificantMarkerNames();
	}

	public void sortMarkersBySignificance() {
		int n = panel.size();
		double[][] newResult2DArray = new double[result2DArray.length][result2DArray[0].length];
		String[] newSignificantMarkerNames = new String[significantMarkerNames.length];
		Map<String, Integer> newMap = new HashMap<String, Integer>();
		ArrayList<Integer> indices = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			indices.add(i);
		}
		Collections.sort(indices, new SignificanceComparator());
		CSPanel<T> newPanel = new CSPanel<T>();

		for (int i = 0; i < n; i++) {
			newPanel.add(panel.get(indices.get(i)));
			newSignificantMarkerNames[i] = significantMarkerNames[indices
					.get(i)];
			newMap.put(newSignificantMarkerNames[i], i);
			for (int cx = 0; cx < result2DArray.length; cx++) {
				newResult2DArray[cx][i] = result2DArray[cx][indices.get(i)];
			}
		}
		panel = newPanel;
		map = newMap;
		result2DArray = newResult2DArray;
		significantMarkerNames = newSignificantMarkerNames;
	}

	private class SignificanceComparator implements Comparator<Integer> {

		public int compare(Integer x, Integer y) {
			// double sigX = anovaResult.getPVals(x);
			// double sigY = anovaResult.getPVals(y);
			double sigX = result2DArray[0][x];
			double sigY = result2DArray[0][y];

			if (sigX > sigY) {
				return 1;
			} else if (sigX < sigY) {
				return -1;
			} else {
				return 0;
			}
		}

	}

	public double[][] getResult2DArray() {
		return result2DArray;
	}

	public double getPValue(T marker) {
		// int
		// index=microarraySetView.getMicroarraySet().getMarkers().indexOf(marker);
		// TODO: add a HashMap for Marker=>PValue to speed up following methods.
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(significantMarkerNames));
		int index = list.indexOf(marker.getShortName());
		// TODO: error check
		return result2DArray[0][index];
	};

	public double getAdjPValue(T marker) {
		// int
		// index=microarraySetView.getMicroarraySet().getMarkers().indexOf(marker);
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(significantMarkerNames));
		int index = list.indexOf(marker.getShortName());
		// TODO: error check
		return result2DArray[1][index];
	};

	public double getFStatistic(T marker) {
		// int
		// index=microarraySetView.getMicroarraySet().getMarkers().indexOf(marker);
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(significantMarkerNames));
		int index = list.indexOf(marker.getShortName());
		// TODO: error check
		return result2DArray[2][index];
	};

	public double getMean(T marker, String label) {
		// int groupIndex=Arrays.binarySearch(groupNames,label); //binarySearch
		// only works on sorted array.
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(groupNames));
		int groupIndex = list.indexOf(label);
		// int
		// markerIndex=microarraySetView.getMicroarraySet().getMarkers().indexOf(marker);
		ArrayList<String> mlist = new ArrayList<String>();
		mlist.addAll(Arrays.asList(significantMarkerNames));
		int markerIndex = mlist.indexOf(marker.getShortName());
		// TODO: error check
		return result2DArray[3 + groupIndex * 2][markerIndex];
	};

	public double getDeviation(T marker, String label) {
		// int groupIndex=Arrays.binarySearch(groupNames,label); //binarySearch
		// only works on sorted array.
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(groupNames));
		int groupIndex = list.indexOf(label);
		// int
		// markerIndex=microarraySetView.getMicroarraySet().getMarkers().indexOf(marker);
		ArrayList<String> mlist = new ArrayList<String>();
		mlist.addAll(Arrays.asList(significantMarkerNames));
		int markerIndex = mlist.indexOf(marker.getShortName());
		// TODO: error check
		return result2DArray[3 + groupIndex * 2 + 1][markerIndex];
	};

	// for injection used in grid service
	public void microarraySetViewSetter(DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view) { 
		microarraySetView = view;
	}

	public String[] significantMarkerNamesGetter() {
		return significantMarkerNames;
	}
}
