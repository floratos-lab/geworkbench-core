/*
 * CSChipchipSet.java
 *
 * A dataset describing a ChIP-chip experiment
 */

package org.geworkbench.bison.datastructure.biocollections.microarrays;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Serializable;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.CSPSAMMatch;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.CSPSAMRegistration;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.CSPositionSpecificAffinityMatrix;

/**
 * @author manjunath at c2b2 dot org
 */
public class CSChipchipSet extends CSDataSet<CSPSAMMatch<CSPositionSpecificAffinityMatrix, CSPSAMRegistration>> implements Serializable {
    
    static Log log = LogFactory.getLog(CSChipchipSet.class);
    
    int currentLocusId = 0;
    boolean loadingCancelled = false;
    
    /**
     * Creates a new instance of CSChipchipSet
     */
    public CSChipchipSet() {
    }
    
    public void read(File _file) {
        file = _file;
        label = file.getName();
        readFromFile(file);
    }
    
    public void readFromFile(File file) {
        currentLocusId = 0;
        ChipchipParser parser = new ChipchipParser();
        ReaderMonitor rm = null;
        this.label = file.getName();
        this.absPath = file.getAbsolutePath();
        try {
            rm = createProgressReader("Loading data from " + file.getName(), file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        String line;
        try {
            while ((line = rm.reader.readLine()) != null){
                if (!line.trim().equalsIgnoreCase("")) {
                    parser.parseLine(line.trim(), this);
                    if (rm.pm != null) {
                        if (rm.pm.isCanceled()) {
                            loadingCancelled = true;
                            rm.reader.close();
                            return;
                        }
                    }
                }
            }
            rm.reader.close();
        } catch (InterruptedIOException iioe) {
            iioe.printStackTrace();
            loadingCancelled = true;
            return;
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return;
        } finally {
        }
    }
    
    private ReaderMonitor createProgressReader(String display, File file) throws FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, display, fileIn);
        ReaderMonitor retValue = new ReaderMonitor();
        
        retValue.pm = progressIn.getProgressMonitor();
        retValue.reader = new BufferedReader(new InputStreamReader(progressIn));
        return retValue;
    }
    
    private class ChipchipParser {
        int lociNo = 0;
        int tFCount = 0;
        CSPositionSpecificAffinityMatrix currentTF = null;
        
        void parseLine(String line, DSDataSet dataset) {
            if (line.charAt(0) == '#') {
                return; //
            }
            String[] st = line.split("\\s+");
            if (st.length > 0) {
                String token = st[0];
                if (token.equalsIgnoreCase("TF")) {
                    tFCount++;
                    String tf = (new String(st[1])).trim();
                    currentTF = new CSPositionSpecificAffinityMatrix();
                    currentTF.setLabel(tf);
                } else if (line.charAt(0) != '\t') {
                    // Parsing Loci
                    lociNo++;
                    String acc = st[0];
                    String gene = st[1];
                    float pValue = Float.parseFloat(st[2]);
                    String[] annots = acc.split("\\_");
                    
                    CSPSAMRegistration.Organism org;
                    String assembly = "hg18";
                    int x1 = 0;
                    int x2 = 0;
                    int strand = 0;
                    if (annots[0].startsWith("Hs"))
                        org = CSPSAMRegistration.Organism.HUMAN;
                    else if (annots[0].startsWith("Mm"))
                        org = CSPSAMRegistration.Organism.MOUSE;
                    if (annots[1].startsWith("0504"))
                        assembly = "hg17";
                    if (annots[3].contains("+")){
                        String[] coords = annots[3].split("\\+");
                        x1 = Integer.parseInt(coords[0]);
                        x2 = x1 + Integer.parseInt(coords[1]);
                        strand = 0;
                    } else if (annots[3].contains("-")){
                        String[] coords = annots[3].split("\\-");
                        x1 = Integer.parseInt(coords[0]);
                        x2 = x1 - Integer.parseInt(coords[1]);
                        strand = 1;                        
                    }
                    CSPSAMRegistration pr = new CSPSAMRegistration(CSPSAMRegistration.Organism.HUMAN, assembly, new String(annots[2]), x1, x2, strand, pValue);
                    CSPSAMMatch match = new CSPSAMMatch(currentTF);
                    match.setLabel(currentTF.getLabel() + ":" + new String(acc));
                    match.setRegistration(pr);
                    match.setPValue((double)pValue);
                    dataset.add(match);
                }
            }
        } //end of inner class parser
    }
    
    private class ReaderMonitor {
        BufferedReader reader = null;
        ProgressMonitor pm = null;
    }
}