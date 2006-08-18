package org.geworkbench.util.promoter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import org.geworkbench.bison.datastructure.biocollections.sequences.
        DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSSeqRegistration;
import org.geworkbench.util.patterns.PatternOperations;
import org.geworkbench.util.patterns.PatternSequenceDisplayUtil;
import org.geworkbench.util.promoter.pattern.Display;
import org.geworkbench.util.sequences.SequenceViewWidget;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.geworkbench.engine.management.Publish;
import java.awt.image.BufferedImage;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author
 * @version 1.0
 */

public class SequencePatternDisplayPanel extends SequenceViewWidget {
    final int xOff = 60;
    final int yOff = 20;
    final int xStep = 5;
    final int yStep = 14;
    boolean isText = false;

    double scale = 1.0;
    int maxLen = 1;

    int selected = 0;
    private boolean displayTF = true;
    private boolean displaySeqPattern = true;
    DSSequenceSet sequenceDB = null;
    HashMap patternDisplay = new HashMap();
    Hashtable<DSPattern<DSSequence, DSSeqRegistration>,
            List<DSPatternMatch<DSSequence,
            DSSeqRegistration>>> patternMatches = new Hashtable<DSPattern<
                                                  DSSequence, DSSeqRegistration>,
                                                  List<DSPatternMatch<
                                                  DSSequence, DSSeqRegistration>>>();
    public HashMap<CSSequence,
            PatternSequenceDisplayUtil> patternTFMatches = new HashMap<
            CSSequence,
            PatternSequenceDisplayUtil>();
    HashMap<CSSequence,
            PatternSequenceDisplayUtil>
            patternSeqMatches = new HashMap<CSSequence,
                                PatternSequenceDisplayUtil>();
    JPanel jinfoPanel = new JPanel();
//    JPopupMenu itemListPopup = new JPopupMenu();

//

    public void setInfoPanel(JPanel jinfoPanel) {
        // this.jinfoPanel = jinfoPanel;
    }

    public JPanel getInfoPanel() {
        return jinfoPanel;
    }

    public SequencePatternDisplayPanel() {
        try {
            jbInit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addToolBarButton(AbstractButton jbutton) {
        jToolBar1.add(jbutton);
        repaint();
    }

    public void addMenuItem(JMenuItem saveItem) {
        //outsource the save functionto PromoterViewPanel.
        seqViewWPanel.addMenuItem(saveItem);
        repaint();
    }

    /**
     * Transform the patterns to patternsUtil class.
     * Child class should override this method.
     */
    public void updatePatternSeqMatches() {
        patternSeqMatches = PatternOperations.processPatterns(selectedPatterns,
                sequenceDB);
        if (displayTF) {
            if (displaySeqPattern) {
                patternLocationsMatches = PatternOperations.merge(
                        patternSeqMatches, patternTFMatches);
            } else {
                patternLocationsMatches = patternTFMatches;
            }
        } else {
            if (displaySeqPattern) {
                patternLocationsMatches = patternSeqMatches;
            } else {
                patternLocationsMatches = null;
            }

        }

        //patternLocationsMatches
    }

    void jbInit() throws Exception {

        super.removeButtons(SequenceViewWidget.NONBASIC);
//        saveItem.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                createImageSnapshot();
//            }
//
//        });

    //    addMenuItem(saveItem);

    }

    public org.geworkbench.events.ImageSnapshotEvent
            createImageSnapshot() {
        Dimension panelSize = seqViewWPanel.getSize();
        BufferedImage image = new BufferedImage(panelSize.width,
                                                panelSize.height,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        seqViewWPanel.paint(g);
        ImageIcon icon = new ImageIcon(image, "Promoter");
        org.geworkbench.events.ImageSnapshotEvent event = new org.geworkbench.
                events.ImageSnapshotEvent("Promoter Snapshot", icon,
                                          org.geworkbench.events.
                                          ImageSnapshotEvent.Action.SAVE);
        return event;
    }

    public void initialize(DSSequenceSet seqDB) {
        super.setSequenceDB(seqDB);
        //super.initialize(null, seqDB);
        //        selectedPatterns = ar;

        patternMatches.clear();
        patternDisplay.clear();
        sequenceDB = seqDB;
        updateBottomPanel();
        repaint();
    }


//Reset, added by xq.
    public void initialize() {

        patternMatches.clear();
        patternDisplay.clear();
        sequenceDB = null;
        repaint();
    }

//    /**
//      * Initate the Panel, which should be used as the entry point.
//      * @return boolean
//      */
//     public boolean initPanelView() {
//         updatePatternSeqMatches();
//         super.initPanelView();
//         return true;
//     }


    public void flipIsText() {

        isText = !isText;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        if (isText) {
//            paintText(g);
//        } else {
//            paintGraphic(g);
//        }
    }

    private void paintGraphic(Graphics g) {
        Font f = new Font("Courier New", Font.PLAIN, 10);
        if (sequenceDB != null) {
            int rowId = -1;
            int maxLn = sequenceDB.getMaxLength();
            int seqNo = sequenceDB.getSequenceNo();

            scale = Math.min(5.0,
                             (double) (this.getWidth() - 20 - xOff) /
                             (double) maxLn);
            g.clearRect(0, 0, getWidth(), getHeight());
            // draw the patterns
            g.setFont(f);
            JViewport scroller = (JViewport)this.getParent();
            Rectangle r = new Rectangle();
            r = scroller.getViewRect();

            for (int seqId = 0; seqId < seqNo; seqId++) {
                rowId++;
                drawSequence(g, seqId, seqId, maxLn);
            }

            for (DSPattern pattern : patternMatches.keySet()) {
                List<DSPatternMatch<DSSequence,
                        DSSeqRegistration>>
                        matches = patternMatches.get(pattern);
                if ((matches != null) && (matches.size() > 0)) {
                    drawPattern(g, matches, r,
                                (Display) patternDisplay.get(pattern));
                }

            }
            int maxY = (seqNo + 1) * yStep + yOff;
            setPreferredSize(new Dimension(this.getWidth() - yOff, maxY));
            revalidate();

        } else {

        }

    }

    private void paintText(Graphics g) throws ArrayIndexOutOfBoundsException {

        if (sequenceDB != null) {
            DSSequence theone = sequenceDB.getSequence(selected);

            if (theone != null) {
                Font f = new Font("Courier New", Font.PLAIN, 11);
                ((Graphics2D) g).setRenderingHint(RenderingHints.
                                                  KEY_ANTIALIASING,
                                                  RenderingHints.
                                                  VALUE_ANTIALIAS_ON);
                FontMetrics fm = g.getFontMetrics(f);
                String asc = theone.getSequence();
                Rectangle2D r2d = fm.getStringBounds(asc, g);
                double xscale = (r2d.getWidth() + 3) / (double) (asc.length());
                double yscale = 1.3 * r2d.getHeight();
                int width = this.getWidth();
                int cols = (int) (width / xscale) - 8;
                int rowId = 0;
                g.setFont(f);
                JViewport scroller = (JViewport)this.getParent();
                Rectangle r = scroller.getViewRect();
                String lab = theone.getLabel();
                int y = yOff + (int) (rowId * yscale);
                g.setColor(Color.black);
                //            if (lab.length() > 10) {
                //                g.drawString(lab.substring(0, 10), 2, y + 3);
                //            }
                //            else {
                g.drawString(lab, 2, y + 3);
                //            }

                int begin = 0 - cols;
                int end = 0;
                //            rowId++;
                while (end < asc.length()) {
                    rowId++;
                    y = yOff + (int) (rowId * yscale);

                    begin = end;
                    end += cols;
                    String onepiece = "";
                    if (end > asc.length()) {
                        onepiece = asc.substring(begin, asc.length());
                    } else {
                        onepiece = asc.substring(begin, end);

                    }
                    g.drawString(onepiece, (int) (6 * xscale), y + 3);
                }
                for (DSPattern pattern : patternMatches.keySet()) {
                    List<DSPatternMatch<DSSequence,
                            DSSeqRegistration>>
                            matches = patternMatches.get(pattern);
                    if (matches != null) {
                        for (int i = 0; i < matches.size(); i++) {
                            DSPatternMatch<DSSequence,
                                    DSSeqRegistration> match = matches.get(i);
                            DSSequence sequence = match.getObject();
                            if (sequence.getSerial() == selected) {
                                drawPattern(g, match, r, xscale, yscale, cols,
                                            (Display) patternDisplay.get(
                                        pattern));
                            }
                        }
                    }
                }

                int maxY = y + yOff;
                setPreferredSize(new Dimension(this.getWidth() - yOff, maxY));
                revalidate();

            }
        }
    }

    /**
     * drawPattern
     *
     * @param g       Graphics
     * @param sp      IPatternMatch
     * @param r       Rectangle
     * @param xscale  double
     * @param yscale  double
     * @param cols    int
     * @param display Display
     */
    private void drawPattern(Graphics g, DSPatternMatch<DSSequence,
                             DSSeqRegistration> sp, Rectangle r, double xscale,
                             double yscale, int cols, Display dp) {
        int length = sp.getRegistration().length();
        int offset = sp.getRegistration().x1;
        int x = (int) ((6 + offset % cols) * xscale);
        int y = yOff + (int) ((1 + (offset / cols)) * yscale);
        int xb = (int) (length * xscale);
        g.setColor(dp.getColor());
        int height = (int) (dp.getHeight() * yscale);
        if (offset % cols + length <= cols) {
            switch (dp.getShape()) {

            case 0:
                g.drawRect(x, y - height / 2, xb, height);
                break;

            case 1:
                g.drawRoundRect(x, y - height / 2, xb, height, 2, 2);
                ;
                break;
            case 2:
                g.draw3DRect(x, y - height / 2, xb, height, false);
                ;
                break;

            case 3:
                g.drawOval(x, y - height / 2, xb, height);
                ;
                break;

            default:
                g.draw3DRect(x, y - height / 2, xb, height, false);

            }
        } else {
            int startx = (int) (6 * xscale);
            int endx = (int) ((cols + 6) * xscale);
            int k = (offset + length) / cols - offset / cols;
            switch (dp.getShape()) {

            case 0:

                g.drawRect(x, y - height / 2, endx - x, height);
                for (int i = 1; i < k; i++) {
                    g.drawRect(startx, y - height / 2 + (int) (i * yscale),
                               endx - startx, height);
                }
                g.drawRect(startx,
                           yOff +
                           (int) ((1 + ((offset + length) / cols)) * yscale) -
                           height / 2,
                           (int) (((offset + length) % cols) * xscale), height);
                break;

            case 1:

                g.drawRoundRect(x, y - height / 2, endx - x, height, 2,
                                height / 2);
                for (int i = 1; i < k; i++) {
                    g.drawRoundRect(startx, y - height / 2 + (int) (i * yscale),
                                    endx - startx, height, 2, height / 2);
                }
                g.drawRoundRect(startx,
                                yOff +
                                (int) ((1 + ((offset + length) / cols)) *
                                       yscale) -
                                height / 2,
                                (int) (((offset + length) % cols) * xscale),
                                height, 2, height / 2);
                break;

            case 2:
                g.draw3DRect(x, y - height / 2, endx - x, height, true);
                for (int i = 1; i < k; i++) {
                    g.draw3DRect(startx, y - height / 2 + (int) (i * yscale),
                                 endx - startx, height, true);
                }
                g.draw3DRect(startx,
                             yOff +
                             (int) ((1 + ((offset + length) / cols)) * yscale) -
                             height / 2,
                             (int) (((offset + length) % cols) * xscale),
                             height, true);
                break;

            case 3:
                g.drawOval(x, y - height / 2, endx - x, height);
                for (int i = 1; i < k; i++) {
                    g.drawOval(startx, y - height / 2 + (int) (i * yscale),
                               endx - startx, height);
                }
                g.drawOval(startx,
                           yOff +
                           (int) ((1 + ((offset + length) / cols)) * yscale) -
                           height / 2,
                           (int) (((offset + length) % cols) * xscale), height);
                break;

            default:
                g.draw3DRect(x, y - height / 2, endx - x, height, true);
                for (int i = 1; i < k; i++) {
                    g.draw3DRect(startx, y - height / 2 + (int) (i * yscale),
                                 endx - startx, height, true);
                }
                g.draw3DRect(startx,
                             yOff +
                             (int) ((1 + ((offset + length) / cols)) * yscale) -
                             height / 2,
                             (int) (((offset + length) % cols) * xscale),
                             height, true);
                break;

            }
        }
    }

    void drawSequence(Graphics g, int rowId, int seqId, double len) {
        String lab = ">seq " + seqId;
        if (sequenceDB.getSequenceNo() > 0) {
            DSSequence theSequence = sequenceDB.getSequence(seqId);
            len = (double) theSequence.length();
            lab = theSequence.getLabel();

        }
        int y = yOff + rowId * yStep;
        int x = xOff + (int) (len * scale);
        g.setColor(Color.black);
        if (lab.length() > 10) {
            g.drawString(lab.substring(0, 10), 4, y + 3);
        } else {
            g.drawString(lab, 4, y + 3);
        }
        g.drawLine(xOff, y, x, y);
    }

    public int getSeqId(int y) {
        int seqId = (y - yOff + 5) / yStep;
        return seqId;
    }

    public int getSeqDx(int x) {
        double scale = Math.min(5.0,
                                (double) (this.getWidth() - 20 - xOff) /
                                (double) sequenceDB.getMaxLength());
        int seqDx = (int) ((double) (x - xOff) / scale);

        return seqDx;
    }

    //    public String asString() {
    //        String result = "";
    //        if (sequenceDB != null) {
    //            for (int seqId = 0; seqId < sequenceDB.getSequenceNo(); seqId++) {
    //                AnnotableSequence as = (AnnotableSequence) sequenceDB.
    //                    getSequence(seqId);
    //
    //                String match = "";
    //                for (int row = 0; row < selectedPatterns.size(); row++) {
    //                    IGetPattern pattern = (IGetPattern) selectedPatterns.get(
    //                        row);
    //                    IGetPatternMatchCollection matches = (
    //                        IGetPatternMatchCollection) as.get(pattern);
    //                    if ( (matches != null) && (matches.size() > 0)) {
    //
    //                        match += pattern.asString() + "\n";
    //                        for (int i = 0; i < matches.size(); i++) {
    //                            match +=
    //                                ( (IPatternMatch) matches.get(i)).
    //                                getOffset() +
    //                                "\t" +
    //                                ( (IPatternMatch) matches.get(i)).
    //                                getLength()
    //                                + "\t score:" +
    //                                ( (IPatternMatch) matches.get(i)).
    //                                getScore() +
    //                                "\t";
    //                        }
    //
    //                    }
    //
    //                }
    //                if (match.length() > 1) {
    //                    result = result + as.getLabel() + "\n" + match + "\n";
    //
    //                }
    //            }
    //        }
    //        return result;
    //    }

    //
//    public void this_mouseClicked(MouseEvent e) {
//        if (e.isMetaDown()) {
//            elementRightClicked(e);
//            return;
//        }
//
//        if (e.getClickCount() == 2) {
//            if (!isText) {
//                int y = e.getY();
//                selected = getSeqId(y);
//
//            }
//            this.flipIsText();
//            this.repaint();
//        }
//
//    }
//
//    /**
//     * elementRightClicked
//     *
//     * @param e MouseEvent
//     */
//    private void elementRightClicked(final MouseEvent e) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                itemListPopup.show(e.getComponent(), e.getX(), e.getY());
//            }
//        });
//
//    }
//
//    public void this_mouseMoved(MouseEvent e) {
//        if (!isText) {
//            mouseOverGraph(e);
//        } else {
//
//            mouseOverText(e);
//
//        }
//
//    }
//
//    private void mouseOverText(MouseEvent e) throws
//            ArrayIndexOutOfBoundsException {
//
//        if (sequenceDB == null) {
//            return;
//        }
//        int x1 = e.getX();
//        int y1 = e.getY();
//
//        Font f = new Font("Courier New", Font.PLAIN, 11);
//        Graphics g = this.getGraphics();
//        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                          RenderingHints.VALUE_ANTIALIAS_ON);
//        FontMetrics fm = g.getFontMetrics(f);
//        if (sequenceDB.getSequence(selected) == null ||
//            sequenceDB.getSequence(selected).getSequence() == null) {
//            return;
//        }
//        String asc = sequenceDB.getSequence(selected).getSequence();
//
//        Rectangle2D r2d = fm.getStringBounds(asc, g);
//        double xscale = (r2d.getWidth() + 3) / (double) (asc.length());
//        double yscale = 1.3 * r2d.getHeight();
//        int width = this.getWidth();
//        int cols = (int) (width / xscale) - 8;
//
//        int dis = (int) ((int) ((y1 - yOff - 1) / yscale) * cols + x1 / xscale -
//                         5);
//        if (sequenceDB.getSequence(selected) != null) {
//            if (((y1 - yOff - 1) / yscale > 0) && (dis > 0) &&
//                (dis <= sequenceDB.getSequence(selected).length())) {
//                this.setToolTipText("" + dis);
//            }
//        }
//
//        String display = "";
//        for (DSPattern pattern : patternMatches.keySet()) {
//            List<DSPatternMatch<DSSequence,
//                    DSSeqRegistration>> matches = patternMatches.get(pattern);
//            if ((matches != null) && (matches.size() > 0)) {
//                for (int i = 0; i < matches.size(); i++) {
//                    DSPatternMatch<DSSequence,
//                            DSSeqRegistration> match = matches.get(i);
//                    DSSequence sequence = match.getObject();
//                    if (selected == sequence.getSerial()) {
//                        DSSeqRegistration reg = match.getRegistration();
//                        if (dis >= reg.x1 && dis <= reg.x2) {
//                            display = "Pattern:" + pattern;
//                            displayInfo(display);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void mouseOverGraph(MouseEvent e) throws
//            ArrayIndexOutOfBoundsException {
//        int y = e.getY();
//        int seqid = getSeqId(y);
//        int x = e.getX();
//        if (sequenceDB == null) {
//            return;
//        }
//        int off = this.getSeqDx(x);
//        if (sequenceDB.getSequence(seqid) != null) {
//            if ((off <= sequenceDB.getSequence(seqid).length()) && (off > 0)) {
//                this.setToolTipText("" + off);
//            }
//        }
//
//        for (DSPattern pattern : patternMatches.keySet()) {
//            List<DSPatternMatch<DSSequence,
//                    DSSeqRegistration>> matches = patternMatches.get(pattern);
//            if (matches != null) {
//                for (int i = 0; i < matches.size(); i++) {
//                    DSPatternMatch<DSSequence,
//                            DSSeqRegistration> match = matches.get(i);
//                    DSSequence sequence = match.getObject();
//                    if (sequence.getSerial() == seqid) {
//                        DSSeqRegistration reg = match.getRegistration();
//                        if ((off > reg.x1 - 5) && (off < reg.x2 + 4)) {
//                            /**
//                             * todo need to make it more generalize
//                             */
//                            //                            display = "Pattern:" + pattern + " " +
//                            //                                "possible hit per 1 kb:" +
//                            //                                ( (TranscriptionFactor) pattern).getMatrix().
//                            //                                getRandom();
//
//                            displayInfo(pattern.toString());
//
//                        }
//                    }
//                }
//            }
//        }
//    }

    private void displayInfo(String display) {
//disabled.
        //                                this.setToolTipText(display);
//        if (jinfoPanel != null) {
//            Graphics g = jinfoPanel.getGraphics();
//            Font f = new Font("Courier New", Font.PLAIN, 11);
//            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                              RenderingHints.VALUE_ANTIALIAS_ON);
//            g.clearRect(0, 0, jinfoPanel.getWidth(), jinfoPanel.getHeight());
//            g.setFont(f);
//            g.drawString(display, 10, 20);
//
//        }
    }

    public void addAPattern(DSPattern<DSSequence, DSSeqRegistration> pt,
                            Display dis, List<DSPatternMatch<DSSequence,
                            DSSeqRegistration>> matches) {
//        if (pt != null && dis != null && matches != null) {
//            patternDisplay.put(pt, dis);
//            patternMatches.put(pt, matches);
//            repaint();
//        }
        if (patternTFMatches == null) {
            patternTFMatches = new HashMap<
                               CSSequence,
                               PatternSequenceDisplayUtil>();

        }
        PatternOperations.addTFMatches(patternTFMatches, matches, pt);
        initPanelView();

    }

//    public void addAPattern(DSPattern<DSSequence, DSSeqRegistration> pt,
//                            Display dis, List<DSPatternMatch<DSSequence,
//                            DSSeqRegistration>> matches) {
//        if (pt != null && dis != null && matches != null) {
//            patternDisplay.put(pt, dis);
//            patternMatches.put(pt, matches);
//            repaint();
//        }
//    }

    public void removePattern(DSPattern<DSSequence, DSSeqRegistration> pt) {
        patternMatches.remove(pt);
        patternDisplay.remove(pt);
        repaint();

    }

    public Hashtable<DSPattern<DSSequence, DSSeqRegistration>,
            List<DSPatternMatch<DSSequence,
            DSSeqRegistration>>> getPatternMatches() {
        return patternMatches;
    }

    public HashMap getPatternDisplay() {
        return patternDisplay;
    }

    public boolean isDisplaySeqPattern() {
        return displaySeqPattern;
    }

    public boolean isDisplayTF() {
        return displayTF;
    }

    public HashMap getPatternTFMatches() {
        return patternTFMatches;
    }

    /**
     * drawPattern
     *
     * @param g       Graphics
     * @param seqId   int
     * @param matches IGetPatternMatchCollection
     * @param r       Rectangle
     */
    private boolean drawPattern(Graphics g, List<DSPatternMatch<DSSequence,
                                DSSeqRegistration>> matches, Rectangle r,
                                Display dp) {
        if (matches != null) {
            for (int i = 0; i < matches.size(); i++) {
                DSPatternMatch<DSSequence,
                        DSSeqRegistration> match = matches.get(i);
                DSSequence sequence = match.getObject();
                DSSeqRegistration reg = match.getRegistration();
                int y = yOff + sequence.getSerial() * yStep;
                if (y > r.y) {
                    if (y > r.y + r.height) {
                        return true;
                    }
                    double x0 = reg.x1;
                    double dx = reg.length();
                    int xa = xOff + (int) (x0 * scale) + 1;
                    int xb = (int) (dx * scale) - 1;
                    if (xb < 4) {
                        xb = 4;
                    }
                    g.setColor(dp.getColor());
                    int height = (int) (dp.getHeight() * yStep);
                    switch (dp.getShape()) {

                    case 0:
                        g.drawRect(xa, y - height / 2, xb, height);
                        g.fillRect(xa, y - height / 2, xb, height);
                        break;

                    case 1:
                        g.drawRoundRect(xa, y - height / 2, xb, height, 2, 2);
                        ;
                        break;
                    case 2:
                        g.draw3DRect(xa, y - height / 2, xb, height, true);
                        ;
                        break;

                    case 3:
                        g.drawOval(xa, y - height / 2, xb, height);
                        ;
                        break;

                    default:
                        g.draw3DRect(xa, y - height / 2, xb, height, false);

                    }

                }
            }
        }
        return false;

    }

    public void setPatternDisplay(HashMap patternDisplay) {
        this.patternDisplay = patternDisplay;
    }

    public void setPatternMatches(Hashtable patternMatches) {
        this.patternMatches = patternMatches;
        repaint();
    }

    public void setDisplaySeqPattern(boolean displaySeqPattern) {
        this.displaySeqPattern = displaySeqPattern;
    }

    public void setDisplayTF(boolean displayTF) {
        this.displayTF = displayTF;
    }

    public void setPatternTFMatches(HashMap patternTFMatches) {
        this.patternTFMatches = patternTFMatches;
    }
}
