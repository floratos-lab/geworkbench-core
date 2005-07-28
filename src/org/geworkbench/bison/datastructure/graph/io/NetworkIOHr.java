package org.geworkbench.bison.datastructure.graph.io;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.io.File;
import java.io.FileWriter;

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
 *          Input/output of human readable adjacency matrices
 */
public class NetworkIOHr {
    public NetworkIOHr() {
    }

    public void writeAdjacencyMatrix(SparseDoubleMatrix2D network, DSMicroarraySet<DSMicroarray> mArraySet, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            writer.write(network.rows() + "\n");
            for (int rowCtr = 0; rowCtr < network.rows(); rowCtr++) {
                //                writer.write(mArraySet.getMarkerAccession(rowCtr) + ":" + rowCtr);
                String markerName = mArraySet.getMarkers().get(rowCtr).getShortName();
                writer.write(markerName + ":" + rowCtr);
                DoubleMatrix1D gene2Row = network.viewRow(rowCtr);
                for (int colCtr = 0; colCtr < gene2Row.size(); colCtr++) {
                    double mi = network.getQuick(rowCtr, colCtr);
                    if (mi != 0) {
                        String targetName = mArraySet.getMarkers().get(colCtr).getShortName();
                        //                        writer.write("\t" + targetName + "\t" + mi);
                        writer.write("\t" + targetName);
                        //                        writer.write("\t" + colCtr + "\t" + mi);
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printGeneTargets(SparseDoubleMatrix2D network, DSMicroarraySet<DSMicroarray> mArraySet, File writeFile, String accession) {
        try {
            FileWriter writer = new FileWriter(writeFile);

            DSGeneMarker marker = mArraySet.getMarkers().get(accession);
            writer.write(marker.getLabel() + "\t" + marker.getShortName() + "\n");
            int markerId = marker.getSerial();
            DoubleMatrix1D gene2Row = network.viewRow(markerId);
            for (int colCtr = 0; colCtr < gene2Row.size(); colCtr++) {
                double mi = network.getQuick(markerId, colCtr);
                if (mi != 0) {
                    DSGeneMarker targetMarker = mArraySet.getMarkers().get(colCtr);
                    //                        writer.write("\t" + targetName + "\t" + mi);
                    writer.write("Hs." + targetMarker.getUnigene() + "\t" + targetMarker.getShortName() + "\t" + targetMarker.getLabel() + "\t" + mi + "\n");
                    //                        writer.write("\t" + colCtr + "\t" + mi);
                }
            }
            writer.write("\n");

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
