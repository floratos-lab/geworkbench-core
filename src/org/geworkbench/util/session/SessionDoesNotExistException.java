package org.geworkbench.util.session;

/**
 * <p>Title: SessionDoesNotExistException</p>
 * <p>Description: Signals that a session with this name doesnot exits</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Aner
 * @version 1.0
 */

public class SessionDoesNotExistException extends Exception {
    public SessionDoesNotExistException() {
    }

    public SessionDoesNotExistException(String message) {
        super(message);
    }
}