package org.geworkbench.components.normalization;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import org.geworkbench.analysis.AbstractSaveableParameterPanel;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.*;
import org.geworkbench.events.GeneSelectorEvent;
import java.util.Enumeration;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
  // public class HouseKeepingGeneNormalizerPanel extends JPanel implements    Serializable {
  public class HouseKeepingGeneNormalizerPanel extends AbstractSaveableParameterPanel implements Serializable {
    public HouseKeepingGeneNormalizerPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saveButtonPressed(TreePath path) {

        if (panel != null) {
            JFileChooser fc = new JFileChooser(".");
            FileFilter filter = new MarkerPanelSetFileFilter();
            fc.setFileFilter(filter);
            fc.setDialogTitle("Save Marker Panel");
            String extension = ((MarkerPanelSetFileFilter) filter).getExtension();
            int choice = fc.showSaveDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getAbsolutePath();
                if (!filename.endsWith(extension)) {
                    filename += extension;
                }
                boolean confirmed = true;
                if (new File(filename).exists()) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Replace existing file?");
                    if (confirm != JOptionPane.YES_OPTION) {
                        confirmed = false;
                    }
                }
                if (confirmed) {
                    saveToText(filename);
                }
            }
        }
    }

    /**
     * saveToText
     *
     * @param filename String
     */
    public void saveToText(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
                    filename)));
            String line = null;
            if(selectedModel.size()==0){
                reportError("No gene is selected", null);
                return;
            }

            for(int i=0; i<selectedModel.size(); i++){
                line = (String)selectedModel.getElementAt(i);
                    writer.write(line);
                    writer.newLine();
                }

            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Utility to save a panel to the filesystem.
     *
     * @param filename filename to which the current panel is to be saved.
     */
    private void serializePanel(String filename, DSPanel<DSGeneMarker> panel) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
                    filename)));
            String line = null;
            if (panel != null && panel.size() > 0) {
                line = "Label\t" + panel.getLabel();
                writer.write(line);
                writer.newLine();
                line = "MinorLabel\t" + panel.getSubLabel();
                writer.write(line);
                writer.newLine();
                line = "MarkerType\t" + panel.get(0).getClass().getName();
                writer.write(line);
                writer.newLine();
                for (int i = 0; i < panel.size(); i++) {
                    DSGeneMarker marker = (DSGeneMarker) panel.get(i);
                    line = marker.getSerial() + "\t" + marker.getLabel() + "\t" +
                           marker.getDescription();
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadButtonPressed() {
        JFileChooser fc = new JFileChooser(".");
        javax.swing.filechooser.FileFilter filter = new
                MarkerPanelSetFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Load new housekeeping Genes");
        int choice = fc.showOpenDialog(mainPanel.getParent());
        if (choice == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getAbsolutePath();
            try {
                InputStream input2 = new FileInputStream(filename);
                populateList(input2);

            } catch (Exception ex) {
                reportError(ex.getMessage(), null);
            }

        }
            System.out.println("one");
    }

    private void markerClicked(int index, MouseEvent e) {
        throwEvent(GeneSelectorEvent.MARKER_SELECTION);
    }

    private void throwEvent(int type) {
        org.geworkbench.events.GeneSelectorEvent event = null;
        switch (type) {
        case GeneSelectorEvent.PANEL_SELECTION:
            event = new GeneSelectorEvent(markerPanel);
            break;
        case org.geworkbench.events.GeneSelectorEvent.MARKER_SELECTION:
            int index = -1;
            if (index != -1) {
                if (markerList != null) {
                    event = new GeneSelectorEvent(markerList.get(index));
                }
            }
            break;
        }

    }

    private void markerDoubleClicked(int index, MouseEvent e) {
        // Get double-clicked marker
        DSGeneMarker marker = markerList.get(index);
        if (markerPanel.getSelection().contains(marker)) {
            markerPanel.getSelection().remove(marker);
        } else {
            markerPanel.getSelection().add(marker);
        }
        // treeModel.firePanelChildrenChanged(markerPanel.getSelection());
        throwEvent(GeneSelectorEvent.PANEL_SELECTION);
    }

    private void markerRightClicked(int index, final MouseEvent e) {
        //ensureItemIsSelected(index);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //   geneListPopup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }


    /**
     * Utility to obtain the stored panel sets from the filesystem
     *
     * @param filename filename which contains the stored panel set
     */
    private DSPanel<DSGeneMarker> deserializePanel(final String filename) {
        BufferedReader stream = null;
        try {
            //stream = new BufferedReader(new InputStreamReader(new ProgressMonitorInputStream(getComponent(), "Loading probes " + filename, new FileInputStream(filename))));
            String line = null;
            DSPanel<DSGeneMarker> panel = new CSPanel<DSGeneMarker>();
            Class type = null;
            while ((line = stream.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens != null && tokens.length == 2) {
                    if (tokens[0].trim().equalsIgnoreCase("Label")) {
                        panel.setLabel(new String(tokens[1].trim()));
                    } else if (tokens[0].trim().equalsIgnoreCase("MinorLabel")) {
                        panel.setSubLabel(new String(tokens[1].trim()));
                    } else if (tokens[0].trim().equalsIgnoreCase("MarkerType")) {
                        type = Class.forName(tokens[1].trim());
                    } else {
                    }
                }
                if (tokens != null && tokens.length == 3) {
                    if (type != null) {
                        DSGeneMarker marker = (DSGeneMarker) type.newInstance();
                        if (marker != null) {
                            marker.setSerial(Integer.parseInt(tokens[0].trim()));
                            marker.setLabel(new String(tokens[1].trim()));
                            marker.setDescription(new String(tokens[2].trim()));
                            panel.add(marker);
                        }
                    }
                }
            }
            return panel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * <code>FileFilter</code> that is used by the <code>JFileChoose</code> to
     * show just panel set files on the filesystem
     */
    private static class MarkerPanelSetFileFilter extends javax.swing.
            filechooser.FileFilter {
        String fileExt;

        MarkerPanelSetFileFilter() {
            fileExt = ".csv";
        }

        public String getExtension() {
            return fileExt;
        }

        public String getDescription() {
            return "csv";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            if (f.isDirectory() || f.getName().endsWith(fileExt)) {
                return true;
            }
            return returnVal;
        }
    }


    /**
     * List Model backed by the marker item list.
     */
    private class MarkerListModel extends AbstractListModel {

        public int getSize() {
            if (markerList == null) {
                return 0;
            }
            return markerList.size();
        }

        public Object getElementAt(int index) {
            if (markerList == null) {
                return null;
            }
            return markerList.get(index);
        }

        public DSGeneMarker getMarker(int index) {
            return markerList.get(index);
        }

        /**
         * Indicates to the associated JList that the contents need to be redrawn.
         */
        public void refresh() {
            if (markerList == null) {
                fireContentsChanged(this, 0, 0);
            } else {
                fireContentsChanged(this, 0, markerList.size());
            }
        }

    }


    /**
     * Auto-scrolling list for markers that customizes the double-click and right-click behavior.
     */
    private class MarkerList extends org.geworkbench.util.JAutoList {

        public MarkerList(ListModel model) {
            super(model);
        }

        @Override protected void elementDoubleClicked(int index, MouseEvent e) {
            markerDoubleClicked(index, e);
        }

        @Override protected void elementRightClicked(int index, MouseEvent e) {
            markerRightClicked(index, e);
        }

        @Override protected void elementClicked(int index, MouseEvent e) {
            markerClicked(index, e);
        }

    }


    private class ListCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list,
                    value, index, isSelected, cellHasFocus);
            if (!isSelected) {
                if (markerPanel.getSelection().contains(markerList.get(index))) {
                    component.setBackground(Color.YELLOW);
                }
            }
            return component;
        }
    }


    public void reportError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title,
                                      JOptionPane.ERROR_MESSAGE);
    }

    void populateList(InputStream input) throws FileNotFoundException,
            IOException {

        BufferedReader br = null;

        br = new BufferedReader(new InputStreamReader(input));

        HashMap factors = new HashMap();
        String line = null;


        while ((line = br.readLine()) != null) {

            String[] cols = line.split(",");

             markerModel.addElement(cols[0]);
            factors.put(cols[0], cols[1]);
            markerList.add(new CSGeneMarker(cols[0]));
        }
        //jList1.setModel(ls);
        System.out.println(markerModel.size() + " " + markerList.size());
        br.close();
        revalidate();
        repaint();

//        jList1.setToolTipText("HouseKeeping genes list");
//        jList1.setModel(ls);
//        jList1.addMouseListener(new java.awt.event.
//                                MouseAdapter() {
//            public void mouseClicked(MouseEvent e) {
//                markerList_mouseClicked(e);
//            }
//
//        });

    }

    public void markerList_mouseClicked(MouseEvent e) {
        int index = jList1.locationToIndex(e.getPoint());
        if (e.getClickCount() == 2) {
            String value = (String) markerModel.getElementAt(index);
            addMarkers(value);
        }
    }

    /**
     * Add one marker into the selected list.
     * @param markerName String
     */
    public void addMarkers(String markerName) {
        if(!selectedModel.contains(markerName)){
            selectedModel.addElement(markerName);
        }
    }

    /**
     * Remove marker from selected list.
     * @param markerName String
     */
    public void removeMarkers(String markerName) {

        selectedModel.removeElement(markerName);

    }


    private void jbInit() throws Exception {
        this.setLayout(xYLayout1);
        markerList = new CSItemList<DSGeneMarker>();
        panel = new CSPanel<DSGeneMarker>();
        jButton1.setText(">>");
        jButton2.setText("<<");
        jPanel3.setLayout(xYLayout2);
        jLabel1.setText("Selected genes");
        jLabel2.setText("HouseKeeping Genes List");
        jButton5.setText("Clear all");
        jButton5.addActionListener(new
                HouseKeepingGeneNormalizerPanel_jButton5_actionAdapter(this));
        jPanel4.setLayout(borderLayout1);
        loadButton.setText("Load from file");
        loadButton.addActionListener(new
                                     HouseKeepingGeneNormalizerPanel_loadButton_actionAdapter(this));
        jPanel1.add(jButton1);
        jPanel1.add(jButton2);
        jPanel1.add(jButton5);
        this.add(jPanel4, new XYConstraints(2, 0, 277, 31));
        jPanel3.add(jScrollPane1, new XYConstraints(2, 3, 86, 128));
        jPanel3.add(jPanel1, new XYConstraints(90, 3, 76, 128));
        //jList2.setModel(selectedModel);
        jList1 = new JList(samples);
       jList1.setToolTipText("HouseKeeping genes list");
       markerModel =  new DefaultListModel();
       jList1 = new JList(markerModel);//(DefaultListModel) jList1.getListModel();

      jList1.addMouseListener(new java.awt.event.
                              MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
              markerList_mouseClicked(e);
          }

      });

        jScrollPane2.getViewport().add(jList2);
        jScrollPane1.getViewport().add(jList1);

        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);
        jPanel4.add(jLabel1, java.awt.BorderLayout.EAST);
        this.add(jPanel2, new XYConstraints(0, 168, 277, 36));
        jPanel2.add(loadButton);
        this.add(jPanel3, new XYConstraints(0, 29, 277, 140));
        jPanel3.add(jScrollPane2, new XYConstraints(164, 3, 81, 128));
        InputStream input = HouseKeepingGeneNormalizer.class.
                            getResourceAsStream(
                                    "DEFAULT_HOUSEKEEPING_GENES.txt");



        populateList(input);
    }

    XYLayout xYLayout1 = new XYLayout();
    String[] samples = {"AFFX-BioB-M_at", "31463_s_at"};
    JScrollPane jScrollPane1 = new JScrollPane();
    JScrollPane jScrollPane2 = new JScrollPane();
    JList jList1;
    JPanel jPanel1 = new JPanel();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JPanel jPanel2 = new JPanel();

    JPanel jPanel3 = new JPanel();
    XYLayout xYLayout2 = new XYLayout();
    JPanel jPanel4 = new JPanel();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jButton5 = new JButton();
    BorderLayout borderLayout1 = new BorderLayout();
    // Data models
    private DSItemList<DSGeneMarker> markerList;
    private DSPanel<DSGeneMarker> panel;
    private DSPanel<DSGeneMarker> markerPanel;
    DefaultListModel selectedModel = new DefaultListModel();
    DefaultListModel markerModel = new DefaultListModel();
    JPanel mainPanel = new JPanel();
    JButton loadButton = new JButton();
    JList jList2 = new JList(selectedModel);


    public void jButton3_actionPerformed(ActionEvent e) {
        saveButtonPressed(null);
    }

    public DSPanel getMarkerPanel() {
        return markerPanel;
    }

    public DSPanel getPanel() {
        updatePanel();

        return panel;
    }

    /**
     * updatePanel
     */
    private void updatePanel() {

        for (Enumeration en = selectedModel.elements(); en.hasMoreElements();) {

                        CSGeneMarker csg = new CSGeneMarker((String) en.nextElement());
                        panel.add(csg);

        }
    }

    public void setMarkerPanel(DSPanel markerPanel) {
        this.markerPanel = markerPanel;
    }

    public void setPanel(DSPanel panel) {
        this.panel = panel;
    }

    public void loadButton_actionPerformed(ActionEvent e) {
        loadButtonPressed();
    }

    public void jButton5_actionPerformed(ActionEvent e) {
        selectedModel.clear();
    }
}


class HouseKeepingGeneNormalizerPanel_jButton5_actionAdapter implements
        ActionListener {
    private HouseKeepingGeneNormalizerPanel adaptee;
    HouseKeepingGeneNormalizerPanel_jButton5_actionAdapter(
            HouseKeepingGeneNormalizerPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton5_actionPerformed(e);
    }
}


class HouseKeepingGeneNormalizerPanel_loadButton_actionAdapter implements
        ActionListener {
    private HouseKeepingGeneNormalizerPanel adaptee;
    HouseKeepingGeneNormalizerPanel_loadButton_actionAdapter(
            HouseKeepingGeneNormalizerPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.loadButton_actionPerformed(e);
    }
}
