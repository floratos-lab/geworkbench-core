package org.geworkbench.builtin.projects;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.builtin.projects.SaveFileFilterFactory.CustomFileFilter;
import org.geworkbench.engine.properties.PropertiesManager;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: First Genetic Trust Inc.
 * </p>
 * <p/> <code>ProjectTree</code> node which represents a generic node in the
 * Project panel component
 * 
 * @author First Genetic Trust
 * @version $Id$
 */
public class ProjectTreeNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 6368086703128743579L;

	protected String description = "";

	/**
	 * Default Constructor, required by its sub-class's constructor
	 */
	protected ProjectTreeNode() {
	}

	/**
	 * Constructor
	 * 
	 * The only time you construct an instance of this class, instead of its sub-class, is the root node. */
	public ProjectTreeNode(String rootLabel) {
		setUserObject(rootLabel);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected String dirPropertyKey = null;
	protected DSDataSet<? extends DSBioObject> getDataset() {
		return null;
	}
	/* the extending class can extend this class, or only modify the two members above */
	protected void writeToFile(final boolean tabDelimited, final Component dialogParent) {
		DSDataSet<? extends DSBioObject> ds = getDataset();
		if(ds==null) {
			JOptionPane.showMessageDialog(null,
					"This node contains no Dataset.", "Save Error",
					JOptionPane.ERROR_MESSAGE);
		}

		File f = ds.getFile();
		JFileChooser jFileChooser1 = new JFileChooser(f);
		jFileChooser1.setSelectedFile(f);

		PropertiesManager properties = PropertiesManager.getInstance();
		if (f == null) {
			try {
				String dir = properties.getProperty(this.getClass(),
						dirPropertyKey, jFileChooser1.getCurrentDirectory()
								.getPath());
				jFileChooser1.setCurrentDirectory(new File(dir));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		CustomFileFilter filter = null;
		if (tabDelimited)
			filter = SaveFileFilterFactory.getTabDelimitedFileFilter();
		else
			filter = SaveFileFilterFactory.createFilter(ds);

		if (f != null && !filter.accept(f)){
			String newFileName = f.getAbsolutePath();
			newFileName = newFileName.substring(0, newFileName.lastIndexOf("."));
			newFileName += "." + filter.getExtension();
			jFileChooser1.setSelectedFile(new File(newFileName));
		}

		jFileChooser1.setFileFilter(filter);
		
		if (JFileChooser.APPROVE_OPTION == jFileChooser1
				.showSaveDialog(dialogParent)) {
			String newFileName = jFileChooser1.getSelectedFile()
					.getAbsolutePath();

			if (f == null) {
				try {
					properties.setProperty(this.getClass(), dirPropertyKey,
							jFileChooser1.getSelectedFile().getParent());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (!filter.accept(new File(newFileName))) {
				newFileName += "." + filter.getExtension();
			}
		

			if (new File(newFileName).exists()) {
				int o = JOptionPane.showConfirmDialog(null, 
						"The file already exists. Do you wish to overwrite it?",
						"Replace the existing file?",
						JOptionPane.YES_NO_OPTION);
				if (o != JOptionPane.YES_OPTION) {
					return;
				}
			}
			
			try {
				if (tabDelimited
						&& ds instanceof DSMicroarraySet)
					((DSMicroarraySet) ds).writeToTabDelimFile(newFileName);
				else
					ds.writeToFile(newFileName);
			} catch (RuntimeException e) {
				JOptionPane.showMessageDialog(null, e.getMessage() + " "
						+ ds.getClass().getName(), "Save Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}