package org.geworkbench.util.session;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: The interface let us query sessions on
 * different servers</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public interface SessionQuery {
    /**
     * This method deletes a session on the server.
     *
     * @param sessionId int the id of the session
     * @throws SessionOperationException
     */
    void deleteSession(int sessionId) throws SessionOperationException;

    /**
     * This method gets an array of session.
     *
     * @return SessionStat[]
     * @throws SessionOperationException
     */
    SessionStat[] getSession() throws SessionOperationException;

    /**
     * Closes this SessioQuery.
     *
     * @throws SessionOperationException
     */
    void close() throws SessionOperationException;
}
