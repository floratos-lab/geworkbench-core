package org.geworkbench.engine.config.rules;

import org.geworkbench.util.BaseException;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version $Id$
 */

/**
 * Thrown when a plugin that does not implement interface <code>VisualPlugin</code>
 * attempts to register an <code>ActionListener</code> with a menu item in the
 * application configuration file, requsting to be notified only when the plugin has
 * the focus.
 */
public class NotVisualPluginException extends BaseException {
	private static final long serialVersionUID = -879145735495136862L;

	// ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public NotVisualPluginException() {
        super();
    }

    public NotVisualPluginException(String message) {
        super(message);
    }

}