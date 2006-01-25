package org.geworkbench.util.session;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.utils.Options;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        CSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

public class SoapClient {

    //default username;
    private String username = "amdecweb";
    private String serverURL;
    private String cmd;
    private String inputfile;
    private CSSequenceSet sequenceDB;
    private static long TIMEGAP = 20000;
    private final String DEFAULT_OUTPUTFILE = "testout.txt";
    private String outputfile;
    static final String STRINGURL =
            "http://adparacel.cu-genome.org/axis/servlet/AxisServlet";
    private String url = STRINGURL;

    Options opts = null;

    /**
     * getFile
     *
     * @param filename String
     * @return DataHandler
     */
    public DataHandler getFile(String filename) {

        try {

            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService",
                                            "download")); //This is the target services method to invoke.

            QName qnameAttachment = new QName("urn:downloadfileService",
                                              "DataHandler");
            String s = "http://amdec-bioinfo.cu-genome.org/html/index.html";
            call.registerTypeMapping((new DataHandler(new URL(s))).getClass(), //Add serializer for attachment.
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
            e.printStackTrace();
        }
        return null;

    }

    public SoapClient(Options opts) {
        this();
        this.opts = opts;
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
            System.out.println(e + "at getServerInof");
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
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
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
    public String submitFile(CSSequenceSet sequences) throws Exception {
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

    /**
     * getInputFileName
     *
     * @param inputFileString String
     * @return String
     */
    public String getInputFileName() {
        return inputfile;
    }

    public static void mainOld(String[] args) throws Exception {
        SoapClient sc = new SoapClient("blastn", "ncbi/nt",
                                       "C:/cvsProject/test.fasta");
        if (args.length < 4) {
            System.out.println("Please check the number of your arguments.\n" + "The correct usage:\n java SoapClient UserName \"yourFirstPartOfQuery\" inputFile outputFile");
            //System.exit(1);
        } else {
            sc.username = args[0];

            sc.cmd = args[1];
            sc.inputfile = "C:/cvsProject/" + args[2].trim();
            sc.outputfile = args[3];

        }

        sc.startRun(true);

    }

    /**
     * getBlastServerInfo
     *
     * @return String
     */
    public String getBlastServerInfo() {

        return getServerInfo("pb status");
    }

    public void startRun(boolean enableHTML, CSSequenceSet sequences) {
        //String uploadedFile = submitFile(sequences);
        try {
            String uploadedFile = submitFile(sequences);
            if (enableHTML) {
                if (!cmd.matches(" -T T")) {
                    cmd += " -T T ";
                }

            } else {
                if (cmd.matches(" -T T")) {
                    cmd = cmd.replaceAll(" -T T", " -T F ");
                    //System.out.println("replaced");
                } else if (!cmd.matches(" -T F")) {
                    cmd += "-T F";
                }

                outputfile += ".txt";
            }

            submitJob(cmd, uploadedFile, outputfile);

        } catch (Exception e) {

            //e.printStackTrace();

        } while (!isJobFinished(getFileName(outputfile))) {
            try {
                Thread.sleep(TIMEGAP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }

        getFile(getFileName(outputfile));


    }

    public void startRun(boolean enableHTML) throws Exception {
        String uploadedFile = "";
        if (sequenceDB != null) {
            uploadedFile = submitFile(sequenceDB);
        } else {
            uploadedFile = submitFile(inputfile);
        }
        try {
            if (enableHTML) {
                if (!cmd.matches(" -T T")) {
                    cmd += " -T T ";
                }

            } else {
                if (cmd.matches(" -T T")) {
                    cmd = cmd.replaceAll(" -T T", " -T F ");
                    //System.out.println("replaced");
                } else if (!cmd.matches(" -T F")) {
                    cmd += "-T F";
                }

                outputfile += ".txt";
            }

            submitJob(cmd, uploadedFile, outputfile);

        } catch (Exception e) {

        } while (!isJobFinished(getFileName(outputfile))) {
            try {
                Thread.sleep(TIMEGAP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }

        getFile(getFileName(outputfile));

    }

    /**
     * For start other algorithms.
     *
     * @param program String
     * @param dbName  String
     * @param input   String
     */
    public void startRun() throws Exception {

        String uploadedFile = submitFile(inputfile);
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

        System.out.println("Your query is ended at " + new Date());

    }

    /**
     * getCMD
     *
     * @return String
     */
    public String getCMD() {
        return cmd;
    }

    /**
     * SoapClient different constructors
     *
     * @param cmd1 String
     */
    public SoapClient(String program, String dbName, String input) {
        this();

        cmd = "pb blastall -p " + program + "   -d   " + dbName;
        inputfile = input;
        outputfile = DEFAULT_OUTPUTFILE;
    }

    public SoapClient(String program, String dbName, String input,
                      String output) {
        this();
        cmd = "pb blastall -p " + program + "   -d   " + dbName;
        inputfile = input;
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
        inputfile = input;
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

}
