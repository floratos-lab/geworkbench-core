package org.geworkbench.util.session;


import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import java.io.IOException;


public class SoapClient {

    //default username;
    private String username = "amdecweb";
    private String serverURL;
    private String cmd;
    private CSSequenceSet sequenceDB;
    public static long TIMEGAP = 4000;
    private final String DEFAULT_OUTPUTFILE = "testout.txt";
    private String outputfile;
    static final String STRINGURL =
            "http://adparacel.cu-genome.org/axis/servlet/AxisServlet";
    private String url = STRINGURL;
    private DSSequenceSet parentSequenceDB;


    private String blastServerInfo;
    private String inputFileName;

    /**
     * getFile
     *
     * @param filename String
     * @return boolean
     */
    public boolean getFile(String filename) {

        try {

            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService",
                                            "download")); //This is the target services method to invoke.

            QName qnameAttachment = new QName("urn:downloadfileService",
                                              "DataHandler");

            call.registerTypeMapping((new DataHandler(new URL(STRINGURL))).
                                     getClass(), //Add serializer for attachment.
                                     qnameAttachment,
                                     JAFDataHandlerSerializerFactory.class,
                                     JAFDataHandlerDeserializerFactory.class);
            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(qnameAttachment);
            Object result = call.invoke(new Object[] {filename.trim()});
            ((DataHandler) result).writeTo(new java.io.FileOutputStream(
                    getLocalFileName(filename)));
        } catch (Exception e) {

            System.out.println("ERROR in SoapClient.getFile()");
            return false;
        }
        return true;

    }

    public DSSequenceSet getParentSequenceDB() {
        return parentSequenceDB;
    }

    public void setParentSequenceDB(DSSequenceSet parentSeqeucneDB) {
        this.parentSequenceDB = parentSeqeucneDB;
    }

    /**
     * getServerInfo
     *
     * @param checkstatus String
     * @return String
     */
    public String getServerInfo(String checkstatus) {
        try {

            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService",
                                            "getServerInfo")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);
            Object result = call.invoke(new Object[] {checkstatus});
            return result.toString();

        } catch (Exception e) {
            System.out.println(e + "at SoapClient.getServerInfo()");
        }

        return "";
    }

    public String submitJob(String cmd, String inputFile, String outputFile) {
        try {
            Service service = new Service();

            Call call = (Call) service.createCall();

            //Set the target service host and service location,
            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService", "submit")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam2",
                              org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam3",
                              org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);

            //get the  filename only.
            String destinationFile = getDesFileName(outputFile);

            Object result = call.invoke(new Object[] {cmd, inputFile,
                                        destinationFile});

            return result.toString();
        } catch (IOException ex) {


        } catch (Exception e) {
            System.out.println("Cannot connect with server: " + e);

        }
        return null;

    }

    /**
     * This method sends Sequence text as an attachment then
     * receives a boolean value as a return.
     *
     * @param The sequenceDB.
     * @return True if sent successfully.
     */
    public String submitSequenceDB(CSSequenceSet sequences) throws Exception {
        boolean doTheDIME = false;

        //Create the data for the attached file.
        if (sequences == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < sequences.getSequenceNo(); i++) {
            DSSequence s = sequences.getSequence(i);
            sb.append(">" + s.getLabel() + "\n" + s.getSequence() + "\n");

        }

        DataHandler dhSource = new DataHandler(sb.toString(), "text/plain");

        Service service = new Service();

        Call call = (Call) service.createCall();

        call.setTargetEndpointAddress(new URL(serverURL)); //Set the target service host and service location,

        call.setOperationName(new QName("urn:SequenceAlignmentService",
                                        "upload")); //This is the target services method to invoke.

        QName qnameAttachment = new QName("urn:SequenceAlignmentService",
                                          "DataHandler");
        QName qnamereturntype = new QName("urn:SequenceAlignmentService",
                                          "String");

        call.registerTypeMapping(dhSource.getClass(), //Add serializer for attachment.
                                 qnameAttachment,
                                 JAFDataHandlerSerializerFactory.class,
                                 JAFDataHandlerDeserializerFactory.class);

        call.addParameter("source", qnameAttachment, ParameterMode.IN); //Add the file.

        call.setReturnType(org.apache.axis.Constants.XSD_STRING);

        if (doTheDIME) {
            call.setProperty(call.ATTACHMENT_ENCAPSULATION_FORMAT,
                             call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);

        }
        Object result = call.invoke(new Object[] {dhSource});

        return (String) result;

    }

    /**
     * This method sends a file as an attachment then
     * receives a boolean value as a return.
     *
     * @param The filename that is the source to send.
     * @return True if sent successfully.
     */
    public String submitFile(String filename) throws Exception {
        boolean doTheDIME = false;
        //javax.activation.MimetypesFileTypeMap map= (javax.activation.MimetypesFileTypeMap)javax.activation.MimetypesFileTypeMap.getDefaultFileTypeMap();
        //map.addMimeTypes("application/x-org-apache-axis-wsdd wsdd");


        //Create the data for the attached file.
        DataHandler dhSource = new DataHandler(new FileDataSource(filename));

        Service service = new Service();

        Call call = (Call) service.createCall();

        call.setTargetEndpointAddress(new URL(serverURL)); //Set the target service host and service location,

        call.setOperationName(new QName("urn:SequenceAlignmentService",
                                        "upload")); //This is the target services method to invoke.

        QName qnameAttachment = new QName("urn:SequenceAlignmentService",
                                          "DataHandler");
        QName qnamereturntype = new QName("urn:SequenceAlignmentService",
                                          "String");

        call.registerTypeMapping(dhSource.getClass(), //Add serializer for attachment.
                                 qnameAttachment,
                                 JAFDataHandlerSerializerFactory.class,
                                 JAFDataHandlerDeserializerFactory.class);

        call.addParameter("source", qnameAttachment, ParameterMode.IN); //Add the file.

        //call.setReturnType(qnameAttachment);
        //call.setReturnType(qnamereturntype);
        call.setReturnType(org.apache.axis.Constants.XSD_STRING);
        //  call.setUsername(opts.getUser());

        // call.setPassword(opts.getPassword());

        if (doTheDIME) {
            call.setProperty(call.ATTACHMENT_ENCAPSULATION_FORMAT,
                             call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);

        }
        Object result = call.invoke(new Object[] {dhSource});

        return (String) result;

    }

    /**
     * SoapClient
     *
     * @param aString String
     */
    public SoapClient() {
        serverURL = System.getProperties().getProperty(
                "sequence.server.endpoint");

        if (serverURL == null) {
            serverURL = STRINGURL;
        }

    }


    public String getSession(String s) {
        try {
            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService",
                                            "getSession")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam2",
                              org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);

            Object result = call.invoke(new Object[] {s, s});
            System.out.println("Session: the array result is " +
                               result.toString());
            return result.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    /**
     * checkJobStatus
     *
     * @param jobNumber String
     * @return boolean
     */
    public boolean isJobFinished(String jobNumber) {

        try {

            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService",
                                            "isFinished")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING,
                              javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_BOOLEAN);
            Object result = call.invoke(new Object[] {getDesFileName(jobNumber)});

            return new Boolean(result.toString()).booleanValue();
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public String getOutputfile() {
        return new File(outputfile).getAbsolutePath();
    }

    public CSSequenceSet getSequenceDB() {
        return sequenceDB;
    }

    public String getUsername() {
        return username;
    }

    /**
     * getInputFileName
     *
     * @param inputFileString String
     * @return String
     */
    public String getInputFileName() {
        return null;
    }


    /**
     * getBlastServerInfo
     *
     * @return String
     */
    public String getBlastServerInfo() {

        return getServerInfo("pb status");
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getCmd() {
        return cmd;
    }


    public boolean startRun(boolean enableHTML) throws Exception {
        String uploadedFile = "";
        if (sequenceDB != null) {
            uploadedFile = submitSequenceDB(sequenceDB);
        }
        try {
            //Below code should be moved into AlgorithmMatcher.
            if (enableHTML) {
                if (!cmd.matches(" -T T")) {
                    //make sure the result is in HTML format.
                    cmd += " -T T ";
                }

            }

            if (submitJob(cmd, uploadedFile, outputfile) == null) {
                //fail to submit job.
                return false;
            }

        } catch (Exception e) {

        } while (!isJobFinished(getFileName(outputfile))) {
            try {
                Thread.sleep(TIMEGAP);
            } catch (InterruptedException ie) {
                //ie.printStackTrace();
                System.out.println("In SoapClient" + ie);
            }

        }

        return getFile(getFileName(outputfile));

    }

    /**
     * For start other algorithms.
     *
     * @param program String
     * @param dbName  String
     * @param input   String
     */
    public void startRun() throws Exception {

        String uploadedFile = submitSequenceDB(sequenceDB); ;
        try {

            System.out.println(cmd + "  before submitJob " + uploadedFile +
                               " output" + outputfile);
            submitJob(cmd, uploadedFile, outputfile);

        } catch (Exception e) {

            e.printStackTrace();

        } while (!isJobFinished(getFileName(outputfile))) {
            System.out.println("wait for finish " + outputfile);
            try {
                Thread.sleep(TIMEGAP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }

        getFile(getFileName(outputfile));

    }


    /**
     * SoapClient different constructors
     *
     * @param cmd1 String
     */
    public SoapClient(String program, String dbName) {
        this();

        cmd = "pb blastall -p " + program + "   -d   " + dbName;

        outputfile = DEFAULT_OUTPUTFILE;
    }

    public SoapClient(String program, String dbName,
                      String output) {
        this();
        cmd = "pb blastall -p " + program + "   -d   " + dbName;

        outputfile = output;
    }

    public SoapClient(String program, String dbName, String matrix,
                      String input, String output) {
        this();
        if (program.startsWith("sw")) {
            cmd = "btk search  " + program + "   dbset=" + dbName + " matrix=" +
                  matrix;
        } else if (program.startsWith("hmm")) {
            cmd = "btk " + program + " ";
        }

        outputfile = output;
    }

    public String getFileName(String path) {
        StringTokenizer st = new StringTokenizer(path, "/");
        if (st.countTokens() <= 1) {
            st = new StringTokenizer(path, "\\");
            if (st.countTokens() <= 1) {
                return path;
            }
            int k = st.countTokens();
            for (int i = 0; i < k - 1; i++) {
                st.nextToken();
            }

            String s = st.nextToken();
            System.out.println(s + " " + path);
            return s;

        }
        int k = st.countTokens();
        for (int i = 0; i < k - 1; i++) {
            st.nextToken();
        }

        String s = st.nextToken();

        return s;
    }

    public String getDesFileName(String path) {
        return "outputFolder/" + getFileName(path);
    }

    public String getLocalFileName(String path) {
        String tempFolder = System.getProperties().getProperty(
                "temporary.files.directory");
        if (tempFolder == null) {
            tempFolder = "./";

        }
        return tempFolder + getFileName(path);
    }

    public void setSequenceDB(CSSequenceSet sequenceDB) {
        this.sequenceDB = sequenceDB;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBlastServerInfo(String blastServerInfo) {
        this.blastServerInfo = blastServerInfo;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public void setOutputfile(String outputfile) {
        this.outputfile = outputfile;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public void setCmd(String cmd) {
        if (cmd != null) {
            this.cmd = cmd;
        }
    }


}
