package org.geworkbench.engine.ccm;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CCMTableModel
 * 
 */
class CCMTableModel2 extends CCMTableModel {
	private static final long serialVersionUID = 8538132847034690969L;

	private static Log log = LogFactory.getLog(CCMTableModel2.class);

	// column index changed over CCMTableModel
	static final int AVAILABLE_UPDATE_INDEX = 4;
	static final int TUTORIAL_URL_INDEX_2 = TUTORIAL_URL_INDEX+1;
	static final int TOOL_URL_INDEX_2 = TOOL_URL_INDEX+1;

	// column names changed over CCMTableModel
	private String[] columnNames = { "On/Off", "Name", "Author", "Current Version","New Version",
			"Tutorial", "Tool URL" };

	// these two are different from CCMTableModel to support the setting method
	private String[] resourceFolders = null;
	private File[] files = null;
	
	/*
	 * Constructor
	 */
	public CCMTableModel2(ComponentConfigurationManager manager) {
		super(manager);
		
		availableUpdate = new String[getRowCount()];
		resourceFolders = new String[getRowCount()];
		files = new File[getRowCount()];
		for(int i=0; i<super.getRowCount(); i++) {
			resourceFolders[i] = super.getResourceFolder(i);
			files[i] = super.getFile(i);
			availableUpdate[i] = "";
		}
		
		log.debug("CCMTableModel2 constrcuted");
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
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	static class DownloadLink {
		public DownloadLink(String text, String url) {
			this.text = text;
			this.url = url;
		}
		String text;
		String url;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		if (column == SELECTION_INDEX) {
			return Boolean.class;
		} else if (column == AUTHOR_INDEX) {
			return HyperLink.class;
		} else if (column == AVAILABLE_UPDATE_INDEX) {
			return DownloadLink.class;
		} else if (column == TUTORIAL_URL_INDEX_2 || column == TOOL_URL_INDEX_2) {
			return ImageLink.class;
		} else {
		return String.class;
		}
	}

	public Object getValueAt(int row, int column) {
		if(column==AVAILABLE_UPDATE_INDEX)
			return new DownloadLink(availableUpdate[row], availableUpdate[row]);
		else if(column==TUTORIAL_URL_INDEX_2)
			return getModelValueAt(row, TUTORIAL_URL_INDEX);
		else if(column==TOOL_URL_INDEX_2)
			return getModelValueAt(row, TOOL_URL_INDEX);
		else
			return getModelValueAt(row, column);
	}



	void setResourceFolder(int rowNumber, String folder ) {
		resourceFolders[rowNumber] = folder;
	}

	void setFile(int rowNumber, File file) {
		files[rowNumber] = file;
	}

	private String[] availableUpdate = null;
	final public void setAvailableUpdateAt(String value, int modelRow) {
		availableUpdate[modelRow] = value;
		fireTableCellUpdated(modelRow, AVAILABLE_UPDATE_INDEX);
	}

	final public void setVersionAt(String value, int modelRow) {
		this.setValueAt(value, modelRow, VERSION_INDEX);
		fireTableCellUpdated(modelRow, VERSION_INDEX);
	}

	public void setModelValueAt(Object value, int row,
			int column, boolean noValidation) {
		this.setValueAt(value, row, column);
	}
}