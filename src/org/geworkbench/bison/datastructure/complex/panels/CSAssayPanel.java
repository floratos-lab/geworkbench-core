package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.*;

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
public class CSAssayPanel <T extends DSNamed, Q extends Comparable> extends CSAnnotPanel<T, Q> implements DSAnnotatedPanel<T, Q> {
    static AssayAnnotationManager annotations = new AssayAnnotationManager();
    protected DSAnnotator<DSAnnotLabel, String> annotation = new Annotator<DSAnnotLabel, String>();

    public CSAssayPanel() {
        super("");
        annotation.setAnnotation(new CSAnnotLabel("Selection"), "Control");
    }

    public CSAssayPanel(String label) {
        super(label);
        annotation.setAnnotation(new CSAnnotLabel("Selection"), "Control");
    }

    public int getAnnotationNo() {
        return annotations.size();
    }

    public DSAnnotator<DSAnnotLabel, String> getAnnotator() {
        return annotation;
    }

    public AssayAnnotationManager getAnnotations() {
        return annotations;
    }


}
