package org.geworkbench.bison.algorithm.classification;

import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.DSClassCriteria;
import org.geworkbench.bison.annotation.CSCriteria;
import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.util.CSCriterionManager;
import org.geworkbench.bison.util.DSAnnotValue;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileWriter;

/**
 * <p>Title: caWorkbench</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 * @todo - watkin - Should this now be retrofitted to implement {@link DSClassifier}?
 */
public class ExpressionClassifier <T extends DSGeneMarker,Q extends DSMicroarray> {
    /**
     * The reference microarray set.
     */
    DSMicroarraySetView<T, Q> maSetView = null;
    CSCriteria criteria = null;
    DSClassCriteria classCriteria = null;

    public ExpressionClassifier(DSMicroarraySetView<T, Q> maSetView) {
        this.maSetView = maSetView;
    }

    public void initializeClassifier() {
        DSCriteria<DSBioObject> criteria = CSCriterionManager.getCriteria(maSetView.getDataSet());
        classCriteria = CSCriterionManager.getClassCriteria(maSetView.getDataSet());
        //selectedCriterion = criteria.setSelectedCriterion();

        int numMarkers = maSetView.markers().size();
        //ClassificationManager classMgr = ((IMicroarraySet) maSetView.getReferenceMicroarraySet()).getClassificationManager();
        FastVector attVector = new FastVector();
        for (int i = 0; i < numMarkers; i++) {
            attVector.addElement("Value" + i);
        }
        attVector.addElement("Class");
        Instances classificationSet = new Instances("MASet", attVector, 1);
        classificationSet.setClassIndex(classificationSet.numAttributes() - 1);

        for (int maCtr = 0; maCtr < maSetView.items().size(); maCtr++) {
            DSMicroarray ma = maSetView.items().get(maCtr);
            //double[] markerValues = maView.getMarkerValueValues();
            Instance instance = new Instance(maSetView.items().size());
            instance.setDataset(classificationSet);
            for (int markerCtr = 0; markerCtr < maSetView.items().size(); markerCtr++) {
                instance.setValue(markerCtr, ma.getMarkerValue(markerCtr).getValue());
            }
            DSAnnotValue value = classCriteria.getValue(ma);
            instance.setClassValue(value.hashCode());
            classificationSet.add(instance);
        }
    }

    public void printWekaFile() {
        try {
            FileWriter writer = new FileWriter("c:/JavaProgs/WekaTest/Data/Module2.txt");
            int numMarkers = maSetView.markers().size();
            //            ClassificationManager classMgr = ((IMicroarraySet)maSetView.getReferenceMicroarraySet()).getClassificationManager();

            FastVector attVector = new FastVector();
            for (int i = 0; i < numMarkers; i++) {
                //                for (int i = 0; i < 30; i++) {
                attVector.addElement(new Attribute("Value" + i));
            }

            FastVector classValues = new FastVector();
            classValues.addElement("case");
            classValues.addElement("control");
            Attribute classAttribute = new Attribute("Class", classValues);

            attVector.addElement(classAttribute);
            //        attVector.addElement(new Attribute("Class"));
            Instances classificationSet = new Instances("MASet", attVector, 1);
            classificationSet.setClassIndex(classificationSet.numAttributes() - 1);

            for (int maCtr = 0; maCtr < maSetView.items().size(); maCtr++) {
                DSMicroarray ma = maSetView.items().get(maCtr);
                //                double[] markerValues = ma.getMarkerValueValues();
                Instance instance = new Instance(maSetView.items().size() + 1);
                //                Instance instance = new Instance(31);
                instance.setDataset(classificationSet);
                for (int markerCtr = 0; markerCtr < maSetView.items().size(); markerCtr++) {
                    //                for (int markerCtr = 0; markerCtr < 30;
                    //                     markerCtr++) {
                    //                    int tmpCtr = (int)(Math.random() * 593.0);
                    //                    instance.setValue(markerCtr, markerValues[tmpCtr]);
                    instance.setValue(markerCtr, ma.getMarkerValue(markerCtr).getValue());
                }
                //            int classId = 2;
                DSAnnotValue value = classCriteria.getValue(ma);
                //                IPropertyTaggedItem) maView);
                if (value == CSClassCriteria.cases) {
                    instance.setClassValue("case");
                    //                instance.setValue(classAttribute, "case");
                } else {
                    //                instance.setValue(classAttribute, "control");
                    instance.setClassValue("control");
                }
                classificationSet.add(instance);
            }
            //        try{
            //            SMO classifier = new SMO();
            //            classifier.buildClassifier(classificationSet);
            //        }catch(Exception e){
            //            e.printStackTrace();
            //        }
            writer.write(classificationSet.toString());
            //            System.out.print(classificationSet.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        System.out.println("done");
    }

}
