package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.structure.CSProteinStructure;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.ProjectEvent.Message;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
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
     * Finds the project node associated with this node of the given class
     *
     * @param parentPath
     * @return
     */
    private ProjectTreeNode getNodeOfClass(ProjectTreeNode node, Class<?> aClass) {
        if (node == null) {
            return null;
        }
        while (!(aClass.isInstance(node))) {
            node = (ProjectTreeNode) node.getParent();
            if (node == null) {
                return null;
            }
        }
        return (ProjectTreeNode) node;
    }

    /**
     * Assigns a new selection node
     *
     * @param pNode
     * @param node
     */
    @SuppressWarnings("unchecked")
	public void setNodeSelection(ProjectTreeNode node) {
        if (selectedNode != node) {
            selectedNode = node;

            if (node instanceof DataSetNode) {
                selectedDataSetNode = (DataSetNode) node;
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetNode.getDataset());
                checkDataSetNode();
                if(selectedDataSetNode.getDataset() instanceof CSProteinStructure){
                	throwEvent(ProjectEvent.Message.CLEAR);
                }
                else throwEvent(ProjectEvent.Message.SELECT);
            } else if (node instanceof DataSetSubNode) {
                selectedDataSetSubNode = (DataSetSubNode) node;
                selectedDataSetNode = (DataSetNode) getNodeOfClass(node, DataSetNode.class);
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());//Fix bug 1471
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetSubNode._aDataSet);
                checkDataSetNode();
				if (selectedDataSetSubNode._aDataSet != null) {
					ProjectPanel.getInstance().publishProjectEvent(
							new ProjectEvent(Message.SELECT,
									selectedDataSetSubNode._aDataSet,
									selectedDataSetSubNode));
				}
            } else if (node instanceof PendingTreeNode) {
                selectedDataSetNode = (DataSetNode) getNodeOfClass(node, DataSetNode.class);
                AnnotationParser.setCurrentDataSet(selectedDataSetNode.getDataset());
                GeawConfigObject.getGuiWindow().setVisualizationType(null);
                checkDataSetNode();
                throwEvent(ProjectEvent.Message.SELECT);
            } else  {
                selectedDataSetNode = null;
                selectedDataSetSubNode = null;
                GeawConfigObject.getGuiWindow().setVisualizationType(null);                
                checkDataSetNode();
				ProjectPanel.getInstance().publishProjectEvent(
						new ProjectEvent(Message.CLEAR, null, null));
            }            
        }
    }

    /**
     * Checks that the selections are correctly assigned at the DataSet level
     */
    private void checkDataSetNode() {
        if (selectedDataSetNode == null) {
            selectedDataSetSubNode = null;
        } else {
            checkDataSetSubNode();
        }
    }

    /**
     * Checks that the selections are correctly assigned at the DataSetSub level
     */
    private void checkDataSetSubNode() {
        if (selectedDataSetSubNode != null) {
            if (selectedDataSetSubNode.getParent() != selectedDataSetNode) {
                selectedDataSetSubNode = null;
            }
        }
    }

	private void throwEvent(Message value) {
		if (selectedDataSetNode == null)
			return; // this should not happen

		ProjectPanel.getInstance().publishProjectEvent(
				new ProjectEvent(value, selectedDataSetNode.getDataset(),
						selectedDataSetNode));
	}
}
