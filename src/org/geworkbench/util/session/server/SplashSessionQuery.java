package org.geworkbench.util.session.server;

import org.apache.axis.types.UnsignedInt;
import org.geworkbench.util.session.*;
import org.geworkbench.util.remote.Connection;
import org.geworkbench.util.remote.ConnectionCreationException;
import polgara.soapPD_wsdl.LoginToken;
import polgara.soapPD_wsdl.SessionInfo;
import polgara.soapPD_wsdl.SoapPDPortType;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: The class implements the sessionQuery for the splash server</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public class SplashSessionQuery implements SessionQuery {
    //splash port
    private SoapPDPortType port;
    //token for remoter operations
    private LoginToken token = new LoginToken();

    public SessionQuery getSessionQuery(String user, String password, String url, int port) throws SessionOperationException {
        return null;
    }

    public SplashSessionQuery(String user, char[] password, String host, int port) throws SessionOperationException {
        try {
            URL url = getURL(host, port);
            Connection connection = getConnection(url);
            Logger logger = getLogger(connection, user, password);
            this.port = connection.getPort();
            setUserId(logger.getUserId());
        } catch (ConnectionCreationException exp) {
            throw new SessionOperationException("Could not establish connection. Please check parameters.");
        } catch (MalformedURLException exp) {
            throw new SessionOperationException("Could not form a URL. \nPlease check port and host.");
        } catch (LoggerException exp) {
            throw new SessionOperationException("The system could not log you in.\n" + exp.getMessage());
        }
    }

    private void setUserId(int userId) {
        UnsignedInt userIdInt = new UnsignedInt(userId);
        token.setUserId(userIdInt);
    }

    private void setSessionId(int sId) {
        UnsignedInt sInt = new UnsignedInt(sId);
        token.setSessionId(sInt);
    }

    private Connection getConnection(URL url) throws ConnectionCreationException {
        return new Connection(url);
    }

    private Logger getLogger(Connection connection, String user, char[] pw) throws LoggerException {
        return new Logger(connection, user, pw);
    }

    /**
     * From SessionQuery interface.
     *
     * @param sessionId int
     * @throws SessionOperationException
     */
    public void deleteSession(int sessionId) throws SessionOperationException {
        try {
            setSessionId(sessionId);
            if (0 != port.deleteSession(token)) {
                throw new SessionOperationException("Session was not deleted. (Are all operations done?)");
            }
        } catch (RemoteException exp) {
            throw new SessionOperationException("Connection error while trying to delete the seesion.");
        }

    }

    /**
     * From SessionQuery interface.
     *
     * @return SessionStat[]
     * @throws SessionOperationException
     */
    public SessionStat[] getSession() throws SessionOperationException {
        try {
            SessionInfo info[] = port.getAllSession(token);
            SessionStat[] stats = new SessionStat[info.length];
            for (int i = 0; i < stats.length; ++i) {
                int sessionId = info[i].getSessionId().intValue();
                String name = info[i].getName();
                String type = (info[i].getType() == 0) ? "DNA" : "Protein";
                double percent = info[i].getPercentComplete();
                stats[i] = new SessionStat(sessionId, name, type, percent, null);
            }
            return stats;
        } catch (RemoteException exp) {
            throw new SessionOperationException("Error while getting the sessions.");
        }
    }

    /**
     * From SessionQuery interface.
     *
     * @throws SessionOperationException
     */
    public void close() throws SessionOperationException {
        try {
            UnsignedInt usrId = new UnsignedInt();
            port.logout((token.getUserId().intValue()));
        } catch (RemoteException exp) {
            throw new SessionOperationException("Error while closing the session.");
        }
    }

    private URL getURL(String host, int port) throws MalformedURLException {
        URL url = null;
        url = Connection.getURL(host, port);
        return url;
    }
}
