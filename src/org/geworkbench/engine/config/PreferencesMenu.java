package org.geworkbench.engine.config;

import org.geworkbench.engine.preferences.Preferences;
import org.geworkbench.engine.preferences.FileField;
import org.geworkbench.engine.preferences.ChoiceField;
import org.geworkbench.engine.preferences.PreferencesManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Manages the global preferences
 *
 * @author John Watkinson
 */
public class PreferencesMenu implements MenuListener {

    private static final String[] VISUALIZATION_VALUES = new String[]{"Absolute", "Variance"};

    private static final String[] GENEPIX_VALUES = new String[]
            {
                    "Option 1: (Mean F635 - Mean B635) / (Mean F532 - Mean B532)",
                    "Option 2: (Median F635 - Median B635) / (Median F532 - Median B532)",
                    "Option 3: (Mean F532 - Mean B532) / (Mean F635 - Mean B635)",
                    "Option 4: (Median F532 - Median B532) / (Median F635 - Median B635)"
            };

    public ActionListener getActionListener(String var) {
        if (var.equalsIgnoreCase("Tools.Preferences")) {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Preferences prefs = new Preferences();

                    // TEXT EDITOR
                    FileField field1 = new FileField("Text Editor");
                    // Default value
                    field1.setValue(new File("c:/windows/system32/notepad.exe"));

                    // Visualization
                    ChoiceField field2 = new ChoiceField("Visualization", VISUALIZATION_VALUES);
                    field2.setSelection(0);

                    // GENEPIX VALUE COMPUTATION
                    ChoiceField field3 = new ChoiceField("Genepix Value Computation", GENEPIX_VALUES);
                    field3.setSelection(0);

                    prefs.addField(field1);
                    prefs.addField(field2);
                    prefs.addField(field3);

                    PreferencesManager manager = PreferencesManager.getPreferencesManager();
                    manager.fillPreferences(null, prefs);
                    manager.showPreferencesDialog(null, prefs, null);
                }
            };
        }
        return null;
    }

}
