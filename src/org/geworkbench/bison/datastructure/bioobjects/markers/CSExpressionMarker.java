package org.geworkbench.bison.datastructure.bioobjects.markers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Range;

/**
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: First Genetic Trust
 * </p>
 * 
 * @author Andrea Califano
 * @version $Id$
 */

public class CSExpressionMarker extends CSGeneMarker implements
		org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker,
		Serializable {
	private static final long serialVersionUID = 1573294343188088869L;

	private Range range = new org.geworkbench.bison.util.Range();

	@Override
	public Object clone() {
		CSExpressionMarker clone = (CSExpressionMarker) super.clone();
		clone.range = new org.geworkbench.bison.util.Range();
		return clone;
	}

	public CSExpressionMarker() {
		markerId = 0;
	}

	public CSExpressionMarker(int id) {
		markerId = id;
	}

	/**
	 * @param marker
	 * @param isCase
	 */
	public void check(DSMutableMarkerValue marker, boolean isCase) {
		range.min = Math.min(range.min, marker.getValue());
		range.max = Math.max(range.max, marker.getValue());
		range.norm.add(marker.getValue());
	}

	/**
	 * @param id
	 * @param casesNum
	 * @param controlsNum
	 */
	public void reset(int id, int casesNum, int controlsNum) {
		// ready = false;
		range.max = -999999;
		range.min = +999999;
		range.norm = new org.geworkbench.bison.util.Normal();
		markerId = id;
	}

	public Range getRange() {
		return range;
	}

	public void write(BufferedWriter writer) throws IOException {
		writer.write(label);
		writer.write('\t');
		writer.write(label);
	}

	/**
	 * todo make this more generalize
	 * 
	 * @return String
	 */
	public String toString() {
		return super.toString();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean equals(DSGeneMarker o) {
		return label.equalsIgnoreCase(o.getLabel());
	}

	/**
	 * Program that use equals() method will automatically use this one instead
	 * of the above method.
	 * 
	 * @param o
	 * @return
	 */
	public boolean equals(Object o) {
		// change this temporarily to match on the GeneID so we can call
		// contains on markers that
		// represent equivalent genes. May need a separate class for this.
		return super.equals(o);
	}

	protected void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		super.readObject(ois);
		range = new org.geworkbench.bison.util.Range();
	}

	public DSGeneMarker deepCopy() {
		CSExpressionMarker gi = new CSExpressionMarker(markerId);
		gi.range = new org.geworkbench.bison.util.Range();
		gi.range.min = this.range.min;
		gi.range.max = this.range.max;
		gi.label = label;
		gi.geneId = geneId;
		gi.geneIds = geneIds;
		gi.unigene = unigene;
		return gi;
	}

	public int getGeneId() {
		if (this.geneId == -1 && label != null) {
			String[] entrezIds = AnnotationParser.getInfo(label,
					AnnotationParser.LOCUSLINK);
			if(entrezIds==null || entrezIds.length==0)
				return 0;
			
			String entrezId = entrezIds[0].trim();

			try {
				this.geneId = Integer.parseInt(entrezId);
			} catch (NumberFormatException e) {
				this.geneId = 0;
			}
		}
		return this.geneId;
	}

	public int[] getGeneIds() {
		if (this.geneIds == null && label != null) {
			String[] entrezIds = AnnotationParser.getInfo(label,
					AnnotationParser.LOCUSLINK);
			if (entrezIds == null || entrezIds.length <= 0 )
				return null;
			geneIds = new int[entrezIds.length];
			for (int i = 0; i < entrezIds.length; i++) {
				try {
					this.geneIds[i] = Integer.parseInt(entrezIds[i].trim());
				} catch (NumberFormatException e) {
					this.geneIds[i] = 0;
				}
			}
		}
		return this.geneIds;
	}
}
