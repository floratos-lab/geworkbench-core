package org.geworkbench.util.session.server;

import org.geworkbench.util.session.SessionOperationException;
import org.geworkbench.util.session.SessionQuery;
import org.geworkbench.util.session.SessionQueryConnection;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: A factory for creating splash session queries</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public class SplashSessionQueryFactory implements SessionQueryConnection {
    public SplashSessionQueryFactory() {
    }

    public SessionQuery getSessionQuery(String user, String password, String url, int port) throws SessionOperationException {
        return new SplashSessionQuery(user, password.toCharArray(), url, port);
    }
}
