package org.geworkbench.bison.parsers;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Parse context to customize parsing of Genepix files.
 * 
 * @author watkin
 * @author zji
 * @version $Id: GenepixParseContext.java,v 1.3 2008-10-07 19:05:50 jiz Exp $
 */
public class GenepixParseContext implements Serializable {
	private static final long serialVersionUID = 8430221091524748106L;

	/**
	 * List of possible column names from input file that can be parsed
	 */
	private static final String[] columnNames = { "Block", "Column", "Row",
			"Name", "ID", "X", "Y", "Dia.", "F635 Median", "F635 Mean",
			"F635 SD", "B635 Median", "B635 Mean", "B635 SD", "% > B635+1SD",
			"% > B635+2SD", "F635 % Sat.", "F532 Median", "F532 Mean",
			"F532 SD", "B532 Median", "B532 Mean", "B532 SD", "% > B532+1SD",
			"% > B532+2SD", "F532 % Sat.", "Ratio of Medians",
			"Ratio of Means", "Median of Ratios", "Mean of Ratios",
			"Ratios SD", "Rgn Ratio", "Rgn R²", "F Pixels", "B Pixels",
			"Sum of Medians", "Sum of Means", "Log Ratio", "Flags" };

	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_STRING = "String";
	public static final String TYPE_DOUBLE = "Double";

	/**
	 * Column datatypes for the columns listed in <code>columnNames</code> in
	 * the same order
	 */
	// Change Flags Values as String, following the request of Aris.
	private static final String[] columnTypes = { TYPE_INTEGER, TYPE_INTEGER,
			TYPE_INTEGER, TYPE_STRING, TYPE_STRING, TYPE_INTEGER, TYPE_INTEGER,
			TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_INTEGER, TYPE_INTEGER, TYPE_DOUBLE, TYPE_DOUBLE,
			TYPE_DOUBLE, TYPE_STRING };

	/**
	 * List of the column names (among those available in the Genepix file
	 * format and listed in
	 * {@link org.geworkbench.bison.parsers.GenepixParseContext#columnNames
	 * columnNames}) to be used in building the currrent
	 * <code>MicroarraySet</code>.
	 */
	private HashMap<String, Object> columnsToUse = new HashMap<String, Object>();

	/**
	 * Serializable fields.
	 */
	private final static ObjectStreamField[] serialPersistentFields = {
			new ObjectStreamField("columnNames", String[].class),
			new ObjectStreamField("columnTypes", String[].class),
			new ObjectStreamField("columnsToUse", HashMap.class) };

	/**
	 * Constructor
	 * 
	 * @param ctu
	 *            columns to use for parsing
	 */
	public GenepixParseContext(List<String> ctu) {
		for (int i = 0; i < columnNames.length; i++) {
			if (ctu.contains(columnNames[i])) {
				columnsToUse.put(columnNames[i], columnTypes[i]);
			}

		}

	}

	/**
	 * Gets the columns to use for parsing
	 * 
	 * @return columns to use for parsing
	 */
	public HashMap<String, Object> getColumnsToUse() {
		return columnsToUse;
	}

}
