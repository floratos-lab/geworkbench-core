package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.properties.DSSequential;
import org.geworkbench.bison.datastructure.properties.DSUnigene;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class is used to represent any arbitrary genetic marker, such as an Affy
 * probe, a DNA sequence, etc. The accession should be the universal identifier
 * of this data. This should be compatible with caBIO representation
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version $Id$
 */

public interface DSGeneMarker extends Comparable<DSGeneMarker>, DSSequential,
		Cloneable, Serializable {

	/**
	 * Returns the textual description of this Marker
	 * 
	 * @return a String representing the textual representation
	 */
	String getDescription();

	void setDescription(String label);

	/**
	 * Returns a unique identifier that represent this piece of genetic
	 * information
	 * 
	 * @return a unique identifier
	 */
	int getGeneId();
	/**
	 * return all entrez ID's for this 'marker' (probe set) from annotation file
	 * @return
	 */
	int[] getGeneIds();

	DSUnigene getUnigene();

	/**
	 * @return String
	 */
	String getShortName();
	/**
	 * return all gene symbols for this 'marker' (probe set) from annotation file
	 * @return
	 */
	String[] getShortNames();

	/**
	 * Make a deep copy of this marker.
	 * 
	 * @return
	 */
	DSGeneMarker deepCopy();

	void write(BufferedWriter writer) throws IOException;

	public void setGeneId(int x);

	public void setGeneName(String name);

	public String getGeneName();

	public Object clone();

}
