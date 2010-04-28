package org.geworkbench.engine.ccm;

import java.util.List;

/**
 * A java bean to hold the contents of a .cwb.xml file. This is only used for
 * those that have plugin element.
 * 
 * @author tg2321
 * @version $Id: CcmComponent.java,v 1.4 2009-11-17 15:32:31 tgarben Exp $
 * 
 */
public class PluginComponent2 {
	private String clazz = null;
	private String name = null;
	private String version = null;
	private String availableUpdate = null;	
	private String author = null;
	private String authorUrl = null;
	private String tutorialUrl = null;
	private String toolUrl = null;
	private String description = null;
	private String license = null;
	private boolean mustAccept = false;
	private String documentation = null;
	private boolean loadByDefault = false;
	private boolean isHidden = false;

	private String pluginName;
	private String pluginId;
	private String resource;

	private List<String> required = null;
	private List<String> related = null;

	private boolean isAnalysis = false;
	private boolean isVisualizer = false;

	public PluginComponent2(String name, String clazz, String version, String availableUpdate, 
			String author, String authorUrl, String tutorialUrl,
			String toolUrl, String description, String license,
			boolean mustAccept, String documentation, boolean loadByDefault,
			boolean isHidden, String pluginName, String pluginId,
			String resource, boolean isAnalysis, boolean isVisualizer,
			List<String> required, List<String> related) {
		this.name = name;
		this.clazz = clazz;
		this.version = version;
		this.availableUpdate = availableUpdate;
		this.author = author;
		this.authorUrl = authorUrl;
		this.tutorialUrl = tutorialUrl;
		this.toolUrl = toolUrl;
		this.description = description;
		this.license = license;
		this.mustAccept = mustAccept;
		this.documentation = documentation;
		this.loadByDefault = loadByDefault;
		this.isHidden = isHidden;
		this.pluginName = pluginName;
		this.pluginId = pluginId;
		this.resource = resource;
		this.isAnalysis = isAnalysis;
		this.isVisualizer = isVisualizer;
		
		this.required = required;
		this.related = related;
	}

	public String getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getAvailableUpdate() {
		return availableUpdate;
	}

	public void setAvailableUpdate(String availableUpdate) {
		this.availableUpdate = availableUpdate;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public String getTutorialUrl() {
		return tutorialUrl;
	}

	public String getToolUrl() {
		return toolUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getLicense() {
		return license;
	}

	public boolean isMustAccept() {
		return mustAccept;
	}

	public String getDocumentation() {
		return documentation;
	}

	public boolean getLoadByDefault() {
		return loadByDefault;
	}

	public List<String> getRequired() {
		return required;
	}

	public List<String> getRelated() {
		return related;
	}

	String getPluginName() {
		return pluginName;
	}

	String getPluginId() {
		return pluginId;
	}

	String getResource() {
		return resource;
	}

	boolean isAnalysis() {
		return isAnalysis;
	}

	boolean isVisualizer() {
		return isVisualizer;
	}

	boolean isHidden() {
		return isHidden;
	}

	public boolean equals(Object object) {
		if(!(object instanceof PluginComponent2))
			return false;
		
		PluginComponent2 component = (PluginComponent2)object;
		if(component.getName().equals(name))
			return true;
		else
			return false;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
