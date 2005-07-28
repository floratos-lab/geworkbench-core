package org.geworkbench.util.session.server;

import org.apache.axis.types.UnsignedInt;
import org.geworkbench.util.session.SoapClient;
import org.geworkbench.util.session.SessionOperationException;
import org.geworkbench.util.session.SessionQuery;
import org.geworkbench.util.session.SessionStat;

import java.util.StringTokenizer;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class BlastSessionQuery implements SessionQuery {

    public BlastSessionQuery(String user, char[] password, String host, int port) throws SessionOperationException {
        //super(user, password, host, port);

    }

    public void deleteSession(int sessionId) throws SessionOperationException {
        /**@todo Implement this org.geworkbench.bisonparsers.session.SessionQuery method*/
        throw new java.lang.UnsupportedOperationException("Method deleteSession() not yet implemented.");
    }

    /**
     * parseSessionResult
     */
    public SessionStat[] parseSessionResult(String resultString) throws SessionOperationException {

        if (resultString.startsWith("No job"))
            throw  new SessionOperationException("No session was found");

        StringTokenizer st = new StringTokenizer(resultString, ">");
        int jobNumber = st.countTokens();
        SessionStat[] ss = new SessionStat[jobNumber];
        for (int i = 0; i < jobNumber; i++) {
            String jobValue = st.nextToken();
            StringTokenizer st1 = new StringTokenizer(jobValue, "|");
            //if(st1.countTokens()<4)
            //   System.out.println("SOMETHING WRONG!" + jobValue);
            System.out.println("SOMETHING WRONG!" + jobValue);
            String[] str = jobValue.split("\\|");
            for (int j = 0; j < str.length; j++)
                System.out.println(str[j]);
            ss[i] = new SessionStat(i, str[0], str[1], 0.2, str[3]);
        }
        return ss;

    }

    public SessionStat[] getSession() throws SessionOperationException {
        SoapClient sc = new SoapClient();
        System.out.println(sc.getSession("pb"));
        // SessionStat s1 = new SessionStat(2, "btk", sc.getSession("pb"), 0.1, "nt");
        SessionStat[] ss = parseSessionResult(sc.getSession("pb"));
        return ss;

        //throw new java.lang.UnsupportedOperationException("Method getSession() not yet implemented.");
    }

    public void close() throws SessionOperationException {
        /**@todo Implement this org.geworkbench.bisonparsers.session.SessionQuery method*/
        //try{
        UnsignedInt usrId = new UnsignedInt();

        //}catch(RemoteException exp){
        // throw new SessionOperationException("Error while closing the session.");
        // }

    }

}
