/**
 * Copyright 2008 Center for Computational Biology and Bioinformatics
 */
package org.geworkbench.bison.util;

import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

/**
 * A class containing utility functions used in sequence analysis.
 * 
 * @author zji
 * @version $Id: SequenceUtils.java,v 1.1 2008-05-06 19:14:30 jiz Exp $ 
 */
public class SequenceUtils {
	// add 'E' in the list because the promoter panel local cache has lots of E for exon sequences.
    private final static java.util.regex.Pattern dnaPattern = java.util.regex.Pattern.compile(
            "[^#acgtnxACGTNXE]");

    /**
     * Check if the DSSequence argument represents a well formed DNA sequence for the purpose of a BLAST submission.
     *
     * @return <code>true</code> if it is a DNA sequence, <code>false</code> if not.
     */
    public static boolean isValidDNASeqForBLAST(DSSequence dsSequence) {
        if (dnaPattern.matcher(dsSequence.getSequence()).find())
        	return false;
        else
        	return true;
	}
}
