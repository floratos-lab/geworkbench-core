package org.geworkbench.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author zji
 * @version $Id$
 */

public class CsvFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		if (f.getName().toLowerCase().endsWith("csv")) {
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return "CSV files";
	}

}
