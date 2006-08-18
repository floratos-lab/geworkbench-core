package org.geworkbench.util.sequences;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class  GeneChromosomeMatcher {
        private boolean positiveStrandDirection;
        private String chr;
        private int startPoint;
        private int endPoint;
        private String genomeBuildNumber;
        private String name;

        public GeneChromosomeMatcher(boolean _strand, String _chr, int _startPoint,
                         int _endPoint, String _genomeBuildNumber) {
            positiveStrandDirection = _strand;
            chr = _chr;
            startPoint = _startPoint;
            endPoint = _endPoint;
            genomeBuildNumber = _genomeBuildNumber;
        }

    public String getChr() {
        return chr;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public String getGenomeBuildNumber() {
        return genomeBuildNumber;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public boolean isPositiveStrandDirection() {
        return positiveStrandDirection;
    }

    public String getName() {
        return name;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    public void setGenomeBuildNumber(String genomeBuildNumber) {
        this.genomeBuildNumber = genomeBuildNumber;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public void setPositiveStrandDirection(boolean positiveStrandDirection) {
        this.positiveStrandDirection = positiveStrandDirection;
    }

    public void setName(String name) {
        this.name = name;
    }


}
