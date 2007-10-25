package org.geworkbench.bison.datastructure.complex.pattern.sequence;

import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;

public class CSSeqPatternMatch extends CSPatternMatch<DSSequence, CSSeqRegistration> implements DSSeqPatternMatch {

    public CSSeqPatternMatch(DSSequence seq) {
        super(seq);
    }

    //public void setPattern(IGetPattern pattern){
    //  this.pattern=pattern;
    //}
    public CSSeqRegistration getRegistration() throws IndexOutOfBoundsException {
        if (registration == null) {
            registration = new CSSeqRegistration();
            return registration;
        }
        return registration;
    }
    /*
      public void setAlignment(DSSequence _seq, int off, int length){
         object          = _seq;
         if(registration == null) {
           registration = new CSSeqRegistration();
         }
         registration.x1 = off;
         registration.x2 = off + length;
      }
    */
    /**
     * getLength
     *
     * @return int
     */
    /*
    public int getLength() {
        return registration.x2 - registration.x1;
    }
*/
    /**
     * getOffset
     *
     * @return int
     */

    /**
     * getPattern
     *
     * @return IGetPattern
     */
    //public IGetPattern getPattern() {
    //    return pattern;
    //}

}
