package org.geworkbench.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Progress listener for bounded progress item
 * $Id$
 */

public class BoundedProgressListener implements PropertyChangeListener {
	private ProgressItem pi;

	@SuppressWarnings("unused")
	private BoundedProgressListener(){}

	public BoundedProgressListener(ProgressItem item){
		pi = item;
	}

	public void propertyChange(PropertyChangeEvent evt){
		if("progress" == evt.getPropertyName()) {
			int progress = (Integer)evt.getNewValue();
			pi.updateTo(progress);
		}
	}
}