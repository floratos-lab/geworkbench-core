package org.geworkbench.components.parsers;

//import MAGE.BioAssay;
//import MAGE.DerivedBioAssay;
//import MAGE.MeasuredBioAssay;
import gov.nih.nci.mageom.bean.BioAssay.BioAssayImpl;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.parsers.MAGEParser;
import org.geworkbench.bison.parsers.NCIParser;
import org.geworkbench.bison.parsers.resources.MAGEResource;
import org.geworkbench.bison.parsers.resources.MAGEResource2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Watkinson
 */
public class MAGELoader {

//    public static CSExprMicroarraySet loadMAGEDataSet(MAGEResource2 resource) {
//        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
//        maSet.addDescription(resource.getExperiment().toString());
//        maSet.setLabel(resource.getExperiment().getIdentifier().toString());
//        List ctu = new ArrayList();
//        ctu.add("Avg Diff");
//        ctu.add("Signal");
//        ctu.add("Log2(ratio)");
//        ctu.add("Detection");
//        ctu.add("Detection p-value");
//        ctu.add("Abs Call");
//        NCIParser parser = new NCIParser(ctu);
//        gov.nih.nci.mageom.domain.BioAssay.BioAssay[] assays = resource.getBioAssays();
//        int arrays = 0;
//        for (int i = 0; i < assays.length; i++) {
//            BioAssayImpl bap = (BioAssayImpl) assays[i];
//            CSMicroarray ar = null;
//            if (bap != null) {
//                ar = parser.getMicroarray(arrays, bap, maSet);
//            }
//            if (ar != null) {
//                maSet.add(arrays++, ar);
//            }
//        }
//        return maSet;
//    }
//
//    public static CSExprMicroarraySet loadMAGEDataSet(MAGEResource resource) {
//        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
//        maSet.addDescription(resource.getExperiment().getName());
//        maSet.setLabel(resource.getExperiment().getName());
//        List ctu = new ArrayList();
//        ctu.add("Avg Diff");
//        ctu.add("Signal");
//        ctu.add("Log2(ratio)");
//        ctu.add("Detection");
//        ctu.add("Detection p-value");
//        ctu.add("Abs Call");
//        MAGEParser parser = new MAGEParser(ctu);
//        BioAssay[] assays = resource.getBioAssays();
//        int arrays = 0;
//        for (int i = 0; i < assays.length; i++) {
//            BioAssay ba = (BioAssay) assays[i];
//            if (ba != null && (ba instanceof DerivedBioAssay || ba instanceof MeasuredBioAssay)) {
//                arrays++;
//            }
//        }
//        arrays = 0;
//        for (int i = 0; i < assays.length; i++) {
//            BioAssay ba = (BioAssay) assays[i];
//            if (ba != null && (ba instanceof DerivedBioAssay || ba instanceof MeasuredBioAssay)) {
//                CSMicroarray ar = null;
//                ar = parser.getMicroarray(arrays, ba, maSet);
//                if (ar != null) {
//                    maSet.add(arrays++, ar);
//                }
//            }
//        }
//        return maSet;
//    }

}
