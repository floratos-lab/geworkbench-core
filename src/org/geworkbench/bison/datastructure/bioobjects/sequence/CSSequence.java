package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.io.Serializable;
import java.util.regex.Matcher;

import org.geworkbench.bison.datastructure.properties.CSExtendable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */

public class CSSequence implements DSSequence, Serializable {

	private static final long serialVersionUID = 7157849532235539033L;

    static final String[] repeats = {"(at){5,}", "a{7,}", "c{7,}", "g{7,}",
                                    "t{7,}"};
    static final java.util.regex.Pattern[] repeatPattern = new java.util.regex.
            Pattern[repeats.length];

    private String id = "";
    private int serial = -1;
    private boolean isEnabled = true;
    private String sequence = new String();
    private String label = new String();
    /**
     * Used in the implementation of the <code>Extendable</code> interface.
     */
    protected CSExtendable extend = new CSExtendable();
    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    protected String description = null;

    public CSSequence() {
    }

    public CSSequence(String l, String s) {
        setSequence(s);
        label = l;
    }

    public String getLabel() {
        return label;
    }

    public String getSequence() {
        return sequence;
    }

    public CSSequence getSubSequence(int from, int to) {
        if (from >= 0 && to >= 0 && from < sequence.length() &&
            to < sequence.length()) {
            return new CSSequence(getLabel(), getSequence().substring(from, to));
        }
        return null;
    }

    public void setLabel(String l) {
        label = l;
    }

    public void setSequence(String s) {
        sequence = s;
    }

    public String toString() {
        return label;
    }

    public int length() {
        return sequence.length();
    }

    public void maskRepeats() {
        for (int i = 0; i < repeats.length; i++) {
            if (repeatPattern[i] == null) {
                repeatPattern[i] = java.util.regex.Pattern.compile(repeats[i]);
            }
            Matcher m = repeatPattern[i].matcher(sequence);
            sequence = m.replaceAll("#########");
        }
    }

    public void setID(String _id) {
        id = _id;
    }

    public String getID() {
        return id;
    }

    public void setSerial(int _serial) {
        serial = _serial;
    }

    public int getSerial() {
        return serial;
    }

    public boolean enabled() {
        return isEnabled;
    }

    public void enable(boolean status) {
        isEnabled = status;
    }

    public void clearName(String name) {
        extend.clearName(name);
    }

    public void forceUniqueValue(String name) {
        extend.forceUniqueValue(name);
    }

    public void allowMultipleValues(String name) {
        extend.allowMultipleValues(name);
    }

    public boolean isUniqueValue(String name) {
        return extend.isUniqueValue(name);
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void addNameValuePair(String name, Object value) {
        extend.addNameValuePair(name, value);
    }

    public Object[] getValuesForName(String name) {
        return extend.getValuesForName(name);
    }

    public void shuffle() {
        char[] tokens = new char[sequence.length()];
        for (int i = 0; i < sequence.length(); i++) {
            tokens[i] = sequence.charAt((int) (Math.random() * sequence.length()));
        }
        sequence = new String(tokens);
    }

    public static String reverseString(String s) {
        if(s==null){
            return null;
        }
        int m = s.length();
        char[] r = new char[m];
        for (int i = 0; i < m; i++) {
            r[i] = getComplementChar(s.charAt(m - i - 1));
        }
        return new String(r);
    }

    private static char getComplementChar(char originChr) {
        if (originChr == 'A' || originChr == 'a') {
            return 'T';
        } else if (originChr == 'T' || originChr == 't') {
            return 'A';
        }
        if (originChr == 'G' || originChr == 'g') {
            return 'C';
        }
        if (originChr == 'C' || originChr == 'c') {
            return 'G';
        } else {
            return originChr;
        }

    }

}
