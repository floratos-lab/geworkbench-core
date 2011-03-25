package org.geworkbench.util.microarrayutils;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectSelection;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ImageSnapshotEvent;
import org.geworkbench.events.MarkerSelectedEvent;
import org.geworkbench.events.ProjectEvent;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version $Id$
 */

public abstract class MicroarrayVisualizer {
    protected DSMicroarraySetView<DSGeneMarker, DSMicroarray> dataSetView = new CSMicroarraySetView<DSGeneMarker, DSMicroarray>();

    protected int microarrayId = 0;
    protected int markerId = 0;
    protected JPanel mainPanel = new JPanel();

    protected boolean usePanel = false;

    protected DSPanel<DSGeneMarker> markerPanel;
    protected DSPanel<DSBioObject> mArrayPanel;
    protected DSItemList<DSGeneMarker> uniqueMarkers = null;

    public MicroarrayVisualizer() {
        try {
            jbInit();
        } catch (Exception ex) {
        }
    }

    @Publish public ImageSnapshotEvent publishImageSnapshotEvent(ImageSnapshotEvent event) {
        return event;
    }

    @Publish public MarkerSelectedEvent publishMarkerSelectedEvent(MarkerSelectedEvent event) {
        return event;
    }

    private void jbInit() throws Exception {
        dataSetView.useMarkerPanel(true);
        dataSetView.useItemPanel(true);
        mainPanel.setLayout(new BorderLayout());
    }

    public DSItemList<DSGeneMarker> getUniqueMarkers() {
        return uniqueMarkers;
    }

    public final void changeMicroArraySet(DSMicroarraySet<DSMicroarray> maSet) {
        dataSetView.setMicroarraySet(maSet);
        reset();
        setMicroarraySet(maSet);
    }

    protected abstract void setMicroarraySet(DSMicroarraySet<? extends DSMicroarray> set);

    /**
     * Just a dummy implementation rather than an abstract method so that JBuilder does not show
     * a red component.
     */
    protected void reset() {
        uniqueMarkers = dataSetView.getUniqueMarkers();
    }

    public void showAllMArrays(boolean showAll) {
        dataSetView.useItemPanel(!showAll);
        reset();
        repaint();
    }

    public void showAllMarkers(boolean showAll) {
        dataSetView.useMarkerPanel(!showAll);
        uniqueMarkers = dataSetView.getUniqueMarkers();
        repaint();
    }

    @SuppressWarnings("unchecked")
	@Subscribe public void receive(org.geworkbench.events.ProjectEvent projectEvent, Object source) {

        if (projectEvent.getMessage().equals(ProjectEvent.CLEARED)) {
            changeMicroArraySet(null);
            repaint();
            return;
        }
            ProjectSelection selection = ((ProjectPanel) source).getSelection();
            DSDataSet<?> dataFile = selection.getDataSet();
            if (dataFile != null && dataFile instanceof DSMicroarraySet) {
                changeMicroArraySet((DSMicroarraySet<DSMicroarray>) dataFile);
            } else {
                changeMicroArraySet(null);
            }
            repaint();
    }

    public void repaint() {
        mainPanel.repaint();
    }

    public Component getComponent() {
        return mainPanel;
    }

    public DSMicroarraySetView<DSGeneMarker, DSMicroarray> getDataSetView() {
        return dataSetView;
    }

}
