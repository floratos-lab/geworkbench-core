package org.geworkbench.util.pathwaydecoder.bind;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author: Ta-tsen Soong
 * @version 1.0
 */

import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.EvdAdjacencyMatrix;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class GWgenes {
    private ArrayList serial = new ArrayList();
    private ArrayList locus = new ArrayList();
    private ArrayList genename = new ArrayList();
    private ArrayList genealias = new ArrayList();
    private ArrayList masterOfAlias = new ArrayList(); // corresponding master id of the aliases
    private int cnt = 0;

    private ArrayList geneA = new ArrayList();
    private ArrayList action = new ArrayList();
    private ArrayList geneB = new ArrayList();

    private Connection con;
    private ListModel model;
    private DefaultListModel genemodel = new DefaultListModel();

    private boolean debug = false;

    private ArrayList uniqInteractionType = new ArrayList();

    private int[] interactionPartners;

    private HashMap alias2geneinfo = new HashMap();
    private HashMap alias2serial = new HashMap();
    private HashMap serial2alias = new HashMap();

    private boolean bconnection = false;

    private boolean bAliasFile = false;

    public GWgenes() {

    }

    public boolean aliasFileRead() {
        return this.bAliasFile;
    }

    /**
     * the default way to read the alias file<BR>
     * default path: user.dir/alias1.txt <BR>
     * this is where you should put the alias1.txt file
     */
    public void read() {
        String dir = System.getProperty("user.dir");
        String aliasfile = dir + "//aliases1.txt";
        this.read(new File(aliasfile));
    }

    /**
     * returns a list of serials matching that alias
     */
    public ArrayList getSerialOfAlias(String thealias) {
        if (this.alias2serial.containsKey(thealias)) {
            return (ArrayList) this.alias2serial.get(thealias);
        } else {
            return new ArrayList();
        }
    }

    public ArrayList getAliasofSerial(int serial) {
        if (this.serial2alias.containsKey(new Integer(serial))) {
            return (ArrayList) this.serial2alias.get(new Integer(serial));
        } else {
            return new ArrayList();
        }
    }

    /**
     * read the alias1.txt file for the mapping of alias <-> probe serial number
     *
     * @param fName File
     */
    public void read(File fName) {
        BufferedReader br = null;
        int locusid = -1;
        int serial = -1;
        int pos = -1;
        String thealias = "";
        try {
            br = new BufferedReader(new FileReader(fName));

            String line;
            int ctr = 0;
            while ((line = br.readLine()) != null) {
                /*if (ctr++ % 100 == 0) {
                    System.out.println("reading aliases " + ctr++);
                }*/
                // System.out.println(line);
                if (line.startsWith("#")) continue;

                String[] arrLine2 = line.split("\t");
                locusid = Integer.parseInt(arrLine2[0]);
                pos = Integer.parseInt(arrLine2[1]);
                serial = Integer.parseInt(arrLine2[2]);
                thealias = arrLine2[3].trim();

                String[] arrLineStart = arrLine2[0].split(":");
                String val = new String(arrLineStart[0]);
                Integer num = new Integer(arrLineStart[arrLineStart.length - 1]);

                // add the alias <-> serial pair into the HashMap
                if (this.alias2serial.containsKey(thealias)) {
                    ArrayList serialList = (ArrayList) this.alias2serial.get(thealias);
                    if (serialList.contains(new Integer(serial)) == false) {
                        serialList.add(new Integer(serial));
                    }
                } else {
                    // first encounter with this thealias
                    ArrayList tmpSerialList = new ArrayList();
                    tmpSerialList.add(new Integer(serial));
                    this.alias2serial.put(thealias, tmpSerialList);
                }

                // add the serial -> alias pair
                if (this.serial2alias.containsKey(new Integer(serial))) {
                    ArrayList aliaslist = (ArrayList) this.serial2alias.get(new Integer(serial));
                    if (aliaslist.contains(thealias) == false) {
                        aliaslist.add(thealias);
                    }
                } else {
                    ArrayList aliasal = new ArrayList();
                    aliasal.add(thealias);
                    this.serial2alias.put(new Integer(serial), aliasal);
                }
                /*
                ArrayList al= (ArrayList)alias2geneinfo.get(thealias);
                if (al!= null){

                }else{
                   // alias2geneinfo.put(thealias, )
                }
                */
            }
            br.close();
            this.bAliasFile = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reset() {
        this.serial.clear();
        this.locus.clear();
        this.genename.clear();
        this.genealias.clear();
        this.masterOfAlias.clear();
        this.geneA.clear();
        this.action.clear();
        this.geneB.clear();
        this.cnt = 0;
    }

    public DefaultListModel getGeneModel() {
        return this.genemodel;
    }

    public void setGeneModel(DefaultListModel m) {
        this.genemodel = m;
    }

    public boolean isConnected() {
        return this.bconnection;
    }

    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Oracle driver not found!!");
            return;
        }
        System.out.println("Driver found!!");

        try {
            con = java.sql.DriverManager.getConnection("jdbc:oracle:thin:@156.111.188.210:1521:geneways", "tatsen", "a7r-3");
        } catch (java.sql.SQLException sqe) {
            System.out.println("Oracle connection failed!!");
            this.bconnection = false;
            return;
        }
        this.bconnection = true;
        System.out.println("Oracle connection established!!");
    }

    /**
     * g1, g2 = gene position on the gene list, NOT the serial number
     */
    public void addInteraction(int g1, String interaction, int g2) {
        // System.out.println("adding "+ g1 + " " + action + " "+ g2);
        this.geneA.add(new Integer(g1));
        this.action.add(interaction);
        this.geneB.add(new Integer(g2));

        if (!this.uniqInteractionType.contains(interaction))
            this.uniqInteractionType.add(interaction);

    }

    public void resetUniqueInteraction() {
        this.uniqInteractionType.clear();
    }

    public ArrayList getUniqueInteraction() {
        return this.uniqInteractionType;
    }

    public void setConnection(Connection thecon) {
        this.con = thecon;
    }

    public ArrayList getGeneNameByLocusLinkID(int locuslinkid) {
        ArrayList output = new ArrayList();
        try {
            java.sql.Statement stmt = con.createStatement();

            // selecting all aliases of the gene linked to the LocusLinkID
            String qstring = "select  nm.name from geneways40a.namelink nl, " + "geneways40a.locuslink ll, geneways40a.names nm " + "where accessionnumber= " + locuslinkid + " and nm.nameid= nl.nameid " + "and nl.oid= ll.oid and nl.dbid=3";

            String sql = qstring;
            if (this.debug) System.out.println("SQL: " + sql);

            java.sql.ResultSet rs = stmt.executeQuery(sql);

            int cnt = 0;
            while (rs.next()) {
                output.add(rs.getString(1));
            }
            stmt.close();
        } catch (java.sql.SQLException sqe) {
            System.out.println(sqe.getMessage());
            System.out.println("Oracle query failed!!");
            //         output += "Query failed!!";
        }
        return output;
    }

    /**
     * queries the GeneWays database and returns [output, geneA, actiontype, geneB] <BR>
     * where output= string, <BR>
     * geneA= ArrayList of genes A <BR>
     * actiontype= ArrayList of actions <BR>
     * geneB= ArrayList of genes B <BR><BR>
     * here geneA and geneB are stored in the <i>string(alias)</i> format <BR>
     * we need to furthur use <i>getIndexOfAlias</i> to map to pos or serial<BR>
     */
    public ArrayList getGeneFromGeneways(String genename) {
        ArrayList all = new ArrayList();
        ArrayList geneA = new ArrayList();
        ArrayList actiontype = new ArrayList();
        ArrayList geneB = new ArrayList();

        genename = genename.replaceAll("\'", "\'\'");
        if (this.debug) System.out.println(genename);
        StringBuffer output = new StringBuffer();
        String gene1, action, gene2;
        try {
            java.sql.Statement stmt = con.createStatement();

            String sql = "select up.name, at.name, dn.name " + "from geneways40a.substance up, geneways40a.substance dn, geneways40a.action, " + "geneways40a.action_type at " + "where upstream = up.substanceid and downstream = dn.substanceid and " + " result = at.id and (up.name = '" + genename.toLowerCase() + "' or dn.name='" + genename.toLowerCase() + "')";

            if (this.debug) System.out.println("SQL: " + sql);
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            // java.sql.ResultSet rs = stmt.executeQuery("select * from geneways40a.names where upper(name)='"+
            //                                           genename.toUpperCase()+ "' ");

            int cnt = 0;
            while (rs.next()) {
                gene1 = rs.getString(1);
                action = rs.getString(2);
                gene2 = rs.getString(3);
                // add the relationship to the output buffer
                geneA.add(gene1);
                actiontype.add(action);
                geneB.add(gene2);

                cnt++;
                output.append("#" + cnt + " " + gene1 + " " + action + " " + gene2 + "\n");
            }
            stmt.close();
        } catch (java.sql.SQLException sqe) {
            System.out.println(sqe.getMessage());
            System.out.println("Oracle query failed!!");
            output.append("Query failed!!");
        }
        all.add(output.toString());
        all.add(geneA);
        all.add(actiontype);
        all.add(geneB);

        return all;
    }

    /**
     * this functional call is all that's needed to retrieve <BR>
     * the interactions from GeneWays <BR>
     * <i>... maybe move this to the background someday... </I>
     */
    public void getAllGeneInfo() {
        System.out.println("Total: " + this.getNumGenes() + " genes");
        for (int i = 0; i < this.getNumGenes(); i++) {
            if (i % 10 == 0) System.out.print("\n" + i);
            System.out.print(".");
            this.getGeneInfo(i);
        }
        System.out.println();
    }


    public void getGeneInfo(int pos) {
        if (this.debug) System.out.println("getGeneInfo for pos " + pos);

        // retrieving the gene's short name using the complicated way
        // since the getShortName function is not working...
        DSGeneMarker igm = (DSGeneMarker) model.getElementAt(pos);
        String endl = "\n";
        String tmp = model.getElementAt(pos).toString();

        // get the affy gene name
        int s1 = tmp.indexOf("(") + 1;
        int s2 = tmp.indexOf(")");
        String genename = tmp.substring(s1, s2);

        StringBuffer geneways = new StringBuffer(); // storing the output from geneways
        String tmpname = "";

        ArrayList tmpal;
        ArrayList tmpgeneA;
        ArrayList tmpaction;
        ArrayList tmpgeneB;
        int geneApos = -1; // the position on the gene list
        int geneBpos = -1;
        String theaction;

        StringBuffer finaloutput = new StringBuffer();
        StringBuffer selfLoop = new StringBuffer();

        if (igm.getGeneId() > 0) {
            // retrieve all of the aliases of this gene in GeneWays
            // and store them in the arraylist
            ArrayList gwgenename = this.getGeneNameByLocusLinkID(igm.getGeneId());

            if (this.debug) System.out.println("Using LocusLink: ");
            geneways.append(gwgenename.size() + " aliases exist in GeneWays\n");

            for (int i = 0; i < gwgenename.size(); i++) {
                // outputs the aliases to the text area
                geneways.append("(" + (i + 1) + ") " + gwgenename.get(i).toString() + endl);
            }

            geneways.append("\n------ Query Results ------\n");

            // for every alias, we get the interactions from GeneWays
            // using this.getGeneFromGeneways(the_alias)
            for (int i = 0; i < gwgenename.size(); i++) {
                // a tmp alias for query
                tmpname = gwgenename.get(i).toString();
                geneways.append("Alias #" + (i + 1) + ": " + tmpname + endl);

                // query geneways using this alias (tmpname)
                // the results are stored in an arraylist (tmpal)
                tmpal = this.getGeneFromGeneways(tmpname);

                geneways.append(tmpal.get(0) + endl); // output string
                tmpgeneA = (ArrayList) tmpal.get(1);  // geneA
                tmpaction = (ArrayList) tmpal.get(2); // action type
                tmpgeneB = (ArrayList) tmpal.get(3);  // geneB

                // maybe categorize the action types here...

                // now getting the interaction
                for (int j = 0; j < tmpgeneA.size(); j++) {
                    // get the position of geneA on the list (on the left jlist panel)
                    geneApos = this.getClassOfAlias(tmpgeneA.get(j).toString());
                    if (geneApos < 0) continue;

                    geneBpos = this.getClassOfAlias(tmpgeneB.get(j).toString());
                    if (geneBpos < 0) continue;

                    theaction = tmpaction.get(j).toString(); // this is the interaction found between A and B

                    if (geneApos == geneBpos) {
                        // it's a self loop
                        // output the geneA action geneA cases here...
                        // do we add self loops to the interaction list??? currently yes
                        if (this.debug) System.out.println("attempting to add " + geneApos + " " + theaction + " " + geneBpos);
                        this.addInteraction(geneApos, theaction, geneBpos);
                        selfLoop.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        continue;
                    }

                    if (geneApos == pos) {
                        // here geneA!= geneB and is our target gene on the jlist list
                        // our gene -> action -> geneB
                        if (geneBpos < this.getNumGenes()) {
                            // does not include the case: geneA action geneA
                            // adding this interaction to GWgenes
                            if (this.debug) System.out.println("attempting to add  " + geneApos + " " + theaction + " " + geneBpos);
                            this.addInteraction(geneApos, theaction, geneBpos);
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                        continue;
                    }

                    if (geneBpos == pos) {
                        // here geneB is our target gene on the list
                        // geneA -> action -> our gene
                        if (geneApos < this.getNumGenes()) {
                            // does not include the case: geneB action geneB
                            // adding this interaction to Gwgenes
                            if (this.debug) System.out.println("attempting to add " + geneApos + " " + theaction + " " + geneBpos);
                            this.addInteraction(geneApos, theaction, geneBpos);
                            finaloutput.append("#" + j + " -> " + tmpgeneA.get(j) + " " + tmpaction.get(j) + " " + tmpgeneB.get(j) + endl);
                        }
                    }
                }
            }
        }

        /*
         geneways.append("Using Affy gene name: "+ genename+ endl);
               geneways.append(this.getGeneFromGeneways(genename)+ endl);
         */
        if (this.debug) {
            String info = "Name: " + igm.getLabel() + endl + "UniGene ID: " + igm.getUnigene() + endl + "LocusLinkID " + igm.getGeneId() + endl + "Desc: " + igm.getDescription() + endl + "Short name: " + igm.getShortName() + endl + "Serial: " + igm.getSerial() + endl + tmp + endl + "Gene: " + genename + endl + endl + "GeneWays: \n" + geneways + endl + "------- Interaction List ---------" + endl + finaloutput.toString() + endl + "------- Self Loop ----------" + endl + selfLoop.toString();

            System.out.println(info);
        }
        //  jTextArea1.setText(info);
    }

    /**
     * returns the gene [pos] that has the most interaction partners
     */
    public int getGeneWithMaxPartners() {
        int maxid = -1;
        int max = -999999;
        for (int i = 0; i < this.interactionPartners.length; i++) {
            if (this.interactionPartners[i] >= max) {
                max = this.interactionPartners[i];
                maxid = i;
            }
        }
        System.out.println(this.getGeneNameByIndex(maxid) + " has " + this.interactionPartners[maxid] + " partners");
        return maxid;
    }

    public EvdAdjacencyMatrix getAdjNetwork() {
        return this.getAdjNetwork(null);
    }

    /**
     * remember to perform adj.clean(this.getMicroarraySet(), threshold, eps)
     * after retrieving this adjacency matrix <BR>
     * this also records the number of interaction partners in int [] interactionPartners <BR>
     */
    public EvdAdjacencyMatrix getAdjNetwork(Object[] filter) {
        EvdAdjacencyMatrix adj = new EvdAdjacencyMatrix();
        ArrayList ar = new ArrayList();
        int serial1 = -1, serial2 = -1;
        String tmpaction = "";

        int[] serials = new int[this.getNumGenes()];

        for (int i = 0; i < this.getNumGenes(); i++) {
            serials[i] = this.getSerial(i);
        }
        System.out.println("filter= " + filter);

        this.resetUniqueInteraction();

        boolean pass = false;
        for (int i = 0; i < this.action.size(); i++) {
            // geneA, action, and geneB are supposed to be of equal size
            serial1 = this.getSerial(((Integer) geneA.get(i)).intValue());
            serial2 = this.getSerial(((Integer) geneB.get(i)).intValue());
            tmpaction = this.action.get(i).toString();
            pass = false;
            if (filter == null || filter.length == 1) {
                pass = true;
            } else {
                for (int j = 1; j < filter.length; j++) {
                    if (filter[j].equals(tmpaction)) {
                        pass = true;
                        break;
                    }
                }
            }

            if (pass) {
                // add the interaction to the adjacency matrix
                adj.addGeneRow(serial1);
                adj.addGeneRow(serial2);
                adj.addInteractionType2(serial1, serial2, tmpaction);
                adj.addMarkerName(serial1, this.getGeneNameByIndex(((Integer) geneA.get(i)).intValue()));
                adj.addMarkerName(serial2, this.getGeneNameByIndex(((Integer) geneB.get(i)).intValue()));
                // System.out.println("adding "+ serial1+ " "+ tmpaction + " " + serial2);
            }
        }
        System.out.println("adj.size= " + adj.size());
        return adj;
    }

    /**
     * @deprecated remember to perform adj.clean(this.getMicroarraySet(), threshold, eps)
     *             after retrieving this adjacency matrix <BR>
     *             this also records the number of interaction partners in int [] interactionPartners <BR>
     */
    public AdjacencyMatrix getAdjNetwork_old() {
        System.out.println("Warning: no one should use getAdjNetwork_old()");
        AdjacencyMatrix adj = new AdjacencyMatrix();
        ArrayList ar = new ArrayList();
        int serial1 = -1, serial2 = -1;
        String tmpaction = "";

        this.interactionPartners = new int[this.getNumGenes()];

        // for all genes on the list (position based)
        for (int i = 0; i < this.getNumGenes(); i++) {
            //convert position into serial number
            serial1 = this.getSerial(i);

            adj.addGeneRow(serial1);

            // get all interactions involved in gene(pos=i)
            ar = this.getInteractionInvolvedIn(i, false);
            this.interactionPartners[i] = ar.size();

            for (int j = 0; j < ar.size(); j++) {
                serial2 = this.getSerial(((Integer) ar.get(j)).intValue());

                //this is the interaction type for the gene on the position
                tmpaction = this.action.get(i).toString();

                // System.out.println("adding interaction: "+ serial1 + " <-> "+ serial2);
                //adj.add(serial1, serial2, 2.0f);
                //adj.addGeneRow(serial2);

                // old adj.addInteraction(serial1, serial2, tmpaction);

                // subclass => adj.addInteractionType2(serial1, serial2, tmpaction);
            }
        }
        return adj;
    }

    /**
     * the list model
     */
    public void setModel(ListModel lm) {
        this.model = lm;
    }

    /**
     * get all the aliases of the gene and then store: <BR>
     * geneShorname to GeneName<BR>
     * locusLink to GeneLocusID<BR>
     */
    public void getAllAliases() {
        DSGeneMarker tmpigm;
        int locuslink = -1;
        String tmp;
        int s1, s2;

        // reset the geneways genes
        this.reset();

        for (int i = 0; i < model.getSize(); i++) {
            tmpigm = ((DSGeneMarker) model.getElementAt(i));
            locuslink = tmpigm.getGeneId();

            tmp = model.getElementAt(i).toString();
            s1 = tmp.indexOf("(") + 1;
            s2 = tmp.indexOf(")");

            if (this.debug) System.out.println("Getting aliases for " + locuslink + " " + tmp.substring(s1, s2));
            this.addSerial(tmpigm.getSerial());
            this.addGeneLocusID(locuslink);
            this.addGeneName(tmp.substring(s1, s2)); // add the Affy gene short name
            this.addAliases(this.getGeneNameByLocusLinkID(locuslink));
        }
    }

    public String printInteraction(Object[] ob) {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < ob.length; i++) {
            output.append(((Integer) ob[i]).intValue() + " ");
        }
        return output.toString();
    }

    public String dumpAllInteraction() {
        return dumpAllInteraction(null);
    }

    /**
     * returns the list of interactions in the string format
     *
     * @param filter Object[] refers to the array of interactions we desire to keep
     * @return String
     */
    public String dumpAllInteraction(Object[] filter) {
        StringBuffer output = new StringBuffer();
        ArrayList ar = new ArrayList();
        int gene1 = -1, gene2 = -1;
        String tmpaction = "";

        int[] serials = new int[this.getNumGenes()];

        for (int i = 0; i < this.getNumGenes(); i++) {
            serials[i] = this.getSerial(i);
        }

        boolean pass = false;
        for (int i = 0; i < this.action.size(); i++) {
            // geneA, action, and geneB are supposed to be of equal size
            gene1 = ((Integer) geneA.get(i)).intValue();
            gene2 = ((Integer) geneB.get(i)).intValue();
            tmpaction = this.action.get(i).toString();
            pass = false;
            if (filter == null || filter.length == 1) {
                pass = true;
            } else {
                for (int j = 1; j < filter.length; j++) {
                    if (filter[j].equals(tmpaction)) {
                        pass = true;
                        break;
                    }
                }
            }

            if (pass) {
                // add the interaction to the adjacency matrix
                // adj.addInteractionType2(serial1, serial2, tmpaction);
                output.append("interaction #" + i + ": " + this.getGeneNameByIndex(gene1) + " " + tmpaction + " " + this.getGeneNameByIndex(gene2) + "\n");
            }
        }
        return output.toString();
    }

    public String dumpInteractionInvolvedIn(int g1, boolean selfloop) {
        StringBuffer output = new StringBuffer();
        int tmpgeneA = -1;
        int tmpgeneB = -1;
        String tmpaction;
        ArrayList partner = new ArrayList();

        if (this.debug) System.out.println("getInteractionInvolvedIn()");

        for (int i = 0; i < geneA.size(); i++) {
            tmpgeneA = ((Integer) geneA.get(i)).intValue();
            // if (tmpgeneA< 0) continue;
            tmpgeneB = ((Integer) geneB.get(i)).intValue();
            // if (tmpgeneB< 0) continue;

            if (!selfloop && (tmpgeneA == tmpgeneB)) {
                // don't want self loop here
                continue;
            }

            tmpaction = action.get(i).toString();

            output.append("interaction #" + i + " -> " + this.getGeneNameByIndex(tmpgeneA) + " (" + tmpgeneA + ") " + tmpaction + " " + this.getGeneNameByIndex(tmpgeneB) + " (" + tmpgeneB + ")\n");
            if (tmpgeneA == g1) {
                partner.add(new Integer(tmpgeneB));
                continue;
            }
            if (tmpgeneB == g1) {
                partner.add(new Integer(tmpgeneA));
                continue;
            }
            // gene g1 is involved
            // currently only returns the interaction partner

        }
        return output.toString(); //.toArray();
    }

    public ArrayList getInteractionInvolvedIn(int g1, boolean selfloop) {
        int tmpgeneA = -1;
        int tmpgeneB = -1;
        String tmpaction;
        ArrayList partner = new ArrayList();

        if (this.debug) System.out.println("getInteractionInvolvedIn()");

        for (int i = 0; i < geneA.size(); i++) {
            tmpgeneA = ((Integer) geneA.get(i)).intValue();
            // if (tmpgeneA< 0) continue;
            tmpgeneB = ((Integer) geneB.get(i)).intValue();
            // if (tmpgeneB< 0) continue;

            if (!selfloop && (tmpgeneA == tmpgeneB)) {
                // don't want self loop here
                continue;
            }

            tmpaction = action.get(i).toString();

            if (this.debug) System.out.println("#" + i + " -> " + tmpgeneA + " " + tmpaction + " " + tmpgeneB);
            if (tmpgeneA == g1) {
                // check for redundancy
                if (partner.contains(new Integer(tmpgeneB))) continue;
                partner.add(new Integer(tmpgeneB));
                continue;
            }
            if (tmpgeneB == g1) {
                if (partner.contains(new Integer(tmpgeneA))) continue;
                partner.add(new Integer(tmpgeneA));
                continue;
            }
            // gene g1 is involved
            // currently only returns the interaction partner

        }
        return partner; //.toArray();
    }

    public int getInteractionTargetOf(int g1) {
        System.out.println("getInteractionTargetOf(int g1) not implemented");

        // for (int i=0; i<
        return -999999;
    }

    /**
     * this adds the Affy serial number
     *
     * @param gene_serial int
     */
    public void addSerial(int gene_serial) {
        this.serial.add(new Integer(gene_serial));
    }

    /**
     * returns the serial number of a gene at position <i>pos</i>
     *
     * @param pos int
     * @return int
     */
    public int getSerial(int pos) {
        return ((Integer) this.serial.get(pos)).intValue();
    }

    /**
     * add the locusLinkID
     */
    public void addGeneLocusID(int locusid) {
        this.locus.add(new Integer(locusid));
    }

    /**
     * returns the locuslink id by class (= idx)
     */
    public int getLocusIDbyIndex(int idx) {
        return ((Integer) (this.locus.get(idx))).intValue();
    }

    public void addGeneName(String gname) {
        this.genename.add(gname);
    }

    /**
     * returns the gene name by class (= idx)
     */
    public String getGeneNameByIndex(int idx) {
        return this.genename.get(idx).toString();
    }

    public int getClassOfAlias(String alias) {
        int pos = this.getIndexOfAlias(alias);
        if (pos < 0)
            return -1;
        else
            return ((Integer) this.masterOfAlias.get(pos)).intValue();
    }

    public int getIndexOfAlias(String alias) {
        return this.genealias.indexOf(alias);
    }

    public int getNumGenes() {
        return this.locus.size();
    }

    /**
     * data structure of dealing with aliases
     * [genealias]  [ masterOfAlias] <BR>
     * alias 1  ->    gene 1 <BR>
     * alias 2  ->  gene 1 <BR>
     * alias 3  ->    gene 2 <BR>
     * ... <BR>
     */
    public void addAliases(ArrayList aliases) {
        for (int i = 0; i < aliases.size(); i++) {
            genealias.add(aliases.get(i));
            masterOfAlias.add(new Integer(cnt));
        }
        cnt++;
    }

    /**
     * dump the aliases. The format being: <BR>
     * locusid, pos, serial, alias
     */
    public String dumpAliasesToSave() {
        StringBuffer output = new StringBuffer();
        output.append("There are " + this.genealias.size() + " aliases\n");
        for (int i = 0; i < this.genealias.size(); i++) {
            output.append(this.getLocusIDbyIndex(((Integer) masterOfAlias.get(i)).intValue()) + "\t" + masterOfAlias.get(i) + "\t" + this.getSerial(((Integer) this.masterOfAlias.get(i)).intValue()) + "\t" + this.genealias.get(i) + "\n");
        }
        return output.toString();
    }

    /**
     * dump the aliases
     */
    public void dumpAliases() {
        System.out.println("There are " + this.genealias.size() + " aliases");
        for (int i = 0; i < this.genealias.size(); i++) {
            System.out.println("#" + i + " " + this.getLocusIDbyIndex(((Integer) masterOfAlias.get(i)).intValue()) + " " + masterOfAlias.get(i) + " -> " + this.genealias.get(i));
        }
    }
}
