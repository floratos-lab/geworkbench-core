package org.geworkbench.util.pathwaydecoder.bayes.util;

import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibBToAdj {

    //    File libBFile = new File("Y:/LibB/GeneSim/Results.txt");
    //    AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
    //    File expFile = new File("Y:/LibB/GeneSim/GeneSim.exp");
    //    File writeFile = new File("Y:/LibB/GeneSim/GeneSim_results.adj");

    //    File libBFile = new File("Y:/LibB/SF-001/prior_.00001.txt");
    AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
    //    File expFile = new File("Y:/LibB/SF-001/SF-001.exp");
    //    File writeFile = new File("Y:/LibB/SF-001/prior_.00001.adj");


    public LibBToAdj() {
    }

    public static void main(String[] args) {
        new LibBToAdj().doMe2(args);
    }

    void doMe2(String[] args) {
        String dirBase = args[0];
        File expFile = new File(args[1]);

        File[] libBFiles = new File(dirBase).listFiles();

        for (int fileCtr = 0; fileCtr < libBFiles.length; fileCtr++) {
            File libBFile = libBFiles[fileCtr];
            if (libBFile.getName().endsWith(".txt")) {
                File adjFile = new File(libBFile.getAbsolutePath().replaceAll(".txt", ".adj"));
                convertToAdj(libBFile, expFile, adjFile);
            }
        }
    }

    //    void doMe(){
    //        String searchAlg = "Greedy";
    //        String searchAlg = "HillClimbing";
    //
    //        int[] arrSamples = {1000};
    //        String[] arrPriors = {".001", ".01", ".1", "1", "5", "10", "20", "50", "100", "250", "500", "1000"};
    //        String[] arrPriors = {".000001", ".00001", ".0001"};
    //        String[] arrPriors = {".1", "1", "5"};
    //
    //        for(int samplesCtr = 0; samplesCtr < arrSamples.length; samplesCtr++){
    //            System.out.println("*****************************************************************\n\n\n");
    //            int numSamples = arrSamples[samplesCtr];
    //            for (int i = 0; i < arrPriors.length; i++) {
    //                String prior = arrPriors[i];
    //                String dirBase = "Y:/LibB/SF-001/samples_" + numSamples;
    //                libBFile = new File(dirBase + "/" + searchAlg + "/prior_" + prior +
    //                                    ".txt");
    //                expFile = new File(dirBase + "/SF-001.exp");
    //                writeFile = new File(dirBase + "/" + searchAlg + "/prior_" + prior +
    //                                     ".adj");
    //                doIt();
    //            }
    //        }
    //    }

    //    void doIt(){
    //        convertToAdj(libBFile, expFile, writeFile);
    //    }

    void convertToAdj(File libBFile, File expFile, File writeFile) {
        System.out.println("Converting file " + writeFile.getAbsolutePath());

        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);
        adjMatrix = new AdjacencyMatrix();
        adjMatrix.setMicroarraySet(mArraySet);
        Vector fileData = FileUtil.readFileData(libBFile);
        Iterator fileIt = fileData.iterator();
        while (fileIt.hasNext()) {
            String line = (String) fileIt.next();
            //            System.out.println(line);
            Pattern p = Pattern.compile("\\((parents)(.*)");
            Matcher m = p.matcher(line);
            if (m.matches()) {
                //                \\s'(.*)'(\\(.*\\)).*
                //            if(line.startsWith("(parents")){
                //            if(Pattern.matches(line, "\\(parents")){
                String tail = m.group(2);
                //                System.out.println(tail);
                //                Pattern newPat = Pattern.compile(".*('.*')(\\(.*\\)).*");
                p = Pattern.compile("\\s*'(.*?)'\\((.*?)\\)\\s*'.*");
                //
                m = p.matcher(tail);

                if (m.matches()) {
                    String parents = m.group(1);
                    String children = m.group(2);

                    String[] arrChildren = children.split(" ");

                    int parentIndex = mArraySet.get(parents.trim()).getSerial();
                    for (int i = 0; i < arrChildren.length; i++) {

                        int childIndex = mArraySet.get(arrChildren[i].trim()).getSerial();
                        if (childIndex != -1) {
                            adjMatrix.add(parentIndex, childIndex, 0.5f);
                        }

                    }

                    //                System.out.println("Children " + children);
                } else {
                    //                    System.out.println(tail);
                }
            }
        }
        adjMatrix.print(mArraySet, writeFile);
    }
}
