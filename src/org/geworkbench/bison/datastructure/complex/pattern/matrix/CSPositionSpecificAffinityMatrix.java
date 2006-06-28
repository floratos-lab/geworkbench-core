package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import javax.swing.*;

/**
 * @author John Watkinson
 */
public class CSPositionSpecificAffinityMatrix implements DSPositionSpecificAffintyMatrix {

    private ImageIcon psamImage;
    private String experiment;
    private String seedSequence;
    private String consensusSequence;
    private double pValue;

    public ImageIcon getPsamImage() {
        return psamImage;
    }

    public String getExperiment() {
        return experiment;
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
        this.experiment = experiment;
    }

    public void setSeedSequence(String seedSequence) {
        this.seedSequence = seedSequence;
    }

    public void setConsensusSequence(String consensusSequence) {
        this.consensusSequence = consensusSequence;
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
        return null;
    }

    public void setID(String id) {
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
