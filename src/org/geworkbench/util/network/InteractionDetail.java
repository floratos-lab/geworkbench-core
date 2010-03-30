package org.geworkbench.util.network;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 5, 2007
 * Time: 4:27:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * The deatil of one interaction.
 */
public class InteractionDetail implements Serializable{
    
	private static final long serialVersionUID = -4163326138016520667L;
	private String dSGeneMarker1;    //One node name
    private String dSGeneMarker2;
    private String dSGeneName1;    //One node name
    private String dSGeneName2;
    boolean isGene1EntrezId = true;
    boolean isGene2EntrezId = true;
    private double confidence;
    private String InteraactionType;
   
    public InteractionDetail(String dSGeneMarker1, String dSGeneMarker2, String dSGeneName1, String dSGeneName2, boolean isGene1EntrezId, boolean isGene2EntrezId, double confidence, String interaactionType) {
        this.dSGeneMarker1 = dSGeneMarker1;
        this.dSGeneMarker2 = dSGeneMarker2;
        this.dSGeneName1 = dSGeneName1;
        this.dSGeneName2 = dSGeneName2;
        this.isGene1EntrezId = isGene1EntrezId;
        this.isGene2EntrezId = isGene2EntrezId;
        this.confidence = confidence;
        InteraactionType = interaactionType;
    }

    
    public String getdSGeneMarker1() {
        return dSGeneMarker1;
    }

    public void setdSGeneMarker1(String dSGeneMarker1) {
        this.dSGeneMarker1 = dSGeneMarker1;
    }

    public String getdSGeneMarker2() {
        return dSGeneMarker2;
    }

    public void setdSGeneMarker2(String dSGeneMarker2) {
        this.dSGeneMarker2 = dSGeneMarker2;
    }
    
    public String getdSGeneName1() {
        return dSGeneName1;
    }

    public void setdSGeneName1(String dSGeneName1) {
        this.dSGeneName1 = dSGeneName1;
    }

    public String getdSGeneName2() {
        return dSGeneName2;
    }

    public void setdSGeneName2(String dSGeneName2) {
        this.dSGeneName2 = dSGeneName2;
    }
    
    public boolean isGene1EntrezId() {
        return isGene1EntrezId;
    }

    public boolean isGene2EntrezId() {
        return isGene2EntrezId;
    }
    
    
    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getInteractionType() {
        return InteraactionType;
    }

    public void setInteraactionType(String interaactionType) {
        InteraactionType = interaactionType;
    }

    public boolean equals(Object obj) {
        if (obj instanceof InteractionDetail) {
            InteractionDetail mInfo = (InteractionDetail) obj;
            return (dSGeneMarker1.toString().equals(mInfo.dSGeneMarker1.toString()) && dSGeneMarker2.toString().equals(mInfo.dSGeneMarker2.toString())) || (dSGeneMarker1.toString().equals(mInfo.dSGeneMarker2.toString()) && dSGeneMarker2.toString().equals(mInfo.dSGeneMarker1.toString()));
        }
        return false;
    }

    public String toString() {
        return dSGeneMarker1 + "-" + dSGeneMarker2 + ":" + confidence;

    }
}
