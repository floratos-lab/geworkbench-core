package org.geworkbench.bison.datastructure.biocollections;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSSequentialItemList;
import org.jfree.util.Log;

/**
 * <p>
 * Copyright: Copyright (c) 2003 -2004
 * </p>
 * <p>
 * Company: Columbia University
 * </p>
 * 
 * @author Adam Margolin
 * @version $Id$
 */
public class CSMarkerVector extends CSSequentialItemList<DSGeneMarker> {

	private static final long serialVersionUID = -7674561735987333555L;

	private Hashtable<Integer, Set<DSGeneMarker>> geneIdMap = new Hashtable<Integer, Set<DSGeneMarker>>();
	private Hashtable<String, Set<DSGeneMarker>> geneNameMap = new Hashtable<String, Set<DSGeneMarker>>();

	public DSGeneMarker getMarkerByUniqueIdentifier(String label) {
		return super.get(label);
	}

	// utility to add a new item to a map's value set
	private static <T> void addItem(Map<T, Set<DSGeneMarker>> map, T key,
			DSGeneMarker marker) {
		Set<DSGeneMarker> set = map.get(key);
		if (set == null) {
			set = new HashSet<DSGeneMarker>();
			set.add(marker);
			map.put(key, set);
		} else {
			set.add(marker);
		}
	}

	/* reset geneIdMap and genNameMap as a temporary fix */
	/* TODO to find out whether and why this is necessary */
	/* this method is only used in HouseKeepingGeneNormalizer */
	public void correctMaps() {
		geneIdMap.clear();
		geneNameMap.clear();
		for (DSGeneMarker item : this) {
			Integer geneId = new Integer(item.getGeneId());
			if (geneId != null && geneId.intValue() > 0) {
				addItem(geneIdMap, geneId, item);
			}

			String geneName = item.getGeneName();
			String label = item.getLabel();
			if (geneName != null && (!"---".equals(geneName))) {
				if (label != null && geneName.equals("")) {
					addItem(geneNameMap, label, item);
				} else {
					addItem(geneNameMap, geneName, item);
				}
			}

		}
	}

	/* return all matching markers */
	/* only used in this class and HouseKeepingGeneNormalizer */
	public Vector<DSGeneMarker> getMatchingMarkers(String aString) {
		Vector<DSGeneMarker> matchingMarkers = new Vector<DSGeneMarker>();
		DSGeneMarker uniqueKeyMarker = super.get(aString);
		if (uniqueKeyMarker != null) {
			matchingMarkers.add(uniqueKeyMarker);
		}

		Set<DSGeneMarker> markersSet = geneNameMap.get(aString);
		if (markersSet != null && markersSet.size() > 0) {
			for (DSGeneMarker marker : markersSet) {
				if (!matchingMarkers.contains(marker)) {
					matchingMarkers.add(marker);
				}
			}
		}

		try {
			Integer geneId = Integer.parseInt(aString);
			markersSet = geneIdMap.get(geneId);
			if (markersSet != null && markersSet.size() > 0) {
				for (DSGeneMarker marker : markersSet) {
					if (!matchingMarkers.contains(marker)) {
						matchingMarkers.add(marker);
					}
				}
			}
		} catch (NumberFormatException e) {
			// this is not a good idea. leave this way for now.
			Log.error(e);
		}

		return matchingMarkers;
	}

	/* return an first matching item based on a given string */
	public DSGeneMarker get(String aString) {
		if (aString == null) {
			return null;
		}

		DSGeneMarker marker = super.get(aString);
		if (marker == null) {
			Vector<DSGeneMarker> matchingMarkers = getMatchingMarkers(aString);
			if (matchingMarkers != null && matchingMarkers.size() > 0) {
				marker = matchingMarkers.get(0);
			}
		}
		return marker;
	}

	/* add an item to this object if it is not already in */
	public boolean add(DSGeneMarker item) {

		if (item == null)
			return false;
		if (this.contains(item))
			return false;

		boolean result = super.add(item);
		if (result) {
			Integer geneId = new Integer(item.getGeneId());
			if (geneId != null && geneId.intValue() > 0) {
				addItem(geneIdMap, geneId, item);
			}

			String geneName = item.getGeneName();
			if (geneName != null && (!"---".equals(geneName))) {
				addItem(geneNameMap, geneName, item);
			}

		}

		return result;
	}

	/* add an item at the given index */
	public void add(int i, DSGeneMarker item) {
		super.add(i, item);
		Integer geneId = new Integer(item.getGeneId());
		if (geneId != null && geneId.intValue() > 0) {
			addItem(geneIdMap, geneId, item);
		}

		String geneName = item.getGeneName();
		if (geneName != null && (!"---".equals(geneName))) {
			addItem(geneNameMap, geneName, item);
		}

	}

	/* return the fist matching item */
	public DSGeneMarker get(DSGeneMarker item) {
		DSGeneMarker marker = super.get(item);
		if (marker == null) {
			Vector<DSGeneMarker> matchingMarkers = getMatchingMarkers(item);
			if (matchingMarkers != null && matchingMarkers.size() > 0) {
				marker = matchingMarkers.get(0);
			}
		}

		return marker;
	}

	// TODO the callers of this method, CSMicroarraySet and
	// HouseKeepingGeneNormalizer, should use set instead of vector.
	public Vector<DSGeneMarker> getMatchingMarkers(DSGeneMarker item) {
		Set<DSGeneMarker> set = getMatchingMarkersSet(item);
		if (set == null)
			return null;

		return new Vector<DSGeneMarker>(set);
	}

	// TODO to replace the Vector version later
	private Set<DSGeneMarker> getMatchingMarkersSet(DSGeneMarker item) {
		if ((item.getGeneName() != null) && (item.getGeneName().length() > 0)) {
			return geneNameMap.get(item.getGeneName());
		} else {
			return geneIdMap.get(new Integer(item.getGeneId()));
		}
	}

	/* check if this object contains the given item */
	public boolean contains(Object item) {
		if (item instanceof DSGeneMarker) {
			// contains method in the super class CSItemList is not public, so
			// we need to use get instead contains to do this
			DSGeneMarker marker = super.get((DSGeneMarker) item);
			if (marker != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/* remove an item from this object */
	public boolean remove(Object item) {
		boolean removed = super.remove(item);
		if (removed && item instanceof DSGeneMarker) {
			geneIdMap.remove(new Integer(((DSGeneMarker) item).getGeneId()));
		}
		return removed;
	}

	/* remove the item at the given index from this class */
	public DSGeneMarker remove(int index) {
		DSGeneMarker marker = super.remove(index);
		geneIdMap.remove(new Integer(marker.getGeneId()));
		return marker;
	}

	/* reset this class to be clear */
	public void clear() {
		super.clear();
		geneIdMap.clear();
	}

	/* used in AffyFileFormat and GenPixFileFormat */
	public void setLabel(int index, String label) {
		DSGeneMarker item = get(index);
		if (item == null)
			return;

		String oldLabel = item.getLabel();
		if (oldLabel != null) {
			objectMap.remove(oldLabel);
		}
		item.setLabel(label);
		objectMap.put(label, item);
	}
}
