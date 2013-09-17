package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.DSPValued;

public interface DSMatrixReduceExperiment extends DSBioObject, DSPValued {

	public double getTValue();

	public void setTValue(double tvalue);

	public double getCoeff();

	public void setCoeff(double coeff);
	
	public String getPsamId();
	
	public void setPsamId(String id);
}
