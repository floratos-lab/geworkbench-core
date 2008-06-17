package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;

public class CSTTestResultSet <T extends DSGeneMarker> extends CSSignificanceResultSet<T> implements DSTTestResultSet<T>{

	private static final long serialVersionUID = 1L;

	public CSTTestResultSet(DSMicroarraySet<DSMicroarray> parent, String label,
			String[] caseLabels, String[] controlLabels, double alpha) {
		super(parent, label, caseLabels, controlLabels, alpha);
		// TODO Auto-generated constructor stub	
	}
	
	public void saveDataToCSVFile()
    {
		
		 try{  
			 
			 JFileChooser chooser = new JFileChooser( ".");
             ExampleFilter filter = new ExampleFilter();
             filter.addExtension("csv");
             filter.setDescription("CSV Files");
             chooser.setFileFilter(filter);
             int returnVal = chooser.showSaveDialog(null);
             if (returnVal == JFileChooser.APPROVE_OPTION) 
             { 
			     File aCSVFile = new File(chooser.getSelectedFile().getAbsolutePath()+ ".csv");
			     FileWriter writer = new FileWriter(aCSVFile);    	  
    	         //column headers
    	         writer.write("Probe Set Name, Gene Name, p-Value, Fold Change" + "\n");    	  
    	         int numMarkers = getSignificantMarkers().size();  
    	         for (int i = 0; i < numMarkers; i++) {
    		         T marker = getSignificantMarkers().get(i);    		   
                     writer.write(marker.getLabel() + ", " + marker.getShortName() + ", " + getSignificance(marker) + ", " + getFoldChange(marker) + "\n");
                 }
    	         writer.flush();
    	         writer.close();
             }
		    }catch(Exception e){

	        }
    }
	
}
