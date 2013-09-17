package org.geworkbench.bison.datastructure.bioobjects;

import java.io.Serializable;

public class IdeaModule implements Serializable{

	/**
	 *data structure of IDEA Analysis for result display.  
  	 *@author zm2165 $Id$ 
	 */
	private static final long serialVersionUID = 6652015492936201564L;
	private String probe1;
	private String probe2;
	private String connType;
	private String gLoc;
	
	public IdeaModule(String probe1, String probe2, String connType, String gLoc){
		this.probe1=probe1;
		this.probe2=probe2;
		this.connType=connType;
		this.gLoc=gLoc;
	}
	
	public String getGene1() {
		return probe1;
	}
	public String getGene2() {
		return probe2;
	}
	public String getConnType() {
		return connType;
	}
	public String getGLoc() {
		return gLoc;
	}
}
