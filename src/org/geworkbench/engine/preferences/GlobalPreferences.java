package org.geworkbench.engine.preferences;

import java.io.File;

/**
 * @author John Watkinson
 */
public class GlobalPreferences {

    public static final String[] VISUALIZATION_VALUES = new String[]{"Absolute", "Variance"};
    public static final String[] GENEPIX_VALUES = new String[]
            {
                    "Option 1: (Mean F635 - Mean B635) / (Mean F532 - Mean B532)",
                    "Option 2: (Median F635 - Median B635) / (Median F532 - Median B532)",
                    "Option 3: (Mean F532 - Mean B532) / (Mean F635 - Mean B635)",
                    "Option 4: (Median F532 - Median B532) / (Median F635 - Median B635)"
            };
    public static final String PREF_VISUALIZATION = "Visualization";
    public static final String PREF_GENEPIX_COMPUTATION = "Genepix Value Computation";
    public static final String PREF_TEXT_EDITOR = "Text Editor";
    public static final String DEFAULT_TEXT_EDITOR = "c:/windows/system32/notepad.exe";

    private static GlobalPreferences instance;

    public static GlobalPreferences getInstance() {
        if (instance == null) {
            instance = new GlobalPreferences();
        }
        return instance;
    }

    private Preferences prefs;

    private GlobalPreferences() {
        prefs = new Preferences();

        // TEXT EDITOR
        FileField field1 = new FileField(GlobalPreferences.PREF_TEXT_EDITOR);
        // Default value
        field1.setValue(new File(GlobalPreferences.DEFAULT_TEXT_EDITOR));

        // Visualization
        ChoiceField field2 = new ChoiceField(GlobalPreferences.PREF_VISUALIZATION, GlobalPreferences.VISUALIZATION_VALUES);
        field2.setSelection(0);

        // GENEPIX VALUE COMPUTATION
        ChoiceField field3 = new ChoiceField(GlobalPreferences.PREF_GENEPIX_COMPUTATION, GlobalPreferences.GENEPIX_VALUES);
        field3.setSelection(0);

        prefs.addField(field1);
        prefs.addField(field2);
        prefs.addField(field3);
        // Load stored values
        PreferencesManager manager = PreferencesManager.getPreferencesManager();
        manager.fillPreferences(null, prefs);
    }

    public void displayPreferencesDialog() {
        PreferencesManager manager = PreferencesManager.getPreferencesManager();
        prefs = manager.showPreferencesDialog(null, prefs, null);        
    }

    public String getTextEditor() {
        return prefs.getField(PREF_TEXT_EDITOR).toString();
    }

    public int getGenepixComputationMethod() {
        return ((ChoiceField)prefs.getField(PREF_GENEPIX_COMPUTATION)).getSelection();
    }

}
