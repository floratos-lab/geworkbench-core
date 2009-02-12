package org.geworkbench.analysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.model.analysis.AlgorithmExecutionResults;
import org.ginkgo.labs.reader.XmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

/**
 * A test analysis to test saving and loading parameters.
 * 
 * @author keshav
 * @author ch2514
 * @version $Id: TestAnalysis.java,v 1.2 2009-02-12 22:28:15 keshav Exp $
 * 
 * 
 */
class TestAnalysis extends AbstractAnalysis {

	private static final long serialVersionUID = 1L;

	private static final String TEST_PARAMS_XML = "temp/gui/foo.xml";

	private static final String[] ANALYSIS_LIST = { "Test Analysis" };

	private static final String ANALYSIS_TITLE = "Test Analysis Parameters";

	private static final String BUTTONS_TITLE = "Analysis Action";

	private static final String PARAM_1 = "String Parameter  ";

	private static final String PARAM_2 = "Integer Parameter";

	private static final String PARAM_3 = "Double Parameter ";

	private static final String ID_TAG = "id";

	private Log log = LogFactory.getLog(this.getClass());

	private JTabbedPane analysisTab, paramsTab;

	private JSplitPane mainPanel;

	private JPanel analysisPanel, paramsPanel, enterParamsPanel, buttonsPanel;

	private JScrollPane analysisScrollPane, paramsScrollPane;

	private JList analysisList, paramsList;

	private JButton analyze, save, load, delete;

	private JTextField param1, param2, param3;

	private DefaultListModel savedParamsData;

	public TestAnalysis() {
		try {
			log.info("Created TestAnalysis: current directory is [ "
					+ new File(".").getCanonicalPath() + " ]");
		} catch (Exception e) {
			log.error("Cannot get current directory.", e);
		}
	}

	public int getAnalysisType() {
		return -1;
	}

	@SuppressWarnings("unchecked")
	public AlgorithmExecutionResults execute(Object input) {
		log.info("Ran analysis.");
		return null;
	}

	public void createGUI() {
		log.info("Creating gui for TestAnalysis.");

		try {
			File f = new File(TEST_PARAMS_XML).getParentFile();
			if (!f.exists() && !f.mkdirs()) {
				JOptionPane.showMessageDialog(null,
						"Please create the following directory to store the xml file: "
								+ f.getCanonicalPath(), "Parameter Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			log
					.error("Cannot create temp directory for xml: "
							+ e.getMessage());
			JOptionPane
					.showMessageDialog(
							null,
							"Please create the following subdirectory to store the xml file: ./temp/gui",
							"Parameter Error", JOptionPane.ERROR_MESSAGE);
		}

		PlasticLookAndFeel.setMyCurrentTheme(new SkyBlue());
		try {
			UIManager
					.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Exception e) {
			log.error("Problems setting look and feel." + e.getMessage());
		}

		analysisList = new JList(ANALYSIS_LIST);
		analysisList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		analysisList.setSelectedIndex(0);
		analysisScrollPane = new JScrollPane(analysisList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		initSavedParameters();
		paramsList = new JList(savedParamsData);
		paramsList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		paramsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					loadParams();
				}
			}
		});
		paramsScrollPane = new JScrollPane(paramsList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		analysisPanel = new JPanel(new GridLayout(1, 2));
		analysisPanel.add(analysisScrollPane);
		analysisPanel.add(paramsScrollPane);

		param1 = new JTextField(15);
		param2 = new JTextField(15);
		param3 = new JTextField(15);

		JPanel p1 = new JPanel();
		p1.add(new JLabel(PARAM_1));
		p1.add(param1);
		JPanel p2 = new JPanel();
		p2.add(new JLabel(PARAM_2));
		p2.add(param2);
		JPanel p3 = new JPanel();
		p3.add(new JLabel(PARAM_3));
		p3.add(param3);

		enterParamsPanel = new JPanel(new GridLayout(4, 1));
		JLabel analysisLabel = new JLabel(ANALYSIS_TITLE);
		analysisLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		analysisLabel.setForeground(Color.DARK_GRAY);
		enterParamsPanel.add(analysisLabel);
		enterParamsPanel.add(p1);
		enterParamsPanel.add(p2);
		enterParamsPanel.add(p3);

		analyze = new JButton("   Analyze   ");
		analyze.setEnabled(false);
		save = new JButton("   Save Settings   ");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Clicked Save...");
				String paramName = JOptionPane.showInputDialog(mainPanel,
						"New Parameter Setting Name",
						"New Parameter Setting Name");
				// Validation already exists in Analysis.java and
				// AbstractAnalysis.java
				// It is therefore not tested here.
				Map<Serializable, Serializable> params = TestAnalysis.this
						.getParameters();
				log.info("Set the following parameters:\n" + params.toString());
				TestAnalysis.this.saveParameters(TestAnalysis.TEST_PARAMS_XML);
				if (!findInParameterList(paramName)) {
					savedParamsData.addElement(paramName);
					paramsList.setSelectedValue(paramName, true);
				}
				log
						.info("Saved parameters in file [ "
								+ TestAnalysis.TEST_PARAMS_XML + " ]");
			}
		});
		load = new JButton("   Load Settings   ");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Clicked Load...");
				loadParams();
			}
		});
		delete = new JButton("   Delete Settings   ");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Clicked Delete...");
				String s = null;
				int selectedIndex = paramsList.getSelectedIndex();
				Object o = paramsList.getSelectedValue();
				if (o != null)
					s = o.toString();
				if (s != null) {
					int choice = JOptionPane
							.showConfirmDialog(
									null,
									"Are you sure you want to delete saved parameters?",
									"Deleting Saved Parameters",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE);
					if (choice == 0) {
						TestAnalysis.this.deleteParameters(TEST_PARAMS_XML);
						savedParamsData.remove(selectedIndex);
						param1.setText("");
						param2.setText("");
						param3.setText("");
						log.info("Deleted parameter setting [ " + s
								+ " ] in file [ "
								+ TestAnalysis.TEST_PARAMS_XML + " ]");
					}
				} else {
					log.error("No parameter setting selected.");
					JOptionPane.showMessageDialog(mainPanel,
							"No parameter setting selected.",
							"Parameter Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		buttonsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
		JLabel buttonsLabel = new JLabel(BUTTONS_TITLE);
		buttonsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		buttonsLabel.setForeground(Color.DARK_GRAY);
		buttonsPanel.add(buttonsLabel);
		buttonsPanel.add(analyze);
		buttonsPanel.add(save);
		buttonsPanel.add(load);
		buttonsPanel.add(delete);

		paramsPanel = new JPanel(new BorderLayout());
		paramsPanel.add(enterParamsPanel, BorderLayout.WEST);
		paramsPanel.add(buttonsPanel, BorderLayout.EAST);

		analysisTab = new JTabbedPane();
		analysisTab.add(analysisPanel);
		analysisTab.setTitleAt(0, "Analysis");

		paramsTab = new JTabbedPane();
		paramsTab.add(paramsPanel);
		paramsTab.setTitleAt(0, "Parameters");

		mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, analysisTab,
				paramsTab);
		mainPanel.setOneTouchExpandable(false);
		mainPanel.setDividerLocation(100);
		mainPanel.setPreferredSize(new Dimension(700, 300));

		JDialog dialog = new JDialog();
		dialog.add(mainPanel);
		dialog.setTitle("Test Parameters API");
		dialog.setResizable(false);
		dialog.pack();
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void loadParams() {
		String s = null;
		Object o = paramsList.getSelectedValue();
		if (o != null)
			s = o.toString();
		if (s != null) {
			Map<Serializable, Serializable> params = TestAnalysis.this
					.getParameters();
			if (!params.isEmpty()) {
				log.info("parameters:\n" + params.toString());
				//TODO: implement it, probably can copy from AbstractAnalysisTest.java
			} else {
				log.error("Parameter Setting [ " + s + " ] does not exist.");
				JOptionPane.showMessageDialog(mainPanel, "Parameter Setting [ "
						+ s + " ] does not exist.", "Parameter Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			log.error("No parameter setting selected.");
			JOptionPane.showMessageDialog(mainPanel,
					"No parameter setting selected.", "Parameter Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initSavedParameters() {
		if (savedParamsData == null) {
			savedParamsData = new DefaultListModel();
		} else {
			savedParamsData.clear();
		}
		File f = new File(TEST_PARAMS_XML);
		if (f.exists()) {
			Document doc = XmlReader.readXmlFile(TEST_PARAMS_XML);
			NodeList nl = doc.getElementsByTagName(ID_TAG);
			for (int i = 0; i < nl.getLength(); i++) {
				Node idNode = nl.item(i).getFirstChild();
				savedParamsData.addElement(idNode.getNodeValue());
			}
		}
	}

	private boolean findInParameterList(String name) {
		for (int i = 0; i < savedParamsData.getSize(); i++) {
			Object o = savedParamsData.elementAt(i);
			if (o != null) {
				String s = (String) o;
				if (StringUtils.equals(s.trim(), name.trim())) {
					return true;
				}
			}
		}
		return false;
	}

}