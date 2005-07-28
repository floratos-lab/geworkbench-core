package org.geworkbench.bison.datastructure.exceptions;

/**
 * This exception is thrown when a fatal exception is encountered while running a process.
 */
public class DSRunException extends Exception {

    // @todo - watkin - The presence of this constructor prevents a message or chained exception from being associated with the exception.
    public DSRunException() {
    }
}
