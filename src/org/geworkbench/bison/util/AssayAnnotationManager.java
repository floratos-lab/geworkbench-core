package org.geworkbench.bison.util;

import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.ClassificationCriteria;

import javax.swing.*;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AssayAnnotationManager extends CSAnnotationManager {

    public static final int CASE = 1;
    public static final int CONTROL = 0;
    public static final int TEST = -100;
    public static final int IGNORE = -99;

    public final static ImageIcon redPinIcon = new ImageIcon(ClassificationCriteria.class.getResource("redpin.gif"));
    public final static ImageIcon whitePinIcon = new ImageIcon(ClassificationCriteria.class.getResource("whitepin.gif"));
    public final static ImageIcon eraserIcon = new ImageIcon(ClassificationCriteria.class.getResource("eraser.gif"));
    public final static ImageIcon markerIcon = new ImageIcon(ClassificationCriteria.class.getResource("marker.gif"));

    public AssayAnnotationManager() {
        addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Case", CASE));
        addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Control", CONTROL));
        addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Ignore", IGNORE));
        addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Test", TEST));
    }

    /**
     * Each class has an associated icon that is used whenever a microarray of that class wants to be
     * represented in an iconic fashion. This method returns the default icon for each classId.
     *
     * @param phClassId the class id
     * @return the default icon for that class id
     */
    static public ImageIcon getSelectionIcon(int classificationId) {
        ImageIcon icon = null;
        switch (classificationId) {
            case CASE:
                icon = redPinIcon;
                break;
            case CONTROL:
                icon = whitePinIcon;
                break;
            case TEST:
                icon = markerIcon;
                break;
            case IGNORE:
            default:
                icon = eraserIcon;
                break;
        }
        return icon;
    }

    static public ImageIcon getSelectionIcon(String label) {
        ImageIcon icon = null;
        if ("Case".equals(label)) {
            return redPinIcon;
        } else if ("Control".equals(label)) {
            return whitePinIcon;
        } else if ("Ignore".equals(label)) {
            return eraserIcon;
        }
        return eraserIcon;
    }

}
