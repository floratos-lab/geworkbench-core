package org.geworkbench.engine.config.rules;

/**
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version $Id$
 */

/**
 * Thrown when a plugin that does not implement interface <code>MenuListener</code>
 * attempts to register an <code>ActionListener</code> with a menu item in the
 * application configuration file.
 */
public class NotMenuListenerException extends org.geworkbench.util.BaseException {
	private static final long serialVersionUID = 1294386556835006669L;

	// ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public NotMenuListenerException() {
        super();
    }

    public NotMenuListenerException(String message) {
        super(message);
    }

}