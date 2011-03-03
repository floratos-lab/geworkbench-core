package org.geworkbench.builtin.projects.comments;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.CommentsEvent;
 
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id$
 */
@AcceptTypes({DSDataSet.class}) 
public class CommentsPanel implements VisualPlugin {

    static Log log = LogFactory.getLog(CommentsPanel.class);

    /**
     * Text to display when there are no user comments entered.
     */
    private final String DEFAULT_MESSAGE = "";

    private JTextArea commentsTextArea = new JTextArea(DEFAULT_MESSAGE);
    private JPanel commentsPanel = new JPanel();
    private boolean liveMode = true;

    public CommentsPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        commentsPanel.setLayout(new BorderLayout());
        commentsTextArea.setLineWrap(true);
        commentsTextArea.setWrapStyleWord(true);
        commentsTextArea.getDocument().addDocumentListener(new DocumentListener() {
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
        String userComments = commentsTextArea.getText().trim();

        if (liveMode) {
            publishCommentsEvent(new CommentsEvent(userComments));
        }
        
    }

    @Publish public CommentsEvent publishCommentsEvent(CommentsEvent event) {
        return event;
    }

    @Subscribe public void receive(CommentsEvent event, Object source) {
        liveMode = false;
        commentsTextArea.setText(event.getText());
        commentsTextArea.setCaretPosition(0);
        liveMode = true;
    }
  
}
