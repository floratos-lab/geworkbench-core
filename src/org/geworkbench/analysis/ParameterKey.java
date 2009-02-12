package org.geworkbench.analysis;

import java.io.Serializable;

/**
 * Represents the saved parameters for a specific analysis.
 * 
 * @author keshav
 * @version $Id: ParameterKey.java,v 1.2 2009-02-12 22:28:14 keshav Exp $
 * 
 */
public class ParameterKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8967255706305280349L;

	private String className = null;
	private String parameterName = null;

	/**
	 * 
	 * @param cn
	 * @param pn
	 */
	public ParameterKey(String cn, String pn) {
		className = cn;
		parameterName = pn;
	}

	/**
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * 
	 * @return
	 */
	public String getParameterName() {
		return parameterName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (className + parameterName).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object tuple) {
		if (tuple instanceof ParameterKey) {
			return ((ParameterKey) tuple).className.equals(className)
					&& ((ParameterKey) tuple).parameterName
							.equals(parameterName);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Key: " + className + "+" + parameterName;
	}
}