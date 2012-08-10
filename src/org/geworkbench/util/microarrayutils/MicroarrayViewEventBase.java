package org.geworkbench.util.microarrayutils;

/*
 * The geworkbench project
 *
 * Copyright (c) 2006 Columbia University
 *
 */

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.Asynchronous;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.GeneSelectorEvent;

/**
 * @author unattributable
 * @see VisualPlugin
 * @version $Id$
 */
public abstract class MicroarrayViewEventBase implements VisualPlugin {

	private Log log = LogFactory.getLog(MicroarrayViewEventBase.class);

	/**
	 * The reference microarray set.
	 */
	protected DSMicroarraySet refMASet = null;
	protected DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView = null;

	protected JButton plotButton = new JButton("Plot");
	private final String markerLabelPrefix = "  Markers: ";
	protected JLabel numMarkersSelectedLabel = new JLabel(markerLabelPrefix);
	protected JPanel mainPanel;
	protected JToolBar jToolBar3;
	protected DSPanel<DSGeneMarker> markers = null;
	protected DSPanel<DSGeneMarker> activatedMarkers = null;
	protected DSItemList<? extends DSGeneMarker> uniqueMarkers = null;
	protected DSPanel<DSMicroarray> activatedArrays = null;

	/**
	 *
	 *
	 */
	public MicroarrayViewEventBase() {
		mainPanel = new JPanel();

		jToolBar3 = new JToolBar();

		BorderLayout borderLayout2 = new BorderLayout();
		mainPanel.setLayout(borderLayout2);

		mainPanel.add(jToolBar3, java.awt.BorderLayout.SOUTH);
	}

	/**
	 * getComponent
	 *
	 * @return Component
	 */
	public Component getComponent() {
		return mainPanel;
	}


	/**
	 * receiveProjectSelection
	 *
	 * @param e
	 *            ProjectEvent
	 */
	@Subscribe(Asynchronous.class)
	public void receive(org.geworkbench.events.ProjectEvent e, Object source) {

		log.debug("Source object " + source);

		if (e.getValue()==org.geworkbench.events.ProjectEvent.Message.CLEAR) {
			if(beingRefreshed) {
				return;
			}

			beingRefreshed = true;
			refMASet = null;
			fireModelChangedEvent();
			beingRefreshed = false;
		} else {
			DSDataSet<?> dataSet = e.getDataSet();
			if (dataSet instanceof DSMicroarraySet) {
				if (refMASet != dataSet) {
					this.refMASet = (DSMicroarraySet) dataSet;					
				}
			}
			refreshMaSetView();
		}
	}

	/**
	 * geneSelectorAction
	 *
	 * @param e
	 *            GeneSelectorEvent
	 */
	@Subscribe(Asynchronous.class)
	public void receive(GeneSelectorEvent e, Object source) {

		log.debug("Source object " + source);

		markers = e.getPanel();
		activatedMarkers = new CSPanel<DSGeneMarker>();
		if (markers != null && markers.size() > 0) {            
			for (int j = 0; j < markers.panels().size(); j++) {
				DSPanel<DSGeneMarker> mrk = markers.panels().get(j);
				if (mrk.isActive()) {
					for (int i = 0; i < mrk.size(); i++) {						
						activatedMarkers.add(mrk.get(i));

					}
				}
			}
			markers = activatedMarkers;

			numMarkersSelectedLabel.setText(markerLabelPrefix
					+ activatedMarkers.size() + "  ");

		}

		else
			numMarkersSelectedLabel.setText(markerLabelPrefix);

		if (markers!=null)
			refreshMaSetView();

	}

	/**
	 * phenotypeSelectorAction
	 *
	 * @param e
	 *            PhenotypeSelectorEvent
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Subscribe(Asynchronous.class)
	public void receive(org.geworkbench.events.PhenotypeSelectorEvent e,
			Object source) {

		log.debug("Source object " + source);

		if (e.getTaggedItemSetTree() != null) {
			activatedArrays = e.getTaggedItemSetTree().activeSubset();
		}

		refreshMaSetView();

	}

	private volatile boolean beingRefreshed = false;
	/**
	 * Refreshes the chart view.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final protected void refreshMaSetView() {
		if(beingRefreshed) {
			return;
		}
		
		beingRefreshed = true;
		maSetView = new CSMicroarraySetView(this.refMASet);
		if (activatedMarkers != null && activatedMarkers.panels().size() > 0)
			maSetView.setMarkerPanel(activatedMarkers);
		if (activatedArrays != null && activatedArrays.panels().size() > 0 && activatedArrays.size() > 0)
			maSetView.setItemPanel(activatedArrays);
		maSetView.useMarkerPanel(true);
		maSetView.useItemPanel(true);

		uniqueMarkers = maSetView.getUniqueMarkers();

		fireModelChangedEvent();
		beingRefreshed = false;
	}

	/**
	 * @param event
	 */
	protected synchronized void fireModelChangedEvent() {
		// no-op
	}

}
