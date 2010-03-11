package org.geworkbench.bison.datastructure.biocollections.medusa;

import java.io.Serializable;

/**
 * A command object to pass around with medusa parameters. This command object
 * is oblivious to any client, and can be reused in any context (rich client,
 * web, etc.).
 * 
 * @author keshav
 * @version $Id: MedusaCommand.java,v 1.3 2007-06-20 16:59:35 keshav Exp $
 */
public class MedusaCommand implements Serializable {
	private static final long serialVersionUID = 1L;

	private String featuresFile = null;

	private int minKer = 0;

	private int maxKer = 0;

	private double base = 0;

	private double bound = 0;

	private int minGap = 0;

	private int maxGap = 0;

	private boolean usingDimers = true;

	private int iter = 0;

	private int pssmLength = 0;

	private int agg = 0;

	private boolean reverseComplement = true;

	/**
	 * 
	 * @return
	 */
	public int getAgg() {
		return this.agg;
	}

	/**
	 * 
	 * @param agg
	 */
	public void setAgg(int agg) {
		this.agg = agg;
	}

	/**
	 * 
	 * @return
	 */
	public int getPssmLength() {
		return pssmLength;
	}

	/**
	 * 
	 * @param pssmLength
	 */
	public void setPssmLength(int pssmLength) {
		this.pssmLength = pssmLength;
	}

	/**
	 * 
	 * @return
	 */
	public int getIter() {
		return iter;
	}

	/**
	 * 
	 * @param iter
	 */
	public void setIter(int iter) {
		this.iter = iter;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxGap() {
		return maxGap;
	}

	/**
	 * 
	 * @param maxGap
	 */
	public void setMaxGap(int maxGap) {
		this.maxGap = maxGap;
	}

	/**
	 * 
	 * @return
	 */
	public int getMinGap() {
		return minGap;
	}

	/**
	 * 
	 * @param minGap
	 */
	public void setMinGap(int minGap) {
		this.minGap = minGap;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUsingDimers() {
		return this.usingDimers;
	}

	/**
	 * 
	 * @param usingDimers
	 */
	public void setUsingDimers(boolean usingDimers) {
		this.usingDimers = usingDimers;
	}

	/**
	 * 
	 * @return
	 */
	public double getBase() {
		return base;
	}

	/**
	 * 
	 * @param base
	 */
	public void setBase(double base) {
		this.base = base;
	}

	/**
	 * 
	 * @return
	 */
	public double getBound() {
		return bound;
	}

	/**
	 * 
	 * @param bound
	 */
	public void setBound(double bound) {
		this.bound = bound;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxKer() {
		return maxKer;
	}

	/**
	 * 
	 * @param maxKer
	 */
	public void setMaxKer(int maxKer) {
		this.maxKer = maxKer;
	}

	/**
	 * 
	 * @return
	 */
	public int getMinKer() {
		return minKer;
	}

	/**
	 * 
	 * @param minKer
	 */
	public void setMinKer(int minKer) {
		this.minKer = minKer;
	}

	/**
	 * 
	 * @param featuresFile
	 */
	public void setFeaturesFile(String featuresFile) {
		this.featuresFile = featuresFile;
	}

	/**
	 * 
	 * @return
	 */
	public String getFeaturesFile() {
		return featuresFile;
	}

	public boolean isReverseComplement() {
		return reverseComplement;
	}

	public void setReverseComplement(boolean reverseComplement) {
		this.reverseComplement = reverseComplement;
	}

}
