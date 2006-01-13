package org.geworkbench.util.sequences;

import org.geworkbench.bison.datastructure.biocollections.Collection;
import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.biocollections.sequences.CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import org.geworkbench.events.SequenceDiscoveryTableEvent;
import org.geworkbench.util.PropertiesMonitor;
import org.geworkbench.util.patterns.PatternTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 * <p>Widget provides all GUI services for sequence panel displays.</p>
 * <p>Widget is controlled by its associated component, SequenceViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 * @author
 * @version 1.0
 */

/**
 * <p>Widget provides all GUI services for sequence panel displays.</p>
 * <p>Widget is controlled by its associated component, SequenceViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 * @author
 * @version 1.0
 */

public class SequenceViewWidgetByXQ extends JPanel {

  private HashMap listeners = new HashMap();
  private ActionListener listener = null;
  private final int xOff = 60;
  private final int yOff = 20;
  private final int xStep = 5;
  private final int yStep = 12;
  private int prevSeqId = 0;
  private int prevSeqDx = 0;
  private DSSequenceSet sequenceDB = new CSSequenceSet();
  //patterns
  //ArrayList  selectedPatterns   = null;
  DSCollection<DSMatchedPattern<DSSequence, DSSeqRegistration>> selectedPatterns = new Collection<DSMatchedPattern<DSSequence, DSSeqRegistration>>();
  //Layouts
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  //Panels and Panes
  private JScrollPane seqScrollPane = new JScrollPane();

  private SequenceViewWidgetPanel seqViewWPanel = new SequenceViewWidgetPanel();
  //Models
  private PatternTableModel model = null;
  private PropertiesMonitor propertiesMonitor = null; //debug
  private JToolBar jToolBar1 = new JToolBar();
  private JToggleButton showAllBtn = new JToggleButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea seqArea = new JTextArea();
  JSlider jSlider1 = new JSlider();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
 // DBTextDataBinder dBTextDataBinder1 = new DBTextDataBinder();

  public SequenceViewWidgetByXQ() {
    try {
      jbInit();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {

    propertiesMonitor = PropertiesMonitor.getPropertiesMonitor();

    this.setLayout(gridBagLayout3);

    seqScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
    });
    this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        this_propertyChange(e);
      }
    });

    showAllBtn.setText("All / Partial");
    showAllBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jToggleButton1_actionPerformed(e);
      }
    });
    seqArea.setEnabled(true);
    seqArea.setMinimumSize(new Dimension(10, 17));
    seqArea.setOpaque(true);
    seqArea.setEditable(true);
    seqArea.setText("Seq Info");
    seqArea.setLineWrap(true);
    seqArea.setWrapStyleWord(true);
  //  dBTextDataBinder1.setJTextComponent(seqArea);
  //  dBTextDataBinder1.setEnableFileLoading(true);
    this.setMinimumSize(new Dimension(100, 314));
    this.add(seqScrollPane,  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 378, 162));
    seqScrollPane.getViewport().add(seqViewWPanel, null);
    this.add(jToolBar1,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 13), 329, 0));
    this.add(jScrollPane1,  new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 11, 0), 345, 59));
    jScrollPane1.getViewport().add(seqArea, null);
    this.add(jSlider1,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 211, -13));
    jToolBar1.add(showAllBtn, null);
    seqViewWPanel.initialize(selectedPatterns, sequenceDB);
    seqViewWPanel.setShowAll(showAllBtn.isSelected());
  }

  //sets all required session objects such as patternTable, sequenceDB, and model
  public void patternSelectionHasChanged(SequenceDiscoveryTableEvent e) {

    //setPatterns(e.getPatterns());
    //seqViewWPanel.initialize(selectedPatterns, e.getSequenceDB());
    setPatterns(e.getPatternMatchCollection());
    seqViewWPanel.initialize(selectedPatterns, sequenceDB);
    showPatterns();

  }

  void jDisplayPanel_mouseClicked(MouseEvent e) {

    final Font font = new Font("Courier", Font.BOLD, 10);
    int x = e.getX();
    int y = e.getY();
    int seqId = getSeqId(y);
    int seqDx = getSeqDx(x);
    if ( (seqId != prevSeqId) || (seqDx != prevSeqDx)) {

      //mofied by xiaoqing on 3/2/04 replace the panel with a textarea. get rid of G.
  //    Graphics g = jPanel1.getGraphics();
     // g.clearRect(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
      if ( (seqId >= 0) && (seqId < sequenceDB.getSequenceNo())) {
        //g.setFont(font);
        DSSequence sequence = sequenceDB.getSequence(seqId);
        if (sequence != null) {
          if ( (seqDx >= 0) && (seqDx < sequence.length())) {
            String seqAscii = sequence.getSequence().substring(seqDx);
            seqArea.setText(seqAscii);
          //g.drawString(seqAscii, 10, 20);
            /*
            FontMetrics fm = g.getFontMetrics(font);
            Rectangle2D r = fm.getStringBounds(seqAscii, g);
            if (patternTable != null) {
              for (int rowId = 0; rowId < patternTable.getSelectedRowCount(); rowId++) {
                Pattern pattern = null;
                try { //added this try/catch clause for debugging
                  pattern = (Pattern) model.getPattern(patternTable.getSelectedRows()[rowId]);

                } catch (Exception ex) {

                  ex.printStackTrace();
                } //added this try/catch clause for debugging
                if (pattern != null) {
                  int id = 0;
                  while ( (id < pattern.getSupport()) && (pattern.getId(id) < seqId)) {
                    id++;
                  } while ( (id < pattern.getSupport()) && (pattern.getId(id) == seqId)) {
                    double scale = (r.getWidth() + 3) / (double) (seqAscii.length());
                    int dx = pattern.getOff(id);
                    double x1 = (dx - seqDx) * scale + 10;
                    double x2 = ( (double) pattern.getExtent()) * scale;
                    g.setColor(PatternOps.getPatternColor(rowId));
                    g.drawRect( (int) x1, 2, (int) x2, 23);
                    id++;
                  }
                }
              }
            }*/
          }

        }
        prevSeqId = seqId;
        prevSeqDx = seqDx;
      } else {

      }
    }
  }

  private int getSeqId(int y) {
    int seqId = (y - yOff) / yStep;
    return seqId;
  }

  private int getSeqDx(int x) {
    double scale = Math.min(5.0, (double) (seqViewWPanel.getWidth() - 20 - xOff) / (double) sequenceDB.getMaxLength());
    int seqDx = (int) ( (double) (x - xOff) / scale);
    return seqDx;
  }
//temp changed by xq
  void showPatterns() {

    /*if (selectedPatterns!= null & selectedPatterns.size() > 0) {
      for (int i = 0; i < selectedPatterns.size(); i++) {
        Pattern pattern = (Pattern)selectedPatterns.get(i);
        if (pattern instanceof PatternImpl) {
          if (pattern.getASCII() == null) {
            PatternOperations.fill( (PatternImpl) pattern, sequenceDB);
=======
      if (selectedPatterns != null){
          if (selectedPatterns.size() > 0) {
              for (int i = 0; i < selectedPatterns.size(); i++) {
                  Pattern pattern = (Pattern) selectedPatterns.get(i);
                  if (pattern instanceof PatternImpl) {
                      if (pattern.getASCII() == null) {
                          PatternOperations.fill( (PatternImpl) pattern,
                              sequenceDB);
                      }
                      //( (DefaultListModel) patternList.getModel()).addElement(pattern);
                      this.repaint();
                  }
              }
>>>>>>> 1.3
          }
      }
<<<<<<< SequenceViewWidgetByXQ.java
    } */

   }


  void this_caretPositionChanged(InputMethodEvent e) {
    showPatterns();
  }

  void this_propertyChange(PropertyChangeEvent e) {

    showPatterns();
  }

  public void deserialize(String filename) {
  /*
        FileInputStream stream;
        ObjectInput oos;
   try{
     stream = new FileInputStream(filename);
     oos = new ObjectInputStream(stream);
     panel = (MarkerPanelSetImpl)oos.readObject();
     jScrollPane2.getViewport().remove(panelTree);
     panelTreeModel = (DefaultTreeModel)oos.readObject();
     root = (DefaultMutableTreeNode)panelTreeModel.getRoot();
     panelTree = new JTree(panelTreeModel);
     panelTree.addMouseListener(panelTreeListener);
     panelTree.setEditable(false);
     panelTreeSelection = panelTree.getSelectionModel();
     panelTreeSelection.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
     panelTree.setCellRenderer(treeRenderer);
     jScrollPane2.getViewport().add(panelTree, null);
     String label = panel.getLabel();
     if (panels.containsKey(label)){
       int i = 0;
       while (panels.containsKey(label)){
         label += ++i;
       }
       JOptionPane.showMessageDialog(genePanel, "Renamed PanelSet as a PanelSet " +
                                     "with the same name already exists in " +
                                     "the list of PanelSets",
                                     "Renamed PanelSet",
                                     JOptionPane.INFORMATION_MESSAGE);
       panel.setName(label);
     }
     panels.put(label, panel);
     jPanelSetItem.addItem(label);
     jPanelSetItem.setSelectedItem(label);
     genePanel.invalidate();
     genePanel.repaint();
     panelModified();
   }
   catch (IOException ioe){ ioe.printStackTrace(); }
   catch (ClassNotFoundException cnfe){ cnfe.printStackTrace(); }
   */
  }

  /*      public ActionListener getActionListener(String var){
          return (ActionListener)listeners.get(var);
        }
   */

  //public void setPatternList(JList patList){
  //patternList = patList;
  //}

  public void setSequenceDB(DSSequenceSet db) {
    sequenceDB = db;
    seqViewWPanel.initialize(null, db);
    repaint();
  }

  public DSSequenceSet getSequenceDB() {
    return sequenceDB;
  }

  void jToggleButton1_actionPerformed(ActionEvent e) {
    seqViewWPanel.setShowAll(showAllBtn.isSelected());
    this.repaint();
  }
  //  public void setPatterns(Pattern[] patterns) {
//    selectedPatterns.clear();
//    for(int i = 0; i < patterns.length; i++)
//      selectedPatterns.add(patterns[i]);
//  }
  public void setPatterns(DSCollection<DSMatchedPattern<DSSequence, DSSeqRegistration>> patternMatches) {
    selectedPatterns.clear();
    for(int i = 0; i < patternMatches.size(); i++)
      selectedPatterns.add(patternMatches.get(i));
  }
}
