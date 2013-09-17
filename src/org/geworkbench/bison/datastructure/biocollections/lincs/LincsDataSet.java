package org.geworkbench.bison.datastructure.biocollections.lincs;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet; 
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * 
 * @author min you
 * @version $Id: LincsDataSet.java
 */
public class LincsDataSet 	extends CSAncillaryDataSet<DSMicroarray> {
	 
	private static final long serialVersionUID = 2691812481782483733L;

	private String tissue = null;
	private String cellLine = null;
	private String drug1 = null;
	private String drug2 = null;
	private String assayType = null;
	private String measurement = null;
	private double[][] pValues = null;
	private long[][] levelTwoIds = null;
	private String[] freeVariableNames;
	
	private boolean isExperimental = true; 
	
	public LincsDataSet(DSMicroarraySet parent, String label) {
		super(parent, label);

	}
	
	public void setTissue(String tissue)
	{
		this.tissue = tissue;
	}
	
	public String getTissue()
	{
		return this.tissue;
	}
	public void setCellLine(String cellLine)
	{
		this.cellLine = cellLine;
	}
	
	public String getCellLine()
	{
		return this.cellLine;
	}
	
	public void setDrug1(String drug1)
	{
		this.drug1 = drug1;
	}
	
	public String getDrug1()
	{
		return this.drug1;
	}
	
	public void setDrug2(String drug2)
	{
		this.drug2 = drug2;
	}
	
	public String getDrug2()
	{
		return this.drug2;
	}
	public void setAssayType(String assayType)
	{
		this.assayType = assayType;
	}
	
	public String getAssayType()
	{
		return this.assayType;
	}
	
	public void setMeasurement(String measurement)
	{
		this.measurement = measurement;
	}
	
	public String getMeasurement()
	{
		return this.measurement;
	}
	
	public void setPvalues(double[][] pValues)
	{
		this.pValues = pValues;
	}
	public double[][] getPValues()
	{
		return this.pValues;
	}
	
	public void setLevelTwoIds(long[][] levelTwoIds)
	{
		this.levelTwoIds = levelTwoIds;
	}
	public long[][] getLevelTwoIds()
	{
		return this.levelTwoIds;
	}
	
    public boolean isExperimental()
    {
       return this.isExperimental;
    }
    public void isExperimental(boolean isExperimental)
    {
       this.isExperimental = isExperimental;
    }    
    
    public void setFreeVariableNames(String[] freeVariableNames)
	{
		this.freeVariableNames = freeVariableNames;
	}
	public String[] getfreeVariableNames()
	{
		return this.freeVariableNames;
	}
}
