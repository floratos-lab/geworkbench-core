package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.Serializable;
import java.util.Map;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * @author John Watkinson
 * @version $Id: APSerializable.java,v 1.3 2009-11-25 17:31:53 jiz Exp $
 */
public class APSerializable implements Serializable {
	private static final long serialVersionUID = 6455427625940524515L;

	DSMicroarraySet<? extends DSMicroarray> currentDataSet = null;
	Map<DSMicroarraySet<? extends DSMicroarray>, String> datasetToChipTypes = null;
	Map<String, Map<String, AnnotationFields>> chipTypeToAnnotation = null;


	public APSerializable(
			DSMicroarraySet<? extends DSMicroarray> currentDataSet,
			Map<DSMicroarraySet<? extends DSMicroarray>, String> datasetToChipTypes,
			Map<String, Map<String, AnnotationFields>> chipTypeToAnnotation) {

		this.currentDataSet = currentDataSet;
		this.datasetToChipTypes = datasetToChipTypes;
		this.chipTypeToAnnotation = chipTypeToAnnotation;
	}
}
