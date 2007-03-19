package org.geworkbench.builtin.projects.remoteresources.query;

import org.geworkbench.builtin.projects.remoteresources.RemoteResource;
import org.geworkbench.builtin.projects.remoteresources.RemoteResourceDialog;
import org.geworkbench.engine.properties.PropertiesManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Mar 19, 2007
 * Time: 2:53:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class CaARRAYQueryPanel extends JPanel {
    public CaARRAYQueryPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * main
     *
     * @param anObject String[]
     */
    public static void main(String[] anObject) {
        CaARRAYQueryPanel ca = new CaARRAYQueryPanel();
        JFrame frame = new JFrame("Query CaARRAY");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setContentPane(ca);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }


    public static void display(String remoteSourceName) {
        RemoteResource resourceDialog = RemoteResourceDialog.getRemoteResourceManager().getSelectedResouceByName(
                remoteSourceName.trim());
        try {
            if (resourceDialog != null) {
                PropertiesManager properties = PropertiesManager.getInstance();
                properties.setProperty(GeWorkbenchCaARRAYAdaptor.class, GeWorkbenchCaARRAYAdaptor.CAARRAY_USERNAME, resourceDialog.getUsername());
                properties.setProperty(GeWorkbenchCaARRAYAdaptor.class, GeWorkbenchCaARRAYAdaptor.PASSWORD, resourceDialog.getPassword());
                properties.setProperty(GeWorkbenchCaARRAYAdaptor.class, GeWorkbenchCaARRAYAdaptor.SERVERLOCATION, resourceDialog.getUri());

            }
            CaARRAYQueryPanel ca = new CaARRAYQueryPanel();

            JFrame frame = new JFrame("Query CaARRAY");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(ca);
            frame.pack();
            frame.setVisible(true);
        } catch (IOException e) {

        }
    }

    private void jbInit() throws Exception {
        allButton.setText("AND");
        allButton.setToolTipText("The search will based on criteria.");
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new
                CaARRAYQueryPanel_deleteButton_actionAdapter(this));
        clearAllButton.setText("Clear All");
        clearAllButton.addActionListener(new
                CaARRAYQueryPanel_clearAllButton_actionAdapter(this));
        searchButton.setToolTipText("Click here to run the search");
        searchButton.setText("Search");
        searchButton.addActionListener(new
                CaARRAYQueryPanel_searchButton_actionAdapter(this));
        cancelButton.setToolTipText("Cancel the action.");
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new
                CaARRAYQueryPanel_cancelButton_actionAdapter(this));
        jCheckBox1.setToolTipText("");
        jCheckBox1.setText("Delete");
        this.setLayout(borderLayout1);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.
                HORIZONTAL_SCROLLBAR_ALWAYS);
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

        jcatagoryComboBox.addActionListener(new
                CaARRAYQueryPanel_jcatagoryComboBox_actionAdapter(this));
        jPanel1.setBorder(border2);
        jPanel1.setLayout(new BorderLayout());
        chipPlatformNameField.setText("Please enter text here.");
        piTextField.setText("Please enter PI information here.");
        jSplitPane2.add(jScrollPane1, JSplitPane.LEFT);
        jSplitPane2.add(jPanel2, JSplitPane.RIGHT);
        jScrollPane1.add(jList);

        for (String aListContent : listContent) {
            jComboBox1.addItem(aListContent);
        }

        jToolBar1.add(clearAllButton);
        jToolBar1.add(deleteButton);
        jToolBar1.add(Box.createHorizontalStrut(160));
        jToolBar1.add(searchButton);
        jToolBar1.add(cancelButton);
        this.add(jToolBar1, java.awt.BorderLayout.SOUTH);
        jSplitPane1.add(jSplitPane2, JSplitPane.RIGHT);
        jSplitPane1.add(jPanel1, JSplitPane.LEFT);
        jPanel1.add(jcatagoryComboBox, BorderLayout.NORTH);
        JPanel allCheckBoxPanel = new JPanel();
        allCheckBoxPanel.add(allButton, BorderLayout.CENTER);
        jPanel1.add(allCheckBoxPanel);
        this.add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }

    public static final String PINAME = "Principal Investigator";
    public static final String ORGANISM = "Organism";
    public static final String TISSUETYPE = "Tissue Type";
    public static final String CHIPPLATFORM = "Chip Platform";
    public static final boolean INISTATE = false; //The initial state for a value.

    String[] listContent = new String[]{TISSUETYPE, CHIPPLATFORM,
            ORGANISM, PINAME};  //The content of search criteria.
    String currentSelectedContent = null;
    int currentSelectedContentIndex = -1;
    JList jList = new JList(listContent);
    JSplitPane jSplitPane1 = new JSplitPane();
    JPanel jPanel1 = new JPanel();
    JSplitPane jSplitPane2 = new JSplitPane();
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel jPanel2 = new JPanel();
    JComboBox jComboBox1 = new JComboBox();
    JToolBar jToolBar1 = new JToolBar();
    JCheckBox allButton = new JCheckBox();
    JButton deleteButton = new JButton();
    JButton clearAllButton = new JButton();
    JButton searchButton = new JButton();
    JButton cancelButton = new JButton();
    JCheckBox jCheckBox1 = new JCheckBox();
    BorderLayout borderLayout1 = new BorderLayout();
    JComboBox jcatagoryComboBox = new JComboBox(new String[]{
            "Please select one catagory",
            EXPERIMENT});
    static final String EXPERIMENT = "Experiments";
    TitledBorder titledBorder1 = new TitledBorder("");
    Border border1 = BorderFactory.createEtchedBorder(Color.white,
            new Color(165, 163, 151));
    Border border2 = new TitledBorder(border1, "Catagory");
    Border border3 = BorderFactory.createEtchedBorder(Color.white,
            new Color(165, 163, 151));
    Border border4 = new TitledBorder(border3, "Field Selection");
    TitledBorder titledBorder2 = new TitledBorder("");
    Border border5 = BorderFactory.createEtchedBorder(Color.white,
            new Color(165, 163, 151));
    Border border6 = new TitledBorder(border5, "Value");
    valueTableModel tableModel = new valueTableModel();
    boolean loaded = false; //To present whether the values are retrieved already.

    //SelectedValue is a util class for values.
    private class SelectedValue {
        String value;
        boolean selected;

        public SelectedValue(String _value, boolean _selected) {
            value = _value;
            selected = _selected;
        }

        public void setValue(String _value) {
            value = _value;
        }

        public void setSelected(boolean _selected) {
            selected = _selected;
        }

        public boolean getSelected() {
            return selected;
        }

        public String getValue() {
            return value;
        }
    }


    Vector<SelectedValue> valueHits = new Vector<SelectedValue>();
    Vector<SelectedValue> tissueHits = new Vector<SelectedValue>();
    JTextField chipPlatformNameField = new JTextField();
    JTextField piTextField = new JTextField();


    private class valueTableModel extends AbstractTableModel {
        /* array of the column names in order from left to right*/
        final String[] columnNames = {"Value", "Include"};
        private Vector<SelectedValue> hits;

        /* returns the number of columns in table*/
        public int getColumnCount() {
            return columnNames.length;
        }

        /* returns the number of rows in table*/
        public int getRowCount() {
            return (hits.size());
        }

        public void setHits(Vector<SelectedValue> theHits) {
            hits = theHits;
        }

        /* return the header for the column number */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /* get the Object data to be displayed at (row, col) in table*/
        public Object getValueAt(int row, int col) {

            SelectedValue hit = hits.get(row);
            /*display data depending on which column is chosen*/
            switch (col) {
                case 0:
                    return hit.getValue(); //database ID

                case 1:
                    return hit.getSelected();
            }
            return null;
        }

        /*returns the Class type of the column c*/
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*returns if the cell is editable; returns false for all cells in columns except column 6*/
        public boolean isCellEditable(int row, int col) {

            return col >= 1;

        }

        /*detect change in cell at (row, col); set cell to value; update the table */
        public void setValueAt(Object value, int row, int col) {
            SelectedValue hit = hits.get(row);
            hit.setSelected((Boolean) value);
            fireTableCellUpdated(row, col);
        }

    }


    public void jcatagoryComboBox_actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();

        String selectedProgramName = (String) cb.getSelectedItem();

        if (selectedProgramName.equalsIgnoreCase(EXPERIMENT)) {
            jList = new JList(listContent);
            jList.addMouseListener(new CaARRAYQueryPanel_jList_mouseAdapter(this));
            (jScrollPane1.getViewport()).add(jList, null);

        } else {
            jList = new JList();
            (jScrollPane1.getViewport()).add(jList, null);
            clearAllButton_actionPerformed(null);
        }

    }

    /**
     * Method to connect with caArray server to get the predefined values. It should only be called
     * once per session and it would get all required information back not just the selected content.
     */
    public void populateHits() {
        if (!loaded) {

            try {
                GeWorkbenchCaARRAYAdaptor geWorkbenchCaARRAYAdaptor = new
                        GeWorkbenchCaARRAYAdaptor();
                valueHits = new Vector<SelectedValue>();
                tissueHits = new Vector<SelectedValue>();
                String catagory = geWorkbenchCaARRAYAdaptor.matchCatagory(
                        TISSUETYPE);
                Object[] array = geWorkbenchCaARRAYAdaptor.
                        testGetOntologyEntriesByCategory(catagory).
                        toArray();
                for (int i = 0; i < array.length; i++) {
                    SelectedValue se = new SelectedValue((String) array[i], INISTATE);
                    tissueHits.add(i, se);
                }
                catagory = geWorkbenchCaARRAYAdaptor.matchCatagory(ORGANISM);
                array = geWorkbenchCaARRAYAdaptor.
                        testGetOntologyEntriesByCategory(catagory).toArray();
                for (int i = 0; i < array.length; i++) {
                    SelectedValue se = new SelectedValue((String) array[i], INISTATE);
                    valueHits.add(i, se);
                }
                loaded = true;

            } catch (Exception e) {
                GeWorkbenchCaARRAYAdaptor.fail("Cannot connect to server.");
            }
        }
    }


    public void jList_mouseClicked(MouseEvent e) {
        int index = jList.locationToIndex(e.getPoint());
        populateHits();
        if (index >= 0 && index < listContent.length) {
            currentSelectedContent = listContent[index];
            currentSelectedContentIndex = index;
            updateSelectionValues(currentSelectedContent);
            repaint();
        }
    }

    /**
     * Respsond to the change of selected search criteria content.
     *
     * @param selectedCritiria
     */
    public void updateSelectionValues(String selectedCritiria) {
        jPanel2.removeAll();
        if (selectedCritiria.equalsIgnoreCase(CHIPPLATFORM)) {
            jPanel2.add(chipPlatformNameField);
            revalidate();
            repaint();
            return;
        }
        if (selectedCritiria.equalsIgnoreCase(PINAME)) {
            jPanel2.add(piTextField);
            revalidate();
            repaint();
            return;
        }
        JTable jTable = new JTable();
        // jTable.setPreferredScrollableViewportSize(new Dimension(100, 100));
        if (selectedCritiria.equalsIgnoreCase(ORGANISM)) {
            tableModel.setHits(valueHits);
            jTable.setModel(tableModel);
            jPanel2.setLayout(new BorderLayout());
            jPanel2.add(jTable, BorderLayout.CENTER);
            revalidate();
            repaint();
            return;
        }
        if (selectedCritiria.equalsIgnoreCase(TISSUETYPE)) {
            tableModel.setHits(tissueHits);
            jTable.setModel(tableModel);
            jPanel2.setLayout(new BorderLayout());
            jPanel2.add(jTable, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

    }

    /**
     * Clear all contents. To reinstore the content, connect with server is required.
     *
     * @param e
     */
    public void clearAllButton_actionPerformed(ActionEvent e) {
        chipPlatformNameField.setText("");
        piTextField.setText("");
        valueHits = new Vector<SelectedValue>();
        tissueHits = new Vector<SelectedValue>();
        loaded = false;
        jPanel2.removeAll();
        revalidate();
        repaint();
    }

    //What for?

    public void deleteButton_actionPerformed(ActionEvent e) {

    }

    /**
     * Strat the search based on the selection of search criteria.
     *
     * @param e
     */
    public void searchButton_actionPerformed(ActionEvent e) {
        //get parameters.
        String piName = piTextField.getText().trim();
        String chipPlatformName = chipPlatformNameField.getText().trim();
        String speciesValue = "";
        String tissueType = "";
        for (SelectedValue selectedValue : valueHits) {
            if (selectedValue.getSelected()) {
                speciesValue += selectedValue.value;
            }
        }
        for (SelectedValue selectedValue : tissueHits) {
            if (selectedValue.getSelected()) {
                tissueType += selectedValue.value;
            }
        }
        try {
            GeWorkbenchCaARRAYAdaptor geWorkbenchCaARRAYAdaptor = new GeWorkbenchCaARRAYAdaptor();
            boolean conbineSearchCritiria = allButton.isSelected();
            if (conbineSearchCritiria) {

                geWorkbenchCaARRAYAdaptor.setChipTypeName(chipPlatformName);
                geWorkbenchCaARRAYAdaptor.setOrganName(speciesValue);
                geWorkbenchCaARRAYAdaptor.setTissueTypeName(tissueType);
                geWorkbenchCaARRAYAdaptor.setPiName(piName);
            } else {
                switch (currentSelectedContentIndex) {
                    case 0:
                        geWorkbenchCaARRAYAdaptor.setTissueTypeName(tissueType);
                        break;
                    case 1:
                        geWorkbenchCaARRAYAdaptor.setChipTypeName(chipPlatformName);
                        break;
                    case 2:
                        geWorkbenchCaARRAYAdaptor.setOrganName(speciesValue);
                        break;

                    case 3:
                        geWorkbenchCaARRAYAdaptor.setPiName(piName);
                        break;
                }
            }

            geWorkbenchCaARRAYAdaptor.testFiltering(true);

        } catch (Exception er) {
            GeWorkbenchCaARRAYAdaptor.fail("Cannot process the query.");
        }

    }

    public void cancelButton_actionPerformed(ActionEvent e) {

    }
}


class CaARRAYQueryPanel_cancelButton_actionAdapter implements ActionListener {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_cancelButton_actionAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.cancelButton_actionPerformed(e);
    }
}


class CaARRAYQueryPanel_searchButton_actionAdapter implements ActionListener {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_searchButton_actionAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.searchButton_actionPerformed(e);
    }
}


class CaARRAYQueryPanel_deleteButton_actionAdapter implements ActionListener {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_deleteButton_actionAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.deleteButton_actionPerformed(e);
    }
}


class CaARRAYQueryPanel_clearAllButton_actionAdapter implements ActionListener {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_clearAllButton_actionAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.clearAllButton_actionPerformed(e);
    }
}


class CaARRAYQueryPanel_jcatagoryComboBox_actionAdapter implements
        ActionListener {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_jcatagoryComboBox_actionAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jcatagoryComboBox_actionPerformed(e);
    }
}


class CaARRAYQueryPanel_jList_mouseAdapter extends MouseAdapter {
    private CaARRAYQueryPanel adaptee;

    CaARRAYQueryPanel_jList_mouseAdapter(CaARRAYQueryPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jList_mouseClicked(e);
    }
}
