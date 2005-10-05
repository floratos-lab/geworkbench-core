package org.geworkbench.components.alignment.blast;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BlastDataOutOfBoundException extends Exception {
    public BlastDataOutOfBoundException() {
    }


    public BlastDataOutOfBoundException(String errorMessage) {
        super(errorMessage);
    }

    public BlastDataOutOfBoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
