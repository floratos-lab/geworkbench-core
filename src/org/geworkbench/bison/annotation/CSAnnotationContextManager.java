package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.apache.commons.collections15.set.ListOrderedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author John Watkinson
 */
public class CSAnnotationContextManager implements DSAnnotationContextManager {

    private static CSAnnotationContextManager instance;

    public static CSAnnotationContextManager getInstance() {
        if (instance == null) {
            instance = new CSAnnotationContextManager();
        }
        return instance;
    }

    public static final String DEFAULT_CONTEXT_NAME = "Default";

    private Map<DSItemList, ListOrderedSet<DSAnnotationContext>> contextMap;

    public CSAnnotationContextManager() {
        contextMap = new HashMap<DSItemList, ListOrderedSet<DSAnnotationContext>>();
    }

    public <T extends DSNamed> DSAnnotationContext<T>[] getAllContexts(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return new DSAnnotationContext[0];
        } else {
            return contexts.toArray(new DSAnnotationContext[0]);
        }
    }

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, String name) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        for (Iterator<DSAnnotationContext> iterator = contexts.iterator(); iterator.hasNext();) {
            DSAnnotationContext context = iterator.next();
            if (name.equals(context.getName())) {
                return context;
            }
        }
        return null;
    }

    public <T extends DSNamed> DSAnnotationContext<T> createContext(DSItemList<T> itemList, String name) {
        CSAnnotationContext<T> context = new CSAnnotationContext<T>(name, itemList);
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            contexts = new ListOrderedSet<DSAnnotationContext>();
            contextMap.put(itemList, contexts);
        }
        contexts.add(context);
        return context;
    }

    public <T extends DSNamed> int getNumberOfContexts(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return 0;
        } else {
            return contexts.size();
        }
    }

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, int index) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            throw new ArrayIndexOutOfBoundsException("Attempt to index an empty context.");
        } else {
            return contexts.get(index);
        }
    }

    public boolean removeContext(DSItemList itemList, String name) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return false;
        } else {
            DSAnnotationContext context = getContext(itemList, name);
            if (context == null) {
                return false;
            } else {
                return contexts.remove(context);
            }
        }
    }

    public boolean renameContext(DSItemList itemList, String oldName, String newName) {
        DSAnnotationContext context = getContext(itemList, oldName);
        if (context == null) {
            throw new NullPointerException("Context not found: " + oldName);
        } else {
            context.setName(newName);
            return !oldName.equals(newName);
        }
    }

    public <T extends DSBioObject> DSAnnotationContext<T> getDefaultContext(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            contexts = new ListOrderedSet<DSAnnotationContext>();
            contextMap.put(itemList, contexts);
        }
        if (contexts.isEmpty()) {
            // Create a default context
            CSAnnotationContext<T> context = new CSAnnotationContext<T>(DEFAULT_CONTEXT_NAME, itemList);
            contexts.add(context);
        }
        return contexts.get(0);
    }
}
