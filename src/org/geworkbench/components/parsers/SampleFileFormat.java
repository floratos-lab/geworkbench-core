package org.geworkbench.components.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.parsers.SOFTParser;

public class SampleFileFormat {
    /**
     * The file extensions expected for Affy files.
     */	
  
    private Log log = LogFactory.getLog(this.getClass());
    DSMicroarraySet microarraySet = null;

    public boolean checkFormat(File f) {
    	
//    	return true;

        boolean headerExist = false;
        boolean columnsMatch = true;
        boolean noDuplicateMarkers = true;
        boolean valuesAreExpectedType = true;
        BufferedReader reader = null;
    	try{
	        FileInputStream fileIn = new FileInputStream(f);
	        reader = new BufferedReader(new InputStreamReader(fileIn));
	
	        String line = null;
	        int totalColumns = 0;
	        int accessionIndex= -1;
	        Set<String> markers = new HashSet<String>();
	        int lineIndex = 0;
	        while ((line = reader.readLine()) != null) { //for each line
	        	if (line.indexOf("ID_REF") >= 0) {
	                headerExist = true;
	            }
	        	if (headerExist){//we'll skip anything before header
		            String token=null;
			        int columnIndex = 0;
		            StringTokenizer st = new StringTokenizer(line, "\t\n");	            
		            while (st.hasMoreTokens()) {	//for each column
		                token = st.nextToken().trim();
		                if (token.equals("ID_REF")) {
		                    accessionIndex = columnIndex;
		                }else if (headerExist && (columnIndex==accessionIndex)){ // if this line is after header, then first column should be our marker name
		                	if (markers.contains(token)){//duplicate markers
		                		noDuplicateMarkers=false;
		                		log.info("duplicate markers: "+token);
		                	}else{
		                		markers.add(token);
		                	}
		                }
		                columnIndex++;
		            }
		            //check if column match or not
		            if (headerExist){ //if this line is real data, we assume lines after header are real data. (we might have bug here)
		            	if (totalColumns==0)//not been set yet
		            		totalColumns=columnIndex;
		            	else if (columnIndex!=totalColumns){//if not equal 
		            		columnsMatch=false;
		            		log.debug("In the file"+f.getName()+", header contains "+totalColumns+" columns, but line "+lineIndex+" only contains "+columnIndex+" columns.");
		            	}
		            }
	        	}
	        	lineIndex++;
	        }
	        fileIn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	  //if (headerExist && columnsMatch && noDuplicateMarkers)
	    if (headerExist && noDuplicateMarkers)
		    
	    	return true;
	    else
	    	return false;

    }

    public void getMArraySet(File file, CSExprMicroarraySet maSet) throws InputFileFormatException, InterruptedIOException {
        // Check that the file is OK before starting allocating space for it.
		if (!checkFormat(file))
				throw new InputFileFormatException(
						"Attempting to open a file that does not comply with the "
								+ "GEO SOFT Sample format.");
	        log.info(file.getAbsoluteFile() + " passed file format check");
        BufferedReader reader = null;
        ProgressMonitorInputStream progressIn = null;
        try {
            // microarraySet = new CSExprMicroarraySet((AffyResource) getResource(file));
            microarraySet = maSet;
            List ctu = new ArrayList();
            ctu.add("ID_REF");
            ctu.add("VALUE");
            System.out.print("STEP 1");
            SOFTParser parser = new SOFTParser(ctu);
            System.out.print("STEP 2");
            FileInputStream fileIn = new FileInputStream(file);
            progressIn = new ProgressMonitorInputStream(null, "Scanning File", fileIn);
            reader = new BufferedReader(new InputStreamReader(progressIn));

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    parser.process(line);
                }
            }

            Vector v = parser.getAccessions();
            microarraySet.setLabel(file.getName());
            if (microarraySet.getCompatibilityLabel() == null || microarraySet.getCompatibilityLabel().equals("")) {
                microarraySet.setCompatibilityLabel(AnnotationParser.matchChipType(maSet, "", false));
            }
//            microarraySet.setCompatibilityLabel("MAS");
            microarraySet.initialize(1, v.size());
//            String chiptype = AnnotationParser.matchChipType(microarraySet, "dummymarker", false);
            CSMarkerVector markerVector = (CSMarkerVector) microarraySet.getMarkers();
            int count = 0;
            for (Iterator it = v.iterator(); it.hasNext();) {
                String acc = ((String) it.next()).toString();
                markerVector.setLabel(count, acc);
                String[] geneNames = AnnotationParser.getInfo(acc, AnnotationParser.ABREV);
                if (geneNames != null) {
                    markerVector.get(count).setGeneName(geneNames[0]);
                }                
                markerVector.get(count).setDisPlayType(DSGeneMarker.AFFY_TYPE);
                markerVector.get(count++).setDescription(acc);
            }
            reader.close();
            // Read again, this time loading data
            fileIn = new FileInputStream(file);
            progressIn = new ProgressMonitorInputStream(null, "Loading Data", fileIn);
            reader = new BufferedReader(new InputStreamReader(progressIn));
            System.out.print("Step 6");
            CSMicroarray microarray = new CSMicroarray(0, v.size(), file.getName(), null, null, true, DSMicroarraySet.affyTxtType);
            microarray.setLabel(file.getName());
            parser.reset();
            parser.setMicroarray(microarray);
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals("")) {
                	System.out.print("Step 7");
                    parser.parseLine(line);
                }
            }
            reader.close();
            parser.getMicroarray().setLabel(file.getName());
            microarraySet.add(0, parser.getMicroarray());
            microarraySet.setFile(file);
        }
        catch (java.io.InterruptedIOException ie) {
    			if ( progressIn.getProgressMonitor().isCanceled())
    			{	
    				throw ie;    				 			 
    			}			 
    			else
    			   ie.printStackTrace();
        
        } catch (Exception ec) {
            log.error(ec,ec);
        } finally {
        	try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    public DSMicroarraySet getMArraySet(File file) throws InputFileFormatException, InterruptedIOException{
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        getMArraySet(file, maSet);
        if (maSet.loadingCancelled)
            return null;
        return maSet;
    }   
}

