package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.DSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.util.CSCriterionManager;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class DataSetNode extends ProjectTreeNode {
    public DSDataSet dataFile = null;

    private SerialInstance serialInstance;

    DataSetNode(DSDataSet df) {
        dataFile = df;
        setUserObject(dataFile.getDataSetName());
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // Include the criteria info if there is any
        DSCriteria criteria = CSCriterionManager.getCriteria(dataFile);
        DSClassCriteria classCriteria = CSCriterionManager.getClassCriteria(dataFile);
        serialInstance = new SerialInstance(criteria, classCriteria);
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (serialInstance.criteria != null) {
            CSCriterionManager.setCriteria(dataFile, serialInstance.criteria);
        }
        if (serialInstance.classCriteria != null) {
            CSCriterionManager.setClassCriteria(dataFile, serialInstance.classCriteria);
        }
        serialInstance = null;
    }

    protected static class SerialInstance implements Serializable {

        DSCriteria criteria;
        DSClassCriteria classCriteria;

        public SerialInstance(DSCriteria criteria, DSClassCriteria classCriteria) {
            this.criteria = criteria;
            this.classCriteria = classCriteria;
        }

    }

}
