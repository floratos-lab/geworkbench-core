package org.geworkbench.bison.datastructure.biocollections.gsea;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * Created by IntelliJ IDEA.
 * User: nazaire
 * @version $Id$
 */
public interface DSGSEAResultDataSet extends DSAncillaryDataSet<DSBioObject> 
{
	public abstract String getReportFile();

}
