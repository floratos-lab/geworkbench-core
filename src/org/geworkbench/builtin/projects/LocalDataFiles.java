package org.geworkbench.builtin.projects;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.builtin.projects.comments.CommentsPanel;
import org.geworkbench.builtin.projects.experimentinformation.ExperimentInformationPanel;
import org.geworkbench.builtin.projects.history.HistoryPanel;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.ComponentRegistry;

/**
 * 
 * 
 * @author zji
 * @version $Id: InformationPanel.java 8369 2011-10-06 15:38:33Z zji $
 * 
 */
@AcceptTypes({ DSDataSet.class })
public class LocalDataFiles extends JPanel implements VisualPlugin {

	private static final long serialVersionUID = -4544518025307954674L;

	public LocalDataFiles() {

	
	}

	public Component getComponent() {
		return this;
	}

}
