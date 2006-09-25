package org.geworkbench.components.parsers;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.CaARRAYParser;
import org.geworkbench.bison.parsers.resources.CaArrayResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Watkinson
 */
public class CaArrayLoader {

    public static CSExprMicroarraySet loadCaArrayData(CaArrayResource resource) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        maSet.addDescription(resource.getExperiment().toString());
        maSet.setLabel(resource.getExperiment().getIdentifier());
        List ctu = new ArrayList();
        ctu.add("Avg Diff");
        ctu.add("Signal");
        ctu.add("Log2(ratio)");
        ctu.add("Detection");
        ctu.add("Detection p-value");
        ctu.add("Abs Call");
        CaARRAYParser parser = new CaARRAYParser(ctu);
        gov.nih.nci.mageom.domain.BioAssay.BioAssay[] assays = resource.getBioAssays();
        int arrays = 0;
        for (int i = 0; i < assays.length; i++) {
            gov.nih.nci.mageom.domain.BioAssay.BioAssay bap = assays[i];
            DSMicroarray ar = null;
            if (bap != null) {

                ar = parser.getMicroarray(arrays, bap, maSet);
            }
            if (ar != null) {
                maSet.add(arrays++, ar);
            }
             if(assays.length==1){
            maSet.setLabel(bap.getIdentifier());
        }
        }


        return maSet;
    }
}
