package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.builtin.projects.SaveFileFilterFactory;
import org.geworkbench.builtin.projects.SaveFileFilterFactory.CustomFileFilter;
 

public class CSTTestResultSet <T extends DSGeneMarker> extends CSSignificanceResultSet<T> implements DSTTestResultSet<T>{

	private static final long serialVersionUID = -6724936547706753609L;
	private boolean isLogNormalized=false;

	public CSTTestResultSet(DSMicroarraySet parent, String label,
			String[] caseLabels, String[] controlLabels, double alpha, boolean isLogNormalized) {
		super(parent, label, caseLabels, controlLabels, alpha);
		this.isLogNormalized=isLogNormalized;
		// TODO Auto-generated constructor stub	
	}
	
	public void saveDataToCSVFile()
    {
		
		 try{  
			 
			 
			    JFileChooser fc = new JFileChooser();
				CustomFileFilter filter = SaveFileFilterFactory.createCsvFileFilter();	
				fc.setFileFilter(filter);			 		 		 
				String exportFile = null;			
				if (JFileChooser.APPROVE_OPTION == fc
						.showSaveDialog(null)) {
					exportFile = fc.getSelectedFile().getPath();				
					if (!filter.accept(new File(exportFile))) {
						exportFile += "." + filter.getExtension();
					}
				
				
				} else {
					return;
				} 
				
				File aCSVFile = new File(exportFile);
				if (new File(exportFile).exists()) {
					int n = JOptionPane.showConfirmDialog(
							null,
							"The file exists, are you sure to overwrite?",
							"Overwrite?", JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
						JOptionPane.showMessageDialog(null, "Save cancelled.");
						return;
					}
				}
			 
			  
			   FileWriter writer = new FileWriter(aCSVFile);    	  
    	       //column headers
    	       writer.write("Probe Set Name, Gene Name, p-Value, Fold Change (log2)" + "\n");    	  
    	       int numMarkers = getSignificantMarkers().size();  
    	       for (int i = 0; i < numMarkers; i++) {
    		         T marker = getSignificantMarkers().get(i);    		   
                     writer.write(marker.getLabel() + ", " + marker.getShortName() + ", " + getSignificance(marker) + ", " + getFoldChange(marker) + "\n");
               }
    	       writer.flush();
    	       writer.close();
              
		    }catch(Exception e){

	        }
    }

	public boolean getIsLogNormalized() {
		// TODO Auto-generated method stub
		return isLogNormalized;
	}
	
}
