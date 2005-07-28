package org.geworkbench.util.session;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.apache.axis.utils.Options;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

public class SoapClient {

    //default username;
    private String username = "amdecweb";
    private String serverURL;
    private String cmd;
    private String inputfile;
    private static long TIMEGAP = 120000;

    // private PropertiesMonitor pm = new PropertiesMonitor("config.ini");

    //private String DEFAULT_URL = pm.get("blast_server", "http://adgate.cu-genome.org:9000/glue/urn:cmd.wsdl");

    private final String DEFAULT_OUTPUTFILE = "testout.txt";
    private String outputfile;
    static final String stringURL = "http://adparacel.cu-genome.org/axis/servlet/AxisServlet";
    private String url = stringURL;

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
            call.setOperationName(new QName("urn:downloadfileService", "download")); //This is the target services method to invoke.

            QName qnameAttachment = new QName("urn:downloadfileService", "DataHandler");
            String s = "http://amdec-bioinfo.cu-genome.org/html/index.html";
            call.registerTypeMapping((new DataHandler(new URL(s))).getClass(), //Add serializer for attachment.
                    qnameAttachment, JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class);
            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(qnameAttachment);
            //System.out.println("In download is the result: " + filename);
            Object result = call.invoke(new Object[]{filename.trim()});
            // System.out.println("In download is the result: " + result.toString());
            //s = "c:/axis/downloaded.txt";

            ((DataHandler) result).writeTo(new java.io.FileOutputStream(getLocalFileName(filename)));
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
            call.setOperationName(new QName("urn:downloadfileService", "getServerInfo")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);
            Object result = call.invoke(new Object[]{checkstatus});
            // System.out.println("New In checkJobStatus is the result: " +
            //                  result.toString());
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

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam2", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam3", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);

            //get the  filename only.
            String destinationFile = getDesFileName(outputFile);
            System.out.println(new Date() + "In submit is before invoke: " + destinationFile);
            Object result = call.invoke(new Object[]{cmd, inputFile, destinationFile});
            System.out.println(new Date() + "In submit is the result: " + result.toString());
            return result.toString();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;

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

        call.setOperationName(new QName("urn:SequenceAlignmentService", "upload")); //This is the target services method to invoke.

        QName qnameAttachment = new QName("urn:SequenceAlignmentService", "DataHandler");
        QName qnamereturntype = new QName("urn:SequenceAlignmentService", "String");

        call.registerTypeMapping(dhSource.getClass(), //Add serializer for attachment.
                qnameAttachment, JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class);

        call.addParameter("source", qnameAttachment, ParameterMode.IN); //Add the file.

        //call.setReturnType(qnameAttachment);
        //call.setReturnType(qnamereturntype);
        call.setReturnType(org.apache.axis.Constants.XSD_STRING);
        //  call.setUsername(opts.getUser());

        // call.setPassword(opts.getPassword());

        if (doTheDIME) {
            call.setProperty(call.ATTACHMENT_ENCAPSULATION_FORMAT, call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);

        }
        Object result = call.invoke(new Object[]{dhSource});

        return (String) result;
        // return ((BooleanHolder)result).value;// booleanValue() ;
        /*
                Object ret = call.invoke(new Object[]{
                    dhSource
                }
                ); //Add the attachment.

                if (null == ret) {
                    System.out.println("Received null ");
                    throw new AxisFault("", "Received null", null, null);
                }

                if (ret instanceof String) {
         System.out.println("Received problem response from server: " + ret);
                    throw new AxisFault("", (String) ret, null, null);
                }

                if (!(ret instanceof DataHandler)) {
                    //The wrong type of object that what was expected.
         System.out.println("Received problem response from server:" +
                                       ret.getClass().getLabel());
         throw new AxisFault("", "Received problem response from server:" +
         ret.getClass().getLabel(), null, null);

                }
                //Still here, so far so good.
                //Now lets brute force compare the source attachment
                // to the one we received.
                DataHandler rdh = (DataHandler) ret;

                //From here we'll just treat the data resource as file.
                String receivedfileName = rdh.getLabel();//Get the filename.

                if (receivedfileName == null) {
                    System.err.println("Could not get the file name.");
         throw new AxisFault("", "Could not get the file name.", null, null);
                }


                System.out.println("Going to compare the files.....");
                boolean retv = compareFiles(filename, receivedfileName);

                java.io.File receivedFile = new java.io.File(receivedfileName);

                receivedFile.delete();

                return retv;
         }*/
    }

    /**
     * SoapClient
     *
     * @param aString String
     */
    public SoapClient() {
        serverURL = System.getProperties().getProperty("sequence.server.endpoint");

        if (serverURL == null) {
            serverURL = stringURL;
        }
    }

    /**
     * This method sends all the files in a directory.
     *  @param The directory that is the source to send.
     *  @return True if sent and compared.
     */


    /**
     * Give a single file to send or name a directory
     * to send an array of attachments of the files in
     * that directory.
     */
    /* public static void main(String args[]) {
         try {
             args = new String[] {
                 "c:/axis/protein.fasta"};
             Options opts = new Options(args);
             SoapClient echoattachment = new SoapClient(opts);

             args = opts.getRemainingArgs();
             int argpos = 0;

             if (args == null || args.length == 0) {
                 System.err.println("Need a file or directory argument.");
                 System.exit(8);
             }

             boolean doTheDIME = false;
             if (args[0].trim().equalsIgnoreCase("+FDR")) {
                 doTheDIME = true;
                 ++argpos;
             }

             if (argpos >= args.length) {
                 System.err.println("Need a file or directory argument.");
                 System.exit(8);
             }

             String argFile = args[argpos];

             java.io.File source = new java.io.File(argFile);

             if (!source.exists()) {
     System.err.println("Error \"" + argFile + "\" does not exist!");
                 System.exit(8);
             }

             if (true) {

                 System.out.println("Attachment sent ok!!");

                 String uploadedFile = echoattachment.submitFile(argFile);
                 String outputFile = "outputFolder/Output" +
                     new Random().nextInt();
                 echoattachment.submitJob(
                     "pb blastall -p blastp -d ncbi/pdbaa   ",

                     uploadedFile, outputFile);

                 while (!echoattachment.isJobFinished(outputFile)) {
                     Thread.sleep(TIMEGAP);
                     System.out.println(new Date());
                 }
                 ;
                 //   echoattachment.getServerInfo("pb status");
                 //outputFile = "Output1380658754";
                 // outputFile = "http://amdec-bioinfo.cu-genome.org/html/index.html";
                 echoattachment.getFile(outputFile);

                 System.out.println(outputFile + "end ok!");
             }

         }
         catch (Exception e) {
             System.err.println(e);
             e.printStackTrace();
         }
         System.exit(18);
     }
     */


    public String getSession(String s) {
        try {
            Service service = new Service();

            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new URL(serverURL));
            call.setOperationName(new QName("urn:downloadfileService", "getSession")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("testParam2", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_STRING);

            Object result = call.invoke(new Object[]{s, s});
            System.out.println("Session: the array result is " + result.toString());
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
            call.setOperationName(new QName("urn:downloadfileService", "isFinished")); //This is the target services method to invoke.

            call.addParameter("testParam", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);

            call.setReturnType(org.apache.axis.Constants.XSD_BOOLEAN);
            Object result = call.invoke(new Object[]{getDesFileName(jobNumber)});
            // System.out.println("New In checkJobStatus is the result: " +
            //                    result.toString());
            return new Boolean(result.toString()).booleanValue();
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public String getOutputfile() {
        return new File(outputfile).getAbsolutePath();
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
        SoapClient sc = new SoapClient("blastn", "ncbi/nt", "C:/cvsProject/test.fasta");
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

    public void startRun(boolean enableHTML) throws Exception {

        String uploadedFile = submitFile(inputfile);
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
            //System.out.println(cmd + "  before submitJob " + uploadedFile +
            //                   " output" + outputfile);
            submitJob(cmd, uploadedFile, outputfile);

        } catch (Exception e) {

            //e.printStackTrace();

        }
        while (!isJobFinished(getFileName(outputfile))) {
            try {
                Thread.sleep(TIMEGAP);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }


        }
        //System.out.println("Your query is ended at " + new Date() + outputfile);
        getFile(getFileName(outputfile));

        // System.out.println("Your query is ended at " + new Date());

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

            System.out.println(cmd + "  before submitJob " + uploadedFile + " output" + outputfile);
            submitJob(cmd, uploadedFile, outputfile);

        } catch (Exception e) {

            e.printStackTrace();

        }
        while (!isJobFinished(getFileName(outputfile))) {
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

    public SoapClient(String program, String dbName, String input, String output) {
        this();
        cmd = "pb blastall -p " + program + "   -d   " + dbName;
        inputfile = input;
        outputfile = output;
    }

    public SoapClient(String program, String dbName, String matrix, String input, String output) {
        this();
        if (program.startsWith("sw")) {
            cmd = "btk search  " + program + "   dbset=" + dbName + " matrix=" + matrix;
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
        String tempFolder = System.getProperties().getProperty("temporary.files.directory");
        if (tempFolder == null) {
            tempFolder = "./";

        }
        return tempFolder + getFileName(path);
    }

}
