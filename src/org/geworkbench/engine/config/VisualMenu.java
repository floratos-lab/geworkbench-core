package org.geworkbench.engine.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class VisualMenu implements MenuListener {
    public ActionListener getActionListener(String string) {
        if (string.equalsIgnoreCase("Tools.VisualBuilder")) {
            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    VisualBuilder designer = new VisualBuilder();
                    designer.setRegistry();
                    designer.pack();
                    designer.setVisible(true);
                }
            };
            return listener;
        }
        return null;
    }


}
