package org.geworkbench.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.builtin.projects.ProjectTreeNode;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id$
 */
public class ProjectEvent {

	static Log log = LogFactory.getLog(ProjectEvent.class);
	
    private final Message value;
    private final DSDataSet<? extends DSBioObject> dataSet;
    private final ProjectTreeNode node;

    public enum Message {CLEAR, SELECT, CCM_UPDATE};

    public ProjectEvent(final Message value, final DSDataSet<? extends DSBioObject> dataSet, final ProjectTreeNode node) {
        this.value = value;
        this.dataSet = dataSet;
        this.node = node;
    }

    public Message getValue() {
    	return value;
    }

	public DSDataSet<? extends DSBioObject> getDataSet() {
        return dataSet;
    }

    public ProjectTreeNode getTreeNode() { return node; }

}
