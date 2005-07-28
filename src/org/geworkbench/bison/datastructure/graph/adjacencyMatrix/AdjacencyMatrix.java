package org.geworkbench.bison.datastructure.graph.adjacencyMatrix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 * @todo - watkin - There are two AdjacencyMatrix classes, this one appears to be redundant and little-used. 
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class AdjacencyMatrix {
    protected HashMap geneRows = new HashMap();

    public AdjacencyMatrix() {
    }

    public void read(String name, float miThresh) {
        int connectionsInstantiated = 0;
        int connectionsIgnored = 0;
        double startTime = 0;
        double endTime = 0;

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(name));
            try {
                String line;
                int ctr = 0;
                while ((line = br.readLine()) != null) {
                    if (ctr++ % 100 == 0) {
                        endTime = System.currentTimeMillis();
                        double totalTime = endTime - startTime;
                        System.out.println("Time for iteration " + (totalTime / 1000.0));
                        System.out.println("Adjacency Matrix Reading line " + ctr);
                        startTime = System.currentTimeMillis();
                        double usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("used memory " + usedMemory);
                    }
                    if (ctr % 500 == 0) {
                        System.gc();
                    }

                    if (line.length() > 0 && line.charAt(0) != '-') {
                        StringTokenizer tr = new StringTokenizer(line, "\t:");

                        String geneAccess = new String(tr.nextToken());
                        String strGeneId1 = new String(tr.nextToken());
                        int geneId1 = Integer.parseInt(strGeneId1);
                        if (geneId1 >= 0) {
                            while (tr.hasMoreTokens()) {
                                String strGeneId2 = new String(tr.nextToken());
                                int geneId2 = Integer.parseInt(strGeneId2);
                                if (geneId2 >= 0) {
                                    String strMi = new String(tr.nextToken());
                                    float mi = Float.parseFloat(strMi);
                                    if (mi > miThresh) {
                                        if (geneId1 != geneId2) {
                                            connectionsInstantiated++;
                                            add(geneId1, geneId2, mi);
                                        } else {
                                            connectionsIgnored++;
                                        }
                                    } else {
                                        connectionsIgnored++;
                                    }
                                }
                            }
                        }

                    }
                    //                    line = br.readLine();
                }
                System.out.println("Connections instantiated " + connectionsInstantiated);
                System.out.println("Connections ignored " + connectionsIgnored);
                System.out.println("Total processed " + (connectionsInstantiated + connectionsIgnored));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex3) {
            ex3.printStackTrace();
        }

    }

    /**
     * Adds and edge between geneId1 and geneId2
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param edge    float
     */
    public void add(int geneId1, int geneId2, float edge) {
        if ((geneId1 >= 0) && (geneId2 >= 0)) {
            // adding the neighbor and edge for geneId1
            // gene1 -> (gene2, edge)
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId1), row);
            }
            row.put(new Integer(geneId2), new Float(edge));

            // doing it both ways; [gene2 -> (gene1, edge)]
            row = (HashMap) geneRows.get(new Integer(geneId2));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId2), row);
            }
            row.put(new Integer(geneId1), new Float(edge));
        }
    }

}
