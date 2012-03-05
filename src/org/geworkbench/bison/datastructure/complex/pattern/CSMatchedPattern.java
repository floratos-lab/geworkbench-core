package org.geworkbench.bison.datastructure.complex.pattern;

import org.geworkbench.bison.datastructure.properties.DSSequential;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version $Id$
 */
public class CSMatchedPattern <T extends DSSequential, R> implements DSMatchedPattern<T, R> {
    protected List<DSPatternMatch<T, R>> matches = new ArrayList<DSPatternMatch<T, R>>();
    protected double zScore;

    public CSMatchedPattern() {
        zScore = 1.0;
    }

    public List<DSPatternMatch<T, R>> matches() {
        return matches;
    }

    public boolean containsObject(T object) {
        for (DSPatternMatch<T, R> match : matches) {
            if (match.getObject().equals(object)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSupport(CSMatchedPattern<T, R> pat) {
        int j = 0;
        int shared = 0;
        for (int i = 0; i < pat.matches.size(); i++) {
            if (j >= matches.size()) {
                break;
            }
            if (matches.get(j).getObject().getSerial() == pat.matches.get(i).getObject().getSerial()) {
                j++;
                shared++;
            } else if (matches.get(j).getObject().getSerial() < pat.matches.get(i).getObject().getSerial()) {
                j++;
                i--;
            }
        }
        System.out.println("Support Shared: " + shared + ", No: " + pat.matches.size());
        if ((double) shared / (double) pat.matches.size() >= 0.8) {
            return true;
        }
        return false;
    }

    public double getPValue() {
        return zScore;
    }

    public void setPValue(double pValue) {
        zScore = pValue;
    }

    public int getSupport() {
        return matches.size();
    }

    /**
     * @todo to be fixed
     */
    public int getUniqueSupport() {
        return matches.size();
    }

    public DSPatternMatch<T, R> get(int i) {
        return matches.get(i);
    }

    public void setLabel(String label) {

    }

    public String getLabel() {
        return toString();
    }
}
