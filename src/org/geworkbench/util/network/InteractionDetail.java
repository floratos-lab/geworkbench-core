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
	private static final String ENTREZ_GENE = "Entrez Gene";
	private String dSGeneMarker1;    //One node name
    private String dSGeneMarker2;
    private String dSGeneName1;    //One node name
    private String dSGeneName2;
    private String dbSource1;
    private String dbSource2;   
    private double confidence;
    private String interactionType;
    private String interactionId;
    private Short  evidenceId;
   
    public InteractionDetail(String dSGeneMarker1, String dSGeneMarker2, String dSGeneName1, String dSGeneName2, String dbSource1, String dbSource2, double confidence, String interactionType, String interactionId, Short evidenceId) {
        this.dSGeneMarker1 = dSGeneMarker1;
        this.dSGeneMarker2 = dSGeneMarker2;
        this.dSGeneName1 = dSGeneName1;
        this.dSGeneName2 = dSGeneName2;
        this.dbSource1 = dbSource1;
        this.dbSource2 = dbSource2;
        this.confidence = confidence;
        this.interactionType = interactionType;
        this.evidenceId = evidenceId;
    }
         
    public InteractionDetail(String dSGeneMarker1, String dSGeneMarker2, String dSGeneName1, String dSGeneName2, String dbSource1, String dbSource2, double confidence, String interactionType, String interactionId) {
        this.dSGeneMarker1 = dSGeneMarker1;
        this.dSGeneMarker2 = dSGeneMarker2;
        this.dSGeneName1 = dSGeneName1;
        this.dSGeneName2 = dSGeneName2;
        this.dbSource1 = dbSource1;
        this.dbSource2 = dbSource2;
        this.confidence = confidence;
        this.interactionType = interactionType;
         
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

    public Integer getInteractionGeneId(Integer geneId)
    {
    	Integer interactionGeneId = null;
        if (dSGeneMarker1 != null && isGene1EntrezId() && !dSGeneMarker1.equals(geneId.toString()) )    	
        	interactionGeneId = new Integer(dSGeneMarker1);
        else if (dSGeneMarker2 != null && isGene2EntrezId() && !dSGeneMarker2.equals(geneId.toString()) )    	
        	interactionGeneId = new Integer(dSGeneMarker2);
       
        return interactionGeneId;
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
    
    public String getDbSource1() {
        return this.dbSource1;
    }

    public void setDbSource1(String dbSource1) {
        this.dbSource1 = dbSource1;
    }

    public String getDbSource2() {
        return this.dbSource2;
    }

    public void setDbSource2(String dbSource2) {
        this.dbSource2 = dbSource2;
    }
    
    
    public boolean isGene1EntrezId() {
        return this.dbSource1.equalsIgnoreCase(ENTREZ_GENE);
    }

    public boolean isGene2EntrezId() {
        return this.dbSource2.equalsIgnoreCase(ENTREZ_GENE);
    }
    
    
    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getInteractionType() {
        return this.interactionType;
    }

    public void setInteraactionType(String interactionType) {
    	this.interactionType = interactionType;
    }
    
    public String getInteractionId() {
        return this.interactionId;
    }

    public Short getEvidenceId() {
        return this.evidenceId;
    }

    
    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
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
