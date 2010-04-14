
package org.geworkbench.components.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;

/**  
 * @author Nikhil
 */
public class MageTabFileFormat extends DataSetFileFormat {

	static Log log = LogFactory.getLog(MageTabFileFormat.class);
	private static final String[] maExtensions = { "txt", "TXT"};
	
	static final char ABSENT    = 'A';
    static final char PRESENT   = 'P';
    static final char MARGINAL  = 'M';
    static final char UNDEFINED = '\0';  
    char detectionStatus = UNDEFINED;

	ExpressionResource resource = new ExpressionResource();
	int possibleMarkers = 0;	
	MageTabFileFilter maFilter = null;
	 public MageTabFileFormat() {
		formatName = "MAGE-TAB Files";
		maFilter = new MageTabFileFilter();
		Arrays.sort(maExtensions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.FileFormat#getResource(java.io.File)
	 */
	public Resource getResource(File file) {
		try {
			resource.setReader(new BufferedReader(new FileReader(file)));
			resource.setInputFileName(file.getName());
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.FileFormat#getFileExtensions()
	 */
	public String[] getFileExtensions() {
		return maExtensions;
	}
	
	/*
	 * (non-Javadoc) 
	 * @see org.geworkbench.components.parsers.FileFormat#checkFormat(java.io.File)
	 */
	public boolean checkFormat(File file) throws InterruptedIOException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.microarray.DataSetFileFormat#getDataFile(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	public DSDataSet getDataFile(File file) throws InputFileFormatException, InterruptedIOException{  
		DSMicroarraySet maSet1 = new CSExprMicroarraySet();
		maSet1 = getMArraySet(file);
		return maSet1;
	}
	 
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.FileFormat#getMArraySet(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	public DSMicroarraySet getMArraySet(File file)
			throws InputFileFormatException, InterruptedIOException {
		
		CSExprMicroarraySet maSet = new CSExprMicroarraySet();
		BufferedReader in = null;
		final int extSeperater = '.';
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(extSeperater);
		if (dotIndex != -1) {
			fileName = fileName.substring(0, dotIndex);
		}
		maSet.setLabel(fileName);
		List<String> markers = new ArrayList<String>();
		List<String> arrayNames = new ArrayList<String>();
		int n = 0;
		int valueNumber = 0;
		int detection = 0;
		int call = 0;
		boolean pValueFound = false;
		boolean absCallFound = false;
		try {
			int m = 0;
			in = new BufferedReader(new FileReader(file));
			if(in != null){
				try {
					String header = in.readLine();
					while (header != null) {	
						String[] tokens = header.split("\t");
						if(tokens[0].contentEquals("Scan REF")){
							arrayNames.add(tokens[1]);
							int r = 0;
							for(int q=2; q<tokens.length; q++){
								if(tokens[q-1].contentEquals(tokens[q])){
									r = r+1;
								}else {
									if(r==0){
										arrayNames.add(tokens[q]);
									}else {
										n = r;
										arrayNames.add(tokens[q]);
										r = 0;
									}
								}
							}
						}
						if(!tokens[0].contentEquals("Scan REF") && !tokens[0].contentEquals("Reporter REF") && !tokens[0].contentEquals("Composite Element REF")){
							String[] markerNames = tokens[0].split(":");
							int markLength = markerNames.length-1;
							markers.add(markerNames[markLength]);
							String markerName = new String(markerNames[markLength].trim());
							CSExpressionMarker marker = new CSExpressionMarker(m);
							marker.setLabel(markerName);
							maSet.getMarkerVector().add(m, marker);
							m++;
						}
						if(tokens[0].contentEquals("Reporter REF") || tokens[0].contentEquals("Composite Element REF")){
							if(n != 0){
								for(int p=0; p<n+1; p++){
									String[] valueLabels = null;
									valueLabels = tokens[p+1].split(":");
									/*
									 * This checks for the expression value column in the data file
									 * To my knowledge MAGE-TAB has four types of column headers for expression value
									 * i.e., 'VALUE', 'CHPSignal', 'AFFYMETRIX_VALUE', 'Quantification'
									 * In future if anyone finds new column header for expression value add it to the loop.
									 * 
									 * NOTE: In future geWorkbench should allow users to select the expression value and do lazy loading. 
									 * 
									 */
									if(valueLabels[1].contentEquals("VALUE") || valueLabels[1].contentEquals("CHPSignal") || valueLabels[1].contentEquals("AFFYMETRIX_VALUE")
											|| valueLabels[1].contentEquals("Quantification")){
										valueNumber = p+1;
									}
									/*
									 * This checks for the P value column in the data file
									 * To my knowledge MAGE-TAB has three types of column headers for detection P value 
									 * i.e., 'CHPDetectinoPvalue', 'AFFYMETRIX_Detection P-value', 'DETECTION P-VALUE'
									 * In future if anyone finds new column header for P value add it to the loop. 
									 * 
									 */
									
									if(valueLabels[1].contentEquals("DETECTION P-VALUE") || valueLabels[1].contentEquals("AFFYMETRIX_Detection P-value")
											|| valueLabels[1].contentEquals("CHPDetectionPvalue")){
										detection = p+1;
										pValueFound = true;
									}
									/*
									 * This checks for the ABS CALL column in the data file
									 * To my knowledge MAGE-TAB has two types of column headers for ABS_CALL
									 * i.e., 'ABS_CALL', 'CHPDetection'
									 * In future if anyone finds new column header for P value add it to the loop. 
									 * 
									 */
									if(valueLabels[1].contentEquals("ABS_CALL") || valueLabels[1].contentEquals("CHPDetection")){
										call = p+1;
										System.out.println(call);
										absCallFound = true;
									}
								}
							}
						}
						header = in.readLine();
					}
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		//Getting the markers size to include in a loop
		possibleMarkers = markers.size();
		for (int i = 0; i < arrayNames.size(); i++) {
			String arrayName = arrayNames.get(i);
			CSMicroarray array = new CSMicroarray(i, possibleMarkers,
					arrayName, null, null, false,
					DSMicroarraySet.affyTxtType);
			maSet.add(array);	
		}
		//This buffered reader is used to put insert marker values for one sample at a time from the Series file
		BufferedReader out = null;
		int j = 0;
		try {
			out = new BufferedReader(new FileReader(file));
			try {
				String line = out.readLine();
				while (line != null) {	
					String[] tokens = line.split("\t");
					/*
					 * This loop is activated if the data file has multiple columns for each array
					 *i.e., each array has 'value', 'detection P value', 'abs call'. If not it goes to the else part.
					 *
					 */
					if(n != 0){
						if(!tokens[0].contentEquals("Scan REF") && !tokens[0].contentEquals("Reporter REF") && !tokens[0].contentEquals("Composite Element REF")){
							String[] values1 = line.split("\t");
							List<String> values = new ArrayList<String>();
							for(int s=0; s<values1.length; s++){
								if(s!= 0){
									values.add(values1[s]);
								}
							}
							String valString = null; 
							int loopCount;
							loopCount = arrayNames.size()*(n+1);
							int count = 0;
							int counter = 0;
							String ca = null;
							for(int k = 1; k < (loopCount-n+1); k++){
								count++;
								if(count%(n+1) == 0 || k == 1){
									valString = values.get(k+(valueNumber-2)).trim();
									if(valString == null){
										Float v = Float.NaN;
										CSExpressionMarkerValue markerValue = new CSExpressionMarkerValue(v);
										maSet.get(counter).setMarkerValue(j, markerValue);
										if (v.isNaN()) {
											markerValue.setMissing(true);
										} else {
											markerValue.setPresent();
										}
										counter++;
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
										maSet.get(counter).setMarkerValue(j, markerValue);
										if (v.isNaN()) {
											markerValue.setMissing(true);
										} else {
											markerValue.setPresent();
										}
										/*
										 * This loop if only activated if the data file with multiple coloumns for each array either has
										 * detection value or abs call
										 * else no Affy detection value is set
										 * 
										 */
										if(detection !=0 || call != 0){
											if(detection != 0){	
												String token = null;
												token = values.get(k+(detection-2)).trim();
												Object value1 = null;
												value1 = Double.valueOf(token);
												markerValue.setConfidence(( (Double) value1).doubleValue());
												pValueFound = true;
											}
											if(call != 0){
												ca = values.get(k+(call-2)).trim();
												char C = ' ';
												if(ca.equalsIgnoreCase("A") || ca.equalsIgnoreCase("P")||ca.equalsIgnoreCase("M") ){
													C = ca.charAt(0);
												}else{
													/*
													 * This handles if the abs_call is not a character.
													 * for example: 'PRESENT', 'Present'
													 */
													if(ca.equalsIgnoreCase("ABSENT")){
														C = 'A';
													}
													if(ca.equalsIgnoreCase("PRESENT")){
														C = 'P';
													}
													if(ca.equalsIgnoreCase("MARGINAL")){
														C = 'M';
													}
												}
												char Call = Character.toUpperCase(C);
												if (Call == PRESENT || Call == ABSENT || Call == MARGINAL){
													this.detectionStatus = Call;
													absCallFound = true;
													if (!pValueFound){
														switch (Call){
															case PRESENT: 
																markerValue.setPresent();
																break;
															case ABSENT:
																markerValue.setAbsent();
																break;
															case MARGINAL:
																markerValue.setMarginal();
																break;
														}
													}
												}
											}
										}
										counter++;
									}
									count = 0;
								}
							}
							j++;
						}
					}else {
						if(!tokens[0].contentEquals("Scan REF") && !tokens[0].contentEquals("Reporter REF") && !tokens[0].contentEquals("Composite Element REF")){
							String[] values = line.split("\t");
							String valString = null; 
							for(int k = 0; k < arrayNames.size(); k++){
								valString = values[k+1].trim();
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
							}
							j++;
						}
					}
					line = out.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return maSet;
	}
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getOptions() {
		throw new UnsupportedOperationException(
				"Method getOptions() not yet implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.FileFormat#getFileFilter()
	 */
	public FileFilter getFileFilter() {
		return maFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.microarray.DataSetFileFormat#getDataFile(java.io.File[])
	 */
	@SuppressWarnings("unchecked")
	public DSDataSet getDataFile(File[] files) {
		// TODO Implement this
		// org.geworkbench.components.parsers.microarray.DataSetFileFormat
		// abstract method
		throw new UnsupportedOperationException(
				"Method getDataFile(File[] files) not yet implemented.");
	}
	
	

	/**
	 * Defines a <code>FileFilter</code> to be used when the user is prompted
	 * to select SOFT input files. The filter will only display files
	 * whose extension belongs to the list of file extensions defined.
	 * 
	 * @author yc2480
	 * @author nrp2119
	 */
	class MageTabFileFilter extends FileFilter {

		public String getDescription() {
			return getFormatName();
		}

		public boolean accept(File f) {
			boolean returnVal = false;
			for (int i = 0; i < maExtensions.length; ++i)
				if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
					return true;
				}
			return returnVal;
		}
	}
}