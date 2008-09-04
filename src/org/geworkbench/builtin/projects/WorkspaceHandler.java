/*
 * The geworkbench-core project
 * 
 * Copyright (c) 2008 Columbia University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.geworkbench.builtin.projects;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.APSerializable;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.builtin.projects.ProjectPanel.WorkspaceFileFilter;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.properties.PropertiesManager;
import org.geworkbench.util.ProgressBar;
import org.geworkbench.util.threading.SwingWorker;

/**
 * this class handles workspace's saving and opening: showing progress bar
 * 
 * the main purpose to build this class is to take some tasks out of the super
 * large ProjectPanel
 * 
 * @author zji
 * 
 */
public class WorkspaceHandler {
	static Log log = LogFactory.getLog(WorkspaceHandler.class);

	private ProjectPanel enclosingProjectPanel = null;

	WorkspaceHandler(ProjectPanel enclosingProjectPanel) {
		this.enclosingProjectPanel = enclosingProjectPanel;
	}

	ProgressBar pb = null;
	
	private String wsFilePath = "";

	/**
	 * Prompt the user to save the current workspace.
	 */
	public void save(String WORKSPACE_DIR, boolean terminating) {
		PropertiesManager properties = PropertiesManager.getInstance();
		String workspaceDir = ".";
		try {
			workspaceDir = properties.getProperty(ProjectPanel.class,
					WORKSPACE_DIR, workspaceDir);
		} catch (IOException exception) {
			exception.printStackTrace(); // To change body of catch statement
			// use File | Settings | File
			// Templates.
		}

		JFileChooser fc = new JFileChooser(workspaceDir);
		String wsFilename = null;
		FileFilter filter = enclosingProjectPanel.new WorkspaceFileFilter();
		fc.setFileFilter(filter);
		fc.setDialogTitle("Save Current Workspace");
		String extension = ((WorkspaceFileFilter) filter).getExtension();
		int choice = fc.showSaveDialog(enclosingProjectPanel.jProjectPanel);
		if (choice == JFileChooser.APPROVE_OPTION) {
			wsFilename = fc.getSelectedFile().getAbsolutePath();

			if (fc.getSelectedFile().exists()) {
				if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
						null,
						"Are you sure you want to overwrite this workspace?",
						"Overwrite?", JOptionPane.YES_NO_OPTION)) {
					JOptionPane.showMessageDialog(null, "Save cancelled.");
					return;
				}
			}

			// Store directory that we opened this from
			try {
				properties.setProperty(ProjectPanel.class, WORKSPACE_DIR, fc
						.getSelectedFile().getParent());
			} catch (IOException e1) {
				e1.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			}

			if (!wsFilename.endsWith(extension)) {
				wsFilename += extension;
			}

		    PanelFocusedListener wfl = new PanelFocusedListener();
			SaveTask task = new SaveTask(wsFilename, terminating, wfl);
			task.execute();
			pb = ProgressBar.create(ProgressBar.INDETERMINATE_TYPE);
			pb.setTitle("Workspace is being saved.");
			pb.start();
			
			wsFilePath = wsFilename;

		}
	}

	/**
	 * Prompt the user to open a saved workspace.
	 */
	public void open(String WORKSPACE_DIR) {
		PropertiesManager properties = PropertiesManager.getInstance();
		String workspaceDir = ".";
		try {
			workspaceDir = properties.getProperty(ProjectPanel.class,
					WORKSPACE_DIR, workspaceDir);
		} catch (IOException exception) {
			exception.printStackTrace(); // To change body of catch statement
			// use File | Settings | File
			// Templates.
		}

		// Prompt user for designating the file containing the workspace to be
		// opened.
		JFileChooser fc = new JFileChooser(workspaceDir);
		String wsFilename = null;
		FileFilter filter = enclosingProjectPanel.new WorkspaceFileFilter();
		fc.setFileFilter(filter);
		fc.setDialogTitle("Open Workspace");
		String extension = ((WorkspaceFileFilter) filter).getExtension();
		int choice = fc.showOpenDialog(enclosingProjectPanel.jProjectPanel);
		if (choice == JFileChooser.APPROVE_OPTION) {
			wsFilename = fc.getSelectedFile().getAbsolutePath();
			if (!wsFilename.endsWith(extension)) {
				wsFilename += extension;
			}

			// Store directory that we opened this from
			try {
				properties.setProperty(ProjectPanel.class, WORKSPACE_DIR, fc
						.getSelectedFile().getParent());
			} catch (IOException e) {
				log.info("current directory was not successfuly stored.");
				e.printStackTrace();
			}

			OpenTask openTask = new OpenTask(wsFilename);
			openTask.execute();
			pb = ProgressBar.create(ProgressBar.INDETERMINATE_TYPE);
			pb.setTitle("Workspace is being loaded.");
			pb.setAlwaysOnTop(true);
			pb.start();
			
			wsFilePath = wsFilename;

		}

	}
	
	public String getWorkspacePath(){
		return this.wsFilePath;
	}

	private class SaveTask extends SwingWorker<Void, Void> {
		private String filename;
		private boolean terminating;
		private PanelFocusedListener wfl = null;

		SaveTask(String filename, boolean terminating, PanelFocusedListener wfl ) {
			super();
			this.filename = filename;
			this.terminating = terminating;
			this.wfl = wfl;
		}

		@Override
		protected void done() {
			GeawConfigObject.getGuiWindow().removeWindowFocusListener(wfl); 			 
			pb.dispose();
			JOptionPane.getRootFrame().setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(null, "Workspace saved.");			
			if(terminating) {
				GeawConfigObject.getGuiWindow().dispose();
				System.exit(0);
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			GeawConfigObject.getGuiWindow().addWindowFocusListener(wfl);
			enclosingProjectPanel.serialize(filename);
			return null;
		}

	}

	private class OpenTask extends SwingWorker<Void, Void> {
		private String filename;

		OpenTask(String filename) {
			super();
			this.filename = filename;
		}

		@Override
		protected void done() {
			try {
				get();
			} catch (ExecutionException e) {
				// printStackTrace what is from doInBackground
				e.getCause().printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Check that the file contains a valid workspace.",
						"Open Workspace Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				// This should not happen. get() is called only to handle the
				// exception from doInBackGound
				e.printStackTrace();
			}
			pb.dispose();
		}

		@Override
		protected Void doInBackground() throws Exception {
			FileInputStream in = new FileInputStream(filename);
			ObjectInputStream s = new ObjectInputStream(in);
			SaveTree saveTree = (SaveTree) s.readObject();
			enclosingProjectPanel.clear();
			enclosingProjectPanel.populateFromSaveTree(saveTree);
			APSerializable aps = (APSerializable) s.readObject();
			AnnotationParser.setFromSerializable(aps);
			// ProjectTreeNode tempNode = (ProjectTreeNode) s.readObject();
			// // Clean up local structures and notify interested components
			// to clean
			// // themselves up.
			// root = tempNode;
			// projectRenderer.clearNodeSelections();
			// projectTreeModel = new DefaultTreeModel(root);
			// projectTree.setModel(projectTreeModel);

			return null;
		}

	}
	
	private class PanelFocusedListener
    implements java.awt.event.WindowFocusListener {
	 
		public void windowGainedFocus(java.awt.event.WindowEvent e) {
      		  
       	   if (pb.isShowing())
       	   {  
       		   pb.setFocusable(true);
       	       pb.requestFocus();
       	       
       	   }
       		
        }

        public void windowLostFocus(java.awt.event.WindowEvent e) {
      		    	//do nothing; 
           	    
        }
     
   
 }
   

	
	
}
