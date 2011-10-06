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
 * The new panel that is shown in visualization area and contains the three
 * panels that used to be in command area: CommentsPanel, HistoryPanel, and
 * ExperimentInformationPanel.
 * 
 * @author zji
 * @version $Id$
 * 
 */
@AcceptTypes({ DSDataSet.class })
public class InformationPanel extends JPanel implements VisualPlugin {

	private static final long serialVersionUID = -4544518025307954674L;

	public InformationPanel() {

		JPanel commentPanel = new JPanel();
		commentPanel.setLayout(new BorderLayout());
		commentPanel.add(new JLabel("Dataset Annotation (User Comments)"),
				BorderLayout.NORTH);
		Object objComments = ComponentRegistry.getRegistry()
				.getDescriptorForPluginClass(CommentsPanel.class).getPlugin();
		CommentsPanel comments = (CommentsPanel) objComments;
		commentPanel.add(comments.getComponent(), BorderLayout.CENTER);

		JPanel experimentInformationPanel = new JPanel();
		experimentInformationPanel.setLayout(new BorderLayout());
		experimentInformationPanel.add(new JLabel("Experiment Info"),
				BorderLayout.NORTH);
		Object objInfo = ComponentRegistry.getRegistry()
				.getDescriptorForPluginClass(ExperimentInformationPanel.class)
				.getPlugin();
		ExperimentInformationPanel info = (ExperimentInformationPanel) objInfo;
		experimentInformationPanel
				.add(info.getComponent(), BorderLayout.CENTER);

		JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				commentPanel, experimentInformationPanel);
		leftPanel.setOneTouchExpandable(true);
		leftPanel.setResizeWeight(0.5);
//		leftPanel.setDividerLocation(0.5);

		JPanel historyPanel = new JPanel();
		historyPanel.setLayout(new BorderLayout());
		historyPanel.add(new JLabel("Dataset History"), BorderLayout.NORTH);
		Object obj = ComponentRegistry.getRegistry()
				.getDescriptorForPluginClass(HistoryPanel.class).getPlugin();
		HistoryPanel h = (HistoryPanel) obj;
		historyPanel.add(h.getComponent(), BorderLayout.CENTER);

		// Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, historyPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.33);
//		splitPane.setDividerLocation(0.5); // only useful if the split pane is visible

		this.setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);
	}

	public Component getComponent() {
		return this;
	}

}
