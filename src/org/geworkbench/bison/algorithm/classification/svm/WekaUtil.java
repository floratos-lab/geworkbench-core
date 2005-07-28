package org.geworkbench.bison.algorithm.classification.svm;

import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.DSClassCriteria;
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
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.util.Vector;

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
public class WekaUtil {
    public WekaUtil() {
    }

    public Instances generateInstances(DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView) {
        //        criteria = (CSPhCriteria)(maSetView.getDataSet()).getObject(CSPhCriteria.class);
        DSClassCriteria classCriteria = CSCriterionManager.getClassCriteria(maSetView.getDataSet());
        int numMarkers = maSetView.markers().size();
        FastVector attVector = new FastVector();
        for (int i = 0; i < numMarkers; i++) {
            String markerLabel = maSetView.markers().get(i).getLabel();
            Attribute attr = new Attribute(markerLabel);
            attVector.addElement(attr);
            //            attVector.addElement(markerLabel);
        }
        FastVector classValues = new FastVector();
        classValues.addElement("Case");
        classValues.addElement("Control");
        Attribute classAttr = new Attribute("Class", classValues);
        attVector.addElement(classAttr);
        Instances classificationSet = new Instances("MASet", attVector, 1);
        classificationSet.setClassIndex(classificationSet.numAttributes() - 1);

        for (int maCtr = 0; maCtr < maSetView.items().size(); maCtr++) {
            DSBioObject bioObject = maSetView.items().get(maCtr);
            if (bioObject instanceof DSMicroarray) {
                DSMicroarray ma = (DSMicroarray) bioObject;
                Instance instance = new Instance(maSetView.markers().size() + 1);
                instance.setDataset(classificationSet);
                for (int markerCtr = 0; markerCtr < maSetView.markers().size(); markerCtr++) {
                    instance.setValue(markerCtr, ma.getMarkerValue(markerCtr).getValue());
                }
                DSAnnotValue value = classCriteria.getValue(ma);
                instance.setClassValue(value.toString());
                classificationSet.add(instance);
            }
        }

        return classificationSet;
    }

    public static Instances generateInstances(DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> caseMaSet, DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> controlMaSet) {
        int numMarkers = caseMaSet.markers().size();
        FastVector attVector = new FastVector();
        for (int i = 0; i < numMarkers; i++) {
            String markerLabel = caseMaSet.markers().get(i).getLabel();
            attVector.addElement(new Attribute(markerLabel));
        }
        FastVector classValues = new FastVector();
        classValues.addElement("Case");
        classValues.addElement("Contol");
        Attribute classAttr = new Attribute("Class", classValues);
        attVector.addElement(classAttr);

        Instances classificationSet = new Instances("MASet", attVector, 1);
        classificationSet.setClassIndex(classificationSet.numAttributes() - 1);

        for (int caseMaCtr = 0; caseMaCtr < caseMaSet.size(); caseMaCtr++) {
            Instance instance = new Instance(numMarkers + 1);
            instance.setDataset(classificationSet);
            for (int markerCtr = 0; markerCtr < numMarkers; markerCtr++) {
                instance.setValue(markerCtr, caseMaSet.getValue(markerCtr, caseMaCtr));
            }
            instance.setClassValue("Case");
            classificationSet.add(instance);
        }

        for (int controlMaCtr = 0; controlMaCtr < controlMaSet.size(); controlMaCtr++) {
            Instance instance = new Instance(numMarkers + 1);
            instance.setDataset(classificationSet);
            for (int markerCtr = 0; markerCtr < numMarkers; markerCtr++) {
                instance.setValue(markerCtr, controlMaSet.getValue(markerCtr, controlMaCtr));
            }
            instance.setClassValue("Control");
            classificationSet.add(instance);
        }
        return classificationSet;
    }

    public static Instances generateInstances(DSMicroarraySetView caseMaSet, DSMicroarraySetView controlMaSet, Vector<DSGeneMarker> markerVector) {
        int numMarkers = markerVector.size();
        FastVector attVector = new FastVector();
        for (int i = 0; i < numMarkers; i++) {
            String markerLabel = markerVector.get(i).getLabel();
            attVector.addElement(new Attribute(markerLabel));
        }
        FastVector classValues = new FastVector();
        classValues.addElement("Case");
        classValues.addElement("Control");
        Attribute classAttr = new Attribute("Class", classValues);
        attVector.addElement(classAttr);

        Instances classificationSet = new Instances("MASet", attVector, 1);
        classificationSet.setClassIndex(classificationSet.numAttributes() - 1);

        for (int caseMaCtr = 0; caseMaCtr < caseMaSet.size(); caseMaCtr++) {
            Instance instance = new Instance(numMarkers + 1);
            instance.setDataset(classificationSet);
            for (int markerCtr = 0; markerCtr < numMarkers; markerCtr++) {
                double value = caseMaSet.getValue(markerVector.get(markerCtr), caseMaCtr);
                instance.setValue(markerCtr, value);
            }
            instance.setClassValue("Case");
            classificationSet.add(instance);
        }

        for (int controlMaCtr = 0; controlMaCtr < controlMaSet.size(); controlMaCtr++) {
            Instance instance = new Instance(numMarkers + 1);
            instance.setDataset(classificationSet);
            for (int markerCtr = 0; markerCtr < numMarkers; markerCtr++) {
                double value = controlMaSet.getValue(markerVector.get(markerCtr), controlMaCtr);
                instance.setValue(markerCtr, value);
            }
            instance.setClassValue("Control");
            classificationSet.add(instance);
        }
        return classificationSet;
    }


    public Instances normalizeInstances(Instances origInstances) {
        try {
            Filter filter = new Normalize();
            filter.setInputFormat(origInstances);
            for (int i = 0; i < origInstances.numInstances(); i++) {
                filter.input(origInstances.instance(i));
            }
            filter.batchFinished();
            Instances newData = filter.getOutputFormat();
            Instance processed;
            while ((processed = filter.output()) != null) {
                newData.add(processed);
            }
            return newData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
