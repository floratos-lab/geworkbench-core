package org.geworkbench.bison.datastructure.graph.tp;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.graph.adjacencyMatrix.AdjacencyMatrix;

import java.io.File;


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
public class AdjacencyMatrixReader {
    public AdjacencyMatrixReader() {
    }

    public static void main(String[] args) {
        new AdjacencyMatrixReader().readNetwork();
    }

    void readNetwork() {
        String dirBase = "/users/aam2110/caWorkbench_3_0";
        //        String dirBase = "C:/caWorkbench_3_0/project/BioWorks/";

        File expFile = new File(dirBase + "/data/BCell/BCell_110104.exp");
        //        File expFile = new File( dirBase + "/data/BCell/BCell_110104_NoManipulatedCL.exp");
        DSMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile); //,false);

        File adjFile = new File("/users/aam2110/RevEng/Human/BCells/NoThreshold/samples_379/sigma_0.16/0.adj");
        //        File adjFile = new File("C:/RevEng/Human/BCells/NoThreshold/samples_379/sigma_0.16/0.adj");

        //        File adjFile = new File("/users/aam2110/RevEng/Human/SmallerMatrix/NoThreshold/samples_254/sigma_0.18/0.adj");
        //        File adjFile = new File("/fsnode1/users/aam2110/RevEng/Human/SmallerMatrix/NoThreshold/samples_254/sigma_0.18/0.adj");
        System.out.println("Adj File " + adjFile);

        //        float miThresh = 0.060619f;
        float miThresh = .062907f;
        //        GeneNetworkIO networkIO = new GeneNetworkIO();
        //        FastNetworkIO networkIO = new FastNetworkIO();
        AdjacencyMatrix adj = new AdjacencyMatrix();
        double startTime = System.currentTimeMillis();
        //        networkIO.readAdjacencyMatrix(mArraySet, adjFile, miThresh);
        adj.read(adjFile.getAbsolutePath(), miThresh);
        double endTime = System.currentTimeMillis();
        double totalTime = endTime - startTime;
        System.out.println("Total Time " + (totalTime / 1000.0));
        double usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("used memory " + usedMemory);
    }

}
