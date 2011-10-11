package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

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
	Map<DSMicroarraySet<? extends DSMicroarray>, Map<String, AnnotationFields>> datasetToAnnotation = null;


	public APSerializable(
			DSMicroarraySet<? extends DSMicroarray> currentDataSet,
			WeakHashMap<DSMicroarraySet<? extends DSMicroarray>, String> datasetToChipTypes,
			WeakHashMap<DSMicroarraySet<? extends DSMicroarray>, Map<String, AnnotationFields>> datasetToAnnotation) {

		this.currentDataSet = currentDataSet;
		this.datasetToChipTypes = new HashMap<DSMicroarraySet<? extends DSMicroarray>, String>();
		this.datasetToAnnotation = new HashMap<DSMicroarraySet<? extends DSMicroarray>, Map<String, AnnotationFields>>();
		for(DSMicroarraySet<? extends DSMicroarray> dataset : datasetToChipTypes.keySet()) {
			String s = datasetToChipTypes.get(dataset);
			if(s!=null) this.datasetToChipTypes.put(dataset, s);
		}
		for(DSMicroarraySet<? extends DSMicroarray> dataset : datasetToAnnotation.keySet()) {
			Map<String, AnnotationFields> m = datasetToAnnotation.get(dataset);
			if(m!=null) this.datasetToAnnotation.put(dataset, m);
		}
	}
}
