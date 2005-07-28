package org.geworkbench.util.pathwaydecoder.bayes.discretizers;

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;

import java.util.List;


public class LogDiscretizer {
    double underexpressedExp = -.5;
    double overexpressedExp = .5;


    public LogDiscretizer() {
    }

    public Table getDiscretizedData(Table origData) {
        Table discretizedData = new Table(false);
        int numAttrs = origData.getAttributes().size();
        int dataSize = origData.size();

        for (int i = 0; i < numAttrs; i++) {
            Attribute oldAttr = (Attribute) origData.getAttribute(i).clone();
            oldAttr.addValue("underexpressed");
            oldAttr.addValue("normal");
            oldAttr.addValue("overexpressed");
            discretizedData.addAttribute(oldAttr);
        }


        double[][] cutPoints = new double[numAttrs][2];
        for (int i = 0; i < numAttrs; i++) {
            cutPoints[i][0] = getMinCutPoint(origData, i);
            cutPoints[i][1] = getMaxCutPoint(origData, i);
        }

        for (int tupCtr = 0; tupCtr < dataSize; tupCtr++) {
            Tuple origTuple = origData.getTuple(tupCtr);
            Tuple newTuple = new Tuple();
            for (int attrCtr = 0; attrCtr < numAttrs; attrCtr++) {
                List values = origTuple.getValues();
                String val = values.get(attrCtr).toString();
                double value = Double.parseDouble(val);

                //double value = origTuple.getValue(attrCtr);
                if (value < cutPoints[attrCtr][0]) {
                    newTuple.addValue("underexpressed");
                } else if (value > cutPoints[attrCtr][1]) {
                    newTuple.addValue("overexpressed");
                } else {
                    newTuple.addValue("normal");
                }
            }
            discretizedData.addTuple(newTuple);
        }
        return discretizedData;
    }

    double getMinCutPoint(Table data, int index) {

        double sum = 0;
        for (int i = 0; i < data.size(); i++) {
            List values = data.getTuple(i).getValues();
            String val = values.get(index).toString();
            double value = Double.parseDouble(val);

            sum += value;

        }
        double mean = sum / data.size();
        double cutPoint = mean * Math.pow(2, underexpressedExp);
        //    System.out.println("Mean " + mean + " Min cut point " + cutPoint);
        return cutPoint;
    }

    double getMaxCutPoint(Table data, int index) {
        double sum = 0;
        for (int i = 0; i < data.size(); i++) {
            List values = data.getTuple(i).getValues();
            String val = values.get(index).toString();
            double value = Double.parseDouble(val);

            sum += value;

        }
        double mean = sum / data.size();
        double cutPoint = mean * Math.pow(2, overexpressedExp);
        //    System.out.println("Mean " + mean + " Max cut point " + cutPoint + "\n");
        return cutPoint;

    }


}
