package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.Serializable;
import java.util.Map;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser.MarkerAnnotation;

/**
 * @author John Watkinson
 * @version $Id: APSerializable.java,v 1.3 2009-11-25 17:31:53 jiz Exp $
 */
public class APSerializable implements Serializable {
	private static final long serialVersionUID = 6455427625940524515L;

	DSDataSet<? extends DSBioObject> currentDataSet = null;
	Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes = null;
	Map<String, MarkerAnnotation> chipTypeToAnnotation = null;


	public APSerializable(
			DSDataSet<? extends DSBioObject> currentDataSet2,
			Map<DSDataSet<? extends DSBioObject>, String> datasetToChipTypes,
			Map<String, MarkerAnnotation> chipTypeToAnnotation) {

		this.currentDataSet = currentDataSet2;
		this.datasetToChipTypes = datasetToChipTypes;
		this.chipTypeToAnnotation = chipTypeToAnnotation;
	}
}
