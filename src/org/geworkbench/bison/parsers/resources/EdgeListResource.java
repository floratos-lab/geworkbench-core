package org.geworkbench.bison.parsers.resources;

import java.io.File;

public class EdgeListResource extends AbstractResource {
    /**
     * The input edge list file that gives rise to the inputReader
     */
    File inputFile = null;

    /**
     * Set the file name for the input file from which this resource is generated.
     *
     * @param iFName The input file name.
     */
    public void setInputFile(File file) {
        inputFile = file;
    }

    /**
     * Return the name of the input file from which this resource is generated
     *
     * @return
     */
    public File getInputFile() {
        return inputFile;
    }
}
