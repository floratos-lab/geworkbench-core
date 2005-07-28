package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

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
 * @author not attributable
 * @version 3.0
 */
public class CSMicroarrayVector extends ArrayList<DSMicroarray> implements DSItemList<DSMicroarray> {

    protected HashMap<String, DSMicroarray> microarrayByName = new HashMap<String, DSMicroarray>();
    protected TreeSet orderedMarkers = new TreeSet();

    public CSMicroarrayVector() {
    }

    /**
     * must overload add
     *
     * @param item Microarray
     */
    public boolean add(DSMicroarray item) {
        // Store it indexed by the name
        microarrayByName.put(item.getLabel(), item);
        // Add it to the vector
        if (!orderedMarkers.contains(item)) {
            item.setSerial(this.size() - 1);
            orderedMarkers.add(item);
        }
        return this.add(item);
    }

    /**
     * @param maID A presumed id of a microarray.
     * @return The <code>Microarray</code> object from within this microarray
     *         set that has the prescribed id, if such a microarray exists.
     *         <code>null</code> otherwise.
     */
    public DSMicroarray get(String maID) {
        return microarrayByName.get(maID);
    }

    public String getID() {
        return "";
    }

    public void setID(String id) {
    }

    public DSMicroarray get(DSMicroarray item) {
        return null;
    }

    public void rename(DSMicroarray dsMicroarray, String label) {
        // Just relabel,  no need to update internal structure.
        dsMicroarray.setLabel(label);
    }
}
