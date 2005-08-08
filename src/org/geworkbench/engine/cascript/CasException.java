package org.geworkbench.engine.cascript;

/**
 * Exception class: messages are generated in various classes
 * <p/>
 * Can we do better?
 *
 * @author Hanhua Feng - hf2048@columbia.edu
 * @version $Id: CasException.java,v 1.1 2005-08-08 15:57:48 watkin Exp $
 * @modified by Behrooz Badii to CasException.java
 */
class CasException extends RuntimeException {
    CasException(String msg) {
        System.err.println("Error: " + msg);
    }
}
