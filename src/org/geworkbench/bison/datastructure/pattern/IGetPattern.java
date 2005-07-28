package org.geworkbench.bison.datastructure.pattern;


/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface IGetPattern {
    IGetPatternMatchCollection match(Object object);//may use a hashmap for matching parameters

    String asString();// a string representation of the pattern


}
