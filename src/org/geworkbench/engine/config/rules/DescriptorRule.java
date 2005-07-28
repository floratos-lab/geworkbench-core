package org.geworkbench.engine.config.rules;

import org.apache.commons.digester.ObjectCreateRule;
import org.xml.sax.Attributes;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Invoked to process the top-most tag, namely the pattern "component-descriptor". It
 * instantiated and pushes into the <code>Digester</code> an object of type
 * <code>DescriptorObject</code>.
 */
public class DescriptorRule extends ObjectCreateRule {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------

    private PluginObject pluginObject;

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public DescriptorRule(PluginObject pluginObject) {
        super(DescriptorObject.class);
        this.pluginObject = pluginObject;
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------

    @Override public void begin(Attributes attributes) throws Exception {
        super.begin(attributes);
        ((DescriptorObject) getDigester().peek()).setPluginObject(pluginObject);
    }

    /**
     * Overrides the corresponding method from <code>ObjectCreateRule</code>.
     * Called at the end of parsing, in order to finish up.
     *
     * @throws Exception
     */
    public void finish() throws Exception {
    }

}

