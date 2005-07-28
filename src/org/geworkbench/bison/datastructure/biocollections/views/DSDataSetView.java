package org.geworkbench.bison.datastructure.biocollections.views;


import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */

public interface DSDataSetView <Q extends DSBioObject> {

    public int size();

    public Q get(int index);


    /**
     * @return A DSItemList containing all the <code>Q</code> type objects (generally microarrays)
     *         associated with this <code>DSDataView</code>.
     */
    public DSItemList<Q> items();

    /**
     * Set/reset microarray subselection based on activated phenotypes.
     *
     * @param status
     */
    public void useItemPanel(boolean status);

    /**
     * Gets the statuc of Phenotype Activation
     *
     * @return
     */
    public boolean useItemPanel();

    /**
     * Allows to assign a specific microarray panel selection
     *
     * @param mArrayPanel DSPanel
     */
    public void setItemPanel(DSPanel<Q> mArrayPanel);

    /**
     * Allows to assign a specific microarray panel selection
     */
    public DSPanel<Q> getItemPanel();

    /**
     * Sets the reference microarray set for this <code>DSDataSetView</code>.
     *
     * @param ma The new reference microarray set.
     */
    public void setDataSet(DSDataSet<Q> ma);

    /**
     * Get the <code>DSDataSet</code> object underlying this is view
     *
     * @return The reference <code>DSDataSet</code> object.
     */
    public DSDataSet<Q> getDataSet();

}
