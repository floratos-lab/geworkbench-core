package org.geworkbench.bison.parsers;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Parse context to customize parsing of Affymetrix files
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class SOFTParseContext implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7299827979956398558L;
	/**
     * List of possible column names from input file that can be parsed
     */
    public static final String[] columnNames = {"ID_REF", "VALUE", "ABS_CALL", "DETECTION_P"};
    /**
     * Column datatypes for the columns listed in <code>columnNames</code>
     * in the same order
     */
    static final String[] columnTypes = {"String", "Double", "Character", "Double"};
    /**
     * List of the column names (among those available in the Affymetrix file format and
     * listed in {@link org.geworkbench.bison.parsers.AffyParseContext#columnNames
     * columnNames}) to be used in building the current <code>MicroarraySet</code>.
     */
    Hashtable columnsToUse = new Hashtable();
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {//new ObjectStreamField("columnNames", String[].class),
        //new ObjectStreamField("columnTypes", String[].class),
        new ObjectStreamField("columnsToUse", Hashtable.class)};

    /**
     * Default Constructor
     */
    public SOFTParseContext() {
    }

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public SOFTParseContext(List ctu) {
        columnsToUse.clear();
        for (int i = 0; i < columnNames.length; i++) {
            if (ctu.contains(columnNames[i])) {
                columnsToUse.put(new String(columnNames[i]), new String(columnTypes[i]));
            }

        }

    }

    /**
     * Gets the columns to use for parsing
     *
     * @return columns to use for parsing
     */
    public Map getColumnsToUse() {
        return columnsToUse;
    }

    /**
     * Generates a deep copy of this parse context
     *
     * @return
     */
    public SOFTParseContext deepCopy() {
        SOFTParseContext copy = null;
        try {
            copy = (SOFTParseContext) this.getClass().newInstance();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }

        copy.columnsToUse.putAll(columnsToUse);
        return copy;
    }

}
