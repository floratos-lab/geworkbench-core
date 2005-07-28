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

public interface IGetSequence {
    Object getToken(int i) throws ArrayIndexOutOfBoundsException;

    char getChar(int i) throws ArrayIndexOutOfBoundsException;

    int getLength();
}
