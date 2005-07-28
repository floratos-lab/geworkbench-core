package org.geworkbench.util.microarrayutils;

import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.PhenotypeSelectorEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.engine.management.Asynchronous;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.VisualPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MicroarrayViewEventBase implements VisualPlugin {

    /**
     * The reference microarray set.
     */
    protected DSMicroarraySet<DSMicroarray> refMASet = null;
    protected DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView = null;

    protected boolean activateMarkers = false;
    protected boolean activateArrays = false;
    protected JCheckBox chkActivateMarkers = new JCheckBox("Activated Markers");
    protected JCheckBox chkShowArrays = new JCheckBox("Activated Arrays");
    protected JPanel mainPanel;
    protected JToolBar jToolBar3;
    protected DSPanel<? extends DSGeneMarker> activatedMarkers = null;
    protected DSPanel activatedArrays = null;

    public MicroarrayViewEventBase() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * getComponent
     *
     * @return Component
     */
    public Component getComponent() {
        return mainPanel;
    }

    @Publish public SubpanelChangedEvent publishSubpanelChangedEvent(org.geworkbench.events.SubpanelChangedEvent event) {
        return event;
    }

    /**
     * receiveProjectSelection
     *
     * @param e ProjectEvent
     */
    @Subscribe public void receive(org.geworkbench.events.ProjectEvent e, Object source) {
        if (e.getMessage().equals(org.geworkbench.events.ProjectEvent.CLEARED)) {
            refMASet = null;
            fireModelChangedEvent(null);
        } else {
            DSDataSet dataSet = e.getDataSet();
            if (dataSet instanceof DSMicroarraySet) {
                if (refMASet != dataSet) {
                    this.refMASet = (DSMicroarraySet) dataSet;
                    // panels are now invalid
                    activatedArrays = null;
                    activatedMarkers = null;
                }
            }
            refreshMaSetView();
        }
    }

    /**
     * geneSelectorAction
     *
     * @param e GeneSelectorEvent
     */
    @Subscribe(Asynchronous.class) public void receive(GeneSelectorEvent e, Object source) {
        if (e.getPanel() != null && e.getPanel().size() > 0) {
            activatedMarkers = e.getPanel().activeSubset();
        }
        refreshMaSetView();
    }

    /**
     * phenotypeSelectorAction
     *
     * @param e PhenotypeSelectorEvent
     */
    @Subscribe public void receive(org.geworkbench.events.PhenotypeSelectorEvent e, Object source) {
        if (e.getTaggedItemSetTree() != null) {
            activatedArrays = e.getTaggedItemSetTree().activeSubset();
        }
        refreshMaSetView();
    }

    protected void refreshMaSetView() {
        maSetView = getDataSetView();
        fireModelChangedEvent(null);
    }

    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {

    }

    protected void jbInit() throws Exception {
        mainPanel = new JPanel();

        jToolBar3 = new JToolBar();
        chkActivateMarkers.setToolTipText("");

        BorderLayout borderLayout2 = new BorderLayout();
        mainPanel.setLayout(borderLayout2);

        jToolBar3.add(chkShowArrays, null);
        jToolBar3.add(chkActivateMarkers, null);
        mainPanel.add(jToolBar3, java.awt.BorderLayout.SOUTH);

        chkActivateMarkers.addActionListener(new MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter(this));
        chkShowArrays.addActionListener(new MicroarrayViewPanelBase_chkShowArrays_actionAdapter(this));
        mainPanel.add(jToolBar3, BorderLayout.SOUTH);
        jToolBar3.add(chkShowArrays, null);
        jToolBar3.add(chkActivateMarkers, null);

        this.activateMarkers = chkActivateMarkers.isSelected();
        this.activateArrays = chkShowArrays.isSelected();
    }

    void chkShowArrays_actionPerformed(ActionEvent e) {
        activateArrays = ((JCheckBox) e.getSource()).isSelected();
        refreshMaSetView();
    }

    void chkActivateMarkers_actionPerformed(ActionEvent e) {
        activateMarkers = ((JCheckBox) e.getSource()).isSelected();
        refreshMaSetView();
    }

    public DSMicroarraySetView getDataSetView() {
        DSMicroarraySetView dataSetView = new CSMicroarraySetView(this.refMASet);
        if (activatedMarkers != null && activatedMarkers.panels().size() > 0)
            dataSetView.setMarkerPanel(activatedMarkers);
        if (activatedArrays != null && activatedArrays.panels().size() > 0)
            dataSetView.setItemPanel(activatedArrays);
        dataSetView.useMarkerPanel(activateMarkers);
        dataSetView.useItemPanel(activateArrays);
        //        dataSetView.setMicroarraySet(this.refMASet);
        return dataSetView;
    }

}

class MicroarrayViewPanelBase_chkShowArrays_actionAdapter implements java.awt.event.ActionListener {
    MicroarrayViewEventBase adaptee;

    MicroarrayViewPanelBase_chkShowArrays_actionAdapter(MicroarrayViewEventBase adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.chkShowArrays_actionPerformed(e);
        adaptee.getComponent().repaint();
    }
}

class MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter implements java.awt.event.ActionListener {
    MicroarrayViewEventBase adaptee;

    MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter(MicroarrayViewEventBase adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.chkActivateMarkers_actionPerformed(e);
        adaptee.getComponent().repaint();
    }
}
