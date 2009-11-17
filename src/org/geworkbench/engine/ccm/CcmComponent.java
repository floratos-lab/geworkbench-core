package org.geworkbench.engine.ccm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A java bean to hold the contents of a xxx.ccm.xml file.
 * 
 * @author tg2321
 * @version $Id: CcmComponent.java,v 1.4 2009-11-17 15:32:31 tgarben Exp $
 * 
 */
public class CcmComponent {
	private String clazz = StringUtils.EMPTY;
	private String name = StringUtils.EMPTY;
	private String version = StringUtils.EMPTY;
	private String author = StringUtils.EMPTY;
	private String authorURL = StringUtils.EMPTY;
	private String tutorialURL = StringUtils.EMPTY;
	private String toolURL = StringUtils.EMPTY;
	private String description = StringUtils.EMPTY;
	private String license = StringUtils.EMPTY;
	private String mustAccept = StringUtils.EMPTY;
	private String documentation = StringUtils.EMPTY;
	private String loadByDefault = StringUtils.EMPTY;
	private String hidden = StringUtils.EMPTY;
	private List<Plugin> plugins = null;
	private List<String> requiredComponents = new ArrayList<String>();
	private List<String> relatedComponents = new ArrayList<String>();
	private String parser = StringUtils.EMPTY;
	private String analysis = StringUtils.EMPTY;
	private String visualizer = StringUtils.EMPTY;

	public CcmComponent() {
		super();
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorURL() {
		return authorURL;
	}

	public void setAuthorURL(String authorURL) {
		this.authorURL = authorURL;
	}

	public String getTutorialURL() {
		return tutorialURL;
	}

	public void setTutorialURL(String tutorialURL) {
		this.tutorialURL = tutorialURL;
	}

	public String getToolURL() {
		return toolURL;
	}

	public void setToolURL(String toolURL) {
		this.toolURL = toolURL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getMustAccept() {
		return mustAccept;
	}

	public void setMustAccept(String mustAccept) {
		if (mustAccept == null){
			this.mustAccept = "false";	
		}else{
			this.mustAccept = mustAccept;
		}
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getLoadByDefault() {
		return loadByDefault;
	}

	public void setLoadByDefault(String loadByDefault) {
		if (loadByDefault == null){
			this.loadByDefault = "false";	
		}else{
			this.loadByDefault = loadByDefault;
		}
	}

	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		if (hidden == null){
			this.hidden = "false";	
		}else{
			this.hidden = hidden;
		}
	}
	
	public List<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
	}

	public void addRequiredComponent(String component) {
		requiredComponents.add(component);
	}

	public List<String> getRequiredComponents() {
		return requiredComponents;
	}

	public void setRequiredComponents(List<String> requiredComponents) {
		this.requiredComponents = requiredComponents;
	}

	public void addRelatedComponent(String component) {
		relatedComponents.add(component);
	}

	public List<String> getRelatedComponents() {
		return relatedComponents;
	}

	public void setRelatedComponents(List<String> relatedComponents) {
		this.relatedComponents = relatedComponents;
	}
	public String getParser() {
		return parser;
	}

	public void setParser(String parser) {
		if (parser == null){
			this.parser = "false";	
		}else{
			this.parser = parser;
		}
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		if (analysis == null){
			this.analysis = "false";	
		}else{
			this.analysis = analysis;
		}
	}

	public String getVisualizer() {
		return visualizer;
	}

	public void setVisualizer(String visualizer) {
		if (visualizer == null){
			this.visualizer = "false";	
		}else{		
			this.visualizer = visualizer;
		}
	}

}
