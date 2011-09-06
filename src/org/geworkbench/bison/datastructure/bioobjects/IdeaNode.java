package org.geworkbench.bison.datastructure.bioobjects;

import java.io.Serializable;

public class IdeaNode implements Serializable, Comparable<IdeaNode>{

	/**
	 *data structure of IDEA Analysis for result display.  
  	 *@author zm2165 $Id$ 
	 */
	private static final long serialVersionUID = 1610758764072258387L;
	private String probe;
	private String geneSymbol;
	private String chrBand;
	private int	conn;
	private double nes;
	private int loc;
	private int loCHits;
	private double loCEs;
	private double loCNes;
	private int goc;
	private int goCHits;
	private double goCEs;
	private double goCNes;
	
	public IdeaNode(String probe, String geneSymbol, String chrBand, int	conn,
		double nes, int loc, int loCHits, double loCEs, double loCNes,
			int goc, int goCHits, double goCEs, double goCNes){
		this.probe=probe;
		this.geneSymbol=geneSymbol;
		this.chrBand=chrBand;
		this.conn=conn;
		this.nes=nes;
		this.loc=loc;
		this.loCHits=loCHits;
		this.loCEs=loCEs;
		this.loCNes=loCNes;
		this.goc=goc;
		this.goCHits=goCHits;
		this.goCEs=goCEs;
		this.goCNes=goCNes;		
	}
	

	public int compareTo(IdeaNode ideaNode2) {
		double d = nes - ideaNode2.getNes();
		if(d<0) return -1;
		else if (d>0) return 1;
		else return 0;
	}

	public double getNes() {
		return nes;
	}
	
	public void setConn(int conn) {
		this.conn = conn;
	}

	public int getConn() {
		return conn;
	}	

	public String getProbe() {
		return probe;
	}

	public String getGene() {
		return geneSymbol;
	}

	public String getChrBand() {
		return chrBand;
	}

	public int getLoc() {
		return loc;
	}

	public int getLoCHits() {
		return loCHits;
	}

	public double getLoCEs() {
		return loCEs;
	}

	public double getLoCNes() {
		return loCNes;
	}

	public int getGoc() {
		return goc;
	}

	public int getGoCHits() {
		return goCHits;
	}

	public double getGoCEs() {
		return goCEs;
	}

	public double getGoCNes() {
		return goCNes;
	}
	
	
}
