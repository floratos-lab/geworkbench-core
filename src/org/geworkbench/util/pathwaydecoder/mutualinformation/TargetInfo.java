package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * @author oshteynb
 * @version $Id: TargetInfo.java,v 1.1 2009-04-29 19:59:39 oshteynb Exp $
 *
 */
public class TargetInfo  implements Serializable {
    /**
	 * 	serialVersionUID generated by Eclipse
	 */
	private static final long serialVersionUID = -2814826276781866410L;

	public TargetInfo(DSGeneMarker target, double correlation) {
		this.target = target;
		this.correlation = correlation;
	}

	private DSGeneMarker target;
	private double correlation;  // pearson correlation (TF, target)

	/**
     * Pearson correlation between the transcription factor and the target gene.
     * Used primarily for the heat map.
     * @return result of Pearson correlation
     */
    public double getCorrelation(){
    	return this.correlation;
    }

    /**
     * Pearson correlation between the transcription factor and the target gene.
     * Used primarily for the heat map.
     *
     * @param Pearson correlation
     */
    public void setCorrelation(double correlation){
    	this.correlation = correlation;
    }

}
