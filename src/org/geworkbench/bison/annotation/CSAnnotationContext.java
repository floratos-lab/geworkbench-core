package org.geworkbench.bison.annotation;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class CSAnnotationContext<T extends DSNamed> implements DSAnnotationContext<T>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8064184768541031268L;

    public static final String SELECTION = "Selection";

    public static final String CLASS_CASE = "Case";
    public static final String CLASS_CONTROL = "Control";
    public static final String CLASS_TEST = "Test";
    public static final String CLASS_IGNORE = "Ignore";

    /**
     * Initializes a context to have typical values for describing phenotypes.
     */
    public static <T extends DSNamed> void initializePhenotypeContext(DSAnnotationContext<T> context) {
        context.addLabel(SELECTION);
        context.addClass(CLASS_CASE);
        context.addClass(CLASS_CONTROL);
        context.addClass(CLASS_TEST);
        context.addClass(CLASS_IGNORE);
        context.setDefaultClass(CLASS_CONTROL);
    }

    private class Label implements Cloneable, Serializable {
		private static final long serialVersionUID = -2093219495767578519L;
		
		public String name;
        public DSPanel<T> panel;

        public boolean active;

        /**
         * A static label.
         */
        public Label(String name, DSPanel<T> panel) {
            this.name = name;
            this.panel = panel;
            active = false;
        }

        protected Label clone() {
            CSPanel<T> clonedPanel = null;
            Label clone;
            if (panel != null) {
                clonedPanel = new CSPanel<T>(panel);
                clone = new Label(name, clonedPanel);
            } else {
                clone = new Label(name);
            }
            clone.active = active;
            return clone;
        }

        /**
         * A label with null panel.
         */
        public Label(String name) {
            this.name = name;
        }

        public DSPanel<T> getPanel() {
            if (panel != null) {
                return panel;
            } else {
                DSPanel<T> panel = new CSPanel<T>(name);
                return panel;
            }
        }

    }

    private String name;
    /**
     * Stored as a weak reference so that this context can be released by the GC when the item list is GCed.
     *
     * @see CSAnnotationContextManager#contextMap
     */
    private transient WeakReference<DSItemList<T>> itemListReference;

    private ListOrderedMap<String, Label> labels;
    private ListOrderedMap<String, ListOrderedSet<String>> classes;
    private String defaultClass;

    public CSAnnotationContext(String name, DSItemList<T> itemList) {
        this.name = name;
        this.itemListReference = new WeakReference<DSItemList<T>>(itemList);
        labels = new ListOrderedMap<String, Label>();
        classes = new ListOrderedMap<String, ListOrderedSet<String>>();
        defaultClass = null;
    }

    /**
     * Deep clones this context.
     */
    public CSAnnotationContext<T> clone() {
        CSAnnotationContext<T> clone = new CSAnnotationContext<T>(name, itemListReference.get());

        MapIterator<String, Label> labelIterator = labels.mapIterator();
        while (labelIterator.hasNext()) {
            labelIterator.next();
            clone.labels.put(labelIterator.getKey(), labelIterator.getValue().clone());
        }
        MapIterator<String, ListOrderedSet<String>> classIterator = classes.mapIterator();
        while (classIterator.hasNext()) {
            classIterator.next();
            ListOrderedSet<String> cloneSet = new ListOrderedSet<String>();
            cloneSet.addAll(classIterator.getValue());
            clone.classes.put(classIterator.getKey(), cloneSet);
        }
        clone.defaultClass = defaultClass;
        return clone;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public DSItemList<T> getItemList() {
        return itemListReference.get();
    }

    public boolean addLabel(String label) {
        if (labels.get(label) != null) {
            return false;
        } else {
            CSPanel<T> panel = new CSPanel<T>(label);
            labels.put(label, new Label(label, panel));
            return true;
        }
    }

    public boolean removeLabel(String label) {
        return (labels.remove(label) != null);
    }

    public int getNumberOfLabels() {
        return labels.size();
    }

    public String getLabel(int index) {
        if(index<labels.size()){
        return labels.get(index);
        }
        return "label";
    }

    public void setLabelActive(String label, boolean active) {
        Label lab = labels.get(label);
        if (lab != null) {
            lab.active = active;
        }
    }

    public void activateLabel(String label) {
        setLabelActive(label, true);
    }

    public void deactivateLabel(String label) {
        setLabelActive(label, false);
    }

    public boolean isLabelActive(String label) {
        Label lab = labels.get(label);
        if (lab != null) {
            return lab.active;
        } else {
            return false;
        }
    }

    public boolean labelItem(T item, String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            addLabel(label);
            lab = labels.get(label);
        }

        if (lab.panel.contains(item)) {
            return false;
        } else {
            lab.panel.add(item);
            return true;
        }
    }
    
    public boolean labelItems(List<T> items, String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            addLabel(label);
            lab = labels.get(label);
        }

        if((lab.panel != null) && (lab.panel.size() == 0)){
        	for(T item: items){
        		lab.panel.add(item);
        	}
        	return true;
        }
    	for(T item: items){
	        if (lab.panel.contains(item)) {
	            continue;
	        } else {        	
	            lab.panel.add(item);
	        }
        }
        return true;
    }
    
    public boolean labelItems(T[] items, String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            addLabel(label);
            lab = labels.get(label);
        }

        if((lab.panel != null) && (lab.panel.size() == 0)){
        	for(T item: items){
        		lab.panel.add(item);
        	}
        	return true;
        }
    	for(T item: items){
	        if (lab.panel.contains(item)) {
	            continue;
	        } else {        	
	            lab.panel.add(item);
	        }
        }
        return true;
    }

    public DSPanel<T> getActiveItems() {
        CSPanel<T> top = new CSPanel<T>();
        MapIterator<String, Label> iterator = labels.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            Label lab = iterator.getValue();
            if (lab.active) {
                DSPanel<T> labelPanel = lab.getPanel();
                labelPanel.setActive(true);
                // Handle special case of "Selection" label
                if (SELECTION.equals(lab.name)) {
                    DSPanel<T> selection = top.getSelection();
                    for (Iterator<T> itemIterator = labelPanel.iterator(); itemIterator.hasNext();) {
                        selection.add(itemIterator.next());
                    }
                    selection.setActive(true);
                } else {
                    top.panels().add(labelPanel);
                }
            }
        }
        return top;
    }

    public DSPanel<T> getLabelTree() {
        CSPanel<T> top = new CSPanel<T>();
        MapIterator<String, Label> iterator = labels.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            Label lab = iterator.getValue();
            DSPanel<T> labelPanel = lab.getPanel();
            // Handle special case of "Selection" label
            if (SELECTION.equals(lab.name)) {
                DSPanel<T> selection = top.getSelection();
                for (Iterator<T> itemIterator = labelPanel.iterator(); itemIterator.hasNext();) {
                    selection.add(itemIterator.next());
                }
                if (isLabelActive(SELECTION)) {
                    selection.setActive(true);
                } else {
                    selection.setActive(false);
                }
            } else {
                if (isLabelActive(lab.name)) {
                    labelPanel.setActive(true);
                } else {
                    labelPanel.setActive(false);
                }
                top.panels().add(labelPanel);
            }
        }
        return top;
    }

    public DSPanel<T> getItemsWithLabel(String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            return new CSPanel<T>(label);
        } else {
            return lab.getPanel();
        }
    }

    public DSPanel<T> getItemsWithoutLabel(String label) {
        DSPanel<T> retPanel = new CSPanel<T>("Not " + label);
        DSItemList<T> list = getItemList();
        for (T t : list) {
            if (!hasLabel(t, label)) {
                retPanel.add(t);
            }
        }
        return retPanel;
    }

    public boolean labelExists(String label) {
        if (labels.get(label) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasLabel(T item, String label) {
        Label lab = labels.get(label);
        if (lab != null) {
            return lab.panel.contains(item);
        } else {
            return false;
        }
    }

    public int indexOfLabel(String label) {
        return labels.indexOf(label);
    }

    public void clearItemsFromLabel(String label) {
        Label lab = labels.get(label);
        if (lab != null) {
        	lab.panel = new CSPanel<T>(lab.name);
        }
    }

    public boolean renameLabel(String oldName, String newName) {
        Label lab = labels.get(oldName);
        if (lab == null) {
            return false;
        } else {
            lab.name = newName;
            if (lab.panel != null) {
                lab.panel.setLabel(newName);
            }
            // Must build up a new labels map
            ListOrderedMap<String, Label> newLabels = new ListOrderedMap<String, Label>();

            MapIterator<String, Label> iterator = labels.mapIterator();
            while (iterator.hasNext()) {
                iterator.next();
                String label = iterator.getKey();
                if (oldName.equals(label)) {
                    label = newName;
                }
                newLabels.put(label, iterator.getValue());
            }
            labels = newLabels;
            // Move classes from old label to the new one
            String c = getClassForLabel(oldName);
            if (c != null) {
                removeClassFromLabel(oldName);
                assignClassToLabel(newName, c);
            }
            return true;
        }
    }

    public DSPanel<T> getItemsWithAnyLabel(String... labels) {
        CSPanel<T> top = new CSPanel<T>();
        for (int i = 0; i < labels.length; i++) {
            Label lab = this.labels.get(labels[i]);
            if (lab != null) {
                if (SELECTION.equals(lab.name)) {
                    DSPanel<T> selection = top.getSelection();
                    DSPanel<T> labelPanel = lab.getPanel();
                    for (Iterator<T> itemIterator = labelPanel.iterator(); itemIterator.hasNext();) {
                        selection.add(itemIterator.next());
                    }
                } else {
                    top.panels().add(lab.getPanel());
                }
            }
        }
        return top;
    }

    public DSPanel<T> getItemsWithAllLabels(String... labels) {
        CSPanel<T> top = new CSPanel<T>();
        ListOrderedSet<T> set = new ListOrderedSet<T>();
        if (labels.length > 0) {
            Label lab = this.labels.get(labels[0]);
            if (lab != null) {
                set.addAll(lab.getPanel());
            }
        }
        for (int i = 1; i < labels.length; i++) {
            Label lab = this.labels.get(labels[i]);
            if (lab != null) {
                set.retainAll(lab.getPanel());
            }
        }
        for (Iterator<T> iterator = set.iterator(); iterator.hasNext();) {
            top.add(iterator.next());
        }
        return top;
    }

    public String[] getLabelsForItem(T item) {
        MapIterator<String, Label> iterator = labels.mapIterator();
        ArrayList<String> list = new ArrayList<String>();
        while (iterator.hasNext()) {
            iterator.next();
            Label lab = iterator.getValue();

            if (lab.panel.contains(item)) {
                list.add(iterator.getKey());
            }
        }
        return list.toArray(new String[0]);
    }

    public boolean removeLabelFromItem(T item, String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            return false;
        }

        if (lab.panel.contains(item)) {
            lab.panel.remove(item);
            return true;
        } else {
            return false;
        }
    }

    public boolean addClass(String clazz) {
        if (classes.containsKey(clazz)) {
            return false;
        } else {
            ListOrderedSet<String> set = new ListOrderedSet<String>();
            classes.put(clazz, set);
            return true;
        }
    }

    public boolean removeClass(String clazz) {
        boolean result = (classes.remove(clazz) != null);
        if (defaultClass == clazz) {
            defaultClass = null;
        }
        return result;
    }

    public int getNumberOfClasses() {
        return classes.size();
    }


    public boolean isUnsupervised() {
        ListOrderedSet<String> set = classes.get(CLASS_CASE);
        if (set.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String getClass(int index) {
        return classes.get(index);
    }

    public String getDefaultClass() {
        if (defaultClass == null) {
            if (classes.isEmpty()) {
                return null;
            } else {
                return classes.lastKey();
            }
        } else {
            return defaultClass;
        }
    }

    public boolean setDefaultClass(String clazz) {
        if (defaultClass == null) {
            if (clazz == null) {
                return false;
            }
        } else {
            if (defaultClass.equals(clazz)) {
                return false;
            }
        }
        defaultClass = clazz;
        return true;
    }

    public boolean assignClassToLabel(String label, String clazz) {
        // Remove old class
        if (clazz == null) {
            throw new NullPointerException("Class cannot be null.");
        }
        String oldClass = removeClassFromLabel(label);
        ListOrderedSet<String> set = classes.get(clazz);
        if (set == null) {
            addClass(clazz);
            set = classes.get(clazz);
        }
        set.add(label);
        return (!clazz.equals(oldClass));
    }

    public String getClassForLabel(String label) {
        MapIterator<String, ListOrderedSet<String>> iterator = classes.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            ListOrderedSet<String> set = iterator.getValue();
            if (set.contains(label)) {
                return iterator.getKey();
            }
        }
        return getDefaultClass();
    }

    public String getClassForItem(T item) {
        MapIterator<String, ListOrderedSet<String>> iterator = classes.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            ListOrderedSet<String> set = iterator.getValue();
            for (Iterator<String> setIterator = set.iterator(); setIterator.hasNext();) {
                String label = setIterator.next();
                if (hasLabel(item, label)) {
                    return iterator.getKey();
                }
            }
        }
        return getDefaultClass();
    }

    public String[] getLabelsForClass(String clazz) {
        ListOrderedSet<String> set = classes.get(clazz);
        if (clazz.equals(defaultClass)) {
            MapIterator<String, Label> iterator = labels.mapIterator();
            while (iterator.hasNext()) {
                iterator.next();
                if (getClassForLabel(iterator.getKey()).equals(defaultClass)) {
                    set.add(iterator.getKey());
                }
            }
        }
        if (set == null) {
            return new String[0];
        } else {
            return set.toArray(new String[0]);
        }
    }

    public String removeClassFromLabel(String label) {
        MapIterator<String, ListOrderedSet<String>> iterator = classes.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            ListOrderedSet<String> set = iterator.getValue();
            if (set.contains(label)) {
                set.remove(label);
                return iterator.getKey();
            }
        }
        return null;
    }

    public DSPanel<T> getItemsForClass(String clazz) {
        ListOrderedSet<T> items = new ListOrderedSet<T>();
        // Include all unclassified labels as well
        int n = getNumberOfLabels();
        for (int i = 0; i < n; i++) {
            String label = getLabel(i);
            if (clazz.equals(getClassForLabel(label))) {
                items.addAll(getItemsWithLabel(label));
            }
        }
        DSPanel<T> panel = new CSPanel<T>(clazz);
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext();) {
            panel.add(iterator.next());
        }
        // If this is the default class, then also include unlabeled items
        if (clazz.equals(defaultClass)) {
            DSItemList<T> allItems = itemListReference.get();
            for (int i = 0; i < allItems.size(); i++) {
                T item = allItems.get(i);
                if (getLabelsForItem(item).length == 0) {
                    panel.add(item);
                }
            }
        }
        return panel;
    }

    public DSPanel<T> getActivatedItemsForClass(String clazz) {
        ListOrderedSet<T> items = new ListOrderedSet<T>();
        // Include all unclassified labels as well
        int n = getNumberOfLabels();
        for (int i = 0; i < n; i++) {
            String label = getLabel(i);
            if (clazz.equals(getClassForLabel(label)) && isLabelActive(label)) {
                items.addAll(getItemsWithLabel(label));
            }
        }
        DSPanel<T> panel = new CSPanel<T>(clazz);
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext();) {
            panel.add(iterator.next());
        }
        // If this is the default class, then also include unlabeled items, but only if no activated labels of this type
        if (clazz.equals(defaultClass) && panel.size() == 0) {
            DSItemList<T> allItems = itemListReference.get();
            for (int i = 0; i < allItems.size(); i++) {
                T item = allItems.get(i);
                if (getLabelsForItem(item).length == 0) {
                    panel.add(item);
                }
            }
        }
        return panel;
    }

    @SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
        if (obj instanceof DSAnnotationContext) {
            DSAnnotationContext<? extends DSNamed> other = (DSAnnotationContext<? extends DSNamed>) obj;
            if (name == null) {
                return (other.getName() == null);
            } else {
                return name.equals(other.getName());
            }
        }
        return false;
    }

    public int hashCode() {
        if (name == null) {
            return 0;
        } else {
            return name.hashCode();
        }
    }

    public String toString() {
        return name;
    }

}
