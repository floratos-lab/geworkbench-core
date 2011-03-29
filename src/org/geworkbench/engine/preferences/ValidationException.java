package org.geworkbench.engine.preferences;

/**
 * @author John Watkinson
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = -811286462820397093L;

	public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
