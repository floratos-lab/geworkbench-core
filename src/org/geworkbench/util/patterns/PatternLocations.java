package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;

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
public class PatternLocations implements Comparable{
        private String ascii;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;
        private  DSSeqRegistration registration;
        private int idForDisplay;
        private String patternType;
       // private  DSPattern pattern;
        public static final String DEFAULTTYPE = "splash";
        public static final String TFTYPE = "TFBS";

        public PatternLocations(DSPattern tf, DSSeqRegistration _registration){

            //this.pattern = tf;
            registration = _registration;

           ascii = tf.toString();
            displayName = ascii;
        }

        public PatternLocations(String _ascii){
            ascii = _ascii;
            displayName = ascii;
            patternType = DEFAULTTYPE;

        }
        public  PatternLocations(String _ascii, DSSeqRegistration _registration){
            ascii = _ascii;
            registration = _registration;
            patternType = DEFAULTTYPE;
        }

    public int getIdForDisplay() {
        return idForDisplay;
    }

    public String getAscii() {
        return ascii;
    }

    public String getPatternType() {
        return patternType;
    }

    public DSSeqRegistration getRegistration() {
        return registration;
    }

    public void setIDForDisplay(int hashcode) {
        this.idForDisplay = hashcode;
    }

    public void setAscii(String ascii) {
        this.ascii = ascii;
    }

    public void setPatternType(String patternType) {
        this.patternType = patternType;
    }

    public void setRegistration(DSSeqRegistration registration) {
        this.registration = registration;
    }
    public int compareTo(Object o){
        if(o instanceof PatternLocations){
            if(((PatternLocations)o).getRegistration().x1 == registration.x1 && ((PatternLocations)o).getRegistration().x2 == registration.x2){
                return 0;
            }else  {
                return   registration.x1 - ((PatternLocations)o).getRegistration().x1;
            }
        }
        return 1;
    }
}
