package org.geworkbench.util.pathwaydecoder.mutualinformation;

import org.geworkbench.util.pathwaydecoder.bind.myio;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Ta-tsen Soong
 * @version 1.0
 */

public class EvdAdjacencyMatrix extends AdjacencyMatrix {

    static public boolean MIlibraryLoaded = false;
    static final public int HIGH = 1;
    static final public int LOW = 2;
    static final public int BOTH = 3;
    protected HashMap geneRows = new HashMap();
    protected HashMap geneInteractionRows = new HashMap();
    protected HashMap idToGeneMapper = new HashMap();
    protected HashMap snToGeneMapper = new HashMap();

    protected Parameter parms = null;
    protected int[] histogram = new int[1024];
    protected DSMicroarraySet<DSMicroarray> maSet = null;

    private ArrayList geneA = new ArrayList();
    private ArrayList interactionType = new ArrayList();
    private ArrayList interactionMIValue = new ArrayList();
    private ArrayList geneB = new ArrayList();
    private ArrayList uniqInteractionType = new ArrayList();

    private Object[] filter;

    private boolean bMI = false;

    private String adjName;

    private String interactionText = "";

    private int adjSource = 0;
    static public int fromGeneNetworkPanelNotTakenCareOf = 1;
    static public int fromGeneNetworkPanelTakenGoodCareOf = 2;
    static public int fromBindPanel = 3;

    static protected final double edgeScale = 1024.0 / 0.15;

    HashMap keyMapping = new HashMap();
    String[] keyMapArray = new String[7000];

    public EvdAdjacencyMatrix() {
        super();
    }

    /**
     * this changes the interaction (edge) strength between geneA and geneB
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param mi      double
     */
    public void changeInteractionType2Strength(int geneId1, int geneId2, double mi) {
        for (int i = 0; i < this.geneA.size(); i++) {
            // go through the list of genes and test if the gene pair exists
            if (((Integer) (this.geneA.get(i))).intValue() == geneId1 && ((Integer) (this.geneB.get(i))).intValue() == geneId2) {
                // the gene pair (geneId1, geneId2) is found...

                // changing both the interaction label and the numerical edge strength
                // the interactionMIValue keeps track of the mi values
                // so that "thresholding" could be done (currently for mi >? 0)
                this.interactionMIValue.set(i, new Double(mi));

                // the next line is removed because it should always be "pp"
                // for all of the mi edges...
                // so it does not show numbers when its strength is changed
                this.interactionType.set(i, myio.decimal(mi));

                //System.out.println("mi changed between "+ this.getMarkerName(geneId1) +
                //                   " and "+ this.getMarkerName(geneId2)+ " to "+ mi);
                return;
                //return ((Double)this.interactionMIValue.get(i)).doubleValue();
            }
        }
        // System.out.println("!!!mi not found between "+ this.getMarkerName(geneId1) +
        //                   " and "+ this.getMarkerName(geneId2));
        this.addInteractionType2(geneId1, geneId2, mi);
    }

    public void dumpInteraction() {
        System.out.println(this.geneInteractionRows);
    }

    /**
     * get the interaction partners of g1 (serial) <BR>
     * returns an arralylist of genes (serial)
     *
     * @param g1 int
     * @return ArrayList
     */
    public ArrayList getInteractionInvolvedIn(int g1) {
        int tmpgeneA = -1; // serial of gene A
        int tmpgeneB = -1; // serial of gene B
        String tmpaction;
        ArrayList partner = new ArrayList();

        for (int i = 0; i < geneA.size(); i++) {
            tmpgeneA = ((Integer) geneA.get(i)).intValue();
            // if (tmpgeneA< 0) continue;
            tmpgeneB = ((Integer) geneB.get(i)).intValue();
            // if (tmpgeneB< 0) continue;

            if (tmpgeneA == tmpgeneB) {
                // don't want self loop here
                continue;
            }

            tmpaction = interactionType.get(i).toString();

            // System.out.println("#"+ i + " -> "+ tmpgeneA+ " "+ tmpaction+ " "+ tmpgeneB );
            if (tmpgeneA == g1) {
                // check for redundancy
                if (partner.contains(new Integer(tmpgeneB))) {
                    continue;
                }
                // System.out.println("adding gene B "+ tmpgeneB);
                partner.add(new Integer(tmpgeneB));
                continue;
            }
            if (tmpgeneB == g1) {
                if (partner.contains(new Integer(tmpgeneA))) {
                    continue;
                }
                // System.out.println("adding gene A "+ tmpgeneA);
                partner.add(new Integer(tmpgeneA));
                continue;
            }
            // gene g1 is involved
            // currently only returns the interaction partner

        }
        return partner; //.toArray();
    }

    /**
     * returns the interaction strength (MI) at position pos<BR>
     * it returns 99999 in the case it is null <BR>
     *
     * @param geneId1 int
     * @param geneId2 int
     * @return double
     */
    public double getInteractionType2Strength(int pos) {
        //if (this.interactionMIValue.get(pos)== null)
        //    return 99999;
        return ((Double) this.interactionMIValue.get(pos)).doubleValue();
    }

    /**
     * returns the interaction strength (MI) between geneA and geneB<BR>
     * if there is no interaction, return -99999.00
     * otherwise returns the MI interaction
     *
     * @param geneId1 int
     * @param geneId2 int
     * @return double
     */
    public double getInteractionType2Strength(int geneId1, int geneId2) {
        for (int i = 0; i < this.geneA.size(); i++) {
            if (((Integer) (this.geneA.get(i))).intValue() == geneId1 && ((Integer) (this.geneB.get(i))).intValue() == geneId2) {
                // the gene pair found...
                /*if (this.interactionMIValue.get(i)== null){
                    return 99998;
                                 }*/
                return ((Double) this.interactionMIValue.get(i)).doubleValue();
            }
        }
        return -99999.00;
    }

    /**
     * returns all of the MI edges
     *
     * @return ArrayList
     */
    public ArrayList getAllMIStrengths() {
        return this.interactionMIValue;
    }

    /**
     * type 2 interaction refers to this experimental data type
     * which stores the data in array lists
     *
     * @param geneId1     int (serial)
     * @param geneId2     int (serial)
     * @param interaction String (action)
     *                    <i>p.s. need to modify later for HubSize</I>
     */
    public void addInteractionType2(int geneId1, int geneId2, String interaction) {
        float edge = 1; // this is dummy
        int bin = Math.min(1023, (int) (edge * edgeScale));
        if (bin >= 0) {
            histogram[bin]++;
        }

        // currently we add 999 as the mi strength for interations found by GeneWays
        if (!myio.isDouble(interaction) && !interaction.equals("pp")) {
            // if the interaction was added by GeneWays alone, make its MI strength 999
            // however, if the interaction equals "pp", it means that it comes from MI
            // with the pseudo interaction "pp" added through addInteractionType2(int, int, double)
            this.interactionMIValue.add(new Double(999));
        }

        this.geneA.add(new Integer(geneId1));
        this.interactionType.add(interaction);
        this.geneB.add(new Integer(geneId2));

        //  System.out.println("addInteractionType2: "+ geneId1+ " "+ interaction+ " "+ geneId2);
        //  System.out.println(interaction+ " -> "+ this.uniqInteractionType);

        boolean bMIinteraction = false;
        try {
            // is it a number???
            double val = Double.parseDouble(interaction);
            bMIinteraction = true; // it's a temporary one -> meaning this is an MI edge
        } catch (NumberFormatException nx) {
            bMIinteraction = false;
        }

        if (!this.uniqInteractionType.contains(interaction) & !bMIinteraction) {
            // add interaction to unique interaction type if it's not on the list and not a MI interaction
            this.uniqInteractionType.add(interaction);
        }
    }

    /**
     * true = mutual information is used to construct the network
     *
     * @param flg boolean
     */
    public void setMIflag(boolean flg) {
        this.bMI = flg;
    }

    public boolean getMIflag() {
        return this.bMI;
    }

    public ArrayList getUniqueInteractionType() {
        return this.uniqInteractionType;
    }

    public int getNumType2Interaction() {
        return this.geneA.size();
    }

    /**
     * returns the idx-th geneA
     *
     * @param idx int
     * @return int
     */
    public int getInteractionType2GeneAID(int idx) {
        return ((Integer) (this.geneA.get(idx))).intValue();
    }

    public int getInteractionType2GeneBID(int idx) {
        return ((Integer) (this.geneB.get(idx))).intValue();
    }

    public String getInteractionType2Action(int idx) {
        return this.interactionType.get(idx).toString();
    }

    /**
     * @deprecated replaced by addInteractionType2()
     */
    public void addInteraction(int geneId1, int geneId2, String interaction) {
        float edge = 1; // this is dummy
        int bin = Math.min(1023, (int) (edge * edgeScale));
        if (bin >= 0) {
            histogram[bin]++;
        }

        // adding the neighbor and edge for geneId1
        // gene1 -> (gene2, edge)
        // generows are changed into geneinteraction rows

        HashMap row = (HashMap) this.geneInteractionRows.get(new Integer(geneId1));
        if (row == null) {
            row = new HashMap();
            geneInteractionRows.put(new Integer(geneId1), row);
        }
        row.put(new Integer(geneId2), interaction);

        // doing it both ways; [gene2 -> (gene1, edge)]
        // temporarily deleted to make sure if this is the cause of the double edge effect
        /*
         row = (HashMap) geneInteractionRows.get(new Integer(geneId2));
                     if (row == null) {
            row = new HashMap();
            geneInteractionRows.put(new Integer(geneId2), row);
                     }
                     row.put(new Integer(geneId1), interaction);
         */
    }

    public String getInteractionText() {
        String geneA = "";
        String geneB = "";
        String interaction = "";
        boolean passedFilter = false;
        StringBuffer output = new StringBuffer();

        for (int i = 0; i < this.getNumType2Interaction(); i++) {
            passedFilter = false;

            interaction = this.getInteractionType2Action(i);
            //System.out.println("interaction= "+ interaction);


            if (this.filter == null || this.filter.length == 0) {
                passedFilter = true;
            } else if (interaction.equals("pp") || this.filter[0].equals("ALL")) {
                passedFilter = true;
            } else {
                // if this is an mi interaction, display it
                if (myio.isDouble(interaction)) {
                    passedFilter = true;
                } else {
                    for (int j = 0; j < this.filter.length; j++) {
                        //System.out.println("filter["+ j+ "] "+  filter[j]);
                        if (this.filter[j].equals(interaction)) {
                            passedFilter = true;
                            //System.out.println("match!");
                            break;
                        }
                    }
                }
            }
            // the interaction passing the test
            if (passedFilter == false) {
                continue;
            }
            output.append("Interaction #" + i + ": " + this.getMarkerNameGW(this.getInteractionType2GeneAID(i)) + " (" + this.getInteractionType2GeneAID(i) + ") " + " " + interaction + " " + this.getMarkerNameGW(this.getInteractionType2GeneBID(i)) + " (" + //this.getInteractionType2GeneAID(i)+, this.getInteractionType2GeneBID(i)
                    this.getInteractionType2GeneBID(i) + ") " + "\n");
        }
        return output.toString();
    }

    /**
     * the filter to be applied
     *
     * @param filter Object[]
     */
    public void setFilter(Object[] filter) {
        this.filter = filter;
    }

    /**
     * this returns a trimmed/filtered adj mtx for the GW-interaction adj mtx
     *
     * @return AdjacencyMatrix
     */
    public EvdAdjacencyMatrix filterAndOutputNewAdjMtx() {
        EvdAdjacencyMatrix adj = new EvdAdjacencyMatrix();
        int gene1, gene2;

        boolean passedFilter = false;
        String actiontype;
        for (int i = 0; i < this.geneA.size(); i++) {
            actiontype = this.getInteractionType2Action(i);
            passedFilter = false;
            if (this.filter == null || this.filter.length == 0) {
                passedFilter = true;
            } else if (actiontype.equals("pp") || this.filter[0].equals("ALL")) {
                passedFilter = true;
            } else {
                // if it is an mi interaction, display it
                if (myio.isDouble(actiontype)) {
                    passedFilter = true;
                } else {
                    for (int j = 0; j < this.filter.length; j++) {
                        if (this.filter[j].equals(actiontype)) {
                            passedFilter = true;
                            break;
                        }
                    }
                }
            }
            if (passedFilter == false) {
                continue;
            }

            // action type passed the filter
            gene1 = ((Integer) geneA.get(i)).intValue();
            gene2 = ((Integer) geneB.get(i)).intValue();
            adj.addGeneRow(gene1);
            adj.addGeneRow(gene2);
            adj.addInteractionType2(gene1, gene2, actiontype);
        }
        adj.setFilter(this.filter);
        return adj;
    }

    public void addFilter(Object[] tmpfilter) {
        if (tmpfilter == null) {
            return;
        }
        ArrayList al = myio.obj2arraylist(this.filter);
        // System.out.println("current filter: "+ al);
        for (int i = 0; i < tmpfilter.length; i++) {
            if (al.contains(tmpfilter[i]) == false) {
                al.add(tmpfilter[i]);
            }
        }
        this.filter = al.toArray();
        // System.out.println("new filter: "+ al);
    }

    public Object[] getFilter() {
        return this.filter;
    }

    /**
     * @param text String
     * @deprecated --- maybe not used anymore...
     *             this should be modified to show something like:
     *             the list of interacting genes???
     *             <p/>
     *             maybe just generate the interaction list on the fly???
     */
    public void setInteractionText(String text) {
        this.interactionText = text;
    }

    /**
     * returns all [geneA, interactionType, geneB] in this adjmtx
     *
     * @return ArrayList
     */
    public ArrayList getAllInteractionType2() {
        ArrayList al = new ArrayList();
        al.add(this.geneA);
        al.add(this.interactionType);
        al.add(this.geneB);

        /*
                 System.out.println("getAllInteractionType2");
                 System.out.println("geneA: "+ this.geneA);
                 System.out.println("interaction: "+ this.interactionType);
                 System.out.println("geneB: "+ this.geneB);
         */
        return al;
    }

    public void cleanGWInteraction() {
        this.geneA.clear();
        this.geneB.clear();
        this.interactionMIValue.clear();
        this.interactionType.clear();
    }

    /**
     * add the gene pair along with the Mutual Information
     *
     * @param geneId1 int (serial number)
     * @param geneId2 int (serial number)
     * @param mi      float
     */
    public void addInteractionType2(int geneId1, int geneId2, double mi) {
        this.interactionMIValue.add(new Double(mi));
        // this.addInteractionType2(geneId1, geneId2, myio.decimal(mi));
        // it might not be protein-protein interaction, but we just
        // add pp to differentiate it from the interactions found by GeneWays
        this.addInteractionType2(geneId1, geneId2, "pp");
    }

    /**
     * this adds information from adj to this adj mtx <BR>
     * currently the MI edges are added as STRINGS <BR>
     *
     * @param adj AdjacencyMatrix
     */
    public void addAdjMatrix(EvdAdjacencyMatrix adj) {
        // add edges (float)
        // add interactions
        // add
        ArrayList al = adj.getAllInteractionType2();

        // determine if this is from a MI network
        // if so, then add the numeric edge strengths...
        this.setMIflag(true);

        //if (adj.getAllMIStrengths().size()== 0 /*adj.getMIflag() == true*/) {
        // meaning that this adj mtx comes from Aracne
        // so we adds all MI's to the GW data structure
        // here are some problems to be fixed
        //   System.out.println("adding an MI network");
        // System.out.println("the mi values are: \n"+ adj.getAllMIStrengths());
        //   this.interactionMIValue.addAll(adj.getAllMIStrengths());
        // System.out.println("the interactionType list is "+ (ArrayList) al.get(1));
        //}
        /*else {
            // here the adj mtx comes from GeneWays
            // add a lot of 999s to this GW adj
            for (int i = 0; i < ( (ArrayList) al.get(0)).size(); i++) {
                this.interactionMIValue.add(new Double(999));
            }
            // System.out.println("adding null's for nonMI network -> total: "+
            //                    this.interactionMIValue.size()+ " null(double) interactions");
               }*/

        System.out.println("adding an AdjacencyMatrix");
        this.interactionMIValue.addAll(adj.getAllMIStrengths());

        this.geneA.addAll((ArrayList) al.get(0));
        this.interactionType.addAll((ArrayList) al.get(1));
        this.geneB.addAll((ArrayList) al.get(2));

        this.geneRows.putAll(adj.getGeneRows());
        // this.setFilter(new Object []{});
        // System.out.println(adj.getGeneRows());

        /*
              System.out.println("geneA: "+ this.geneA);
              System.out.println("interaction: "+ this.interactionType);
              System.out.println("geneB: "+ this.geneB);
         */
    }

    /**
     * cleanFirstNeighbors
     *
     * @param gm0 IGenericMarker
     */
    public void cleanFirstNeighbors(DSMicroarraySet microarraySet, DSGeneMarker gm0) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        HashSet neighbors = new HashSet();
        HashSet completed = new HashSet();
        HashSet allGenes = new HashSet();
        int uniId = gm0.getUnigene().getUnigeneId();
        Set rowIds = geneRows.keySet();
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            int uniId1 = maSet.getMarkers().get(geneAId).getUnigene().getUnigeneId();
            if (uniId1 == uniId) {
                neighbors.add(new Integer(geneAId));
            }
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float mi = this.get(geneAId, geneBId);
                if (mi < 0) {
                    // remove the edge between geneA and geneB
                    this.add(geneAId, geneBId, 0);
                    // GW compatible
                    this.changeInteractionType2Strength(geneAId, geneBId, 0);
                }
            }
        }
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            if (!neighbors.contains(geneAKey)) {
                allGenes.add(new Integer(geneAId));
            }
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float mi = this.get(geneAId, geneBId);
                // remove the edge between geneA and geneB
                this.add(geneAId, geneBId, -Math.abs(mi));
                // GW compatible
                this.changeInteractionType2Strength(geneAId, geneBId, -Math.abs(mi));
            }
        }
        ArrayList neighborSet = new ArrayList();
        int neighborId = 0;
        neighborSet.add(neighborId, neighbors);
        boolean decrease = true;
        while (!allGenes.isEmpty() && decrease) {
            // find the best connection to any of the genes in the current selection.
            decrease = false;
            HashSet newNeighbors = new HashSet();
            for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
                Integer geneAKey = (Integer) geneA.next();
                if (allGenes.contains(geneAKey)) {
                    int geneAId = geneAKey.intValue();
                    DSGeneMarker gmA = maSet.getMarkers().get(geneAId);
                    if (!completed.contains(new Integer(gmA.getUnigene().getUnigeneId()))) {
                        HashMap geneRow = (HashMap) geneRows.get(geneAKey);
                        float maxMI = 0;
                        int maxId = -1;
                        Set colIds = geneRow.keySet();
                        for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                            Integer geneBKey = (Integer) geneB.next();
                            int geneBId = geneBKey.intValue();
                            if (neighbors.contains(geneBKey)) {
                                float mi = Math.abs(get(geneAId, geneBId));
                                if (mi > maxMI) {
                                    maxMI = mi;
                                    maxId = geneBId;
                                }
                            }
                        }
                        // what is maxID?????
                        if (maxId != -1) {
                            // Now remove geneA from allgenes and add it to the new neighbors
                            // allGenes.remove(geneAKey);
                            decrease = true;
                            newNeighbors.add(geneAKey);
                            add(geneAId, maxId, maxMI);
                            // GW compatible
                            this.changeInteractionType2Strength(geneAId, maxId, maxMI);
                            completed.add(new Integer(maSet.getMarkers().get(geneAId).getUnigene().getUnigeneId()));
                        }
                    }
                }
            }
            allGenes.removeAll(newNeighbors);
            neighborSet.add(newNeighbors);
            neighbors = newNeighbors;
        }
    }

    /**
     * the clean() function called from GeneNetworkPanel -> CreateNetwork()
     * this function is GW compatible
     *
     * @param microarraySet DSMicroarraySet
     * @param threshold     double
     * @param eps           double
     */
    public void clean(DSMicroarraySet microarraySet, double threshold, double eps) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        // ArrayList edges = new ArrayList();
        Set rowIds = geneRows.keySet();

        // First we reinstate all the edges that have a negative MI
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB < 0) {
                    // make the edge positive???
                    // added a new method to change the MI strength for GW interaction
                    // System.out.println("Negative miAB found: " + geneAId + " + " + geneBId);

                    this.add(geneAId, geneBId, -miAB);

                    // hey, this line is necessary for Geneways!!
                    // basically, if we change miAB into -miAB
                    // then we register the same change in the GeneWays data structure
                    this.changeInteractionType2Strength(geneAId, geneBId, -miAB);
                    //                    this.add(geneAId, geneBId, -1.0f);
                }
                if (Math.abs(miAB) < threshold) {
                    // make the edge negative???
                    this.add(geneAId, geneBId, -Math.abs(miAB));

                    // hey, this line is necessary for Geneways !!
                    this.changeInteractionType2Strength(geneAId, geneBId, -Math.abs(miAB));
                    //                    this.add(geneAId, geneBId, -1.0f);
                }
            }
        }

        // Now we remove edges that satisfy the data processing inequality with tolerance eps
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB > 0) {
                    // Check if a shortcut exists to get to B from A
                    boolean foundAB = false;
                    boolean foundBA = false;
                    for (Iterator geneC = colIds.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            //                            if ( (miAB * (1 + eps) < miAC) &&
                            //                                (miAB * (1 + eps) < miBC)) {
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundAB = true;
                                break;
                                //this.add(geneAId, geneBId, -miAB);
                            }
                        }
                    }
                    // Check if a shortcut exists to get to A from B
                    HashMap geneRowB = (HashMap) geneRows.get(geneBKey);
                    Set colIdsB = geneRowB.keySet();
                    for (Iterator geneC = colIdsB.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            //                            if ( (miAB * (1 + eps) < miAC) &&
                            //                                (miAB * (1 + eps) < miBC)) {
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                //this.add(geneAId, geneBId, -miAB);
                                foundBA = true;
                                break;
                            }
                        }
                    }
                    if (foundAB || foundBA) {
                        // add the new edge here???
                        // maybe just make the edge negative???
                        this.add(geneAId, geneBId, -miAB);
                        // hey, this line is necessary for Geneways!!
                        this.changeInteractionType2Strength(geneAId, geneBId, -miAB);
                    }
                }
            }
        }
    }

}
