package org.geworkbench.bison.datastructure.biocollections.views;

import java.io.Serializable;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * View of microarray dataset for a given marker subset and a given microarray
 * subset.
 * 
 * <p>
 * Copyright: Copyright (c) 2003 -2004
 * </p>
 * <p/>
 * <p>
 * Company: Columbia University
 * </p>
 * 
 * @author Adam Margolin
 * @version $Id$
 */
public class CSMicroarraySetView<T extends DSGeneMarker, Q extends DSMicroarray>
		implements DSMicroarraySetView<T, Q>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1452815738190971373L;

	private DSMicroarraySet dataSet;

	/**
	 * Contains the active microarrays, organized as a DSPanel.
	 */
	private DSPanel<DSMicroarray> itemPanel = new CSPanel<DSMicroarray>("");

	/**
	 * Designates if the microarray subselection imposed by the activated
	 * phenotypic categories is imposed on the this microarray set view.
	 */
	private boolean useItemPanel = false;

	/**
	 * Contains the active markers, organized as a DSPanel.
	 */
	private DSPanel<T> markerPanel = new CSPanel<T>("");

	/**
	 * Designates if the marker subselection imposed by the activated panels is
	 * imposed on the this microarray set view.
	 */
	private boolean useMarkerPanel = false;

	// This was original considered a bad idea, but too many usages are already
	// in geWrokbench.
	public CSMicroarraySetView() {
	}

	public CSMicroarraySetView(DSMicroarraySet dataSet) {
		this.dataSet = dataSet;
	}

	@SuppressWarnings("unchecked")
	public DSItemList<T> markers() {
		if(dataSet==null)
			return markerPanel;
		
		// todo Why is this size > 0 requirement here? Should probably be
		// changed to return markerPanel if boolean is set no matter what
		if (markerPanel != null && useMarkerPanel && markerPanel.size() > 0) {
			return markerPanel;
		} else {
			// please note that in fact T is only allowed to be DSGeneMarker
			// we should in fact get rid of these generics not properly
			// designed/documented
			return (DSItemList<T>) dataSet.getMarkers();
		}
	}

	@SuppressWarnings("unchecked")
	public DSItemList<T> getUniqueMarkers() {
		if (useMarkerPanel && markerPanel.size() > 0) {
			ListOrderedSet<T> orderedSet = new ListOrderedSet<T>();
			for (T t : markerPanel) {
				orderedSet.add(t);
			}

			CSItemList<T> itemList = new CSItemList<T>();
			for (T item : orderedSet) {
				itemList.add(item);
			}

			return itemList;
		} else {
			if (dataSet == null) {
				return null;
			} else {
				return (DSItemList<T>) dataSet.getMarkers();
			}
		}
	}

	/**
	 * Set/resets marker subselection based on activated panels.
	 * 
	 * @param status
	 */
	public void useMarkerPanel(boolean status) {
		useMarkerPanel = status;
	}

	/**
	 * Gets the status of marker activation
	 * 
	 * @return the status of marker activation
	 */
	public boolean useMarkerPanel() {
		return useMarkerPanel;
	}

	public void setMarkerPanel(DSPanel<T> markerPanel) {
		this.markerPanel = markerPanel;
	}

	public DSPanel<T> getMarkerPanel() {
		return markerPanel;
	}

	@SuppressWarnings("unchecked")
	public DSItemList<T> allMarkers() {
		if (dataSet instanceof DSMicroarraySet) {
			return (DSItemList<T>) dataSet.getMarkers();
		} else {
			return null;
		}
	}

	public double getValue(int markerIndex, int arrayIndex) {
		DSMicroarray ma = get(arrayIndex);
		DSGeneMarker marker = markers().get(markerIndex);
		return ma.getMarkerValue(marker).getValue();
	}

	public double getValue(T marker, int arrayIndex) {
		DSMicroarray ma = get(arrayIndex);
		// DSGeneMarker markerValue = markers().get(marker);
		return ma.getMarkerValue(marker.getSerial()).getValue();
	}

	public double getMeanValue(T marker, int arrayIndex) {
		DSMicroarray ma = get(arrayIndex);
		// This is a bit incorrect because it does not limit to only the
		// selected markers
		return getMicroarraySet().getMeanValue(marker, ma.getSerial());
	}

	public double[] getRow(int index) {
		double[] rowVals = new double[this.size()];
		for (int itemCtr = 0; itemCtr < rowVals.length; itemCtr++) {
			rowVals[itemCtr] = getValue(index, itemCtr);
		}
		return rowVals;
	}

	/**
	 * Sets the reference microarray set for this <code>MicroarraySetView</code>
	 * .
	 * 
	 * @param ma
	 *            The new reference microarray set.
	 */
	@SuppressWarnings("unchecked")
	public void setMicroarraySet(DSMicroarraySet ma) {
		if (ma != null) {
			dataSet = ma;
			{
				DSAnnotationContext<DSGeneMarker> context = CSAnnotationContextManager
						.getInstance().getCurrentContext(ma.getMarkers());
				DSPanel<DSGeneMarker> mp = context.getActiveItems();
				if (mp != null) {
					markerPanel = (DSPanel<T>) mp;
				}
			}
			{
				DSAnnotationContext<DSMicroarray> context = CSAnnotationContextManager
						.getInstance().getCurrentContext(dataSet);
				DSPanel<DSMicroarray> mp = context.getActiveItems();
				if (mp != null) {
					itemPanel = mp;
				}
			}
		}
	}

	public DSMicroarraySet getMicroarraySet() {
		return (DSMicroarraySet) getDataSet();
	}

	// the following 9 methods used to be in CSDataSetView
	public int size() {
		return items().size();
	}

	/**
	 * @return The microarray at the desiganted index position, if
	 *         <code>index</code> is non-negative and no more than the total
	 *         number of microarrays in the set. <code>null</code> otherwise.
	 */
	@SuppressWarnings("unchecked")
	public DSItemList<Q> items() {
		if ((useItemPanel && (itemPanel != null) && (itemPanel.size() > 0))
				|| dataSet == null) {
			return (DSItemList<Q>) itemPanel;
		} else {
			// to change
			return (DSItemList<Q>) dataSet;
		}
	}

	/**
	 * Set/reset microarray subselection based on activated phenotypes.
	 * 
	 * @param status
	 */
	public void useItemPanel(boolean status) {
		useItemPanel = status;
	}

	/**
	 * Gets the statuc of Phenotype Activation
	 * 
	 * @return
	 */
	public boolean useItemPanel() {
		return useItemPanel;
	}

	@SuppressWarnings("unchecked")
	public void setItemPanel(DSPanel<Q> mArrayPanel) {
		this.itemPanel = (DSPanel<DSMicroarray>) mArrayPanel;
	}

	@SuppressWarnings("unchecked")
	public DSPanel<Q> getItemPanel() {
		return (DSPanel<Q>) itemPanel;
	}

	public void setDataSet(DSDataSet<Q> qs) {
		dataSet = (DSMicroarraySet) qs;
	}

	@SuppressWarnings("unchecked")
	public DSDataSet<Q> getDataSet() {
		return (DSDataSet<Q>) dataSet;
	}

	public Q get(int index) {
		return items().get(index);
	}

}
