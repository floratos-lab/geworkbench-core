package org.geworkbench.bison.datastructure.bioobjects;

import java.io.Serializable;

public class IdeaModule implements Serializable{

	/**
	 *data structure of IDEA Analysis for result display.  
  	 *@author zm2165 $Id$ 
	 */
	private static final long serialVersionUID = 6652015492936201564L;
	private String gene1;
	private String gene2;
	private String connType;
	private String gLoc;
	
	public IdeaModule(String gene1, String gene2, String connType, String gLoc){
		this.gene1=gene1;
		this.gene2=gene2;
		this.connType=connType;
		this.gLoc=gLoc;
	}
	
	public String getGene1() {
		return gene1;
	}
	public String getGene2() {
		return gene2;
	}
	public String getConnType() {
		return connType;
	}
	public String getGLoc() {
		return gLoc;
	}
}
