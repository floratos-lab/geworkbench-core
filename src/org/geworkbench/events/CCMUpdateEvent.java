package org.geworkbench.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

/**
 * @version $Id$
 */
public class CCMUpdateEvent {

	static Log log = LogFactory.getLog(CCMUpdateEvent.class);

	@SuppressWarnings("rawtypes")
	private final Class<? extends DSDataSet> dataType;
	
    @SuppressWarnings("rawtypes")
	public CCMUpdateEvent(Class<? extends DSDataSet> dataType) {
    	this.dataType = dataType;
    }

	@SuppressWarnings("rawtypes")
	public Class<? extends DSDataSet> getDataSetType() {
		return dataType;
	}

}
