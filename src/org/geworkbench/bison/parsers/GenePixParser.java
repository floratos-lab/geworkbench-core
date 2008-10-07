package org.geworkbench.bison.parsers;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * A parser of GenePix gpr file.
 * @author manjunath
 * @version $Id: GenePixParser.java,v 1.6 2008-10-07 15:56:10 jiz Exp $
 */
public class GenePixParser {

    /**
     * List of the column names (among those available in the Geenpix gpr file format and
     * listed in {@link org.geworkbench.bison.parsers.GenepixParseContext#columnNames
     * columnNames}) to be used in building the currrent <code>MicroarraySet</code>.
     */
    private List<String> columnsToUse = null;

    /**
     * Indexing of columns parsed to an <code>Integer</code> key
     */
    private Hashtable<Integer, String> columnOrder = null;

    /**
     * Bit to specify if a header was found in the file being parsed
     */
    private boolean headerFound = false;

    /**
     * New <code>Microarray</code> instance into which the data is loaded on
     * parsing
     */
    private DSMicroarray microarray = null;

    private int markerIndex = 0;
    private Vector<String[]> accessions = null;;
    private TreeSet<String> flagsValue = null;

   /**
    * 
    * @param ctu
    */
    public GenePixParser(List<String> ctu) {
        columnOrder = new Hashtable<Integer, String>();
        headerFound = false;
        accessions = new Vector<String[]>();
        flagsValue = new TreeSet<String>();
        markerIndex = 0;

        columnsToUse = ctu;
    }

    private int idIndex = 0;
    private int nameIndex = 0;

    /** Process one line of data file. */
    public void process(String line) {
        if(line.indexOf("F635 Median") >= 0) {// the case of header line. this should be handled the first
            headerFound = true;
            String[] tokens = line.split("[\\t\\n]+");
            for(int i=0; i<tokens.length; i++) {
            	String t = tokens[i];

            	if(t.equals("ID") || t.equals("\"ID\"")) {
                    idIndex = i;
            	} else if(t.equals("Name") || t.equals("\"Name\"")) {
                    nameIndex = i;
            	}
            }
        } else if(headerFound) { // now is the case after the header is reached and has been processed in the first if case
            String[] tokens = line.split("[\\t\\n]+");
            accessions.add(new String[]{tokens[idIndex], tokens[nameIndex]});
        }
    	// before the header is reached, this method does nothing and just return
    }

    /**
     * Retrieve the accession values pre-processed from the input datafile
     *
     * @return Vector
     */
    public Vector<String[]> getAccessions() {
        return accessions;
    }

    /**
     * Resets the parser
     */
    public void reset() {
        headerFound = false;
    }

    /**
     * Sets the <code>Microarray</code> to be populated
     *
     * @param m IMicroarray
     */
    public void setMicroarray(DSMicroarray m) {
        microarray = m;
    }

    /**
     * Returns the microarray produced by reading the input file.
     *
     * @return microarray produced by reading the input file.
     */
    public DSMicroarray getMicroarray() {
        return microarray;
    }

    /**
     * Reads the argument line (coming from an Genepix gpr formatted file)
     * and parses out experiment information and individual marker values. It is
     * assumed that each file contains exactly one microarray. The parsing will
     * result in a <code>MicroarraySet</code> object containing exactly one
     * <code>Microarray</code> (the one described by the file).
     *
     * @param line Input file line.
     */
    @SuppressWarnings("unchecked")
	public void parseLine(String line) {
        if(line.indexOf("F635 Median") >=0) { // Read in the header line
            headerFound = true;
            String[] tokens = line.split("[\\t\\n]+");
            for(int i=0; i<tokens.length; i++) {
            	String t = tokens[i];

                if (t.startsWith("\"") && t.endsWith("\"")) {
                    t = t.split("\"")[1];
                }
                if (columnsToUse.contains(t)) {
                    columnOrder.put(new Integer(i), t);
                }
            }
        } else if(headerFound) { // after the head line
            GenepixParseContext context = new GenepixParseContext(columnsToUse);
            
            Map<String, Object> ctu = context.getColumnsToUse();
            Object value = null;

            String[] tokens = line.split("[\\t\\n]+");
            for(int i=1; i<tokens.length; i++) {
            	String t = tokens[i];
                String column = (String) columnOrder.get(new Integer(i));
                if (column != null) {
                    String type = (String) ctu.get(column);
                    if (type.equals("String")) {
                        value = new String(t.toCharArray());
                    } else if (type.equals("Integer")) {//FIXME these are defined GenepixParseContext
                        value = Integer.valueOf(t);
                    } else if (type.equals("Double")) {//FIXME these are defined GenepixParseContext
                        value = Double.valueOf(t);
                    } else if (type.equals("Character")) {//FIXME these are defined GenepixParseContext
                        value = new Character(t.charAt(0));
                    }
                    if (value != null) {
                        ctu.put(column, value);
                    }
                }
            }
            populateValues(ctu,
                    (DSGenepixMarkerValue) microarray.
                            getMarkerValue(markerIndex++));
        }
    }

    /**
	 * Calculate the signal value from the channel values.
	 */
	private void populateValues(Map<String, Object> columns,
			DSGenepixMarkerValue gmv) {
		Object value = null;
		double ch1f = 0d, ch2f = 0d, ch1b = 0d, ch2b = 0d;

		boolean medianMissing = false;
		if (!CSGenepixMarkerValue.getComputeSignalMethod().usesMean()) {
			if (columns.containsKey("F532 Median")) {
				value = columns.get("F532 Median");
				if (value instanceof Double) {
					ch1f = ((Double) value).doubleValue();
				}
			} else {
				medianMissing = true;
			}
			if (columns.containsKey("B532 Median")) {
				value = columns.get("B532 Median");
				if (value instanceof Double) {
					ch1b = ((Double) value).doubleValue();
				}
			} else {
				medianMissing = true;
			}
			if (columns.containsKey("F635 Median")) {
				value = columns.get("F635 Median");
				if (value instanceof Double) {
					ch2f = ((Double) value).doubleValue();
				}
			} else {
				medianMissing = true;
			}
			if (columns.containsKey("B635 Median")) {
				value = columns.get("B635 Median");
				if (value instanceof Double) {
					ch2b = ((Double) value).doubleValue();
				}
			} else {
				medianMissing = true;
			}
		}
		
		if (columns.containsKey("Flags")) {
			value = columns.get("Flags");
			if (value instanceof String) {

				if (!value.equals("0")) {
					flagsValue.add((String) value);
				}

			}
			gmv.setFlag((String) value);
		}

		if (CSGenepixMarkerValue.getComputeSignalMethod().usesMean()
				|| medianMissing) {
			if (columns.containsKey("F532 Mean")) {
				value = columns.get("F532 Mean");
				if (value instanceof Double) {
					ch1f = ((Double) value).doubleValue();
				}
			}
			if (columns.containsKey("B532 Mean")) {
				value = columns.get("B532 Mean");
				if (value instanceof Double) {
					ch1b = ((Double) value).doubleValue();
				}
			}
			if (columns.containsKey("F635 Mean")) {
				value = columns.get("F635 Mean");
				if (value instanceof Double) {
					ch2f = ((Double) value).doubleValue();
				}
			}
			if (columns.containsKey("B635 Mean")) {
				value = columns.get("B635 Mean");
				if (value instanceof Double) {
					ch2b = ((Double) value).doubleValue();
				}
			}
		}

		gmv.setCh1Fg(ch1f);
		gmv.setCh1Bg(ch1b);
		gmv.setCh2Fg(ch2f);
		gmv.setCh2Bg(ch2b);

		gmv.computeSignal();
	}

}
