package org.geworkbench.builtin.projects;

import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a node for saving to disk.
 *
 * @author John Watkinson
 * @version $Id$
 */
public class DataSetSaveNode implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2094279433020194290L;
    private String name;
    private String description = "";
    private DSDataSet<? extends DSBioObject> dataSet;
    private String visualSelected;
    private String commandSelected;
    private String selectionSelected;
    private ArrayList<DataSetSaveNode> children;

    @SuppressWarnings("unchecked")
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Include the criteria info if there is any
        if (dataSet != null) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            CSAnnotationContextManager.SerializableContexts contexts = manager.getContextsForSerialization(dataSet);
            out.writeObject(contexts);
            if (dataSet instanceof DSMicroarraySet) {
                DSMicroarraySet<DSMicroarray> set = (DSMicroarraySet<DSMicroarray>) dataSet;
                CSAnnotationContextManager.SerializableContexts markerContexts = manager.getContextsForSerialization(set.getMarkers());
                out.writeObject(markerContexts);
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (dataSet != null) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            manager.setContextsFromSerializedObject(dataSet, (CSAnnotationContextManager.SerializableContexts) in.readObject());
            if (dataSet instanceof DSMicroarraySet) {
                DSMicroarraySet<DSMicroarray> set = (DSMicroarraySet<DSMicroarray>) dataSet;
                manager.setContextsFromSerializedObject(set.getMarkers(), (CSAnnotationContextManager.SerializableContexts) in.readObject());
            }
        }
    }

    public DataSetSaveNode(String name) {
        this.name = name;
        dataSet = null;
        children = new ArrayList<DataSetSaveNode>();
    }

    public DataSetSaveNode(DSDataSet<? extends DSBioObject> dataSet) {
        this.dataSet = dataSet;
        this.name = dataSet.getLabel();
        children = new ArrayList<DataSetSaveNode>();
    }

    public String getName() {
        return name;
    }

    public DSDataSet<? extends DSBioObject> getDataSet() {
        return dataSet;
    }

    public List<DataSetSaveNode> getChildren() {
        return children;
    }

    public void addChild(DataSetSaveNode node) {
        children.add(node);
    }

    public String getVisualSelected() {
        return visualSelected;
    }

    public void setVisualSelected(String visualSelected) {
        this.visualSelected = visualSelected;
    }

    public String getCommandSelected() {
        return commandSelected;
    }

    public void setCommandSelected(String commandSelected) {
        this.commandSelected = commandSelected;
    }

    public String getSelectionSelected() {
        return selectionSelected;
    }

    public void setSelectionSelected(String selectionSelected) {
        this.selectionSelected = selectionSelected;
    }
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
