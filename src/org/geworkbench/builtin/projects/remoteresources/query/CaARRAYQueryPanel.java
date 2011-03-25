package org.geworkbench.builtin.projects.remoteresources.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.geworkbench.builtin.projects.LoadDataDialog;
import org.geworkbench.builtin.projects.remoteresources.RemoteResource;
import org.geworkbench.builtin.projects.remoteresources.RemoteResourceDialog;
import org.geworkbench.builtin.projects.util.CaARRAYPanel;
import org.geworkbench.engine.properties.PropertiesManager;
import org.geworkbench.events.CaArrayQueryEvent;
import org.geworkbench.events.CaArrayQueryResultEvent;
import org.geworkbench.events.CaArrayRequestEvent;

/**
 * @author xiaoqing
 * @version $Id$
 */
public class CaARRAYQueryPanel extends JDialog {
	private static final long serialVersionUID = -5214948658970068347L;

	public CaARRAYQueryPanel(Frame frame, String title) {
		super(frame, title, false);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void publishCaArrayQueryEvent(CaArrayQueryEvent event) {
		loadData.publishCaArrayQueryEvent(event);
	}

	public void receiveCaAraryQueryResultEvent(CaArrayQueryResultEvent event) {

		progressBar.setIndeterminate(false);

		if (!event.isSucceed()) {
			JOptionPane.showMessageDialog(this, event.getErrorMessage());
			return;
		}

		TreeMap<String, Set<String>> treeMap = event.getQueryPairs();
		if (treeMap == null) {
			JOptionPane.showMessageDialog(this,
					"No data can be retrieved from the caArray Server!");
			return;
		}

		for (String searchItem : listContent) {
			Set<String> set = treeMap.get(searchItem);
			String[] values = null;
			if (set != null) {
				values = new String[set.size()];
				values = set.toArray(values);
				searchButton.setEnabled(true);
			}
			if (searchItem.equalsIgnoreCase(ORGANISM)) {
				orginsmBox = new JComboBox(values);
			} else if (searchItem.equals(CHIPPROVIDER)) {
				chipPlatformBox = new JComboBox(values);
			} else if (searchItem.equals(PINAME)) {
				piComboxBox = new JComboBox(values);
			} else if (searchItem.equals(TISSUETYPE)) {
				tissueTypeBox = new JComboBox(values);
			}
		}
		updateSelectionValues(currentSelectedContent);
		repaint();
	}

	private final static String CAARRAY_USERNAME = "username";
	private final static String PASSWORD = "password";
	private final static String SERVERURL = "serverlocation";
	private final static String SERVERPORT = "serverport";

	public void display(LoadDataDialog frameComp, String remoteSourceName) {
		RemoteResource resourceDialog = RemoteResourceDialog
				.getRemoteResourceManager().getSelectedResouceByName(
						remoteSourceName.trim());

		try {
			loadData = frameComp;
			if (resourceDialog != null) {
				username = resourceDialog.getUsername();
				password = resourceDialog.getPassword();
				portnumber = resourceDialog.getPortnumber();
				url = resourceDialog.getUri();
				PropertiesManager properties = PropertiesManager.getInstance();
				properties.setProperty(CaARRAYQueryPanel.class,
						CAARRAY_USERNAME,
						resourceDialog.getUsername());
				String encyrpted = "";
				try {
					encyrpted = RemoteResource.encrypt(password);
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
				properties.setProperty(CaARRAYQueryPanel.class,
						PASSWORD, encyrpted );
				properties.setProperty(CaARRAYQueryPanel.class,
						SERVERURL, resourceDialog
								.getUri());
				properties.setProperty(CaARRAYQueryPanel.class,
						SERVERPORT, new Integer(
								resourceDialog.getPortnumber()).toString());

			}
			this.repaint();
			this.setVisible(true);
		} catch (IOException e) {

		}
	}

	private void jbInit() throws Exception {
		searchButton.setToolTipText("Click here to run the search");
		searchButton.setText("Search");
		searchButton
				.addActionListener(new CaARRAYQueryPanel_searchButton_actionAdapter(
						this));
		cancelButton.setToolTipText("Cancel the action.");
		cancelButton.setText("Cancel");
		cancelButton
				.addActionListener(new CaARRAYQueryPanel_cancelButton_actionAdapter(
						this));
		refreshButton.setToolTipText("Clear Selections");
		refreshButton.setText("Refresh");
		refreshButton
				.addActionListener(new CaARRAYQueryPanel_refreshButton_actionAdapter(
						this));

		this.setLayout(borderLayout1);
		jScrollPane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setBorder(border4);
		jScrollPane1.setDoubleBuffered(true);
		jScrollPane1.setPreferredSize(new Dimension(159, 200));
		jSplitPane2.setDividerSize(1);
		jPanel2.setBorder(border6);
		jPanel2.setMinimumSize(new Dimension(70, 33));
		jPanel2.setPreferredSize(new Dimension(250, 33));
		jSplitPane1.setDividerSize(1);
		jList.setMaximumSize(new Dimension(800, 900));
		jList.setPreferredSize(new Dimension(149, 51));

		jcatagoryComboBox
				.addActionListener(new CaARRAYQueryPanel_jcatagoryComboBox_actionAdapter());
		jPanel1.setBorder(border2);
		jPanel1.setLayout(new BorderLayout());
		// chipPlatformNameField.setText(ChipFieldDefaultMessage);
		// piTextField.setText(PIFieldDefaultMessage);
		jSplitPane2.add(jScrollPane1, JSplitPane.LEFT);
		jSplitPane2.add(jPanel2, JSplitPane.RIGHT);
		jScrollPane1.add(jList);

		for (String aListContent : listContent) {
			jComboBox1.addItem(aListContent);
		}

		// jToolBar1.add(clearAllButton);
		// jToolBar1.add(deleteButton);
		jToolBar1.add(progressBar);
		jToolBar1.add(Box.createHorizontalStrut(60));
		jToolBar1.add(searchButton);
		jToolBar1.add(refreshButton);
		jToolBar1.add(cancelButton);
		this.add(jToolBar1, java.awt.BorderLayout.SOUTH);
		jSplitPane1.add(jSplitPane2, JSplitPane.RIGHT);
		jSplitPane1.add(jPanel1, JSplitPane.LEFT);
		jPanel1.add(jcatagoryComboBox, BorderLayout.NORTH);
		JPanel allCheckBoxPanel = new JPanel();
		// allCheckBoxPanel.add(allButton, BorderLayout.CENTER);
		jPanel1.add(allCheckBoxPanel);// BorderLayout interprets the absence
		// of a string specification the same as
		// the constant CENTER:
		this.getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);
		pack();
	}

	public static final String PINAME = "Principal Investigator";
	public static final String ORGANISM = "Organism";
	public static final String TISSUETYPE = "Tissue Type";
	public static final String CHIPPLATFORM = "Chip Platform";
	public static final String CHIPPROVIDER = "Array Provider";
	public static final String CLEARALL = "Clear All";

	public static final boolean INISTATE = false; // The initial state for a
	// value.
	public static final String ChipFieldDefaultMessage = "Please enter chip type information here.";
	public static final String PIFieldDefaultMessage = "Please enter PI information here.";
	private String username;
	private String password;
	private int portnumber;
	private String url;

	public static String[] listContent = new String[] { CHIPPROVIDER, ORGANISM,
			PINAME };// , TISSUETYPE};// Remove TissueType because it takes
	// too long to get any result back., TISSUETYPE }; //
	// The
	// content
	// of
	// search
	// criteria.
	String currentSelectedContent = null;
	int currentSelectedContentIndex = -1;
	JList jList = new JList(listContent);
	JSplitPane jSplitPane1 = new JSplitPane();
	JPanel jPanel1 = new JPanel();
	JSplitPane jSplitPane2 = new JSplitPane();
	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();
	JPanel jPanel2 = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JComboBox jComboBox1 = new JComboBox();
	JToolBar jToolBar1 = new JToolBar();

	JButton searchButton = new JButton();
	JButton cancelButton = new JButton();
	JButton refreshButton = new JButton();
	JCheckBox jCheckBox1 = new JCheckBox();
	BorderLayout borderLayout1 = new BorderLayout();
	JComboBox jcatagoryComboBox = new JComboBox(new String[] {
			"Please select one category", EXPERIMENT });
	static final String EXPERIMENT = "Experiments";
	TitledBorder titledBorder1 = new TitledBorder("");
	Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(
			165, 163, 151));
	Border border2 = new TitledBorder(border1, "Category");
	Border border3 = BorderFactory.createEtchedBorder(Color.white, new Color(
			165, 163, 151));
	Border border4 = new TitledBorder(border3, "Field Selection");
	TitledBorder titledBorder2 = new TitledBorder("");
	Border border5 = BorderFactory.createEtchedBorder(Color.white, new Color(
			165, 163, 151));
	Border border6 = new TitledBorder(border5, "Value");

	LoadDataDialog loadData;
	boolean loaded = false; // To present whether the values are retrieved

	JComboBox chipPlatformBox = new JComboBox();
	JComboBox piComboxBox = new JComboBox();
	JComboBox orginsmBox = new JComboBox();
	JComboBox tissueTypeBox = new JComboBox();
	
	public void jcatagoryComboBox_actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();

		String selectedProgramName = (String) cb.getSelectedItem();

		if (selectedProgramName.equalsIgnoreCase(EXPERIMENT)) {
			jList = new JList(listContent);
			jList.addMouseListener(new CaARRAYQueryPanel_jList_mouseAdapter(
					this));
			(jScrollPane1.getViewport()).add(jList, null);

		} else {
			jList = new JList();
			(jScrollPane1.getViewport()).add(jList, null);
			clearAllButton_actionPerformed(null);
		}

	}

	/**
	 * Method to connect with caArray server to get the predefined values. It
	 * should only be called once per session and it would get all required
	 * information back not just the selected content.
	 */
	public void populateHits() {
		if (!loaded) {

			Runnable thread = new Runnable() {
				public void run() {
					CaArrayQueryEvent event = new CaArrayQueryEvent(url,
							portnumber, username, password,
							CaArrayQueryEvent.GOTVALIDVALUES);
					event.setQueries(listContent);
					publishCaArrayQueryEvent(event);

					loaded = true;
				}
			};
			Thread t = new Thread(thread);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();

			searchButton.setEnabled(false);
			progressBar.setIndeterminate(true);
			progressBar.setString("Waiting for the response from serve...");
			repaint();
		}
	}

	public void jList_mouseClicked(MouseEvent e) {
		int index = jList.locationToIndex(e.getPoint());

		if (index >= 0 && index < listContent.length) {
			currentSelectedContent = listContent[index];
			currentSelectedContentIndex = index;
			if (loaded) {
				updateSelectionValues(currentSelectedContent);
				repaint();
			}
		}
		populateHits();
	}

	/**
	 * Respond to the change of selected search criteria content.
	 *
	 * @param selectedCritiria
	 */
	public void updateSelectionValues(String selectedCritiria) {
		jPanel2.removeAll();
		System.out.println(selectedCritiria);
		if (selectedCritiria.equalsIgnoreCase(CHIPPROVIDER)) {
			// jPanel2.setLayout(new FlowLayout());
			jPanel2.setLayout(new GridLayout(6, 1));
			chipPlatformBox.setMinimumSize(new Dimension(200, 50));
			jPanel2.add(chipPlatformBox);
			jPanel2.revalidate();
			repaint();
			return;
		}
		if (selectedCritiria.equalsIgnoreCase(PINAME)) {
			jPanel2.setLayout(new GridLayout(6, 1));
			piComboxBox.setMinimumSize(new Dimension(200, 50));
			jPanel2.add(piComboxBox);
			jPanel2.revalidate();
			repaint();
			return;
		}
		if (selectedCritiria.equalsIgnoreCase(ORGANISM)) {
			jPanel2.setLayout(new GridLayout(6, 1));
			orginsmBox.setMinimumSize(new Dimension(200, 50));
			jPanel2.add(orginsmBox);
			jPanel2.revalidate();
			repaint();
			return;
		}
		if (selectedCritiria.equalsIgnoreCase(TISSUETYPE)) {
			jPanel2.setLayout(new GridLayout(6, 1));
			tissueTypeBox.setMinimumSize(new Dimension(200, 50));
			jPanel2.add(tissueTypeBox);
			jPanel2.revalidate();
			repaint();
			return;
		}

		repaint();
		return;
	}

	/**
	 * Clear all contents. To reinstore the content, connect with server is
	 * required.
	 *
	 * @param e
	 */
	public void clearAllButton_actionPerformed(ActionEvent e) {
		loaded = false;
		jPanel2.removeAll();
		jPanel2.revalidate();
		repaint();
	}

	/**
	 * Start the search based on the selection of search criteria.
	 *
	 * @param e
	 */
	public void searchButton_actionPerformed(ActionEvent e) {
		// get parameters.
		Map<String, String> filterCrit = new HashMap<String, String>();
		String piName = ((String) piComboxBox.getSelectedItem()).trim();
		if (piName != null && piName.startsWith(PIFieldDefaultMessage)) {
			piName = "";
		}
		String chipPlatformName = ((String) (chipPlatformBox.getSelectedItem()))
				.trim();
		if (chipPlatformName != null
				&& chipPlatformName.startsWith(ChipFieldDefaultMessage)) {
			chipPlatformName = "";
		}

		String organismName = ((String) (orginsmBox.getSelectedItem())).trim();
		if (organismName != null
				&& chipPlatformName.startsWith(ChipFieldDefaultMessage)) {
			organismName = "";
		}
		String tissueTypeName = null;
		if (tissueTypeBox != null) {
			tissueTypeName = ((String) (tissueTypeBox.getSelectedItem()));
		}

		this.setVisible(false);

		try {
			switch (currentSelectedContentIndex) {
			case 0:
				filterCrit.put(listContent[0], chipPlatformName);

				break;
			case 1:
				filterCrit.put(listContent[1], organismName);

				break;
			case 2:
				filterCrit.put(listContent[2], piName);
				break;

			case 3:
				filterCrit.put(listContent[3], tissueTypeName);
				break;
			}

			final CaArrayRequestEvent event = new CaArrayRequestEvent(url,
					portnumber);
			event.setQueryExperiment(true);
			event.setRequestItem(CaArrayRequestEvent.EXPERIMENT);
			event.setFilterCrit(filterCrit);
			event.setUseFilterCrit(true);
			if (username != null && username.trim().length() > 0) {
				event.setUsername(username);
				event.setPassword(password);
			} else {
				event.setUsername(null);
			}
			CaARRAYPanel caArrayPanel = loadData.getCaArrayDisplayPanel();
			caArrayPanel.setUser(username);
			caArrayPanel.setUrl(url);
			caArrayPanel.setPortnumber(portnumber);
			caArrayPanel.setPasswd(password);
			caArrayPanel.startProgressBar();

			Runnable thread = new Runnable() {
				public void run() {
					loadData.publishCaArrayRequestEvent(event);
				}
			};
			Thread t = new Thread(thread);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		} catch (Exception er) {
			er.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot process the query.");
		}

	}

	public void cancelButton_actionPerformed(ActionEvent e) {
		progressBar.setIndeterminate(false);
		dispose();
	}

	public void refreshButton_actionPerformed(ActionEvent e) {
		jcatagoryComboBox.setSelectedIndex(0);
		updateSelectionValues(CLEARALL);
	}

	private class CaARRAYQueryPanel_cancelButton_actionAdapter implements
			ActionListener {
		private CaARRAYQueryPanel adaptee;

		CaARRAYQueryPanel_cancelButton_actionAdapter(CaARRAYQueryPanel adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.cancelButton_actionPerformed(e);
		}
	}

	private class CaARRAYQueryPanel_refreshButton_actionAdapter implements
			ActionListener {
		private CaARRAYQueryPanel adaptee;

		CaARRAYQueryPanel_refreshButton_actionAdapter(CaARRAYQueryPanel adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.refreshButton_actionPerformed(e);
		}
	}

	private class CaARRAYQueryPanel_searchButton_actionAdapter implements
			ActionListener {
		private CaARRAYQueryPanel adaptee;

		CaARRAYQueryPanel_searchButton_actionAdapter(CaARRAYQueryPanel adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.searchButton_actionPerformed(e);
		}
	}

	private class CaARRAYQueryPanel_jcatagoryComboBox_actionAdapter implements
			ActionListener {

		public void actionPerformed(ActionEvent e) {
			CaARRAYQueryPanel.this.jcatagoryComboBox_actionPerformed(e);
		}
	}

	private class CaARRAYQueryPanel_jList_mouseAdapter extends MouseAdapter {
		private CaARRAYQueryPanel adaptee;

		CaARRAYQueryPanel_jList_mouseAdapter(CaARRAYQueryPanel adaptee) {
			this.adaptee = adaptee;
		}

		public void mouseClicked(MouseEvent e) {
			adaptee.jList_mouseClicked(e);
		}
	}

}
