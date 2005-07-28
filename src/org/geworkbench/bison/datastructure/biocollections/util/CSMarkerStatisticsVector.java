package org.geworkbench.bison.datastructure.biocollections.util;

import org.geworkbench.util.Normal;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

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
 * @author not attributable
 * @version 3.0
 */
public class CSMarkerStatisticsVector {
    private static final float nullValue = 9999.9999F;
    String id = "";
    float[] mean = null;
    float[] sdev = null;

    public CSMarkerStatisticsVector() {
    }

    public float getMean(int i, DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view) {
        checkID(view);
        if (sdev[i] == nullValue) {
            compute(i, view);
        }
        return mean[i];
    }

    public float getVariance(int i, DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view) {
        checkID(view);
        if (sdev[i] == nullValue) {
            compute(i, view);
        }
        return sdev[i];
    }

    private void checkID(DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view) {
        if (!view.items().getID().equals(id)) {
            // The selection of items has changed in the meantime. Hence
            // The entire set must be recomputed.
            mean = new float[view.items().size()];
            sdev = new float[view.items().size()];
            for (int i = 0; i < mean.length; i++) {
                sdev[i] = nullValue;
            }
        }
    }

    private void compute(int i, DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> view) {
        org.geworkbench.util.Normal normal = new org.geworkbench.util.Normal();
        for (DSGeneMarker m : view.markers()) {
            normal.add(view.items().get(i).getMarkerValue(m.getSerial()).getValue());
        }
        normal.compute();
        mean[i] = (float) normal.getMean();
        sdev[i] = (float) normal.getSigma();
    }
}
