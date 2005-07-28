package org.geworkbench.util.sequences;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class SequenceAnnotationTrack {

    String annotationName;
    private int featureNum;
    private int color_num;
    private int[] featureHitStart;
    private int[] featureHitEnd;
    private int[] sequenceHitStart;
    private int[] sequenceHitEnd;
    private int[] featureLength;
    private int[] featureEnd;
    private boolean[] featureDirection;
    private boolean[] featureActive;
    private double[] featureEValue;
    private String[] featureName;
    private String[] featureTag; /* or short name for displaying purpose */
    private String[] featureURL;

    /**
     * Constructors
     */

    /**
     * Use of this constructor will require separate use of initial function
     */
    public SequenceAnnotationTrack() {
    }

    public SequenceAnnotationTrack(int n) {
        this.initSequenceAnnotationTrack(n);
    }

    public void initSequenceAnnotationTrack(int n) {
        featureNum = n;
        featureHitStart = new int[n];
        featureHitEnd = new int[n];
        sequenceHitStart = new int[n];
        sequenceHitEnd = new int[n];
        featureLength = new int[n];
        featureEnd = new int[n];
        featureDirection = new boolean[n];
        featureActive = new boolean[n];
        featureEValue = new double[n];
        featureName = new String[n];
        featureTag = new String[n];
        featureURL = new String[n];
    }

    /**
     * Accessors
     */
    public String getAnnotationName() {
        return annotationName;
    }

    public int getFeatureNum() {
        return featureNum;
    }

    public int getColorNum() {
        return color_num;
    }

    public int[] getFeatureHitStart() {
        return featureHitStart;
    }

    public int getFeatureHitStart(int n) {
        return featureHitStart[n];
    }

    public int[] getFeatureHitEnd() {
        return featureHitEnd;
    }

    public int getFeatureHitEnd(int n) {
        return featureHitEnd[n];
    }

    public int[] getSequenceHitStart() {
        return sequenceHitStart;
    }

    public int getSequenceHitStart(int n) {
        return sequenceHitStart[n];
    }

    public int[] getSequenceHitEnd() {
        return sequenceHitEnd;
    }

    public int getSequenceHitEnd(int n) {
        return sequenceHitEnd[n];
    }

    public int[] getFeatureLength() {
        return featureLength;
    }

    public int getFeatureLength(int n) {
        return featureLength[n];
    }

    public boolean[] getFeatureDirection() {
        return featureDirection;
    }

    public boolean getFeatureDirection(int n) {
        return featureDirection[n];
    }

    public boolean[] getFeatureActive() {
        return featureActive;
    }

    public boolean getFeatureActive(int n) {
        return featureActive[n];
    }

    public double[] getFeatureEValue() {
        return featureEValue;
    }

    public double getFeatureEValue(int n) {
        return featureEValue[n];
    }

    public String[] getFeatureName() {
        return featureName;
    }

    public String getFeatureName(int n) {
        return featureName[n];
    }

    public String[] getFeatureTag() {
        return featureTag;
    }

    public String getFeatureTag(int n) {
        return featureTag[n];
    }

    public String[] getFeatureURL() {
        return featureURL;
    }

    public String getFeatureURL(int n) {
        return featureURL[n];
    }


    /**
     * Modificators
     */

    public void setAnnotationName(String name) {
        annotationName = name;
    }

    public void setColorNum(int cn) {
        color_num = cn;
    }

    public void setFeatureNum(int fn) {
        featureNum = fn;
    }

    public void setFeatureHitStart(int[] fs) {
        featureHitStart = fs;
    }

    public void setFeatureHitStart(int n, int fs) {
        featureHitStart[n] = fs;
    }

    public void setFeatureHitEnd(int[] fhe) {
        featureHitEnd = fhe;
    }

    public void setFeatureHitEnd(int n, int fhe) {
        featureHitEnd[n] = fhe;
    }

    public void setSequenceHitStart(int[] shs) {
        sequenceHitStart = shs;
    }

    public void setSequenceHitStart(int n, int shs) {
        sequenceHitStart[n] = shs;
    }

    public void setSequenceHitEnd(int[] she) {
        sequenceHitEnd = she;
    }

    public void setSequenceHitEnd(int n, int she) {
        sequenceHitEnd[n] = she;
    }

    public void setFeatureLength(int[] fl) {
        featureLength = fl;
    }

    public void setFeatureLength(int n, int fl) {
        featureLength[n] = fl;
    }

    public void setFeatureEnd(int[] fe) {
        featureEnd = fe;
    }

    public void setFeatureEnd(int n, int fe) {
        featureEnd[n] = fe;
    }

    public void setFeatureDirection(boolean[] fd) {
        featureDirection = fd;
    }

    public void setFeatureDirection(int n, boolean fd) {
        featureDirection[n] = fd;
    }

    public void setFeatureActive(boolean[] act) {
        featureActive = act;
    }

    public void setFeatureActive(int n, boolean act) {
        featureActive[n] = act;
    }

    public void setFeatureEValue(double[] fev) {
        featureEValue = fev;
    }

    public void setFeatureEValue(int n, double fev) {
        featureEValue[n] = fev;
    }

    public void setFeatureName(String[] fn) {
        featureName = fn;
    }

    public void setFeatureName(int n, String fn) {
        featureName[n] = fn;
    }

    public void setFeatureTag(String[] ft) {
        featureTag = ft;
    }

    public void setFeatureTag(int n, String ft) {
        featureTag[n] = ft;
    }

    public void setFeatureURL(String[] fu) {
        featureURL = fu;
    }

    public void setFeatureURL(int n, String fu) {
        featureURL[n] = fu;
    }
}

