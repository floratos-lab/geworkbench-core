package org.geworkbench.util.session;

import org.apache.axis.types.UnsignedInt;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import org.geworkbench.util.associationdiscovery.cluster.hierarchical.Node;
import org.geworkbench.util.patterns.CSMatchedHMMSeqPattern;
import org.geworkbench.util.patterns.CSMatchedSeqPattern;
import org.geworkbench.util.remote.Connection;
import polgara.soapPD_wsdl.HMMLoci;
import polgara.soapPD_wsdl.LoginToken;
import polgara.soapPD_wsdl.Parameters;
import polgara.soapPD_wsdl.SoapPDPortType;
import polgara.soapPD_wsdl.holders.*;

import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;
import java.rmi.RemoteException;


/**
 * <p>Title: Session</p>
 * <p>Description: Class Session describes a
 * session. A session is an abstraction of a session on
 * a SPLASH server on which different queries
 * will be perfomred. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Aner
 * @version 1.0
 */
public class Session {
    // indicate if this is a normal SPLASH session(true) or globus(false)
    public static boolean isNormalSession = true;

    //user id and session id as known on the host
    private LoginToken logToken = new LoginToken();

    //we make all our calls through this Soap port.
    private SoapPDPortType soapPort;

    //the database for this session
    private DSSequenceSet database;

    //the name Session's name.
    private String sessionName;

    //the last parameters which were set for this session
    private Parameters parameter;

    //sessionType
    private final int sType; //either DNA or Protein

    //the database will be saved with this name on the server
    private String databaseName;

    //user name of this session
    private String userName;
    private Connection connection;

    //signals that a method throw A session exception
    //This lets us delete the session
    private boolean failed = false;
    private GlobusSession globSession;

    /**
     * Create a new session with the given on the remote server
     *
     * @param sessionName  a name for the session.
     * @param database     the sequences database.
     * @param databaseName the name of the database. this name will used to save
     *                     the database on the server.
     * @param connection   a link to execute calls to a server.
     * @param userName     the user name
     * @param userId       for creating a session
     * @throws SessionCreationException if a a call to the server failed.
     */
    public Session(String sessionName, DSSequenceSet database, String databaseName, Connection connection, String userName, int userId) throws SessionCreationException {
        /** @todo   fix this  type matching!  ... (dna=0 protein=1 on server)*/
        sType = (database.isDNA()) ? 0 : 1;

        if (Session.isNormalSession) {
            try {
                init(sessionName, database, databaseName, connection, userName);

                //Now we actually create the session on the server
                int sessionId = createSession(userId, sessionName, sType);
                setLogToken(userId, sessionId);
            } catch (RemoteException exp) {
                throw new SessionCreationException("Could not reach the server.");
            }
        } else {
            globSession = new GlobusSession(sessionName, database, databaseName, connection.getInnerConnection(), userName, userId);
        }
    }

    /**
     * The function does not create a new session on the server. It just "reconnects"
     * to the already established session.
     *
     * @param sessionName  String
     * @param database     CSSequenceSet
     * @param databaseName String
     * @param connection   Connection
     * @param userName     String
     * @param userId       int
     * @param sessionId    int
     */
    public Session(String sessionName, DSSequenceSet database, String databaseName, Connection connection, String userName, int userId, int sessionId) {
        sType = (database.isDNA()) ? 0 : 1;

        if (Session.isNormalSession) {
            init(sessionName, database, databaseName, connection, userName);
            setLogToken(userId, sessionId);
        } else {
            globSession = new GlobusSession(sessionName, database, databaseName, connection.getInnerConnection(), userName, userId, sessionId);
        }
    }

    private void setLogToken(int userId, int sessionId) {
        UnsignedInt userIdInt = new UnsignedInt(userId);
        logToken.setUserId(userIdInt);

        UnsignedInt sessionIdInt = new UnsignedInt(sessionId);
        logToken.setSessionId(sessionIdInt);
    }

    private void init(String sessionName, DSSequenceSet database, String databaseName, Connection connection, String userName) {
        this.database = database;
        this.sessionName = sessionName;
        this.soapPort = connection.getPort();
        this.databaseName = databaseName;
        this.userName = userName;
        this.connection = connection;
    }

    /**
     * This method uploads a single sequence from this sessions' sequence file.
     * A call to loadSequenceRemote will save uploading time if this session's
     * database was previousely loaded.
     *
     * @param index the index of the sequence to upload. 0<= index < SequenceNo
     * @throws SessionOperationException if upload fails.
     */
    public void upload(int index) throws SessionOperationException {
        if (Session.isNormalSession) {
            if ((index < 0) || (index >= database.getSequenceNo())) {
                throw new IndexOutOfBoundsException("0<= idexRange <" + database.getSequenceNo() + " ; index = " + index);
            }

            try {
                CSSequence seq = (CSSequence)database.getSequence(index);
                seq.maskRepeats();
                addSequence(seq.getSequence(), seq.getLabel(), sType);
            } catch (RemoteException exp) {
                setState(true);
                throw new SessionOperationException("Could not load Sequences.");
            }
        } else {
            globSession.upload(index);
        }
    }

    /**
     * The method tries to read into the server's memory the session's database
     * sequence file from a remote location
     * (currently from where the splash server resides).
     *
     * @return true if this database was found remotely and was loaded into memory.
     * @throws SessionOperationException
     */
    public boolean loadSequenceRemote() throws SessionOperationException {
        if (Session.isNormalSession) {
            int retValue;

            try {
                retValue = loadSeqDB(databaseName);
            } catch (RemoteException exp) {
                setState(true);
                throw new SessionOperationException("Could not load the sequences remotely.");
            }

            return ((retValue == 0) ? true : false);
        } else {
            return globSession.loadSequenceRemote();
        }
    }

    /**
     * This method uploads all the sequences in this Session's database and saves
     * them to the server.
     * There is no need to reload a file once it is saved on the server.
     *
     * @throws SessionOperationException if a call to the server failed.
     */
    public void uploadAndSave() throws SessionOperationException {
        if (Session.isNormalSession) {
            boolean status = loadSequenceRemote();

            if (status == true) {
                System.out.println("File was loaded on server");

                return; //the file was found on the server and is loaded into memory
            }

            //upload this database and save it
            int size = database.getSequenceNo();

            for (int i = 0; i < size; i++) {
                upload(i);
            }

            saveSeqDB();
        } else {
            globSession.uploadAndSave();
        }
    }

    /**
     * This method creates a session on the server
     *
     * @param name session's name.
     * @param type session's type: DNA, Protein.
     * @return a handle id for this session.
     * @throws RemoteException if a a call to the server failed.
     */
    private int createSession(int userId, String name, int type) throws RemoteException {
        return soapPort.createSession(name, new UnsignedInt(userId), type);
    }

    /**
     * The method returns the sequence databse.
     *
     * @return the sequence database
     */
    public synchronized DSSequenceSet getSequenceDB() {
        if (Session.isNormalSession) {
            return database;
        }

        return globSession.getSequenceDB();
    }

    /**
     * Set the search parameters.
     *
     * @throws SessionOperationException if the parameters cant be set.
     * @params parms parameters for this session
     */
    public int setParameters(Parameters parms) throws SessionOperationException {
        if (Session.isNormalSession) {
            int returnVal;
            try {
                returnVal = soapPort.setParameters(logToken, parms);
            } catch (RemoteException exp) {
                setState(true);
                throw new SessionOperationException("Could not set parameters for the session.");
            }

            parameter = parms;

            return returnVal;
        } else {
            return globSession.setParameters(parms);
        }
    }

    /**
     * Get the parametes which were set for this session
     *
     * @return parameters;
     */
    public Parameters getParameter() {
        return (Session.isNormalSession) ? parameter : globSession.getParameter();
    }

    /**
     * Get the number of patterns found.
     *
     * @throws SessionOperationException
     */
    public int getPatternNo() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.getPatternNo(logToken);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not get total pattern number.");
            }
        } else {
            return globSession.getPatternNo();
        }
    }

    public Parameters getParameters() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                IntHolder minSupport = new IntHolder(); //1
                IntHolder minTokens = new IntHolder();
                IntHolder window = new IntHolder();
                IntHolder minWTokens = new IntHolder();
                IntHolder exactTokens = new IntHolder(); //5
                IntHolder countSeq = new IntHolder();
                IntHolder exact = new IntHolder();
                IntHolder printDetails = new IntHolder();
                IntHolder sortMode = new IntHolder();
                IntHolder groupingType = new IntHolder(); //10
                IntHolder groupingN = new IntHolder();
                IntHolder outputMode = new IntHolder();
                DoubleHolder minPer100Support = new DoubleHolder();
                IntHolder computePValue = new IntHolder();
                DoubleHolder minPValue = new DoubleHolder(); //15
                IntHolder threadNo = new IntHolder();
                IntHolder threadId = new IntHolder();
                IntHolder minPatternNo = new IntHolder();
                IntHolder maxPatternNo = new IntHolder();
                IntHolder maxRunTime = new IntHolder(); //20
                StringHolder similarityMatrix = new StringHolder();
                DoubleHolder similarityThreshold = new DoubleHolder();
                StringHolder inputName = new StringHolder();
                StringHolder outputName = new StringHolder();
                ExhaustiveHolder exhaustive = new ExhaustiveHolder();
                HierarchicalHolder hierarchical = new HierarchicalHolder();
                ProfileHMMHolder profile = new ProfileHMMHolder();

                soapPort.getParameters(logToken, minSupport, minTokens, window, minWTokens, exactTokens, countSeq, exact, printDetails, sortMode, groupingType, groupingN, outputMode, minPer100Support, computePValue, minPValue, threadNo, threadId, minPatternNo, maxPatternNo, maxRunTime, similarityMatrix, similarityThreshold, inputName, outputName, exhaustive, hierarchical, profile);

                Parameters p = new Parameters();
                p.setMinSupport(minSupport.value);
                p.setMinTokens(minTokens.value);
                p.setWindow(window.value);
                p.setMinWTokens(minWTokens.value);
                p.setExactTokens(exactTokens.value);
                p.setCountSeq(countSeq.value);
                p.setExact(exact.value);
                p.setPrintDetails(printDetails.value);
                p.setSortMode(sortMode.value);
                p.setGroupingType(groupingType.value);
                p.setGroupingN(groupingN.value);
                p.setOutputMode(outputMode.value);
                p.setMinPer100Support(minPer100Support.value);
                p.setComputePValue(computePValue.value);
                p.setMinPValue(minPValue.value);
                p.setThreadNo(threadNo.value);
                p.setThreadId(threadId.value);
                p.setMinPatternNo(minPatternNo.value);
                p.setMaxPatternNo(maxPatternNo.value);
                p.setMaxRunTime(maxRunTime.value);
                p.setSimilarityMatrix(similarityMatrix.value);
                p.setSimilarityThreshold(similarityThreshold.value);
                p.setInputName(inputName.value);
                p.setOutputName(outputName.value);
                p.setExhaustive(exhaustive.value);
                p.setHierarchical(hierarchical.value);
                p.setProfile(profile.value);

                return p;
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not get Parameters.");
            }
        } else {
            return globSession.getParameters();
        }
    }

    /**
     * Add a sequence to the session.
     *
     * @throws RemoteException
     */
    private int addSequence(java.lang.String sequence, java.lang.String label, int isDNA) throws RemoteException {
        return soapPort.addSequence(logToken, sequence, label, isDNA);
    }

    /**
     * This method reports if a discovery is running or finished.
     *
     * @return true if finished, else false
     * @throws SessionOperationException if a a call to the server failed.
     */
    public boolean isDone() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.isDone(logToken);
            } catch (Exception ex) {
                setState(true);
                throw new SessionOperationException("Could not determine session status.");
            }
        } else {
            return globSession.isDone();
        }
    }

    public String getDataFileName() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.getDataFileName(logToken);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not determine session status.");
            }
        } else {
            return globSession.getDataFileName();
        }
    }

    /**
     * Save the database on the server.
     */
    public int saveSeqDB() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.saveSeqDB(logToken, databaseName);
            } catch (java.rmi.RemoteException exp) {
                setState(true);
                throw new SessionOperationException("Could not save the sequences.");
            }
        } else {
            return globSession.saveSeqDB();
        }
        // return 0;
    }

    /**
     * Loads a sequence Database into the server's memory from the server's local
     * file system.
     *
     * @return 0 on success otherwise 1
     */
    private int loadSeqDB(java.lang.String name) throws java.rmi.RemoteException {
        return soapPort.loadSeqDB(logToken, name);
    }

    /**
     * Start a discovery on the database. The method blocks until the discovery
     * is done.
     *
     * @throws SessionOperationException if the discovery can't run.
     */
    public int discover(String algorithm) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.discover(logToken, algorithm);
            } catch (RemoteException ex) {
                setState(true);

                String msg = ex.getMessage();
                throw new SessionOperationException("Unable to run discovery. " + msg);
            }
        } else {
            return globSession.discover(algorithm);
        }
    }

    /**
     * Delete this session on the server.
     */
    public int deleteSession() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.deleteSession(logToken);
            } catch (RemoteException exp) {
                setState(true);
                throw new SessionOperationException("Session was not deleted. Server was not reached.");
            }
        } else {
            return globSession.deleteSession();
        }
    }

    public void getPattern(int patId, org.geworkbench.util.patterns.CSMatchedSeqPattern pattern) throws SessionOperationException {
        if (Session.isNormalSession) {
            DoubleHolder pValue = new DoubleHolder();
            ByteArrayHolder loci = new ByteArrayHolder();

            try {
                soapPort.getPattern(logToken, patId, pattern.idNo, pattern.seqNo, pValue, pattern.offset, loci);
                pattern.setPValue(pValue.value);
                pattern.locus = loci.value;
                String s = null;
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not get the pattern.");
            }
        } else {
            globSession.getPattern(patId, pattern);
        }
    }

    /**
     * Sort the patterns on the server.
     *
     * @param sortmode sort on field sortmode
     * @throws SessionOperationException
     */
    public int sortPatterns(int sortMode) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.sortPatterns(logToken, sortMode);
            } catch (Exception ex) {
                setState(true);
                throw new SessionOperationException("Could not sort the patterns.");
            }
        } else {
            return globSession.sortPatterns(sortMode);
        }
    }

    /**
     * Return the percentage of completion search.
     *
     * @throws SessionOperationException
     */
    public double getCompletion() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.getCompletion(logToken);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not get completion status.");
            }
        } else {
            return globSession.getCompletion();
        }
    }

    public void stop() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                soapPort.stop(logToken);
            } catch (RemoteException ex) {
                setState(true);
                System.out.println("ex: " + ex.getMessage());
                throw new SessionOperationException("Could not stop the algorithm.");
            }
        } else {
            globSession.stop();
        }
    }

    /**
     * Mask a pattern on the server.
     *
     * @throws SessionOperationException
     */
    public int maskPattern(int patId, int complete) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.maskPattern(logToken, patId, complete);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not mask the pattern.");
            }
        } else {
            return globSession.maskPattern(patId, complete);
        }
    }

    /**
     * Unmask the entire sequences on the server.
     *
     * @throws SessionOperationException
     */
    public void unmask() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                soapPort.unmask(logToken);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not mask all sequences.");
            }
        } else {
            globSession.unmask();
        }
    }

    public String getAlgorithmName() throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.getAlgorithmName(logToken);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could not get Algorithm Name.");
            }
        } else {
            return globSession.getAlgorithmName();
        }
    }

    /**
     * Return a Node structure based on a path
     *
     * @param path String
     * @param node Node
     * @return Node - may return null if no pattern was found.
     * @throws SessionOperationException
     */
    public Node getPatternNode(String path) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                SOAPPatternHolder patHolder = new SOAPPatternHolder();
                IntHolder patIncluded = new IntHolder();
                IntHolder patExcluded = new IntHolder();
                IntHolder hPatIncluded = new IntHolder();
                IntHolder hPatExcluded = new IntHolder();
                HMMPatternHolder hmmPat = new HMMPatternHolder();
                // soapPort.getPatternNode(logToken, path, 0, patHolder,
                //   patIncluded, patExcluded);
                soapPort.getPatternNode(logToken, path, 0, patHolder, hmmPat, hPatIncluded, hPatExcluded, patIncluded, patExcluded);

                //check if the pattern was found
                if ((patIncluded.value == 0) && (patExcluded.value == 0)) {
                    //not found...
                    return null;
                }

                org.geworkbench.util.patterns.CSMatchedSeqPattern pattern = new CSMatchedSeqPattern(database);
                pattern.idNo = new IntHolder(patHolder.value.getIdNo());
                pattern.seqNo = new IntHolder(patHolder.value.getSeqNo());
                pattern.setPValue(patHolder.value.getPValue());
                pattern.locus = patHolder.value.getLoci();
                pattern.offset = new ArrayOfSOAPOffsetHolder(patHolder.value.getOffset());

                Node node = new Node(pattern);
                node.patIncluded = patIncluded.value;
                node.patExcluded = patExcluded.value;

                String conSeq = hmmPat.value.getConsensusSeq();

                HMMLoci[] hmmArr = hmmPat.value.getLoci().getItem();
                node.hmmPattern = new CSMatchedHMMSeqPattern(database, conSeq, hmmArr);

                node.hPatIncluded = hPatIncluded.value;
                node.hPatExcluded = hPatExcluded.value;

                return node;
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("RemoteException in fetching node.");
            }
        } else {
            return globSession.getPatternNode(path);
        }
    }

    /**
     * Mask Locus on the server.
     *
     * @param locuse - locus array to mask
     * @param extent - mask [offset+from, offset+to] were offset is calculated from
     *               the locus array
     * @param mask   - 0 masks 1 unmasks
     */
    public int maskPatternLocus(byte[] locus, int from, int to, int mask) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.maskPatternLocus(logToken, locus, from, to, mask);
            } catch (RemoteException ex) {
                setState(true);
                ex.printStackTrace();
                throw new SessionOperationException("Could not mask the pattern Locus.");
            }
        } else {
            return globSession.maskPatternLocus(locus, from, to, mask);
        }
    }

    /**
     * Service definition of function ns__SetStatus
     *
     * @throws SessionOperationException
     */
    public int setStatus(int[] ids, int enable) throws SessionOperationException {
        if (Session.isNormalSession) {
            try {
                return soapPort.setStatus(logToken, ids, enable);
            } catch (RemoteException ex) {
                setState(true);
                throw new SessionOperationException("Could set the status.");
            }
        } else {
            return globSession.setStatus(ids, enable);
        }
    }

    /**
     * This method returns the name of this session.
     *
     * @return this session name
     */
    public String getSessionName() {
        return (Session.isNormalSession) ? sessionName : globSession.getSessionName();
    }

    /**
     * This method returns the number of sequences in the database file of
     * the current session.
     *
     * @retrun number of sequences.
     */
    public int getSequenceNo() {
        return (Session.isNormalSession) ? database.getSequenceNo() : globSession.getSequenceNo();
    }

    /**
     * The method return true if a seesion failed on one of it method calls
     *
     * @return true if a session failed else false
     */
    public boolean isFailed() {
        return (Session.isNormalSession) ? this.failed : globSession.isFailed();
    }

    private void setState(boolean fail) {
        this.failed = fail;
    }

    /**
     * This method returns the connection object for this session.
     *
     * @return Connection for this session.
     */
    public Connection getConnection() {
        return connection;
    }

    public String getUserName() {
        return (Session.isNormalSession) ? userName : globSession.getUserName();
    }

    public int getUserId() {
        return (Session.isNormalSession) ? logToken.getUserId().intValue() : globSession.getUserId();
    }
}
