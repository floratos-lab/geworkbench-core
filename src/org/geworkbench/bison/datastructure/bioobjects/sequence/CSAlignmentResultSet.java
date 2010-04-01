package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.io.File;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

public class CSAlignmentResultSet extends CSAncillaryDataSet implements DSAlignmentResultSet {

    public CSAlignmentResultSet(String fileName, String inputFile, DSDataSet dataSet) {
        super(dataSet, "BLAST Result");
        resultFile = new File(fileName);
        fastaFile = new File(inputFile);

    }

    /**
     * @param fileName
     * @param inputFile
     * @param dataSet
     */
    public CSAlignmentResultSet(String fileName, String inputFile, DSDataSet blastedParentdataSet, DSDataSet parentDataSet) {
        super(parentDataSet, "BLAST Result");
        resultFile = new File(fileName);
        fastaFile = new File(inputFile);
        blastedParentDataSet = blastedParentdataSet;

    }


    private File fastaFile = null;
    private File resultFile = null;

    public DSDataSet getBlastedParentDataSet() {
        return blastedParentDataSet;
    }

    public void setBlastedParentDataSet(DSDataSet blastedParentDataSet) {
        this.blastedParentDataSet = blastedParentDataSet;
    }

    private DSDataSet blastedParentDataSet;

    public void setResultFile(String outputFilename) {
        resultFile = new File(outputFilename);
    }

    /**
     * isDirty
     *
     * @return boolean
     * @todo Implement this geaw.bean.microarray.MAMemoryStatus method
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * setDirty
     *
     * @param boolean0 boolean
     * @todo Implement this geaw.bean.microarray.MAMemoryStatus method
     */
    public void setDirty(boolean boolean0) {
    }

    /**
     * getDataSetName
     *
     * @return String
     * @todo Implement this medusa.components.projects.IDataSet method
     */
    public String getDataSetName() {
        // System.out.println("in get Datasetname " + resultFile.getAbsolutePath());
        return resultFile.getName();
    }

    public String getResultFilePath() {
        if (resultFile.canRead()) {
            return resultFile.getAbsolutePath();
        }
        return null;
    }

    public File getDataSetFile() {

        return fastaFile;
    }

    public void setDataSetFile(File _file) {
        fastaFile = _file;
    }

    /**
     * @param ads IAncillaryDataSet
     * @return boolean
     * @todo implement later.
     */
    public boolean equals(Object ads) {
        if (ads instanceof DSAncillaryDataSet) {
            return getDataSetName() ==
                    ((DSAncillaryDataSet) ads).getDataSetName();
        } else {
            return false;
        }
    }

    /**
     * getFile
     *
     * @return File
     * @todo Implement this medusa.components.projects.IDataSet method
     */
    public File getFile() {
        return resultFile;
    }

    /**
     * writeToFile
     *
     * @param fileName String
     */
    public void writeToFile(String fileName) {
    }

}
