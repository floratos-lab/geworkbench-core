package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.builtin.projects.MicroarraySetNode;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>ProjectNodeOld</code> node which represents a 'Project' node in the Project
 * panel component
 * @todo Phase out and combine with ProjectNode.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ProjectNodeOld extends ProjectTreeNode implements Serializable {
    /**
     * To keep a track of the current <code>MicroarraySetNode</code> selection
     */
    public class JMAProject implements Serializable {
        MicroarraySetNode microarraySetSelection = null;
    }

    /**
     * Keeps a track of the current <code>MicroarraySetNode</code> selection
     */
    JMAProject project = new JMAProject();
    /**
     * current <code>MicroarraySetNode</code> selection
     */
    MicroarraySetNode microarraySetNodeSelection = null;

    /**
     * Constructor
     *
     * @param nodeName <code>Object</code> to be set as user object
     */
    public ProjectNodeOld(Object nodeName) {
        setUserObject(nodeName);
    }

    /**
     * Gets the <code>MicroarraySet</code> in the current
     * <code>MicroarraySetNode</code> selection
     *
     * @return <code>MicroarraySet</code> in the current
     *         <code>MicroarraySetNode</code> selection
     */
    public DSMicroarraySet getSelectedMicroarray() {
        return microarraySetNodeSelection.getMicroarraySet();
    }

    /**
     * Adds a node a s child of this <code>ProjectNodeOld</code>
     *
     * @param node   <code>MicroarraySetNode</code> to be added as a child
     * @param object <code>MicroarraySet</code> to be set as user object of
     *               <code>node</code>
     */
    public void addNode(TreeNode node, Object object) {
        MicroarraySetNode maNode = new MicroarraySetNode((DSMicroarraySet) object);
        int childCount = microarraySetNodeSelection.getChildCount();
        System.out.println(childCount);
        try {
            microarraySetNodeSelection.insert((MutableTreeNode) node, childCount);
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            aiobe.printStackTrace();
        }

    }

}

