package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.text.CollationKey;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

public class MindyGeneMarker implements Serializable {
	private DSGeneMarker marker;		// the original gene marker
	
	transient private CollationKey nameSortKey;	// for sorting
	transient private CollationKey descSortKey;	// for sorting
	
	public MindyGeneMarker(DSGeneMarker marker, CollationKey nameKey, CollationKey descKey){
		this.marker = marker;
		this.nameSortKey = nameKey;
		this.descSortKey = descKey;
	}
	
	public DSGeneMarker getGeneMarker(){
		return this.marker;
	}
	
	public void setGeneMarker(DSGeneMarker marker){
		this.marker = marker;
	}
	
	public CollationKey getNameSortKey(){
		return this.nameSortKey;
	}
	
	public void setNameSortKey(CollationKey key){
		this.nameSortKey = key;
	}
	
	public CollationKey getDescriptionSortKey(){
		return this.descSortKey;
	}
	
	public void setDescriptionSortKey(CollationKey key){
		this.descSortKey = key;
	}
}
