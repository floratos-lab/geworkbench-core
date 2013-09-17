package org.geworkbench.util;

/**
 * Used by classifiers to indicate an exception.
 * User: mhall
 * Date: Jan 11, 2006
 * Time: 5:22:41 PM
 */
public class ClassifierException extends Exception {
	private static final long serialVersionUID = 7217613511326291355L;

	public ClassifierException(String message) {
        super(message);
    }
}
