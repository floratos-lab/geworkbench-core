package org.geworkbench.bison.datastructure.bioobjects.microarray.microarrayIO;

import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CSMAParser {
    protected int currGeneId = 0;
    protected int microarrayNo = 0;
    protected int markerNo = 0;

    //total number of properties
    public int propNo = 0;
    protected Vector phenotypes = new Vector();
    protected int phenotypeNo = 0;

    protected int currMicroarrayId = 0;

    public CSMAParser() {
    }
}
