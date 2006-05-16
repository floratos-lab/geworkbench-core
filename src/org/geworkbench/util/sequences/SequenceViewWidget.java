package org.geworkbench.util.sequences;

import org.geworkbench.bison.datastructure.biocollections.Collection;
import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSMatchedSeqPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSSeqRegistration;
import org.geworkbench.events.SequenceDiscoveryTableEvent;
import org.geworkbench.util.PropertiesMonitor;
import org.geworkbench.util.patterns.CSMatchedSeqPattern;
import org.geworkbench.util.patterns.PatternOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import javax.swing.event.MouseInputAdapter;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.management.Asynchronous;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.biocollections.views.
        DSMicroarraySetView;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.biocollections.microarrays.
        DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import org.geworkbench.util.patterns.PatternSequenceDisplayUtil;
import java.util.TreeSet;
import org.geworkbench.util.patterns.PatternLocations;

/**
 * <p>Widget provides all GUI services for sequence panel displays.</p>
 * <p>Widget is controlled by its associated component, SequenceViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 * @author
 * @version 1.0
 */

public class SequenceViewWidget extends JPanel {
    private HashMap listeners = new HashMap();
    private ActionListener listener = null;
    private final int xOff = 60;
    private final int yOff = 20;
    private final int xStep = 5;
    private final int yStep = 14;
    private int prevSeqId = 0;
    private int prevSeqDx = 0;
    private DSSequenceSet sequenceDB = new CSSequenceSet();
    private DSSequenceSet orgSequenceDB = new CSSequenceSet();
    private DSSequenceSet displaySequenceDB = new CSSequenceSet();
    HashMap<CSSequence,
            PatternSequenceDisplayUtil> patternSeqMatches;
    //Layouts
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private BorderLayout borderLayout1 = new BorderLayout();
    private BorderLayout borderLayout2 = new BorderLayout();
    private BorderLayout borderLayout3 = new BorderLayout();
    //Panels and Panes
    private JPanel jPanel1 = new JPanel();
    private JScrollPane seqScrollPane = new JScrollPane();

    private SequenceViewWidgetPanel seqViewWPanel = new SequenceViewWidgetPanel();
    private DSCollection<DSMatchedPattern<DSSequence,
            DSSeqRegistration>>
            selectedPatterns = new Collection<DSMatchedPattern<DSSequence,
                               DSSeqRegistration>>();
    private PropertiesMonitor propertiesMonitor = null; //debug
    private JToolBar jToolBar1 = new JToolBar();
    private JToggleButton showAllBtn = new JToggleButton();
    private JCheckBox jAllSequenceCheckBox = new JCheckBox();
    private JLabel jViewLabel = new JLabel();
    private JComboBox jViewComboBox = new JComboBox();
    private static final String LINEVIEW = "Line";
    private static final String FULLVIEW = "Full Sequence";
    private JTextField jSequenceSummaryTextField = new JTextField();
    private boolean isLineView = true; //true is for LineView.
    private boolean onlyShowPattern = false;
    protected CSSequenceSet activeSequenceDB = null;
    protected boolean subsetMarkerOn = true;
    protected DSPanel<? extends DSGeneMarker> activatedMarkers = null;

    public SequenceViewWidget() {
        try {
            jbInit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {

        propertiesMonitor = PropertiesMonitor.getPropertiesMonitor();

        this.setLayout(borderLayout2);
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setMinimumSize(new Dimension(50, 40));
        jPanel1.setPreferredSize(new Dimension(50, 40));

        seqScrollPane.setBorder(BorderFactory.createEtchedBorder());
        seqViewWPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jDisplayPanel_mouseClicked(e);
            }
        });
        this.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(InputMethodEvent e) {
            }

            public void caretPositionChanged(InputMethodEvent e) {
                this_caretPositionChanged(e);
            }
        }); this.addPropertyChangeListener(new java.beans.
                                           PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                this_propertyChange(e);
            }
        });
        showAllBtn.setToolTipText(
                "Push down to show sequences with selected patterns.");
        showAllBtn.setText("All / Matching Pattern");

        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllBtn_actionPerformed(e);
            }
        });
        jAllSequenceCheckBox.setToolTipText("Click to display all sequences.");
        jAllSequenceCheckBox.setSelected(false);
        jAllSequenceCheckBox.setText("All Sequences");
        jAllSequenceCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jAllSequenceCheckBox_actionPerformed(e);
            }
        });
        jViewLabel.setText("View: ");
        jSequenceSummaryTextField.setText("Move the mouse over to see details.");

        seqViewWPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                seqViewWPanel_mouseMoved(e);
            }
        });
        jViewComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jViewComboBox_actionPerformed(e);
            }
        });
        jViewComboBox.setToolTipText("Select a view to display results.");
        this.add(jPanel1, BorderLayout.SOUTH);
        this.add(seqScrollPane, BorderLayout.CENTER);
        this.add(jToolBar1, BorderLayout.NORTH);
        jToolBar1.add(jViewLabel);
        jToolBar1.add(jViewComboBox);
        jToolBar1.add(showAllBtn);
        jToolBar1.add(jAllSequenceCheckBox);

        jToolBar1.addSeparator();
        jToolBar1.add(jSequenceSummaryTextField);
        jViewComboBox.addItem(LINEVIEW);
        jViewComboBox.addItem(FULLVIEW);

        jViewComboBox.setSize(jViewComboBox.getPreferredSize());
        if (sequenceDB != null) {
            seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
        }
        seqViewWPanel.initialize(selectedPatterns, sequenceDB);
        seqScrollPane.getViewport().add(seqViewWPanel, null);
        seqViewWPanel.setShowAll(showAllBtn.isSelected());
    }


    /**
     * receiveProjectSelection
     *
     * @param e ProjectEvent
     */
    @Subscribe public void receive(org.geworkbench.events.ProjectEvent e,
                                   Object source) {
        if (e.getMessage().equals(org.geworkbench.events.ProjectEvent.CLEARED)) {
            //refMASet = null;
            fireModelChangedEvent(null);
        } else {
            DSDataSet dataSet = e.getDataSet();
            if (dataSet instanceof DSSequenceSet) {
                if (orgSequenceDB != dataSet) {
                    this.orgSequenceDB = (DSSequenceSet) dataSet;
                    selectedPatterns = null;
                    // activatedMarkers = null;
                }
            }
            //refreshMaSetView();
        }
        refreshMaSetView();
    }

    /**
     * geneSelectorAction
     *
     * @param e GeneSelectorEvent
     */
    public void sequenceDBUpdate(GeneSelectorEvent e) {
        if (e.getPanel() != null && e.getPanel().size() > 0) {
            activatedMarkers = e.getPanel().activeSubset();
        } else {
            activatedMarkers = null;
        }
        refreshMaSetView();
    }


    protected void refreshMaSetView() {
        getDataSetView();
    }

    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {
        this.repaint();
    }


    void chkActivateMarkers_actionPerformed(ActionEvent e) {
        subsetMarkerOn = !((JCheckBox) e.getSource()).isSelected();
        refreshMaSetView();
    }

    public void getDataSetView() {
        subsetMarkerOn = !jAllSequenceCheckBox.isSelected();
        if (subsetMarkerOn) {
            if (activatedMarkers != null &&
                activatedMarkers.size() > 0) {

                if (subsetMarkerOn && (orgSequenceDB != null)) {
                    // createActivatedSequenceSet();
                    activeSequenceDB = (CSSequenceSet) ((CSSequenceSet)
                            orgSequenceDB).
                                       getActiveSequenceSet(activatedMarkers);

                }

            } else if (orgSequenceDB != null) {

                activeSequenceDB = (CSSequenceSet) orgSequenceDB;
            }

        } else if (orgSequenceDB != null) {
            activeSequenceDB = (CSSequenceSet) orgSequenceDB;
        }
        if (activeSequenceDB != null) {
            sequenceDB = activeSequenceDB;
            initPanelView();
        }
    }


    public void patternSelectionHasChanged(SequenceDiscoveryTableEvent e) {
        setPatterns(e.getPatternMatchCollection());
//        if (sequenceDB != null) {
//            seqViewWPanel.setMaxSeqLen(orgSequenceDB.getMaxLength());
//
//            if (showAllBtn.isSelected()) {
//                DSSequenceSet db = sequenceDB.createSubSetSequenceDB(
//                        createTempDBIndex());
//
//                db.setMatchIndex(sequenceDB.getMatchIndex());
//
//                seqViewWPanel.initialize(selectedPatterns, db, isLineView);
//            } else {
//                seqViewWPanel.initialize(selectedPatterns, sequenceDB,
//                                         isLineView);
//            }
//            showPatterns();
//        }
        refreshMaSetView();

    }


    /**
     * Create a boolean list to define which sequences will be included in the new DataSet.
     * @return boolean[]
     */

    public boolean[] createTempDBIndex() {
        int size = sequenceDB.getSequenceNo();
        boolean[] included = new boolean[size];
        for (int i = 0; i < size; i++) {
            included[i] = false;
        }
        if (selectedPatterns != null) {

            for (int row = 0; row < selectedPatterns.size(); row++) {
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                              selectedPatterns.get(row);
                // Pattern pattern = (Pattern) selectedPatterns.get(row);
                if (pattern != null) {
                    //   if (pattern.getClass().isAssignableFrom(PatternImpl.class)) {
                    if (pattern instanceof CSMatchedSeqPattern) {

                        if (pattern != null) {
                            for (int locusId = 0;
                                               locusId < pattern.getSupport();
                                               locusId++) {

                                int seqId = ((CSMatchedSeqPattern) pattern).
                                            getId(locusId);
                                if (seqId < size) {

                                    included[seqId] = true;
                                } else {



                                }
                            }
                        }
                    }
                }
            }
        }
        return included;
    }


    /**
     * Update the detail of sequence.
     * @param e MouseEvent
     */

    void jDisplayPanel_mouseClicked(MouseEvent e) {
        seqViewWPanel.this_mouseClicked(e);
        DSSequence selectedSequence = seqViewWPanel.getSelectedSequence();
        int xStartPoint = seqViewWPanel.getSeqXclickPoint();

        final Font font = new Font("Courier", Font.BOLD, 10);
        // Get the mouse position
        int x = e.getX();
        int y = e.getY();
        // get the position of the sequence and the offset, relative to the sequence start from
        // the mouse position
        int seqId = getSeqId(y);
        int seqDx = getSeqDx(x);

        // int seqId = getSeqId(y);
        if (sequenceDB != null) {
            for (int i = 0; i < sequenceDB.size(); i++) {
                DSSequence seq = sequenceDB.getSequence(i);
                if (seq == selectedSequence) {
                    seqId = i;
                }
            }
        }
        seqDx = xStartPoint;

        DSSequence sequence = selectedSequence;
        // Check if we are clicking on a new sequence
        if ((seqId != prevSeqId) || (seqDx != prevSeqDx)) {
            Graphics g = jPanel1.getGraphics();
            g.clearRect(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
            g.setFont(font);
            if ((seqDx >= 0) && (seqDx < sequence.length())) {
                //turn anti alising on
                ((Graphics2D) g).setRenderingHint(RenderingHints.
                                                  KEY_ANTIALIASING,
                                                  RenderingHints.
                                                  VALUE_ANTIALIAS_ON);
                //shift the selected pattern/sequence into middle of the panel.
                int startPoint = 0;
                if (seqDx > 50) {
                    startPoint = seqDx / 10 * 10 - 50;
                }
                FontMetrics fm = g.getFontMetrics(font);

                String seqAscii = sequence.getSequence().substring(
                        startPoint);
                Rectangle2D r2d = fm.getStringBounds(seqAscii, g);
                int seqLength = seqAscii.length();
                double xscale = (r2d.getWidth() + 3) /
                                (double) (seqLength);
                double yscale = 0.6 * r2d.getHeight();
                g.drawString(seqAscii, 10, 20);
                int paintPoint = 0;
                while (paintPoint < seqLength) {
                    g.drawString("|",
                                 10 + (int) (paintPoint * xscale),
                                 (int) (20 + yscale));
                    g.drawString(new Integer(paintPoint + 1 +
                                             startPoint).toString(),
                                 10 + (int) (paintPoint * xscale),
                                 (int) (20 + 2 * yscale));
                    paintPoint += 40;
                }

                if (patternSeqMatches != null) {
                    PatternSequenceDisplayUtil psd = patternSeqMatches.
                            get(sequence);
                    TreeSet<PatternLocations>
                            patternsPerSequence = psd.getTreeSet();
                    if (patternsPerSequence != null &&
                        patternsPerSequence.size() > 0) {
                        for (PatternLocations pl : patternsPerSequence) {
                            DSSeqRegistration registration = pl.getRegistration();
                            if (registration != null) {
                                Rectangle2D r = fm.getStringBounds(seqAscii, g);
                                double scale = (r.getWidth() + 3) /
                                               (double) (seqAscii.length());
                                DSSeqRegistration seqReg = (
                                        DSSeqRegistration) registration;
                                int patLength = pl.getAscii().length();
                                int dx = seqReg.x1;
                                double x1 = (dx - startPoint) * scale +
                                            10;
                                double x2 = ((double) patLength) *
                                            scale;
                                g.setColor(PatternOperations.
                                           getPatternColor(new Integer(pl.
                                        getHashcode())));
                                g.drawRect((int) x1, 2, (int) x2, 23);
                                g.drawString("|",
                                             (int) x1, (int) (20 + yscale));
                                g.drawString("|",
                                             (int) (x1 + x2 - scale),
                                             (int) (20 + yscale));
                                g.drawString(new Integer(dx + 1).
                                             toString(),
                                             (int) x1,
                                             (int) (20 + 2 * yscale));
                                g.drawString(new Integer(dx +
                                        seqReg.length() + 1).toString(),
                                             (int) (x1 + x2 - scale),
                                             (int) (20 + 2 * yscale));

                            }

                        }
                    }
                }

            }
        }

        prevSeqId = seqId;
        prevSeqDx = seqDx;

    }


    private int getSeqId(int y) {
        int seqId = (y - yOff) / yStep;
        return seqId;
    }

    private int getSeqDx(int x) {
        double scale = Math.min(5.0,
                                (double) (seqViewWPanel.getWidth() - 20 - xOff) /
                                (double) displaySequenceDB.getMaxLength());
        int seqDx = (int) ((double) (x - xOff) / scale);
        return seqDx;
    }

    void showPatterns() {
        if (selectedPatterns.size() > 0) {
            for (int i = 0; i < selectedPatterns.size(); i++) {
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                              selectedPatterns.get(i);
                if (pattern instanceof CSMatchedSeqPattern) {
                    if (pattern.getASCII() == null) {
                        PatternOperations.fill((CSMatchedSeqPattern) pattern,
                                               displaySequenceDB);
                    }
                    //( (DefaultListModel) patternList.getModel()).addElement(pattern);
                    this.repaint();
                }
            }
        }
    }

    void this_caretPositionChanged(InputMethodEvent e) {
        showPatterns();
    }

    void this_propertyChange(PropertyChangeEvent e) {

        showPatterns();
    }

    public void deserialize(String filename) {

    }


    public void setSequenceDB(DSSequenceSet db) {
        orgSequenceDB = db;
        sequenceDB = db;
        displaySequenceDB = db;
        refreshMaSetView();
        //selectedPatterns = new ArrayList();
        if (sequenceDB != null) {
            seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
            seqViewWPanel.initialize(null, db);
            selectedPatterns.clear();
            repaint();
        }
    }


    public DSSequenceSet getSequenceDB() {
        return sequenceDB;
    }


    public void setPatterns(DSCollection<DSMatchedPattern<DSSequence,
                            DSSeqRegistration>> matches) {
        selectedPatterns.clear();
        for (int i = 0; i < matches.size(); i++) {
            selectedPatterns.add(matches.get(i));
        }
    }


    public void showAllBtn_actionPerformed(ActionEvent e) {
        if (selectedPatterns == null && showAllBtn.isSelected()) {
            if (sequenceDB == null) {
                JOptionPane.showMessageDialog(null,
                                              "No sequence is stored right now, please load sequences first.",
                                              "No Pattern",
                                              JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No pattern is stored right now, please generate patterns with Pattern Discory module first.",
                                              "No Pattern",
                                              JOptionPane.ERROR_MESSAGE);
                seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
                displaySequenceDB = sequenceDB;
                seqViewWPanel.setShowAll(false);
                seqViewWPanel.initialize(selectedPatterns, sequenceDB,
                                         isLineView);

            }

            showAllBtn.setSelected(false);

        }
        initPanelView();

    }

    public void jViewComboBox_actionPerformed(ActionEvent e) {

        initPanelView();

    }

    public void seqViewWPanel_mouseMoved(MouseEvent e) {

        seqViewWPanel.this_mouseMoved(e);
        jSequenceSummaryTextField.setText(seqViewWPanel.getDisplayInfo());

    }

    /**
     * Initate the Panel, which should be used as the entry point.
     * @return boolean
     */
    public boolean initPanelView() {

        isLineView = jViewComboBox.getSelectedItem().equals(LINEVIEW);
        onlyShowPattern = showAllBtn.isSelected();
        HashMap<CSSequence,
                PatternSequenceDisplayUtil>
                onlyPatternSeqMatches = new HashMap<CSSequence,
                                        PatternSequenceDisplayUtil>();
        if (onlyShowPattern) {
            displaySequenceDB = new CSSequenceSet();
        }
        patternSeqMatches = PatternOperations.processPatterns(selectedPatterns,
                sequenceDB);
        if (patternSeqMatches != null && sequenceDB != null) {
            for (int i = 0; i < sequenceDB.size(); i++) {
                DSSequence sequence = sequenceDB.getSequence(i);
                PatternSequenceDisplayUtil pdu = patternSeqMatches.get(sequence);
                if (onlyShowPattern) {
                    if (pdu.hasPattern()) {
                        displaySequenceDB.addASequence(sequence);
                    }
                }

            }
            if (onlyShowPattern) {
                seqViewWPanel.initialize(patternSeqMatches, displaySequenceDB,
                                         isLineView);
            } else {
                seqViewWPanel.initialize(patternSeqMatches, sequenceDB,
                                         isLineView);

            }

        } else {
            if (sequenceDB != null) {
                seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
                displaySequenceDB = sequenceDB;
                seqViewWPanel.setShowAll(false);
                seqViewWPanel.initialize(selectedPatterns, sequenceDB,
                                         isLineView);
                showAllBtn.setSelected(false);

            } else {
                seqViewWPanel.removeAll();
            }
        }

        return true;
    }

    public void jAllSequenceCheckBox_actionPerformed(ActionEvent e) {
        refreshMaSetView();
    }


}
