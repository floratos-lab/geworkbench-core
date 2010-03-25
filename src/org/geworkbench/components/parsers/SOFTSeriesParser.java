package org.geworkbench.components.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;

/**  
 * @author Nikhil
 */
public class SOFTSeriesParser {

	static Log log = LogFactory.getLog(SOFTFileFormat.class);
	private static final String commentSign1 = "#";
	private static final String commentSign2 = "!";
	private static final String commentSign3 = "^";
	private static final String columnSeperator = "\t";
	private static final String lineSeperator = "\n";
	private static final String duplicateLabelModificator = "_2";

	CSExprMicroarraySet maSet = new CSExprMicroarraySet();
	
	private int possibleMarkers = 0; 
 	 
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.FileFormat#getMArraySet(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	public DSMicroarraySet getMArraySet(File file)
			throws InputFileFormatException, InterruptedIOException {
		
		final int extSeperater = '.';
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(extSeperater);
		if (dotIndex != -1) {
			fileName = fileName.substring(0, dotIndex);
		}
		maSet.setLabel(fileName);
		List<String> arrayNames = new ArrayList<String>();
		List<String> markers = new ArrayList<String>();
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			if(in != null){
				try {
					String tempName = null;
					int counter = 0;
					int m = 0;
					String header = in.readLine();
					while (header != null) {
						/*
					 	* Adding comments to Experiment Information tab.
					 	*We will ignore the line which start with '!platform_table_end', '!platform_table_begin', '!sample_table_begin'
					 	*and '!sample_table_end'
					 	*/
						if (header.startsWith(commentSign1) || header.startsWith(commentSign2)) {
							if(!header.equalsIgnoreCase("!platform_table_end") && !header.equalsIgnoreCase("!platform_table_begin") 
									&& !header.equalsIgnoreCase("!sample_table_begin") && !header.equalsIgnoreCase("!sample_table_end")) {
								maSet.addDescription(header.substring(1));
							}
						}	
						String[] temp = null;
						if(header.startsWith(commentSign3)){
							temp = header.split("=");
							String temP = temp[0].trim();
							if(temP.equals("^SAMPLE")){
								tempName = temp[1].trim();
							}
						}
						if(header.startsWith(commentSign2)){
							temp = header.split("=");
							String temP1 = temp[0].trim();
							if(temP1.equals("!Sample_title")){
								String temp1 = temp[1].trim()
												+ " ("
												+tempName
												+")";
								arrayNames.add(temp1);
							}
						}
						if(!header.startsWith(commentSign1) && !header.startsWith(commentSign2) && !header.startsWith(commentSign3)){
							if(header.subSequence(0, 6).equals("ID_REF")){
								counter++;
							}
							if(!header.subSequence(0, 6).equals("ID_REF") && counter == 1 ){
								String[] mark = header.split("\t");
								markers.add(mark[0]);
								String markerName = new String(mark[0].trim());
								CSExpressionMarker marker = new CSExpressionMarker(m);
								marker.setLabel(markerName);
								maSet.getMarkerVector().add(m, marker);
								m++;
							}
						}
						header = in.readLine();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		possibleMarkers = markers.size();
		for (int i = 0; i < arrayNames.size(); i++) {
			String arrayName = arrayNames.get(i);
			CSMicroarray array = new CSMicroarray(i, possibleMarkers,
					arrayName, null, null, false,
					DSMicroarraySet.affyTxtType);
			maSet.add(array);	
		}
		BufferedReader out = null;
		int count = 0;
		int j = 0;
		try {
			out = new BufferedReader(new FileReader(file));
			try {
				String line = out.readLine();
				while (line != null) {
					if(!line.startsWith(commentSign1) && !line.startsWith(commentSign2) && !line.startsWith(commentSign3)){
						if(line.subSequence(0, 6).equals("ID_REF")){
							count++;
						}
						int k = count - 1;
						if(!line.subSequence(0, 6).equals("ID_REF") && count != 0){
							String[] values = line.split("\t");
							String valString = null; 
							valString = values[1].trim();
							if(valString == null){
								Float v = Float.NaN;
								CSExpressionMarkerValue markerValue = new CSExpressionMarkerValue(v);
								maSet.get(k).setMarkerValue(j, markerValue);
								if (v.isNaN()) {
									markerValue.setMissing(true);
								} else {
									markerValue.setPresent();
								}
							}else { 
								float value = Float.NaN;
								try {
									value = Float.parseFloat(valString);
								} catch (NumberFormatException nfe) {
								}
								// put values directly into CSMicroarray inside of
								// maSet
								Float v = value;
								CSExpressionMarkerValue markerValue = new CSExpressionMarkerValue(
										v);
								maSet.get(k).setMarkerValue(j, markerValue);
								if (v.isNaN()) {
									markerValue.setMissing(true);
								} else {
									markerValue.setPresent();
								}
							}
							j++;
							if(j == possibleMarkers) {
								j = 0;
							}
						}
					}
					line = out.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return maSet;
	}
}

