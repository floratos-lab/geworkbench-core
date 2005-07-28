package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSAssay {
    static DSAnnotationManager annotations = new CSAnnotationManager();
    protected DSAnnotator<DSAnnotLabel, DSAnnotValue> annotation = new Annotator<DSAnnotLabel, DSAnnotValue>();
    public static int CASE = 1;
    public static int CONTROL = 0;
    public static int TEST = -100;
    public static int IGNORE = -99;

    static {
        annotations.addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Case", CASE));
        annotations.addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Control", CONTROL));
        annotations.addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Ignore", IGNORE));
        annotations.addLabelValue(new CSAnnotLabel("Selection"), new CSAnnotValue("Test", TEST));
    }

    public CSAssay() {
        annotation.setAnnotation(new CSAnnotLabel("Selection"), new CSAnnotValue("Control", CONTROL));
    }

    public int getAnnotationNo() {
        return annotations.size();
    }

    public DSAnnotator getAnnotator() {
        return annotation;
    }

    public double getPValue() {
        return 0.0;
    }

    public void setPValue(double pValue) {
    }
}
