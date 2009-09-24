package org.geworkbench.builtin.projects;

import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a node for saving to disk.
 *
 * @author John Watkinson
 */
public class DataSetSaveNode implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2094279433020194290L;
    private String name;
    private DSDataSet dataSet;
    private String visualSelected;
    private String commandSelected;
    private String selectionSelected;
    private ArrayList<DataSetSaveNode> children;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Include the criteria info if there is any
        if (dataSet != null) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            CSAnnotationContextManager.SerializableContexts contexts = manager.getContextsForSerialization(dataSet);
            out.writeObject(contexts);
            if (dataSet instanceof DSMicroarraySet) {
                DSMicroarraySet set = (DSMicroarraySet) dataSet;
                CSAnnotationContextManager.SerializableContexts markerContexts = manager.getContextsForSerialization(set.getMarkers());
                out.writeObject(markerContexts);
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (dataSet != null) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            manager.setContextsFromSerializedObject(dataSet, (CSAnnotationContextManager.SerializableContexts) in.readObject());
            if (dataSet instanceof DSMicroarraySet) {
                DSMicroarraySet set = (DSMicroarraySet) dataSet;
                manager.setContextsFromSerializedObject(set.getMarkers(), (CSAnnotationContextManager.SerializableContexts) in.readObject());
            }
        }
    }

    public DataSetSaveNode(String name) {
        this.name = name;
        dataSet = null;
        children = new ArrayList<DataSetSaveNode>();
    }

    public DataSetSaveNode(DSDataSet dataSet) {
        this.dataSet = dataSet;
        this.name = dataSet.getLabel();
        children = new ArrayList<DataSetSaveNode>();
    }

    public String getName() {
        return name;
    }

    public DSDataSet getDataSet() {
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
}
