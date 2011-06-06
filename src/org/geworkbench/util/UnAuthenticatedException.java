package org.geworkbench.util;

/**  
 * @author Min You
 * @version $Id: UnAuthenticatedException.java,v 1.1 2009-12-03 19:00:52 my2248 Exp $
 */

 
public class UnAuthenticatedException extends org.geworkbench.util.BaseException {
    
	private static final long serialVersionUID = 6379819293142168996L;

	// ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public UnAuthenticatedException() {
        super();
    }

    public UnAuthenticatedException(String message) {
        super(message);
    }

}