package org.geworkbench.util.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellularNetworkPreference implements java.io.Serializable {
	private static final long serialVersionUID = -3049737574513104023L;
	private String title = null;
	private String context = null;
	private String version = null;
	private List<String> displaySelectedInteractionTypes = new ArrayList<String>();

	private Short selectedConfidenceType = 0;
	private List<Short> confidenceTypeList = new ArrayList<Short>();
	private Map<Short, Double> maxConfidenceValueMap = new HashMap<Short, Double>();
	private double smallestIncrement = 0.01; 
	
	public CellularNetworkPreference(String title)
	{
		this.title = title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setContext(String context)
	{
		this.context = context;
	}
	
	public String getContext()
	{
		return this.context;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public String getVersion()
	{
		return this.version;
	}
	
	public List<String> getDisplaySelectedInteractionTypes() {
		return this.displaySelectedInteractionTypes;
	}
	
	public  void getDisplaySelectedInteractionTypes(List<String> displaySelectedInteractionTypes) {
		 this.displaySelectedInteractionTypes = displaySelectedInteractionTypes;
	}

	public double getSmallestIncrement() {		 
		return smallestIncrement;
	}
	 
	public void setSmallestIncrement(double smallestIncrementNumber) {
		smallestIncrement = smallestIncrementNumber;
	}	
	 
	
	public Double getMaxConfidenceValue(Short confidenceType) {		 
			return maxConfidenceValueMap.get(confidenceType);
	}
	
	public Map<Short, Double> getMaxConfidenceValueMap() {		 
		return maxConfidenceValueMap ;
    }
	
	public void setMaxConfidenceValue(Map<Short, Double> maxConfidenceValueMap) { 
		 
		 this.maxConfidenceValueMap = maxConfidenceValueMap;
		 	
	}	
	
	public List<Short> getConfidenceTypeList()
	{
		return confidenceTypeList;
	}
	
	public void setConfidenceTypeList(List<Short> list)
	{
		confidenceTypeList = list;
	}
	
	public Short getSelectedConfidenceType()
	{
		return selectedConfidenceType;
	}	

    public void setSelectedConfidenceType(Short type )
	{
    	selectedConfidenceType = type;
	}    
	
	public void clearConfidenceTypes()
	{
		selectedConfidenceType = 0;
		if (confidenceTypeList != null)
			confidenceTypeList.clear();
		maxConfidenceValueMap.clear();
	}
	
}
