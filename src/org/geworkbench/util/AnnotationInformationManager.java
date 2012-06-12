package org.geworkbench.util;

import java.util.WeakHashMap;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

// this could replace the current mechanism to manage:
// 1) annotation file name (absolute path of the actual disk file), 
// 2) 'chipType' (in fact the file name), 
// 3) annotation type (3' or not)
// For now, it is used for annotation type only because it is not covered by workspace mechanism
public class AnnotationInformationManager {

	public enum AnnotationType {
		THREE_PRIME, OTHERS
	};

	private static class Info {
		AnnotationType annotationType;

		// File file; String chipType

		Info(AnnotationType annotationType) {
			this.annotationType = annotationType;
		}
	}

	private static final AnnotationInformationManager INSTANCE = new AnnotationInformationManager();

	WeakHashMap<DSMicroarraySet, Info> infoMap = new WeakHashMap<DSMicroarraySet, Info>();

	private AnnotationInformationManager() {

	}

	public static AnnotationInformationManager getInstance() {
		return INSTANCE;
	}

	public void add(DSMicroarraySet dataset, AnnotationType annotationType) {
		infoMap.put(dataset, new Info(annotationType));
	}

	public boolean is3Prime(DSMicroarraySet dataset) {
		Info info = infoMap.get(dataset);
		if (info != null && info.annotationType == AnnotationType.THREE_PRIME)
			return true;
		else
			return false;
	}
}
