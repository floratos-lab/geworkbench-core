
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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ProgressMonitorInputStream;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;
import org.geworkbench.components.parsers.GeoSeriesMatrixParser;

/**  
 * @author Nikhil
 */
public class SOFTFileFormat extends DataSetFileFormat {

	static Log log = LogFactory.getLog(SOFTFileFormat.class);
	
	private static final String commentSign1 = "#";
	private static final String commentSign2 = "!";
	private static final String commentSign3 = "^";
	private static final String[] maExtensions = { "soft", "txt", "TXT"};

	ExpressionResource resource = new ExpressionResource();
		
	SOFTFilter maFilter = null;
	 public SOFTFileFormat() {
		formatName = "GEO Soft Files (GDS, GSE) & GEO Series Matrix Files";
		maFilter = new SOFTFilter();
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
	String[] checkLine = null;
	int carry = 0;
	public boolean checkFormat(File file) throws InterruptedIOException {
		BufferedReader check = null;
		String Checker = null;
		int arrays = 0;
		try {
			check = new BufferedReader(new FileReader(file));
			try {
				Checker = check.readLine();
				if(Checker == null)
				{
					carry = 1;
					return false;
				}
				while(Checker != null){
					int markNum = 0;
					if(!Checker.startsWith(commentSign1)&& !Checker.startsWith(commentSign2) && !Checker.startsWith(commentSign3)){
						checkLine = null;
						checkLine = Checker.split("\t");
						if(checkLine[0].equals("ID_REF")){
							arrays = checkLine.length;
						}
						if(!checkLine[0].equals("ID_REF")){
							markNum = checkLine.length;
							if(markNum != arrays){
								carry = 2;
								return false;
							}
						}
					}
					Checker = check.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.components.parsers.microarray.DataSetFileFormat#getDataFile(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	public DSDataSet getDataFile(File file) throws InputFileFormatException, InterruptedIOException{  
		
		BufferedReader readIn = null;
		String lineCh = null; 
		DSMicroarraySet maSet1 = new CSExprMicroarraySet();
		try {
			readIn = new BufferedReader(new FileReader(file));
			try {
				lineCh = readIn.readLine();
				if(lineCh.subSequence(0, 7).equals("!Series")){
					GeoSeriesMatrixParser parser = new GeoSeriesMatrixParser();
					maSet1 = parser.getMArraySet(file);
				}
				if(lineCh.subSequence(0, 7).equals("^SAMPLE")){
					SOFTSampleParser parser = new SOFTSampleParser();
					maSet1 = parser.getMArraySet(file);
				}
				if(lineCh.subSequence(0, 9).equals("^DATABASE")){
					
					lineCh = readIn.readLine();
					lineCh = readIn.readLine();
					lineCh = readIn.readLine();
					lineCh = readIn.readLine();
					lineCh = readIn.readLine();
					if(lineCh.subSequence(0, 7).equals("^SERIES")){
						SOFTSeriesParser parser = new SOFTSeriesParser();
						maSet1 = parser.getMArraySet(file);
					}
					if(!lineCh.subSequence(0, 7).equals("^SERIES")){
						maSet1 = getMArraySet(file);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print("Nikhil");
			e.printStackTrace();
		}
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
		
		if (!checkFormat(file)) {
			log
					.info("SOFTFileFormat::getMArraySet - "
							+ "Attempting to open a file that does not comply with the "
							+ "GEO SOFT file format.");
			throw new InputFileFormatException(
				"Attempting to open a file that does not comply with the "
				+ "SOFT file format.");
		}
					
		CSExprMicroarraySet maSet = new CSExprMicroarraySet();
		List<String> arrayNames = new ArrayList<String>();
		int possibleMarkers = 0;
		BufferedReader in = null;
		final int extSeperater = '.';
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(extSeperater);
		if (dotIndex != -1) {
			fileName = fileName.substring(0, dotIndex);
		}
		maSet.setLabel(fileName);
		
		List<String> markers = new ArrayList<String>();
		try {
			in = new BufferedReader(new FileReader(file));
			if(in != null){
				try {
					int m = 0;
					String header = in.readLine();
					while (header != null) {
						/*
					 	* Adding comments to Experiment Information tab.
					 	*We will ignore the line which start with '!dataset_table_begin' and '!dataset_table_end'
					 	*/
						if (header.startsWith(commentSign1) || header.startsWith(commentSign2)) {
							if(!header.equalsIgnoreCase("!dataset_table_begin") && !header.equalsIgnoreCase("!dataset_table_end")) {
								maSet.addDescription(header.substring(1));
							}
						}	
						String[] tokens = null;
						if(!header.startsWith(commentSign1) && !header.startsWith(commentSign2) && !header.startsWith(commentSign3)){
							if(header.subSequence(0, 6).equals("ID_REF")){
								tokens = header.split("\t");
								for(int n = 2; n<tokens.length; n++){
									arrayNames.add(tokens[n]);
								}
							}
							if(!header.subSequence(0, 6).equals("ID_REF")){
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
					if(!line.startsWith(commentSign1) && !line.startsWith(commentSign2) && !line.startsWith(commentSign3)){
						if(!line.subSequence(0, 6).equals("ID_REF")){
							String[] values = line.split("\t");
							String valString = null; 
							for(int k = 0; k < arrayNames.size(); k++){
								valString = values[k+2].trim();
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
				out.close();
				String result = null;
				for (int i = 0; i < possibleMarkers; i++) {
					result = AnnotationParser.matchChipType(maSet, maSet
							.getMarkerVector().get(i).getLabel(), false);
					if (result != null) {
						break;
					}
				}
				if (result == null) {
					AnnotationParser.matchChipType(maSet, "Unknown", true);
				} else {
					maSet.setCompatibilityLabel(result);
				}
				for (DSGeneMarker marker : maSet.getMarkerVector()) {
					String token = marker.getLabel();
					String[] locusResult = AnnotationParser.getInfo(token,
							AnnotationParser.LOCUSLINK);
					String locus = "";
					if ((locusResult != null)
							&& (!locusResult[0].trim().equals(""))) {
						locus = locusResult[0].trim();
					}
					if (locus.compareTo("") != 0) {
						try {
							marker.setGeneId(Integer.parseInt(locus));
						} catch (NumberFormatException e) {
							log.info("Couldn't parse locus id: " + locus);
						}
					}
					String[] geneNames = AnnotationParser.getInfo(token,
							AnnotationParser.ABREV);
					if (geneNames != null) {
						marker.setGeneName(geneNames[0]);
					}

					marker.getUnigene().set(token);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		labelDisp(file, maSet, arrayNames);
		return maSet;
	}
	/*
	 * Method adds data to Array/Phenotype Sets drop down in the selection panel 
	 */
	public void labelDisp(File dataFile, CSExprMicroarraySet mArraySet, List<String> arrays)
	{ 
		List<String> arrayNames = arrays;
		BufferedReader read = null;
		String line1 = null;
		try {
			read = new BufferedReader(new FileReader(dataFile));
			try {
				String phLabel = null;
				String[] token = null;
				line1 = read.readLine();
				while (line1 != null){		
					String[] lineSp1 = line1.split("=");
					String lineSp = lineSp1[0].trim();
					String lineDt = lineSp1[1].trim();
					
					if(lineSp.contentEquals("!subset_description")){
						phLabel = lineDt.trim();
					}
					if(lineSp.contentEquals("!subset_sample_id")){
						token = lineDt.split(",");
					}
					if(lineSp.contentEquals("!subset_type")){	
						CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
						DSAnnotationContext<DSMicroarray> context = manager.getContext(mArraySet, lineDt);
	                    CSAnnotationContext.initializePhenotypeContext(context);
	                    for(int m=0; m<token.length; m++){
	                    	for(int n=2; n<arrayNames.size(); n++){
	                    		if(token[m].contentEquals(arrayNames.get(n))){
	                    			context.labelItem(mArraySet.get(n-2), phLabel);
	                    		}
	                    	}
	                    }
	                }
					line1 = read.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
		} catch (IndexOutOfBoundsException e) {
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
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
	class SOFTFilter extends FileFilter {

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
