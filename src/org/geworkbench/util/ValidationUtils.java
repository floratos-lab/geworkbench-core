package org.geworkbench.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

public class ValidationUtils {
	private static final String DEFAULT_DELIMITER = ",";
	
	private static String delimiter = DEFAULT_DELIMITER;
	private static String errorMsg = "";
	
	@SuppressWarnings("unchecked")
	public static boolean validateMicroarrayMarkers(Object input, String loadedMarkers){
		// Init
		errorMsg = "";	
		
		// Checking to see if there is input data
		if ((input == null) || (loadedMarkers == null)
				|| StringUtils.isEmpty(loadedMarkers)) {
			errorMsg = "Please make sure neither the input data nor the loaded markers are empty.";
			return false;
		}
		
		// Check for proper data type
		if (input instanceof DSMicroarraySetView) {
			DSItemList<DSGeneMarker> geWorkbenchMarkers = ((DSMicroarraySetView) input)
			.getMicroarraySet().getMarkers();
			return validateMarkers(geWorkbenchMarkers, loadedMarkers);
		} else {
			errorMsg = "Input data is not a microarray set.";
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean validateMicroarrayMarkers(DSDataSet dataSet, String loadedMarkers){
		// Init
		errorMsg = "";	
		
		// Checking to see if there is input data
		if ((dataSet == null) || (loadedMarkers == null)
				|| StringUtils.isEmpty(loadedMarkers)) {
			errorMsg = "Please make sure neither the input data nor the loaded markers are empty.";
			return false;
		}
		
		// Check for proper data type
		if(dataSet instanceof DSMicroarraySet){
			return validateMarkers(((DSMicroarraySet) dataSet).getMarkers(), loadedMarkers);
		} else {
			errorMsg = "Data is not a microarray data set.";
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean validateMarkers(DSItemList<DSGeneMarker> geWorkbenchMarkers,
			String loadedMarkers) {
		
		// Pre-processing geWorkbench data
		int numWorkbenchMarkers = geWorkbenchMarkers.size();
		String[] workbenchMarkers = new String[numWorkbenchMarkers];
		for(int i = 0; i < numWorkbenchMarkers; i++){
			DSGeneMarker m = geWorkbenchMarkers.get(i);
			if((m != null) && (StringUtils.isNotEmpty(m.getLabel()))){
				workbenchMarkers[i] = m.getLabel();
			} else {
				workbenchMarkers[i] = "";
			}
		}
		Arrays.sort(workbenchMarkers);
				
		// Pre-processing loaded markers
		String[] keys = StringUtils.split(loadedMarkers, delimiter);
		
		// Search
		StringBuilder sb = new StringBuilder();
		int size = keys.length;
		int lastString = size - 1;
		for(int i = 0; i < size; i++){
			if((keys[i] != null) && StringUtils.isNotEmpty(keys[i])){
				int index = Arrays.binarySearch(workbenchMarkers, keys[i].trim());
				if(index < 0){
					// key not in list
					sb.append(keys[i]);
					if(i < lastString){
						sb.append(delimiter);
						sb.append(" ");
					}
				} 
			} 
		}
		String s = sb.toString();
		if(StringUtils.isEmpty(s)){
			return true;
		} else {
			errorMsg = "The following are not valid markers:\n" + s;
			return false;
		}
	}
	
	public static String getDelimiter(){
		return delimiter;
	}
	
	public static void setDelimiter(String d){
		delimiter = d;
	}
	
	public static String getErrorMessage(){
		return errorMsg;
	}
}
