package org.geworkbench.engine.preferences;

import org.geworkbench.bison.util.colorcontext.ColorContext;
import org.geworkbench.bison.util.colorcontext.DefaultColorContext;
import org.geworkbench.bison.util.colorcontext.ExpressionPValueColorContext;
import org.geworkbench.util.Util;

import java.io.File;

/**
 * @author John Watkinson
 */
public class GlobalPreferences {

    public static final String PREF_VISUALIZATION = "Visualization";
    public static final String PREF_GENEPIX_COMPUTATION = "Genepix Value Computation";
    public static final String PREF_TEXT_EDITOR = "Text Editor";
    public static final String RCM_URL = "Remote Components URL";
    
    public static final String[] VISUALIZATION_VALUES = new String[]{"Absolute", "Relative"};
    public static final Class<? extends ColorContext>[] VISUALIZATON_COLOR_CONTEXTS = new Class[]{DefaultColorContext.class, ExpressionPValueColorContext.class};

    public static final String[] GENEPIX_VALUES = new String[]
            {
                    "Option 1: (Mean F635 - Mean B635) / (Mean F532 - Mean B532)",
                    "Option 2: (Median F635 - Median B635) / (Median F532 - Median B532)",
                    "Option 3: (Mean F532 - Mean B532) / (Mean F635 - Mean B635)",
                    "Option 4: (Median F532 - Median B532) / (Median F635 - Median B635)"
            };
    public static final String DEFAULT_TEXT_EDITOR = "c:/windows/system32/notepad.exe";
    public static final String DEFAULT_TEXT_EDITOR_MAC = "Open";

    public static final String DEFAULT_RCM_URL = "http://califano11.cgc.cpmc.columbia.edu:8080/componentRepository";

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
        if (!Util.isRunningOnAMac()) {
            field1.setValue(new File(GlobalPreferences.DEFAULT_TEXT_EDITOR));
        } else {
            field1.setValue(new File(DEFAULT_TEXT_EDITOR_MAC));
        }

        // Visualization
        ChoiceField field2 = new ChoiceField(GlobalPreferences.PREF_VISUALIZATION, GlobalPreferences.VISUALIZATION_VALUES);
        field2.setSelection(1);

        // GENEPIX VALUE COMPUTATION
        ChoiceField field3 = new ChoiceField(GlobalPreferences.PREF_GENEPIX_COMPUTATION, GlobalPreferences.GENEPIX_VALUES);
        field3.setSelection(0);

        // RCM URL
        TextField field4 = new TextField(GlobalPreferences.RCM_URL);
        field4.setValue(GlobalPreferences.DEFAULT_RCM_URL);
        
        // Color Context

        prefs.addField(field1);
        prefs.addField(field2);
        prefs.addField(field3);
        prefs.addField(field4);
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

    public String getRCM_URL() {
        return prefs.getField(RCM_URL).toString();
    }
    
    public Class<? extends ColorContext> getColorContextClass() {
        int pref = ((ChoiceField) prefs.getField(PREF_VISUALIZATION)).getSelection();
        return VISUALIZATON_COLOR_CONTEXTS[pref];
    }

}
