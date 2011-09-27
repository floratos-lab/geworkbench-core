package org.geworkbench.builtin.projects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.skin.Skin;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class SaveTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2534917305724421302L;

	List<DataSetSaveNode> nodes;
	private DSDataSet<? extends DSBioObject> selected;
	private int wspId=0;
	private boolean dirty = false;
	private String checkout = null;
	private String lastchange = null;

	public SaveTree(ProjectPanel panel, DSDataSet<? extends DSBioObject> selected, int rid, boolean d, String co, String lc) {
		this.selected = selected;
		this.wspId = rid;
		this.dirty = d;
		this.checkout = co;
		this.lastchange = lc;
		nodes = new ArrayList<DataSetSaveNode>();
		selected = panel.getDataSet();
		ProjectTreeNode root = panel.getRoot();
		int n = root.getChildCount();
		for (int i = 0; i < n; i++) {
			ProjectTreeNode project = (ProjectTreeNode) root.getChildAt(i);
			DataSetSaveNode saveProject = new DataSetSaveNode(project
					.toString());
			saveProject.setDescription(project.getDescription());
			nodes.add(saveProject);
			addChildren(project, saveProject);
		}
	}

	static DSDataSet<?> getDSDataSet(ProjectTreeNode treeNode) {
		if (treeNode instanceof DataSetNode) {
			DataSetNode childNode = (DataSetNode) treeNode;
			return childNode.getDataset();
		} else if (treeNode instanceof ImageNode) {
			ImageNode childNode = (ImageNode) treeNode;
			DSDataSet<?> dataSet = childNode._aDataSet;
			if (dataSet.getID() == null)
				dataSet.setID(RandomNumberGenerator.getID());
			return dataSet;
		} else if (treeNode instanceof DataSetSubNode) {
			DataSetSubNode childNode = (DataSetSubNode) treeNode;
			DSDataSet<?> dataSet = childNode._aDataSet;
			if (dataSet.getID() == null)
				dataSet.setID(RandomNumberGenerator.getID());
			return dataSet;
		} else if (treeNode instanceof PendingTreeNode) {
			PendingTreeNode childNode = (PendingTreeNode) treeNode;
			return childNode.getDSDataSet();
		}
		return null; // unexpected;
	}
	
	// Recursively build tree
	private void addChildren(ProjectTreeNode node, DataSetSaveNode saveNode) {
		Skin skin = (Skin) GeawConfigObject.getGuiWindow();
		int n = node.getChildCount();
		for (int i = 0; i < n; i++) {
			ProjectTreeNode treeNode = (ProjectTreeNode) node.getChildAt(i);
			DSDataSet<?> dataSet = getDSDataSet(treeNode);

			DataSetSaveNode childSave = new DataSetSaveNode(dataSet);
			childSave.setCommandSelected(skin.getCommandLastSelected(dataSet));
			childSave.setSelectionSelected(skin
					.getSelectionLastSelected(dataSet));
			childSave.setVisualSelected(skin.getVisualLastSelected(dataSet));
			childSave.setDescription(treeNode.getDescription());
			saveNode.addChild(childSave);
			addChildren(treeNode, childSave);
		}
	}

	public List<DataSetSaveNode> getNodes() {
		return nodes;
	}

	public DSDataSet<? extends DSBioObject> getSelected() {
		return selected;
	}

	public int getWspId(){
		return wspId;
	}
	public boolean getDirty(){
		return dirty;
	}
	public void setDirty(boolean d){
		dirty= d;
	}
	public String getCheckout(){
		return checkout;
	}
	public void setCheckout(String co){
		checkout = co;
	}
	public String getLastchange(){
		return lastchange;
	}
}
