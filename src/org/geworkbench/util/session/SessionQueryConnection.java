package org.geworkbench.util.session;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Returns a sessionQuery object.</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public interface SessionQueryConnection {
    /**
     * This method should return a SessionQuery object
     *
     * @param user     String
     * @param password String
     * @param url      String
     * @param port     int
     * @return SessionQuery
     * @throws SessionOperationException
     */
    SessionQuery getSessionQuery(String user, String password, String url, int port) throws SessionOperationException;
}
