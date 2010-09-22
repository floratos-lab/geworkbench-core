package org.geworkbench.bison.datastructure.complex.pattern;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.util.FilePathnameUtils;
import org.geworkbench.util.patterns.PatternDB;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */
// this class name is very misleading. it should be something like pattern discovery result
public class SoapParmsDataSet extends CSAncillaryDataSet<DSSequence> {
	private static final long serialVersionUID = 6919356172704233963L;
	static Log log = LogFactory.getLog(	SoapParmsDataSet.class);
	
    private Parameters parms = null;
    private File dataSetFile = null;
    private File resultFile;

    /**
     * Initiate the dataset and assign a result file for that dataset.
     * @param p
     * @param name
     * @param parent
     */
    @SuppressWarnings("unchecked")
	public SoapParmsDataSet(Parameters p, String name, DSDataSet<? extends DSSequence> parent) {
        super((DSDataSet<DSSequence>)parent, name);
        parms = p;
        String idString =  RandomNumberGenerator.getID();
        setID(idString);
        setLabel(name);
         try {
                    String tempFolder = FilePathnameUtils.getTemporaryFilesDirectoryPath();
                     String outputFile = tempFolder;
                     if(parent.getFile().exists()){
                        outputFile+= parent.getFile().getName() + idString +  ".pat";
                     }
             if(!new File(outputFile).exists()){
                 new File(outputFile).createNewFile();
             }
               resultFile = new File(outputFile);

        }catch (IOException ie){
               System.out.print("Cannot create the new fil: " + ie.getMessage());
         }

    }

    public File getDataSetFile() {
        return dataSetFile;
    }

    public void setDataSetFile(File _file) {
        dataSetFile = _file;
    }

    public Parameters getParameters() {
        return parms;
    }

    @Override
    public String getDataSetName() {
        return "Parms S:" + parms.getMinSupport() + ", T:" + parms.getMinTokens() + ", W[" + parms.getMinWTokens() + "," + parms.getWindow() + "]";
    }

    /**
     * writeToFile
     *
     * @param fileName String
     */
    @Override
    public void writeToFile(String fileName) {
    	// TODO not implemented yet
    	log.warn("writeToFile not implemented for SoapParmsDataSet");
    }

	public File getResultFile() {
		return resultFile;
	}
	
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        System.out.println(patternDB);
    }


	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        System.out.println(patternDB);
        for(DSMatchedSeqPattern x : patternDB.getPatterns()) {
        	System.out.println(x.toString());
        }
        System.out.println(resultFile.getAbsolutePath());

		patternDB.write(resultFile);
    }

    private PatternDB patternDB = null;
	public void setPatternDB(PatternDB patternDB) {
		this.patternDB = patternDB;
	}

}
