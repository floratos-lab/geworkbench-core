package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

/**
 * @author John Watkinson
 * @version $Id: APSerializable.java,v 1.3 2009-11-25 17:31:53 jiz Exp $
 */
public class APSerializable implements Serializable {
	private static final long serialVersionUID = 6455427625940524515L;

	DSMicroarraySet currentDataSet = null;
	Map<DSMicroarraySet, String> datasetToChipTypes = null;
	Map<DSMicroarraySet, Map<String, AnnotationFields>> datasetToAnnotation = null;


	public APSerializable(
			DSMicroarraySet currentDataSet,
			WeakHashMap<DSMicroarraySet, String> datasetToChipTypes,
			WeakHashMap<DSMicroarraySet, Map<String, AnnotationFields>> datasetToAnnotation) {

		this.currentDataSet = currentDataSet;
		this.datasetToChipTypes = new HashMap<DSMicroarraySet, String>();
		this.datasetToAnnotation = new HashMap<DSMicroarraySet, Map<String, AnnotationFields>>();
		for(DSMicroarraySet dataset : datasetToChipTypes.keySet()) {
			String s = datasetToChipTypes.get(dataset);
			if(s!=null) this.datasetToChipTypes.put(dataset, s);
		}
		for(DSMicroarraySet dataset : datasetToAnnotation.keySet()) {
			Map<String, AnnotationFields> m = datasetToAnnotation.get(dataset);
			if(m!=null) this.datasetToAnnotation.put(dataset, m);
		}
	}
}
