package org.geworkbench.engine.ccm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.help.HelpSet;
import javax.help.TryMap;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.config.rules.GeawConfigRuleCCM;
import org.geworkbench.engine.config.rules.PluginRuleCCM;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.management.ComponentResource;
import org.geworkbench.engine.management.TypeMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Manages the dynamic loading and removal of components.
 * 
 * @author keshav
 * @author tg2321
 * @version $Id: ComponentConfigurationManager.java,v 1.1 2009/02/09 19:54:43
 *          keshav Exp $
 */
public class ComponentConfigurationManager {

	private static Log log = LogFactory
			.getLog(ComponentConfigurationManager.class);
	private String[] files = null;
	private Digester digester = null;

	private static final String FILE_DEL = System.getProperty("file.separator");
	public static final String CCM_EXTENSION = ".ccm.xml";
	private ArrayList<String> allComponentFolders = new ArrayList<String>();
	private Map<String, List<String>> foldersToCcmFiles = new HashMap<String, List<String>>();

	private static String propertiesDirectory = null;
	/**
	 * Constructor
	 * 
	 * @param
	 */
	public ComponentConfigurationManager() {
		File dir = new File(UILauncher.componentsDir);
		if (!dir.isDirectory()) {
			log.warn("Supplied components directory is not a directory: "
					+ UILauncher.componentsDir);
			return;
		}
		files = dir.list();

		digester = new Digester(new org.apache.xerces.parsers.SAXParser());
		this.configure();
		
		String userSettingDirectory =  System.getProperty("user.setting.directory");
		if(userSettingDirectory!=null) {
			propertiesDirectory = userSettingDirectory = System.getProperty("user.home")
					+ FILE_DEL
					+ userSettingDirectory;
		} else {
			propertiesDirectory = UILauncher.componentsDir;
		}

	}

	/**
	 * Creates a {@link ComponentResource}.
	 * 
	 * @return {@link ComponentResource}
	 */
	private ComponentResource createComponentResource(String resource) {
		log.debug("Create component resource " + resource);

		if (StringUtils.isEmpty(resource)) {
			log.error("Input resource is null.  Returning ...");
		}
		List<String> list = Arrays.asList(files);

		ComponentResource componentResource = null;
		if (list.contains(resource)) {
			int index = list.indexOf(resource);
			File file = new File(list.get(index));
			try {
				String path = UILauncher.componentsDir + FILE_DEL
						+ file.getPath();
				componentResource = new ComponentResource(path, false);
				log.debug("Created component resource " + file.getName());
			} catch (IOException e) {
				log.error("Could not initialize component resource '"
						+ file.getName() + "'.", e);
			}
			return componentResource;
		}
		log.error("Resource does not exist for " + resource
				+ ".  Returning null.");
		return null;
	}

	/**
	 * User.ccm.xml files contained in Component Folders to decide which rows
	 * should be displayed in the CCM window.
	 * 
	 * @return {@link ArrayList}
	 */
	public ArrayList<String> loadAllComponentFolders() {

		String componentsDir = System
				.getProperty(UILauncher.COMPONENTS_DIR_PROPERTY);
		if (componentsDir == null) {
			componentsDir = UILauncher.DEFAULT_COMPONENTS_DIR;
		}

		File dir = new File(componentsDir);
		if (!dir.isDirectory()) {
			log.debug("Component resource path is not a directory: "
					+ componentsDir);
			return null;
		}
		File[] folders = dir.listFiles();
		for (int i = 0; i < folders.length; i++) {
			File folder = folders[i];
			if (!folder.isDirectory()) {
				continue;
			}

			// TODO clarify the location of the components folder
			// and see what special cases that need to be handled like the two
			// below.
			String folderName = folder.getName();
			if (folderName.endsWith("geworkbench-core")
					|| folderName.endsWith(".metadata")) {
				continue;
			}

			boolean foundOne = false;
			File[] filesInaFolder = folder.listFiles();
			ArrayList<String> ccmFilesInaFolder = new ArrayList<String>();
			for (int j = 0; j < filesInaFolder.length; j++) {
				File possibleFile = filesInaFolder[j];
				if (!possibleFile.isFile()) {
					continue;
				}

				String fileName = possibleFile.getName();
				if (fileName.endsWith(CCM_EXTENSION)) {
					foundOne = true;
					ccmFilesInaFolder.add(fileName);
				}
			}

			if (foundOne) {
				this.allComponentFolders.add(folder.getName());
				this.foldersToCcmFiles.put(folder.getName(), ccmFilesInaFolder);
			}
		}

		return allComponentFolders;
	}

	/**
	 * Load components that have on-off=true in their properties file
	 * 
	 */
	public void loadSelectedComponents() {

		for (int i = 0; i < allComponentFolders.size(); i++) {
			String folder = allComponentFolders.get(i);

			List<String> ccmFiles = this.foldersToCcmFiles.get(folder);
			for (int j = 0; j < ccmFiles.size(); j++) {
				String ccmFileName = ccmFiles.get(j);
				String propFileName = ccmFileName.replace(CCM_EXTENSION,
						".ccmproperties");

				String onOff = readProperty(folder, propFileName, "on-off");
				CcmComponent ccmComponent = getPluginsFromCcmFile(folder, ccmFileName );
				String loadByDefault = ccmComponent.getLoadByDefault();
				
				if (onOff == null && loadByDefault.equalsIgnoreCase("true")){
					writeProperty(folder, propFileName, "on-off", "true");
					onOff = readProperty(folder, propFileName, "on-off");
				}
				if (onOff != null && onOff.equals("true")) {
					loadComponent(folder, ccmFileName);
				}
			}
		}
	}

	/**
	 * Loads a component.
	 * 
	 * @param folder
	 * @param ccmFileName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean loadComponent(String folder, String ccmFileName) {

		/* create component resource */
		ComponentResource componentResource = createComponentResource(folder);

		/* add resource to registry */
		Map<String, ComponentResource> resourceMap = ComponentRegistry
				.getRegistry().getComponentResourceMap();
		ComponentResource existingComponentResource = resourceMap.get(folder);
		if (existingComponentResource==null){
			resourceMap.put(folder, componentResource);
		}
		/* get input stream for ccm.xml */
		String ccmFullPath = UILauncher.componentsDir + FILE_DEL + folder
				+ FILE_DEL + ccmFileName;

		/* parse using digester */
		boolean successful = false;
		InputStream is = null;
		try {
			is = new FileInputStream(new File(ccmFullPath));
			digester.parse(is);
			successful = true;
		} catch (Exception e) {
			log.error(e, e);
		}

		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				log.error(e, e);
			}
		}

		/* Remove HelpSets from Master Help*/
		HelpSet localMasterHelp = GeawConfigObject.getMasterHelp();
		Enumeration<HelpSet> helpSets =  localMasterHelp.getHelpSets();
		Set<HelpSet> helpSetTemp = new HashSet<HelpSet>();  
		while (helpSets.hasMoreElements()){
			HelpSet helpSet = helpSets.nextElement();
			helpSetTemp.add(helpSet);
		}
		for (HelpSet hs : helpSetTemp){
			localMasterHelp.remove(hs);
		}
		
		/* Remove Combined Maps from Master Help*/
		TryMap combinedMap =  (TryMap)localMasterHelp.getCombinedMap();
		Enumeration<?>  maps = combinedMap.getMaps();
		Set<javax.help.Map> mapsTemp = new HashSet<javax.help.Map>();
		while (maps.hasMoreElements()){
			javax.help.Map map = (javax.help.Map)maps.nextElement();
			mapsTemp.add(map);
		}
		for (javax.help.Map map: mapsTemp){
			combinedMap.remove(map);
		}

		/* Rebuild Help here */
		TreeMap<String, HelpSet> localSortedHelpSets = GeawConfigObject.getSortedHelpSets();
		for (Map.Entry<String, HelpSet> entry : localSortedHelpSets.entrySet()) {
			if (!helpSetContainsTitle(localMasterHelp, entry.getValue().getTitle())){
				log.debug("Adding help set: " + entry.getKey() + " | " + entry.getValue().getTitle());
				localMasterHelp.add(entry.getValue());
			}
		}
		
		return successful;
	}

	/**
	 * Use this instead of HelpSet.contains()
	 * 
	 * @param helpSet
	 * @param title
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean helpSetContainsTitle(HelpSet helpSet, String title){
		Enumeration<HelpSet> helpSets = helpSet.getHelpSets();
		if (helpSets == null){
			return false;
		}
		
		while (helpSets.hasMoreElements()) {
			HelpSet subHelpSet = helpSets.nextElement();
			
			if (subHelpSet.getTitle().equalsIgnoreCase(title)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Removes the component with resourceName.
	 * 
	 * @param folderName
	 * @param ccmFileName
	 * @return Returns false if component was not successfully removed, else
	 *         true.
	 */
	@SuppressWarnings("unchecked")
	public boolean removeComponent(String folderName, String ccmFileName) {

		/*
		 * Container Summary:
		 * 
		 * ComponentRegistry Section listeners = new TypeMap<List>(); 
		 * acceptors = new HashMap<Class, List<Class>>(); 
		 * synchModels = new HashMap<Class, SynchModel>(); 
		 * components = new ArrayList(); 
		 * idToDescriptor = new HashMap<String, PluginDescriptor>(); 
		 * nameToComponentResource = new HashMap<String, ComponentResource>();
		 */

		ComponentRegistry componentRegistry = ComponentRegistry.getRegistry();

		/* validation */
		if (StringUtils.isEmpty(folderName)) {
			log.error("Input resource is null.  Returning ...");
			return false;
		}

		List<String> list = Arrays.asList(files);
		if (!list.contains(folderName)) {
			return false;
		}

		// FIXME parse again? can't we get this info another way?
		/* parse the ccm.xml file */
		CcmComponent ccmComponent = getPluginsFromCcmFile(folderName,
				ccmFileName);

		if (ccmComponent == null) return false;
		
		List<Plugin> pluginList = ccmComponent.getPlugins();

		if (pluginList == null) {
			return false;
		}

		/* GET THE VARIOUS MAPS/VECTORS FROM THE PLUGIN REGISTRY */

		/* plugin registry component vector */
		Vector<PluginDescriptor> componentVector = PluginRegistry
				.getComponentVector();

		/* plugin registry visual area map */
		HashMap<PluginDescriptor, String> visualAreaMap = PluginRegistry
				.getVisualAreaMap();

		/* plugin registry used ids */
		Vector<String> usedIds = PluginRegistry.getUsedIds();

		/* GET THE VARIOUS MAPS/VECTORS FROM THE COMPONENT REGISTRY */

		/* component registry listeners */
		TypeMap<List> listeners = componentRegistry.getListenersTypeMap();

		/* component registry resource map */
		Map<String, ComponentResource> resourceMap = componentRegistry
				.getComponentResourceMap();

		/* component registry acceptors */
		HashMap<Class, List<Class>> acceptors = componentRegistry
				.getAcceptorsHashMap();

		/* component registry id to descriptor */
		Map<String, PluginDescriptor> idToDescriptor = componentRegistry
				.getIdToDescriptorMap();

		/* component registry components list */
		List<Object> components = componentRegistry.getComponentsList();
		List<Object> updatedComponentList = new ArrayList<Object>();
		for (Object cmp : components) {
			updatedComponentList.add(cmp);
		}

		// FIXME Can't we get the PluginDesriptor we want other than from the
		// ccm.xml file?
		for (int i = 0; i < pluginList.size(); i++) {
			Plugin plugin = pluginList.get(i);
			final String pluginClazzName = plugin.getClazz();
			PluginDescriptor pluginDescriptor = PluginRegistry
					.getPluginDescriptor(plugin.getId());

			if (pluginDescriptor == null) {
				continue;
			}

			/* Skin.visualRegistry */
			/* If we need to support skins that do not refresh themselves, after receiving an event, 
			 * when component has been added or removed,
			 * then something like the following code might be necessary. 
			if (pluginDescriptor.isVisualPlugin()) {
				VisualPlugin visualPlugin = (VisualPlugin) pluginDescriptor.getPlugin();
				Component component = visualPlugin.getComponent();
				JFrame jframeGUI = GUIFramework.getFrame();
				jframeGUI.remove(component);
			}
			*/
			
			/* START THE REMOVAL PROCESS IN THE PLUGIN REGISTRY */
			/* PluginRegistry.visualAreaMap */
			for (Entry<PluginDescriptor, String> entry : visualAreaMap.entrySet()) {
				Object pluginDescriptor1 = entry.getKey();
				Class clazz = pluginDescriptor1.getClass();
				String proxiedClazzName = clazz.getName();
				
				// TODO Replace $$ parse methods with clazzName = clazz.getSuperclass() 
				String[] temp = StringUtils.split(proxiedClazzName, "$$");
				String clazzName = temp[0];

				if (StringUtils.equals(pluginClazzName, clazzName)) {
					visualAreaMap.remove(entry.getKey());
					break;
				}
			}

			/* PluginRegistry.visualAreaMap */
			String id = plugin.getId();
			if (PluginDescriptor.idExists(id)) {
				usedIds.remove(id);
			}

			/* PluginRegistry.compontentVector */
			if (componentVector.contains(pluginDescriptor)) {
				componentVector.remove(pluginDescriptor);
			}

			/* START THE REMOVAL PROCESS IN THE COMPONENT REGISTRY */

			/* ComponentRegistry.listeners */
			// componentRegistry.removeFromListeners(pluginClazzName);
			for (Map.Entry<Class, List> entry : listeners.entrySet()) {
				List listenersForOneEvent = entry.getValue();

				for (Object proxiedListener : listenersForOneEvent) {
					Class clazz = proxiedListener.getClass();
					String proxiedClazzName = clazz.getName();
					String[] temp = StringUtils.split(proxiedClazzName, "$$");
					String clazzName = temp[0];

					if (StringUtils.equals(pluginClazzName, clazzName)) {
						listenersForOneEvent.remove(proxiedListener);
						break;
					}
				}
			}

			/* ComponentRegistry.acceptors */
			HashMap<Class, List<Class>> acceptorsNew = new HashMap<Class, List<Class>>();

			for (Map.Entry<Class, List<Class>> entry : acceptors.entrySet()) {
				Class acceptorKey = entry.getKey();
				List<Class> componentList = entry.getValue();
				List<Class> componentsNew = new ArrayList<Class>();

				ListIterator<Class> componentListIter = componentList
						.listIterator();
				while (componentListIter.hasNext()) {
					Class componentClass = componentListIter.next();
					String componentClassName = componentClass.getName();

					if (!pluginClazzName.equals(componentClassName)) {
						componentsNew.add(componentClass);
					}
				}

				acceptorsNew.put(acceptorKey, componentsNew);
			}

			componentRegistry.setAcceptorsHashMap(acceptorsNew);

			/* ComponentRegistry.components */
			for (Object proxiedComponent : components) {
				// FIXME use a "deproxy" (see cglib or HibernateProxy)
				Class clazz = proxiedComponent.getClass();
				String proxiedClazzName = clazz.getName();
				String[] temp = StringUtils.split(proxiedClazzName, "$$");
				String clazzName = temp[0];

				if (StringUtils.equals(pluginClazzName, clazzName)) {
					updatedComponentList.remove(proxiedComponent);
				}
			}

			/* ComponentRegistry.idToDescriptor */
			String pluginToRemove = plugin.getId();
			PluginDescriptor tempPluginDescriptor = idToDescriptor
					.get(pluginToRemove);
			if (tempPluginDescriptor != null) {
				idToDescriptor.remove(pluginToRemove);
			}

		}// for

		/* ComponentRegistry.idToDescriptor */
		/* If other Plugins are using the same Component Resource, don't remove the Resource */
		int foldersInUse = 0;
		for (int i = 0; i < componentVector.size(); i++) {
			PluginDescriptor pd = componentVector.get(i);
			if (pd == null){
				continue;
			}
			
			String pluginName = pd.getLabel();// For Debugging
			ComponentResource componentResource = pd.getResource();
			if (componentResource == null){
				continue;	
			}
			
			String name = componentResource.getName();
			if (name == null){
				continue;
			}
				
			if (name.equalsIgnoreCase(folderName)) {
				foldersInUse++;
			}
		}

		if (foldersInUse < 1 ){
			ComponentResource resourceToRemove = resourceMap.get(folderName);
			if (resourceToRemove != null) {
				resourceMap.remove(folderName);
			}
		}
		
		componentRegistry.setComponentsList(updatedComponentList);

		return true;
	}
	/**
	 * A custom parser for the ccm descriptor.
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CcmComponent getPluginsFromCcmFile(String folder, String fileName) {
		CcmComponent ccmComponent = new CcmComponent();

		String ccmPath = UILauncher.componentsDir + FILE_DEL + folder
				+ FILE_DEL + fileName;
		File ccmFile = new File(ccmPath);
		if (!ccmFile.exists()) {
			return null;
		}

		ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
		SAXBuilder builder = new SAXBuilder();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(ccmFile);

			if (inputStream != null) {
				Document doc = null;
				try {
					doc = builder.build(inputStream);
				} catch (JDOMException e) {
					log.error(e, e);
				} catch (IOException e) {
					log.error(e, e);
				}
				Element root = doc.getRootElement();
				if (root.getName().equals("geaw-config")) {
					java.util.List<Element> elements = root.getChildren();
					for (int i = 0; i < elements.size(); i++) {
						Element element = elements.get(i);
						if (element.getName().equals("component")) {
							ccmComponent.setClazz(element
									.getAttributeValue("class"));
							ccmComponent.setName(element
									.getAttributeValue("name"));
							ccmComponent.setVersion(element
									.getAttributeValue("version"));
							ccmComponent.setAuthor(element
									.getAttributeValue("author"));
							ccmComponent.setAuthorURL(element
									.getAttributeValue("authorURL"));
							ccmComponent.setToolURL(element
									.getAttributeValue("toolURL"));
							ccmComponent.setDescription(element
									.getAttributeValue("description"));
							ccmComponent.setMustAccept(element
									.getAttributeValue("mustAccept"));
							ccmComponent.setDocumentation(element
									.getAttributeValue("documentation"));
							ccmComponent.setLoadByDefault(element
									.getAttributeValue("loadByDefault"));

							
							List<Element> subElements = element.getChildren();
							for (int j = 0; j < subElements.size(); j++) {
								Element subElement = subElements.get(j);
								String type = subElement.getName();
								String dependencyClass = subElement
										.getAttributeValue("class");
								if (type.equals("required-component")) {
									ccmComponent.addRequiredComponent(dependencyClass);
								} else if (type.equals("related-component")) {
									ccmComponent.addRelatedComponent(dependencyClass);
								} else if (type.equals("license")) {
									String cdata = subElement.getTextTrim();

									ccmComponent.setLicense(cdata);
								}
							}

							ccmComponent.setParser(element
									.getAttributeValue("parser"));
							ccmComponent.setAnalysis(element
									.getAttributeValue("analysis"));
							ccmComponent.setVisualizer(element
									.getAttributeValue("visualizer"));
						}

						if (element.getName().equals("plugin")) {
							String id = element.getAttributeValue("id");
							String name = element.getAttributeValue("name");
							String clazz = element.getAttributeValue("class");
							String source = element.getAttributeValue("source");

							pluginList.add(new Plugin(id, name, clazz, source));
						}
					}
					ccmComponent.setPlugins(pluginList);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error(e, e);
				return null;
			}
		}

		return ccmComponent;
	}

	public List<String> getFoldersToCcmFiles(String folder) {
		return foldersToCcmFiles.get(folder);
	}

	/**
	 * Save component selections to properties file
	 * 
	 * @param folder
	 * @param propertyFileName
	 * @param key
	 * @param value
	 */
	public static void writeProperty(String folder, String propertyFileName,
			String key, String value) {

		String fullPropertiesPath = propertiesDirectory + FILE_DEL
				+ folder + FILE_DEL + propertyFileName;
		FileInputStream in = null;
		try {
			Properties pro = new Properties();
			File f = new File(fullPropertiesPath);

			if (f.exists()) {
				in = new FileInputStream(f);
				pro.load(in);
			} else {
				f.getParentFile().mkdirs();
			}

			pro.setProperty(key, value);
			pro.store(new FileOutputStream(fullPropertiesPath), null);
			log.debug(fullPropertiesPath + " has been updated.");

		} catch (IOException e) {
			log.error(e, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error("Problems closing the stream.");
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Read component selections to properties file.
	 * 
	 * @param folder
	 * @param propertyFileName
	 * @param key
	 * @return
	 */
	public static String readProperty(String folder, String propertyFileName,
			String key) {
		String returnValue = null;

		String fullPropertiesPath = propertiesDirectory + FILE_DEL
				+ folder + FILE_DEL + propertyFileName;

		FileInputStream in = null;
		try {
			Properties properties = new Properties();
			File f = new File(fullPropertiesPath);

			if (!f.exists()) {
				return null;
			}

			in = new FileInputStream(f);
			properties.load(in);

			returnValue = properties.getProperty(key);

			properties.store(new FileOutputStream(fullPropertiesPath), null);

		} catch (IOException e) {
			log.error(e, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error("Problems closing the stream.");
				throw new RuntimeException(e);
			}
		}

		return returnValue;
	}

	/**
	 * Configure the rules for translating the application configuration file.
	 */
	private void configure() {

		digester.setUseContextClassLoader(true);

		digester.addRule("geaw-config", new GeawConfigRuleCCM("org.geworkbench.engine.config.rules.GeawConfigObjectCCM"));
      // Creates the top-level GUI window
		digester.addObjectCreate("geaw-config/gui-window", "org.geworkbench.engine.config.rules.GUIWindowObjectCCM");
		digester.addCallMethod("geaw-config/gui-window", "createGUI", 1);
		digester.addCallParam("geaw-config/gui-window", 0, "class");
		// Instantiates a plugin and adds it in the PluginResgistry
		digester.addRule("geaw-config/plugin", new PluginRuleCCM(
				"org.geworkbench.engine.config.rules.PluginObjectCCM"));
		// Registers a plugin with an extension point
		digester.addCallMethod("geaw-config/plugin/extension-point",
				"addExtensionPoint", 1);
		digester.addCallParam("geaw-config/plugin/extension-point", 0, "name");
		// Registers a visual plugin with the top-level application GUI.
		digester.addCallMethod("geaw-config/plugin/gui-area",
				"addGUIComponent", 1);
		digester.addCallParam("geaw-config/plugin/gui-area", 0, "name");
		// Associates the plugin's module methods to plugin modules
		digester.addCallMethod("geaw-config/plugin/use-module", "addModule", 2);
		digester.addCallParam("geaw-config/plugin/use-module", 0, "name");
		digester.addCallParam("geaw-config/plugin/use-module", 1, "id");
		// Turn subscription object on and off
		digester.addCallMethod("geaw-config/plugin/subscription",
				"handleSubscription", 2);
		digester.addCallParam("geaw-config/plugin/subscription", 0, "type");
		digester.addCallParam("geaw-config/plugin/subscription", 1, "enabled");
		// Sets up a coupled listener relationship involving 2 plugins.
		digester.addCallMethod("geaw-config/plugin/coupled-event",
				"registerCoupledListener", 2);
		digester.addCallParam("geaw-config/plugin/coupled-event", 0, "event");
		digester.addCallParam("geaw-config/plugin/coupled-event", 1, "source");
	}	


    
}