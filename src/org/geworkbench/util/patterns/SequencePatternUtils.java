package org.geworkbench.util.patterns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.geworkbench.bison.datastructure.biocollections.sequences.CSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;


/**
 * A class containing utility functions used in sequence analysis.
 * 
 * @author xiaoqing
 * @version $Id: SequencePatternUtils.java,v 1.1 2008-06-20 16:39:06 xiaoqing Exp $
 */
public class SequencePatternUtils {
/**
 * Write the sequence data into a file.
 * @return <code>true<code> if succeeds.
 * @param tempFile
 * @param sequences
 * @return
 */
	public static boolean createFile(File tempFile, CSSequenceSet sequences) {
		try {
			if (sequences == null || sequences.size() == 0) {
				return false;
			}
 			PrintWriter out = new PrintWriter(new FileOutputStream(tempFile));
			for (int i = 0; i < sequences.size(); i++) {

				CSSequence seq = (CSSequence) sequences.get(i);
				if (seq != null) {
					out.println(">" + seq.getLabel());
					out.println(seq.getSequence());
				}
			}

			out.flush();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

}
