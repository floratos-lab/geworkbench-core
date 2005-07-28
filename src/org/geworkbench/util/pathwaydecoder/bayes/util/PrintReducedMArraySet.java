package org.geworkbench.util.pathwaydecoder.bayes.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;

import java.io.File;

public class PrintReducedMArraySet {
    //    File expFile = new File("Y:/LibB/SF-001/SF-001.exp");
    //    int numSamples = 125;

    public PrintReducedMArraySet() {
    }

    public static void main(String[] args) {
        new PrintReducedMArraySet().doMe(args);
    }

    void doMe(String[] args) {
        File expFile = new File(args[0]);
        int numSamples = Integer.parseInt(args[1]);
        String writeFile = args[2];
        System.out.println("Writing file " + writeFile);
        doIt(expFile, writeFile, numSamples);

        //        int[] arrNumSamples = {125, 250, 500, 750, 1000};
        //        for(int i = 0; i < arrNumSamples.length; i++){
        //            numSamples = arrNumSamples[i];
        //            doIt(expFile, writeFile, numSamples);
        //        }
    }

    void doIt(File expFile, String writeFile, int numSamples) {
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        //        String writeFile = "Y:/LibB/SF-001/samples_" + numSamples + "/SF-001.exp";

        mArraySet.writeToFile(writeFile, numSamples);
    }
}
