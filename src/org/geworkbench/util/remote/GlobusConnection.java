package org.geworkbench.util.remote;

import globus.soapPD_wsdl.SoapPDPortType;
import globus.soapPD_wsdl.service.SoapPDServiceLocator;
import org.globus.ogsa.utils.GridServiceFactory;
import org.gridforum.ogsi.Factory;
import org.gridforum.ogsi.OGSIServiceGridLocator;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GlobusConnection {
    private static OGSIServiceGridLocator gridLocator;
    private static Factory factory;
    private static GridServiceFactory gridFactory;
    private static boolean instantiated = false;
    private SoapPDPortType port;
    private Random rd = new Random();

    public GlobusConnection(URL serverURL) throws ConnectionCreationException {
        try {
            String guid = Integer.toString(rd.nextInt()) + InetAddress.getLocalHost().getHostName();

            if (!instantiated) {
                instantiated = true;

                // Get a reference to the SplashService Factory
                gridLocator = new OGSIServiceGridLocator();
                factory = gridLocator.getFactoryPort(serverURL);

                gridFactory = new GridServiceFactory(factory);
            }

            gridFactory.createService(guid);

            SoapPDServiceLocator splashLocator = new SoapPDServiceLocator();

            String s = serverURL.toString() + "/" + guid;
            port = splashLocator.getsoapPDPort(new URL(s));
        } catch (Exception ex) {
            throw new ConnectionCreationException("Could not establish connection.");
        }
    }

    /**
     * This method returns the port of which this connection is binded to.
     *
     * @return port
     */
    public SoapPDPortType getPort() {
        return port;
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
        String urlString = new String("http://" + host + ":" + port + "/ogsa/services/splash/" + "SplashFactoryService");

        return new URL(urlString);
    }
}
