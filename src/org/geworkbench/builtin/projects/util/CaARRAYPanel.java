package org.geworkbench.builtin.projects.util;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import gov.nih.nci.common.search.*;
import gov.nih.nci.common.search.SearchCriteria;
import gov.nih.nci.common.search.SearchResult;
import gov.nih.nci.common.search.session.SecureSession;
import gov.nih.nci.common.search.session.SecureSessionFactory;
import gov.nih.nci.mageom.domain.BioAssay.BioAssay;
import gov.nih.nci.mageom.domain.BioAssay.impl.*;
import gov.nih.nci.mageom.domain.BioAssayData.BioAssayData;
import gov.nih.nci.mageom.domain.Description.Description;
import gov.nih.nci.mageom.domain.Experiment.Experiment;
import gov.nih.nci.mageom.domain.Experiment.impl.ExperimentImpl;
import gov.nih.nci.mageom.search.SearchCriteriaFactory;
import gov.nih.nci.mageom.search.Experiment.ExperimentSearchCriteria;
import org.geworkbench.bison.parsers.resources.CaArrayResource;
import org.geworkbench.builtin.projects.LoadData;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public class CaARRAYPanel extends JPanel {

    /**
     * Stores experiments from the remote server.
     */
    protected CaArrayExperiment[] experiments = null;

    /**
     * Used to avoid querying the server for all experiments all the time.
     */
    protected boolean experimentsLoaded = false;

    private JPanel jPanel6 = new JPanel();
    private GridLayout grid4 = new GridLayout();
    private JPanel jPanel7 = new JPanel();
    private JPanel jPanel8 = new JPanel();
    private BorderLayout borderLayout4 = new BorderLayout();
    private BorderLayout borderLayout7 = new BorderLayout();
    private Border border1 = null;
    private Border border2 = null;
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JPanel jPanel10 = new JPanel();
    private JLabel jLabel4 = new JLabel();
    private JScrollPane jScrollPane2 = new JScrollPane();
    private JPanel jPanel14 = new JPanel();
    private JPanel jPanel16 = new JPanel();
    private JLabel measuredLabel = new JLabel("Measured");
    private JLabel derivedLabel = new JLabel("Number of Derived Assays");
    private JTextField measuredField = new JTextField();
    private JTextField derivedField = new JTextField();
    private JTextArea experimentInfoArea = new JTextArea();
    private JPanel jPanel13 = new JPanel();
    private JButton openButton = new JButton();
    private JButton cancelButton = new JButton();
    private LoadData parent = null;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            "caARRAY experiments");
    private DefaultTreeModel remoteTreeModel = new DefaultTreeModel(root);
    private JTree remoteFileTree = new JTree(remoteTreeModel);
    private JPopupMenu jRemoteDataPopup = new JPopupMenu();
    private JMenuItem jGetRemoteDataMenu = new JMenuItem();
    private boolean connectionSuccess = true;
    private String user;
    private String passwd;
    private String url;


    public CaARRAYPanel(LoadData p) {
        parent = p;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        border1 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
        border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
        grid4.setColumns(2);
        grid4.setHgap(10);
        grid4.setRows(1);
        grid4.setVgap(10);
        jLabel4.setMaximumSize(new Dimension(200, 15));
        jLabel4.setMinimumSize(new Dimension(200, 15));
        jLabel4.setPreferredSize(new Dimension(200, 15));
        jLabel4.setText("Experiment Information:");

        jPanel13.setLayout(new BoxLayout(jPanel13, BoxLayout.X_AXIS));
        jPanel13.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        derivedField.setMinimumSize(new Dimension(8, 20));
        derivedField.setPreferredSize(new Dimension(8, 20));
        jPanel13.add(Box.createHorizontalGlue());
        jPanel13.add(openButton);
        jPanel13.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanel13.add(cancelButton);

        measuredField.setEditable(false);

        openButton.setPreferredSize(new Dimension(73, 25));
        openButton.setText("Open");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRemoteFile_action(e);
            }
        });
        cancelButton.setMinimumSize(new Dimension(73, 25));
        cancelButton.setPreferredSize(new Dimension(73, 25));
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });

        jPanel16.setLayout(new BoxLayout(jPanel16, BoxLayout.Y_AXIS));
        jPanel16.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel16.add(Box.createVerticalGlue());
        jPanel16.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanel16.add(derivedLabel);
        jPanel16.add(derivedField);
        jPanel16.add(Box.createRigidArea(new Dimension(10, 0)));
        derivedField.setEditable(false);
        jScrollPane2.getViewport().add(experimentInfoArea, null);
        jScrollPane2.setPreferredSize(new Dimension(300, 300));
        jPanel14.setLayout(new BoxLayout(jPanel14, BoxLayout.X_AXIS));
        jPanel14.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel14.add(Box.createHorizontalGlue());
        jPanel14.add(Box.createRigidArea(new Dimension(10, 0)));
        jPanel14.add(Box.createRigidArea(new Dimension(10, 0)));
        experimentInfoArea.setPreferredSize(new Dimension(300, 300));
        experimentInfoArea.setText("");
        experimentInfoArea.setEditable(false);
        experimentInfoArea.setLineWrap(true);
        experimentInfoArea.setWrapStyleWord(true);

        jPanel6.setLayout(grid4);
        jPanel8.setLayout(borderLayout4);
        jPanel7.setLayout(borderLayout7);
        jPanel10.setLayout(new BoxLayout(jPanel10, BoxLayout.Y_AXIS));
        jPanel10.add(jLabel4, null);
        jPanel10.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel10.add(jScrollPane2, null);
        jPanel10.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel10.add(jPanel14, null);
        jPanel14.add(jPanel16);
        jPanel10.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        jPanel7.setBorder(border1);
        jPanel8.setBorder(border2);
        jScrollPane1.getViewport().add(remoteFileTree, null);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel8.add(jScrollPane1, BorderLayout.CENTER);
        jPanel7.add(jPanel10, BorderLayout.CENTER);
        jPanel7.add(jPanel13, BorderLayout.SOUTH);
        jPanel6.setMaximumSize(new Dimension(675, 339));
        jPanel6.setMinimumSize(new Dimension(675, 339));
        jPanel6.setPreferredSize(new Dimension(675, 339));
        jPanel6.add(jPanel8);
        jPanel6.add(jPanel7);

        remoteFileTree.setToolTipText("");
        remoteFileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.
                DISCONTIGUOUS_TREE_SELECTION);
        remoteFileTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                remoteFileSelection_action(tse);
            }
        });
        remoteFileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                jRemoteFileTree_mouseReleased(e);
            }
        });
        jGetRemoteDataMenu.setText("Get bioassays");
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jGetRemoteData_actionPerformed(e);
            }
        };
        jGetRemoteDataMenu.addActionListener(listener);
        jRemoteDataPopup.add(jGetRemoteDataMenu);
        jPanel6.setMaximumSize(new Dimension(500, 300));
        jPanel6.setMinimumSize(new Dimension(500, 300));
        jPanel6.setPreferredSize(new Dimension(500, 300));
        this.add(jPanel6, BorderLayout.CENTER);
    }

    /**
     * Action listener invoked when the user presses the "Open" button
     * after having selected a remote microarray. The listener will attempt
     * to get the microarray data from the remote server and load them in the
     * application.
     *
     * @param e
     */
    private void openRemoteFile_action(ActionEvent e) {
        TreePath[] paths = remoteFileTree.getSelectionPaths();
        ExperimentImpl exp = null;
        if (paths.length > 0) {
            exp = ((CaArrayExperiment) ((DefaultMutableTreeNode) paths[0].
                                        getPath()[1]).getUserObject()).
                  getExperiment();
        }
        Vector tempAssays = new Vector();
        boolean multipleAssays = false;
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].
                                          getLastPathComponent();
            if (node != null &&
                (node.getUserObject() instanceof CaArrayBioassay)) {
                if (exp ==
                    ((CaArrayExperiment) ((DefaultMutableTreeNode) node.getPath()[
                                          1]).getUserObject()).getExperiment()) {
                    CaArrayBioassay ba = (CaArrayBioassay) node.getUserObject();
                    if (ba != null) {
                        tempAssays.add(ba.getBioAssayImpl());
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No data in selected experiment " +
                                ba.getBioAssayImpl().getName(),
                                "Remote Open File Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    multipleAssays = true;
                }
            }
        }
        if (multipleAssays) {
            JOptionPane.showMessageDialog(null,
                                          "BioAssay selections only from " +
                                          exp.getName() + " will be used",
                                          "Multiple Experiment Selections",
                                          JOptionPane.WARNING_MESSAGE);
        }

        BioAssayImpl[] assays = new BioAssayImpl[tempAssays.size()];
        assays = (BioAssayImpl[]) tempAssays.toArray(assays);
        if (exp != null && assays.length > 0) {
            parent.parentProjectPanel.remoteFileOpenAction(new CaArrayResource(
                    assays, exp));
        }
        dispose();
        return;
    }

    /**
     * This method is called if the cancel button is pressed.
     *
     * @param e - Event information.
     */
    private void jButton1_actionPerformed(ActionEvent e) {
        dispose();
    }

    public void getExperiments(ActionEvent e) {
        if (!experimentsLoaded) {
            experiments = getExperiments();
            connectionSuccess = true;
            if (experiments == null) {
                connectionSuccess = false;
                JOptionPane.showMessageDialog(null,
                                              "Unable to connect to server.",
                                              "Open File Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < experiments.length; ++i) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                        experiments[i]);
                remoteTreeModel.insertNodeInto(node, root, root.getChildCount());
            }
            remoteFileTree.expandRow(0);
            experimentsLoaded = true;
        }
        this.validate();
        this.repaint();
    }

    /**
     * Menu action listener that brings up the popup menu that allows
     * populating the tree node for a remote file.
     *
     * @param e
     */
    private void jRemoteFileTree_mouseReleased(MouseEvent e) {
        TreePath path = remoteFileTree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            Object selectedNode = ((DefaultMutableTreeNode) path.
                                   getLastPathComponent()).getUserObject();
            if (e.isMetaDown() && (selectedNode instanceof CaArrayExperiment)) {
                jRemoteDataPopup.show(remoteFileTree, e.getX(), e.getY());
                if (((CaArrayExperiment) selectedNode).hasBeenPopulated()) {
                    jGetRemoteDataMenu.setEnabled(false);
                } else {
                    jGetRemoteDataMenu.setEnabled(true);
                }
            }
        }
    }

    /**
     * Prints the reporter related data to standard out.
     *
     * @param reporterData - Array of reporter related data to be printed.
     */
    //    private static void printReporterRelatedData(ReporterRelatedData[] reporterData) {
    //      for (int x = 0; x < reporterData.length; x++) {
    //        System.out.println("\n*********************************************************");
    //        System.out.println("Reporter: " + reporterData[x].getReporterName());
    //        System.out.println("Reporter ID: " + reporterData[x].getReporterId());
    //        BioAssayDatumData[] datum = reporterData[x].getBioAssayDatumData();
    //        for (int y = 0; y < datum.length; y++) {
    //          System.out.println("\tDatum[" + y + "]: '" + datum[y].getType() + "' - " + datum[y].getValue());
    //        }
    //
    //        System.out.println("");
    //        OntologyEntryData[] onts = reporterData[x].getSequenceOntologies();
    //        for (int y = 0; y < onts.length; y++) {
    //          System.out.println("\tOntology[" + y + "]: '" + onts[y].getCategory() + "' - " + onts[y].getValue());
    //        }
    //        System.out.println("*********************************************************\n");
    //      }
    //    }

    /**
     * Action listener invoked when a remote file is selected in the remote file
     * tree. Updates the experiment information text area.
     *
     * @param lse
     */
    private void remoteFileSelection_action(TreeSelectionEvent tse) {
        if (tse == null) {
            return;
        }
        updateTextArea(); // Update the contents of the text area.
    }

    /**
     * Properly update the experiment text area, based on the currently
     * selected node.
     */
    private void updateTextArea() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) remoteFileTree.
                                      getLastSelectedPathComponent();
        if (node == null || node == root || experiments == null ||
            experiments.length == 0) {
            experimentInfoArea.setText("");
            measuredField.setText("");
            derivedField.setText("");
        } else {
            // get the parent experiment.
            CaArrayExperiment exp = (CaArrayExperiment) ((
                    DefaultMutableTreeNode) node.getPath()[1]).getUserObject();
            experimentInfoArea.setText(exp.getExperimentInformation());
            measuredField.setText(String.valueOf(exp.getMeasuredBioassaysCount()));
            derivedField.setText(String.valueOf(exp.getDerivedBioassaysCount()));
        }
        experimentInfoArea.setCaretPosition(0); // For long text.
    }

    /**
     * Populate the subtree corresponding to an Experiment node with data
     * from the remote server.
     *
     * @param e
     */
    private void jGetRemoteData_actionPerformed(ActionEvent e) {
        if (e == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) remoteFileTree.
                                      getLastSelectedPathComponent();
        if (node != null && node != root && experiments != null &&
            experiments.length != 0) {
            // Lazy computation of the children on an experiment.
            DefaultMutableTreeNode assayNode = null;
            DefaultMutableTreeNode dataNode = null;
            if (node.getUserObject() instanceof CaArrayExperiment) {
                CaArrayExperiment exp = (CaArrayExperiment) node.getUserObject();
                // Add in the tree the measured bioassays
                if (!exp.hasBeenPopulated()) {
//                  Supressing display of Measured assays
//                    CaArrayBioassay[] measured = exp.getMeasuredAssays();
//                    if (measured != null && measured.length > 0) {
//                        for (int i = 0; i < measured.length; ++i) {
//                            assayNode = new DefaultMutableTreeNode(measured[i]);
//                            remoteTreeModel.insertNodeInto(assayNode, node, node.getChildCount());
//                        }
//                    }
                    // Add in the tree the derived bioassays
                    CaArrayBioassay[] derived = exp.getDerivedAssays();
                    if (derived != null && derived.length > 0) {
                        for (int i = 0; i < derived.length; ++i) {
                            assayNode = new DefaultMutableTreeNode(derived[i]);
                            remoteTreeModel.insertNodeInto(assayNode, node,
                                    node.getChildCount());
                        }
                    }
                    exp.setPopulated();
                    remoteFileTree.expandPath(new TreePath(node.getPath()));
                }
            }
        }
    }

    /**
     * Gets a list of all experiments available on the remote server.
     */
    public CaArrayExperiment[] getExperiments() {
        CaArrayExperiment[] exps = new CaArrayExperiment[0];
        Vector temp = new Vector();
        try {

//             System.setProperty("RMIServerURL",  "//caarray-mageom-server.nci.nih.gov:8080/SearchCriteriaHandler");
//             System.setProperty("SecureSessionManagerURL",  "//caarray-mageom-server.nci.nih.gov:8080/SecureSessionManager");
//            System.setProperty("RMIServerURL",
//                               "//165.124.152.12:8999/SearchCriteriaHandler");
//            System.setProperty("SecureSessionManagerURL",
//                               "//165.124.152.12:8999/SecureSessionManager");
//            System.setProperty("caarray.mage.user", "columbia");
//            System.setProperty("caarray.mage.password", "mageomapi");

            if (url == null) {
                user = System.getProperty("caarray.mage.user");
                passwd = System.getProperty("caarray.mage.password");
                url = System.getProperty(
                        "SecureSessionManagerURL");
            }
                     System.setProperty("RMIServerURL", url + "/SearchCriteriaHandler");
             System.setProperty("SecureSessionManagerURL",
                              url + "/SecureSessionManager");
//            System.setProperty("caarray.mage.user", user);
//            System.setProperty("caarray.mage.password", password);

            SecureSession sess = SecureSessionFactory.defaultSecureSession();
            url = url + "/SecureSessionManager";
            ((Directable) sess).direct(url);

            sess.start(user, passwd);

            ExperimentSearchCriteria esc = SearchCriteriaFactory.
                                           new_EXPERIMENT_EXPERIMENT_SC();
            esc.setSessionId(sess.getSessionId());
            SearchResult results = esc.search();

            Experiment[] result = (Experiment[]) results.getResultSet();
            for (int x = 0; x < result.length; x++) {
                if (result[x] instanceof ExperimentImpl) {
                    CaArrayExperiment exp = new CaArrayExperiment((
                            ExperimentImpl) result[x]);
                    // This needs to be handled
                    //                    if (exp.isPlatformTypeUsable()) {
                    temp.add(exp);
                    //                    }
                }
            } while (results.hasMore()) {
                SearchCriteria nextcrit = results.getNextCriteria();
                results = nextcrit.search();
                Object[] objects2 = results.getResultSet();
                for (int x = 0; x < objects2.length; x++) {
                    if (objects2[x] instanceof ExperimentImpl) {
                        CaArrayExperiment exp = new CaArrayExperiment((
                                ExperimentImpl) objects2[x]);
                        // This needs to be handled
                        //                        if (exp.isPlatformTypeUsable()) {
                        temp.add(exp);
                        //                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Collections.sort(temp);
        //dump the experiment objects into the return array.
        exps = (CaArrayExperiment[]) temp.toArray(exps);
        return exps;
    }

    private void dispose() {
        parent.dispose();
    }

    /**
     * This class provides a GUI wrapper for a BioAssayImpl object.  This object will
     * be displayed as a node in the JTree.
     */
    class CaArrayBioassay {
        private String m_id = null;
        private BioAssay ba = null;
        private BioAssayData[] baData = null;
        private int dataCount = 0;
        // Convenience flag.
        private boolean countFlag = false;
        // Convenience flag.
        private boolean flag = false;

        public CaArrayBioassay(BioAssay b) {
            ba = b;
            try {
                m_id = b.getIdentifier();
            } catch (Exception e) {
                System.out.println("Error getting id for bioasay: " +
                                   e.toString());
                e.printStackTrace();
            }
        }

        public String getId() {
            String id = "";
            if (m_id != null) {
                return m_id;
            }
            return id;
        }

        public BioAssay getBioAssayImpl() {
            return ba;
        }

        public int getDataCount() {
            if (ba == null) {
                return 0;
            }
            if (countFlag) {
                return dataCount;
            }
            try {
                if (ba instanceof MeasuredBioAssayImpl) {
                    dataCount = ((MeasuredBioAssayImpl) ba).
                                getMeasuredBioAssayDataCount();
                } else if (ba instanceof DerivedBioAssayImpl) {
                    dataCount = ((DerivedBioAssayImpl) ba).
                                getDerivedBioAssayDataCount();
                } else {
                    dataCount = 0;
                }
            } catch (Exception e) {
                return 0;
            }
            countFlag = true;
            return dataCount;
        }

        BioAssayData[] getData() {
            if (ba == null) {
                return null;
            }
            if (flag) {
                return baData;
            }
            try {
                if (ba instanceof MeasuredBioAssayImpl) {
                    baData = ((MeasuredBioAssayImpl) ba).
                             getMeasuredBioAssayData();
                } else if (ba instanceof DerivedBioAssayImpl) {
                    baData = ((DerivedBioAssayImpl) ba).getDerivedBioAssayData();
                } else {
                    baData = null;
                }
            } catch (Exception e) {
                return null;
            }
            flag = true;
            return baData;
        }

        /**
         * Return the display name for the bioassay.
         */
        public String toString() {
            if (ba == null) {
                return "null";
            } else if (ba.getName() != null) {
                return ba.getName();
            } else if (ba instanceof MeasuredBioAssayImpl) {
                StringBuffer name = new StringBuffer("Measured");
                if (m_id != null) {
                    name.append(": ");
                    name.append(m_id);
                }
                return name.toString();
            } else if (ba instanceof DerivedBioAssayImpl) {
                StringBuffer name = new StringBuffer("Derived");
                if (m_id != null) {
                    name.append(":");
                    name.append(m_id);
                }
                return name.toString();
            } else {
                return "Unknown";
            }
        }
    }


    /**
     * This class represents an experiment in the load data JTree.
     */
    class CaArrayExperiment implements Comparable {
        /**
         * The ID of this experiment.
         */
        Long m_id = null;
        /**
         * The name of this experiment.
         */
        String m_name = null;
        String m_platform = "Unknown";
        /**
         * The reference <code>ExperimentImpl</code> object.
         */
        ExperimentImpl exp = null;
        /**
         * The number of measured Bioassays that are part of the
         * experiment.
         */
        private int measuredNum = 0;
        /**
         * The number of derived Bioassays that are part of the
         * experiment.
         */
        private int derivedNum = 0;
        /**
         * Convenience flag
         */
        private boolean expInfoComputed = false;
        /**
         * Convenience flags.
         */
        private boolean hasBeenComputedFlag = false;
        /**
         * The text of the experiment information
         */
        private String experimentInfo = "";
        /**
         * The list of measured bioassays in the experiment.
         */
        private CaArrayBioassay[] measuredAssays = null;
        /**
         * The list of derived bioassays in the experiment.
         */
        private CaArrayBioassay[] derivedAssays = null;
        /**
         * This is set to true when the tree node corresponding to the
         * experiment has been populated with the proper bioassays.
         */
        private boolean treeNodePopulated = false;

        /**
         * Creates a new experiment object that will wrap the specified ExperimentImpl.
         *
         * @param e - The ExperimentImpl that this object represents.
         */
        public CaArrayExperiment(ExperimentImpl e) {
            exp = e;
            //get the experiment id and name and store them locally
            try {
                m_id = e.getId();
                m_name = e.getName();
                //m_platform = e.getPlatformType;
            } catch (Exception ex) {
                System.out.println("Error getting experiment information: " +
                                   ex.toString());
                ex.printStackTrace();
            }
        }

        /**
         * Returns the ID of this experiment if known, -1 otherwise.
         */
        public long getId() {
            long id = -1;
            if (m_id != null) {
                id = m_id.longValue();
            }
            return id;
        }

        /**
         * Returns the platform of this experiment.
         */
        public String getPlatform() {
            return m_platform;
        }

        /**
         * Checks if the platform of this experiment is usable by caWorkbench.
         *
         * @return true if the platform type is usable, false otherwise.
         */
        public boolean isPlatformTypeUsable() {
            boolean ret = false;
            if (isGenepix() || isAffymetrix()) {
                ret = true;
            }
            return ret;
        }

        /**
         * Checks if the experiment platform is Affymetrix.
         *
         * @return <code>true</code> if the experiment platform is Affy.
         *         <code>false</code> otherwise.
         */
        public boolean isAffymetrix() {
            boolean ret = false;
            if (m_platform.equalsIgnoreCase("Affymetrix") ||
                m_platform.equalsIgnoreCase("Affy-MAS 5.0")) {
                ret = true;
            }
            return ret;
        }

        /**
         * Checks if the experiment platform is Genepix.
         *
         * @return <code>true</code> if the experiment platform is Genepix.
         *         <code>false</code> otherwise.
         */
        public boolean isGenepix() {
            boolean ret = false;
            if (m_platform.equalsIgnoreCase("Long Oligo - Spotted") ||
                m_platform.equalsIgnoreCase("cDNA - Spotted")) {
                ret = true;
            }
            return ret;
        }

        /**
         * Return the number of measured bioassays in the experiment.
         *
         * @return
         */
        public int getMeasuredBioassaysCount() {
            computeArrayNum();
            return measuredNum;
        }

        /**
         * Return the number of derived bioassays in the experiment.
         *
         * @return
         */
        public int getDerivedBioassaysCount() {
            computeArrayNum();
            return derivedNum;
        }

        /**
         * Returns the experiment name, which will be displayed within
         * the experiment tree. This method is impicitly invoked by the
         * <code>DefaultMutableTreeNode</code> "hosting" this experiment
         * within the experiment tree.
         *
         * @return The name of this experiment.
         */
        public String toString() {
            StringBuffer nameBuff = new StringBuffer();
            if (m_id != null) {
                nameBuff.append(m_id);
                nameBuff.append(": ");
            }

            if (m_name != null) {
                nameBuff.append(m_name);
            }
            nameBuff.append(" - ");
            nameBuff.append(getPlatform());
            return nameBuff.toString();
        }

        /**
         * This method will compare this Experiment to another object.
         *
         * @param rhs - The object to compare to.
         * @return 0 if the objects are equal or cannot be compared, negative int if this object
         *         has an ID less than that of rhs object, positive otherwise.
         */
        public int compareTo(Object rhs) {
            int ret = 0;
            if (rhs instanceof CaArrayExperiment) {
                ret = (int) (getId() - ((CaArrayExperiment) rhs).getId());
            }
            return ret;
        }

        /**
         * Returns the wrapped ExperimentImpl object.
         */
        public ExperimentImpl getExperiment() {
            return exp;
        }

        /**
         * Return the information associated with this experiment.
         *
         * @return
         */
        public String getExperimentInformation() {
            if (exp != null && !expInfoComputed) {
                Description[] desc = exp.getDescriptions();
                if (desc == null || desc.length == 0) {
                    experimentInfo = "";
                } else {
                    return experimentInfo = desc[0].getText();
                }
                expInfoComputed = true;
            }
            return experimentInfo;
        }

        /**
         * Return an array containing the measured bioassays (if there are any).
         *
         * @return
         */
        public CaArrayBioassay[] getMeasuredAssays() {
            computeArrayNum();
            return measuredAssays;
        }

        /**
         * Return an array containing the derived bioassays (if there are any).
         *
         * @return
         */
        public CaArrayBioassay[] getDerivedAssays() {
            computeArrayNum();
            return derivedAssays;
        }

        public boolean hasBeenPopulated() {
            return treeNodePopulated;
        }

        public void setPopulated() {
            treeNodePopulated = true;
        }

        /**
         * Compute the number of measured and derived bioassays in the
         * experiment.
         */
        private void computeArrayNum() {
            // No need for repeated computation.
            if (hasBeenComputedFlag || exp == null) {
                return;
            }
            try {
                measuredNum = derivedNum = 0;
                BioAssay[] bioassays = exp.getBioAssays();
                if (bioassays.length > 0) {
                    for (int j = 0; j < bioassays.length; ++j) {
                        if (bioassays[j] instanceof MeasuredBioAssayImpl) {
                            measuredNum++;
                        } else if (bioassays[j] instanceof DerivedBioAssayImpl) {
                            derivedNum++;
                        }
                    }
                }

                // Allocate space for the assays.
                if (bioassays.length > 0) {
                    measuredAssays = new CaArrayBioassay[measuredNum];
                    derivedAssays = new CaArrayBioassay[derivedNum];
                    int k = 0, l = 0;
                    for (int j = 0; j < bioassays.length; ++j) {
                        if (bioassays[j] instanceof MeasuredBioAssayImpl) {
                            measuredAssays[k++] = new CaArrayBioassay(bioassays[
                                    j]);
                        } else if (bioassays[j] instanceof DerivedBioAssayImpl) {
                            derivedAssays[l++] = new CaArrayBioassay(bioassays[
                                    j]);
                        }
                    }
                }
                hasBeenComputedFlag = true;
            } catch (Exception e) {
                measuredNum = derivedNum = 0;
                measuredAssays = derivedAssays = null;
            }
        }
    }


    public boolean isConnectionSuccess() {
        return connectionSuccess;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setConnectionSuccess(boolean isConnectionSuccess) {
        this.connectionSuccess = isConnectionSuccess;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
