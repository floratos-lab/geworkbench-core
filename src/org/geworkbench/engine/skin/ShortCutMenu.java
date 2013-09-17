package org.geworkbench.engine.skin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.geworkbench.engine.config.rules.GeawConfigObject;


public class ShortCutMenu implements org.geworkbench.engine.config.MenuListener {

    public ActionListener getActionListener(String var) {
        if (var.equalsIgnoreCase("Commands.Navigate - (F12)")) {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	Skin skin = (Skin) GeawConfigObject.getGuiWindow();
                	skin.chooseComponent();
                }
            };
        }
        return null;
    }
}
