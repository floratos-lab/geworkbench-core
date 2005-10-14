package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.set.ListOrderedSet;

import java.util.*;

/**
 * @author John Watkinson
 */
public class CSAnnotationContext<T extends DSNamed> implements DSAnnotationContext<T> {

    private static final String SELECTION = "Selection";

    private class Label {

        public String name;
        public DSPanel<T> panel;
        public DSCriterion<T> criterion;
        public boolean active;

        /**
         * A static label.
         */
        public Label(String name, DSPanel<T> panel) {
            this.name = name;
            this.panel = panel;
            active = false;
        }

        /**
         * A criterion-based label.
         */
        public Label(String name, DSCriterion<T> criterion) {
            this.name = name;
            this.criterion = criterion;
        }

        public DSPanel<T> getPanel() {
            if (panel != null) {
                return panel;
            } else {
                DSPanel<T> panel = new CSPanel<T>(name);
                for (T t : itemList) {
                    if (criterion.applyCriterion(t, CSAnnotationContext.this)) {
                        panel.add(t);
                    }
                }
                return panel;
            }
        }

        public boolean isCriterion() {
            return (criterion != null);
        }
    }

    private String name;
    private DSItemList<T> itemList;

    private ListOrderedMap<DSAnnotationType, Map<T, ?>> annotations;
    private ListOrderedMap<String, Label> labels;
    private ListOrderedMap<String, ListOrderedSet<String>> classes;
    private String defaultClass;

    public CSAnnotationContext(String name, DSItemList<T> itemList) {
        this.name = name;
        this.itemList = itemList;
        annotations = new ListOrderedMap<DSAnnotationType, Map<T, ?>>();
        labels = new ListOrderedMap<String, Label>();
        classes = new ListOrderedMap<String, ListOrderedSet<String>>();
        defaultClass = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public DSItemList<T> getItemList() {
        return itemList;
    }

    public boolean addAnnotationType(DSAnnotationType annotationType) {
        if (annotations.containsKey(annotationType)) {
            return false;
        } else {
            HashMap<T, Object> map = new HashMap<T, Object>();
            annotations.put(annotationType, map);
            return true;
        }
    }

    public boolean removeAnnotationType(DSAnnotationType annotationType) {
        return (annotations.remove(annotationType) != null);
    }

    public int getNumberOfAnnotationTypes() {
        return annotations.keySet().size();
    }

    public DSAnnotationType getAnnotationType(int index) {
        return annotations.get(index);
    }

    public <Q> void annotateItem(T item, DSAnnotationType<Q> annotationType, Q value) {
        Map<T, Q> map = (Map<T, Q>) annotations.get(annotationType);
        if (map == null) {
            map = new HashMap<T, Q>();
            annotations.put(annotationType, map);
        }
        map.put(item, value);
    }

    public boolean removeAnnotationFromItem(T item, DSAnnotationType annotationType) {
        Map<T, ?> map = annotations.get(annotationType);
        if (map != null) {
            return (map.remove(item) != null);
        } else {
            return false;
        }
    }

    public DSAnnotationType[] getAnnotationTypesForItem(T item) {
        ArrayList<DSAnnotationType> list = new ArrayList<DSAnnotationType>();
        Set<DSAnnotationType> keySet = annotations.keySet();
        for (DSAnnotationType type : keySet) {
            if (annotations.get(type).containsKey(item)) {
                list.add(type);
            }
        }
        return list.toArray(new DSAnnotationType[0]);
    }

    public <Q> Q getAnnotationForItem(T item, DSAnnotationType<Q> annotationType) {
        Map<T, Q> map = (Map<T, Q>) annotations.get(annotationType);
        if (map != null) {
            return map.get(item);
        } else {
            return null;
        }
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

    public boolean addCriterionLabel(String label, DSCriterion<T> criterion) {
        if (labels.get(label) != null) {
            return false;
        } else {
            labels.put(label, new Label(label, criterion));
            return true;
        }
    }

    public boolean removeLabel(String label) {
        return (labels.remove(label) != null);
    }

    public boolean isCriterionLabel(String label) {
        Label lab = labels.get(label);
        if (lab != null) {
            return lab.isCriterion();
        } else {
            return false;
        }
    }

    public DSCriterion<T> getCriterionForLabel(String label) {
        Label lab = labels.get(label);
        if (lab != null) {
            return lab.criterion;
        } else {
            return null;
        }
    }

    public int getNumberOfLabels() {
        return labels.size();
    }

    public String getLabel(int index) {
        return labels.get(index);
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
        if (lab.isCriterion()) {
            throw new IllegalArgumentException("Cannot explicitly label a criterion label.");
        }
        if (lab.panel.contains(item)) {
            return false;
        } else {
            lab.panel.add(item);
            return true;
        }
    }

    public DSPanel<T> getActiveItems() {
        CSPanel<T> top = new CSPanel<T>();
        MapIterator<String, Label> iterator = labels.mapIterator();
        while (iterator.hasNext()) {
            iterator.next();
            Label lab = iterator.getValue();
            if (lab.active) {
                // Handle special case of "Selection" label
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

    public DSPanel<T> getItemsWithLabel(String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            return null;
        } else {
            return lab.getPanel();
        }
    }

    public boolean hasLabel(T item, String label) {
        Label lab = labels.get(label);
        if (lab != null) {
            if (lab.isCriterion()) {
                return lab.criterion.applyCriterion(item, this);
            } else {
                return lab.panel.contains(item);
            }
        } else {
            return false;
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
            if (lab.isCriterion()) {
                if (lab.criterion.applyCriterion(item, this)) {
                    list.add(iterator.getKey());
                }
            } else {
                if (lab.panel.contains(item)) {
                    list.add(iterator.getKey());
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public boolean removeLabelFromItem(T item, String label) {
        Label lab = labels.get(label);
        if (lab == null) {
            return false;
        }
        if (lab.isCriterion()) {
            throw new IllegalArgumentException("Cannot explicitly remove a criterion label.");
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

    public String getClass(int index) {
        return classes.get(index);
    }

    public String getDefaultClass() {
        if (defaultClass == null) {
            return classes.lastKey();
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
        ListOrderedSet<String> set = classes.get(clazz);
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
        return panel;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DSAnnotationContext) {
            DSAnnotationContext other = (DSAnnotationContext) obj;
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
}
