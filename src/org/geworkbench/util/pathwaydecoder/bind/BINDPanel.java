package org.geworkbench.util.pathwaydecoder.bind;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author Ta-tsen Soong
 * @version 1.0
 */

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.AdjacencyMatrixEvent;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.PhenotypeSelectorEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.microarrayutils.MicroarrayVisualizer;
import org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.EvdAdjacencyMatrix;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectSelection;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.VisualPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

public class BINDPanel extends MicroarrayVisualizer implements VisualPlugin {
    private JPanel mainPanel = new JPanel();

    private JButton jButton1 = new JButton();
    private DefaultListModel model = new DefaultListModel();
    private JList jList1 = new JList(model);
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JScrollPane jScrollPane2 = new JScrollPane();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JTextArea jTextArea1 = new JTextArea();
    private JButton jButtonJDBC = new JButton();

    private Connection con;
    private GWgenes gw = new GWgenes();
    private JCheckBox jCheckSelfLoop = new JCheckBox();
    private DefaultListModel queryModel = new DefaultListModel();
    private DefaultListModel interModel = new DefaultListModel();

    private DSGeneMarker markerHead;

    private int queryCnt = 0; // stores the number of existing queries

    private ArrayList queryList = new ArrayList(); // queryList stores the adjacency matrices

    // used to store the interaction types corresponding to the n-th element on the query list
    private ArrayList interactionArrayList = new ArrayList();

    private boolean sentByBind = false;

    private boolean debug = false;
    private boolean bconnection = false;

    JButton jButtonFakeNetwork = new JButton();
    int markerNo = 0;

    JCheckBox jCheckReceiveSelection = new JCheckBox();
    JButton jButtonGenerateNW = new JButton();
    JLabel jLabel3 = new JLabel();
    JList jListQuery = new JList();
    JScrollPane jScrollPane3 = new JScrollPane();
    JLabel jLabel4 = new JLabel();
    JScrollPane jScrollPane4 = new JScrollPane();
    JList jListInteraction = new JList();
    JLabel jLabel5 = new JLabel();
    JButton jButtonDrawNetworkFromQueryList = new JButton();
    JButton jButtonFilterInteraction = new JButton();
    JButton jButtonFilterAndDraw = new JButton();
    JButton jButtonGetNetworkFromCytoscape = new JButton();
    JPopupMenu popupMenuQueryList = new JPopupMenu();
    JMenuItem menuRename = new JMenuItem();
    JMenuItem jMenuMerge = new JMenuItem();
    JMenuItem jMenuDeleteNW = new JMenuItem();
    JMenuItem jMenuDrawNW = new JMenuItem();
    JButton jButtonWholeNetwork = new JButton();
    JMenuItem jMenuListNeighbors = new JMenuItem();
    JTextField jTextTargetGene = new JTextField();
    JLabel jLabelTargetGene = new JLabel();
    JPopupMenu jPopupMenu1 = new JPopupMenu();
    JMenuItem jMenuDeleteProbe = new JMenuItem();
    JProgressBar jProgressBar1 = new JProgressBar();
    JLabel jLabel6 = new JLabel();

    public BINDPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Component getComponent() {
        return mainPanel;
    }

    private void jbInit() throws Exception {
        jButton1.setMaximumSize(new Dimension(300, 23));
        jButton1.setMinimumSize(new Dimension(100, 23));
        jButton1.setPreferredSize(new Dimension(100, 23));
        jButton1.setActionCommand("GeneWays in BioWorks");
        jButton1.setMnemonic('0');
        jButton1.setText("GeneWays in BioWorks");
        mainPanel.setLayout(gridBagLayout1);
        mainPanel.setInputVerifier(null);
        jLabel1.setText("Selected genes/proteins");
        jLabel2.setText("Annotation from GeneWays");
        jScrollPane1.setBorder(BorderFactory.createEtchedBorder());
        jScrollPane2.setBorder(BorderFactory.createEtchedBorder());
        jScrollPane2.setDebugGraphicsOptions(0);
        jList1.addMouseListener(new BINDPanel_jList1_mouseAdapter(this));

        jList1.addPropertyChangeListener(new BINDPanel_jList1_propertyChangeAdapter(this));
        jList1.addKeyListener(new BINDPanel_jList1_keyAdapter(this));
        jButtonJDBC.setToolTipText("");
        jButtonJDBC.setText("Get all aliases");
        jButtonJDBC.addActionListener(new BINDPanel_jButtonJDBC_actionAdapter(this));
        jTextArea1.setText("");
        jCheckSelfLoop.setToolTipText("Show self loops?");
        jCheckSelfLoop.setText("Self Loop");
        jButtonFakeNetwork.setText("Generate Fake Network");
        jButtonFakeNetwork.addActionListener(new BINDPanel_jButtonFakeNetwork_actionAdapter(this));
        jCheckReceiveSelection.setEnabled(true);
        jCheckReceiveSelection.setBorder(null);
        jCheckReceiveSelection.setDoubleBuffered(true);
        jCheckReceiveSelection.setToolTipText("Automatically changes the gene list when the content of the gene " + "selection panel has changed...");
        jCheckReceiveSelection.setFocusPainted(true);
        jCheckReceiveSelection.setMargin(new Insets(0, 0, 0, 0));
        jCheckReceiveSelection.setSelected(true);
        jCheckReceiveSelection.setSelectedIcon(null);
        jCheckReceiveSelection.setText("Auto Update Genes");
        jButtonGenerateNW.setToolTipText("Generate a network of pairwise interactions between the selected " + "genes");
        jButtonGenerateNW.setText("Pairwise Interaction Network");
        jButtonGenerateNW.addActionListener(new BINDPanel_jButtonGenerateNW_actionAdapter(this));
        jLabel3.setText("Total: ");
        jLabel4.setText("Query List");
        jLabel5.setText("Interaction Type");
        jListQuery.setToolTipText("Right click to show the menu");
        jListQuery.setModel(queryModel);
        jListQuery.addMouseListener(new BINDPanel_jListQuery_mouseAdapter(this));
        jButtonDrawNetworkFromQueryList.setText("Draw Network");
        jButtonDrawNetworkFromQueryList.addActionListener(new BINDPanel_jButtonDrawNetworkFromQueryList_actionAdapter(this));
        jListInteraction.setToolTipText("Selected:");
        jListInteraction.setModel(interModel);
        jListInteraction.setSelectedIndex(1);
        jListInteraction.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListInteraction.addMouseListener(new BINDPanel_jListInteraction_mouseAdapter(this));
        jButtonFilterInteraction.setText("Filter");
        jButtonFilterInteraction.addActionListener(new BINDPanel_jButtonFilterInteraction_actionAdapter(this));
        jButtonFilterAndDraw.setMinimumSize(new Dimension(93, 23));
        jButtonFilterAndDraw.setMargin(new Insets(2, 1, 2, 14));
        jButtonFilterAndDraw.setText("Filter and Draw");
        jButtonFilterAndDraw.addActionListener(new BINDPanel_jButtonFilterAndDraw_actionAdapter(this));
        jButtonGetNetworkFromCytoscape.setMaximumSize(new Dimension(93, 23));
        jButtonGetNetworkFromCytoscape.setMinimumSize(new Dimension(93, 23));
        jButtonGetNetworkFromCytoscape.setMargin(new Insets(2, 14, 2, 0));
        jButtonGetNetworkFromCytoscape.setText("Get CyNetwork");
        jButtonGetNetworkFromCytoscape.addActionListener(new BINDPanel_jButtonGetNetworkFromCytoscape_actionAdapter(this));
        menuRename.setName("Rename");
        menuRename.setText("Rename");
        menuRename.addActionListener(new BINDPanel_menuItem1_actionAdapter(this));
        jMenuMerge.setText("Merge Networks");
        jMenuMerge.addActionListener(new BINDPanel_jMenuMerge_actionAdapter(this));
        jMenuDeleteNW.setText("Delete");
        jMenuDeleteNW.addActionListener(new BINDPanel_jMenuDeleteNW_actionAdapter(this));
        jMenuDrawNW.setDebugGraphicsOptions(0);
        jMenuDrawNW.setContentAreaFilled(true);
        jMenuDrawNW.setText("Draw Network");
        jMenuDrawNW.addActionListener(new BINDPanel_jMenuDrawNW_actionAdapter(this));
        jButtonWholeNetwork.setToolTipText("Generating a network containing genes on the loaded chip that interact " + "with the selected nodes");
        jButtonWholeNetwork.setText("Whole Network");
        jButtonWholeNetwork.addActionListener(new BINDPanel_jButtonWholeNetwork_actionAdapter(this));
        jMenuListNeighbors.setText("Show first neighbors");
        jMenuListNeighbors.addActionListener(new BINDPanel_jMenuListNeighbors_actionAdapter(this));
        jLabelTargetGene.setToolTipText("Click on the marker in the gene selection panel or type in the marker " + "name");
        jLabelTargetGene.setText("Target Gene");
        jScrollPane3.setMinimumSize(new Dimension(258, 130));
        jMenuDeleteProbe.addActionListener(new BINDPanel_jMenuDeleteProbe_actionAdapter(this));
        jMenuDeleteProbe.setText("Delete");
        jList1.setToolTipText("Right click to show the menu");
        jLabel6.setText("Progress");
        jProgressBar1.setForeground(Color.green);
        jProgressBar1.setOpaque(false);
        jProgressBar1.setPreferredSize(new Dimension(100, 18));
        jProgressBar1.setToolTipText("");
        jProgressBar1.setStringPainted(false);
        mainPanel.add(jLabel1, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jScrollPane1, new GridBagConstraints(0, 3, 2, 7, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jScrollPane1.getViewport().add(jList1, null);
        mainPanel.add(jButtonJDBC, new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jScrollPane2, new GridBagConstraints(2, 9, 6, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jScrollPane3, new GridBagConstraints(2, 3, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jScrollPane4, new GridBagConstraints(4, 4, 4, 4, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jScrollPane4.getViewport().add(jListInteraction, null);
        mainPanel.add(jLabel2, new GridBagConstraints(2, 8, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jScrollPane3.getViewport().add(jListQuery, null);
        jScrollPane2.getViewport().add(jTextArea1, null);
        mainPanel.add(jButtonFilterAndDraw, new GridBagConstraints(2, 7, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 4));
        mainPanel.add(jLabel4, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        mainPanel.add(jButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jButtonWholeNetwork, new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jButtonDrawNetworkFromQueryList, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jButtonGetNetworkFromCytoscape, new GridBagConstraints(6, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jCheckReceiveSelection, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 5, 0));
        mainPanel.add(jCheckSelfLoop, new GridBagConstraints(6, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), -17, 0));
        mainPanel.add(jButtonGenerateNW, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jButtonFilterInteraction, new GridBagConstraints(2, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jLabel5, new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jTextTargetGene, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 38, 0));
        mainPanel.add(jLabelTargetGene, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jButtonFakeNetwork, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jProgressBar1, new GridBagConstraints(7, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jLabel6, new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        popupMenuQueryList.add(menuRename);
        popupMenuQueryList.addSeparator();
        popupMenuQueryList.add(jMenuDrawNW);
        popupMenuQueryList.add(jMenuMerge);
        popupMenuQueryList.add(jMenuDeleteNW);
        popupMenuQueryList.addSeparator();
        popupMenuQueryList.add(jMenuListNeighbors);
        jPopupMenu1.add(jMenuDeleteProbe);

        // initialize the list with some dummy proteins
        /*
                 for (int i=0; i< 10; i++){
            model.addElement("protein "+ i);
                 }
         */
        //        this.jList1.setModel(model);

        // connect to geneways

        // this.connectDB();

    }

    public void geneSelectorAction(org.geworkbench.events.GeneSelectorEvent gse) {
        // System.out.println("got something from geneSelectorAction in BIND panel");
        // if not automatic update, then return

        if (gse.getGenericMarker() instanceof DSGeneMarker) {
            DSGeneMarker marker = gse.getGenericMarker(); // GeneselectorEvent can be a panel event therefore won't work here,
            if (marker != null) {
                this.jTextTargetGene.setText(marker.getLabel());
            }
        }

        if (this.jCheckReceiveSelection.isSelected() == false) {
            return;
        }

        DSPanel<DSGeneMarker> thesp = gse.getPanel();
        if (thesp != null) {
            model.clear();
            this.markerHead = thesp.get(0);
            for (int i = 0; i < thesp.size(); i++) {
                model.addElement(thesp.get(i));
                // System.out.println("adding "+ thesp.getGenericMarker(i).toString());
            }
            this.jLabel3.setText("Total: " + model.size() + " genes");
            // this.getAllAliases();
            // System.out.println("updating the model here");
            // this.jList1.setModel(model);
        }
    }

    @Subscribe public void receive(PhenotypeSelectorEvent e, Object source) {
        //   System.out.println("got something from phenotypeSelectorAction in BIND panel");
    }

    @Subscribe public void receive(SubpanelChangedEvent spe, Object source) {
        //  System.out.println("got something from subpanelChangedAction in BIND panel");
    }

    @Subscribe @Override public void receive(org.geworkbench.events.ProjectEvent projectEvent, Object source) {
        // System.out.println("got something from receiveProjectSelection in BIND panel");
        super.receive(projectEvent, source);
            ProjectSelection selection = ((ProjectPanel) source).getSelection();
            DSDataSet dataFile = selection.getDataSet();
            if (dataFile instanceof DSMicroarraySet) {
                DSMicroarraySet set = (DSMicroarraySet) dataFile;
                // System.out.println("setting DSMicroarraySet");
                // pathwayMakerPanel.setMicroarraySet(set);
                if (set != null) {
                    this.setMicroarraySet(set);
                    // ??? reset();
                }
            }

        /* if (e.getListModel() instanceof  DefaultListModel){
             this.jList1.setModel(e.getListModel() );
         }*/
    }

    void jList1_propertyChange(PropertyChangeEvent e) {

    }

    void jList1_mouseReleased(MouseEvent e) {
        int pos = ((JList) e.getSource()).getSelectedIndex();
        //      System.out.println(model.getElementAt(pos));
        if (pos < 0) {
            return;
        }

        // show the popup menu
        if (e.getButton() == MouseEvent.BUTTON3) {
            this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
            return;
        }

        show_gene_info(pos);
        //jList2.(IGenericMarker)model.getElementAt(pos));
    }

    /**
     * this gets the gene-gene / protein-protein interaction for this target position
     * and then stores the information in ... GWgenes???
     */
    public void get_gene_info(int pos) {

        DSGeneMarker igm = (DSGeneMarker) model.getElementAt(pos);
        String endl = "\n";
        String tmp = model.getElementAt(pos).toString();

        int s1 = tmp.indexOf("(") + 1;
        int s2 = tmp.indexOf(")");
        String genename = tmp.substring(s1, s2);

        StringBuffer geneways = new StringBuffer(); // output from geneways
        String tmpname = "";

        ArrayList tmpal;
        ArrayList tmpgeneA;
        ArrayList tmpaction;
        ArrayList tmpgeneB;
        int geneApos = -1; // the position on the gene list
        int geneBpos = -1;
        String theaction;

        StringBuffer finaloutput = new StringBuffer();
        StringBuffer selfLoop = new StringBuffer();

        if (igm.getGeneId() > 0) {
            // get the aliases of this gene in GeneWays
            ArrayList gwgenename = this.getGeneNameByLocusLinkID(igm.getGeneId());
            System.out.println("Using LocusLink: ");
            geneways.append(gwgenename.size() + " aliases exist in GeneWays\n");

            for (int i = 0; i < gwgenename.size(); i++) {
                // outputs the aliases to the text area
                geneways.append("(" + (i + 1) + ") " + gwgenename.get(i).toString() + endl);
            }

            geneways.append("\n------ Query Results ------\n");

            // for every alias
            for (int i = 0; i < gwgenename.size(); i++) {
                // a tmp alias for query
                tmpname = gwgenename.get(i).toString();
                geneways.append("Alias #" + (i + 1) + ": " + tmpname + endl);

                // query geneways using this alias
                tmpal = this.getGeneFromGeneways(tmpname);

                geneways.append(tmpal.get(0) + endl); // output string
                tmpgeneA = (ArrayList) tmpal.get(1); // geneA
                tmpaction = (ArrayList) tmpal.get(2); // action type
                tmpgeneB = (ArrayList) tmpal.get(3); // geneB

                // maybe categorize the action types here...

                // now getting the interaction
                for (int j = 0; j < tmpgeneA.size(); j++) {
                    // get the position of geneA on the list (in the left jlist panel)
                    geneApos = this.gw.getClassOfAlias(tmpgeneA.get(j).toString());
                    if (geneApos < 0) {
                        continue;
                    }
                    geneBpos = this.gw.getClassOfAlias(tmpgeneB.get(j).toString());
                    if (geneBpos < 0) {
                        continue;
                    }

                    theaction = tmpaction.get(j).toString();

                    if (geneApos == geneBpos) {
                        // it's a self loop
                        // output the geneA action geneA cases here...
                        // do we add self loops to the interaction list???
                        selfLoop.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        continue;
                    }

                    if (geneApos == pos) {
                        // here geneA!= geneB and is our target gene on the jlist list
                        // our gene -> action -> geneB
                        if (geneBpos < gw.getNumGenes()) {
                            // does not include the case: geneA action geneA
                            // adding this interaction to GWgenes
                            gw.addInteraction(geneApos, theaction, geneBpos);
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                        continue;
                    }

                    if (geneBpos == pos) {
                        // here geneB is our target gene on the list
                        // geneA -> action -> our gene
                        if (geneApos < gw.getNumGenes()) {
                            // does not include the case: geneB action geneB
                            // adding this interaction to Gwgenes
                            gw.addInteraction(geneApos, theaction, geneBpos);
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                    }
                }
            }
        }

        /*
         geneways.append("Using Affy gene name: "+ genename+ endl);
               geneways.append(this.getGeneFromGeneways(genename)+ endl);
         */

        String info = "Name: " + igm.getLabel() + endl + "UniGene ID: " + igm.getUnigene() + endl + "LocusLinkID " + igm.getGeneId() + endl + "Desc: " + igm.getDescription() + endl + "Short name: " + igm.getShortName() + endl + "Serial: " + igm.getSerial() + endl + tmp + endl + "Gene: " + genename + endl + endl + "GeneWays: \n" + geneways + endl + "------- Interaction List ---------" + endl + finaloutput.toString() + endl;
        if (jCheckSelfLoop.isSelected()) {
            info += "------- Self Loop ----------" + endl + selfLoop.toString();
        }

        //  jTextArea1.setText(info);
    }

    void show_gene_info(int pos) {
        DSGeneMarker igm = (DSGeneMarker) model.getElementAt(pos);
        if (gw.aliasFileRead() == false) {
            gw.read();
        }
        String endl = "\n";
        String info = "Name: " + igm.getLabel() + endl + "UniGene ID: " + igm.getUnigene() + endl + "LocusLinkID " + igm.getGeneId() + endl + "Desc: " + igm.getDescription() + endl + "Short name: " + igm.getShortName() + endl + "Serial: " + igm.getSerial() + endl + "Aliases in GeneWays: \n" + myio.array2string(gw.getAliasofSerial(igm.getSerial()), "\n");
        this.jTextArea1.setText(info);
    }

    /**
     * displays the list of interaction in the text area<BR>
     * content: all genes in the selected genes list that <BR>
     * interact with the clicked-on one
     */
    void show_gene_info_old(int pos) {
        // temporarily disabling this function
        if (true) {
            return;
        }

        this.getAllAliases();

        DSGeneMarker igm = (DSGeneMarker) model.getElementAt(pos);
        String endl = "\n";
        String tmp = model.getElementAt(pos).toString();

        if (tmp.length() > 30) {
            jButton1.setText(tmp.substring(0, 30) + "...");
        } else {
            jButton1.setText(tmp);
        }

        int s1 = tmp.indexOf("(") + 1;
        int s2 = tmp.indexOf(")");
        String genename = tmp.substring(s1, s2);

        StringBuffer geneways = new StringBuffer(); // output from geneways
        String tmpname = "";

        ArrayList tmpal;
        ArrayList tmpgeneA;
        ArrayList tmpaction;
        ArrayList tmpgeneB;
        int geneApos = -1; // the position on the gene list
        int geneBpos = -1;

        StringBuffer finaloutput = new StringBuffer();
        StringBuffer selfLoop = new StringBuffer();

        if (igm.getGeneId() > 0) {
            ArrayList gwgenename = this.getGeneNameByLocusLinkID(igm.getGeneId());
            System.out.println("Using LocusLink: ");
            geneways.append(gwgenename.size() + " aliases exist in GeneWays\n");
            for (int i = 0; i < gwgenename.size(); i++) {
                geneways.append("(" + (i + 1) + ") " + gwgenename.get(i).toString() + endl);
            }
            geneways.append("\n------ Query Results ------\n");

            for (int i = 0; i < gwgenename.size(); i++) {
                tmpname = gwgenename.get(i).toString();
                geneways.append("Alias #" + (i + 1) + ": " + tmpname + endl);
                /*geneways += "Using LocusLinkID: " + gwgenename + endl +
                    this.getGeneFromGeneways(gwgenename) + endl;
                 }
                 */
                tmpal = this.getGeneFromGeneways(tmpname);
                geneways.append(tmpal.get(0) + endl);
                tmpgeneA = (ArrayList) tmpal.get(1);
                tmpaction = (ArrayList) tmpal.get(2);
                tmpgeneB = (ArrayList) tmpal.get(3);

                for (int j = 0; j < tmpgeneA.size(); j++) {
                    geneApos = this.gw.getClassOfAlias(tmpgeneA.get(j).toString());
                    if (geneApos < 0) {
                        continue;
                    }
                    geneBpos = this.gw.getClassOfAlias(tmpgeneB.get(j).toString());
                    if (geneBpos < 0) {
                        continue;
                    }

                    if (geneApos == geneBpos) {
                        // output the geneA action geneA cases here...
                        selfLoop.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        continue;
                    }

                    if (geneApos == pos) {
                        // our gene -> action -> geneB
                        if (/*geneBpos>=0 &&*/geneBpos < gw.getNumGenes()) {
                            // does not include the case: geneA action geneA
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                        continue;
                    }

                    if (geneBpos == pos) {
                        // geneA -> action -> our gene
                        if (/*geneApos>=0 && */geneApos < gw.getNumGenes()) {
                            // does not include the case: geneB action geneB
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                    }

                    /*
                                      if (genepos >=0 && (genepos!= pos) && genepos < gw.getNumGenes()){
                        // geneA(=source )action geneB
                        finaloutput.append("#"+ j+ " -> "+ tmpgeneA.get(j)+ " " + tmpaction.get(j) +
                                           " " + tmpgeneB.get(j)+ endl);
                                      }*/
                }
            }
        }

        /*
         geneways.append("Using Affy gene name: "+ genename+ endl);
               geneways.append(this.getGeneFromGeneways(genename)+ endl);
         */

        String info = "Name: " + igm.getLabel() + endl + "UniGene ID: " + igm.getUnigene() + endl + "LocusLinkID " + igm.getGeneId() + endl + "Desc: " + igm.getDescription() + endl + "Short name: " + igm.getShortName() + endl + "Serial: " + igm.getSerial() + endl + tmp + endl + "Gene: " + genename + endl + endl + "GeneWays: \n" + geneways + endl + "------- Interaction List ---------" + endl + finaloutput.toString() + endl;
        if (jCheckSelfLoop.isSelected()) {
            info += "------- Self Loop ----------" + endl + selfLoop.toString();
        }

        jTextArea1.setText(info);
    }

    void jList1_keyReleased(KeyEvent e) {
        int pos = ((JList) e.getSource()).getSelectedIndex();
        if (pos < 0) {
            return;
        }
        show_gene_info(pos);
    }

    /**
     * setup the query's interaction type list <BR>
     * adds ALL to be the first interaction type
     *
     * @param al ArrayList
     */
    public void setQueryInteractionType(ArrayList al) {
        this.interModel.clear();
        this.interModel.addElement("ALL");
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i) != "ALL") {
                this.interModel.addElement(al.get(i));
            }
        }
        this.jListInteraction.invalidate();
    }

    public void setMicroarraySet(DSMicroarraySet set) {
        if (set != null) {
            markerNo = set.size();
        } else {
            markerNo = 0;
        }
    }

    public ArrayList getGeneNameByLocusLinkID(int locuslinkid) {
        ArrayList output = new ArrayList();
        try {
            java.sql.Statement stmt = con.createStatement();

            /*
              String qstring =
                "select nm.name from geneways40a.locuslink ll, geneways40a.names nm " +
                "where preferredname= nm.nameid and ll.accessionnumber= " + locuslinkid;
             */
            // selecting all aliases of the gene linked to the LocusLinkID
            String qstring = "select  nm.name from geneways40a.namelink nl, " + "geneways40a.locuslink ll, geneways40a.names nm " + "where accessionnumber= " + locuslinkid + " and nm.nameid= nl.nameid " + "and nl.oid= ll.oid and nl.dbid=3";

            String sql = qstring;
            // System.out.println("SQL: " + sql);
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            int cnt = 0;
            while (rs.next()) {
                output.add(rs.getString(1));
            }
            stmt.close();
        } catch (java.sql.SQLException sqe) {
            System.out.println(sqe.getMessage());
            System.out.println("Oracle query failed!!");
            //         output += "Query failed!!";
        }
        return output;

    }

    /**
     * queries the GeneWays database and returns [output, geneA, actiontype, geneB] <BR>
     * where output= string, <BR>
     * geneA= ArrayList of genes A <BR>
     * actiontype= ArrayList of actions <BR>
     * geneB= ArrayList of genes B <BR>
     */
    public ArrayList getGeneFromGeneways(String genename) {
        ArrayList all = new ArrayList();
        ArrayList geneA = new ArrayList();
        ArrayList actiontype = new ArrayList();
        ArrayList geneB = new ArrayList();

        genename = genename.replaceAll("\'", "\'\'");
        System.out.println(genename);
        StringBuffer output = new StringBuffer();
        String gene1, action, gene2;
        try {
            java.sql.Statement stmt = con.createStatement();

            String qstring = "select up.name, at.name, dn.name " + "from geneways40a.substance up, geneways40a.substance dn, geneways40a.action, " + "geneways40a.action_type at " + "where upstream = up.substanceid and downstream = dn.substanceid and " + " result = at.id and (up.name = '" + genename.toLowerCase() + "' or dn.name='" + genename.toLowerCase() + "')";

            // only upstream
            String qstring1 = "select up.name, at.name, dn.name " + "from geneways40a.substance up, geneways40a.substance dn, geneways40a.action, " + "geneways40a.action_type at " + "where upstream = up.substanceid and downstream = dn.substanceid and " + " result = at.id and (upper(up.name) = '" + genename.toUpperCase() + "')";

            String qstring2 = "select up.name, at.name, dn.name " + "from geneways40a.substance up, geneways40a.substance dn, geneways40a.action, " + "geneways40a.action_type at " + "where ((((up.name = '" + genename.toLowerCase() + "' or dn.name='" + genename.toLowerCase() + "') and (upstream = up.substanceid )) and (downstream = dn.substanceid)) and (result = at.id))";

            String sql = qstring;

            // System.out.println("SQL: "+ sql);
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            // java.sql.ResultSet rs = stmt.executeQuery("select * from geneways40a.names where upper(name)='"+
            //                                           genename.toUpperCase()+ "' ");

            int cnt = 0;
            while (rs.next()) {
                gene1 = rs.getString(1);
                action = rs.getString(2);
                gene2 = rs.getString(3);
                // add the relationship to the output buffer
                geneA.add(gene1);
                actiontype.add(action);
                geneB.add(gene2);

                cnt++;
                output.append("#" + cnt + " " + gene1 + " " + action + " " + gene2 + "\n");
            }
            stmt.close();
        } catch (java.sql.SQLException sqe) {
            System.out.println(sqe.getMessage());
            System.out.println("Oracle query failed!!");
            output.append("Query failed!!");
        }
        all.add(output.toString());
        all.add(geneA);
        all.add(actiontype);
        all.add(geneB);

        return all;
    }

    void connectDB() {
        // connects to geneway by default...
        // bind database connection will be added after it's been incorporated to our mysql server

        /*      Thread t = new Thread() {
                  public void run() {*/
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Oracle driver not found!!");
            return;
        }
        System.out.println("Driver found!!");

        try {
            java.sql.DriverManager.setLoginTimeout(15000);
            con = java.sql.DriverManager.getConnection("jdbc:oracle:thin:@156.111.188.210:1521:geneways", "tatsen", "a7r-3");
        } catch (java.sql.SQLException sqe) {
            System.out.println("Oracle connection failed!!");
            bconnection = false;
            return;
        }
        bconnection = true;
        System.out.println("Oracle connection established!!");
        /*      }
          };
          t.start();
         */

    }

    void JDBCTest() {

        this.connectDB();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Oracle driver not found!!");
            return;
        }
        System.out.println("Driver found!!");

        try {
            java.sql.DriverManager.setLoginTimeout(15000);
            con = java.sql.DriverManager.getConnection("jdbc:oracle:thin:@156.111.188.210:1521:geneways", "tatsen", "a7r-3");
        } catch (java.sql.SQLException sqe) {
            System.out.println("Oracle connection failed!!");
            return;
        }

        System.out.println("Oracle connection established!!");

        try {
            java.sql.Statement stmt = con.createStatement();

            /*
                       stmt.executeUpdate("show databases;");
                       stmt.executeUpdate(" ");

                       String sqlString = "CREATE TABLE Bars " +
                "(name VARCHAR2(40), address VARCHAR2(80), license INT)";
                       stmt.executeUpdate(sqlString);
             */
            java.sql.ResultSet rs = stmt.executeQuery("select * from geneways40.names where name='bad' ");
            while (rs.next()) {
                String bar = rs.getString(2);
                System.out.println(bar + "\n");
            }

        } catch (java.sql.SQLException sqe) {
            System.out.println(sqe.getMessage());
            System.out.println("Oracle query failed!!");
            return;
        }
    }

    /**
     * @deprecated somehow. function has been incorporated into GWgenes
     */
    public void getAllAliases() {
        ListModel lm = jList1.getModel();
        DSGeneMarker tmpigm;

        int locuslink = -1;
        String tmp;
        int s1, s2;

        // reset the geneways genes
        gw = new GWgenes();
        //gw.setModel(this.model);

        for (int i = 0; i < lm.getSize(); i++) {
            tmpigm = ((DSGeneMarker) lm.getElementAt(i));
            locuslink = tmpigm.getGeneId();
            //System.out.println(locuslink);

            tmp = lm.getElementAt(i).toString();
            s1 = tmp.indexOf("(") + 1;
            s2 = tmp.indexOf(")");

            gw.addSerial(tmpigm.getSerial());
            gw.addGeneLocusID(locuslink);
            gw.addGeneName(tmp.substring(s1, s2)); // add the Affy gene short name
            gw.addAliases(this.getGeneNameByLocusLinkID(locuslink));
        }

        System.out.println("-------- gw dump ---------");
        gw.dumpAliases();
        System.out.println("-------- end of gw dump ---------");
        System.out.println(gw.getIndexOfAlias("il4") + " in gene " + gw.getClassOfAlias("il4"));
        System.out.println(gw.dumpAliasesToSave());

    }

    /**
     * shows the aliases of the genes on the selection list
     */
    void jButtonJDBC_actionPerformed(ActionEvent e) {
        if (!this.bconnection) {
            this.connectDB();
        }
        // System.out.println(System.getProperty("user.dir"));
        ListModel lm = jList1.getModel();
        /*
               ArrayList locus= new ArrayList();
               ArrayList genename= new ArrayList();
               ArrayList genealias= new ArrayList();
               int locuslink= -1;
               String tmp;
               int s1, s2;
         */
        GWgenes gw = new GWgenes();
        gw.setConnection(this.con);
        gw.setModel(lm);
        gw.getAllAliases();

        /*
               for (int i=0; i< lm.getSize(); i++){
            locuslink= ((IGenericMarker)lm.getElementAt(i)).getLocusLink();
            //System.out.println(locuslink);
            locus.add(new Integer(locuslink));
            tmp= lm.getElementAt(i).toString();
              s1= tmp.indexOf("(") +1 ;
              s2= tmp.indexOf(")");
            genename.add(tmp.substring(s1, s2));
            genealias.add(this.getGeneNameByLocusLinkID(locuslink));

            gw.addGeneLocusID(locuslink);
            gw.addGeneName(tmp.substring(s1, s2));
            gw.addAliases(this.getGeneNameByLocusLinkID(locuslink));
               }

               System.out.println("-------- gw dump ---------");
               gw.dumpAliases();
               System.out.println("-------- end of gw dump ---------");
               System.out.println(gw.getIndexOfAlias("il4")+ " in gene "+ gw.getClassOfAlias("il4"));

               StringBuffer output= new StringBuffer();
               ArrayList tmpal= new ArrayList();
               String endl= "\n";

               for (int i =0; i< genealias.size(); i++){
            tmpal= (ArrayList)genealias.get(i);
            output.append("Affy gene name: "+ genename.get(i) + endl);
            for (int j=0; j< tmpal.size(); j++){
                output.append(" -> "+ tmpal.get(j)+ endl);
            }
               }
               jTextArea1.setText(output.toString());
         */
        //      System.out.println(gw.dumpAliasesToSave());
        jTextArea1.setText(gw.dumpAliasesToSave());
    }

    public static void main(String args[]) {
        System.out.println("Hello!!!");
        String tmp = "askljflkasf'123'";
        GWgenes gw = new GWgenes();
        System.out.println(System.getProperty("user.dir"));
        gw.read(new File("aliases.txt"));
    }

    /**
     * send2querylist = true -> the adj matrix will be stored in the query list<BR>
     *
     * @param adj            AdjacencyMatrix
     * @param send2querylist boolean
     */
    public void drawNetwork(EvdAdjacencyMatrix adj, boolean send2querylist) {
        int depth = 3;
        /* serial does not mean anything. <BR>
         * it came in for the old cytoscape drawing method <BR>
         * some day i will replace it<BR><BR>
         */
        int serial1 = 000;
        // double threshold = -1;

        double threshold = .1;
        double eps = .1;

        //adj.clean(this.getMicroarraySet(), threshold, eps);
        adj.setMicroarraySet((DSMicroarraySet) dataSetView.getDataSet());

        // System.out.println("adj.size= "+ adj.size());
        org.geworkbench.events.AdjacencyMatrixEvent ae = new org.geworkbench.events.AdjacencyMatrixEvent(adj, "Initiate Adjacency Matrix transfer", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.RECEIVE);
        this.sentByBind = !send2querylist;
        publishAdjacencyMatrixEvent(ae);
        ae = new AdjacencyMatrixEvent(adj, "Draw Network", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.DRAW_NETWORK_AND_INTERACTION);
        publishAdjacencyMatrixEvent(ae);
        ae = new org.geworkbench.events.AdjacencyMatrixEvent(adj, "Post Processing", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.FINISH);
        publishAdjacencyMatrixEvent(ae);
    }

    void jButtonFakeNetwork_actionPerformed(ActionEvent e) {
        this.jCheckReceiveSelection.setSelected(false);
        EvdAdjacencyMatrix adj = new EvdAdjacencyMatrix();
        ListModel lm = jList1.getModel();
        int serial1 = ((DSGeneMarker) lm.getElementAt(0)).getSerial();
        int serial2 = -1;

        //      System.out.println("serial 1: "+ serial1);
        adj.addGeneRow(serial1);
        for (int i = 01; i < lm.getSize(); i++) {
            serial2 = ((DSGeneMarker) lm.getElementAt(i)).getSerial();
            adj.addGeneRow(serial2);
            /*
             System.out.println("adding serial 2: "+ serial2);
             adj.addInteraction(serial1, serial2, "interaction #"+ Integer.toString(i));
             adj.addInteraction(serial1, serial2, "interaction __#"+ Integer.toString(i));
             */
            adj.addInteractionType2(serial1, serial2, "interaction #" + Integer.toString(i));
            adj.addInteractionType2(serial1, serial2, "interaction __#" + Integer.toString(i));

            //adj.add(serial1, serial2, i);
            //adj.addGeneRow(serial2);
        }

        //    adj.dumpInteraction();

        double threshold = .1;
        double eps = .1;

        //adj.clean(this.getMicroarraySet(), threshold, eps);
        adj.setMicroarraySet((DSMicroarraySet) dataSetView.getDataSet());
        // adj.compute(this.getMicroarraySet(), -.1);
        //      adj.setInteractionText(gw.dumpAllInteraction());


        this.queryList.add(adj);
        this.queryModel.addElement("(" + (++this.queryCnt) + ") Fake Network: " + this.markerHead);

        this.interactionArrayList.add(gw.getUniqueInteraction());
        this.jListInteraction.invalidate();

        // System.out.println("adj.get(serial1)"+ adj.get(serial1).toString());
        //adj.execute();
        int depth = 3;
        // double threshold = -1;

        org.geworkbench.events.AdjacencyMatrixEvent ae = new org.geworkbench.events.AdjacencyMatrixEvent(adj, "Initiate Adjacency Matrix transfer", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.RECEIVE);
        this.sentByBind = true;
        publishAdjacencyMatrixEvent(ae);
        ae = new org.geworkbench.events.AdjacencyMatrixEvent(adj, "Draw Network", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.DRAW_NETWORK_AND_INTERACTION);
        System.out.println("try drawNetworkAndInteraction");
        publishAdjacencyMatrixEvent(ae);
        ae = new AdjacencyMatrixEvent(adj, "Post Processing", serial1, depth, threshold, org.geworkbench.events.AdjacencyMatrixEvent.Action.FINISH);
        publishAdjacencyMatrixEvent(ae);
    }

    /**
     * create the whole network containing the interactions between<BR>
     * the genes in the selected list
     */
    void jButtonGenerateNW_actionPerformed(ActionEvent e) {
        if (!this.bconnection) {
            this.connectDB();
        }

        gw.setConnection(this.con);
        gw.setModel(this.jList1.getModel());

        System.out.println("getAllAliases()");
        gw.getAllAliases();

        //gw.dumpAliases();

        //gw.getGeneInfo(0);
        System.out.println("getAllGeneInfo()");
        gw.getAllGeneInfo();

        /*
               if (this.debug) System.out.println("Get interaction: ");
               for (int i=0; i< gw.getNumGenes(); i++){
            ArrayList ar= gw.getInteractionInvolvedIn(i, false);
            System.out.println("gene " + i + " interacts with genes "+ ar);
               }
         */

        EvdAdjacencyMatrix adj = gw.getAdjNetwork();

        double threshold = .1;
        double eps = .1;

        // adj.clean(this.getMicroarraySet(), threshold, eps);
        // adj.setInteractionText(gw.dumpAllInteraction());
        // adj by default finds the unique interaction types

        // this.drawNetwork(adj, gw.getSerial(gw.getGeneWithMaxPartners()));
        // since serial doesn't mean anything in the new cytoscape drawing method
        // we pass a dummy serial 000
        this.drawNetwork(adj, false);

        // outputing the interaction
        this.jTextArea1.setText(gw.dumpAllInteraction());
        this.jCheckReceiveSelection.setSelected(false);

        this.queryModel.addElement("(" + (++this.queryCnt) + ") " + gw.getNumGenes() + " genes, " + model.get(0));

        this.queryList.add(adj);
        this.jListQuery.invalidate(); // update the list GUI
        this.interactionArrayList.add(adj.getUniqueInteractionType());
        System.out.println("interList: " + this.interactionArrayList);
        //this.jListInteraction.invalidate();

        // setting the interaction type list to the unique interaction types
        this.setQueryInteractionType(adj.getUniqueInteractionType() /*gw.getUniqueInteraction()*/);
    }

    /**
     * draw the selected (single) network
     */
    void jButtonDrawNetworkFromQueryList_actionPerformed(ActionEvent e) {

        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }

        //GWgenes gw= (GWgenes) this.queryList.get(pos);
        //AdjacencyMatrix adj= gw.getAdjNetwork();
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        adj.setSource(AdjacencyMatrix.fromBindPanel);
        adj.setLabel(this.queryModel.elementAt(pos).toString());
        this.drawNetwork(adj, false);
        //this.jTextArea1.setText(gw.dumpAllInteraction()); -> change this

        // unchecking the "auto update" button, so that the gene list panel
        // does not update when we select from cytoscape
        this.jCheckReceiveSelection.setSelected(false);
    }

    /**
     * when clicking on the query list,
     *
     * @param e MouseEvent
     */
    void jListQuery_mouseReleased(MouseEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }

        // show the popup menu
        if (e.getButton() == MouseEvent.BUTTON3) {
            popupMenuQueryList.show(e.getComponent(), e.getX(), e.getY());
            return;
        }

        // get the adj mtx from the list
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);

        // System.out.println("pos= "+ pos+ " -> ");
        // System.out.println((ArrayList)this.interactionArrayList.get(pos));

        // add the interaction types to the interaction list
        if (this.interactionArrayList.get(pos) != null) {
            // this.setQueryInteractionType(myio.obj2arraylist(adj.getFilter()));
            this.setQueryInteractionType((ArrayList) this.interactionArrayList.get(pos));
        } else {
            this.setQueryInteractionType(new ArrayList());
        }

        this.jListInteraction.setSelectedIndex(0);

        // **** temp deleted... why did i use it before??? *** adj.setFilter(this.jListInteraction.getSelectedValues());
        // this.setQueryInteractionType(gw.getUniqueInteraction()); -> set the interaction filter
        this.jTextArea1.setText(adj.getInteractionText());
        // this.model= gw.getGeneModel();
        // this.jList1.invalidate();
    }

    /**
     * this filters the network and sends the adj mtx to be stored
     *
     * @param e ActionEvent
     */
    void jButtonFilterAndDraw_actionPerformed(ActionEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        // System.out.println("Pos= "+ pos);
        adj.setFilter(this.jListInteraction.getSelectedValues());

        adj = adj.filterAndOutputNewAdjMtx();
        adj.setLabel("(" + (this.queryCnt + 1) + ") Filtered Network: " + adj.size() + " genes -> " + myio.obj2arraylist(adj.getFilter()));
        // the interaction list
        System.out.println("interaction list in filter: " + this.arrayTostring(this.jListInteraction.getSelectedValues()));
        System.out.println("(adjmtx) interaction list in filter: " + myio.obj2arraylist(adj.getFilter()));

        this.drawNetwork(adj, false);
        this.jTextArea1.setText(adj.getInteractionText());
        this.jCheckReceiveSelection.setSelected(false);

        // adding the adj matrix
        // refreshing the interaction list
        // then setting the query list title
        this.addAdj2QueryList(adj, ++this.queryCnt);
        /*
               ArrayList tmpfilter= myio.obj2arraylist(adj.getFilter());
               this.queryList.add(adj);
               this.interactionArrayList.add(tmpfilter);
               this.setQueryInteractionType(myio.obj2arraylist(this.jListInteraction.getSelectedValues()));
               //  System.out.println("jButtonFilterAndDraw_actionPerformed" + myio.obj2arraylist(adj.getFilter()));
               this.queryModel.addElement("("+ (++this.queryCnt)+ ") Filtered Network: "+
                                   adj.size()+ " genes -> "+ tmpfilter);
         }
         */
    }

    public void deleteAdjFromQueryList(int pos) {
        if (pos < 0) {
            return;
        }
        EvdAdjacencyMatrix tmpadj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        tmpadj = null;
        this.queryList.remove(pos);
        this.interactionArrayList.remove(pos);
        this.queryModel.remove(pos);
        this.setQueryInteractionType(new ArrayList());
    }

    public void addAdj2QueryList(EvdAdjacencyMatrix adj, int count) {
        ArrayList tmpfilter = myio.obj2arraylist(adj.getFilter());
        this.queryList.add(adj);
        this.interactionArrayList.add(tmpfilter);
        this.setQueryInteractionType(myio.obj2arraylist(this.jListInteraction.getSelectedValues()));
        //  System.out.println("jButtonFilterAndDraw_actionPerformed" + myio.obj2arraylist(adj.getFilter()));
        this.queryModel.addElement("(" + (count) + ") Filtered Network: " + adj.size() + " genes -> " + tmpfilter);

    }

    public String arrayTostring(Object[] obj) {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < obj.length; i++) {
            output.append(obj[i] + " ");
        }
        return output.toString();
    }

    void jButtonFilterInteraction_actionPerformed(ActionEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        // System.out.println("Pos= "+ pos);
        adj.setFilter(this.jListInteraction.getSelectedValues());
        System.out.println("selected interactions: " + this.arrayTostring(this.jListInteraction.getSelectedValues()));
        //this.drawNetwork(adj, 000);
        this.jTextArea1.setText(adj.getInteractionText());
        this.jCheckReceiveSelection.setSelected(false);
    }

    void jListInteraction_mouseReleased(MouseEvent e) {

        Object[] names = this.jListInteraction.getSelectedValues();
        String selected = "Selected: ";
        for (int i = 0; i < names.length - 1; i++) {
            selected += names[i] + ", ";
        }
        selected += names[names.length - 1];
        this.jListInteraction.setToolTipText(selected);
    }

    public void jButtonGetNetworkFromCytoscape_actionPerformed(ActionEvent e) {
        System.out.println("Current network: " + Cytoscape.getCurrentNetwork().getIdentifier());
        CyNetwork cyn = Cytoscape.getCurrentNetwork();
        int[] nodes = cyn.getNodeIndicesArray();
        System.out.println(myio.array2string(nodes, ", "));
        for (int i = 0; i < nodes.length; i++) {
            System.out.println(nodes[i] + " " + cyn.getNode(nodes[i]).getIdentifier());
        }
    }

    public void menuRename_actionPerformed(ActionEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }
        String oldname = this.queryModel.getElementAt(pos).toString();
        String newname = JOptionPane.showInputDialog("Change the label name:", oldname);
        if (newname != null) {
            this.queryModel.setElementAt(newname, pos);
        }
    }

    /**
     * merging the selected adj matrices
     * some problems to fix still...
     * ex. merging MI with GW
     */
    void jMenuMerge_actionPerformed(ActionEvent e) {
        EvdAdjacencyMatrix network;
        int[] idx = this.jListQuery.getSelectedIndices();
        String newname = JOptionPane.showInputDialog("New network name:", "");
        // now create a new adj mtx for storing information
        EvdAdjacencyMatrix tmpadj = new EvdAdjacencyMatrix();
        tmpadj.setMicroarraySet((DSMicroarraySet) dataSetView.getDataSet()); // setup the microarrayset so that probe names could be retrieved
        for (int i = 0; i < idx.length; i++) {
            System.out.println("adding adj matrix " + idx[i]);
            network = (EvdAdjacencyMatrix) this.queryList.get(idx[i]);
            tmpadj.addAdjMatrix(network);

            // leave the filter alone now
            tmpadj.addFilter(network.getFilter());
        }

        // System.out.println(tmpadj.getInteractionText());
        this.drawNetwork(tmpadj, false);
        // gotta decide which information to add to this new network
        this.queryList.add(tmpadj);

        // why did i make filter an obj[] instead of an arraylist????
        // chech it out sometime
        ArrayList tmpal = myio.obj2arraylist(tmpadj.getFilter());
        this.interactionArrayList.add(tmpal);
        this.setQueryInteractionType(tmpal);
        this.queryModel.addElement("(" + (++this.queryCnt) + ") Merged Network: " + tmpadj.size() + " genes -> " + newname);
        network = null;
        tmpadj = null;
        // draw the network finally
    }

    void jMenuDeleteNW_actionPerformed(ActionEvent e) {
        // select the network
        this.deleteAdjFromQueryList(this.jListQuery.getSelectedIndex());
        // delete the network

    }

    void jMenuDrawNW_actionPerformed(ActionEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }

        //GWgenes gw= (GWgenes) this.queryList.get(pos);
        //AdjacencyMatrix adj= gw.getAdjNetwork();
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        this.drawNetwork(adj, false);
        //this.jTextArea1.setText(gw.dumpAllInteraction()); -> change this

        // unchecking the "auto update" button, so that the gene list panel
        // does not update when we select from cytoscape
        this.jCheckReceiveSelection.setSelected(false);
        adj = null;
    }

    public void getWholeNetwork() {
        // load the list of aliases
        EvdAdjacencyMatrix adj = new EvdAdjacencyMatrix();
        adj.setMicroarraySet((DSMicroarraySet) dataSetView.getDataSet());
        GWgenes gw = new GWgenes();
        gw.connect();
        gw.read();

        String[] alias = {"il10", "apo-1"}; // testing purposes

        int[] theserials = {1, 2, 10741}; // default nodes to find interaction partners for

        DSGeneMarker tmpigm;
        if (model.size() != 0) {
            theserials = new int[model.size()];
            for (int i = 0; i < theserials.length; i++) {
                tmpigm = ((DSGeneMarker) model.getElementAt(i));
                theserials[i] = tmpigm.getSerial();
                System.out.println("adding target gene: " + theserials[i]);
            }
        }

        int serialA, serialB;

        for (int i = 0; i < theserials.length; i++) {
            // for each serial number
            // get the aliases
            // for each alias
            // get the interacting genes (ArrayList [output, geneA, interaction, geneB])
            // create the edge between this_gene and the partner_gene

            System.out.println("looking up the alias of " + theserials[i]);
            ArrayList tmpAlias = gw.getAliasofSerial(theserials[i]);

            for (int j = 0; j < tmpAlias.size(); j++) {
                // get the interacting genes
                System.out.println("querying GeneWays for : " + tmpAlias.get(j));

                ArrayList theinteraction = gw.getGeneFromGeneways((String) tmpAlias.get(j));

                // geneA and geneB are in the string format (of the alias)
                ArrayList theGeneA = (ArrayList) theinteraction.get(1);
                ArrayList theAction = (ArrayList) theinteraction.get(2);
                ArrayList theGeneB = (ArrayList) theinteraction.get(3);

                for (int k = 0; k < theGeneA.size(); k++) {
                    // now we find the probe on the chipd corresponding to the alias
                    // here we have the problem of getting more than one probe mapping to one alias

                    ArrayList tmpserialA = (ArrayList) gw.getSerialOfAlias(theGeneA.get(k).toString());
                    if (tmpserialA.size() < 1) {
                        continue;
                    }
                    ArrayList tmpserialB = (ArrayList) gw.getSerialOfAlias(theGeneB.get(k).toString());
                    if (tmpserialB.size() < 1) {
                        continue;
                    }

                    serialA = ((Integer) tmpserialA.get(0)).intValue(); // here we use the serial of the first name match
                    serialB = ((Integer) tmpserialB.get(0)).intValue(); //

                    // compensate
                    // here if one of the serials of geneA is identical to the query probe's serial
                    // we say that geneA is actually the query probe
                    for (int m = 0; m < tmpserialA.size(); m++) {
                        if (((Integer) tmpserialA.get(m)).intValue() == theserials[i]) {
                            serialA = ((Integer) tmpserialA.get(m)).intValue();
                        }
                    }
                    for (int m = 0; m < tmpserialB.size(); m++) {
                        if (((Integer) tmpserialB.get(m)).intValue() == theserials[i]) {
                            serialB = ((Integer) tmpserialB.get(m)).intValue();
                        }
                    }

                    adj.addGeneRow(serialA);
                    adj.addGeneRow(serialB);
                    adj.addInteractionType2(serialA, serialB, theAction.get(k).toString());
                    /*System.out.println(theGeneA.get(k) + " (" + serialA+ ") " +
                                       theAction.get(k)+ " " +
                                       theGeneB.get(k)+ " ("+ serialB+ ")");
                     */
                }
            }
        }
        adj.setLabel("(" + (this.queryCnt + 1) + ") " + adj.size() + " genes");
        // this.drawNetwork(adj, false);

        this.queryModel.addElement("(" + (++this.queryCnt) + ") " + adj.size() + " genes");

        this.queryList.add(adj);
        this.jListQuery.invalidate(); // update the list GUI
        this.interactionArrayList.add(adj.getUniqueInteractionType());
        System.out.println("interList: " + this.interactionArrayList);

        // setting the interaction type list  to the unique interaction types
        this.setQueryInteractionType(adj.getUniqueInteractionType());

        gw = null; // or clean???

    }

    /**
     * generating the interaction network of the target genes with
     * all other genes on the chip
     */
    void jButtonWholeNetwork_actionPerformed(ActionEvent e) {
        if ((DSMicroarraySet) dataSetView.getDataSet() == null) {
            JOptionPane.showMessageDialog(null, "Please load a microarray first.");
            return;
        }

        if (!this.bconnection) {
            this.connectDB();
            if (!this.bconnection) {
                JOptionPane.showMessageDialog(null, "Database connection failed!");
                return;
            }
        }

        jProgressBar1.setIndeterminate(true);
        Thread t = new Thread() {
            public void run() {
                getWholeNetwork();
                jProgressBar1.setIndeterminate(false);
                jProgressBar1.setString("");
            }
        };
        t.start();

    }

    void jMenuListNeighbors_actionPerformed(ActionEvent e) {
        int pos = this.jListQuery.getSelectedIndex();
        if (pos < 0) {
            return;
        }

        if ((DSMicroarraySet) dataSetView.getDataSet() == null) {
            JOptionPane.showMessageDialog(null, "Please load a microarray first.");
            return;
        }

        org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil decoder = new org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil(dataSetView);
        DSItemList<DSGeneMarker> markers = decoder.matchingMarkers(this.jTextTargetGene.getText().trim().toLowerCase());
        System.out.println("Finding neighbors of " + this.jTextTargetGene.getText().trim().toLowerCase());
        if (markers.size() == 0) {
            return;
        }
        for (DSGeneMarker m : markers) {
            // showing the markers that match the label
            System.out.println("#" + m.getSerial() + " " + m.getSerial() + " " + m.getShortName() + " " + m.getDescription());
        }

        //GWgenes gw= (GWgenes) this.queryList.get(pos);
        //AdjacencyMatrix adj= gw.getAdjNetwork();
        EvdAdjacencyMatrix adj = (EvdAdjacencyMatrix) this.queryList.get(pos);
        ArrayList neighbors = new ArrayList();
        ArrayList tmpal = new ArrayList();
        int partner = -1;
        for (DSGeneMarker m : markers) {
            int target = m.getSerial(); // we now use the i-th marker match
            tmpal = adj.getInteractionInvolvedIn(target); // getting all neighbors of serial(target)

            // now get a non-redundant one
            for (int j = 0; j < tmpal.size(); j++) {
                if (!neighbors.contains(tmpal.get(j))) {
                    neighbors.add(tmpal.get(j));
                }
            }
        }

        StringBuffer output = new StringBuffer();
        for (int i = 0; i < neighbors.size(); i++) {
            partner = ((Integer) neighbors.get(i)).intValue();
            // System.out.println("#"+ i+ " "+ partner+ " " +  adj.getMarkerName(partner) );
            output.append("#" + i + " " + partner + " " + adj.getMarkerName(partner) + "\n");
        }
        this.jTextArea1.setText(output.toString());
        adj = null;
        decoder = null;
        neighbors = null;
        output = null;
        markers = null;
    }

    void jMenuDeleteProbe_actionPerformed(ActionEvent e) {
        int pos = this.jList1.getSelectedIndex();
        if (pos < 0) {
            return;
        }
        model.remove(pos);
    }

    @Subscribe public void receive(org.geworkbench.events.AdjacencyMatrixEvent ae, Object source) {
        switch (ae.getAction()) {
            case LOADED:
                {
                    // no-op
                }
                break;
            case RECEIVE:
                {
                    // if the adjmtx was sent by BINDPanel itself, do nothing
                    if (this.sentByBind == true) {
                        // System.out.println("adj mtx sent by bindpanel");
                        this.sentByBind = false;
                        return;
                    }
                    if (ae.getAdjacencyMatrix().getSource() == AdjacencyMatrix.fromGeneNetworkPanelNotTakenCareOf) {
                        // we accept only "taken-care-of" adj matrices from GeneNetworkPanel
                        return;
                    }
                    this.queryList.add(ae.getAdjacencyMatrix());
                    this.interactionArrayList.add(new ArrayList());
                    this.setQueryInteractionType(new ArrayList());
                    this.queryModel.addElement("(" + (++this.queryCnt) + ") MI Network: " + ae.getAdjacencyMatrix().size() + " genes");
                }
                break;
            case DRAW_NETWORK:
                {
                    // no-op
                }
                break;
            case DRAW_NETWORK_AND_INTERACTION:
                {
                    // no-op
                }
                break;
            case FINISH:
                {
                    // no-op
                }
                break;
        }
    }
}

class BINDPanel_menuItem1_actionAdapter implements ActionListener {
    private BINDPanel adaptee;

    BINDPanel_menuItem1_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.menuRename_actionPerformed(e);
    }
}

class BINDPanel_jButtonGetNetworkFromCytoscape_actionAdapter implements ActionListener {
    private BINDPanel adaptee;

    BINDPanel_jButtonGetNetworkFromCytoscape_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButtonGetNetworkFromCytoscape_actionPerformed(e);
    }
}

class BINDPanel_jList1_mouseAdapter extends java.awt.event.MouseAdapter {
    BINDPanel adaptee;

    BINDPanel_jList1_mouseAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.jList1_mouseReleased(e);
    }

}

class BINDPanel_jList1_propertyChangeAdapter implements java.beans.PropertyChangeListener {
    BINDPanel adaptee;

    BINDPanel_jList1_propertyChangeAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void propertyChange(PropertyChangeEvent e) {
        adaptee.jList1_propertyChange(e);
    }
}

class BINDPanel_jList1_keyAdapter extends java.awt.event.KeyAdapter {
    BINDPanel adaptee;

    BINDPanel_jList1_keyAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void keyReleased(KeyEvent e) {
        adaptee.jList1_keyReleased(e);
    }
}

class BINDPanel_jButtonJDBC_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonJDBC_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonJDBC_actionPerformed(e);
    }
}

class BINDPanel_jButtonFakeNetwork_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonFakeNetwork_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonFakeNetwork_actionPerformed(e);
    }
}

class BINDPanel_jButtonGenerateNW_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonGenerateNW_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonGenerateNW_actionPerformed(e);
    }
}

class BINDPanel_jButtonDrawNetworkFromQueryList_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonDrawNetworkFromQueryList_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonDrawNetworkFromQueryList_actionPerformed(e);
    }
}

class BINDPanel_jListQuery_mouseAdapter extends java.awt.event.MouseAdapter {
    BINDPanel adaptee;

    BINDPanel_jListQuery_mouseAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.jListQuery_mouseReleased(e);
    }
}

class BINDPanel_jButtonFilterAndDraw_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonFilterAndDraw_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonFilterAndDraw_actionPerformed(e);
    }
}

class BINDPanel_jButtonFilterInteraction_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonFilterInteraction_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonFilterInteraction_actionPerformed(e);
    }
}

class BINDPanel_jListInteraction_mouseAdapter extends java.awt.event.MouseAdapter {
    BINDPanel adaptee;

    BINDPanel_jListInteraction_mouseAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.jListInteraction_mouseReleased(e);
    }
}

class BINDPanel_jMenuMerge_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jMenuMerge_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuMerge_actionPerformed(e);
    }
}

class BINDPanel_jMenuDeleteNW_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jMenuDeleteNW_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuDeleteNW_actionPerformed(e);
    }
}

class BINDPanel_jMenuDrawNW_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jMenuDrawNW_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuDrawNW_actionPerformed(e);
    }
}

class BINDPanel_jButtonWholeNetwork_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jButtonWholeNetwork_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonWholeNetwork_actionPerformed(e);
    }
}

class BINDPanel_jMenuListNeighbors_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jMenuListNeighbors_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuListNeighbors_actionPerformed(e);
    }
}

class BINDPanel_jMenuDeleteProbe_actionAdapter implements java.awt.event.ActionListener {
    BINDPanel adaptee;

    BINDPanel_jMenuDeleteProbe_actionAdapter(BINDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuDeleteProbe_actionPerformed(e);
    }
}
