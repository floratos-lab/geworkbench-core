package org.geworkbench.util.pathwaydecoder.bayes.util;

import edu.ksu.cis.kdd.data.Attribute;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.data.Tuple;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.bayes.BayesUtil;
import org.geworkbench.util.pathwaydecoder.bayes.discretizers.LogDiscretizer;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

public class PrintLibBFiles {

    //    File expFile = new File("Y:/LibB/GeneSim/GeneSim.exp");
    //    File namesFile = new File("Y:/LibB/GeneSim/GeneSim_names.txt");
    //    File instanceFile = new File("Y:/LibB/GeneSim/GeneSim_instances.txt");

    //        File expFile = new File("Y:/LibB/RND-001/samples_1000/RND-001.exp");
    //            File namesFile = new File("Y:/LibB/RND-001/samples_1000/RND-001_names.txt");
    //            File instanceFile = new File("Y:/LibB/RND-001/samples_1000/RND-001_instances.txt");


    //        File expFile = new File("Y:/LibB/SF-001/samples_1000/SF-001.exp");
    //    File namesFile = new File("Y:/LibB/SF-001/samples_1000/SF-001_names.txt");
    //    File instanceFile = new File("Y:/LibB/SF-001/samples_1000/SF-001_instances.txt");


    public PrintLibBFiles() {
    }

    public static void main(String[] args) {
        new PrintLibBFiles().doIt(args);
    }

    void doIt(String[] args) {
        File expFile = new File(args[0]);
        File instanceFile = new File(args[1]);
        File namesFile = new File(args[2]);
        printInstanceFile(expFile, instanceFile);
        printNFile(expFile, namesFile);
    }

    void printInstanceFile(File expFile, File instanceFile) {
        try {
            FileWriter writer = new FileWriter(instanceFile);

            CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
            mArraySet.readFromFile(expFile);

            BayesUtil util = new BayesUtil();
            Table rawData = util.convertToTable(mArraySet);

            LogDiscretizer discretizer = new LogDiscretizer();
            Table discretizedData = discretizer.getDiscretizedData(rawData);

            List tuples = discretizedData.getTuples();
            Iterator it = tuples.iterator();

            List attributes = discretizedData.getAttributes();


            while (it.hasNext()) {
                Tuple tuple = (Tuple) it.next();
                //                Iterator attrIt = attributes.iterator();
                //                while(attrIt.hasNext()){
                //                    Attribute attr = (Attribute)attrIt.next();
                //
                //                    double value = tuple.getValue(attr.getLabel());
                List vals = tuple.getValues();
                Iterator valIt = vals.iterator();

                writer.write("(");
                while (valIt.hasNext()) {
                    writer.write(valIt.next() + " ");
                }
                writer.write(")\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void printNFile(File expFile, File namesFile) {
        try {
            FileWriter writer = new FileWriter(namesFile);

            CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
            mArraySet.readFromFile(expFile);

            BayesUtil util = new BayesUtil();
            Table rawData = util.convertToTable(mArraySet);

            LogDiscretizer discretizer = new LogDiscretizer();
            Table discretizedData = discretizer.getDiscretizedData(rawData);

            List attributes = discretizedData.getAttributes();
            Iterator it = attributes.iterator();

            while (it.hasNext()) {
                Attribute at = (Attribute) it.next();
                writer.write("(var '" + at.getName() + " ' (");
                List vals = at.getValues();
                Iterator valIt = vals.iterator();
                while (valIt.hasNext()) {
                    writer.write(valIt.next() + " ");
                }
                writer.write("))\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
