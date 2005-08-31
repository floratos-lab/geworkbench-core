package org.geworkbench.engine.config;

import org.geworkbench.engine.preferences.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Manages the global preferences
 *
 * @author John Watkinson
 */
public class PreferencesMenu implements MenuListener {

    public ActionListener getActionListener(String var) {
        if (var.equalsIgnoreCase("Tools.Preferences")) {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    GlobalPreferences globalPreferences = GlobalPreferences.getInstance();
                    globalPreferences.displayPreferencesDialog();
                }
            };
        }
        return null;
    }

}
