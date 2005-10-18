package org.geworkbench.bison.datastructure.bioobjects.sequence;

import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import javax.swing.*;
import java.util.RandomAccess;
import java.io.Serializable;

/**
 * @author John Watkinson
 */
public interface DSAlignmentResultSet extends RandomAccess, Cloneable, DSAncillaryDataSet, Serializable {
    void setIcon(ImageIcon icon);

    String getResultFilePath();
}
