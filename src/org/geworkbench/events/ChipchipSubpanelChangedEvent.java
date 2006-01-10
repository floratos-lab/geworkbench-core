package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSChipchipSet;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * @author John Watkinson
 */
public class ChipchipSubpanelChangedEvent extends SubpanelChangedEvent<CSChipchipSet> {

    public ChipchipSubpanelChangedEvent(DSPanel<CSChipchipSet> dsChipchips, int m) {
        super(CSChipchipSet.class, dsChipchips, m);
    }

}
