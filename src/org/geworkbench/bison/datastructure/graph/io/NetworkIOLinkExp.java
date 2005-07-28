package org.geworkbench.bison.datastructure.graph.io;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.graph.CSGeneNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class NetworkIOLinkExp {
    public NetworkIOLinkExp() {
    }

    HashMap<Integer, String> idToMarkerMap = new HashMap<Integer, String>();
    HashMap<String, Integer> markerToIdMap = new HashMap<String, Integer>();

    public CSGeneNetwork readAdjacencyMatrix(File adjFile, DSMicroarraySet maSet, float miThresh) {
        mapIdsToMarkers(adjFile);
        mapMarkersToIds(maSet);

        double startTime = 0;
        double endTime = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(adjFile));
            String line;

            int geneNo = 0;
            if ((line = br.readLine()) != null) {
                geneNo = Integer.parseInt(line);
            }
            CSGeneNetwork matrix = new CSGeneNetwork(geneNo, geneNo);

            int ctr = 0;
            while ((line = br.readLine()) != null) {
                if (ctr++ % 100 == 0) {
                    endTime = System.currentTimeMillis();
                    double totalTime = endTime - startTime;
                    System.out.println("Time for iteration " + (totalTime / 1000.0));
                    System.out.println("Sparse Matrix Reading line " + ctr);
                    System.out.println("Network Size " + matrix.cardinality() + "");
                    startTime = System.currentTimeMillis();
                    double usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    System.out.println("used memory " + usedMemory);
                }
                if (ctr % 500 == 0) {
                    System.gc();
                }

                if (line.length() > 1 && line.charAt(0) != '-') {
                    String[] arrLine = line.split("[\t:]");

                    String geneAccess = new String(arrLine[0]);
                    String strGeneId1 = new String(arrLine[1]);
                    int geneId1 = Integer.parseInt(strGeneId1);
                    if (geneId1 >= 0) {
                        for (int tokenCtr = 2; tokenCtr < arrLine.length; tokenCtr++) {
                            String strGeneId2 = new String(arrLine[tokenCtr]);
                            int geneId2 = Integer.parseInt(strGeneId2);
                            if (geneId2 >= 0) {
                                String strMi = new String(arrLine[++tokenCtr]);
                                float mi = Float.parseFloat(strMi);
                                if (mi > miThresh) {
                                    if (geneId1 != geneId2) {
                                        //                                    if (geneId2 > geneId1) {
                                        addRelationship(matrix, geneId1, geneId2, mi);
                                    }
                                    //                                    }
                                }
                            }
                        }
                    }

                }
            }
            br.close();
            System.out.println("finished reading");
            return matrix;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void addRelationship(SparseDoubleMatrix2D matrix, int geneId1, int geneId2, float mi) {
        geneId1 = getMappedId(geneId1);
        geneId2 = getMappedId(geneId2);
        matrix.setQuick(geneId1, geneId2, mi);
    }

    int getMappedId(int id) {
        String markerName = idToMarkerMap.get(id);
        int newId = markerToIdMap.get(markerName);
        return newId;
    }

    void mapIdsToMarkers(File adjFile) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(adjFile));
            String line;

            int geneNo = 0;
            if ((line = br.readLine()) != null) {
                geneNo = Integer.parseInt(line);
            }

            while ((line = br.readLine()) != null) {

                if (line.length() > 1 && line.charAt(0) != '-') {
                    String[] arrLine = line.split("[\t:]");

                    String geneAccess = new String(arrLine[0]);
                    String strGeneId1 = new String(arrLine[1]);

                    idToMarkerMap.put(Integer.parseInt(strGeneId1), geneAccess);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void mapMarkersToIds(DSMicroarraySet<DSMicroarray> maSet) {
        for (int markerInfoCtr = 0; markerInfoCtr < maSet.getMarkers().size(); markerInfoCtr++) {
            DSGeneMarker marker = maSet.getMarkers().get(markerInfoCtr);
            int markerId = marker.getSerial();
            String markerName = marker.getLabel();
            markerToIdMap.put(markerName, markerId);
        }
    }

}
