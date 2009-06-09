package org.geworkbench.engine.config.rules;

import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**
 * Invoked to process the the pattern "geaw-config/plugin". It instantiated and
 * pushes into the Digester an object of type PluginConfigObject.
 * 
 * @author non-attributable
 * 
 */
public class PluginRuleCCM extends ObjectCreateRule {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Collection of all {@link PluginObject}s pushed on the {@link Digester}
	 * stack. This collection will be used at the end of parsing, from within
	 * the {@link org.geworkbench.engine.config.rules.PluginConfigRule#finish
	 * finish} method, in order to allow each {@link PluginObject} to perform
	 * any final postprocessing.
	 */
	// Vector<PluginObject> pluginObjects = null;
	/**
	 * 
	 * @param className
	 */
	public PluginRuleCCM(String className) {
		super(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Overrides the corresponding method from
	 * org.apache.commons.digester.ObjectCreateRule. Called after a new
	 * PluginObjecthas been pushed to the stack, in order to add the new object
	 * in the pluginObjects vector.
	 * 
	 * @see org.apache.commons.digester.Rule#begin(java.lang.String,
	 * java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void begin(String namespace, String name, Attributes attributes)
			throws Exception {

		log.info("begin rule");

		
		PluginObjectCCM pginObj;
		super.begin(namespace, name, attributes);
		pginObj = (PluginObjectCCM) super.getDigester().peek();

		/*
		 * We need to instantiate the plugin descriptor before the various
		 * CallMethod rules are called.
		 */
		pginObj.createPlugin(attributes.getValue("id"), attributes
				.getValue("name"), attributes.getValue("class"), attributes
				.getValue("source"));

		pginObj.finish();
	}

}
