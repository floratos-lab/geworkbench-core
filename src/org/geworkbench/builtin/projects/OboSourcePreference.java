/**
 * 
 */
package org.geworkbench.builtin.projects;

import org.geworkbench.engine.preferences.ChoiceField;
import org.geworkbench.engine.preferences.Preferences;
import org.geworkbench.engine.preferences.PreferencesManager;
import org.geworkbench.engine.preferences.TextField;

/**
 * @author zji
 * @version $Id$
 */
// this is similar to Global Preference, but the current global preference is
// tied to the dialog that is flexible.
public class OboSourcePreference {
	static private OboSourcePreference INSTANCE = new OboSourcePreference();

	public enum Source {
		REMOTE, LOCAL
	};

	public Source getSourceType() {
		if( prefs.getField(SOURCE_TYPE).toString().equals(REMOTE_SOURCE_TYPE))
			return Source.REMOTE;
		else
			return Source.LOCAL;
	}

	public void setSourceType(Source source) {
		ChoiceField textField = (ChoiceField) prefs.getField(SOURCE_TYPE);
		if(source==Source.REMOTE)
			textField.setSelection(INDEX_REMOTE);
		else
			textField.setSelection(INDEX_LOCAL);
	}

	public String getSourceLocation() {
		return prefs.getField(SOURCE_LOCATION).toString();
	}

	public void setLocation(String location) {
		TextField textField = (TextField) prefs.getField(SOURCE_LOCATION);
		textField.setValue(location);
	}

	public final static String DEFAULT_REMOTE_LOCATION = "http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology.1_2.obo";
	public final static String DEFAULT_OBO_FILE = "data/gene_ontology.1_2.obo";
	final static String DEFAULT_LOCAL_LOCATION = System.getProperty("user.dir")+"/"+DEFAULT_OBO_FILE;
	
	private Preferences prefs;

	static private final String SOURCE_TYPE = "Source Type";
	static private final String SOURCE_LOCATION = "Source Location";

	static private final int INDEX_REMOTE = 0;
	static private final int INDEX_LOCAL = 1;
	static private final String REMOTE_SOURCE_TYPE = "Remote";
	static private final String LOCAL_SOURCE_TYPE = "Local";
	
	private OboSourcePreference() {
		prefs = new Preferences();

		ChoiceField sourceType = new ChoiceField(SOURCE_TYPE, new String[] {
				REMOTE_SOURCE_TYPE, LOCAL_SOURCE_TYPE });
		sourceType.setSelection(INDEX_REMOTE);

		TextField location = new TextField(SOURCE_LOCATION);
		location.setValue(DEFAULT_REMOTE_LOCATION);

		prefs.addField(sourceType);
		prefs.addField(location);

		// Load stored values
		PreferencesManager manager = PreferencesManager.getPreferencesManager();
		manager.fillPreferences(this.getClass(), prefs);
	}

	static public OboSourcePreference getInstance() {
		return INSTANCE;
	}

	public void save() {
		PreferencesManager manager = PreferencesManager.getPreferencesManager();
		manager.savePreferences(this.getClass(), prefs);
	}
}
