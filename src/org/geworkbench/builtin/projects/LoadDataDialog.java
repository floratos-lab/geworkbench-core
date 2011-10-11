package org.geworkbench.builtin.projects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.builtin.projects.remoteresources.CaArrayFilteringDialog;
import org.geworkbench.builtin.projects.remoteresources.RemoteResourceDialog;
import org.geworkbench.builtin.projects.util.CaARRAYPanel;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.parsers.DataSetFileFormat;
import org.geworkbench.parsers.FileFormat;
import org.geworkbench.util.FilePathnameUtils;

/**
 *  Popup to select a file (local or remote) to open.
 *
 * @author First Genetic Trust Inc.
 * @version $Id$
 */
public class LoadDataDialog extends JDialog {
	private Log log = LogFactory.getLog(LoadDataDialog.class);
	/* this class name must match the one in all.xml */
	private static final String CAARRAYPANEL_CLASS_NAME = "org.geworkbench.builtin.projects.util.CaARRAYPanel";
	/* this class name must match the one loaded through ccm */
	private static final String CAARRAYCOMPONENT_CLASS_NAME = "org.geworkbench.components.caarray.arraydata.CaArray2Component";

	private static final long serialVersionUID = -1983039293757013174L;

	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();

	private JRadioButton localFileRadioButton = new JRadioButton();
	private JPanel jPanel4 = new JPanel();

	private JFileChooser jFileChooser1 = new JFileChooser();

	private JTextArea experimentInfoArea = new JTextArea();

	private String format = null;

	private CaARRAYPanel caArrayDisplayPanel = null;

	private JPanel lowerPanel;
	private RemoteResourceDialog remoteResourceDialog;

	private String currentRemoteResourceName;
	private String currentDetailedResourceName; // current resource name which
												// shows detail at the top
												// panel.
	private CaArrayFilteringDialog caarrayFilterQueryDialog;
	 
	/**
	 * Stores the <code>FileFormat</code> objects for the supported input
	 * formats.
	 */
	private FileFormat[] supportedInputFormats = null;

	private JRadioButton remoteRadioButton = new JRadioButton();
	private JPanel jPanel10 = new JPanel();

	private DefaultComboBoxModel resourceModel = new DefaultComboBoxModel(
			new String[] { "caARRAY", "GEDP" } );
	private JComboBox jComboBox1 = new JComboBox(resourceModel);

	private JButton addButton = new JButton();
	private JButton queryButton = new JButton();
	private JButton editButton = new JButton();
	private JButton deleteButton = new JButton();

	private JButton openRemoteResourceButton = new JButton();
	private boolean isSynchronized = false;

	private static final String QUERYTITLE = "Query the caARRAY Server.";

	public LoadDataDialog() {

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		checkCaArraySupportingClasses();
	}

	void checkCaArraySupportingClasses () {
		/* display remote button is classes needed are not loaded*/
		boolean caArrayPanelAvailable = false;
		boolean caArrayComponentAvailable = false;
		List<Object> list = ComponentRegistry.getRegistry().getComponentsList();
		for(Object obj: list) {
			if(obj.getClass().getName().startsWith(CAARRAYPANEL_CLASS_NAME)) { // the name is decorated, so 'startsWith'
				caArrayDisplayPanel = (CaARRAYPanel)obj;
				caArrayPanelAvailable = true;
			}
			if(obj.getClass().getName().startsWith(CAARRAYCOMPONENT_CLASS_NAME)) {
				caArrayComponentAvailable = true;
			}
			if(caArrayPanelAvailable && caArrayComponentAvailable){
				remoteRadioButton.setEnabled(true);
				return;
			}
		}
		localFileRadioButton.setSelected(true);
		switchToLocalFileDialog();
		remoteRadioButton.setEnabled(false);
	}

	public void setCaARRAYServer(String url, int portnumber) {
		caArrayDisplayPanel.setUrl(url + ":" + portnumber);
	}

	public void setDirectory(String directory) {
		jFileChooser1.setCurrentDirectory(new File(directory));
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public CaARRAYPanel getCaArrayDisplayPanel() {
		return caArrayDisplayPanel;
	}

	private void jbInit() throws Exception {
		this.setModal(false);
		this.getContentPane().setLayout(new BorderLayout());

		jPanel1.setLayout(new GridLayout());
		jPanel2.setLayout(new FlowLayout());
		localFileRadioButton.setText("Local File");

		jPanel4.setLayout(new BorderLayout());

		experimentInfoArea.setPreferredSize(new Dimension(300, 300));
		experimentInfoArea.setText("");
		experimentInfoArea.setEditable(false);
		experimentInfoArea.setLineWrap(true);
		experimentInfoArea.setWrapStyleWord(true);

		this.setResizable(true);

		lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
		remoteResourceDialog = new RemoteResourceDialog();
		//addButton.setToolTipText("Add a new resource");
		addButton.setText("Add a new Profile");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayRemoteResourceDialog(RemoteResourceDialog.ADD);

			}

		});

		caarrayFilterQueryDialog = new CaArrayFilteringDialog(JOptionPane
				.getFrameForComponent(this), QUERYTITLE);
		queryButton.setToolTipText("Filtering the selections.");
		queryButton.setText("Filtering");
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String remoteSourceName = (String) jComboBox1.getSelectedItem();
				RemoteResourceDialog.setPreviousResourceName(remoteSourceName);
				if (caarrayFilterQueryDialog == null) {
					caarrayFilterQueryDialog = new CaArrayFilteringDialog(JOptionPane
							.getFrameForComponent(LoadDataDialog.this), QUERYTITLE);
				}
				caarrayFilterQueryDialog.display(LoadDataDialog.this, remoteSourceName);
			}

		});

		//editButton.setToolTipText("Edit an existed source");
		editButton.setText("Edit Profile");
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentRemoteResourceName = jComboBox1.getSelectedItem()
						.toString();
				displayRemoteResourceDialog(currentRemoteResourceName,
						RemoteResourceDialog.EDIT);

			}
		});

		jComboBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCurrentView();

			}
		});

		//deleteButton.setToolTipText("Delete an existed resource");
		deleteButton.setText("Delete Profile");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteButton_actionPerformed(e);
			}

		});
		jPanel10.setLayout(new BoxLayout(jPanel10, BoxLayout.X_AXIS));
		openRemoteResourceButton.setText("Go");
		openRemoteResourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openRemoteResourceButton_actionPerformed(e);
			}
		});
		jComboBox1.setPreferredSize(new Dimension(50, 19));

		jPanel10.add(jComboBox1);
		jPanel10.add(openRemoteResourceButton);
		jPanel10.add(queryButton);
		jPanel10.add(addButton);
		jPanel10.add(editButton);
		jPanel10.add(deleteButton);

		lowerPanel.add(jPanel1);

		this.getContentPane().add(lowerPanel, BorderLayout.SOUTH);

		jPanel1.add(jPanel2, null);
		jPanel2.add(localFileRadioButton, null);

		JPanel jPanel7 = new JPanel();
		jPanel1.add(jPanel7, null);
		remoteRadioButton.setText("Remote");
		jPanel7.add(remoteRadioButton, null);

		this.getContentPane().add(jPanel4, BorderLayout.CENTER);
		jPanel4.add(jFileChooser1, BorderLayout.CENTER);

		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(localFileRadioButton);
		buttonGroup1.add(remoteRadioButton);

		localFileRadioButton.setSelected(true);
		localFileRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switchToLocalFileDialog();
			
			}
		});

		remoteRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRemotePanel_actionPerformed(e);
			}
		});
		jFileChooser1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jFileChooser1_actionPerformed(e);
			}
		});
		this.getContentPane().setSize(new Dimension(683, 450));
		this.setTitle("Open File");
		this.updateExistedResourcesGUI();
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Queries the <code>PluginRegistry</code> for all supported file formats
	 * and sets the file chooser options accordingly.
	 */
	public void setupInputFormats() {
		int i; 		
	 
		// Get the supported formats from the registry.
		/**
		 * PluginDescriptor[] inputFormats =
		 * PluginRegistry.getPluginsAtExtension("input-format"); if
		 * (inputFormats != null) { supportedInputFormats = new
		 * FileFormat[inputFormats.length]; for (i = 0; i < inputFormats.length;
		 * ++i) { supportedInputFormats[i] = (FileFormat)
		 * inputFormats[i].getPlugin(); } }
		 */
		supportedInputFormats = ComponentRegistry.getRegistry().getModules(
				org.geworkbench.parsers.FileFormat.class);
		Arrays.sort(supportedInputFormats, new FileFormatComparator());

		// Setup the file chooser options.
		jFileChooser1.resetChoosableFileFilters();
		jFileChooser1.setAcceptAllFileFilterUsed(false);
		jFileChooser1.setMultiSelectionEnabled(true);
		for (i = 0; i < supportedInputFormats.length; ++i) {
			jFileChooser1.addChoosableFileFilter(supportedInputFormats[i]
					.getFileFilter());
		}

		int idx = 0;	 
		
		if ((format != null)
				&& (!format.equals(""))
				&& ((idx = Integer.parseInt(format)) < supportedInputFormats.length)) {
			jFileChooser1.setFileFilter(supportedInputFormats[idx]
					.getFileFilter());		
			 
		}
	}

	/**
	 * Comparator for sorting file format based on their names.
	 * @author zji
	 *
	 */
	private static class FileFormatComparator implements Comparator<FileFormat> {

		public int compare(FileFormat o1, FileFormat o2) {
			return (o1.getFormatName().compareToIgnoreCase(o2.getFormatName()));
		}

	}

	/**
	 * displayRemoteResourceDiolog for add a new resouce.
	 *
	 * @param option
	 *            int
	 */
	private void displayRemoteResourceDialog(int option) {
		RemoteResourceDialog.setPreviousResourceName((String) jComboBox1
				.getSelectedItem());
		RemoteResourceDialog.showDialog(this, null, option, null);
		updateExistedResourcesGUI();
	}

	/**
	 * displayRemoteResourceDiolog for edit/delete an existed resource.
	 *
	 */
	private void displayRemoteResourceDialog(String shortname, int option) {
		if (RemoteResourceDialog.showDialog(this, null, option, shortname))
			updateExistedResourcesGUI();
	}

	/**
	 * updateExistedResources, should be called after any button related to
	 * remoteresource is clicked.
	 */
	private void updateExistedResourcesGUI() {
		String[] resources = RemoteResourceDialog.getResourceNames();
		if (resources != null) {
			resourceModel.removeAllElements();
			for (String s : resources) {
				if (resourceModel.getIndexOf(s) == -1) {
					resourceModel.addElement(s);
				}
			}
			if (remoteResourceDialog.getCurrentResourceName() != null
					&& !remoteResourceDialog.getCurrentResourceName()
							.equals("")) {
				jComboBox1.setSelectedItem(remoteResourceDialog
						.getCurrentResourceName());
				updateCurrentView();
			}
		}

	}

	/**
	 * deleteButton_actionPerformed, remove the selected resource after
	 * confirmation.
	 *
	 * @param e
	 *            ActionEvent
	 */
	private void deleteButton_actionPerformed(ActionEvent e) {
		String deleteResourceStr = (String) jComboBox1.getSelectedItem();
		if (deleteResourceStr != null) {
			int choice = JOptionPane.showConfirmDialog(null,
					"Do you really want to remove the profile: "
							+ deleteResourceStr + "?", "Warning",
					JOptionPane.OK_CANCEL_OPTION);
			if (choice == JOptionPane.OK_OPTION) {
				resourceModel.removeElement(deleteResourceStr);
				remoteResourceDialog.removeResourceByName(deleteResourceStr);
				repaint();
				updateExistedResourcesGUI();
			}
		}
	}

	/**
	 * Responds to the selection of an input file by the user
	 *
	 * @param e
	 */
	private void jFileChooser1_actionPerformed(ActionEvent e) {
		int i;
		if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
			// Get the format that the user designated for the selected file.
			FileFilter selectedFilter = jFileChooser1.getFileFilter();
			if (selectedFilter == null) {
				return; // Just to cover the case where no plugins are defined.
			}
			File[] files = jFileChooser1.getSelectedFiles();
			long totalFileLength = 0;
			for (int j = 0; j < files.length; j++) {
				totalFileLength += files[j].length();
			}

			System.gc();
			Runtime runtime = Runtime.getRuntime();
			long maxMemory = runtime.maxMemory();
			long allocatedMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();
			long actualUsedMemory = allocatedMemory - freeMemory;
			int empiricalFactor = 1;
			// From observation, the amount of Java Heap memory used when loading
			// some .exp files is about 4 times the lenght of the file.
			if(selectedFilter.getDescription().startsWith("Affymetrix File Matrix"))
				empiricalFactor = 4;
			// TODO see how much memory each data type uses vs its file size.
			long proposedMemUsed = actualUsedMemory + empiricalFactor*totalFileLength ;
			long proposedPercentUsed = (100*proposedMemUsed)/maxMemory;
			if (proposedPercentUsed > 90) {
				String warningMessage = "Loading this file";
				if (files.length > 1){
					warningMessage = "Loading these files";
				}
				int response = JOptionPane.showConfirmDialog(null,
						warningMessage + 
						" may attempt to use\n" + 
						proposedPercentUsed +
						"% of your total Java heap memory.\n" +
						"This may lead to an out of memory exception.\n\n" +
						"Would you like to continue?", 
						"Java total heap memory warning",
						JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				if (response != JOptionPane.YES_OPTION) {
					dispose();
					return;
				}
			}
			
			for (i = 0; i < supportedInputFormats.length; ++i) {
				if (selectedFilter == supportedInputFormats[i].getFileFilter()) {
					try {
						String format = i + "\n";
						String filepath = null;
						filepath = jFileChooser1.getCurrentDirectory()
								.getCanonicalPath();
						setLastDataInfo(filepath, format);

						if (supportedInputFormats[i] instanceof DataSetFileFormat) {
							FileOpenHandler handler = new FileOpenHandler(files,
									supportedInputFormats[i]);
							handler.openFiles();
						} else {
							log
									.error("unreachable branch: all FileFormat's are DataSetFileFormat");
						}
						
						dispose();
						return;
					} catch (org.geworkbench.parsers.InputFileFormatException iffe) {
						// Let the user know that there was a problem parsing
						// the file.
						JOptionPane
								.showMessageDialog(
										null,
										"The input file does not comply with the designated format.",
										"Parsing Error",
										JOptionPane.ERROR_MESSAGE);
					} catch (IOException ex) {
					}
				}
			}
		} else if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION) {
			dispose();
		}
	}

	private void updateCurrentView() {
		if (currentRemoteResourceName != null
				&& jComboBox1.getSelectedItem() != null
				&& currentRemoteResourceName != jComboBox1.getSelectedItem()
						.toString()) {

			currentRemoteResourceName = jComboBox1.getSelectedItem().toString();

		} else {
			if (jComboBox1.getSelectedItem() != null)
				currentRemoteResourceName = jComboBox1.getSelectedItem()
						.toString();
		}
		remoteResourceDialog
				.setupSystemPropertyForCurrentResource(currentRemoteResourceName);
		isSynchronized = !remoteResourceDialog.isSourceDirty()
				&& (currentDetailedResourceName
						.equalsIgnoreCase(currentRemoteResourceName));
		if (!isSynchronized) {
			openRemoteResourceButton.setBackground(Color.RED);
			openRemoteResourceButton
					.setToolTipText("Click to get the display synchronized with Remote source.");
			if(caArrayDisplayPanel!=null) {
				//Added by XQ to fix bug 1288
				caArrayDisplayPanel.initializeExperimentTree();
				caArrayDisplayPanel.setExperimentsLoaded(false);
			}
		} else {
			openRemoteResourceButton.setBackground(null);
			openRemoteResourceButton
					.setToolTipText("The top display is synchronized with the Remote source.");
		}

	}

	private void openRemoteResourceButton_actionPerformed(ActionEvent e) {
		String currentResourceName = resourceModel.getSelectedItem().toString()
				.trim();
		currentDetailedResourceName = currentResourceName;
		remoteResourceDialog
				.setupSystemPropertyForCurrentResource(currentResourceName);
		remoteResourceDialog.updateCurrentResourceStatus(currentResourceName,
				false);
		caArrayDisplayPanel.setUrl(remoteResourceDialog.getCurrentURL());
		caArrayDisplayPanel.setUser(remoteResourceDialog.getCurrentUser());
		caArrayDisplayPanel
				.setPasswd(remoteResourceDialog.getCurrentPassword());
		caArrayDisplayPanel.setPortnumber(remoteResourceDialog
				.getCurrentPortnumber());
		caArrayDisplayPanel.setParentPanel(this);
		caArrayDisplayPanel.setCurrentResourceName(currentResourceName);
		// XZ, 4/4. The user name/password can be null now for caArray2.0.1.

		if (!isSynchronized) {
			caArrayDisplayPanel.initializeExperimentTree();
			caArrayDisplayPanel.setExperimentsLoaded(false);
			caArrayDisplayPanel.getExperiments(e);
		} else {

			int choice = JOptionPane.showConfirmDialog(null, "You just connected to the server. Connect again?");
			if(JOptionPane.YES_OPTION==choice){
				caArrayDisplayPanel.initializeExperimentTree();
				caArrayDisplayPanel.setExperimentsLoaded(false);
				caArrayDisplayPanel.getExperiments(e);
			}
		}
		// Reset resource dirty to false after the connection to the server.
		remoteResourceDialog.setSourceDirty(false);
		updateCurrentView();

	}

	public void addRemotePanel() {
		updateCurrentView();
		this.getContentPane().remove(jPanel4);

		/* this method is never called with caArrayDisplayPanel==null */
		if(caArrayDisplayPanel==null) {
			log.error("caARRAYPanel was not available.");
			return;
		}
		caArrayDisplayPanel.setParent(this);

		this.getContentPane().add(caArrayDisplayPanel, BorderLayout.CENTER);
		this.validate();
		this.repaint();
	}

	private void addRemotePanel_actionPerformed(ActionEvent e) {
		addRemotePanel();
		lowerPanel.add(jPanel10);
		this.validate();
		this.repaint();
	}

	private void switchToLocalFileDialog() {
		if(caArrayDisplayPanel!=null)
			this.getContentPane().remove(caArrayDisplayPanel);
		this.getContentPane().add(jPanel4, BorderLayout.CENTER);
		lowerPanel.remove(jPanel10);	 
		this.validate();
		this.repaint();
	}

	/**
	 * Returns the experiment information for the most recently selected remote
	 * file.
	 *
	 * @return
	 */
	public String getExperimentInformation() {
		String expInfo = experimentInfoArea.getText();
		return (expInfo == null ? "" : expInfo);
	}

	static public String getLastDataDirectory() {
		String dir = FilePathnameUtils.getDataFilesDirPath();
		// This is where we store user data information
		String filename = FilePathnameUtils.getUserSettingsFilePath();
		try {
			File file = new File(filename);
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				br.readLine(); // skip the format information
				dir = br.readLine();
				br.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return dir;
	}

	static public String getLastDataFormat() {
		String format = "";
		// This is where we store user data information
		String filename = FilePathnameUtils.getUserSettingsFilePath();
		try {
			File file = new File(filename);
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				format = br.readLine();
				br.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return format;
	}

	static public void setLastDataInfo(String dir, String format) {
		try { // save current settings.
			BufferedWriter br = new BufferedWriter(new FileWriter(
					FilePathnameUtils.getUserSettingsFilePath()));
			br.write(format);
			br.write(dir);
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void processCaAraryQueryResult(boolean succeeded, String message, TreeMap<String, Set<String>> treeMap) {
		if(caarrayFilterQueryDialog==null) { // should never happen
			log.error("null caarrayFilterQueryDialog");
			return;
		}
		caarrayFilterQueryDialog.processCaAraryQueryResult(succeeded, message, treeMap);
	}
	
}
