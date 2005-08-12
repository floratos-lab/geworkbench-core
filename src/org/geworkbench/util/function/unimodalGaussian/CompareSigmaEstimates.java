package org.geworkbench.util.function.unimodalGaussian;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class CompareSigmaEstimates {
    public CompareSigmaEstimates() {
    }

    public static void main(String[] args) {
        new CompareSigmaEstimates().compareEstimates(args);
    }

    void compareEstimates(String[] args) {
        //        int index = Integer.parseInt(args[0]);
        //        int index = 0;
        try {
            //            String fileRoot = "/users/aam2110";
            //            String fileRoot = "Y:";

            //            String fileRoot = args[0];
            //            String readFileBase =
            //                fileRoot + "/MI/UnimodalGaussian/DiffVar/UnimodalGaussianMIs";
            //            File dataFile = new File(readFileBase +
            //                                     "/SigmaResults/UnimodalGaussianMiForSigmas.txt");
            //
            //            File writeFile = new File(readFileBase +
            //                                      "/EstimateCompare/Comparison.txt");

            File estimatesFile = new File(args[0]);
            File writeFile = new File(args[1]);

            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);

            System.out.println("Reading File");
            Vector allFileData = org.geworkbench.bison.util.FileUtil.readFile(estimatesFile);
            int dataSize = allFileData.size();

            int iterations = 100000;
            //            int iterationCtr = 0;
            //            for (int i = 0; i < allFileData.size() - 1; i++) {
            //                for (int j = i + 1; j < allFileData.size(); j++) {

            for (int iterationCtr = 0; iterationCtr < iterations; iterationCtr++) {
                if (iterationCtr % 1000 == 0) {
                    System.out.println("Calculating " + iterationCtr);
                }
                //                    iterationCtr++;
                int index1 = (int) (Math.random() * dataSize);
                int index2 = (int) (Math.random() * dataSize);
                //                    int index1 = i;
                //                    int index2 = j;
                String[] data1Line = (String[]) allFileData.get(index1);
                String[] data2Line = (String[]) allFileData.get(index2);

                compareResults(index1, index2, data1Line, data2Line, writer);
                //                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void compareResults(int index1, int index2, String[] data1Line, String[] data2Line, FileWriter writer) {
        int miValIndex = 5;
        int sigmaValStartIndex = 6;

        double mi1 = Double.parseDouble(data1Line[miValIndex]);
        double mi2 = Double.parseDouble(data2Line[miValIndex]);
        double expectedMiDiff = mi2 - mi1;
        double expectedMiPctDiff = expectedMiDiff / ((mi1 + mi2) / 2.0);

        try {
            String writeVal = index1 + "\t" + mi1 + "\t" + index2 + "\t" + mi2 + "\t" + expectedMiDiff + "\t" + expectedMiPctDiff;
            //            writer.write(index1 + "\t" + mi1 + "\t" + index2 + "\t" + mi2 +
            //                         "\t" + expectedMiDiff + "\t" + expectedMiPctDiff);
            int expMiDiffSign = getSign(expectedMiPctDiff);

            for (int sigmaCtr = sigmaValStartIndex; sigmaCtr < data1Line.length; sigmaCtr++) {
                double data1Val = Double.parseDouble(data1Line[sigmaCtr]);
                //                double data2Val = 0;
                //                try{
                double data2Val = Double.parseDouble(data2Line[sigmaCtr]);
                //                }catch(Exception e){
                //                    System.out.println("Unable to parse " + data2Line[sigmaCtr]);
                //                }
                int miDiffSign = getSign(data2Val - data1Val);
                if (miDiffSign == expMiDiffSign) {
                    //                    writer.write("1\t");
                    writeVal += "\t1";
                } else {
                    //                    writer.write("0\t");
                    writeVal += "\t0";
                }
            }
            //            writer.write("\n");
            writeVal += "\n";
            writer.write(writeVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int getSign(double val) {
        if (val < 0) {
            return -1;
        } else if (val == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
