/**
 * 
 */
package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geworkbench.parsers.InputFileFormatException; 
import org.geworkbench.util.AnnotationInformationManager.AnnotationType;
 
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;

/**
 * Actual parser of affy annotation.
 * 
 * This used to be in class AnnotationParser, whose main role is no longer parsing but the name stuck.
 * 
 * @author zji
 * @version $Id$
 *
 */
public abstract class AffyAnnotationParser {

	// this supports the complete unfinished parsing
	protected transient LabeledCSVParser parser;
	
	transient String affyId;
	 
	Map<String, AnnotationFields> parse(final File file, boolean ignoreDuplicate) throws InputFileFormatException {

		Map<String, AnnotationFields> markerAnnotation = new HashMap<String, AnnotationFields>();

		BufferedInputStream bis = null;
		 
		try {
			bis = new BufferedInputStream(new FileInputStream(file));

			CSVParser cvsParser = new CSVParser(bis);

			cvsParser.setCommentStart("#;!");

			parser = new LabeledCSVParser(cvsParser);			
		 
			while (parser.getLine() != null) {				
				AnnotationFields fields = parseOneLine();
				if (affyId == null)
				{
					//JOptionPane.showMessageDialog(null, "Your annotation file does not have correct format. \nPlease make sure you select correct annotation file type.", "Error", JOptionPane.ERROR_MESSAGE); 
					throw new InputFileFormatException(
							"your annotation file does not have correct format. ");
									 
				}
				if (!ignoreDuplicate && markerAnnotation.containsKey(affyId)) {
					String[] options = { "Skip duplicate",
							"Skip all duplicates", "Cancel", };
					int code = JOptionPane
							.showOptionDialog(
									null,
									"Duplicate entry. Probe Set ID="
											+ affyId
											+ ".\n"
											+ "Skip duplicate - will ignore this entry\n"
											+ "Skip all duplicates - will ignore all duplicate entries.\n"
											+ "Cancel - will cancel the annotation file processing.",
									"Duplicate entry in annotation file", 0,
									JOptionPane.QUESTION_MESSAGE, null,
									options, "Proceed");
					if (code == 0) { // only ignore this duplicate
						continue;
					} else if (code == 1) { // ignore all duplicates
						ignoreDuplicate = true;
						continue;
					}
					if (code == 2) {
						return null;
					}
				} else {
					markerAnnotation.put(affyId, fields);
				}
				 
			}
			// all fine.
		} catch (FileNotFoundException e) {			 
			throw new InputFileFormatException("your annotation file does not exist");
		 
		} catch (IOException e) {
			throw new InputFileFormatException("your annotation file does not have correct format.");
		} finally {
			try {
				bis.close();
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return markerAnnotation;
	}
	 
    abstract AnnotationFields parseOneLine(); 
    abstract AnnotationType getAnnotationType();
}
