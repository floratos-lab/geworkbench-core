package org.geworkbench.bison.datastructure.complex.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.RandomNumberGenerator;

/**
 * A default {@link DSItemList} implementation that is backed by an ArrayList and a HashMap.
 * $Id$
 */
public class CSItemList <T extends DSNamed> extends ArrayList<T> implements DSItemList<T> {
    
    private static final long serialVersionUID = -8885836819661037974L;
    
    private String id = RandomNumberGenerator.getID();
    //use Hashtable to not allow null keys
    private Hashtable<String, T> objectMap = new Hashtable<String, T>();

    /**
     * Gets an item by label, using the HashMap.
     *
     * @param label the label of the item.
     * @return the item found or <code>null</code> if not found.
     */
    @Override
	public T get(String label) {
		if (label == null) {
			return null;
		} else {
			return objectMap.get(label);
		}
	}

    /**
     * Adds a new item to the item list, if it does not already exist.
     *
     * @param item the item to add.
     * @return <code>true</code> always.
     */
	@Override
	public boolean add(T item) {
		if (item == null)
			return false;
		String label = item.getLabel();
		if (label != null && objectMap.containsKey(label))
			return false;

		boolean result = super.add(item);
		if (result && label != null) {
			objectMap.put(label, item);
		}
		return result;
	}

    @Override public boolean addAll(Collection<? extends T> ts) {
        boolean success = true;
        for (Iterator<? extends T> iterator = ts.iterator(); iterator.hasNext();) {
            if (!add(iterator.next())) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Inserts the item at the specified index.
     * watkin - note: this violates the contract for List-- it does not add if index < size(), but replaces!
     * FIXME any use of this evil method should be avoided.
     *
     * @param index the index at which to insert the item.
     * @param item  the item to insert.
     */
    @Override public void add(int index, T item) {
        if (size() > index) {
            super.set(index, item);
        } else {
            super.add(index, item);
        }
        String label = item.getLabel();
        if (label != null) {
            objectMap.put(label, item);
        }
    }

    /**
     * Removes the object from the item list.
     *
     * @param item the item to remove
     * @return <code>true</code> if the item was found and removed, <code>false</code> if it was not found.
     */
    @SuppressWarnings("unchecked")
	@Override public boolean remove(Object item) {
        boolean result = super.remove(item);
        objectMap.remove(((T) item).getLabel());
        return result;
    }

	@Override
	public T set(int index, T element) {
		T old = super.set(index, element);
		if (element.getLabel() != null) {
			objectMap.put(element.getLabel(), element);
		}
		return old;
	}
	
	/*
	 * There are several reason that we don't like this method to be here: it is
	 * only used by CSMarkerVevtor; it has limited used in CSMarkerVevtor in the
	 * first place; it is not part of DSItemList interface; it is a bad idea to
	 * make whatever T mutable. It is still preferable to have it here than
	 * exposing objectMap.
	 */
	public void setLabel(int index, String label) {
		T item = get(index);
		if (item == null)
			return;

		String oldLabel = item.getLabel();
		if (oldLabel != null) {
			objectMap.remove(oldLabel);
		}
		item.setLabel(label);
		objectMap.put(label, item);
	}
	
    /**
     * Gets the ID for this object.
     *
     * @return the id of this item list.
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the ID for this object.
     *
     * @param id the new id for this item list.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Clears the contents of this item list.
     */
    @Override public void clear() {
        super.clear();
        objectMap.clear();
    }

    public boolean equals(Object o) {
        if (o instanceof DSItemList) {
            DSItemList<?> other = (DSItemList<?>) o;
            return id.equals(other.getID());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return id.hashCode();
    }
}
