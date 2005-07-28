package org.geworkbench.bison.datastructure.bioobjects.microarray;


/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

abstract public class CSMarkerValue implements DSMutableMarkerValue {

    /**
     * positive values are normal, negative values are masked
     */
    protected float value = 0.0F;

    /**
     * confidence:
     * <  0.0  -> Masked
     * == 0.0  -> Undefined
     * <= 0.33 -> Absent
     * <= 0.66 -> Marginal
     * >  0.66 -> Present
     */
    protected float confidence = 0.0F;

    /**
     * Default Constructor
     */
    public CSMarkerValue() {
    }

    public CSMarkerValue(CSMarkerValue m) {
        this.value = m.value;
        this.confidence = m.confidence;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double s) {
        value = (float) s;
    }

    public void setMissing(boolean flag) {
        if (flag) {
            confidence = 0.0F;
        } else {
            if (confidence < 0) {
                confidence *= -1.0;
            } else if (confidence == 0) {
                confidence = 1.0F;
            }
        }
    }

    public void setAbsent() {
        confidence = 0.1F;
    }

    public void setPresent() {
        confidence = 0.9F;
    }

    public void setUndefined() {
        confidence = 0.0F;
    }

    public void setMarginal() {
        confidence = 0.5F;
    }

    public boolean isValid() {
        return (confidence > 0);
    }

    public boolean isAbsent() {
        return ((confidence > 0) && (confidence <= .33));
    }

    public boolean isMarginal() {
        return ((confidence > 0.33) && (confidence <= .66));
    }

    public boolean isPresent() {
        return (confidence > 0.66);
    }

    public boolean isMissing() {
        return (confidence == 0.0);
    }

    public boolean isMasked() {
        return (confidence < 0.0);
    }

    public void mask() {
        if (confidence > 0) {
            confidence *= -1.0;
        }
    }

    public void unmask() {
        if (confidence < 0) {
            confidence *= -1.0;
        }
    }

    public void Unmask() {
        if (confidence < 0) {
            confidence *= -1.0;
        }
    }

    public char getStatusAsChar() {
        char result = 'U';
        double c = Math.abs(confidence);
        if (c > 0.66) {
            result = 'P';
        } else if (c > 0.33) {
            result = 'M';
        } else if (c > 0.0) {
            result = 'A';
        } else if (c == 0.0) {
            result = 'U';
        }
        if (confidence < 0.0) {
            result = Character.toLowerCase(result);
        }
        return result;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double c) {
        confidence = (float) c;
    }

    /**
     * Compares two expression markers
     *
     * @param m MarkerValue marker to be compared to
     * @return boolean equality
     */
    public boolean equals(Object obj) {
        if (obj instanceof DSMarkerValue) {
            DSMarkerValue m = (DSMarkerValue) obj;
            if ((isMissing() || m.isMissing()) || (!isValid() || !m.isValid())) {
                return false;
            } else {
                return (getValue() == m.getValue());
            }
        } else {
            return false;
        }
    }
}
