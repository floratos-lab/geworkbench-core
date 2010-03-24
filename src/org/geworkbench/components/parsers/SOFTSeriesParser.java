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
import java.util.StringTokenizer;

import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
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
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			if(in != null){
				try {
					String tempName = null;
					List<String> arrayNames = new ArrayList<String>();
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
							tempName = temp[1].trim();
						}
						if(header.startsWith(commentSign2)){
							temp = header.split("=");
							String temp1 = temp[1].trim()
											+ "("
											+tempName
											+")";
							arrayNames.add(temp1);	
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
		return maSet;
	}
}

