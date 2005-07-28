package org.geworkbench.bison.components.events;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */

public class PanelSelectorEvent <T extends DSNamed> {
    private DSPanel<T> panel;
    private T object;
    private int type;
    public static final int objectSelection = 1;
    public static final int panelSelection = 2;

    public PanelSelectorEvent(DSPanel<T> p) {
        panel = p;
    }

    public PanelSelectorEvent(T object) {
        this.object = object;

    }

    public DSPanel<T> getPanel() {
        return panel;
    }

    public T getObject() {
        return object;
    }
}
