package org.geworkbench.bison.datastructure.sequence;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface IGetSequenceCollection {
    String asString();

    int size();

    IGetSequence get(int i) throws ArrayIndexOutOfBoundsException;
}
