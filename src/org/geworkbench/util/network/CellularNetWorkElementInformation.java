package org.geworkbench.util.network;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.List; 
import java.util.Set; 
import java.util.TreeMap;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.GeneOntologyUtil;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GOTerm;
import org.geworkbench.bison.datastructure.bioobjects.markers.goterms.GeneOntologyTree;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 5, 2007
 * Time: 12:31:48 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * It is used to save all celllualr Network information related to a specific
 * marker.
 * 
 * @version $Id$
 */
public class CellularNetWorkElementInformation implements java.io.Serializable {

	private static final long serialVersionUID = -4163326138016520667L;
  
	
	private HashMap<String, Integer> interactionNumMap = new HashMap<String, Integer>();

	private static Set<String> allInteractionTypes = new HashSet<String>();

	final private DSGeneMarker dSGeneMarker;
	private String goInfoStr;
	private String geneType;
	private InteractionDetail[] interactionDetails;
	private double threshold;
	private boolean isDirty;
	
	final private static int binNumber = 101; 
 
	public CellularNetWorkElementInformation(DSGeneMarker dSGeneMarker) {
		this.dSGeneMarker = dSGeneMarker;	 
		isDirty = true;
		if(GeneOntologyTree.getInstance()==null) {
			geneType = "pending";
			goInfoStr = "pending";
		} else {
			setGoInfoStr();
			geneType = GeneOntologyUtil.checkMarkerFunctions(dSGeneMarker);
		}

		reset();		 

	}

	private void setGoInfoStr() {
		Set<GOTerm> set = getAllGOTerms(dSGeneMarker);

		goInfoStr = ""; 
		if (set != null && set.size() > 0) {
			for (GOTerm goTerm : set) {
				goInfoStr += goTerm.getName() + "; ";
			}
		}
	}

	private static Set<GOTerm> getAllGOTerms(DSGeneMarker dsGeneMarker) {
		GeneOntologyTree tree = GeneOntologyTree.getInstanceUntilAvailable();
		String geneId = dsGeneMarker.getLabel();
		String[] goTerms = AnnotationParser.getInfo(geneId,
				AnnotationParser.GOTERM);
		if (goTerms != null) {
			Set<GOTerm> set = new HashSet<GOTerm>();
			for (String goTerm : goTerms) {
				String goIdStr = goTerm.split("/")[0].trim();
				if (!goIdStr.equalsIgnoreCase("---")) {
					int goId = new Integer(goIdStr);
					if (tree.getTerm(goId) != null)
						set.add(tree.getTerm(goId));
				}
			}
			return set;
		}

		return null;
	}

	public TreeMap<String, Set<GOTerm>> getAllAncestorGoTerms(String catagory) {
		GeneOntologyTree tree = GeneOntologyTree.getInstanceUntilAvailable();
		String geneId = dSGeneMarker.getLabel();
		String[] goTerms = AnnotationParser.getInfo(geneId, catagory);

		TreeMap<String, Set<GOTerm>> treeMap = new TreeMap<String, Set<GOTerm>>();
		if (goTerms != null) {

			for (String goTerm : goTerms) {
				String goIdStr = goTerm.split("/")[0].trim();
				try {
					if (!goIdStr.equalsIgnoreCase("---")) {
						Integer goId = new Integer(goIdStr);
						if (goId != null) {
							treeMap.put(goTerm, tree.getAncestors(goId));
						}
					}
				} catch (NumberFormatException ne) {
					ne.printStackTrace();
				}

			}
		}
		return treeMap;
	}

	/**
	 * Remove all previous retrieved information.
	 */
	public void reset() {
		if (isDirty) {
			for (String interactionType : allInteractionTypes) {
				interactionNumMap.put(interactionType, -1);
			}
		} else {
			for (String interactionType : allInteractionTypes) {
				interactionNumMap.put(interactionType, 0);
			}
		}
	}	 

	public ArrayList<InteractionDetail> getSelectedInteractions(
			List<String> interactionIncludedList, short selectedConfidenceType) {
		ArrayList<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();
		if (interactionDetails != null && interactionDetails.length > 0) {
			for (int i = 0; i < interactionDetails.length; i++) {
				InteractionDetail interactionDetail = interactionDetails[i];
				if (interactionDetail != null
						&& interactionDetail.getConfidenceValue(selectedConfidenceType) >= threshold) {
					if (interactionIncludedList.contains(interactionDetail
							.getInteractionType())) {
						arrayList.add(interactionDetail);
					}

				}
			}
		}
		return arrayList;
	}

	public ArrayList<InteractionDetail> getSelectedInteractions(
			String interactionType, short selectedConfidenceType) {
		ArrayList<InteractionDetail> arrayList = new ArrayList<InteractionDetail>();
		if (interactionDetails != null && interactionDetails.length > 0) {
			for (int i = 0; i < interactionDetails.length; i++) {
				InteractionDetail interactionDetail = interactionDetails[i];
				if (interactionDetail != null
						&& interactionDetail.getConfidenceValue(selectedConfidenceType) >= threshold) {
					if (interactionType.equals(interactionDetail
							.getInteractionType())) {
						arrayList.add(interactionDetail);
					}

				}
			}
		}
		return arrayList;
	}

	
	
	public int[] getDistribution(List<String> displaySelectedInteractionTypes, short selectedConfidenceType, double smallestIncrement) {
		 
		int[] distribution = new int[binNumber];
		for (int i = 0; i < binNumber; i++)
			distribution[i] = 0;
		if (interactionDetails == null || interactionDetails.length <= 0)
			return distribution;
		for (InteractionDetail interactionDetail : interactionDetails) {
			if (interactionDetail != null
					&& displaySelectedInteractionTypes
							.contains(interactionDetail.getInteractionType())) {
				int confidence = (int) (interactionDetail.getConfidenceValue(selectedConfidenceType) / smallestIncrement);
				if (confidence >= 0) {
					if (confidence >= distribution.length)
						confidence = distribution.length-1;
				 
					for (int i = 0; i <= confidence; i++)
						distribution[i]++;

				}

			}
		}
	 
		return distribution;
	}

	public static int getBinNumber() {	 
		return binNumber;
	}
	
 
	public boolean isDirty() {
		return isDirty;
	}

	public static void setAllInteractionTypes(
			List<String> allInteractionTypeList) {
		allInteractionTypes.clear();
		allInteractionTypes.addAll(allInteractionTypeList);

	}

	public void setDirty(boolean dirty) {
		isDirty = dirty;		 
	}

	public Integer getInteractionNum(String interactionType) {
		if (interactionNumMap.containsKey(interactionType))
			return interactionNumMap.get(interactionType);
		else
			return null;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold, short selectedConfidenceType) {
		 
			this.threshold = threshold; 
		    update(selectedConfidenceType);
	}

	/**
	 * Associate the gene marker with the details.
	 * 
	 * @param arrayList
	 */
	public void setInteractionDetails(List<InteractionDetail> arrayList, CellularNetworkPreference pref) {
		 
		if (arrayList != null && arrayList.size() > 0) {
			interactionDetails = new InteractionDetail[arrayList.size()];
			
			for(int i=0; i<arrayList.size(); i++)
			{
				interactionDetails[i] = arrayList.get(i);
				List<Short> typeIdList = interactionDetails[i].getConfidenceTypes();
				for (int j=0; j<typeIdList.size(); j++)
				{  
					Short typeId = typeIdList.get(j);
					Double maxConfidenceValue = pref.getMaxConfidenceValue(typeId);
					double confidenceValue = interactionDetails[i].getConfidenceValue(typeId);
					if (maxConfidenceValue == null )
						pref.getMaxConfidenceValueMap().put(typeId, new Double(confidenceValue));
					else
					{
						if (maxConfidenceValue < confidenceValue)
						{	 
							pref.getMaxConfidenceValueMap().put(typeId, new Double(confidenceValue));
						}
					}
					if (!pref.getConfidenceTypeList().contains(typeId))
						pref.getConfidenceTypeList().add(typeId);
				}
				 
			}
		} else {
			interactionDetails = null;
			reset();
		}

		if (interactionDetails != null) {
			if (pref.getSelectedConfidenceType() == null || pref.getSelectedConfidenceType().shortValue() == 0)
			   pref.setSelectedConfidenceType(pref.getConfidenceTypeList().get(0)); //use first one as default value.
			update(pref.getSelectedConfidenceType());
		}

	}

	/**
	 * Update the number of interaction based on the new threshold or new
	 * InteractionDetails.
	 */
	private void update(short selectedConfidenceType) {		 
			
		reset();
		
		if (interactionDetails == null || interactionDetails.length == 0) {
			return;
		}

		for (InteractionDetail interactionDetail : interactionDetails) {
			if (interactionDetail != null) {
				double confidence = interactionDetail.getConfidenceValue(selectedConfidenceType);
				String interactionType = interactionDetail.getInteractionType();
				if (confidence >= threshold) {
					if (this.interactionNumMap.containsKey(interactionType)) {
						int num = interactionNumMap.get(interactionType) + 1;
						interactionNumMap.put(interactionType, num);

					} else {
						interactionNumMap.put(interactionType, 1);

					}

				}
			}
		}

	}

	public int[] getInteractionDistribution(String interactionType, short selectedConfidenceType, double smallestIncrement) {	
	 
		int[] interactionDistribution = new int[binNumber];
		for (int i = 0; i < binNumber; i++)
			interactionDistribution[i] = 0;
		if (interactionDetails == null || interactionDetails.length <= 0)
			return interactionDistribution;

		for (InteractionDetail interactionDetail : interactionDetails) {
			int confidence = (int) (interactionDetail.getConfidenceValue(selectedConfidenceType) / smallestIncrement);
			if (confidence >= 0) {

				if (interactionDetail.getInteractionType().equals(
						interactionType)) {
					if (confidence >= interactionDistribution.length)
						confidence = interactionDistribution.length-1;				 
					for (int i = 0; i <= confidence; i++)
						interactionDistribution[i]++;

				}
			}

		}
	 
		return interactionDistribution;
	}

	public DSGeneMarker getdSGeneMarker() {
		return dSGeneMarker;
	}

	public String getGoInfoStr() {
		if(!goInfoStr.equals("pending") || GeneOntologyTree.getInstance()==null)
			return goInfoStr;
		
		setGoInfoStr();

		return goInfoStr;
	}

	public String getGeneType() {
		if(!geneType.equals("pending") || GeneOntologyTree.getInstance()==null)
			return geneType;
		
		geneType = GeneOntologyUtil.checkMarkerFunctions(dSGeneMarker);

		return geneType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CellularNetWorkElementInformation) {
			return dSGeneMarker.getGeneName().equals(
					((CellularNetWorkElementInformation) obj).getdSGeneMarker()
							.getGeneName())
					&& dSGeneMarker.getLabel().equals(
							((CellularNetWorkElementInformation) obj)
									.getdSGeneMarker().getLabel());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + dSGeneMarker.getGeneName().hashCode();
		hash = hash * 31 + dSGeneMarker.getLabel().hashCode();
		return hash;
	}
}
