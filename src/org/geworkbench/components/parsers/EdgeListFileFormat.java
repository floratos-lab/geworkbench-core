package org.geworkbench.components.parsers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.components.parsers.microarray.DataSetFileFormat;
import org.geworkbench.bison.parsers.resources.EdgeListResource;
import org.geworkbench.util.pathwaydecoder.mutualinformation.*;
import org.geworkbench.bison.parsers.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.StringTokenizer;

/**
 * 
 * @author ch2514
 * @version $Id: EdgeListFileFormat.java,v 1.1 2007-08-31 15:58:20 hungc Exp $
 */
public class EdgeListFileFormat extends DataSetFileFormat {
	private Log log = LogFactory.getLog(EdgeListFileFormat.class);
	
	public static final String[] COMMENTS = {"#", ">"};
	public static final String[] EDGE_LIST_FILE_EXTENSIONS = {"txt", "TXT"};
	public static final String[] ADJACENCY_MATRIX_FILE_EXTENSIONS = {"adj", "ADJ"};
	public static final String[] GENE_NAME_GENE_ID_DELIMITER = {":", "/"};

    /**
     * The file extensions expected for Edge List files.
     */
    String[] elExtensions = {"txt", "TXT", "adj", "ADJ"};
    EdgeListResource resource = new EdgeListResource();
    DSMicroarraySet microarraySet = null;
    /**
     * <code>FileFilter</code> for gating Edge List files, based on their extension.
     */
    EdgeListFileFilter elFileFilter = null;

    /**
     * Default constructor. Will be invoked by the framework when the
     * <code>&lt;plugin&gt;</code> line for this format is encountered in the
     * application configuration file.
     */
    public EdgeListFileFormat() {
        formatName = "NetBoost Edge List";   // Setup the display name for the format.
        elFileFilter = new EdgeListFileFilter();
    }

    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFile(file);
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }

    /**
     * @return The file extensions defined for edge list data files.
     */
    public String[] getFileExtensions() {
        return elExtensions;
    }

    // Fixme.
    // In here we should check (among other things) that:
    // * There are no duplicate markers (ie., no 2 markers have the same name).
    // * The values of the data points respect their expected type.
    public boolean checkFormat(File f) {
    	return true;
    }

    /**
     * Return the <code>FileFilter</code> defined for Edge List files.
     *
     * @return
     */
    public FileFilter getFileFilter() {
        return elFileFilter;
    }

    /**
     * getDataFile
     *
     * @param file File
     * @return DataSet
     */
    public DSDataSet getDataFile(File file) {
        DSDataSet ds = null;
        try {
            ds = (DSDataSet) getEdgeListDataSet(file);
        } catch (InputFileFormatException ife) {
            ife.printStackTrace();
        }
        return ds;
    }

    public DSMicroarraySet getMArraySet(File file) throws InputFileFormatException {
    	// no-op
        return null;
    }
    
    public EdgeListDataSet getEdgeListDataSet(File file) throws InputFileFormatException {
    	EdgeListDataSet result = null;
    	if (!checkFormat(file))
            throw new InputFileFormatException("Attempting to open a file that does not comply with the Net Boost Edge List format.");        
    	
    	EdgeList el = null;
    	if(this.isEdgeListFile(file)){
    		el = parseEdgeListDataFile(file);
    		result = new EdgeListDataSet("Edge List", el, file.getName());
    	}
    	if(this.isAdjacencyMatrixFile(file)){
    		el = parseAdjacencyMatrixDataFile(file);
    		result = new EdgeListDataSet("Adjacency Matrix Derived Edge List", el, file.getName());
    	}
    	
    	if(el == null){
    		log.warn("Cannot parse Net Boost file: " + file.getName() + ": No edge list created.");
    	} else if (el.isEmpty()){
    		log.warn("Cannot parse Net Boost file: " + file.getName() + ": Edge list is empty.");
    	}    	
    	
    	return result;
    }
    
    private EdgeList parseEdgeListDataFile(File file) throws InputFileFormatException {
    	EdgeList el = new EdgeList();
    	try{ 
	        FileInputStream fileIn = new FileInputStream(file);
	        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Scanning File", fileIn);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(progressIn));
	
	        String line = null;
	        StringTokenizer st = null;
	        while ((line = reader.readLine()) != null) {
	            if (!line.trim().equals("") && !this.isComment(line)) {	     
        			st = new StringTokenizer(line);
        			int numTokens = st.countTokens();
        			if(numTokens == 2){
        				el.addEdge(new Edge(st.nextToken(), st.nextToken()));
        			} 
	            }
	        }
    	} catch (IOException e){
    		log.debug("Problems reading file: " + file.getName(), e);
    		throw new InputFileFormatException("Cannot read Net Boost file: " + file.getName());
    	} catch (Exception e){
    		log.debug("Problems parsing file: " + file.getName(), e);
    		throw new InputFileFormatException("Cannot parse Net Boost file: " + file.getName());
    	}
        return el;
    }
    
    private EdgeList parseAdjacencyMatrixDataFile(File file) throws InputFileFormatException {
    	EdgeList el = new EdgeList();
    	try{ 
	        FileInputStream fileIn = new FileInputStream(file);
	        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Scanning File", fileIn);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(progressIn));
	
	        String line = null;
	        StringTokenizer st = null;
	        while ((line = reader.readLine()) != null) {
	        	if (!line.trim().equals("") && !this.isComment(line)) {	     
        			st = new StringTokenizer(line);
        			int numTokens = st.countTokens();
        			if(numTokens >= 3){
        				String gene1ID = st.nextToken().trim();
        				String delim = this.getDelimiter(gene1ID);
        				if(!delim.trim().equals("")){
        					gene1ID = gene1ID.substring(gene1ID.indexOf(delim) + 1);
        				}
        				while(st.hasMoreTokens()){
        					String gene2ID = st.nextToken().trim();
        					delim = this.getDelimiter(gene2ID);
        					if(!delim.trim().equals("")){
            					gene2ID = gene2ID.substring(gene2ID.indexOf(delim) + 1);
            				}
        					el.addEdge(gene1ID, gene2ID);
        					if(st.hasMoreTokens()){	// skipping interaction strength
        						st.nextToken();	
        					}
        				}
        			} 
	        	}
	        }
    	} catch (IOException e){
    		log.debug("Problems reading file: " + file.getName(), e);
    		throw new InputFileFormatException("Cannot read Net Boost file: " + file.getName());
    	} catch (Exception e){
    		log.debug("Problems parsing file: " + file.getName(), e);
    		throw new InputFileFormatException("Cannot parse Net Boost file: " + file.getName());
    	}
    	return el;
    }
    
    private boolean isComment(String line){
    	boolean result = false;
    	for(String comment: COMMENTS){
    		if(line.trim().startsWith(comment)){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
    
    private String getDelimiter(String token){
    	String result = "";
    	for(String delim: GENE_NAME_GENE_ID_DELIMITER){
    		int index = token.trim().indexOf(delim);
    		if((index >= 0) && (index < token.length())){
    			result = delim;
    			break;
    		}
    	}    	
    	return result;
    }
    
    private boolean isEdgeListFile(File file){
    	boolean result = false;
    	for(String ext: EDGE_LIST_FILE_EXTENSIONS){
    		if(file.getName().endsWith(ext)){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
    
    private boolean isAdjacencyMatrixFile(File file){
    	boolean result = false;
    	for(String ext: ADJACENCY_MATRIX_FILE_EXTENSIONS){
    		if(file.getName().endsWith(ext)){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }

    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet getDataFile(File[] files) {
    	// no-op
        return null;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select edge list input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class EdgeListFileFilter extends FileFilter {
        public String getDescription() {
            return getFormatName();
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < elExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(elExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
