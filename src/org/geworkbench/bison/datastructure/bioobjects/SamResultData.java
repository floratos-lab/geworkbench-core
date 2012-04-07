package org.geworkbench.bison.datastructure.bioobjects;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * 
 * @author zm2165 
 * @version $Id$
 */

public class SamResultData extends CSAncillaryDataSet<DSMicroarray>{
	
	private static final long serialVersionUID = -4551408651966188747L;
	
	private float deltaInc;
	private float deltaMax;
	
	private float[] dd;
	private float[] dbar;
	private float[] pvalue;
	private float[] fold;
	private float[] fdr;	
	private DSMicroarraySet maSet;
	private DSMicroarraySetView<DSGeneMarker, DSMicroarray> data;

/*
	public SAMResult(final DSMicroarraySet maSet, String string){
		super(maSet, string);
	}
*/	
	public SamResultData(final DSMicroarraySet maSet, String string, DSMicroarraySetView<DSGeneMarker, DSMicroarray> data,
			float deltaInc, float deltaMax,	float[] dd, float[] dbar,float[] pvalue,
			float[] fold, float[] fdr){
		super(maSet, string);
		this.maSet=maSet;
		this.data=data;
		this.deltaInc=deltaInc;
		this.deltaMax=deltaMax;
		this.dd=dd;
		this.dbar=dbar;
		this.pvalue=pvalue;
		this.fold=fold;
		this.fdr=fdr;		
	}

	public DSMicroarraySet getMaSet(){
		return maSet;
	}	
	
	public DSMicroarraySetView<DSGeneMarker, DSMicroarray> getData(){
		return data;
	}	
	
	public float getDeltaInc() {
		return deltaInc;
	}
	
	public float getDeltaMax() {
		return deltaMax;
	}
	
	public float[] getD() {
		return dd;
	}

	public float[] getDbar() {
		return dbar;
	}

	public float[] getPvalue() {
		return pvalue;
	}

	public float[] getFold() {
		return fold;
	}

	public float[] getFdr() {
		return fdr;
	}

}
