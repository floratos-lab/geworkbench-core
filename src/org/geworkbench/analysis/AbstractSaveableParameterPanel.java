package org.geworkbench.analysis;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.model.analysis.ParamValidationResults;
import org.geworkbench.bison.model.analysis.ParameterPanel;

/**
 * @author First Genetic Trust Inc.
 * @author keshav
 * @author yc2480
 * @version $Id: AbstractSaveableParameterPanel.java,v 1.5 2009-02-12 22:28:14 keshav Exp $
 */

/**
 * This class is used to (1) store the parameters of an analysis, and (2) manage
 * the visual representation (gui) that will be offered to the user in order to
 * provide values for the parameters. This abstract class provides default
 * implementations for a number of tasks, including:
 * <UL>
 * <LI>Assigning a name to a set of parameter values: per the relevant use
 * case, the settings used in performing an analysis can be stored under
 * user-specified name and later potentially retrieved to be reused in another
 * application of that same analysis.</LI>
 * </UL>
 */
public class AbstractSaveableParameterPanel extends ParameterPanel implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4513020172986430772L;
	private Log log = LogFactory.getLog(this.getClass());
	String name = null;

	/**
	 * 
	 */
	public AbstractSaveableParameterPanel() {
		setName("Parameters");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.model.analysis.ParameterPanel#getName()
	 */
	public String getName() {
		assert name != null;
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geworkbench.bison.model.analysis.ParameterPanel#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * We use firstTime information to select the last saved parameter set in
	 * the JList
	 */
	private boolean firstTime = true;

	/**
	 * 
	 * @return If you never called setFirstTime(false) yet, it will return true.
	 */
	public boolean isFirstTime() {
		return firstTime;
	}

	/**
	 * 
	 * @param setTo
	 *            Use setFirstTime(false) to indicate you already selected the
	 *            last saved parameter
	 */
	public void setFirstTime(boolean setTo) {
		firstTime = setTo;
	}

	/**
	 * Return the result of validating the values of the parameters managed by
	 * this panel. This method will have to be overridden by classes that extend
	 * <code>AbstractSaveableParameterPanel</code>.
	 * 
	 * @return
	 */
	public ParamValidationResults validateParameters() {
		// TODO This method should be made abstract when the
		// class is made abstract.
		return new ParamValidationResults(true, "No Error");
	}

	/*
	 * Override this method to generate dataset history automatically from
	 * parameter panel
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#toString()
	 */
	public String toString() { // 
		String answer = "toString() not implemented in parameter panel";
		return answer;
	}

	/**
	 * Set the parameters to current parameter panel. see setParameters() in
	 * HierClustPanel.java as example. Since this is not a real abstract class,
	 * someone could forget to implement this method, so I print something to
	 * console to remind programmer.
	 */
	public void setParameters(Map<Serializable, Serializable> parameters) {
		// TODO This method should be made abstract when the
		// class is made abstract.

		if (this.getClass() != AbstractSaveableParameterPanel.class)
			log.error("setParameters() not implemented in " + this.getClass());
		/* Override this method to put parameters back to GUI */
	}

	/**
	 * Get current parameters from current parameter panel. see getParameters()
	 * in HierClustPanel.java as example. Since this is not a real abstract
	 * class, someone could forget to implement this method, so I print
	 * something to console to remind programmer.
	 */
	public Map<Serializable, Serializable> getParameters() {
		// TODO This method should be made abstract when the
		// class is made abstract.

		if (this.getClass() != AbstractSaveableParameterPanel.class)
			log.error("getParameters() not implemented in " + this.getClass());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone() Parameter Panels should override this
	 *      method, to only clone needed parameters. See clone() in
	 *      HierClustPanel.java for example.
	 * 
	 */
	@Deprecated
	public AbstractSaveableParameterPanel clone() {
		// TODO This clone() can be replaced by constructor which take
		// parameters as input.

		// TODO This method should be made abstract when the
		// class is made abstract.

		/*
		 * Override this method and do two things. 1. new an instance, 2. copy
		 * parameters to the new instance and GUI accordingly.
		 */
		if (this.getClass() != AbstractSaveableParameterPanel.class)
			log.error("clone() not implemented");

		AbstractSaveableParameterPanel newOne = new AbstractSaveableParameterPanel();
		newOne.setParameters(this.getParameters());
		return newOne;
	}

	/**
	 * This is used to serialize the parameters from a component's parameter
	 * panel. The writeReplace method instantiates this class with the
	 * component's parameters.
	 * 
	 * @author yc2480
	 * @version $Id: AbstractSaveableParameterPanel.java,v 1.4.22.9 2009/01/22
	 *          17:13:25 keshav Exp $
	 * 
	 */
	private static class SerializedInstance implements Serializable {
		private static final long serialVersionUID = 1L;
		private Map<Serializable, Serializable> parameters;

		/**
		 * 
		 * @param parameters
		 */
		public SerializedInstance(Map<Serializable, Serializable> parameters) {
			this.parameters = parameters;
		}

		/**
		 * 
		 * @return
		 * @throws ObjectStreamException
		 */
		Object readResolve() throws ObjectStreamException {
			AbstractSaveableParameterPanel aspp = new AbstractSaveableParameterPanel();
			aspp.setParameters(parameters);
			return aspp;
		}
	}

	/**
	 * Creates a new SerializedInstance with the parameters of 'concrete' panel.
	 * 
	 * @return
	 * @throws ObjectStreamException
	 */
	public Object writeReplace() throws ObjectStreamException {
		return new SerializedInstance(this.getParameters());
	}

	/*
	 * To store the Thread used for CallBack, ex: refresh the highlighted groups
	 * when parameter panel changed
	 */
	private Thread callbackThread = null;

	/**
	 * 
	 * @param callbackThread
	 *            Pass-in the thread which will do the real work when
	 *            notifyAnalysisPanel() been called.
	 */
	public void setParameterHighlightCallback(Thread callbackThread) {
		this.callbackThread = callbackThread;
	}

	private boolean stopNotifyTemporaryFlag = false;

	/**
	 * Notify AnalysisPanel to refresh high lighted group
	 */
	public void notifyAnalysisPanel() {
		if ((callbackThread != null) && (stopNotifyTemporaryFlag == false))
			callbackThread.run();
	}

	public void stopNotifyAnalysisPanelTemporary(boolean b) {
		stopNotifyTemporaryFlag = b;
	}

	public boolean getStopNotifyAnalysisPanelTemporaryFlag() {
		return stopNotifyTemporaryFlag;
	}
}
