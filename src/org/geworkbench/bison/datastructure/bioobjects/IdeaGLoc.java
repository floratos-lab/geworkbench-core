package org.geworkbench.bison.datastructure.bioobjects;

import java.io.Serializable;

public class IdeaGLoc implements Serializable, Comparable<IdeaGLoc> {

	/**
	*data structure of IDEA Analysis for result display.  
  	*@author zm2165 $Id$ 
	*/
	private static final long serialVersionUID = 7415476418084003832L;
	private String probe1;
	private String gene1;
	private String probe2;
	private String gene2;
	private double mi;
	private double deltaMi;
	private double zScore;
	
	public IdeaGLoc(String probe1,String gene1,String probe2,String gene2,
			double mi, double deltaMi,double zScore){
		this.probe1=probe1;
		this.gene1=gene1;
		this.probe2=probe2;
		this.gene2=gene2;
		this.mi=mi;
		this.deltaMi=deltaMi;
		this.zScore=zScore;
	}
	
	
	@Override
	public int compareTo(IdeaGLoc ideaGLoc2) {
		double d = zScore - ideaGLoc2.getzScore();
		if(d<0) return -1;
		else if (d>0) return 1;
		else return 0;
	}
	public void setzScore(double zScore) {
		this.zScore = zScore;
	}
	public double getzScore() {
		return zScore;
	}
	public void setProbe1(String probe1) {
		this.probe1 = probe1;
	}
	public String getProbe1() {
		return probe1;
	}
	public void setGene1(String gene1) {
		this.gene1 = gene1;
	}
	public String getGene1() {
		return gene1;
	}
	public void setProbe2(String probe2) {
		this.probe2 = probe2;
	}
	public String getProbe2() {
		return probe2;
	}
	public void setGene2(String gene2) {
		this.gene2 = gene2;
	}
	public String getGene2() {
		return gene2;
	}
	public void setMi(double mi) {
		this.mi = mi;
	}
	public double getMi() {
		return mi;
	}
	public void setDeltaMi(double deltaMi) {
		this.deltaMi = deltaMi;
	}
	public double getDeltaMi() {
		return deltaMi;
	}

}
