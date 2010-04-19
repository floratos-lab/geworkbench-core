/**
 * 
 */
package org.geworkbench.bison.algorithm.classification;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * This class capture the state that represent a SVMClassifier object, 
 * or just enough to reproduce a SVMClassifier object.
 * The later option may be a smaller footprint and cost.
 * 
 * @author zji
 *
 */
public class CSSvmClassifier extends CSClassifier {
	static Log log = LogFactory.getLog(CSSvmClassifier.class);
			
	private byte[] modelFileContent;

	private List<String> featureNames;
	
	private DSPanel<DSMicroarray> casePanel = null;
	private DSPanel<DSMicroarray> controlPanel = null;

	public byte[] getModelFileContent() {
		return modelFileContent;
	}
	public List<String> getFeatureNames() {
		return featureNames;
	}

	public CSSvmClassifier(DSDataSet<?> parent, String label,
			String[] classifications, byte[] modelFileContent, List<String> featureNames, DSPanel<DSMicroarray> casePanel, DSPanel<DSMicroarray> controlPanel) {
		super(parent, label, classifications);
		log.debug("modelFileContent size is "+modelFileContent.length);
		this.featureNames = featureNames;
		this.modelFileContent = modelFileContent;
		this.casePanel = casePanel;
		this.controlPanel = controlPanel;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3965446427592157302L;

	/* (non-Javadoc)
	 * @see org.geworkbench.bison.algorithm.classification.CSClassifier#classify(float[])
	 */
	@Override
	public int classify(float[] data) {
		// this is ignored for now
		return 0;
	}

	public DSPanel<DSMicroarray> getControlPanel() {
		return controlPanel;
	}
	
	public DSPanel<DSMicroarray> getCasePanel() {
		return casePanel;
	}

}
