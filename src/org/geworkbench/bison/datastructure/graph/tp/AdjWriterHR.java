package org.geworkbench.bison.datastructure.graph.tp;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.graph.CSGeneNetwork;
import org.geworkbench.bison.datastructure.graph.io.NetworkIOHr;
import org.geworkbench.bison.datastructure.graph.io.NetworkIOLinkExp;

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
public class AdjWriterHR {
    File expFile = new File("Y:/RevEng/Human/BCells/TheMatrix11012004.exp");
    File adjFile = new File("Y:/RevEng/Human/BCells/CleanedMatrices/samples_379/sigma_0.16/miThresh_0.08/tolerance_0.15/0.adj");
    File writeFile = new File("Y:/RevEng/Human/BCells/CleanedMatrices/samples_379/sigma_0.16/miThresh_0.08/tolerance_0.15/myc_targets.txt");
    File pegFile = new File("Y:/RevEng/Human/BCells/CleanedMatrices/samples_379/sigma_0.16/miThresh_0.08/tolerance_0.15/peg_targets.txt");

    public AdjWriterHR() {
    }

    void doIt() {
        DSMicroarraySet maSet = new CSExprMicroarraySet();
        maSet.readFromFile(expFile);//, false);

        NetworkIOLinkExp reader = new NetworkIOLinkExp();
        CSGeneNetwork network = reader.readAdjacencyMatrix(adjFile, maSet, 0.0f);

        NetworkIOHr io = new NetworkIOHr();
        //        io.writeAdjacencyMatrix(network, maSet, writeFile);
        io.printGeneTargets(network, maSet, writeFile, "1973_s_at");
        io.printGeneTargets(network, maSet, pegFile, "39696_at");
    }

    public static void main(String[] args) {
        new AdjWriterHR().doIt();
    }
}
