package org.geworkbench.events;

/**
 * The event of the status change of pattern discovery algorithms.
 * 
 * @author zji
 * @version $Id: ProgressChangeEvent.java,v 1.2 2008-10-28 16:55:18 keshav Exp $
 * 
 * TODO this class should be renamed to reflect its role in pattern discovery.
 */
public class ProgressChangeEvent {
	/* the initial event of listener being added */
	private boolean initial;
	/* number of patterns found. */
	private int patternFound = 0;

	/**
	 * General constructor with the option to set to be initial or not.
	 * 
	 * @param initial
	 * @param patternFound
	 */
	public ProgressChangeEvent(boolean initial, int patternFound) {
		this.initial = initial;
		this.patternFound = patternFound;
	}

	/**
	 * Constructor for the non-initial case.
	 * 
	 * @param initial -
	 *            whether it is initial adding of listener
	 * @param patternFound
	 */
	public ProgressChangeEvent(int patternFound) {
		this.initial = false;
		this.patternFound = patternFound;
	}

	/**
	 * Returns the number of patterns found.
	 * 
	 * @return pattern the number of patterns found so far.
	 */
	public int getPatternFound() {
		return patternFound;
	}

	/**
	 * Whether this event is the one for initially adding listener.
	 * 
	 * @return
	 */
	public boolean isInitial() {
		return initial;
	}
}