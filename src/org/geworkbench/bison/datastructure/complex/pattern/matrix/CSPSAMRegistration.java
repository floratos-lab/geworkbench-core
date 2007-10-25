package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqRegistration;

/**
 * @author manjunath at genomecenter dot columbia dot edu
 */
public class CSPSAMRegistration extends CSSeqRegistration{
    
    /** Creates a new instance of CSPSAMRegistration */
    public CSPSAMRegistration(Organism org, String a, String chr, int x1, int x2, int s, float pv) {
        organism = org;
        assembly = a;
        chromosome = chr;
        this.x1 = x1;
        this.x2 = x2;
        strand = s;
        pValue = pv;
    }

    public enum Organism {HUMAN, MOUSE, RAT};

    public Organism organism = Organism.HUMAN;
    
    public String assembly = "hg18";

    public String chromosome = "1";
}
