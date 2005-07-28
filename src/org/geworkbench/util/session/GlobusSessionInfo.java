package org.geworkbench.util.session;

import globus.soapPD_wsdl.SoapPDPortType;


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
public class GlobusSessionInfo {
    private int type;
    private int sessionid;
    private String name;
    private SoapPDPortType port;

    public GlobusSessionInfo(int sessionid, int type, String name, SoapPDPortType port) {
        this.sessionid = sessionid;
        this.type = type;
        this.name = name;
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public int getSessionId() {
        return sessionid;
    }

    public String getName() {
        return name;
    }

    public SoapPDPortType getPort() {
        return port;
    }
}
