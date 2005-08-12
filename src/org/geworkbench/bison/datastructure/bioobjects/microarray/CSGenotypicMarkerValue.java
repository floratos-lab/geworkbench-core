package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.util.Range;
import org.geworkbench.bison.datastructure.bioobjects.markers.genotype.CSGenotypeMarker;

import java.awt.*;
import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class CSGenotypicMarkerValue extends CSMarkerValue implements Serializable, DSGenotypicMarker {

    /**
     * Formats values to be displayed
     */
    protected static DecimalFormat formatter = new DecimalFormat("##.##");

    /**
     * A means of storing each of the two alleles in one int in 16 bits
     */
    private final static int alleleFactor = (1 << 16);

    /**
     * Bit to specify if this marker contains two alleles
     */
    public boolean isGT = true;

    /**
     * Constructor
     *
     * @param mg MarkerGenotype to be cloned
     */
    public CSGenotypicMarkerValue(CSGenotypicMarkerValue mg) {
        value = mg.value;
        confidence = mg.confidence;
        isGT = mg.isGT;
    }

    /**
     * Constructor to create this marker from two alleles
     *
     * @param a1 int allele
     * @param a2 int allele
     */
    public CSGenotypicMarkerValue(int a1, int a2) {
        setGenotype(a1, a2);
    }

    /**
     * Constructor to create this marker from one allele
     *
     * @param allele_1 int
     */
    public CSGenotypicMarkerValue(int allele_1) {
        value = allele_1;
        isGT = false;
    }

    /**
     * Sets the primary allele
     *
     * @param allele int
     */
    public void setAllele(int allele) {
        value = allele;
        isGT = false;
    }

    /**
     * Sets genotype
     *
     * @param allele_1 int
     * @param allele_2 int
     */
    public void setGenotype(int allele_1, int allele_2) {
        this.value = allele_1 + allele_2 * alleleFactor;
        isGT = true;
    }

    /**
     * Sets the genotype as an int
     *
     * @param genotype int
     */
    protected void setGenotype(int genotype) {
        value = genotype;
    }

    /**
     * Gets either of the two alleles
     *
     * @param id int either of the two dimensions
     * @return int allele as int
     */
    public int getAllele(int id) {
        switch (id) {
            case 0:
                return (int) value % alleleFactor;
            case 1:
                return (int) value / alleleFactor;
        }
        return 0;
    }

    /**
     * Obtains a <code>String</code> representation of the genotype
     *
     * @return String representation
     */
    public String representation() {
        String representation = null;
        if (isGT) {
            representation = new String("G:" + getAllele(0) + "|" + getAllele(1));
        } else {
            representation = new String("A:" + getAllele(0));
        }
        return representation;
    }

    /**
     * Gets a <code>String</code> representation of this marker
     *
     * @return String
     */
    public String toString() {
        String string = null;
        String mask = null;
        if (isMasked()) {
            mask = new String("X");
        } else {
            mask = new String("");
        }
        if (!isMissing()) {
            string = new String(getAllele(0) + "_" + getAllele(1) + "\t" + getStatusAsChar());
            //            string = new String(formatter.format(getValue()) + "\t" +
            //                                getStatusAsChar());
        } else {
            string = new String("?" + "\t" + getStatusAsChar());
        }
        return string;
    }

    /**
     * @param m IMarker
     * @return boolean
     */
    public boolean equals(DSMarkerValue m) {
        CSGenotypicMarkerValue aMarker = (CSGenotypicMarkerValue) m;
        if ((aMarker == null) || (isAbsent() || aMarker.isAbsent())) {
            return false;
        }
        return (value == aMarker.value);
    }

    /**
     * This method returns the dimensionality of the marker. Genotype markers
     * are 2-dimensional while Allele/Haplotype markers are 1-dimensional
     *
     * @return the dimensionality of the marker.
     */
    public int getDimensionality() {
        if (isGT) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Gets a copy of this marker
     *
     * @return MarkerValue
     */
    public DSMarkerValue deepCopy() {
        DSMarkerValue copy = new CSGenotypicMarkerValue(this);
        return copy;
    }

    public void parse(String signal, int gtBase) {
        int a1;
        int a2;
        String[] parseableValue = signal.split(":");
        String[] allele = parseableValue[parseableValue.length - 1].split("[| /]");
        switch (allele.length) {
            case 1:
                int v = Integer.parseInt(allele[0]);
                setGenotype(v / gtBase, v % gtBase);
                setMissing(v == 0);
                break;
            case 2:
                a1 = Integer.parseInt(allele[0]);
                a2 = Integer.parseInt(allele[1]);
                setGenotype(a1, a2);
                setMissing((a1 == 0) || (a2 == 0));
                break;
            default:
                a1 = Integer.parseInt(allele[0]);
                a2 = Integer.parseInt(allele[allele.length - 1]);
                setGenotype(a1, a2);
                setMissing((a1 == 0) || (a2 == 0));
                break;
        }
    }

    public void parse(String signal, String status, int gtBase) {
        try {
            char c = status.charAt(0);
            if (Character.isLowerCase(c)) {
                mask();
            }
            parse(signal, gtBase);
        } catch (NumberFormatException e) {
            setGenotype(0, 0);
            setMissing(true);
        }
    }

    public Class getMarkerStatsClass() {
        return CSGenotypeMarker.class;
    }

    public Color getColor(org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker stats, float intensity) {
        CSGenotypeMarker gtStats = (CSGenotypeMarker) stats;
        org.geworkbench.bison.util.Range range = gtStats.getRange();
        Color color = null;
        float v = (float) ((value - (range.max + range.min) / 2) / (range.max - range.min)) * intensity;
        if (v > 0) {
            v = Math.min(1.0F, v);
            color = new Color(0F, v, 0F);
        } else {
            v = Math.min(1.0F, -v);
            color = new Color(0F, 1F, v);
        }
        return color;
    }

    public Color getAbsColor(org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker stats, float intensity) {
        CSGenotypeMarker gtStats = (CSGenotypeMarker) stats;
        Range range = gtStats.getRange();
        if (getAllele(0) == getAllele(1)) {
            if (getAllele(0) == 1) {
                return Color.yellow;
            } else if (getAllele(0) == 2) {
                return Color.yellow.darker();
            }
            float v = (float) getAllele(0) / (float) range.max * intensity;
            v = Math.max(v, -1.0F);
            v = Math.min(v, +1.0F);
            return new Color(v, 0F, 0F);
        } else {
            float v1 = (float) getAllele(0) / (float) range.max * intensity;
            v1 = Math.max(v1, -1.0F);
            v1 = Math.min(v1, +1.0F);
            float v2 = (float) getAllele(1) / (float) range.max * intensity;
            v2 = Math.max(v2, -1.0F);
            v2 = Math.min(v2, +1.0F);
            return new Color(v1, v2, v2);
        }
    }

    public int compareTo(Object o) {
        return Double.compare(((CSAffyMarkerValue) o).getValue(), getValue());
    }
}
