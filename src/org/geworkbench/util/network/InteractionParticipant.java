package org.geworkbench.util.network;

import java.io.Serializable;

public class InteractionParticipant implements Serializable{
	 
	private static final long serialVersionUID = -8958561230301538758L;
	
	private String geneId;    
    private String geneName; 
    private String dbSource;
 
    public InteractionParticipant(String geneId, String geneName, String dbSource) {
        this.geneId = geneId; 
        this.geneName = geneName; 
        this.dbSource = dbSource;
       
    }

    
    public String getGeneId() {
        return geneId;
    }
  
   
    public String getGeneName() {
        return geneName;
    }
 
    public String getDbSource() {
        return dbSource;
    }
 
}
