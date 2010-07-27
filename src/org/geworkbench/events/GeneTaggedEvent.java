/*
 * The geworkbench-core project
 * 
 * Copyright (c) 2008 Columbia University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 *
 * Event published from gene panel to update visualization of cytoscape.
 * 
 * @author zji
 * @version $Id$
 */

public class GeneTaggedEvent extends Event {

    private DSPanel<DSGeneMarker> panel;
 
    public static final int HIGHLIGHT = 1;
    public static final int USE_VISUAL_PROPERTY = 2;
    
    private int type;
    private int panelIndex;
    
    public GeneTaggedEvent(DSPanel<DSGeneMarker> p) {
        super(null);
        panel = p;
        type = HIGHLIGHT;

    }
    
    public GeneTaggedEvent(DSPanel<DSGeneMarker> p, int tagType) {
        super(null);
        panel = p;
        this.type = tagType;

    }

    public GeneTaggedEvent(DSPanel<DSGeneMarker> p, int tagType, int panelIndex) {
        super(null);
        panel = p;
        this.type = tagType;
        this.panelIndex = panelIndex;
    }

    public DSPanel<DSGeneMarker> getPanel() {
        return panel;
    }
    
    public int getType() {
        return type;
    }
    
    public int getPanelIndex() {
        return panelIndex;
    }
    
}
