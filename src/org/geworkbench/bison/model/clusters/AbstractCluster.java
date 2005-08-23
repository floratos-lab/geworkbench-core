package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.util.DefaultIdentifiable;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Default implementation of the <code>Cluster</code> interface.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public abstract class AbstractCluster implements Cluster {
    /**
     * Holds all children of this <code>Cluster</code>
     */
    protected Vector children = new Vector();
    /**
     * Reference to <code>Cluster</code> which contains this <code>Cluster</code>
     * as a child
     */
    protected Cluster parent = null;
    /**
     * {@link java.util.Iterator} to walk through all the children of this
     * <code>Cluster</code>
     */
    protected Iterator iterator = null;
    /**
     * Unique ID acting as a key for this <code>Cluster</code>
     */
    protected org.geworkbench.bison.util.DefaultIdentifiable clusterID = new DefaultIdentifiable();

    /**
     * Gets the <code>Cluster</code> which contains this <code>Cluster</code> as
     * a child. Returns <code>null</code> is this node is the root.
     *
     * @return parent
     */
    public Cluster getParent() {
        return parent;
    }

    /**
     * Adds a <code>Cluster</code> node as a child of this <code>Cluster</code>
     *
     * @param newCluster added as child
     */
    public void addNode(Cluster newCluster) {
        if (newCluster != null) {
            children.add(newCluster);
            ((AbstractCluster) newCluster).parent = this;
        }

    }

    /**
     * Specifes if this <code>Cluster</code> is a leaf. i.e. this <code>Cluster
     * </code> does not contain any children
     *
     * @return if this node is a leaf
     */
    public boolean isLeaf() {
        return (children.size() == 0 ? true : false);
    }

    /**
     * Gets all the children <code>Cluster</code> nodes of this node
     *
     * @return children nodes as an array
     */
    public Cluster[] getChildrenNodes() {
        Cluster[] toBeReturned = (Cluster[]) Array.newInstance(this.getClass(), children.size());
        if (children.size() == 0)
            return null;
        children.toArray(toBeReturned);
        return toBeReturned;
    }

    /**
     * Gets the number of children of this <code>Cluster</code>
     *
     * @return number of children
     */
    public int getNodesCount() {
        return children.size();
    }

    /**
     * Gets all the children of this <code>Cluster</code> that are also leaves
     *
     * @return all the leaf children as an array
     */
    public Cluster[] getLeafChildren() {
        Vector tempClusters = new Vector();
        Cluster[] tmpCluster = null;
        if (!isLeaf()) {
            for (int i = 0; i < children.size(); ++i) {
                tmpCluster = ((Cluster) children.get(i)).getLeafChildren();
                for (int j = 0; j < tmpCluster.length; j++)
                    tempClusters.add(tmpCluster[j]);
            }

        } else
            tempClusters.add(this);
        Cluster[] toBeReturned = (Cluster[]) Array.newInstance(this.getClass(), tempClusters.size());
        tempClusters.toArray(toBeReturned);
        return toBeReturned;
    }

    /**
     * Gets the number of children of this <code>Cluster</code> that are also
     * leaves
     *
     * @return number of leaf children
     */
    public int getLeafChildrenCount() {
        int leafCount = 0;
        if (!isLeaf()) {
            for (int i = 0; i < children.size(); ++i)
                leafCount += ((Cluster) children.get(i)).getLeafChildrenCount();
        } else
            leafCount = 1;
        return leafCount;
    }

    /**
     * Gets the Unique ID of this <code>Cluster</code>
     *
     * @return unique ID
     */
    public String getID() {
        return clusterID.getID();
    }

    /**
     * Sets the Unique ID of this <code>Cluster</code>
     *
     * @param id unique ID
     */
    public void setID(String id) {
        clusterID.setID(id, "Cluster");
    }

    /**
     * Gets the name assigned to this <code>Cluster</code>
     *
     * @return name
     */
    public String getLabel() {
        return clusterID.getLabel();
    }

    /**
     * Sets the name of this <code>Cluster</code>
     *
     * @param name name of this node
     */
    public void setLabel(String name) {
        clusterID.setLabel(name);
    }

    /**
     * {@link org.geworkbench.bison.model.Microarray} method to walk thorugh all the children of
     * this <code>Cluster</code>
     *
     * @return if more children can be accessed
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * {@link org.geworkbench.bison.model.Microarray} method to walk thorugh all the children of
     * this <code>Cluster</code>
     *
     * @return next child
     */
    public Object next() {
        return iterator.next();
    }

    /**
     * {@link org.geworkbench.bison.model.Microarray} method to remove the last <code>Cluster
     * </code> accessed by the {@link AbstractCluster#next} method
     */
    public void remove() {
        iterator.remove();
    }

    /**
     * Removes a <code>Cluster</code> if it is one of the children of this
     * <code>Cluster</code>
     *
     * @param obj child to be removed
     * @return if the object was removed
     */
    public boolean remove(Object obj) {
        if (obj == null || !Cluster.class.isAssignableFrom(obj.getClass()))
            return false;
        else {
            boolean status = children.remove(obj);
            if (status)
                reset();
            return status;
        }

    }

    /**
     * All the children are removed
     */
    public void removeAll() {
        children.clear();
        reset();
    }

    /**
     * Sets the {@link java.util.Iterator} to a state from where it can be used to
     * walk through all the children again
     */
    public void reset() {
        iterator = children.iterator();
    }

}
