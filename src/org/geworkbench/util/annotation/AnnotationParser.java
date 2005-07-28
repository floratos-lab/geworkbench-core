package org.geworkbench.util.annotation;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import org.geworkbench.util.RandomNumberGenerator;
import org.geworkbench.util.associationdiscovery.clusterlogic.ExampleFilter;
import org.geworkbench.engine.config.UILauncher;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class AnnotationParser {

    /**
     * <p>Title: Sequence and Pattern Plugin</p>
     * <p>Description:This Class is for retrive Information from affy annotation file and CABIO server </p>
     * <p>This program used CABIO server therefore requires  a security policy file for the vertual machine when runing.
     * eg. "-Djava.security.policy= java.policy" as a virtual machine parameter </p>
     * <p> requires CABIO.jar.</p>
     * <p>download "HG_U95Av2_annot.csv" from affymatrix website to the root folder. or when you create this class,
     * use the file name as the parametor for the constructer.</p>
     * <p>When you first run this program it will be slow because it will parse the datafile and generate
     * some indx data into another file</p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: Califano lab</p>
     *
     * @author Xuegong Wang
     * @version 1.5
     */
    public static final String version = "30";
    // when you change file format etc. this version number need to be changed so that the old file will be deleted.
    static int counter = 0;
    //field names
    public static final int DESCRIPTION = 0; //(full name)
    public static final int ABREV = 1; // title(short name)
    public static final int PATHWAY = 2; // pathway
    public static final int GOTERM = 3; // Goterms
    public static final int UNIGENE = 4; // Unigene
    public static final int LOCUSLINK = 5; // LocusLink
    public static final int SWISSPROT = 6; // swissprot

    public static HashMap affyIDs = new HashMap(); // the genes or the array

    //    static ArrayList cols = new ArrayList(); //the place to store column names about a gene
    static String chipType = ""; //default;
    final static String chiptyemapfilename = "chiptypeMap.txt";
    private static String systempDir = System.getProperty("temporary.files.directory");
    public final static String tmpDir;

    //static String fname = "HG_U95Av2_annot.csv";
    static File annoFile = null;
    static File indx = null;
    static Gotable goes = null;

    public static HashMap chiptypeMap = new HashMap();
    public static HashMap indexfileMap = new HashMap();

    public static String getChipType() {
        return chipType;
    }

    static {
        if (systempDir == null) {
            systempDir = "temp" + File.separator + "GEAW";
        }
        tmpDir = systempDir + File.separator + "annotationParser/";

        File dir = new File(tmpDir);
        if (!dir.exists()) {
            dir.mkdir();

        }

        BufferedReader br = new BufferedReader(new InputStreamReader(AnnotationParser.class.getResourceAsStream(chiptyemapfilename))); //predefined
        try {
            String str = br.readLine();
            while (str != null) {
                String[] data = str.split(",");
                chiptypeMap.put(data[0].trim(), data[1].trim());
                chiptypeMap.put(data[1].trim(), data[0].trim());
                str = br.readLine();
            }
            br.close();
            File temp = new File(tmpDir + chiptyemapfilename);
            if (temp.exists()) {
                BufferedReader br2 = new BufferedReader(new FileReader(temp)); //learned chiptype
                str = br2.readLine();
                while (str != null) {
                    String[] data = str.split(",");
                    indexfileMap.put(data[0].trim(), data[1].trim());
                    str = br2.readLine();
                }
                br2.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        setChipType("HG_U95Av2");

    }

    public static void setChipType(String chiptype) {
        if (chiptypeMap.containsValue(chiptype)) {
            setType(chiptype);
        } else {
            JOptionPane.showMessageDialog(null, "No such format found in database");

        }

    }

    public static void clearAll() throws IOException {

        File temp = new File(tmpDir + chiptyemapfilename);

        for (Iterator it = indexfileMap.entrySet().iterator(); it.hasNext();) {
            String file = (String) it.next();
            File idex = new File(tmpDir + file);
            if (idex.exists()) {
                idex.delete();
            }
            if (file != null) {
                file = file.substring(0, file.indexOf('.'));
            }
            File path = new File(tmpDir + file + ".go");
            if (path.exists()) {
                path.delete();

            }
        }
        if (temp.exists()) {
            temp.delete();
        }
        indexfileMap.clear();

    }

    private static void setType(String chiptype) {
        chipType = chiptype;
        File datafile = new File(chipType + "_annot.csv");

        if (datafile.exists()) { //data file is found
            annoFile = datafile;
            /*try {

                parse();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                                              "<html>Can't find file " +
                                              datafile.getName() +
                                              "<br>Please download " +
                                              datafile.getName() +
                                              " to " + datafile.getAbsolutePath() +
                                              "</html>");
            }*/
        } else { //data file is not found, search temp folder first.


            datafile = new File(tmpDir + chipType + "_annot.csv");

            if (datafile.exists()) { //data file is found
                annoFile = datafile;

            } else {
                try {

                    String ur =

                            "http://amdec-bioinfo.cu-genome.org/html/caWorkBench/data/" + chipType + "_annot.csv";
                    if (UILauncher.splash.isVisible()) {
                        UILauncher.splash.setProgressBarString("Downloading data file...");
                    }
                    ;

                    URL url = new URL(ur);
                    InputStream is = url.openStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    datafile = new File(tmpDir + chipType + "_annot.csv");
                    BufferedWriter bwr = new BufferedWriter(new FileWriter(datafile));
                    String s = br.readLine();
                    while (s != null) {
                        bwr.write(s);
                        bwr.newLine();
                        s = br.readLine();
                    }
                    bwr.close();
                    br.close();
                    setType(chipType);
                } catch (Exception e) {

                    File d = new File(tmpDir);
                    if (!d.exists()) {
                        d.mkdir();
                    }
                    JOptionPane.showMessageDialog(null, "<html>Can't find file " + datafile.getName() + "<br>Please download " + datafile.getName() + " to " + d.getAbsolutePath() + "</html>");
                    return;

                }
            }
        }
        //Get the datafile now.

        if (!chiptype.equals("Other") && !chiptype.equals("Genepix")) {
            try {

                String indexfilename = (String) indexfileMap.get(chiptypeMap.get(chiptype));

                if (indexfilename == null) { //no such file in record
                    indx = createFilewithID();

                } else {
                    indx = new File(tmpDir + indexfilename);
                }
                if (indx.exists() && indx.length() > 1) { //if indx file exist and valid
                    BufferedReader br = new BufferedReader(new FileReader(indx));
                    if (datafile.exists()) { //datafile is found
                        annoFile = datafile;

                        String ver = br.readLine();
                        String lastModified = br.readLine();
                        //                        File data = new File(chipType + "_annot.csv");
                        if ((ver == null) || (!ver.equalsIgnoreCase(version)) || (lastModified == null) || (datafile.lastModified() != Long.parseLong(lastModified))) {
                            br.close();
                            //                            indx.delete();
                            //                            indx = new File(tmpDir + chipType + "IndexFile");
                            parse();

                        } else {
                            br.close();
                            loadIndx();
                        }

                    }

                    createGoAffytable();

                } else {
                    parse();
                    createGoAffytable();
                }

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }

        }
    }

    private static File createFilewithID() {

        String tempString = "annotationParser" + RandomNumberGenerator.getID() + ".idx";
        return new File(tmpDir + tempString);

    }

    private static void loadIndx() throws HeadlessException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(indx));

        affyIDs.clear();
        //skip first two lines
        br.readLine();
        br.readLine();
        String oneline = br.readLine();
        while (oneline != null) {
            String rows[] = oneline.split("\t\t");
            if (rows.length == 2) {
                affyIDs.put(rows[0], rows[1]);
            } else {
                JOptionPane.showMessageDialog(null, "error in line" + oneline);
            }
            oneline = br.readLine();
        }
        br.close();
    }

    static void parse() throws IOException {

        String line;
        if (!UILauncher.splash.isVisible()) {
            JOptionPane.showMessageDialog(null, "<html>Chip type " + chipType + " recognized.<br> We are going to initialize related annotation files<br> </html>");

        } else {
            UILauncher.splash.setProgressBarString("Creating data index file...");
        }
        BufferedWriter br = new BufferedWriter(new FileWriter(indx));
        BufferedReader xin = new BufferedReader(new FileReader(annoFile));
        line = xin.readLine();
        xin.close();
        System.out.println(indx.getAbsolutePath());
        FileInputStream fileIn = new FileInputStream(annoFile);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Creating index file", fileIn);
        InputStreamReader reader = new InputStreamReader(progressIn);
        CSVParse parse = null;
        if (line.startsWith("\"")) {
            parse = new CSVParser(reader);
        } else {
            parse = new ExcelCSVParser(reader);

        }
        LabeledCSVParser parser = new LabeledCSVParser(parse);

        if (UILauncher.splash.isVisible()) {

            progressIn.getProgressMonitor().setMillisToDecideToPopup(10000000);

        }

        int count = 0;
        br.write(version + '\n');
        String time = (annoFile.lastModified()) + "\n";
        br.write(time);
        affyIDs.clear();
        while (parser.getLine() != null) {
            if (UILauncher.splash.isVisible()) {
                UILauncher.splash.setProgressBarString("probes parsed: " + count++);
            }

            String id = parser.getValueByLabel("Probe Set ID");

            String data = executeLine(parser);

            String dataLine = id + "\t\t" + data + '\n';
            affyIDs.put(id, data);
            br.write(dataLine);
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(tmpDir + chiptyemapfilename), true));
        String pair = (String) chiptypeMap.get(chipType);
        if (pair == null) {
            pair = (String) affyIDs.keySet().iterator().next(); //use the first one as identifyer for the chiptype
        }
        indexfileMap.put(pair, indx.getName());
        pair = pair + "," + indx.getName() + '\n';
        bw.write(pair);
        bw.close();

        createNewGoTable();
    }

    /**
     * executeLine
     *
     * @param line String
     */

    private static String executeLine(LabeledCSVParser parser) {
        String delim = "/////";
        StringBuffer data = new StringBuffer();
        String name = parser.getValueByLabel("Title");
        if (name == null) {
            name = parser.getValueByLabel("Gene Title");
        }
        data.append(name);
        String title = parser.getValueByLabel("Gene Symbol");
        if (title == null) {
            title = parser.getValueByLabel("Probe Set ID");

        }
        data.append(delim).append(title);
        String xpath = "";

        String pathGenMAPP = parser.getValueByLabel("Pathways GenMAPP");
        if (pathGenMAPP != null) {

            xpath = "GenMAPP : " + pathGenMAPP.trim() + '\t';
        }

        String pathKEGG = parser.getValueByLabel("Pathways KEGG");
        if (pathKEGG != null) {

            xpath = xpath + "KEGG : " + pathKEGG.trim();
        }
        if (xpath.length() < 1) {
            xpath = " ";

        }
        data.append(delim).append(xpath);

        String x = "";
        String goProc = parser.getValueByLabel("Biological Process (GO)");
        String goComp = parser.getValueByLabel("Cellular Component (GO)");
        String goFunc = parser.getValueByLabel("Molecular Function (GO)");
        if (goProc == null) {
            goProc = parser.getValueByLabel("Gene Ontology Biological Process");
            goComp = parser.getValueByLabel("Gene Ontology Cellular Component");
            goFunc = parser.getValueByLabel("Gene Ontology Molecular Function");

        }

        if (goProc != null) {
            x = parseGo(goProc);
        }
        if (goComp != null) {
            x = x + parseGo(goComp);
        }
        if (goFunc != null) {
            x = x + parseGo(goFunc);
        }
        if (x.length() < 1) {
            x = " ";
        } else {
            //            System.out.println(counter++);
        }
        data.append(delim).append(x);

        String unigene = parser.getValueByLabel("Unigene");
        if (unigene == null) {
            unigene = parser.getValueByLabel("UniGene ID");
        }

        unigene = unigene.split("//")[0].trim();
        if (unigene.trim().equalsIgnoreCase("---")) {
            unigene = " ";
        }
        data = data.append(delim).append(unigene);

        String locus = parser.getValueByLabel("LocusLink");
        if (locus == null) {
            locus = parser.getValueByLabel("Entrez Gene");
        }
        if (locus.equalsIgnoreCase("---")) {
            locus = " ";
        } else {
            locus = locus.replaceAll(" /// ", "\t");
            locus = locus.replaceAll("---\t", "");
        }
        data = data.append(delim).append(locus);

        String protids = parser.getValueByLabel("SwissProt");
        if (protids.equalsIgnoreCase("---")) {
            protids = " ";
        } else {
            protids = protids.replaceAll(" /// ", "\t");
            protids = protids.replaceAll("---\t", "");
        }

        data = data.append(delim).append(protids);

        return data.toString();

    }

    /**
     * getData
     *
     * @param colums String[]
     * @param string String
     * @return String
     */

    //    private static String getData(String[] columns, String fieldName)
    //{
    //        String result = "";
    //        int field = cols.indexOf(fieldName);
    //        if ( (field >= 0) && (field < columns.length)) {
    //            result = columns[field].replaceAll("\"", "");
    //        }
    //        if (result.trim().equalsIgnoreCase("---") || result.equalsIgnoreCase("")) {
    //            result = " ";
    //        }
    //        return result;
    //
    //    }

    static public String getInfoAsString(String affyID, int fieldID) {
        String[] result = getInfo(affyID, fieldID);

        String info = " ";
        if (result == null) {
            return affyID;
        }

        if (result.length > 0) {
            info = result[0];
            for (int i = 1; i < result.length; i++) {
                info += "/" + result[i];
            }
        }

        return info;
    }

    /**
     * This method returns required information in different format.
     * And it can look for information both  local file.
     *
     * @param affyid  affyID as string
     * @param fieldID //defined at FieldName.java
     *                0 : name(full name)
     *                1 : title(short name)
     *                2 : pathway
     *                3 : Goterms
     *                4: unigene
     *                5:LocusLink
     *                6:swissprotids
     * @return 0: String[]
     *         1: String[]
     *         2: String[]  pathway or null
     *         3: string[]  Goterms//tab delimited or null
     * @author Xuegong Wang
     * @version 1.0
     */
    static public String[] getInfo(String affyID, int fieldID) {
        String data = (String) affyIDs.get(affyID);
        if (data != null) {
            String[] info = null;
            //            System.out.println(data);
            //            System.out.println(data.split("/////").length);
            String inf = data.split("/////")[fieldID];

            info = inf.split("\t");
            return info;
        } else {
            return null;
        }
    }

    //used to parse info from raw go data

    private static String parseGo(String godata) {
        String result = "";
        String[] gos = godata.split("///");

        for (int i = 0; i < gos.length; i++) {
            String onego = gos[i];
            String[] gocat = onego.split("//");
            if (gocat.length > 1) {
                int k = Integer.parseInt(gocat[0].trim()) + 10000000;
                //                System.out.println(k);
                gocat[0] = Integer.toString(k).substring(1);
                result = new String(result + "GO:" + gocat[0] + "::" + gocat[1].trim() + "\t");
            }
        }
        return result;
    }

    /**
     * Get AffyIDs related to Goterm
     *
     * @param Unigene
     * @return AffyIDs
     */

    public static String[] fromGoToAffy(String goName) {
        if (goes == null) {
            createGoAffytable();
        }
        if (goes != null) {
            String ids = (String) goes.get(goName);
            if (ids == null) {
                return null;
            } else {
                return ids.split("\t");
            }
        }
        return null;
    }

    public static HashMap getGotable() {
        if (goes == null) {
            createGoAffytable();
        }
        return goes;

    }

    //create go term table that refers go term to affyid
    private static void createGoAffytable() {
        if (chipType != null && !chipType.equalsIgnoreCase("")) {

            String indexfilename = indx.getName();
            if (indexfilename != null) {

                indexfilename = indexfilename.substring(0, indexfilename.indexOf('.'));
            }
            File path = new File(tmpDir + indexfilename + ".go");

            if (path.exists()) {
                try {
                    ObjectInputStream ob = new ObjectInputStream(new FileInputStream(path));
                    goes = (Gotable) ob.readObject();
                    ob.close();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {

                createNewGoTable();

            }

        }
    }

    private static void createNewGoTable() {
        goes = new Gotable();
        ProgressMonitor progressMonitor = new ProgressMonitor(null, "Processing Go Term index file...", "", 0, affyIDs.keySet().size());
        progressMonitor.setProgress(0);

        int count = 0;
        for (Iterator ids = affyIDs.keySet().iterator(); ids.hasNext();) {
            String id = (String) ids.next();
            String[] info = getInfo(id, AnnotationParser.GOTERM);
            progressMonitor.setNote("Processing probe:" + count);
            progressMonitor.setProgress(count++);
            if (UILauncher.splash.isVisible()) {
                UILauncher.splash.setProgressBarString("Processing probe:" + count++);
            }
            for (int i = 0; i < info.length; i++) {
                if (info[i] != null) {
                    String goid = info[i].split("::")[0];
                    //                        String hs = (String) goes.get(info[i]);
                    String hs = (String) goes.get(goid);

                    if (hs == null) {
                        hs = "";
                    }
                    hs = hs + id + "\t";
                    //                        goes.put(info[i], hs);
                    goes.put(goid, hs);
                }
            }
        }
        progressMonitor.close();
        SerilizeGoTermData();
    }

    public static void SerilizeGoTermData() {
        String indexfilename = indx.getName();
        if (indexfilename != null) {

            indexfilename = indexfilename.substring(0, indexfilename.indexOf('.'));
        }
        File path = new File(tmpDir + indexfilename + ".go");
        if (path.exists()) {
            path.delete();
        }
        path = new File(tmpDir + indexfilename + ".go");
        try {
            ObjectOutputStream oj = null;

            oj = new ObjectOutputStream(new FileOutputStream(path));
            oj.writeObject(goes);
            oj.flush();
            oj.close();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
    }

    static class Gotable extends HashMap implements Serializable {
    }

    public static String matchChipType(String id) {
        String chip = (String) chiptypeMap.get(id);
        if ((chip != null) && (!chip.equalsIgnoreCase(chipType))) {
            setChipType(chip);
        }
        if (indexfileMap.get(id) != null) {
            chipType = chip;
            indx = new File(tmpDir + indexfileMap.get(id));
            try {
                loadIndx();
                createGoAffytable();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (HeadlessException ex) {
                ex.printStackTrace();
            }
        }
        return chip;
    }

    public static void callUserDefinedAnnotation() throws IOException {
        JFileChooser chooser = new JFileChooser();
        ExampleFilter filter = new ExampleFilter();
        filter.addExtension("csv");
        filter.setDescription("CSV files");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Please select the annotation file");
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            annoFile = chooser.getSelectedFile();
            chipType = "User Defined";
            indx = createFilewithID();
            parse();
            createGoAffytable();
        } else {
            return;
        }
    }
}
