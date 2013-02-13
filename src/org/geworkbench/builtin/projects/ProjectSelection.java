package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.events.ProjectEvent;

/**
 * What is selected in project panel.
 *
 * @version $Id$
 */

public class ProjectSelection {

    private DataSetNode selectedDataSetNode = null;
    private DataSetSubNode selectedDataSetSubNode = null;
    private ProjectTreeNode selectedNode = null;

    /**
     * Clears the selections and broadcasts the event
     */
    public void clearNodeSelections() {
        selectedDataSetNode = null;
        selectedDataSetSubNode = null;
        selectedNode = null;
    }

    public DataSetNode getSelectedDataSetNode() {
        return selectedDataSetNode;
    }

    public DataSetSubNode getSelectedDataSetSubNode() {
        return selectedDataSetSubNode;
    }

    public ProjectTreeNode getSelectedNode() {
        return selectedNode;
    }

    @SuppressWarnings("rawtypes")
	public DSDataSet getDataSet() {
        if (selectedDataSetNode != null) {
            return selectedDataSetNode.getDataset();
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
	public DSAncillaryDataSet getDataSubSet() {
        if (selectedDataSetSubNode != null) {
            return selectedDataSetSubNode._aDataSet;
        } else {
            return null;
        }
    }

    /**
     * This is useful only because the original design used to support complex tree. Such support is in fact dropped for now. 
     */
    private DataSetNode getParentDataSet(ProjectTreeNode node) {
        if (node == null) {
            return null;
        }
        while (!(node instanceof DataSetNode)) {
            node = (ProjectTreeNode) node.getParent();
            if (node == null) {
                return null;
            }
        }
        return (DataSetNode) node;
    }

    /**
     * Assigns a new selection node.
     *
     */
    @SuppressWarnings("unchecked")
	public void setNodeSelection(ProjectTreeNode node) {
        if (selectedNode != node) {
            selectedNode = node;

            if (node instanceof DataSetNode) {
                selectedDataSetNode = (DataSetNode) node;
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetNode.getDataset());
        		if (selectedDataSetSubNode != null
        				&& selectedDataSetSubNode.getParent() != selectedDataSetNode) {
        			selectedDataSetSubNode = null;
        		}
				ProjectPanel.getInstance().publishProjectEvent(
						new ProjectEvent(selectedDataSetNode.getDataset(),
								selectedDataSetNode));
            } else if (node instanceof DataSetSubNode) {
                selectedDataSetSubNode = (DataSetSubNode) node;
                selectedDataSetNode = getParentDataSet(selectedDataSetSubNode);
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());//Fix bug 1471
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetSubNode._aDataSet);
				ProjectPanel.getInstance().publishProjectEvent(
						new ProjectEvent(selectedDataSetSubNode._aDataSet,
								selectedDataSetSubNode));
            } else if (node instanceof PendingTreeNode) {
                selectedDataSetNode = getParentDataSet(node);
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());
                GeawConfigObject.getGuiWindow().setVisualizationType(null);
                selectedDataSetSubNode = null;
				ProjectPanel.getInstance().publishProjectEvent(
						new ProjectEvent(selectedDataSetNode.getDataset(),
								selectedDataSetNode));
            } else { // the case of root node
                selectedDataSetNode = null;
                selectedDataSetSubNode = null;
				ProjectPanel.getInstance().publishProjectEvent(
						new ProjectEvent(null, null));
                GeawConfigObject.getGuiWindow().setVisualizationType(null);                
            }            
        }
    }

}
