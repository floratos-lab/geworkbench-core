package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.
        DSSequenceSet;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSMatchedSeqPattern;
import polgara.soapPD_wsdl.SOAPOffset;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import java.util.TreeSet;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSSeqRegistration;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        CSSequenceSet;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import java.util.List;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import java.util.Set;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PatternOperations {
    static private ArrayList<Color> colors = makeColors();
    //{Color.red, Color.blue, Color.cyan, Color.black, Color.green,
    //Color.magenta, Color.orange, Color.pink, Color.yellow};
    static private ArrayList<Color> store = new ArrayList<Color>();
    static private HashMap<Integer,
            Color> colorPatternMap = new HashMap<Integer, Color>();

    static ArrayList<Color> makeColors() {
        Color[] c = {new Color(0, 0, 255), new Color(138, 43, 226),
                    new Color(165, 42, 42), new Color(222, 184, 135),
                    new Color(95, 158, 160), new Color(127, 255, 0),
                    new Color(210, 105, 30), new Color(255, 127, 80),
                    new Color(100, 149, 237), new Color(220, 20, 60),
                    new Color(0, 255, 255), new Color(0, 0, 139),
                    new Color(184, 134, 11), new Color(238, 130, 238),
                    new Color(0, 100, 0), new Color(189, 183, 107),
                    new Color(139, 0, 139), new Color(85, 107, 47),
                    new Color(255, 140, 0), new Color(153, 50, 204),
                    new Color(139, 0, 0), new Color(233, 150, 122),
                    new Color(255, 255, 0), new Color(255, 0, 0),
                    new Color(0, 0, 0), new Color(128, 0, 128)};
        ArrayList<Color> list = new ArrayList<Color>();
        for (int i = 0; i < c.length; i++) {
            list.add(c[i]);
        }
        return list;
    }


    static public void setPatternColor(Integer integer, Color c) {
        colorPatternMap.put(integer, c);
    }

    static public Color getPatternColor(int i) {
        if (colors.isEmpty()) {
            colors.addAll(store);
            store.clear();
        }

        Color c = colorPatternMap.get(i);
        if (c != null) {
            return c;
        }
        int index = Math.abs(i) % colors.size();
        c = colors.remove(index);
        store.add(c);
        colorPatternMap.put(new Integer(i), c);
        return c;
    }

    static public void fill(DSMatchedSeqPattern pattern, DSSequenceSet sDB) {
        if (pattern.getClass().isAssignableFrom(org.geworkbench.util.patterns.
                                                CSMatchedSeqPattern.class)) {
            CSMatchedSeqPattern p = (CSMatchedSeqPattern) pattern;
            String ascii = new String();
            SOAPOffset[] offsets = p.offset.value;
            int j = offsets[0].getDx();
            for (int i = 0; i < offsets.length; i++, j++) {
                int dx = offsets[i].getDx();
                for (; j < dx; j++) {
                    ascii += '.';
                }
                String tokString = offsets[i].getToken();
                if (tokString.length() > 1) {
                    ascii += '[' + tokString + ']';
                } else {
                    ascii += tokString;
                }
            }
            p.ascii = ascii;
        }
    }

    /**
     * A utility to create a match between a sequence with all available patterns within the sequence.
     *
     * @param patterns DSCollection
     * @param sequenceDB DSSequenceSet
     * @return HashMap
     */
    public static HashMap<CSSequence,
            PatternSequenceDisplayUtil> processPatterns(DSCollection<
            DSMatchedPattern<DSSequence,
            DSSeqRegistration>> patterns, DSSequenceSet sequenceDB) {
        return processPatterns(patterns, sequenceDB,
                               PatternLocations.DEFAULTTYPE);

    }

    public static void addTFMatches(HashMap<CSSequence,
                                    PatternSequenceDisplayUtil> existedPatterns,
                                    List<DSPatternMatch<DSSequence,
                                    DSSeqRegistration>>
                                    matches, DSPattern tf) {
        if (matches == null) {
            return;
        }
        if (existedPatterns == null) {
            existedPatterns = new HashMap<CSSequence,
                              PatternSequenceDisplayUtil>();

        }

        for (DSPatternMatch<DSSequence, DSSeqRegistration> sp : matches) {
            DSSequence hitSeq = sp.getObject();
            DSSeqRegistration reg = sp.getRegistration();
            if (existedPatterns.containsKey(hitSeq)) {
                PatternSequenceDisplayUtil pu = (
                        PatternSequenceDisplayUtil)
                                                existedPatterns.get(hitSeq);
                PatternLocations pl = new PatternLocations(
                        tf, reg);
                pl.setPatternType(PatternLocations.TFTYPE);
                pl.setIDForDisplay(tf.hashCode());
                pu.addPattern(pl);

            } else {
                PatternSequenceDisplayUtil pu = new PatternSequenceDisplayUtil((
                        CSSequence) hitSeq);
                PatternLocations pl = new PatternLocations(
                        tf, reg);
                pl.setPatternType(PatternLocations.TFTYPE);
                pl.setIDForDisplay(tf.hashCode());
                pu.addPattern(pl);
                existedPatterns.put((CSSequence) hitSeq, pu);
            }
        }

    }


    /**
     * A utility to create a match between a sequence with all available patterns within the sequence.
     * @param patterns DSCollection
     * @param sequenceDB DSSequenceSet
     * @param patternType String
     * @return HashMap
     */
    public static HashMap<CSSequence,
            PatternSequenceDisplayUtil> processPatterns(DSCollection<
            DSMatchedPattern<DSSequence,
            DSSeqRegistration>> patterns, DSSequenceSet sequenceDB,
            String patternType) {
        if (patterns != null && sequenceDB != null) {
            HashMap<CSSequence,
                    PatternSequenceDisplayUtil>
                    sequencePatternMatches = new HashMap<CSSequence,
                                             PatternSequenceDisplayUtil>();
            PatternSequenceDisplayUtil[] patternsSequenceList = new
                    PatternSequenceDisplayUtil[sequenceDB.size()];
            for (int i = 0; i < sequenceDB.size(); i++) {
                patternsSequenceList[i] = new PatternSequenceDisplayUtil((
                        CSSequence) sequenceDB.get(i));
                sequencePatternMatches.put((CSSequence) sequenceDB.get(i),
                                           patternsSequenceList[i]);
            }

            if (patterns != null) {
                for (int row = 0; row < patterns.size(); row++) {
                    DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                                  patterns.get(row);

                    if (pattern != null) {
                        PatternOperations.setPatternColor(new Integer(pattern.
                                hashCode()),
                                PatternOperations.
                                getPatternColor(row));
                        for (int locusId = 0;
                                           locusId < pattern.getSupport();
                                           locusId++) {
                            int seqId = ((CSMatchedSeqPattern) pattern).getId(
                                    locusId);
                            DSPatternMatch<DSSequence,
                                    DSSeqRegistration> sp = pattern.get(locusId);

                            DSSequence hitSeq = sp.getObject();
                            DSSeqRegistration reg = sp.getRegistration();
                            if (sequencePatternMatches.containsKey(hitSeq)) {
                                PatternSequenceDisplayUtil pu = (
                                        PatternSequenceDisplayUtil)
                                        sequencePatternMatches.get(hitSeq);
                                PatternLocations pl = new PatternLocations(
                                        pattern.
                                        getASCII(), reg);
                                pl.setPatternType(patternType);
                                pl.setIDForDisplay(pattern.hashCode());
                                pu.addPattern(pl);

                            }

                        }

                    }
                }
            }

            return sequencePatternMatches;
        }
        return null;
    }


    public static HashMap<CSSequence,
            PatternSequenceDisplayUtil> merge(HashMap<CSSequence,
                                              PatternSequenceDisplayUtil>
                                              seqPatterns, HashMap<CSSequence,
                                              PatternSequenceDisplayUtil>
                                              tfPatterns) {
        if (seqPatterns == null) {
            return tfPatterns;
        }
        if (tfPatterns == null) {
            return seqPatterns;
        }
        HashMap<CSSequence,
                PatternSequenceDisplayUtil>
                allPatterns = new HashMap<CSSequence,
                              PatternSequenceDisplayUtil>(seqPatterns);

        Set<CSSequence> keySet = tfPatterns.keySet();
        for (CSSequence keySeq : keySet) {
            if (allPatterns.containsKey(keySeq)) {
                PatternSequenceDisplayUtil newPu = tfPatterns.get(keySeq);
                allPatterns.get(keySeq).mergePatternSequenceDisplayUtil(newPu);
            }
        }

        return allPatterns;

    }
    /*
       public int intersection(Pattern p0, Pattern p1) {
      // creates the intersection of two patterns
      int prevId = -1;
      Locus[] locus = new Locus[p0.locus.value.length];
      int i = 0;
      int j = 0;
      while((i < p0.locus.value.length) && (j < p1.locus.value.length)){
        SOAPLocus l_0 = p0.locus.value[i];
        SOAPLocus l_1 = p1.locus.value[j];
        if(l_0.getId() > l1.getId()) {
          j++;
        } else if (l_0.getId() < prevId) {
          i++;
        } else {
          locus[j] = locus.value[i_0++].getId();
          i_0++;
        }
      }
      int[] finalSupport = new int[j];
      for(int i = 0; i < j; i++) finalSupport[i] = support[i];
      return finalSupport;
       }
     */
}
