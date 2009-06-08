package org.geworkbench.util;

import java.io.IOException;
import java.util.Date;

import javax.swing.JOptionPane;

import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GeneOntologyTree;
import org.geworkbench.engine.properties.PropertiesManager;

public class GeneOntologyTreePopulateComponent {
	private static final String DEFAULTOBOFILE = "data/gene_ontology.1_2.obo"; //$NON-NLS-1$
	private String obofileName;
	private String OBOFILEKEY = "obofile_location";

	public String getObofileName() {
		return obofileName;
	}

	/**
	 * @param obofileName
	 *            the obofileName to set
	 */
	public void setObofileName(String obofileName) {
		this.obofileName = obofileName;
		try {
			PropertiesManager.getInstance().setProperty(getClass(), OBOFILEKEY,
					obofileName);
			createGOTree();
		} catch (Exception e) {

		}
	}
/**
 * To create a single tree for the application.
 * @return
 * @throws Exception
 */
	public boolean createGOTree() throws Exception {

		obofileName = PropertiesManager.getInstance().getProperty(getClass(),
				OBOFILEKEY, obofileName); //$NON-NLS-1$
		if (obofileName == null) {
			obofileName = DEFAULTOBOFILE;
		}
		GeneOntologyTree.getInstance().parseOBOFile(obofileName);

		return true;
	}

	public GeneOntologyTreePopulateComponent() {
		try{
		createGOTree();
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The gene ontology data file cannot be read/parsed at " + obofileName, "File Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public GeneOntologyTreePopulateComponent(String obofileName) {
		super();
		this.obofileName = obofileName;
		try{
			createGOTree();
			}catch (Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "The gene ontology data file cannot be read/parsed at " + obofileName, "File Error", JOptionPane.ERROR_MESSAGE);
			}
	}

}
