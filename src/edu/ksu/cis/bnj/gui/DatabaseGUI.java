package edu.ksu.cis.bnj.gui;

/*
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

import edu.ksu.cis.bnj.gui.components.BNJFileDialogFactory;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.util.Parameter;
import edu.ksu.cis.kdd.util.ParameterTable;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * JDBC-based Database Console
 * Requires: JDK 1.4 or better.
 *
 * @author Roby Joehanes
 */
public class DatabaseGUI extends JFrame implements ActionListener, WindowListener, KeyListener, MouseListener {
    class DataModel extends DefaultTableModel {
        public DataModel() {
            super();
        }

        public DataModel(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public DataModel(Vector arg0, int arg1) {
            super(arg0, arg1);
        }

        public DataModel(Object[] arg0, int arg1) {
            super(arg0, arg1);
        }

        public DataModel(Vector arg0, Vector arg1) {
            super(arg0, arg1);
        }

        public DataModel(Object[][] arg0, Object[] arg1) {
            super(arg0, arg1);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    public static final String ln = System.getProperty("line.separator"); // $NON-NLS-1$
    public static final int maxHistoryLength = 100;

    protected Connection connection = null;
    protected Statement stmt = null;

    protected JFrame owner = null;
    protected JTextArea queryField = null, consoleArea = null, infoArea = null, licenseArea = null;
    protected JEditorPane aboutPane = null;
    protected JTabbedPane aboutTab = null;
    protected JTextField consoleQueryField = null;
    protected JPanel guiPanel = null, consolePanel = null, mainPanel = null;
    protected JButton execButton = null, connectButton = null, browseButton = null, tableAddButton = null, tableRemoveButton = null, saveButton = null, cancelSaveButton = null;
    protected Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
    protected JTable table = null, tableList = null, tableColumns = null, tablePKeys = null, tableRKeys = null, tableIKeys = null, tableSuper = null, tablePrevileges = null;
    protected DataModel dataModel = null, tableListModel = null, tableColumnsModel = null, tablePKeysModel = null, tableRKeysModel = null, tableIKeysModel = null, tableSuperModel = null, tablePrevilegesModel = null;
    protected JDialog connectDialog = null, infoDialog = null, tableDialog = null, tableDisplayDialog = null, aboutDialog = null, saveDialog = null;
    protected JTextField url = null, login = null, saveText = null;
    protected JPasswordField passwd = null;
    protected JComboBox driver = null;
    protected JLabel statusBarText = null;
    protected JList availableTableList = null, tableToSaveList = null;
    protected DefaultListModel availableTableListModel = null, tableToSaveListModel = null;
    protected JMenuItem dbInfo = null, dbTables = null, connectMenu = null, exit = null, modeMenu = null, aboutMenu = null, loadMenu = null, saveMenu = null;
    protected boolean consoleMode = false;
    protected LinkedList commandHistory = new LinkedList();
    protected int commandHistoryIndex = 0;
    protected boolean isError = false;
    protected BNJFileDialogFactory fcFactory = null;

    protected static DataModel tableErrorModel = null;
    protected static String driverString = null;
    protected static String urlString = null;
    protected static String loginString = null;
    protected static String passwdString = null;
    protected Hashtable settings = Settings.getBNJSettings();

    /**
     * Constructor for DBTestGUI.
     *
     * @throws HeadlessException
     */
    public DatabaseGUI(JFrame o) throws HeadlessException {
        super();
        init();
        owner = o;
    }

    protected void init() {
        Settings.setLanguage(Locale.ENGLISH, true);
        setSize(new Dimension(800, 600));
        GUIUtil.centerToScreen(this);
        fcFactory = new BNJFileDialogFactory(this);

        tableErrorModel = new DatabaseGUI.DataModel(new String[][]{new String[]{"Unretrievable"}}, new String[]{"Error!"});
        guiPanel = new JPanel();
        guiPanel.setBorder(new EtchedBorder());
        guiPanel.setLayout(new GridBagLayout());

        setTitle("SQL Query GUI");
        GridBagConstraints constraint = new GridBagConstraints();

        JPanel upPanel = new JPanel();
        upPanel.setBorder(new TitledBorder(new EtchedBorder(), "Query"));
        upPanel.setLayout(new GridBagLayout());
        upPanel.setFocusable(false);

        queryField = new JTextArea();
        queryField.setRows(3);
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 5;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 3.0;
        upPanel.add(new JScrollPane(queryField), constraint);

        execButton = new JButton("Execute");
        execButton.addActionListener(this);
        execButton.setMnemonic('x');
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 0.5;
        upPanel.add(execButton, constraint);

        JPanel filler = new JPanel();
        filler.setFocusable(false);
        constraint.gridx = 2;
        constraint.gridy = 1;
        constraint.gridwidth = 3;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        upPanel.add(filler, constraint);

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 0.5;
        guiPanel.add(upPanel, constraint);

        JPanel downPanel = new JPanel();
        downPanel.setBorder(new TitledBorder(new EtchedBorder(), "Result"));
        downPanel.setLayout(new GridBagLayout());
        downPanel.setFocusable(false);

        dataModel = new DataModel(40, 5);

        table = new JTable();
        table.setModel(dataModel);
        table.setFocusable(false);
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        downPanel.add(new JScrollPane(table), constraint);

        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridheight = 5;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 2.0;
        guiPanel.add(downPanel, constraint);

        consolePanel = new JPanel();
        consolePanel.setBorder(new EtchedBorder());
        consolePanel.setLayout(new BorderLayout());

        consoleArea = new JTextArea("> "); // $NON-NLS-1$
        consoleArea.setEditable(false);
        consolePanel.add(new JScrollPane(consoleArea), BorderLayout.CENTER);

        consoleQueryField = new JTextField();
        consoleQueryField.setFont(new Font("Monospaced", Font.PLAIN, 12)); // $NON-NLS-1$
        consoleQueryField.addActionListener(this);
        consoleQueryField.addKeyListener(this);
        consolePanel.add(consoleQueryField, BorderLayout.SOUTH);

        JPanel statusBar = new JPanel();
        statusBar.setBorder(new EtchedBorder());
        statusBar.setLayout(new BorderLayout());
        statusBarText = new JLabel("Not connected");
        statusBar.add(statusBarText, BorderLayout.WEST);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        if (!consoleMode)
            mainPanel.add(guiPanel, BorderLayout.CENTER);
        else
            mainPanel.add(consolePanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menubar.add(menu);

        loadMenu = new JMenuItem("Upload file to server", KeyEvent.VK_U);
        loadMenu.addActionListener(this);
        loadMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        menu.add(loadMenu);

        saveMenu = new JMenuItem("Download to local", KeyEvent.VK_D);
        saveMenu.addActionListener(this);
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        menu.add(saveMenu);

        menu.addSeparator();

        dbInfo = new JMenuItem("Database Info", KeyEvent.VK_I);
        dbInfo.addActionListener(this);
        dbInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        menu.add(dbInfo);

        dbTables = new JMenuItem("Table List", KeyEvent.VK_T);
        dbTables.addActionListener(this);
        dbTables.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menu.add(dbTables);

        modeMenu = new JMenuItem(consoleMode ? "GUI Mode" : "Console Mode", KeyEvent.VK_M);
        modeMenu.addActionListener(this);
        modeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        menu.add(modeMenu);

        menu.addSeparator();

        connectMenu = new JMenuItem("Connect", KeyEvent.VK_C);
        connectMenu.addActionListener(this);
        connectMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menu.add(connectMenu);

        menu.addSeparator();

        exit = new JMenuItem("Quit", KeyEvent.VK_Q);
        exit.addActionListener(this);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menu.add(exit);

        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menubar.add(menu);

        aboutMenu = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenu.addActionListener(this);
        menu.add(aboutMenu);

        setJMenuBar(menubar);
        setupConnectDialog();
        setupInfoDialog();
        setupTableDialog();
        setupTableDisplayDialog();
        setupAboutDialog();
        setupSaveDialog();

        if (driverString != null && urlString != null && loginString != null && passwdString != null) {
            connect();
        }
    }

    protected void setupSaveDialog() {
        saveDialog = new JDialog(this);
        saveDialog.setTitle("Save Remote Database To Local File");
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EtchedBorder());
        mainPanel.setLayout(new BorderLayout());
        saveDialog.setContentPane(mainPanel);

        // Upper panel section

        JPanel upperPanel = new JPanel();
        upperPanel.setBorder(new TitledBorder(new EtchedBorder(), "Where to save"));
        upperPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 4;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 4.0;
        constraint.weighty = 1.0;

        saveText = new JTextField();
        upperPanel.add(saveText, constraint);

        constraint.gridx = 4;
        constraint.gridy = 0;
        constraint.gridwidth = 1;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        upperPanel.add(browseButton, constraint);

        // Lower panel section

        JPanel lowerPanel = new JPanel();
        lowerPanel.setBorder(new TitledBorder(new EtchedBorder(), "What to save"));
        lowerPanel.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Table to save"));
        panel.setLayout(new GridBagLayout());

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        tableToSaveListModel = new DefaultListModel();
        tableToSaveList = new JList(tableToSaveListModel);
        tableToSaveList.addMouseListener(this);
        panel.add(new JScrollPane(tableToSaveList), constraint);

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 5;
        constraint.gridwidth = 3;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 4.0;
        constraint.weighty = 1.0;
        lowerPanel.add(panel, constraint);

        constraint.gridx = 3;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        lowerPanel.add(new JPanel(), constraint); // Filler

        tableAddButton = new JButton("Add");
        tableAddButton.addActionListener(this);
        constraint.gridx = 3;
        constraint.gridy = 1;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 0.5;
        lowerPanel.add(tableAddButton, constraint);

        constraint.gridx = 3;
        constraint.gridy = 2;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 0.25;
        lowerPanel.add(new JPanel(), constraint); // Filler

        tableRemoveButton = new JButton("Remove");
        tableRemoveButton.addActionListener(this);
        constraint.gridx = 3;
        constraint.gridy = 3;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 0.5;
        lowerPanel.add(tableRemoveButton, constraint);

        constraint.gridx = 3;
        constraint.gridy = 4;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        lowerPanel.add(new JPanel(), constraint); // Filler

        panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Available tables"));
        panel.setLayout(new GridBagLayout());

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        availableTableListModel = new DefaultListModel();
        availableTableList = new JList();
        availableTableList.addMouseListener(this);
        panel.add(new JScrollPane(availableTableList), constraint);

        constraint.gridx = 4;
        constraint.gridy = 0;
        constraint.gridheight = 5;
        constraint.gridwidth = 3;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 4.0;
        constraint.weighty = 1.0;
        lowerPanel.add(panel, constraint);

        // Combine both upper and lower panel

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(upperPanel, constraint);

        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridheight = 5;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 10.0;
        panel.add(lowerPanel, constraint);

        mainPanel.add(panel, BorderLayout.CENTER);

        // Lower buttons (Save, cancel)
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 1;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JPanel(), constraint);

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        constraint.gridx = 1;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 0.5;
        constraint.weighty = 1.0;
        panel.add(saveButton, constraint);

        cancelSaveButton = new JButton("Cancel");
        cancelSaveButton.addActionListener(this);
        constraint.gridx = 2;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 0.5;
        constraint.weighty = 1.0;
        panel.add(cancelSaveButton, constraint);

        mainPanel.add(panel, BorderLayout.SOUTH);

        // Misc polishing
        Dimension size = new Dimension(480, 360);
        saveDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        saveDialog.setLocation(loc.width, loc.height);
        saveDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected void setupAboutDialog() {
        aboutDialog = new JDialog(this);
        aboutDialog.setTitle("About This Program");
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        panel.setLayout(new GridBagLayout());
        aboutDialog.setContentPane(panel);

        aboutTab = new JTabbedPane();
        aboutTab.addKeyListener(this);
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(aboutTab, constraint);

        JPanel newPanel = new JPanel();
        newPanel.setBorder(new TitledBorder(new EtchedBorder(), "Version Information"));
        newPanel.setLayout(new GridBagLayout());
        aboutPane = new JTextPane();
        aboutPane.setContentType("text/html"); // $NON-NLS-1$
        aboutPane.setText("<html><head> " + // $NON-NLS-1$
                "<STYLE TYPE=\"text/css\" MEDIA=screen> " + ln + // $NON-NLS-1$
                "<!-- " + ln + // $NON-NLS-1$
                "p, a, ul, ol, li, dd, dt tr, td, center { font: Arial; font-size: 12pt; margin: 2px 1px 2px 1px; font-family: helvetica, sans-serif  } " + ln + // $NON-NLS-1$
                "tt, pre { font: medium Courier New; font-family: courier, monospace; } " + ln + // $NON-NLS-1$
                "h1 { font: bold Arial; font-size: 28pt; margin: 5px 1px 5px 1px; font-family: helvetica, sans-serif } " + ln + // $NON-NLS-1$
                "h3 { font: bold Arial; font-size: 16pt; margin: 5px 1px 5px 1px; font-family: helvetica, sans-serif } " + ln + // $NON-NLS-1$
                "h4 { font: bold small Arial} " + ln + // $NON-NLS-1$
                "p.cpy { font: italic Verdana; font-size: small; font-family: helvetica, sans-serif  } " + ln + // $NON-NLS-1$
                "A:hover {color: #FF0000} " + ln + // $NON-NLS-1$
                "body {font-size: x-small; bgColor=#ffff7f; link=#0000ff; text=#000000; vLink=#7f007f;} " + ln + // $NON-NLS-1$
                "--> " + // $NON-NLS-1$
                "</STYLE></head><body>" + // $NON-NLS-1$
                "<center><h1>SQL Query GUI</h1>" + "<h3>Version 0.0001b</h3>" + "<h2>By: Roby Joehanes</h2>" + "</center><BR>" + // $NON-NLS-1$
                "<P>This program demonstrates basic APIs in JDBC. It also helped to demonstrate " + "the capabilities of the database underlying the engine. Hopefully it helps those" + "who wishes to debug JDBC-based databases." + "<P>I created this program in order to learn JDBC. Now that I succeeded " + "and I hope you benefit from this program as well." + "</body></html>" // $NON-NLS-1$
        );
        aboutPane.setEditable(false);
        aboutPane.addKeyListener(this);
        newPanel.add(new JScrollPane(aboutPane), constraint);
        aboutTab.addTab("About", newPanel);

        newPanel = new JPanel();
        newPanel.setBorder(new TitledBorder(new EtchedBorder(), "Program License"));
        newPanel.setLayout(new GridBagLayout());
        licenseArea = new JTextArea();
        licenseArea.addKeyListener(this);
        licenseArea.setEditable(false);
        licenseArea.setText(Settings.license);
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        newPanel.add(new JScrollPane(licenseArea), constraint);
        aboutTab.addTab("License", newPanel);

        Dimension size = new Dimension(480, 360);
        aboutDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        aboutDialog.setLocation(loc.width, loc.height);
        aboutDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected void setupTableDialog() {
        tableDialog = new JDialog(this);
        tableDialog.setTitle("Table Info");
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Table Information"));
        panel.setLayout(new GridBagLayout());
        tableDialog.setContentPane(panel);

        tableList = new JTable();
        tableListModel = new DataModel(25, 5);

        tableList.setModel(tableListModel);
        tableList.addKeyListener(this);
        tableList.addMouseListener(this);
        tableList.setToolTipText("Double click on each entry to view more information on that table.");
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tableList), constraint);

        Dimension size = new Dimension(480, 360);
        tableDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        tableDialog.setLocation(loc.width, loc.height);
        tableDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected void setupTableDisplayDialog() {
        tableDisplayDialog = new JDialog(this);
        tableDisplayDialog.setTitle("Table Info");

        JPanel mainDialogPanel = new JPanel();
        GridBagConstraints constraint = new GridBagConstraints();

        mainDialogPanel.setLayout(new GridBagLayout());
        tableDisplayDialog.setContentPane(mainDialogPanel);

        JTabbedPane tableTab = new JTabbedPane();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        mainDialogPanel.add(tableTab, constraint);

        // Columns
        tableColumns = new JTable();
        tableColumnsModel = new DataModel(25, 5);
        tableColumns.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableColumns.setModel(tableColumnsModel);
        tableColumns.addKeyListener(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tableColumns), constraint);
        tableTab.addTab("Column Info", panel);

        // Primary Keys
        tablePKeys = new JTable();
        tablePKeysModel = new DataModel(25, 5);
        tablePKeys.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablePKeys.setModel(tablePKeysModel);
        tablePKeys.addKeyListener(this);
        tablePKeys.setToolTipText("Table's primary key information");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tablePKeys), constraint);
        tableTab.addTab("Primary Keys", panel);

        // Reference Keys
        tableRKeys = new JTable();
        tableRKeysModel = new DataModel(25, 5);
        tableRKeys.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableRKeys.setModel(tableRKeysModel);
        tableRKeys.addKeyListener(this);
        tableRKeys.setToolTipText("Table's imported key information " + "(i.e. other table's reference keys that point to this table's primary key");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tableRKeys), constraint);
        tableTab.addTab("Imported Keys", panel);

        // Imported Keys
        tableIKeys = new JTable();
        tableIKeysModel = new DataModel(25, 5);
        tableIKeys.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableIKeys.setModel(tableIKeysModel);
        tableIKeys.addKeyListener(this);
        tableIKeys.setToolTipText("Table's reference key information");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tableIKeys), constraint);
        tableTab.addTab("Reference Keys", panel);

        // Previleges
        tablePrevileges = new JTable();
        tablePrevilegesModel = new DataModel(25, 5);
        tablePrevileges.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablePrevileges.setModel(tablePrevilegesModel);
        tablePrevileges.addKeyListener(this);
        tablePrevileges.setToolTipText("Table's access rights information");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tablePrevileges), constraint);
        tableTab.addTab("Previleges", panel);

        // Super Tables
        tableSuper = new JTable();
        tableSuperModel = new DataModel(25, 5);
        tableSuper.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableSuper.setModel(tableSuperModel);
        tableSuper.addKeyListener(this);
        tableSuper.setToolTipText("Table's parents information in hierarchy");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(tableSuper), constraint);
        tableTab.addTab("Table Parents", panel);

        Dimension size = new Dimension(480, 360);
        tableDisplayDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        tableDisplayDialog.setLocation(loc.width, loc.height);
        tableDisplayDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected void setupInfoDialog() {
        infoDialog = new JDialog(this);
        infoDialog.setTitle("Database Info");
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Database Information"));
        panel.setLayout(new GridBagLayout());
        infoDialog.setContentPane(panel);

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.addKeyListener(this);
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(new JScrollPane(infoArea), constraint);

        infoDialog.setContentPane(panel);
        Dimension size = new Dimension(640, 480);
        infoDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        infoDialog.setLocation(loc.width, loc.height);
        infoDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected void setupConnectDialog() {
        connectDialog = new JDialog(this);
        connectDialog.setTitle("Connect");
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Connect"));
        panel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Driver Name");
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(label, constraint);
        panel.setFocusable(false);

        driver = new JComboBox();
        String dbDrivers = (String) settings.get("AvailableJDBCDrivers"); // $NON-NLS-1$
        if (dbDrivers == null) {
            driver.addItem("oracle.jdbc.driver.OracleDriver"); // $NON-NLS-1$
            driver.addItem("org.postgresql.Driver"); // $NON-NLS-1$
            driver.addItem("org.gjt.mm.mysql.Driver"); // $NON-NLS-1$
            driver.addItem("sun.jdbc.odbc.JdbcOdbcDriver"); // $NON-NLS-1$
        } else {
            StringTokenizer tok = new StringTokenizer(dbDrivers, ","); // $NON-NLS-1$
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken().trim();
                driver.addItem(token);
            }
        }
        driver.addKeyListener(this);
        driver.setEditable(true);
        if (driverString != null) {
            driver.setSelectedItem(driverString);
        } else {
            String settingDriver = (String) settings.get("JDBCDriver"); // $NON-NLS-1$
            if (settingDriver != null) driver.setSelectedItem(settingDriver);
        }
        driver.addActionListener(this);
        constraint.gridx = 1;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        panel.add(driver, constraint);

        label = new JLabel("URL");
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(label, constraint);

        // Old URL = jdbc:oracle:thin:@zaurak.cis.ksu.edu:1521:PROD
        if (urlString == null) {
            url = new JTextField();
            String settingURL = (String) settings.get("JDBCURL"); // $NON-NLS-1$
            if (settingURL != null) url.setText(settingURL);
        } else {
            url = new JTextField(urlString);
        }
        url.addActionListener(this);
        url.addKeyListener(this);
        constraint.gridx = 1;
        constraint.gridy = 1;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        panel.add(url, constraint);

        label = new JLabel("Login");
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(label, constraint);

        if (loginString == null) {
            login = new JTextField();
            String settingLogin = (String) settings.get("JDBCLogin"); // $NON-NLS-1$
            if (settingLogin != null) login.setText(settingLogin);
        } else {
            login = new JTextField(loginString);
        }
        login.addActionListener(this);
        login.addKeyListener(this);
        constraint.gridx = 1;
        constraint.gridy = 2;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        panel.add(login, constraint);

        label = new JLabel("Password");
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(label, constraint);

        if (passwdString == null) {
            passwd = new JPasswordField();
            String settingPasswd = (String) settings.get("JDBCPassword"); // $NON-NLS-1$
            if (settingPasswd != null) passwd.setText(settingPasswd);
        } else {
            passwd = new JPasswordField(passwdString);
        }
        passwd.addActionListener(this);
        passwd.addKeyListener(this);
        constraint.gridx = 1;
        constraint.gridy = 3;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 1.0;
        panel.add(passwd, constraint);

        connectButton = new JButton("Connect");
        connectButton.setMnemonic('c');
        connectButton.addActionListener(this);
        connectButton.addKeyListener(this);
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.gridheight = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(connectButton, constraint);

        connectDialog.setContentPane(panel);
        Dimension size = new Dimension(320, 240);
        connectDialog.setSize(size);
        Dimension loc = center(size, scrSize);
        connectDialog.setLocation(loc.width, loc.height);
        connectDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    protected Dimension center(Dimension src, Dimension dest) {
        if (src.height > dest.height) src.height = dest.height;
        if (src.width > dest.width) src.width = dest.width;
        return new Dimension((dest.width - src.width) / 2, (dest.height - src.height) / 2);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == execButton) {
            String query = queryField.getText();
            try {
                ResultSet rs = executeQuery(query);
                queryField.requestFocus();
                statusBarText.setText("Query success!");
                if (rs == null) return;
                dataModel = resultSetToDataModel(rs);
                table.setModel(dataModel);
                table.invalidate();
                table.repaint();
            } catch (SQLException e) {
                e.printStackTrace();
                statusBarText.setText("Query Error!");
                JOptionPane.showOptionDialog(this, "Query Error!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
                queryField.requestFocus();
            }
        } else if (source == connectMenu) {
            connectDialog.show();
        } else if (source == connectButton || source == passwd) {
            if (connect()) {
                JOptionPane.showOptionDialog(this, "Connected to the database!", "Connected!", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"OK"}, "OK");
            }
            connectDialog.setVisible(false);
            queryField.requestFocus();
        } else if (source == driver) {
            if (url != null) url.requestFocus();
        } else if (source == url) {
            if (login != null) login.requestFocus();
        } else if (source == login) {
            if (passwd != null) passwd.requestFocus();
        } else if (source == exit) {
            windowClosing(null);
        } else if (source == modeMenu) {
            if (consoleMode) toGUIMode(); else toConsoleMode();
        } else if (source == consoleQueryField) {
            String query = consoleQueryField.getText().trim();
            if (query.equals("")) return; // $NON-NLS-1$
            if (commandHistory.size() > maxHistoryLength) commandHistory.removeFirst();
            commandHistory.add(query);
            commandHistoryIndex = commandHistory.size();
            consoleQueryField.setText(""); // $NON-NLS-1$
            if (query.equals("clear") || query.equals("clear;")) { // $NON-NLS-1$ // $NON-NLS-2$
                consoleArea.setText(""); // $NON-NLS-1$
                return;
            }
            consoleArea.append(query + ln);
            try {
                ResultSet rs = executeQuery(query);
                if (rs == null) {
                    if (isError) return;
                    consoleArea.append("OK" + ln + ln);
                    consoleArea.append("> ");  // $NON-NLS-1$
                    return;
                }
                ResultSetMetaData rsmeta = rs.getMetaData();
                int numColumns = rsmeta.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    String columnName = rsmeta.getColumnName(i);
                    if (i > 1) consoleArea.append("\t"); // $NON-NLS-1$
                    consoleArea.append(columnName);
                }
                consoleArea.append(ln);
                while (rs.next()) {
                    Vector row = new Vector();
                    for (int i = 1; i <= numColumns; i++) {
                        String columnValue = rs.getString(i);
                        if (i > 1) consoleArea.append("\t"); // $NON-NLS-1$
                        consoleArea.append(columnValue);
                    }
                    consoleArea.append(ln);
                }
                consoleArea.append(ln);
            } catch (SQLException e) {
                consoleArea.append("Bad SQL Command" + ln + ln);
                e.printStackTrace();
            }
            consoleArea.append("> "); // $NON-NLS-1$
        } else if (source == dbInfo) {
            infoArea.setText(getDatabaseInfo());
            infoDialog.setVisible(true);
        } else if (source == dbTables) {
            getTableInfo();
            tableDialog.setVisible(true);
        } else if (source == aboutMenu) {
            aboutDialog.setVisible(true);
        } else if (source == loadMenu) {
            File fc = fcFactory.openDataFiles();
            if (fc == null) return;
            open(fc.getAbsolutePath());
        } else if (source == saveMenu) {
            if (checkConnect()) {
                emptyTableLists();
                try {
                    repopulateAvailableTableList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                saveDialog.setVisible(true);
            }
        } else if (source == cancelSaveButton) {
            saveDialog.setVisible(false);
        } else if (source == browseButton) {
            if (!checkConnect()) return;
            File fc = fcFactory.saveDataFiles();
            if (fc == null) return;
            saveText.setText(fc.getAbsolutePath());
        } else if (source == saveButton) {
            if (!checkConnect()) return;
            if (tableToSaveListModel.size() == 0) {
                JOptionPane.showOptionDialog(this, "You need to select tables to save!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
                return;
            }
            String whereToSave = saveText.getText();
            if (whereToSave == null || whereToSave.length() == 0) {
                JOptionPane.showOptionDialog(this, "You need to specify the filename!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
                return;
            }
            save(saveText.getText());
            saveDialog.setVisible(false);
        } else if (source == tableAddButton) {
            addTableToSave();
        } else if (source == tableRemoveButton) {
            removeTableToSave();
        }
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

    protected boolean checkConnect() {
        if (connection != null) return true;
        JOptionPane.showOptionDialog(this, "Cannot connect to the database!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
        return false;
    }

    protected boolean save(String file) {
        try {
            LinkedList tableNames = new LinkedList();
            Object[] selectedTables = tableToSaveListModel.toArray();
            int numTables = selectedTables.length;
            for (int i = 0; i < numTables; i++) {
                tableNames.add(selectedTables[i].toString());
            }
            Database db = Database.importRemoteSchema(connection, tableNames);
            db.importDatabaseToLocal();
            db.save(file);
            statusBarText.setText("Data save successful.");
            return true;
        } catch (Exception e) {
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error saving the file", "Cannot save the file!");
            e.printStackTrace();
        }
        return false;
    }

    protected boolean open(String file) {
        try {
            Database db = Database.load(file);
            stmt.close();
            db.exportToServer(connection);
            stmt = connection.createStatement();
            statusBarText.setText("Data load successful.");
            return true;
        } catch (Exception e) {
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error opening the file", "Cannot open the file!");
            e.printStackTrace();
        }
        return false;
    }


    protected DataModel resultSetToDataModel(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmeta = rs.getMetaData();
        int numColumns = rsmeta.getColumnCount();
        Vector columnNames = new Vector();
        for (int i = 1; i <= numColumns; i++) {
            String columnName = rsmeta.getColumnName(i);
            columnNames.add(columnName);
        }
        Vector data = new Vector();
        while (rs.next()) {
            Vector row = new Vector();
            for (int i = 1; i <= numColumns; i++) {
                String columnValue = rs.getString(i);
                if (columnValue == null)
                    row.add("null"); // $NON-NLS-1$
                else
                    row.add(columnValue);
            }
            data.add(row);
        }
        DataModel dm = new DataModel(data, columnNames);
        return dm;
    }

    protected void getTableInfo() {
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            dmd = connection.getMetaData();
            rs = dmd.getTables(null, null, null, new String[]{"TABLE"}); // $NON-NLS-1$
            tableListModel = resultSetToDataModel(rs);
            tableList.setModel(tableListModel);
            tableList.invalidate();
            tableList.repaint();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected String getDatabaseInfo() {
        StringBuffer buf = new StringBuffer();
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            dmd = connection.getMetaData();
        } catch (Throwable e) {
            e.printStackTrace();
            return "Cannot get database metadata!";
        }
        try {
            buf.append("Database Product Name: " + dmd.getDatabaseProductName() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Database Version     : " + dmd.getDatabaseMajorVersion() + "." + dmd.getDatabaseMinorVersion() + ln); // $NON-NLS-2$
        } catch (Throwable e) {
        }
        try {
            buf.append("Database Version Name: " + dmd.getDatabaseProductVersion() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Database Driver      : " + dmd.getDriverName() + ln);
        } catch (Throwable e) {
        }
        buf.append("Database Driver Class: " + driver.getSelectedItem() + ln);
        try {
            buf.append("Driver Version       : " + dmd.getDriverVersion() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("JDBC Version         : " + dmd.getJDBCMajorVersion() + "." + dmd.getJDBCMinorVersion() + ln); // $NON-NLS-2$
        } catch (Throwable e) {
        }
        buf.append(ln);
        try {
            buf.append("URL: " + dmd.getURL() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Login: " + dmd.getUserName() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Catalog: " + connection.getCatalog() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Database " + (dmd.isReadOnly() ? "is " : "is not ") + "in read only mode." + ln);
        } catch (Throwable e) {
        }
        try {
            if (dmd.allProceduresAreCallable()) {
                buf.append("All procedures are callable." + ln);
            } else
                buf.append("Not all procedures are callable." + ln);
        } catch (Throwable e) {
        }
        try {
            if (dmd.allTablesAreSelectable()) {
                buf.append("All tables are selectable." + ln);
            } else
                buf.append("Not all tables are selectable." + ln);
        } catch (Throwable e) {
        }
        buf.append(ln);
        buf.append("Database Features" + ln);
        buf.append("=================" + ln);
        buf.append("ANSI 92 support: ");
        try {
            if (dmd.supportsANSI92FullSQL()) {
                buf.append("full");
            } else if (dmd.supportsANSI92IntermediateSQL()) {
                buf.append("intermediate");
            } else if (dmd.supportsANSI92EntryLevelSQL()) {
                buf.append("entry level");
            } else {
                buf.append("none");
            }
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        buf.append("Supports transaction ? ");
        try {
            if (dmd.supportsTransactions()) {
                buf.append("Yes");
                if (dmd.supportsMultipleTransactions()) {
                    buf.append(", multiple");
                } else {
                    buf.append(", non-multiple");
                }
            } else {
                buf.append("No");
            }
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        buf.append("SQL grammar support: ");
        try {
            if (dmd.supportsExtendedSQLGrammar())
                buf.append("extended ");
            else if (dmd.supportsCoreSQLGrammar())
                buf.append("core ");
            else if (dmd.supportsMinimumSQLGrammar()) buf.append("minimum ");
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        buf.append("Supports data definitions and data manipulation transactions ? ");
        try {
            if (dmd.supportsDataDefinitionAndDataManipulationTransactions())
                buf.append("Yes, both are supported");
            else if (dmd.supportsDataManipulationTransactionsOnly()) {
                buf.append("No, only data manipulations");
            } else {
                buf.append("No, none of them");
            }
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        try {
            buf.append("Supports stored procedures ? " + (dmd.supportsStoredProcedures() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports statement pooling ? " + (dmd.supportsStatementPooling() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports batch updates ? " + (dmd.supportsBatchUpdates() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        buf.append("Outer join support: ");
        try {
            if (dmd.supportsOuterJoins()) {
                if (dmd.supportsFullOuterJoins()) {
                    buf.append("Full");
                } else if (dmd.supportsLimitedOuterJoins()) {
                    buf.append("Limited");
                } else {
                    buf.append("Yes, but unknown whether it's full or limited.");
                }
            } else {
                buf.append("No");
            }
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        try {
            buf.append("Supports get generated keys? " + (dmd.supportsGetGeneratedKeys() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports GROUP BY ? " + (dmd.supportsGroupBy() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports GROUP BY beyond SELECT ? " + (dmd.supportsGroupByBeyondSelect() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports GROUP BY not in SELECT ? " + (dmd.supportsGroupByUnrelated() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports expressions in ORDER BY ? " + (dmd.supportsExpressionsInOrderBy() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports SELECT FOR UPDATE? " + (dmd.supportsSelectForUpdate() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports integrity enhancement facility ? " + (dmd.supportsIntegrityEnhancementFacility() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports LIKE escape clause ? " + (dmd.supportsLikeEscapeClause() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports mixed case identifiers ? " + (dmd.supportsMixedCaseIdentifiers() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports mixed case quoted identifiers ? " + (dmd.supportsMixedCaseQuotedIdentifiers() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports conversion ? " + (dmd.supportsConvert() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports subqueries in comparisons ? " + (dmd.supportsSubqueriesInComparisons() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports subqueries in EXISTS ? " + (dmd.supportsSubqueriesInExists() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports subqueries in IN ? " + (dmd.supportsSubqueriesInIns() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports subqueries in quantified ? " + (dmd.supportsSubqueriesInQuantifieds() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports correlated subqueries ? " + (dmd.supportsCorrelatedSubqueries() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports table correlation names ? " + (dmd.supportsTableCorrelationNames() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports different table correlation names ? " + (dmd.supportsDifferentTableCorrelationNames() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports alter table with add columns ? " + (dmd.supportsAlterTableWithAddColumn() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports alter table with drop columns ? " + (dmd.supportsAlterTableWithDropColumn() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports catalogs in data manipulations ? " + (dmd.supportsCatalogsInDataManipulation() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports catalogs in index definition ? " + (dmd.supportsCatalogsInIndexDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports catalogs in previlege definition ? " + (dmd.supportsCatalogsInPrivilegeDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports catalogs in procedure calls ? " + (dmd.supportsCatalogsInProcedureCalls() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports catalogs in table definitions ? " + (dmd.supportsCatalogsInTableDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports schemas in data manipulations ? " + (dmd.supportsSchemasInDataManipulation() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports schemas in index definition ? " + (dmd.supportsSchemasInIndexDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports schemas in previlege definition ? " + (dmd.supportsSchemasInPrivilegeDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports schemas in procedure calls ? " + (dmd.supportsSchemasInProcedureCalls() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports schemas in table definitions ? " + (dmd.supportsSchemasInTableDefinitions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports column aliasing ? " + (dmd.supportsColumnAliasing() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports multiple open results ? " + (dmd.supportsMultipleOpenResults() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports multiple result set ? " + (dmd.supportsMultipleResultSets() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports named parameters ? " + (dmd.supportsNamedParameters() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports non-nullable columns ? " + (dmd.supportsNonNullableColumns() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports open cursors accross commit ? " + (dmd.supportsOpenCursorsAcrossCommit() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports open cursors accross rollback ? " + (dmd.supportsOpenCursorsAcrossRollback() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports open statements accross commit ? " + (dmd.supportsOpenStatementsAcrossCommit() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports open statements accross rollback ? " + (dmd.supportsOpenStatementsAcrossRollback() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports positioned delete ? " + (dmd.supportsPositionedDelete() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports positioned update ? " + (dmd.supportsPositionedUpdate() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports save points ? " + (dmd.supportsSavepoints() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports UNION ? " + (dmd.supportsUnion() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Supports UNION ALL ? " + (dmd.supportsUnionAll() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max catalog name length: " + dmd.getMaxCatalogNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max column name length: " + dmd.getMaxColumnNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max cursor name length: " + dmd.getMaxCursorNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max procedure name length: " + dmd.getMaxProcedureNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max schema name length: " + dmd.getMaxSchemaNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max table name length: " + dmd.getMaxTableNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max user name length: " + dmd.getMaxUserNameLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max binary literal length: " + dmd.getMaxBinaryLiteralLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max char literal length: " + dmd.getMaxCharLiteralLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max index length: " + dmd.getMaxIndexLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max row size: " + dmd.getMaxRowSize() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max statement length: " + dmd.getMaxStatementLength() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of statements opened at the same time: " + dmd.getMaxStatements() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of columns allowed in GROUP BY: " + dmd.getMaxColumnsInGroupBy() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of columns allowed in ORDER BY: " + dmd.getMaxColumnsInOrderBy() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of columns allowed in SELECT: " + dmd.getMaxColumnsInSelect() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of columns allowed in an index: " + dmd.getMaxColumnsInIndex() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of columns allowed in a table: " + dmd.getMaxColumnsInTable() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of tables allowed in SELECT: " + dmd.getMaxTablesInSelect() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Max number of connections: " + dmd.getMaxConnections() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Is data definition causes transaction commit? " + (dmd.dataDefinitionCausesTransactionCommit() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Is data definition ignored in transaction ? " + (dmd.dataDefinitionIgnoredInTransactions() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Does max row size include blobs ? " + (dmd.doesMaxRowSizeIncludeBlobs() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Is catalog at start ? " + (dmd.isCatalogAtStart() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Default transaction isolation level: " + dmd.getDefaultTransactionIsolation() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Locators update copy ? " + (dmd.locatorsUpdateCopy() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Does null plus non-null equal null ? " + (dmd.nullPlusNonNullIsNull() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Null sort order: " + (dmd.nullsAreSortedAtStart() ? "At start" : dmd.nullsAreSortedAtEnd() ? "At end" : "Neither at start nor at end") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Null sort behavior: " + (dmd.nullsAreSortedHigh() ? "High" : dmd.nullsAreSortedLow() ? "Low" : "Neither high nor low") + ln);
        } catch (Throwable e) {
        }
        buf.append("Result set cursor behavior: ");
        try {
            switch (dmd.getResultSetHoldability()) {
                case ResultSet.HOLD_CURSORS_OVER_COMMIT:
                    buf.append("Hold cursors over commit." + ln);
                    break;
                case ResultSet.CLOSE_CURSORS_AT_COMMIT:
                    buf.append("Close cursors at commit." + ln);
                    break;
                default:
                    buf.append("unspecified" + ln);
            }
        } catch (Throwable e) {
            buf.append("unspecified" + ln);
        }
        try {
            buf.append("Uses local file per table ? " + (dmd.usesLocalFilePerTable() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Uses local files ? " + (dmd.usesLocalFiles() ? "Yes" : "No") + ln);
        } catch (Throwable e) {
        }
        buf.append(ln);
        buf.append("Database specifics" + ln);
        buf.append("==================" + ln);
        try {
            buf.append("Catalog is called " + dmd.getCatalogTerm() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Schema is called " + dmd.getSchemaTerm() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Procedure is called " + dmd.getProcedureTerm() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Escape character is " + dmd.getSearchStringEscape() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Catalog separator character is " + dmd.getCatalogSeparator() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Extended characters allowed in identifier are " + dmd.getExtraNameCharacters() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Identifier quote string is " + dmd.getIdentifierQuoteString() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Recognized non-standard SQL keywords are: " + ln + dmd.getSQLKeywords() + ln);
        } catch (Throwable e) {
        }
        buf.append("SQL state type is: ");
        try {
            switch (dmd.getSQLStateType()) {
                case DatabaseMetaData.sqlStateSQL99:
                    buf.append("SQL99");
                    break;
                case DatabaseMetaData.sqlStateXOpen:
                    buf.append("X Open");
                    break;
                default:
                    buf.append("unspecified");
            }
        } catch (Throwable e) {
            buf.append("unspecified");
        }
        buf.append(ln);
        try {
            buf.append("Numeric functions are: " + dmd.getNumericFunctions() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("String functions are: " + dmd.getStringFunctions() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("Date and time functions are: " + dmd.getTimeDateFunctions() + ln);
        } catch (Throwable e) {
        }
        try {
            buf.append("System functions are: " + dmd.getSystemFunctions() + ln);
        } catch (Throwable e) {
        }
        buf.append(ln + ln);
        buf.append("Supported table types:" + ln);
        try {
            rs = dmd.getTableTypes();
            while (rs.next()) {
                buf.append("  " + rs.getString(1) + ln); // $NON-NLS-1$
            }
        } catch (Throwable e) {
        }

        buf.append(ln + "Supported types:" + ln);
        try {
            rs = dmd.getTypeInfo();
            ResultSetMetaData rsmeta = rs.getMetaData();
            int column = rsmeta.getColumnCount();
            buf.append("  "); // $NON-NLS-1$
            for (int i = 1; i <= column; i++) {
                buf.append(rsmeta.getColumnName(i) + "\t"); // $NON-NLS-1$
            }
            buf.append(ln);
            Hashtable sqlTypes = getSQLTypeTable();
            while (rs.next()) {
                buf.append("  "); // $NON-NLS-1$
                for (int i = 1; i <= column; i++) {
                    if (i == 2)
                        buf.append(sqlTypes.get(new Integer(rs.getInt(2))) + "\t\t"); // $NON-NLS-1$
                    else {
                        String s = rs.getString(i);
                        buf.append(s + "\t"); // $NON-NLS-1$
                        if (s == null) {
                            buf.append("\t");
                            continue; // $NON-NLS-1$
                        }
                        if (s.length() < 8) buf.append("\t"); // $NON-NLS-1$
                    }
                }
                buf.append(ln);
            }
        } catch (Throwable e) {
        }

        buf.append(ln + "Catalogues:" + ln);

        try {
            rs = dmd.getCatalogs();
            while (rs.next()) {
                buf.append("  " + rs.getString(1) + ln);
            }
        } catch (Throwable e) {
        }

        buf.append(ln + "Schemas:" + ln);

        try {
            rs = dmd.getSchemas();
            while (rs.next()) {
                buf.append("  " + rs.getString(1) + ln); // $NON-NLS-1$
            }
        } catch (Throwable e) {
        }

        return buf.toString();
    }

    protected Hashtable getSQLTypeTable() {
        Hashtable table = new Hashtable();
        table.put(new Integer(Types.ARRAY), "ARRAY"); // $NON-NLS-1$
        table.put(new Integer(Types.BIGINT), "BIGINT"); // $NON-NLS-1$
        table.put(new Integer(Types.BINARY), "BINARY"); // $NON-NLS-1$
        table.put(new Integer(Types.BIT), "BIT"); // $NON-NLS-1$
        table.put(new Integer(Types.BLOB), "BLOB"); // $NON-NLS-1$
        table.put(new Integer(Types.BOOLEAN), "BOOLEAN"); // $NON-NLS-1$
        table.put(new Integer(Types.CHAR), "CHAR"); // $NON-NLS-1$
        table.put(new Integer(Types.CLOB), "CLOB"); // $NON-NLS-1$
        table.put(new Integer(Types.DATALINK), "DATALINK"); // $NON-NLS-1$
        table.put(new Integer(Types.DATE), "DATE"); // $NON-NLS-1$
        table.put(new Integer(Types.DECIMAL), "DECIMAL"); // $NON-NLS-1$
        table.put(new Integer(Types.DISTINCT), "DISTINCT"); // $NON-NLS-1$
        table.put(new Integer(Types.DOUBLE), "DOUBLE"); // $NON-NLS-1$
        table.put(new Integer(Types.FLOAT), "FLOAT"); // $NON-NLS-1$
        table.put(new Integer(Types.INTEGER), "INTEGER"); // $NON-NLS-1$
        table.put(new Integer(Types.JAVA_OBJECT), "JAVA OBJECT"); // $NON-NLS-1$
        table.put(new Integer(Types.LONGVARBINARY), "LONGVARBINARY"); // $NON-NLS-1$
        table.put(new Integer(Types.LONGVARCHAR), "LONGVARCHAR"); // $NON-NLS-1$
        table.put(new Integer(Types.NULL), "NULL"); // $NON-NLS-1$
        table.put(new Integer(Types.NUMERIC), "NUMERIC"); // $NON-NLS-1$
        table.put(new Integer(Types.OTHER), "OTHER"); // $NON-NLS-1$
        table.put(new Integer(Types.REAL), "REAL"); // $NON-NLS-1$
        table.put(new Integer(Types.REF), "REF"); // $NON-NLS-1$
        table.put(new Integer(Types.SMALLINT), "SMALLINT"); // $NON-NLS-1$
        table.put(new Integer(Types.STRUCT), "STRUCT"); // $NON-NLS-1$
        table.put(new Integer(Types.TIME), "TIME"); // $NON-NLS-1$
        table.put(new Integer(Types.TIMESTAMP), "TIMESTAMP"); // $NON-NLS-1$
        table.put(new Integer(Types.TINYINT), "TINYINT"); // $NON-NLS-1$
        table.put(new Integer(Types.VARBINARY), "VARBINARY"); // $NON-NLS-1$
        table.put(new Integer(Types.VARCHAR), "VARCHAR"); // $NON-NLS-1$
        return table;
    }

    protected void toConsoleMode() {
        mainPanel.remove(guiPanel);
        mainPanel.add(consolePanel, BorderLayout.CENTER);
        consoleQueryField.requestFocus();
        statusBarText.setText("Console mode");
        modeMenu.setText("GUI Mode");
        consoleMode = true;
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    protected void toGUIMode() {
        mainPanel.remove(consolePanel);
        mainPanel.add(guiPanel, BorderLayout.CENTER);
        queryField.requestFocus();
        statusBarText.setText("GUI mode");
        modeMenu.setText("Console Mode");
        consoleMode = false;
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    protected ResultSet executeQuery(String query) throws SQLException {
        isError = false;
        if (connection == null) {
            JOptionPane.showOptionDialog(this, "Not connected to the database yet! Click option", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
            isError = true;
            return null;
        }
        query = query.trim();
        if (query.endsWith(";")) { // $NON-NLS-1$
            query = query.substring(0, query.length() - 1);
        }
        if (query.length() == 0) {
            JOptionPane.showOptionDialog(this, "No commands entered! Please type some commands!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
            isError = true;
            return null;
        }
        boolean isAnyResult = stmt.execute(query);
        if (!isAnyResult) return null; // no results returned;
        return stmt.getResultSet();
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

    public boolean connect() {
        try {
            disconnect();
            Class.forName(driver.getSelectedItem().toString());
            char[] passwdChars = passwd.getPassword();
            connection = DriverManager.getConnection(url.getText(), login.getText(), new String(passwdChars));
            for (int i = 0; i < passwdChars.length; i++) passwdChars[i] = '\t';
            stmt = connection.createStatement();
            repopulateAvailableTableList();
            statusBarText.setText("Connected.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showOptionDialog(this, "Cannot connect to the database!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
        }
        return false;
    }

    public boolean connect(Connection c) {
        try {
            disconnect();
            connection = c;
            stmt = connection.createStatement();
            repopulateAvailableTableList();
            statusBarText.setText("Connected.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showOptionDialog(this, "Cannot connect to the database!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
        }
        return false;
    }

    public boolean connect(Connection c, Statement s) {
        disconnect();
        connection = c;
        stmt = s;
        try {
            repopulateAvailableTableList();
            statusBarText.setText("Connected.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showOptionDialog(this, "Cannot connect to the database!", "Error!", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
        }
        return false;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                stmt.close();
                connection.close();
                stmt = null;
                connection = null;
                emptyTableLists();
            } catch (Exception e) {
            }
        }
        statusBarText.setText("Not connected");
    }

    public void mouseClicked(MouseEvent evt) {
        Object source = evt.getSource();
        if (source == tableList) {
            if (evt.getClickCount() > 1) {
                int row = tableList.getSelectedRow();
                int maxcol = tableListModel.getColumnCount();
                assert (maxcol > 3);
                String catalogName = (String) tableListModel.getValueAt(row, 0);
                String schemaName = (String) tableListModel.getValueAt(row, 1);
                String tableName = (String) tableListModel.getValueAt(row, 2);
                if (catalogName.equals("null")) catalogName = null; // $NON-NLS-1$
                if (schemaName.equals("null")) schemaName = null; // $NON-NLS-1$
                DatabaseMetaData dmd = null;
                ResultSet rs = null;
                try {
                    dmd = connection.getMetaData();
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("Error! cannot retrieve database meta data!");
                    return;
                }

                try {
                    rs = dmd.getColumns(catalogName, schemaName, tableName, null);
                    tableColumnsModel = resultSetToDataModel(rs);
                    tableColumns.setModel(tableColumnsModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tableColumnsModel = tableErrorModel;
                    tableColumns.setModel(tableColumnsModel);
                }

                try {
                    rs = dmd.getPrimaryKeys(catalogName, schemaName, tableName);
                    tablePKeysModel = resultSetToDataModel(rs);
                    tablePKeys.setModel(tablePKeysModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tablePKeysModel = tableErrorModel;
                    tablePKeys.setModel(tablePKeysModel);
                }

                try {
                    rs = dmd.getExportedKeys(catalogName, schemaName, tableName);
                    tableRKeysModel = resultSetToDataModel(rs);
                    tableRKeys.setModel(tableRKeysModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tableRKeysModel = tableErrorModel;
                    tableRKeys.setModel(tableRKeysModel);
                }

                try {
                    rs = dmd.getImportedKeys(catalogName, schemaName, tableName);
                    tableIKeysModel = resultSetToDataModel(rs);
                    tableIKeys.setModel(tableIKeysModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tableIKeysModel = tableErrorModel;
                    tableIKeys.setModel(tableIKeysModel);
                }

                try {
                    rs = dmd.getTablePrivileges(catalogName, schemaName, tableName);
                    tablePrevilegesModel = resultSetToDataModel(rs);
                    tablePrevileges.setModel(tablePrevilegesModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tablePrevilegesModel = tableErrorModel;
                    tablePrevileges.setModel(tablePrevilegesModel);
                }

                try {
                    rs = dmd.getSuperTables(catalogName, schemaName, tableName);
                    tableSuperModel = resultSetToDataModel(rs);
                    tableSuper.setModel(tableSuperModel);
                } catch (Throwable e) {
                    e.printStackTrace();
                    tableSuperModel = tableErrorModel;
                    tableSuper.setModel(tableSuperModel);
                }
                tableColumns.revalidate();
                tableColumns.repaint();
                tablePKeys.revalidate();
                tablePKeys.repaint();
                tableRKeys.revalidate();
                tableRKeys.repaint();
                tableIKeys.revalidate();
                tableIKeys.repaint();
                tablePrevileges.revalidate();
                tablePrevileges.repaint();
                tableSuper.revalidate();
                tableSuper.repaint();
                tableDisplayDialog.setTitle("Table Info for " + tableName);
                tableDisplayDialog.setVisible(true);
            }
        } else if (source == tableToSaveList) {
            if (evt.getClickCount() > 1) {
                removeTableToSave();
            }
        } else if (source == availableTableList) {
            if (evt.getClickCount() > 1) {
                addTableToSave();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void keyPressed(KeyEvent evt) {
        Object source = evt.getSource();
        int keycode = evt.getKeyCode();
        if (source == consoleQueryField) {
            if (!consoleMode) return;
            switch (keycode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_NUMPAD8:
                    if (commandHistoryIndex > 0) {
                        commandHistoryIndex--;
                        consoleQueryField.setText(commandHistory.get(commandHistoryIndex).toString());
                    } else {
                        consoleQueryField.setText(""); // $NON-NLS-1$
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_NUMPAD2:
                    if (commandHistoryIndex < commandHistory.size()) {
                        consoleQueryField.setText(commandHistory.get(commandHistoryIndex).toString());
                        commandHistoryIndex++;
                    } else {
                        consoleQueryField.setText(""); // $NON-NLS-1$
                    }
                    break;
            }
        } else if (source == connectButton || source == driver || source == url || source == login || source == passwd) {
            if (keycode == KeyEvent.VK_ESCAPE) connectDialog.setVisible(false);
        } else if (source == infoArea) {
            if (keycode == KeyEvent.VK_ESCAPE) infoDialog.setVisible(false);
        } else if (source == tableList) {
            if (keycode == KeyEvent.VK_ESCAPE) tableDialog.setVisible(false);
        } else if (source == tableColumns || source == tablePKeys || source == tableRKeys || source == tableIKeys || source == tableSuper || source == tablePrevileges) {
            if (keycode == KeyEvent.VK_ESCAPE) tableDisplayDialog.setVisible(false);
        } else if (source == aboutTab || source == aboutPane || source == licenseArea) {
            if (keycode == KeyEvent.VK_ESCAPE) aboutDialog.setVisible(false);
        }
    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

    public void windowClosing(WindowEvent evt) {
        if (owner == null) {
            disconnect();
            //            System.exit(0);
        }
        owner.setEnabled(true);
        setVisible(false);
        owner = null;
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }


    public static void main(String args[]) {
        Settings.loadEnglishGUISettings();

        ParameterTable params = Parameter.process(args);
        loginString = params.getString("-l"); // $NON-NLS-1$
        passwdString = params.getString("-p"); // $NON-NLS-1$
        String url = params.getString("-u"); // $NON-NLS-1$
        String driver = params.getString("-d"); // $NON-NLS-1$
        if (url != null) urlString = url;
        if (driver != null) driverString = driver;

        DatabaseGUI gui = new DatabaseGUI(null);
        gui.show();
    }
}



