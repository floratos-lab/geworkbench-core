package org.geworkbench.util.session.server;

import org.geworkbench.util.session.SessionOperationException;
import org.geworkbench.util.session.SessionQuery;
import org.geworkbench.util.session.SessionQueryConnection;

public class BlastSessionQueryFactory implements SessionQueryConnection {
    public BlastSessionQueryFactory() {
    }

    public SessionQuery getSessionQuery(String user, String password, String url, int port) throws SessionOperationException {
        return new BlastSessionQuery(user, password.toCharArray(), url, port);
    }
}
