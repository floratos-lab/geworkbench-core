package org.geworkbench.util.function.miUtil;

import org.geworkbench.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;

public class MiErrorsForSigmas {
    int miCol = 12;
    int sigmaStartCol = 13;

    public MiErrorsForSigmas() {
    }

    void doIt() {
        File dataFile = new File("Y:/MI/BimodalGaussian/BimodalGaussianMIs/SigmaResults/BimodalGaussianMiForSigmas.txt");
        Vector fileData = org.geworkbench.util.FileUtil.readFile(dataFile);

        File mseFile = new File("Y:/MI/BimodalGaussian/BimodalGaussianMIs/SigmaErrors/Mse.txt");
        mseFile.getParentFile().mkdirs();

        File pctErrorFile = new File("Y:/MI/BimodalGaussian/BimodalGaussianMIs/SigmaErrors/PctError.txt");

        writeErrorFiles(fileData, mseFile, pctErrorFile);
    }

    void writeErrorFiles(Vector data, File mseFile, File pctErrorFile) {
        try {
            FileWriter mseWriter = new FileWriter(mseFile);
            FileWriter pctErrorWriter = new FileWriter(pctErrorFile);

            Iterator it = data.iterator();
            while (it.hasNext()) {
                String[] fileLine = (String[]) it.next();
                double trueMi = Double.parseDouble(fileLine[miCol]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
