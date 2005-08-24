package org.geworkbench.components.normalization;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
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
 //public class HouseKeepingGeneNormalizerPanel extends JPanel implements Serializable {
   public class HouseKeepingGeneNormalizerPanel extends AbstractSaveableParameterPanel implements Serializable {
    public HouseKeepingGeneNormalizerPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
JButton jButton3 = new JButton();


    private void saveButtonPressed(TreePath path) {

        if (panel != null) {
            JFileChooser fc = new JFileChooser(".");
            FileFilter filter = new MarkerPanelSetFileFilter();
            fc.setFileFilter(filter);
            fc.setDialogTitle("Save Housekeeping Genes Panel");
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
            if (selectedModel.size() == 0) {
                reportError("No gene is selected", null);
                return;
            }

            for (int i = 0; i < selectedModel.size(); i++) {
                line = (String) selectedModel.getElementAt(i);
                writer.write(line);
                writer.newLine();
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
                 ex.printStackTrace();
                reportError(ex.toString(), null);
            }

        }

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

            for (String s : cols) {
                selectedModel.addElement(s);
            }

            markerList.add(new CSGeneMarker(cols[0]));
        }
        //jList1.setModel(ls);

        br.close();
      }

    public void markerList_mouseClicked(MouseEvent e) {
        int index = jList1.locationToIndex(e.getPoint());
        if (e.getClickCount() == 2) {
            String value = (String) markerModel.getElementAt(index);
            addMarkers(value);
        }
    }


    public void List_mouseClicked(MouseEvent e) {
        int index = jList2.locationToIndex(e.getPoint());
        if (e.getClickCount() == 2) {
            String value = (String) selectedModel.getElementAt(index);
            moveMarkers(value);
        }
    }

    /**
     * moveMarkers
     *
     * @param value String
     */
    public void moveMarkers(String value) {
        if (!markerModel.contains(value)) {
            markerModel.addElement(value);
        }
        selectedModel.removeElement(value);

    }

    /**
     * Add one marker into the selected list.
     * @param markerName String
     */
    public void addMarkers(String markerName) {
        if (!selectedModel.contains(markerName)) {
            selectedModel.addElement(markerName);
        }
        markerModel.removeElement(markerName);
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
        jButton1.setText(">");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jButton2.setText("<");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        jPanel3.setLayout(xYLayout2);
        jLabel1.setText("Current Selected Genes");
        jLabel2.setText("Excluded HouseKeeping Genes");
        jButton5.setText("Clear all");
        jButton5.addActionListener(new
                                   HouseKeepingGeneNormalizerPanel_jButton5_actionAdapter(this));
        jPanel4.setLayout(borderLayout1);
        loadButton.setToolTipText("Load housekeeping genes from a file.");
        loadButton.setText("Load");
        loadButton.addActionListener(new
                                     HouseKeepingGeneNormalizerPanel_loadButton_actionAdapter(this));
        jButton3.setToolTipText("Save the housekeeping genes into a file.");
        jButton3.setText("Save");
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton3_actionPerformed(e);
            }
        });
        jPanel1.add(jButton1);
        jPanel1.add(jButton2);
        jPanel1.add(jButton5);
        jPanel1.add(loadButton);
        jPanel1.add(jButton3);
        this.add(jPanel4, new XYConstraints(2, 0, 277, 31));
        jPanel3.add(jScrollPane1, new XYConstraints(2, 3, 86, 128));
        //jList2.setModel(selectedModel);
        jList1 = new JList(samples);
        jList1.setToolTipText("HouseKeeping genes list");
        markerModel = new DefaultListModel();
        jList1 = new JList(markerModel); //(DefaultListModel) jList1.getListModel();

        jList1.addMouseListener(new java.awt.event.
                                MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                markerList_mouseClicked(e);
            }

        });
        jList2.addMouseListener(new java.awt.event.
                                MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                List_mouseClicked(e);
            }

        });

        jScrollPane2.getViewport().add(jList2);
        jScrollPane1.getViewport().add(jList1);

        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);
        jPanel4.add(jLabel1, java.awt.BorderLayout.EAST);
        this.add(jPanel2, new XYConstraints(0, 168, 277, 36));
        this.add(jPanel3, new XYConstraints(0, 29, 277, 140));
        jPanel3.add(jScrollPane2, new XYConstraints(170, 3, 75, -1));
        jPanel3.add(jPanel1, new XYConstraints(90, 3, 71, 128));
        InputStream input = HouseKeepingGeneNormalizer.class.
                            getResourceAsStream(
                                    "DEFAULT_HOUSEKEEPING_GENES.txt");

        populateList(input);
    }



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
        panel = new CSPanel<DSGeneMarker>();
        for (Enumeration en = selectedModel.elements(); en.hasMoreElements(); ) {

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
        markerModel.clear();
    }

    public void jButton1_actionPerformed(ActionEvent e) {
        Object[] selectedGenes =  jList1.getSelectedValues();
        if (selectedGenes != null) {
            for (Object selected : selectedGenes) {
                markerModel.removeElement(selected);
                selectedModel.addElement(selected);
            }
        } else {
            reportError("No gene is selected", "No selection");
        }
    }

    public void jButton2_actionPerformed(ActionEvent e) {
        Object[] selectedGenes =   jList2.getSelectedValues();
        if (selectedGenes != null) {
            for (Object selected : selectedGenes) {
                selectedModel.removeElement(selected);
                markerModel.addElement(selected);
            }
        } else {
            reportError("No gene is selected", "No selection");
        }

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
