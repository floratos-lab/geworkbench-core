package org.geworkbench.util.remote;

import org.geworkbench.util.session.Session;
import polgara.soapPD_wsdl.SoapPDLocator;
import polgara.soapPD_wsdl.SoapPDPortType;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * <p>Title: Connection</p>
 * <p>Description: A class to abstract a link, i.e. a connection,
 * to a Server. Note: currently we are  supporting only one connection
 * type - through the SoapPDPortType. Nevertheless, the class can easily made to
 * implement a "connection" interface if we ever want to support more connection
 * types. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Aner
 * @version 1.0
 */
public class Connection {
    private SoapPDPortType port;
    private GlobusConnection innerConnection;

    public Connection(URL serverURL) throws ConnectionCreationException {
        try {
            if (Session.isNormalSession) {
                port = connect(serverURL);
            } else {
                innerConnection = new GlobusConnection(serverURL);
            }
        } catch (ServiceException ex) {
            throw new ConnectionCreationException("Could not establish connection.");
        }
    }

    /**
     * This method tries to connect to a soap server.
     *
     * @param url a URL to the server.
     * @return a soap port
     * @throws ServiceException if cannot connect to the server.
     */
    private SoapPDPortType connect(URL url) throws ServiceException {
        return new SoapPDLocator().getsoapPD(url);
    }

    /**
     * This method returns the port of which this connection is binded to.
     *
     * @return port
     */
    public SoapPDPortType getPort() {
        return port;
    }

    public globus.soapPD_wsdl.SoapPDPortType getInnerPort() {
        return innerConnection.getPort();
    }

    public GlobusConnection getInnerConnection() {
        return innerConnection;
    }

    /**
     * This is a helper method to build a URL.
     *
     * @param host name of a host
     * @param port the port on the host
     * @return URL object
     * @throws MalformedURLException
     */
    static public URL getURL(String host, int port) throws MalformedURLException {
        String urlString = new String("http://" + host + ":" + port + "/" + "PDServ.exe");

        return new URL(urlString);
    }
}
