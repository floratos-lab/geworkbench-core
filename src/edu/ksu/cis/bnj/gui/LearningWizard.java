/*
 * Created on 23 Jul 2003
 *
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.ksu.cis.bnj.gui;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.bnj.bbn.prm.PRMk2;
import edu.ksu.cis.bnj.gui.components.BNJFileDialogFactory;
import edu.ksu.cis.bnj.gui.components.BNJMainPanel;
import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.OptionGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Roby Joehanes, Charlie Thornton, Julie Thornton
 */
public class LearningWizard extends JFrame implements ActionListener, WindowListener, MouseListener {
    private GUI owner;

    // GUI variables
    private JPanel fileSelectionPanel = null, tableSelectionPanel = null, algorithmSelectionPanel = null;
    private JPanel mainPanel = null, resultPanel = null;
    private JButton prevButton = null, nextButton = null, cancelButton = null, browseDataButton = null, tableRemoveButton = null, tableAddButton = null, launchDBGUIButton = null;
    private JRadioButton loadFromFileRadioButton = null, useRemoteRadioButton = null, learningRadioButton[] = null;
    private JTextField loadFileText = null, outputFileText = null, dbURLText = null, dbLoginText = null;
    private JPasswordField dbPasswordText = null;
    private JComboBox outputComboBox = null, dbDriversComboBox = null;
    private JLabel dbDriversLabel = null, dbURLLabel = null, dbLoginLabel = null, dbPasswordLabel = null;
    protected JList availableTableList = null, tableToSaveList = null;
    protected DefaultListModel availableTableListModel = null, tableToSaveListModel = null;
    private JScrollPane spAvail = null, spToLearn = null;
    protected BNJMainPanel graphPanel = null;
    protected OptionGUI optGUI = null;

    // Screen state
    private int sectionNo = 1, numAlgorithms = 0;
    private BNJFileDialogFactory fcFactory = null;
    protected boolean isPRMK2 = false; // ugly hack

    // Learning engines // <-- Note: Guys, this should go to the config file!
    private String[][] learningEngines = {{"K2", "edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2"}, {"Genetic Algorithm Wrapper for K2 (GAWK)", "edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk.GAWK"}, {"Genetic Algorithm on Structure", "edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.slam.SLAM"}, {"Greedy Structure Learning", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.GreedySL"}, {"Standard Hill-climbing", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.HillClimbingSL"}, {"Hill-climbing with adversarial reweighting", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.HillClimbingARSL"}, {"Hill-climbing with Dirichlet prior", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.HillClimbingDPSL"}, {"Simulated Annealing", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.SimAnnealSL"}, {"Stochastic structural learning", "edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.StochasticSL"}};

    // Database drivers
    String[][] dbDrivers = {{"Oracle", "oracle.jdbc.driver.OracleDriver"}, {"PostgreSQL", "org.postgresql.Driver"}, {"MySQL", "org.gjt.mm.mysql.Driver"}};

    // Other states
    private Data data = null;
    private BBNGraph graph = null;
    private Learner learner = null;
    private Connection connection = null;
    private Statement statement = null;


    public LearningWizard(GUI owner) {
        super();
        this.owner = owner;
        if (owner != null) {
            //To gain control of the program while the wizard runs.
            owner.setEnabled(false);
        }
        init();
    }

    private void init() {
        Settings.setLanguage(Locale.ENGLISH, true);
        setSize(new Dimension(800, 600));
        GUIUtil.centerToScreen(this);
        fcFactory = new BNJFileDialogFactory(this);

        setTitle("Learning Wizard");
        outputComboBox = GUIUtil.createComboBox(Settings.getNetDescriptionList());

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //lay out the top level
        GUIUtil.gbAdd(dialogPanel, mainPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(dialogPanel, buttonPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);

        //make buttons
        prevButton = new JButton("< Prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(false);
        nextButton = new JButton("Next >");
        nextButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        //add the buttons to the button panel
        GUIUtil.gbAdd(buttonPanel, prevButton, 0, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(buttonPanel, nextButton, 1, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(buttonPanel, cancelButton, 2, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        // ---------------------------------
        // | File selection panel
        // ---------------------------------

        fileSelectionPanel = new JPanel();
        fileSelectionPanel.setLayout(new GridBagLayout());
        fileSelectionPanel.setBorder(new TitledBorder("Data Selection"));
        GUIUtil.gbAdd(mainPanel, fileSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);

        JLabel label = new JLabel("Which data file do you want to learn from?");
        GUIUtil.gbAdd(fileSelectionPanel, label, 0, 0, 3, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        loadFromFileRadioButton = new JRadioButton("Load from file");
        loadFromFileRadioButton.addActionListener(this);
        loadFromFileRadioButton.setSelected(true);
        useRemoteRadioButton = new JRadioButton("Use remote data");
        useRemoteRadioButton.addActionListener(this);
        loadFileText = new JTextField(42);
        browseDataButton = new JButton("Browse");
        browseDataButton.addActionListener(this);
        dbDriversComboBox = new JComboBox();
        for (int i = 0; i < dbDrivers.length; i++) {
            dbDriversComboBox.addItem(dbDrivers[i][0]);
        }
        dbURLText = new JTextField(30);
        dbLoginText = new JTextField(30);
        dbPasswordText = new JPasswordField(30);

        dbDriversLabel = new JLabel("Database type:");
        dbURLLabel = new JLabel("Database URL:");
        dbLoginLabel = new JLabel("Login:");
        dbPasswordLabel = new JLabel("Password:");

        dbDriversLabel.setEnabled(false);
        dbDriversComboBox.setEnabled(false);
        dbURLLabel.setEnabled(false);
        dbURLText.setEnabled(false);
        dbLoginLabel.setEnabled(false);
        dbLoginText.setEnabled(false);
        dbPasswordLabel.setEnabled(false);
        dbPasswordText.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        group.add(loadFromFileRadioButton);
        group.add(useRemoteRadioButton);

        GUIUtil.gbAdd(fileSelectionPanel, loadFromFileRadioButton, 0, 1, 1, 1, 0, 18, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, loadFileText, 0, 2, 2, 1, 0, 36, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, browseDataButton, 2, 2, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, useRemoteRadioButton, 0, 3, 1, 1, 0, 18, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbDriversLabel, 0, 4, 1, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbDriversComboBox, 1, 4, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbURLLabel, 0, 5, 1, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbURLText, 1, 5, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbLoginLabel, 0, 6, 1, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbLoginText, 1, 6, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbPasswordLabel, 0, 7, 1, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, dbPasswordText, 1, 7, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        GUIUtil.gbAdd(fileSelectionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        // ---------------------------------
        // | Table selection panel
        // ---------------------------------

        tableSelectionPanel = new JPanel();
        tableSelectionPanel.setLayout(new GridBagLayout());
        tableSelectionPanel.setBorder(new TitledBorder("Table Selection"));
        label = new JLabel("Which tables in the database do you want to learn?");
        GUIUtil.gbAdd(tableSelectionPanel, label, 0, 0, 3, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        availableTableListModel = new DefaultListModel();
        tableToSaveListModel = new DefaultListModel();
        tableAddButton = new JButton("Add >>");
        tableAddButton.addActionListener(this);
        tableRemoveButton = new JButton("<< Remove");
        tableRemoveButton.addActionListener(this);
        availableTableList = new JList();
        availableTableList.addMouseListener(this);
        tableToSaveList = new JList(tableToSaveListModel);
        tableToSaveList.addMouseListener(this);
        JLabel avail = new JLabel("Available tables:");
        JLabel toLearn = new JLabel("Tables to learn:");
        spAvail = new JScrollPane(availableTableList);
        spToLearn = new JScrollPane(tableToSaveList);

        Dimension dim = new Dimension(100, 120);
        spAvail.setPreferredSize(dim);
        spAvail.setMaximumSize(dim);
        spAvail.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spToLearn.setPreferredSize(dim);
        spToLearn.setMaximumSize(dim);
        spToLearn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JLabel launchDBGUI = new JLabel("If you want to inspect data further:");
        launchDBGUIButton = new JButton("Launch Database Tools");
        launchDBGUIButton.addActionListener(this);

        GUIUtil.gbAdd(tableSelectionPanel, avail, 0, 1, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(tableSelectionPanel, spAvail, 0, 2, 1, 2, 0, 12, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        GUIUtil.gbAdd(tableSelectionPanel, tableAddButton, 1, 2, 1, 1, 0, 12, 12, 12, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 0.05, 0.05);
        GUIUtil.gbAdd(tableSelectionPanel, tableRemoveButton, 1, 3, 1, 1, 0, 12, 12, 12, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 0.05, 0.05);

        GUIUtil.gbAdd(tableSelectionPanel, toLearn, 2, 1, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(tableSelectionPanel, spToLearn, 2, 2, 1, 2, 0, 12, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(tableSelectionPanel, launchDBGUI, 0, 5, 2, 1, 0, 12, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(tableSelectionPanel, launchDBGUIButton, 2, 5, 1, 2, 0, 12, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        GUIUtil.gbAdd(tableSelectionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        // ---------------------------------
        // | Algorithm selection panel
        // ---------------------------------

        algorithmSelectionPanel = new JPanel();
        algorithmSelectionPanel.setLayout(new GridBagLayout());
        algorithmSelectionPanel.setBorder(new TitledBorder("Learning Algorithm Selection"));
        label = new JLabel("Which learning algorithm do you prefer?");
        GUIUtil.gbAdd(algorithmSelectionPanel, label, 0, 0, 3, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        //make buttons
        numAlgorithms = learningEngines.length;
        learningRadioButton = new JRadioButton[numAlgorithms];
        group = new ButtonGroup();
        for (int i = 0; i < numAlgorithms; i++) {
            learningRadioButton[i] = new JRadioButton(learningEngines[i][0]);
            learningRadioButton[i].addActionListener(this);
            learningRadioButton[i].setSelected(false);
            group.add(learningRadioButton[i]);
        }
        learningRadioButton[0].setSelected(true);

        //add buttons to panel
        for (int i = 0; i < numAlgorithms; i++) {
            GUIUtil.gbAdd(algorithmSelectionPanel, learningRadioButton[i], 0, i + 1, 1, 1, 0, 36, 0, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        }

        GUIUtil.gbAdd(algorithmSelectionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        // ---------------------------------
        // | Result panel
        // ---------------------------------

        graphPanel = new BNJMainPanel();
        graphPanel.setGraphEditable(false); // Make it non-editable
        resultPanel = new JPanel();
        resultPanel.setLayout(new GridBagLayout());
        resultPanel.setBorder(new TitledBorder("Learned Graph"));
        GUIUtil.gbAdd(resultPanel, graphPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        this.setContentPane(dialogPanel);
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void changeScreen() {
        mainPanel.removeAll();
        switch (sectionNo) {
            case 1:
                GUIUtil.gbAdd(mainPanel, fileSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 2:
                GUIUtil.gbAdd(mainPanel, tableSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 3:
                GUIUtil.gbAdd(mainPanel, algorithmSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 4:
                GUIUtil.gbAdd(mainPanel, optGUI.getMainPane(), 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 5:
                // Display result
                GUIUtil.gbAdd(mainPanel, resultPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            default:
                throw new RuntimeException("Bug!");
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    protected void repopulateAvailableTableList() throws SQLException {
        DatabaseMetaData dmd = connection.getMetaData();
        ResultSet rs = dmd.getTables(null, null, null, new String[]{"TABLE"}); // $NON-NLS-1$
        availableTableListModel.removeAllElements();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME"); // $NON-NLS-1$
            availableTableListModel.addElement(tableName);
        }
        resort(availableTableListModel);
        availableTableList.setModel(availableTableListModel);
        availableTableList.revalidate();
        availableTableList.repaint();
    }

    protected void emptyTableLists() {
        tableToSaveListModel.removeAllElements();
        tableToSaveList.setModel(tableToSaveListModel);
        tableToSaveList.revalidate();
        tableToSaveList.repaint();
        availableTableListModel.removeAllElements();
        availableTableList.setModel(availableTableListModel);
        availableTableList.revalidate();
        availableTableList.repaint();
    }

    protected void resort(DefaultListModel model) {
        Object[] contents = model.toArray();
        model.clear();
        Arrays.sort(contents);
        int length = contents.length;
        for (int i = 0; i < length; i++) {
            model.addElement(contents[i]);
        }
    }

    protected void addTableToSave() {
        Object[] values = availableTableList.getSelectedValues();
        if (values == null) return;
        int length = values.length;
        for (int i = 0; i < length; i++) {
            tableToSaveListModel.addElement(values[i]);
            availableTableListModel.removeElement(values[i]);
        }
        resort(tableToSaveListModel);
        resort(availableTableListModel);
        tableToSaveList.setModel(tableToSaveListModel);
        tableToSaveList.revalidate();
        tableToSaveList.repaint();
        availableTableList.setModel(availableTableListModel);
        availableTableList.revalidate();
        availableTableList.repaint();
    }

    protected void removeTableToSave() {
        Object[] values = tableToSaveList.getSelectedValues();
        if (values == null) return;
        int length = values.length;
        for (int i = 0; i < length; i++) {
            availableTableListModel.addElement(values[i]);
            tableToSaveListModel.removeElement(values[i]);
        }
        resort(tableToSaveListModel);
        resort(availableTableListModel);
        tableToSaveList.setModel(tableToSaveListModel);
        tableToSaveList.revalidate();
        tableToSaveList.repaint();
        availableTableList.setModel(availableTableListModel);
        availableTableList.revalidate();
        availableTableList.repaint();
    }

    private void selected_BrowseData() {
        File fc = fcFactory.openDataFiles();
        if (fc == null) return;
        loadFileText.setText(fc.getAbsolutePath());
    }

    private void selected_BrowseOutput() {
        File fc = fcFactory.saveNetFiles();
        if (fc == null) return;
        outputFileText.setText(fc.getAbsolutePath());
    }

    private void selected_Next() {
        switch (sectionNo) {
            case 1:
                // Close remote connection, if any
                if (connection != null) disconnect();

                // Load the graph or fetch the graph from the main GUI
                if (loadFromFileRadioButton.isSelected()) {
                    String filename = loadFileText.getText();
                    if (filename == null || filename.length() == 0) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "You must fill in the file name first!");
                        return;
                    }
                    try {
                        Database db = Database.load(loadFileText.getText());
                        List ll = db.getTables();
                        if (ll.size() == 1)
                            data = (Data) ll.get(0);
                        else
                            data = db;
                    } catch (Exception e) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", Messages.getString("Error.CantOpenFile")); // $NON-NLS-1$
                        return;
                    }
                    sectionNo++; // If it's loaded from data, directly step to screen 3
                } else {
                    // Connect to database server
                    connect();
                }
                prevButton.setEnabled(true);
                break;
            case 2:
                LinkedList tableNames = new LinkedList();
                Object[] selectedTables = tableToSaveListModel.toArray();
                int numTables = selectedTables.length;
                for (int i = 0; i < numTables; i++) {
                    tableNames.add(selectedTables[i].toString());
                }
                data = Database.importRemoteSchema(connection, tableNames);
                break;
            case 3:
                if (connection != null) {
                    try {
                        if (connection.isClosed()) {
                            connect();
                        }
                    } catch (Exception e) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Connection to database server aborted");
                        return;
                    }
                }
                int selectedAlgorithm = -1;
                isPRMK2 = false;
                for (int xxx = 0; xxx < numAlgorithms; xxx++) {
                    if (learningRadioButton[xxx].isSelected()) {
                        selectedAlgorithm = xxx;
                        break;
                    }
                }
                if (selectedAlgorithm == -1) return;
                if (selectedAlgorithm == 0 && connection != null && data instanceof Database) {
                    // Cheat on PRM K2
                    learner = new PRMk2((Database) data, null); // Order is ignored for now
                    isPRMK2 = true;
                } else {
                    learner = Learner.load(learningEngines[selectedAlgorithm][1], data);
                }
                optGUI = learner.getOptionsDialog();
                nextButton.setText("Learn!");
                break;
            case 4:
                execute();
                break;
        }
        sectionNo++;
        changeScreen();
        if (sectionNo == 5) nextButton.setEnabled(false);
    }

    private void selected_Prev() {
        if (sectionNo == 3 && connection == null) sectionNo--; // If local, skip table selection screen
        sectionNo--;
        if (sectionNo < 5) nextButton.setText("Next >");
        nextButton.setEnabled(true);
        changeScreen();
        if (sectionNo == 1) prevButton.setEnabled(false);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == prevButton) {
            selected_Prev();
        } else if (source == nextButton) {
            selected_Next();
        } else if (source == cancelButton) {
            selected_Close();
        } else if (source == loadFromFileRadioButton || source == useRemoteRadioButton) {
            boolean isLoad = loadFromFileRadioButton.isSelected();
            boolean isRemote = !isLoad;
            browseDataButton.setEnabled(isLoad);
            loadFileText.setEnabled(isLoad);
            dbDriversLabel.setEnabled(isRemote);
            dbDriversComboBox.setEnabled(isRemote);
            dbURLLabel.setEnabled(isRemote);
            dbURLText.setEnabled(isRemote);
            dbLoginLabel.setEnabled(isRemote);
            dbLoginText.setEnabled(isRemote);
            dbPasswordLabel.setEnabled(isRemote);
            dbPasswordText.setEnabled(isRemote);
        } else if (source == browseDataButton) {
            selected_BrowseData();
        } else if (source == tableAddButton) {
            addTableToSave();
        } else if (source == tableRemoveButton) {
            removeTableToSave();
        } else if (source == launchDBGUIButton) {
            DatabaseGUI dbGUI = new DatabaseGUI(this);
            dbGUI.setVisible(true);
            dbGUI.connect(connection, statement);
        }
    }

    private void selected_Close() {
        disconnect();
        if (owner == null) ;// System.exit(0);
        //reenable the owner component to continue program.
        owner.setEnabled(true);
        setVisible(false);
        owner = null;
    }

    public void connect() {
        // Connect to database server
        int idx = dbDriversComboBox.getSelectedIndex();
        try {
            Class.forName(dbDrivers[idx][1]);
        } catch (Exception e) {
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot load database drivers");
            return;
        }
        try {
            char[] passwdChars = dbPasswordText.getPassword();
            connection = DriverManager.getConnection(dbURLText.getText(), dbLoginText.getText(), new String(passwdChars));
            for (int i = 0; i < passwdChars.length; i++) passwdChars[i] = '\t';
            statement = connection.createStatement();
            repopulateAvailableTableList();
        } catch (Exception e) {
            e.printStackTrace();
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot connect to database server");
            return;
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                statement.close();
                connection.close();
                statement = null;
                connection = null;
            } catch (Exception e) {
            }
        }
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowClosing(WindowEvent evt) {
        if (evt.getSource() == this) selected_Close();
    }

    public void windowDeactivated(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowOpened(WindowEvent evt) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent evt) {
        Object source = evt.getSource();
        if (source == tableToSaveList) {
            if (evt.getClickCount() > 1) {
                removeTableToSave();
            }
        } else if (source == availableTableList) {
            if (evt.getClickCount() > 1) {
                addTableToSave();
            }
        }
    }

    /**
     * Main brain of this GUI
     */
    private void execute() {
        //System.out.println("Execute!");
        if (!isPRMK2) optGUI.getOptionableOwner(); // Apply options
        BBNGraph graph = learner.getGraph();
        String outputFile = learner.getOutputFile();
        if (outputFile != null && outputFile.trim().length() > 0) {
            try {
                String outputFormat = Settings.getNetExtensionFromDescription((String) outputComboBox.getSelectedItem());
                graph.save(outputFile, outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
                DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot save learned graph!");
            }
        }

        graphPanel.setGraph(graph);
        Thread runner = new Thread() {
            public void run() {
                graphPanel.autoLayout();
            }
        };
        runner.start();
    }

    public static void main(String[] args) {
        Settings.loadEnglishGUISettings();
        new LearningWizard(null).show();
    }
}
