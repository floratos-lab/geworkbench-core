package org.geworkbench.parsers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.io.FileInputStream;
import java.util.Properties;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrix;
import org.geworkbench.bison.datastructure.biocollections.AdjacencyMatrixDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.builtin.projects.DataSetNode;
import org.geworkbench.builtin.projects.ProjectNode;
import org.geworkbench.builtin.projects.ProjectTreeNode; 
import org.geworkbench.engine.properties.PropertiesManager;
import org.geworkbench.util.ResultSetlUtil;
import org.geworkbench.util.Util;

/**
 * Handles parsing of ARACNe adjacency matrix .txt files. based on
 * AffyFileFormat
 * 
 * @author os2201
 * @version $Id$
 * 
 */
public class AdjacencyMatrixFileFormat extends DataSetFileFormat {
	private Log log = LogFactory.getLog(AdjacencyMatrixFileFormat.class);

	public static final String INTERACTIONS_SERVLET_URL = "interactions_servlet_url";
	public static final String PROPERTIES_FILE = "conf/application.properties";
	public static final String INTERACTIONS_SERVLET_CONNECTION_TIMEOUT = "interactions_servlet_connection_timeout";
	
	String[] adjMatrixExtensions = { "txt", "adj", "sif" };
	String[] representedByList;
	AdjacencyMatrixFileFilter adjMatrixFilter = null;

	private ProjectNode projectNode;
	private String selectedFormart;
	private String selectedRepresentedBy;
	private DataSetNode selectedDataSetNode;
	private boolean isRestrict = false;
	private boolean isCancel = false;
	private String fileName = null;

	public void setProjectNode(ProjectNode projectNode) {
		this.projectNode = projectNode;
	}

	public AdjacencyMatrixFileFormat() {
		formatName = "Networks"; // Setup the display name for the
		// format.
		adjMatrixFilter = new AdjacencyMatrixFileFilter();

	}

	@Override
	public DSDataSet<? extends DSBioObject> getDataFile(final File file)
			throws InputFileFormatException, InterruptedIOException {

		DSDataSet<?> ds = getMArraySet(file);
		return ds;
	}

	@Override
	public DSDataSet<? extends DSBioObject> getDataFile(File[] files)
			throws InputFileFormatException {
		return null;
	}

	@SuppressWarnings( { "unchecked" })
	public DSDataSet<? extends DSBioObject> getMArraySet(File file)
			throws InputFileFormatException, InterruptedIOException {

		// get list of data sets that a selected adjacency matrix could be
		// attached to
		this.fileName = file.getName();
		ArrayList<DataSetNode> dataSetstmp = new ArrayList<DataSetNode>();
		for (Enumeration<?> en = projectNode.children(); en.hasMoreElements();) {
			ProjectTreeNode node = (ProjectTreeNode) en.nextElement();
			if (node instanceof DataSetNode) {
				dataSetstmp.add((DataSetNode) node);
			}
		}

		if (dataSetstmp.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"No Microarray Set is available");
			return null;
		} else {
			DataSetNode[] dataSets = dataSetstmp.toArray(new DataSetNode[1]);
			JDialog loadDialog = new JDialog();

			loadDialog.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {
					isCancel = true;
				}
			});

			isCancel = false;
			loadDialog.setTitle("Load Interaction Network");
			LoadInteractionNetworkPanel loadPanel = new LoadInteractionNetworkPanel(
					loadDialog, dataSets);

			loadDialog.add(loadPanel);
			loadDialog.setModal(true);
			loadDialog.pack();
			Util.centerWindow(loadDialog);
			loadDialog.setVisible(true);

			if (isCancel)
				return null;

		}

		if ((selectedFormart
				.equalsIgnoreCase(AdjacencyMatrixDataSet.SIF_FORMART) && !fileName
				.toLowerCase().endsWith(".sif"))
				|| (fileName.toLowerCase().endsWith(".sif") && !selectedFormart
						.equalsIgnoreCase(AdjacencyMatrixDataSet.SIF_FORMART))) {
			String theMessage = "The network format selected may not match that of the file.  \nClick \"Cancel\" to terminate this process.";
			Object[] optionChoices = { "Continue", "Cancel"};
			int result = JOptionPane.showOptionDialog(
				(Component) null, theMessage, "Warning",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, optionChoices, optionChoices[1]);
			if (result == JOptionPane.NO_OPTION)
				return null;
	

		}

		AdjacencyMatrixDataSet adjMatrixDS = null;
		if ((selectedDataSetNode != null)) {
			DSDataSet<DSMicroarray> ds = selectedDataSetNode.dataFile;
			if (!(ds instanceof CSMicroarraySet)) {
				JOptionPane.showMessageDialog(null,
						"Not a Microarray Set selected", "Unable to Load",
						JOptionPane.ERROR_MESSAGE);
			} else {
				CSMicroarraySet<DSMicroarray> mASet = (CSMicroarraySet<DSMicroarray>) ds;
				String adjMatrixFileStr = file.getPath();
				String fileName = file.getName();

				HashMap<String, String> interactionTypeMap = null;

				if (selectedFormart
						.equalsIgnoreCase(AdjacencyMatrixDataSet.SIF_FORMART)) {
					interactionTypeMap = getInteractionTypeMap();
				}
				AdjacencyMatrix matrix = AdjacencyMatrixDataSet
						.parseAdjacencyMatrix(adjMatrixFileStr, mASet,
								interactionTypeMap, selectedFormart,
								selectedRepresentedBy, isRestrict);

				adjMatrixDS = new AdjacencyMatrixDataSet(matrix, 0, fileName,
						"network loaded", mASet);

			}
		} else {
			JOptionPane.showMessageDialog(null, "No Microarray Set selected",
					"Unable to Load", JOptionPane.ERROR_MESSAGE);
		}

		return adjMatrixDS;
	}

	@Override
	public boolean checkFormat(File file) throws InterruptedIOException {
		return true;
	}

	@Override
	public Resource getResource(File file) {
		return null;
	}

	@Override
	public String[] getFileExtensions() {
		return adjMatrixExtensions;
	}

	public FileFilter getFileFilter() {
		return adjMatrixFilter;
	}

	public boolean isMergeSupported() {
		return false;
	}

	public HashMap<String, String> getInteractionTypeMap() {
		HashMap<String, String> map = new HashMap<String, String>();

		ResultSetlUtil rs = null;
		String interactionType = null;
		String short_name = null;

		try {

			if (ResultSetlUtil.getUrl() == null || !ResultSetlUtil.getUrl().trim().equals(""))
				ResultSetlUtil.setUrl(getURLProperty())	;
			String methodAndParams = "getInteractionTypes";
			rs = ResultSetlUtil.executeQuery(methodAndParams,
					ResultSetlUtil.getUrl());

			while (rs.next()) {

				interactionType = rs.getString("interaction_type").trim();
				short_name = rs.getString("short_name").trim();

				map.put(interactionType, short_name);
				map.put(short_name, interactionType);
			}
			rs.close();

		} catch (ConnectException ce) {
			if (log.isErrorEnabled()) {
				log.error(ce.getMessage());
			}

		} catch (SocketTimeoutException se) {
			if (log.isErrorEnabled()) {
				log.error(se.getMessage());
			}

		} catch (IOException ie) {
			if (log.isErrorEnabled()) {
				log.error(ie.getMessage());
			}

		} catch (Exception se) {
			if (log.isErrorEnabled()) {
				log
						.error("getInteractionTypes() - ResultSetlUtil: " + se.getMessage()); //$NON-NLS-1$
			}

		}
		return map;
	}
	
	public String getURLProperty() throws Exception{
		 
		String urlStr = PropertiesManager.getInstance().getProperty(this.getClass(),
					"url", "");
		Properties iteractionsProp = new Properties();
		iteractionsProp.load(new FileInputStream(PROPERTIES_FILE));		
		if (urlStr == null
					|| urlStr.trim().equals("")) {
		
			urlStr = iteractionsProp
						.getProperty(INTERACTIONS_SERVLET_URL);			
		}
		
		Integer timeout = new Integer(
				iteractionsProp
						.getProperty(INTERACTIONS_SERVLET_CONNECTION_TIMEOUT));
		ResultSetlUtil.setTimeout(timeout);
			 
		return urlStr;
	}

	
	
	

	class AdjacencyMatrixFileFilter extends FileFilter {
		public String getDescription() {
			return getFormatName();
		}

		public boolean accept(File f) {
			boolean returnVal = false;
			for (int i = 0; i < adjMatrixExtensions.length; ++i)
				if (f.isDirectory()
						|| f.getName().toLowerCase().endsWith(
								adjMatrixExtensions[i])) {
					return true;
				}
			return returnVal;
		}
	}

	private class LoadInteractionNetworkPanel extends JPanel {

		static final long serialVersionUID = -1855255412334333328L;

		final JDialog parent;

		private JComboBox formatJcb;
		private JComboBox presentJcb;

		private JComboBox microarraySetJcb;
		private DataSetNode[] dataSets;

		private JCheckBox restrictToPresent = new JCheckBox(
				"Restrict to genes present in microarray set", false);

		public LoadInteractionNetworkPanel(JDialog parent,
				DataSetNode[] dataSets) {

			setLayout(new BorderLayout());
			this.parent = parent;
			this.dataSets = dataSets;
			init();

		}

		private void init() {

			JPanel panel1 = new JPanel(new GridLayout(4, 3));
			JPanel panel2 = new JPanel(new GridLayout(2, 1));
			JPanel panel3 = new JPanel(new GridLayout(0, 3));
			JLabel label1 = new JLabel("File Format:    ");

			formatJcb = new JComboBox();
			formatJcb.addItem(AdjacencyMatrixDataSet.ADJ_FORMART);
			formatJcb.addItem(AdjacencyMatrixDataSet.SIF_FORMART);
			if (fileName.toLowerCase().endsWith(".sif"))
				formatJcb.setSelectedItem(AdjacencyMatrixDataSet.SIF_FORMART);
			else
				formatJcb.setSelectedItem(AdjacencyMatrixDataSet.ADJ_FORMART);

			JLabel label2 = new JLabel("Node Represented By:   ");

			representedByList = new String[4];
			representedByList[0] = AdjacencyMatrixDataSet.PROBESET_ID;
			representedByList[1] = AdjacencyMatrixDataSet.GENE_NAME;
			representedByList[2] = AdjacencyMatrixDataSet.ENTREZ_ID;
			representedByList[3] = AdjacencyMatrixDataSet.OTHER;
			presentJcb = new JComboBox(representedByList);

			JLabel label3 = new JLabel("Microarray Dataset:   ");

			microarraySetJcb = new JComboBox(dataSets);

			JButton continueButton = new JButton("Continue");
			JButton cancelButton = new JButton("Cancel");

			formatJcb.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					if (formatJcb.getSelectedItem().toString().equals(
							AdjacencyMatrixDataSet.ADJ_FORMART)) {
						representedByList = new String[4];
						representedByList[0] = AdjacencyMatrixDataSet.PROBESET_ID;
						representedByList[1] = AdjacencyMatrixDataSet.GENE_NAME;
						representedByList[2] = AdjacencyMatrixDataSet.ENTREZ_ID;
						representedByList[3] = AdjacencyMatrixDataSet.OTHER;
						presentJcb.setModel(new DefaultComboBoxModel(
								representedByList));
					} else {
						representedByList = new String[3];
						representedByList[0] = AdjacencyMatrixDataSet.GENE_NAME;
						representedByList[1] = AdjacencyMatrixDataSet.ENTREZ_ID;
						representedByList[2] = AdjacencyMatrixDataSet.OTHER;
						presentJcb.setModel(new DefaultComboBoxModel(
								representedByList));
					}
				}
			});
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					continueButtonActionPerformed();
					parent.dispose();
					isCancel = false;
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					parent.dispose();
					isCancel = true;
				}
			});

			panel1.add(label1);
			panel1.add(formatJcb);
			panel1.add(new JLabel("     "));

			panel1.add(label2);
			panel1.add(presentJcb);
			panel1.add(new JLabel("     "));

			panel1.add(label3);
			panel1.add(microarraySetJcb);
			panel1.add(new JLabel("     "));
			panel2.add(restrictToPresent);

			panel3.add(continueButton);
			panel3.add(new JLabel("  "));
			panel3.add(cancelButton);

			this.add(panel1, BorderLayout.NORTH);
			this.add(panel2, BorderLayout.CENTER);
			this.add(panel3, BorderLayout.SOUTH);

		}

		private void continueButtonActionPerformed() {

			selectedFormart = formatJcb.getSelectedItem().toString();

			selectedRepresentedBy = presentJcb.getSelectedItem().toString();
			selectedDataSetNode = (DataSetNode) microarraySetJcb
					.getSelectedItem();
			isRestrict = restrictToPresent.isSelected();

		}

	}

}
