package org.geworkbench.builtin;

import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.algorithm.classification.CSClassifier;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This is the interface for running a previously trained {@link CSClassifier} that now resides in the project panel.
 */
@AcceptTypes({CSClassifier.class})
public class ClassificationRunnerPlugin extends JPanel implements VisualPlugin {
    static Log log = LogFactory.getLog(ClassificationRunnerPlugin.class);

    private DSMicroarraySet microarraySet;
    private JLabel infoLabel;
    private JButton classifyButton = new JButton("Run Classifier");
    private CSClassifier classifier;


    public ClassificationRunnerPlugin() {
        setLayout(new BorderLayout());
        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
        add(internalPanel, BorderLayout.CENTER);
        infoLabel = new JLabel("This classifier will run on activated panels classified as Test.");
        internalPanel.add(infoLabel);
        classifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get data and run classifier on it.

                DSAnnotationContext<DSMicroarray> context = CSAnnotationContextManager.getInstance().getCurrentContext(microarraySet);
                DSPanel<DSMicroarray> panel = context.getItemsForClass(CSAnnotationContext.CLASS_TEST);
                log.debug("Running classifier on " + panel.size() + " test items.");
                String[] classes = classifier.getClassifications();
                int n = classes.length;
                DSPanel<DSMicroarray>[] newPanels = new DSPanel[n];
                for (int i = 0; i < n; i++) {
                    newPanels[i] = new CSPanel<DSMicroarray>(classes[i]);
                }
                for (DSMicroarray microarray : panel) {
                    if (classifier != null) {
                        newPanels[classifier.classify(microarray.getRawMarkerData())].add(microarray);
                    }
                }
                for (int i = 0; i < n; i++) {
                    publishSubpanelChangedEvent(new SubpanelChangedEvent<DSMicroarray>(DSMicroarray.class, newPanels[i], SubpanelChangedEvent.NEW));
                }
                log.debug("Added group.");
            }
        });
        internalPanel.add(classifyButton);
    }

    public Component getComponent() {
        return this;
    }

    @Subscribe public void receive(ProjectEvent event, Object source) {
        DSDataSet dataSet = event.getDataSet();
        // We will act on this object if it is a DSMicroarraySet
        if (dataSet instanceof DSMicroarraySet) {
            microarraySet = (DSMicroarraySet) dataSet;
        } else if (dataSet instanceof CSClassifier) {
            classifier = (CSClassifier) dataSet;
        }
    }

    @Publish public SubpanelChangedEvent publishSubpanelChangedEvent(SubpanelChangedEvent event) {
        return event;
    }
}

