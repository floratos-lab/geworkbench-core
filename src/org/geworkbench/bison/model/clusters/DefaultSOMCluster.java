package org.geworkbench.bison.model.clusters;

import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generalization of <code>Cluster</code> to characterize Clusters obtained
 * from the Self Organizing Map Analysis
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class DefaultSOMCluster extends AbstractCluster implements SOMCluster, Serializable {
	private static final long serialVersionUID = 2894691282983664326L;
    /**
     * The X Grid coordinate of the Grid this <code>SOMCluster</code> represents
     */
    protected int x = -1;
    /**
     * The Y Grid coordinate of the Grid this <code>SOMCluster</code> represents
     */
    protected int y = -1;

    /**
     * Sets the Grid Coordinates of the Grid this <code>SOMCluster</code>
     * represents
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setGridCoordinates(int x, int y) {
        if (x < 0 || y < 0)
            return;
        this.x = x;
        this.y = y;
    }

    /**
     * Clear the Grid Coordinates
     */
    public void clearGridCoordinates() {
        x = y = -1;
    }

    /**
     * Gets the X Coordinate of the Grid this <code>SOMCluster</code> represents
     *
     * @return x coordinate
     */
    public int getXCoordinate() {
        return x;
    }

    /**
     * Gets the Y Coordinate of the Grid this <code>SOMCluster</code> represents
     *
     * @return y coordinate
     */
    public int getYCoordinate() {
        return y;
    }

}