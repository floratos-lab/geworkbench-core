package org.geworkbench.bison.model.clusters;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Models an SOM cluster. Each cluster is characterized by 2 quantities:
 * (1) Representative: this is a n-dimensional tuple of real numbers, where
 * n is the number of microarrays within the microarray set.
 * (2) Grid coordinates: this is the 2-dimensional coordiantes of this cluster
 * within the original SOM grid.
 * <p/>
 * The cluster contains all markers (<code>MarkerInfo</code> objects) from
 * the microarray set whose expression profile vector is closest to the
 * represeantative of this cluster rather than any other representative
 * (proximity is measured using the distance metric defined in setting the
 * parameters for the SOM algorithm).
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public interface SOMCluster extends Cluster {
    /**
     * Sets the grid coordinates for this cluster.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setGridCoordinates(int x, int y);

    /**
     * Clear the grid coordinateds for this cluster.
     */
    public void clearGridCoordinates();

    /**
     * Gets the x coordinate for this cluster
     *
     * @return The x-coordinate for this cluster (or -1, if no coordinate is available).
     */
    public int getXCoordinate();

    /**
     * Gets the y coordinate for this cluster
     *
     * @return The y-coordinate for this cluster (or -1, if no coordinate is available).
     */
    public int getYCoordinate();
}