package org.geworkbench.util.sequences;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version $Id$
 */
public class PatFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        if (f.getName().endsWith("pat")) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return "Motif Files (*.pat)";
    };
}
