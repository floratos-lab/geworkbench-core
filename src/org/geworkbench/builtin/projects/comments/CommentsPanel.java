package org.geworkbench.builtin.projects.comments;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectTreeNode;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ProjectEvent;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * 
 * @author First Genetic Trust
 * @version $Id$
 */
@AcceptTypes({ DSDataSet.class })
public class CommentsPanel implements VisualPlugin {

	static Log log = LogFactory.getLog(CommentsPanel.class);

	/**
	 * Text to display when there are no user comments entered.
	 */
	private final String DEFAULT_MESSAGE = "";

	private JTextArea commentsTextArea = new JTextArea(DEFAULT_MESSAGE);
	private JPanel commentsPanel = new JPanel();

	public CommentsPanel() {
		commentsPanel.setLayout(new BorderLayout());
		commentsTextArea.setLineWrap(true);
		commentsTextArea.setWrapStyleWord(true);
		commentsTextArea.getDocument().addDocumentListener(
				new DocumentListener() {
					public void insertUpdate(DocumentEvent e) {
						commentModified_actionPerformed(e);
					}

					public void removeUpdate(DocumentEvent e) {
						commentModified_actionPerformed(e);
					}

					public void changedUpdate(DocumentEvent e) {
						commentModified_actionPerformed(e);
					}

				});
		JScrollPane jScrollPane1 = new JScrollPane();
		commentsPanel.add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.getViewport().add(commentsTextArea, null);
	}

	public Component getComponent() {
		return commentsPanel;
	}

	private void commentModified_actionPerformed(DocumentEvent e) {
		if (e == null || e.getDocument() != commentsTextArea.getDocument()) {
			return;
		}

		if (!locked) { // if ProjectPanel is making the change, don't set the change back
			String userComments = commentsTextArea.getText().trim();
			ProjectPanel.getInstance().setCommentText(userComments);
		}

	}

	private boolean locked = false;
	private ProjectTreeNode currentTreeNode;
	
	@Subscribe
	public void receive(ProjectEvent event, Object source) {

		ProjectTreeNode treeNode = event.getTreeNode();
		if(treeNode==null || treeNode==currentTreeNode) return;
		
		currentTreeNode = treeNode;

		locked = true;
		commentsTextArea.setText(treeNode.getDescription());
		commentsTextArea.setCaretPosition(0);
		locked = false;
	}

}
