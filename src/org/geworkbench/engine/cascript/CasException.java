package org.geworkbench.engine.cascript;

/**
 * Exception class: messages are generated in various classes
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasException.java,v 1.2 2005-08-18 20:43:53 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasException extends RuntimeException {
    CasException(String msg) {
        System.err.println("Error: " + msg);
    }
}
