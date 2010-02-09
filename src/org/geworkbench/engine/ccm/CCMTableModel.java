package org.geworkbench.engine.ccm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.util.Util;

/**
 * CCMTableModel
 * 
 */
class CCMTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3986726808587129205L;
	private static Log log = LogFactory.getLog(CCMTableModel.class);

	public static final boolean NO_VALIDATION = false;

	// TODO all these indices should be private. it takes more refactoring to do
	// it though.
	static final int SELECTION_INDEX = 0;
	static final int NAME_INDEX = 1;
	static final int AUTHOR_INDEX = 2;
	static final int VERSION_INDEX = 3;
	static final int TUTORIAL_URL_INDEX = 4;
	static final int TOOL_URL_INDEX = 5;

	// FIXME the following indices are really identifiers instead of index
	static final int CLASS_INDEX = 7;
	static final int DESCRIPTION_INDEX = 8;
	static final int LICENSE_INDEX = 11;
	static final int DOCUMENTATION_INDEX = 13;
	static final int ANALYSIS_INDEX = 17;
	static final int VISUALIZER_INDEX = 18;

	static final int HIDDEN_INDEX = 20;
	static final int LAST_VISIBLE_COLUMN = TOOL_URL_INDEX;

	static final int FIRST_STRING_COLUMN = NAME_INDEX;

	private String[] columnNames = { "On/Off", "Name", "Author", "Version",
			"Tutorial", "Tool URL" };
	Vector<PluginComponent> rows = new Vector<PluginComponent>();
	Map<PluginComponent, Boolean> selected = new HashMap<PluginComponent, Boolean>();
	
	private Map<PluginComponent, String> resourceFolders = new HashMap<PluginComponent, String>();
	private Map<PluginComponent, File> files = new HashMap<PluginComponent, File>();
	
	private ComponentConfigurationManager manager = null;

	private String loadedFilterValue = null;
	private String typeFilterValue = null;
	private String keywordFilterValue = null;

	/*
	 * Constructor
	 */
	public CCMTableModel(ComponentConfigurationManager manager) {
		super();

		this.manager = manager;
		for(File file: manager.cwbFile)
			loadGuiModelFromFiles(file);
		Collections.sort(rows, new TableNameComparator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return getModelRowCount();
	}

	/**
	 * 
	 * @return
	 */
	final public int getModelRowCount() {
		return rows.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int) Without
	 *      this method, the check box column would default to a String
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int column) {

		if (column == SELECTION_INDEX) {
			return Boolean.class;
		}

		if (column == AUTHOR_INDEX || column == TUTORIAL_URL_INDEX
				|| column == TOOL_URL_INDEX) {
			return JButton.class;
		}

		return String.class;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == SELECTION_INDEX) {
			return true;
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {
		return getModelValueAt(row, column);
	}

	/**
	 * return the plugin name for a given row
	 * 
	 * @param row
	 * @return
	 */
	String getPluginName(int row) {
		return (String) getModelValueAt(row, CCMTableModel.NAME_INDEX);
	}

	/**
	 * set selected status to opposite for a given row
	 * 
	 * @param row
	 * @return
	 */
	void switchSelected(int row) {
		Boolean selected = (Boolean) getModelValueAt(row,
				CCMTableModel.SELECTION_INDEX);
		Boolean reset = new Boolean(!selected.booleanValue());
		setModelValueAt(reset, row, CCMTableModel.SELECTION_INDEX,
				CCMTableModel.NO_VALIDATION);
	}

	/**
	 * un-select a given row without checking validation
	 * 
	 * @param row
	 * @return
	 */
	void unselectWithoutValiation(int row) {
		setModelValueAt(new Boolean(false), row, CCMTableModel.SELECTION_INDEX,
				CCMTableModel.NO_VALIDATION);
	}

	/**
	 * select a given row without checking validation
	 * 
	 * @param row
	 * @return
	 */
	boolean selectWithoutValiation(int row) {
		return setModelValueAt(new Boolean(true), row,
				CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	final public Object getModelValueAt(int row, int column) {
		PluginComponent record = rows.get(row);
		switch (column) {
		case SELECTION_INDEX:
			return (Boolean) selected.get(record);
		case NAME_INDEX:
			return (String) record.getName();
		case VERSION_INDEX:
			return (String) record.getVersion();
		case AUTHOR_INDEX:
			return createAuthorButton(record);
		case TUTORIAL_URL_INDEX:
			return createButtionField( record.getTutorialUrl() );
		case TOOL_URL_INDEX:
			return createButtionField( record.getToolUrl() );
		case CLASS_INDEX:
			return (String) record.getClazz();
		case DESCRIPTION_INDEX:
			return (String) record.getDescription();
		case LICENSE_INDEX:
			return (String) record.getLicense();
		case DOCUMENTATION_INDEX:
			return (String) record.getDocumentation();
		case ANALYSIS_INDEX:
			return (Boolean) record.isAnalysis();
		case VISUALIZER_INDEX:
			return (Boolean) record.isVisualizer();
		case HIDDEN_INDEX:
			return (Boolean) record.isHidden();

		default:
			return null;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowByClazz(String)
	 */
	final public int getModelRowByClazz(String clazz) {

		for (int i = 0; i < rows.size(); i++) {
			PluginComponent record = rows.get(i);
			String rowClazz = record.getClazz();
			if (clazz.equalsIgnoreCase(rowClazz)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 *      int, int)
	 */
	public void setValueAt(Object value, int row, int column) {
		setModelValueAt(value, row, column, true);
	}

	String getResourceFolder(int rowNumber) {
		return resourceFolders.get(rows.get(rowNumber));
	}

	File getFile(int rowNumber) {
		return files.get(rows.get(rowNumber));
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 *      int, int, boolean)
	 */
	final public boolean setModelValueAt(Object value, int modelRow,
			int column, boolean validation) {
		PluginComponent record = rows.get(modelRow);
		switch (column) {
		case SELECTION_INDEX:
			selected.put( record, (Boolean)value );

			Boolean currentChoice = (Boolean) getModelValueAt(modelRow,
					CCMTableModel.SELECTION_INDEX);

			List<String> required = record.getRequired();
			List<String> related = record.getRelated();
			String folder = resourceFolders.get(record);
			File file = files.get(record);

			List<String> unselectedRequired = new ArrayList<String>();
			for (int i = 0; i < required.size(); i++) {
				String requiredClazz = required.get(i);
				Integer requiredRow = getModelRowByClazz(requiredClazz);
				Boolean requiredSelected = (Boolean) getModelValueAt(
						requiredRow.intValue(), CCMTableModel.SELECTION_INDEX);
				if (!requiredSelected) {
					unselectedRequired.add(requiredClazz);
				}
			}

			if (currentChoice.booleanValue()) {
				/* PLUGIN SELECTED */
				String propFileName = null;
				String cwbFileName = file.getName();
				if (cwbFileName.endsWith(".cwb.xml")) {
					propFileName = cwbFileName.replace(".cwb.xml",
							".ccmproperties");
				} else {
					log.error(".cwb.xml file is expected. Actual file name "+cwbFileName);
				}
				String licenseAccepted = ComponentConfigurationManager
						.readProperty(folder, propFileName, "licenseAccepted");
				Boolean boolRequired = record.isMustAccept();
				boolean bRequired = boolRequired.booleanValue();
				if ((bRequired && (licenseAccepted == null))
						|| (bRequired && licenseAccepted != null && !licenseAccepted
								.equalsIgnoreCase("true"))) {
					String componentName = (String) getModelValueAt(modelRow,
							CCMTableModel.NAME_INDEX);

					String title = "Terms Acceptance";
					String message = "\nBy clicking \"I Accept\", you signify that you \nhave read and accept the terms of the license \nagreement for the \""
							+ componentName
							+ "\" application.\n\nTo view this license, exit out of this dialog and click \non the View License button below.\n\n";
					Object[] options = { "I Accept", "No, thanks" };
					int choice = JOptionPane.showOptionDialog(null, message,
							title, JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options,
							options[0]);

					if (choice != 0) {
						ComponentConfigurationManager.writeProperty(folder,
								propFileName, "licenseAccepted", "false");
						selected.put( record, new Boolean(false) );
						return false;
					}
					ComponentConfigurationManager.writeProperty(folder,
							propFileName, "licenseAccepted", "true");
				}

				if (!validation) {
					fireTableDataChanged();
					return true;
				}

				if (unselectedRequired.size() > 0 || related.size() > 0) {
					DependencyManager dmanager = new DependencyManager(this,
							unselectedRequired, modelRow, related);
					dmanager.checkDependency();
				}
			} else {
				/* PLUGIN UNSELECTED */
				List<Integer> dependentPlugins = null;

				String unselectedPluginClazz = "Plugin is missing a Class descriptor";
				int unselectedRow = modelRow;
				if (unselectedRow >= 0) {
					unselectedPluginClazz = (String) getModelValueAt(
							unselectedRow, CCMTableModel.CLASS_INDEX);
				}

				dependentPlugins = new ArrayList<Integer>();
				/*
				 * Find Plugins that are dependent on this unselected plugin.
				 */
				int rowCount = getModelRowCount();
				for (int i = 0; i < rowCount; i++) {

					/*
					 * If the potentially dependent plugin is not selected,
					 * don't bother to see if it is dependent
					 */
					Boolean selected = (Boolean) getModelValueAt(i,
							CCMTableModel.SELECTION_INDEX);
					if (!selected.booleanValue()) {
						continue;
					}

					List<String> requiredList = rows.get(i).getRequired();

					for (int j = 0; j < requiredList.size(); j++) {
						String requiredClazz = requiredList.get(j);
						if (unselectedPluginClazz
								.equalsIgnoreCase(requiredClazz)) {
							dependentPlugins.add(new Integer(i));
							break;
						}
					}
				}

				if (!validation) {
					fireTableDataChanged();
					return true;
				}

				/* If dependencies are found, then popup a dialog */
				if (dependentPlugins.size() > 0) {
					DependencyManager dmanager = new DependencyManager(this,
							dependentPlugins, modelRow);
					dmanager.checkDependency();
				}
			}

			break;
		default:

		}
		fireTableCellUpdated(modelRow, column);

		return true;
	}

	private void loadGuiModelFromFiles(File file) {

			String comDir = UILauncher.getComponentsDirectory();
			String path = file.getAbsolutePath();
			int index = path.indexOf(comDir)+comDir.length()+1;
			String folderName = path.substring(index, path.indexOf(System.getProperty("file.separator"), index));

			PluginComponent ccmComponent = manager.getPluginsFromFile(file);

			if (ccmComponent == null) {
				return;
			}

			String name = ccmComponent.getPluginName();
			if (name == null) {
				return;
			}

			String propFileName = null;
			String cwbFileName = file.getName();
			if (cwbFileName.endsWith(".cwb.xml")) {
				propFileName = cwbFileName
						.replace(".cwb.xml", ".ccmproperties");
			} else {
				log.error(".cwb.xml file is expected. File name "+cwbFileName);
				return;
			}

			String onOff = ComponentConfigurationManager.readProperty(
					folderName, propFileName, "on-off");

			if (onOff != null && onOff.equalsIgnoreCase("true")) {
				selected.put(ccmComponent, new Boolean(true) );
			} else {
				selected.put(ccmComponent, new Boolean(false) );
			}

			rows.add(ccmComponent);
			resourceFolders.put(ccmComponent, folderName);
			files.put(ccmComponent, file);
	}

	final public String getLoadedFilterValue() {
		return loadedFilterValue;
	}

	final public void setLoadedFilterValue(String loadedFilterValue) {
		this.loadedFilterValue = loadedFilterValue;
	}

	final public String getTypeFilterValue() {
		return typeFilterValue;
	}

	final public void setTypeFilterValue(String typeFilterValue) {
		this.typeFilterValue = typeFilterValue;
	}

	final public String getKeywordFilterValue() {
		return keywordFilterValue;
	}

	final public void setKeywordFilterValue(String keywordFilterValue) {
		this.keywordFilterValue = keywordFilterValue;
	}
	
	public static JButton createAuthorButton(PluginComponent component) {
		String author = component.getAuthor();
		String authorUrl = component.getAuthorUrl();
		JButton button = new JButton(author);
		button.setBorderPainted(false);

		if (authorUrl != null) {
			button.setToolTipText(authorUrl);
			String html = "<html><font><u>" + author
					+ "</u><br></font>";
			button.setText(html);
		}

		return button;
	}
	
	private static JButton createButtionField(String strUrl) {
		ImageIcon image = null;
		if (strUrl == null || strUrl == "") {
			image = Util.createImageIcon(
					"/org/geworkbench/engine/visualPluginGrey.png",
					strUrl);
		} else {
			image = Util.createImageIcon(
					"/org/geworkbench/engine/visualPlugin.png",
					strUrl);
		}

		JButton button = new JButton(image);
		button.setToolTipText(strUrl);
		button.setBorderPainted(false);
		return button;
	}

	static private class TableNameComparator implements Comparator<PluginComponent> {
		public int compare(PluginComponent row1, PluginComponent row2) {
			String name1 = row1.getName().toLowerCase();
			String name2 = row2.getName().toLowerCase();
			return name1.compareTo(name2);
		}
	}

}