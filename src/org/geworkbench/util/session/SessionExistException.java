package org.geworkbench.util.session;

/**
 * <p>Title: Session Exception</p>
 * <p>Description: Signals that a session with the same name already exits.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Aner
 * @version 1.0
 */

public class SessionExistException extends Exception {
    public SessionExistException() {
    }

    public SessionExistException(String message) {
        super(message);
    }
}