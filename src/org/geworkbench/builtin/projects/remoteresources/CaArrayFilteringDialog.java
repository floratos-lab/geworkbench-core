package org.geworkbench.builtin.projects.remoteresources;

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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.builtin.projects.LoadDataDialog;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.util.CaARRAYPanel;
import org.geworkbench.engine.properties.PropertiesManager;
import org.geworkbench.events.CaArrayQueryEvent;
import org.geworkbench.events.CaArrayRequestEvent;

/**
 * @author xiaoqing
 * @version $Id$
 */
public final class CaArrayFilteringDialog extends JDialog {
	private static final long serialVersionUID = -5214948658970068347L;
	private Log log = LogFactory.getLog(CaArrayFilteringDialog.class);
	
	public CaArrayFilteringDialog(Frame frame, String title) {
		super(frame, title, false);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processCaAraryQueryResult(boolean succeeded, String message, TreeMap<String, Set<String>> treeMap) {

		progressBar.setIndeterminate(false);

		if (!succeeded) {
			JOptionPane.showMessageDialog(this, message);
			return;
		}

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

	public void display(final LoadDataDialog frameComp, String remoteSourceName) {
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
				properties.setProperty(CaArrayFilteringDialog.class,
						CAARRAY_USERNAME,
						resourceDialog.getUsername());
				String encyrpted = "";
				try {
					encyrpted = RemoteResource.encrypt(password);
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
				properties.setProperty(CaArrayFilteringDialog.class,
						PASSWORD, encyrpted );
				properties.setProperty(CaArrayFilteringDialog.class,
						SERVERURL, resourceDialog
								.getUri());
				properties.setProperty(CaArrayFilteringDialog.class,
						SERVERPORT, new Integer(
								resourceDialog.getPortnumber()).toString());

			}
			this.repaint();
			this.setVisible(true);
		} catch (IOException e) {
			log.error("CaArray Filtering Query Dialog I/O Exception: "+e);
		}
	}

	private void jbInit() throws Exception {
		searchButton.setToolTipText("Click here to run the search");
		searchButton.setText("Search");
		searchButton
				.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						searchButton_actionPerformed(e);
					}
				});
		cancelButton.setToolTipText("Cancel the action.");
		cancelButton.setText("Cancel");
		cancelButton
				.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						progressBar.setIndeterminate(false);
						dispose();
					}
				});
		refreshButton.setToolTipText("Clear Selections");
		refreshButton.setText("Refresh");
		refreshButton
				.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						jcatagoryComboBox.setSelectedIndex(0);
						updateSelectionValues(CLEARALL);
					}
				});

		this.setLayout(borderLayout1);
		jScrollPane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(
				165, 163, 151)), "Field Selection"));
		jScrollPane1.setDoubleBuffered(true);
		jScrollPane1.setPreferredSize(new Dimension(159, 200));
		jSplitPane2.setDividerSize(1);
		jPanel2.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(
				165, 163, 151)), "Value"));
		jPanel2.setMinimumSize(new Dimension(70, 33));
		jPanel2.setPreferredSize(new Dimension(250, 33));
		jSplitPane1.setDividerSize(1);
		jList.setMaximumSize(new Dimension(800, 900));
		jList.setPreferredSize(new Dimension(149, 51));

		jcatagoryComboBox
				.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						CaArrayFilteringDialog.this.jcatagoryComboBox_actionPerformed(e);
					}
				});
		jPanel1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(
				165, 163, 151)), "Category"));
		jPanel1.setLayout(new BorderLayout());

		jSplitPane2.add(jScrollPane1, JSplitPane.LEFT);
		jSplitPane2.add(jPanel2, JSplitPane.RIGHT);
		jScrollPane1.add(jList);

		for (String aListContent : listContent) {
			jComboBox1.addItem(aListContent);
		}

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
		jPanel1.add(allCheckBoxPanel);// BorderLayout interprets the absence
		// of a string specification the same as
		// the constant CENTER:
		this.getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);
		pack();
	}

	public static final String PINAME = "Principal Investigator";
	public static final String ORGANISM = "Organism";
	public static final String CHIPPROVIDER = "Array Provider";
	
	private static final String TISSUETYPE = "Tissue Type";
	private static final String CLEARALL = "Clear All";

	private static final String ChipFieldDefaultMessage = "Please enter chip type information here.";
	private static final String PIFieldDefaultMessage = "Please enter PI information here.";
	private String username;
	private String password;
	private int portnumber;
	private String url;

	// The content of search criteria.
	public static String[] listContent = new String[] { CHIPPROVIDER, ORGANISM,
			PINAME };// , TISSUETYPE};
	// Remove TissueType because it takes
	// too long to get any result back., TISSUETYPE }; //
	
	private String currentSelectedContent = null;
	private int currentSelectedContentIndex = -1;
	private JList jList = new JList(listContent);
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JPanel jPanel1 = new JPanel();
	private JSplitPane jSplitPane2 = new JSplitPane();
	private JScrollPane jScrollPane1 = new JScrollPane();

	private JPanel jPanel2 = new JPanel();
	private JProgressBar progressBar = new JProgressBar();
	private JComboBox jComboBox1 = new JComboBox();
	private JToolBar jToolBar1 = new JToolBar();

	private JButton searchButton = new JButton();
	private JButton cancelButton = new JButton();
	private JButton refreshButton = new JButton();

	private BorderLayout borderLayout1 = new BorderLayout();
	private JComboBox jcatagoryComboBox = new JComboBox(new String[] {
			"Please select one category", EXPERIMENT });
	private static final String EXPERIMENT = "Experiments";

	private LoadDataDialog loadData;
	private boolean loaded = false; // To present whether the values are retrieved

	private JComboBox chipPlatformBox = new JComboBox();
	private JComboBox piComboxBox = new JComboBox();
	private JComboBox orginsmBox = new JComboBox();
	private JComboBox tissueTypeBox = new JComboBox();
	
	private void jcatagoryComboBox_actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();

		String selectedProgramName = (String) cb.getSelectedItem();

		if (selectedProgramName.equalsIgnoreCase(EXPERIMENT)) {
			jList = new JList(listContent);
			jList.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					jList_mouseClicked(e);
				}
			});
			(jScrollPane1.getViewport()).add(jList, null);

		} else {
			jList = new JList();
			(jScrollPane1.getViewport()).add(jList, null);

			loaded = false;
			jPanel2.removeAll();
			jPanel2.revalidate();
			repaint();
		}

	}

	/**
	 * Method to connect with caArray server to get the predefined values. It
	 * should only be called once per session and it would get all required
	 * information back not just the selected content.
	 */
	private void populateHits() {
		if (!loaded) {

			Runnable thread = new Runnable() {
				public void run() {
					CaArrayQueryEvent event = new CaArrayQueryEvent(url,
							portnumber, username, password,
							CaArrayQueryEvent.GOTVALIDVALUES);
					event.setQueries(listContent);
					ProjectPanel.getInstance().publishCaArrayQueryEvent(event);

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

	private void jList_mouseClicked(MouseEvent e) {
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
	private void updateSelectionValues(String selectedCritiria) {
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
	 * Start the search based on the selection of search criteria.
	 *
	 * @param e
	 */
	private void searchButton_actionPerformed(ActionEvent e) {
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
					ProjectPanel.getInstance().publishCaArrayRequestEvent(event);
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
	
}
