package org.geworkbench.components.promoter;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import org.geworkbench.util.sequences.SequenceDB;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PromoterPatternDB implements Serializable {
    ArrayList promoterPatterns;
    HashMap display;
    Hashtable matches;
    private ArrayList patterns;
    File seqFile;
    SequenceDB seqDB;
    public PromoterPatternDB(File _seqFile) {
    }

    public PromoterPatternDB(SequenceDB _seqDB) {
        this.seqDB = _seqDB;
    }

//
//    public boolean read(File _file) {
//       try {
//           file = new File(_file.getCanonicalPath());
//           label = file.getName();
//           BufferedReader reader = new BufferedReader(new FileReader(file));
//           reader.readLine();
//           String s = reader.readLine();
//           if (s.startsWith("File:")) {
//               File newFile = new File(s.substring(5));
//               if (!dataSetFile.getName().equalsIgnoreCase(newFile.getName())) {
//                   return false;
//               }
//               s = reader.readLine();
//           }
//           patterns.clear();
//           while (s != null) {
//               CSMatchedSeqPattern pattern = new org.geworkbench.util.patterns.CSMatchedSeqPattern(s);
//               patterns.add(pattern);
//               s = reader.readLine();
//           }
//       } catch (IOException ex) {
//           System.out.println("Exception: " + ex);
//       }
//       return true;
//   }
//
//   public void write(File file) {
//       try {
//           BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//           int i = 0;
//           Iterator it = patterns.iterator();
//           String path = this.getDataSetFile().getCanonicalPath();
//
//           writer.write("File:" + path);
//           writer.newLine();
//
//           writer.flush();
//           writer.close();
//       } catch (IOException ex) {
//           System.out.println("Exception: " + ex);
//       }
//   }

    /**
     * setPatterns
     *
     * @param promoterPatterns ArrayList
     */
    public void setPatterns(ArrayList _promoterPatterns) {
        this.promoterPatterns = _promoterPatterns;
    }

    public void setDisplay(HashMap display) {
        this.display = display;
    }

    public void setMatches(Hashtable matches) {
        this.matches = matches;
    }

    public void setSeqDB(SequenceDB seqDB) {
        this.seqDB = seqDB;
    }

    public Hashtable getMatches() {
        return matches;
    }

    public ArrayList getPatterns() {
        return patterns;
    }

    public HashMap getDisplay() {
        return display;
    }

    public SequenceDB getSeqDB() {
        return seqDB;
    }


}
