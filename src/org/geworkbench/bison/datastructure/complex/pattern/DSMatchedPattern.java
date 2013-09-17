package org.geworkbench.bison.datastructure.complex.pattern;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.DSPValue;

import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version $Id$
 */
public interface DSMatchedPattern <T,R> extends DSPValue, DSNamed {
    public int getSupport();

    public int getUniqueSupport();

    public DSPatternMatch<T, R> get(int i);

    public List<DSPatternMatch<T, R>> matches();

}
