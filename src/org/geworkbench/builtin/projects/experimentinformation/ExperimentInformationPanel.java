package org.geworkbench.builtin.projects.experimentinformation;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ProjectEvent;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version $Id$
 */

/**
 * This application component is responsible for displaying the exepriment
 * information (if any) associated with a microarray set.
 */
@AcceptTypes({DSDataSet.class}) 
public class ExperimentInformationPanel implements VisualPlugin {

    /**
     * Text to display when there are no user comments entered.
     */
    private final String DEFAULT_MESSAGE = "";

    private JTextArea experimentTextArea = new JTextArea(DEFAULT_MESSAGE);

    private JPanel experimentPanel = new JPanel();

    public String getName() {
        return "Experiment Info";
    }

    public ExperimentInformationPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        experimentPanel.setLayout(new BorderLayout());
        experimentTextArea.setText(DEFAULT_MESSAGE);
        experimentTextArea.setLineWrap(true);
        experimentTextArea.setWrapStyleWord(true);
        experimentTextArea.setEditable(false);
        JScrollPane jScrollPane1 = new JScrollPane();
        experimentPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(experimentTextArea, null);
    }

    public Component getComponent() {
        return experimentPanel;
    }

    /**
     * Application listener for receiving events that modify the currently
     * selected microarray set.
     *
     * @param e
     */
    @Subscribe public void receive(ProjectEvent e, Object source) {
        DSDataSet<?> dataSet = e.getDataSet();
        if (dataSet != null) {
        	String experimentInfo = DEFAULT_MESSAGE;
            if (!e.getMessage().equals(ProjectEvent.CLEARED)) {
                String description = dataSet.getDescription();
                if (description != null && description.length() > 0)
                    experimentInfo += description + "\n";
                else
                    experimentInfo = DEFAULT_MESSAGE;
            }
            experimentTextArea.setText(experimentInfo);
            experimentTextArea.setCaretPosition(0); // For long text.
        }
    }
}
