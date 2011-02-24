package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.text.CollationKey;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * 
 * @author zji
 * @version $Id$
 *
 */
public class MindyGeneMarker implements Serializable {
	private static final long serialVersionUID = 3982199635486005070L;

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
	
	public CollationKey getNameSortKey(){
		return this.nameSortKey;
	}
	
	public CollationKey getDescriptionSortKey(){
		return this.descSortKey;
	}
	
}
