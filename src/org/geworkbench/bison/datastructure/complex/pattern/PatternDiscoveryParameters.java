package org.geworkbench.bison.datastructure.complex.pattern;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 24, 2007
 * Time: 5:35:31 PM
 * 
 * @version $Id$
 */
import java.io.Serializable;

public class PatternDiscoveryParameters implements Serializable {
	private static final long serialVersionUID = -1314270495289691759L;

	public PatternDiscoveryParameters() {
	}

	public PatternDiscoveryParameters (
			int minSupport,
			int minTokens,
			int window,
			int minWTokens,
			int exactTokens,
			int countSeq,
			int exact,
			int printDetails,
			int groupingType,
			int groupingN,
			int sortMode,
			int outputMode,
			double minPer100Support,
			int computePValue,
			double minPValue,
			int threadNo,
			int threadId,
			int minPatternNo,
			int maxPatternNo,
			int maxRunTime,
			String similarityMatrix,
			double similarityThreshold,
			String inputName,
			String outputName) {
		
		this.minSupport = minSupport;
		this.minTokens = minTokens;
		this.window = window;
		this.exactTokens = exactTokens;
		this.countSeq = countSeq;
		this.exact = exact;
		this.printDetails = printDetails;
		this.groupingType = groupingType;
		this.groupingN = groupingN;
		this.sortMode = sortMode;
		this.outputMode = outputMode;
		this.minPer100Support = minPer100Support;
		this.computePValue = computePValue;
		this.minPValue = minPValue;
		this.threadNo = threadNo;
		this.threadId = threadId;
		this.minPatternNo = minPatternNo;
		this.maxPatternNo = maxPatternNo;
		this.maxRunTime = maxRunTime;
		this.similarityMatrix = similarityMatrix;
		this.similarityThreshold = similarityThreshold;
		this.inputName = inputName;
		this.outputName = outputName;
	}
	
	public int getMinSupport() {
		return minSupport;
	}

	public int getMinTokens() {
		return minTokens;
	}

	public int getWindow() {
		return window;
	}

	public int getMinWTokens() {
		return minWTokens;
	}

	public int getExactTokens() {
		return exactTokens;
	}

	public int getCountSeq() {
		return countSeq;
	}


	public int getExact() {
		return exact;
	}

	public int getPrintDetails() {
		return printDetails;
	}

	public int getGroupingType() {
		return groupingType;
	}

	public int getGroupingN() {
		return groupingN;
	}

	public int getSortMode() {
		return sortMode;
	}

	public int getOutputMode() {
		return outputMode;
	}

	public double getMinPer100Support() {
		return minPer100Support;
	}

	public int getComputePValue() {
		return computePValue;
	}

	public double getMinPValue() {
		return minPValue;
	}

	public int getThreadNo() {
		return threadNo;
	}

	public int getThreadId() {
		return threadId;
	}

	public int getMinPatternNo() {
		return minPatternNo;
	}

	public int getMaxPatternNo() {
		return maxPatternNo;
	}

	public int getMaxRunTime() {
		return maxRunTime;
	}

	public String getSimilarityMatrix() {
		return similarityMatrix;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	public String getInputName() {
		return inputName;
	}

	public String getOutputName() {
		return outputName;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof PatternDiscoveryParameters))
			return false;
		
		PatternDiscoveryParameters parameters = (PatternDiscoveryParameters) obj;
		if (this == obj)
			return true;

		boolean flag = minSupport == parameters.getMinSupport()
				&& minTokens == parameters.getMinTokens()
				&& window == parameters.getWindow()
				&& minWTokens == parameters.getMinWTokens()
				&& exactTokens == parameters.getExactTokens()
				&& countSeq == parameters.getCountSeq()
				&& exact == parameters.getExact()
				&& printDetails == parameters.getPrintDetails()
				&& groupingType == parameters.getGroupingType()
				&& groupingN == parameters.getGroupingN()
				&& sortMode == parameters.getSortMode()
				&& outputMode == parameters.getOutputMode()
				&& minPer100Support == parameters.getMinPer100Support()
				&& computePValue == parameters.getComputePValue()
				&& minPValue == parameters.getMinPValue()
				&& threadNo == parameters.getThreadNo()
				&& threadId == parameters.getThreadId()
				&& minPatternNo == parameters.getMinPatternNo()
				&& maxPatternNo == parameters.getMaxPatternNo()
				&& maxRunTime == parameters.getMaxRunTime()
				&& (similarityMatrix == null
						&& parameters.getSimilarityMatrix() == null || similarityMatrix != null
						&& similarityMatrix.equals(parameters
								.getSimilarityMatrix()))
				&& similarityThreshold == parameters.getSimilarityThreshold()
				&& (inputName == null && parameters.getInputName() == null || inputName != null
						&& inputName.equals(parameters.getInputName()))
				&& (outputName == null && parameters.getOutputName() == null || outputName != null
						&& outputName.equals(parameters.getOutputName()));
		return flag;
	}

	public synchronized int hashCode() {

		int i = 1;
		i += getMinSupport();
		i += getMinTokens();
		i += getWindow();
		i += getMinWTokens();
		i += getExactTokens();
		i += getCountSeq();
		i += getExact();
		i += getPrintDetails();
		i += getGroupingType();
		i += getGroupingN();
		i += getSortMode();
		i += getOutputMode();
		i += (new Double(getMinPer100Support())).hashCode();
		i += getComputePValue();
		i += (new Double(getMinPValue())).hashCode();
		i += getThreadNo();
		i += getThreadId();
		i += getMinPatternNo();
		i += getMaxPatternNo();
		i += getMaxRunTime();
		if (getSimilarityMatrix() != null)
			i += getSimilarityMatrix().hashCode();
		i += (new Double(getSimilarityThreshold())).hashCode();
		if (getInputName() != null)
			i += getInputName().hashCode();
		if (getOutputName() != null)
			i += getOutputName().hashCode();

		return i;
	}

	private int minSupport;
	private int minTokens;
	private int window;
	private int minWTokens;
	private int exactTokens;
	private int countSeq;
	private int exact;
	private int printDetails;
	private int groupingType;
	private int groupingN;
	private int sortMode;
	private int outputMode;
	private double minPer100Support;
	private int computePValue;
	private double minPValue;
	private int threadNo;
	private int threadId;
	private int minPatternNo;
	private int maxPatternNo;
	private int maxRunTime;
	private String similarityMatrix;
	private double similarityThreshold;
	private String inputName;
	private String outputName;

}