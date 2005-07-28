package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.engine.parsers.AffyParseContext;
import org.geworkbench.engine.parsers.NCIParseContext;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Implementation of {@link org.geworkbench.engine.model.microarray.AffyMarkerValue
 * AffyMarkerValue}.
 */
public class CSAffyMarkerValue extends CSMarkerValue implements DSAffyMarkerValue, Serializable {
    /**
     * Stores the contents of the "Detection" column from the Affy input file that
     * corresponds to this value.
     */
    char detectionStatus = '\0';
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("detectionStatus", char.class)};

    public CSAffyMarkerValue() {
        setMissing(true);
    }

    /**
     * Constructs a <code>AffyMarkerValue</code> object from the contents of
     * the <code>AffyParseContext</code> argument.
     *
     * @param val The parse context used for the initialization.
     */
    public CSAffyMarkerValue(AffyParseContext val) {
        Map columns = val.getColumnsToUse();
        Object value = null;
        if (columns.containsKey("Probe Set Name")) {
            value = columns.get("Probe Set Name");
        }

        // Notice below that there are values "competing" for the same semantic concept.
        // E.g., "Avg Diff", "Signal" can both populate AffyMarkerValue.signal. The
        // relative ordering of the if-blocks corresponding to such values imposes
        // a relative importance that resolves conflicts: e.g., if
        // both "Avg Diff" and "Signal" are present, "Signal" will be preferred.
        if (columns.containsKey("Avg Diff")) {
            value = columns.get("Avg Diff");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Signal")) {
            value = columns.get("Signal");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Log2(ratio)")) {
            value = columns.get("Log2(ratio)");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Detection p-value")) {
            value = columns.get("Detection p-value");
            if (value instanceof Double)
                this.setConfidence(((Double) value).doubleValue());
        }

        if (columns.containsKey("Abs Call")) {
            value = columns.get("Abs Call");
            if (value instanceof Character)
                this.detectionStatus = ((Character) value).charValue();
        }

        if (columns.containsKey("Detection")) {
            value = columns.get("Detection");
            if (value instanceof Character)
                this.detectionStatus = ((Character) value).charValue();
        }

    }

    /**
     * Constructs a <code>AffyMarkerValue</code> object from the contents of
     * the <code>NCIParseContext</code> argument.
     *
     * @param val The parse context used for the initialization.
     */
    public CSAffyMarkerValue(NCIParseContext val) {
        HashMap columns = val.getColumnsToUse();
        Object value = null;
        if (columns.containsKey("Probe Set Name")) {
            value = columns.get("Probe Set Name");
            //      if (value instanceof String)
            //        this.markerInfo = new MarkerInfoImpl((String)value);
        }

        // Notice below that there are values "competing" for the same semantic concept.
        // E.g., "Avg Diff", "Signal" can both populate AffyMarkerValue.signal. The
        // relative ordering of the if-blocks corresponding to such values imposes
        // a relative importance that resolves conflicts: e.g., if
        // both "Avg Diff" and "Signal" are present, "Signal" will be preferred.
        if (columns.containsKey("Avg Diff")) {
            value = columns.get("Avg Diff");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Signal")) {
            value = columns.get("Signal");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Log2(ratio)")) {
            value = columns.get("Log2(ratio)");
            if (value instanceof Double) {
                setValue(((Double) value).doubleValue());
                setMissing(false);
            }

        }

        if (columns.containsKey("Detection p-value")) {
            value = columns.get("Detection p-value");
            if (value instanceof Double)
                this.setConfidence(((Double) value).doubleValue());
        }

        if (columns.containsKey("Abs Call")) {
            value = columns.get("Abs Call");
            if (value instanceof Character)
                this.detectionStatus = ((Character) value).charValue();
        }

        if (columns.containsKey("Detection")) {
            value = columns.get("Detection");
            if (value instanceof Character)
                this.detectionStatus = ((Character) value).charValue();
        }

    }

    /**
     * Creates a copy of the designated <code>AffyMarkerValueImpl</code>. The
     * copy maintains the physical link to the argument's associated
     * <code>MarkerInfo</code> and <code>Microarray</code> objects.
     *
     * @param amvi The value to copy.
     */
    CSAffyMarkerValue(CSAffyMarkerValue amvi) {
        setValue(amvi.value);
        this.detectionStatus = amvi.detectionStatus;
        this.setConfidence(amvi.getConfidence());
        this.setMissing(amvi.isMissing());
    }

    public double getDisplayValue() {
        return getValue();
    }

    public boolean isPresent() {
        return (detectionStatus == 'P' || detectionStatus == 'p') ? true : false;
    }

    /**
     * Sets the detection level of this affy marker value to "Present".
     *
     * @param present
     */
    public void setPresent(boolean present) {
        detectionStatus = (present == true ? 'P' : '\0');
    }

    public boolean isMarginal() {
        return (detectionStatus == 'M' || detectionStatus == 'm') ? true : false;
    }

    /**
     * Sets the detection level of this affy marker value to "Marginal".
     *
     * @param marginal
     */
    public void setMarginal(boolean marginal) {
        detectionStatus = (marginal == true ? 'M' : '\0');
    }

    public boolean isAbsent() {
        return (detectionStatus == 'A' || detectionStatus == 'a') ? true : false;
    }

    /**
     * Sets the detection level of this affy marker value to "Absent".
     *
     * @param marginal Marginality of this Marker
     */
    public void setAbsent(boolean absence) {
        detectionStatus = (absence == true ? 'A' : '\0');
    }

    /**
     * @return A copy of the marker value. The associated <code>MarkerInfo</code>
     *         is copied as well.
     */
    public DSMarkerValue deepCopy() {
        CSAffyMarkerValue copy = new CSAffyMarkerValue(this);
        return (DSMarkerValue) copy;
    }

    /**
     * This method returns the dimensionality of the marker. E.g., Genotype markers are 2-dimensional
     * while Allele/Haplotype markers are 1-dimensional
     *
     * @return int the dimensionality of the marker.
     */
    public int getDimensionality() {
        return 1;
    }

    public int compareTo(Object o) {
        return Double.compare(((CSAffyMarkerValue) o).getValue(), getValue());
    }
}
