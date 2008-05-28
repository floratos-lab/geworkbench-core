package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import javax.swing.*;

/**
 * @author John Watkinson
 */
public class CSPositionSpecificAffinityMatrix implements DSPositionSpecificAffintyMatrix {

    private ImageIcon psamImage;
    private String psamId;
    private String experimentName;
    private String experimentId;
    private String seedSequence;
    private String consensusSequence;
    private double pValue;
    private double tValue;
    private double coeff;
    private double[][] scores;
    private boolean trailingStrand;

    public boolean isTrailingStrand() {
        return trailingStrand;
    }

    public void setTrailingStrand(boolean trailingStrand) {
        this.trailingStrand = trailingStrand;
    }

    public long getBonferroni() {
        return 0;
    }

    public void setBonferroni(long bonferroni) {
    }
    
    public double getTValue(){
    	return this.tValue;
    }
    
    public void setTValue(double t){
    	this.tValue = t;
    }
    
    public double getCoeff(){
    	return this.coeff;
    }
    
    public void setCoeff(double F){
    	this.coeff = F;
    }

    public ImageIcon getPsamImage() {
        return psamImage;
    }

    public String getExperiment() {
        return experimentName;
    }
    
    public String getExperimentID(){
    	return this.experimentId;
    }

    public String getSeedSequence() {
        return seedSequence;
    }

    public String getConsensusSequence() {
        return consensusSequence;
    }

    public void setPsamImage(ImageIcon image) {
        this.psamImage = image;
    }

    public void setExperiment(String experiment) {
        this.experimentName = experiment;
    }
    
    public void setExperimentID(String id){
    	this.experimentId = id;
    }

    public void setSeedSequence(String seedSequence) {
        this.seedSequence = seedSequence;
    }

    public void setConsensusSequence(String consensusSequence) {
        this.consensusSequence = consensusSequence;
    }

    public double[][] getScores() {
        return scores;
    }

    public void setScores(double[][] scores) {
        this.scores = scores;
    }

    public void addNameValuePair(String name, Object value) {
    }

    public Object[] getValuesForName(String name) {
        return new Object[0];
    }

    public void forceUniqueValue(String name) {
    }

    public void allowMultipleValues(String name) {
    }

    public boolean isUniqueValue(String name) {
        return false;
    }

    public void clearName(String name) {
    }

    public void addDescription(String description) {
    }

    public String[] getDescriptions() {
        return new String[0];
    }

    public void removeDescription(String description) {
    }

    public String getID() {
        return psamId;
    }

    public void setID(String id) {
    	psamId = id;
    }

    public int getSerial() {
        return 0;
    }

    public void setSerial(int serial) {
    }

    public String getLabel() {
        return consensusSequence;
    }

    public void setLabel(String label) {
        consensusSequence = label;
    }

    public boolean enabled() {
        return false;
    }

    public void enable(boolean status) {
    }

    public double getPValue() {
        return pValue;
    }

    public void setPValue(double value) {
        pValue= value;
    }
}
