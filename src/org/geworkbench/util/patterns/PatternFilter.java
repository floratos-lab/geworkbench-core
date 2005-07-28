package org.geworkbench.util.patterns;

import org.geworkbench.util.sequences.SequenceDB;

import javax.swing.*;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface PatternFilter {
    /**
     * This interface is used to filter a set of patterns
     * For instance, this could eliminate patterns that
     * have specific types of distributions
     */
    ArrayList filter(ArrayList patterns, SequenceDB sequenceDB);

    JPanel getParameterPanel();
}
