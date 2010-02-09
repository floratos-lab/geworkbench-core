package org.geworkbench.engine.ccm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.management.ComponentResource;
import org.geworkbench.engine.management.TypeMap;
import org.geworkbench.util.FilePathnameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

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

	private static final String FILE_DEL = System.getProperty("file.separator");
	private static final String COMPONENT_DESCRIPTOR_EXTENSION = ".cwb.xml";

	private static String propertiesDirectory = FilePathnameUtils.getUserSettingDirectoryPath();
	
	private String componentsDirectory = UILauncher.getComponentsDirectory();
	
	private static ComponentConfigurationManager instance = null;
	
	public static ComponentConfigurationManager getInstance() {
		if(instance==null) {
			instance = new ComponentConfigurationManager();
		}
		return instance;
	}
	
	/**
	 * Constructor
	 * 
	 * @param
	 */
	private ComponentConfigurationManager() {
		File dir = new File(componentsDirectory);
		if (!dir.isDirectory()) {
			log.warn("Supplied components directory is not a directory: "
					+ componentsDirectory);
			return;
		}
		files = dir.list();
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
				String path = componentsDirectory + FILE_DEL
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
	 * .cwb.xml files decides which rows
	 * should be displayed in the CCM window.
	 * 
	 * @return {@link ArrayList}
	 */
	// TODO the name is out-of-date, should be called something like "find all component descriptor files"
	public void loadAllComponentFolders() {
    	cwbFile = new ArrayList<File>();
    	Collection<ComponentResource> resources = ComponentRegistry.getRegistry().getAllComponentResources();

        for (ComponentResource resource: resources) {
        	log.info("searching resource "+resource.getDir());

        	File resourceDir = new File(resource.getDir()+"/classes");
        	try {
        		searchCwb(resourceDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        log.info(cwbFile.size()+" cwb files found under all resource directories");
	}
	
    private void searchCwb(File dir) throws IOException {
        File[] files = dir.listFiles();
        if(files==null || files.length==0) return;
        for (File file : files) {
            if (file.isDirectory()) {
            	searchCwb(file);
            } else {
                if (file.getName().endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
                	cwbFile.add(file);
                }
            }
        }
    }
    
	List<File> cwbFile = null;

	public void loadSelectedComponents() {
		for (File file: cwbFile) {
			// this is not really the right way to do it. just to support existing code. TODO
			String folder = resourceFolder(file);
			String ccmFileName = file.getName();
			
			String propFileName = ccmFileName.replace(COMPONENT_DESCRIPTOR_EXTENSION,
			".ccmproperties");

			String onOff = readProperty(folder, propFileName, "on-off");
			PluginComponent ccmComponent = getPluginsFromFile(file );
			if(ccmComponent==null) {
				log.error(".cwb.xml file "+file+" failed to be loaded");
				continue;
			}
			String name = ccmComponent.getName();
			UILauncher.setProgressBarString(name);
			boolean loadByDefault = ccmComponent.getLoadByDefault();
			
			if (onOff == null && loadByDefault){
				writeProperty(folder, propFileName, "on-off", "true");
				onOff = readProperty(folder, propFileName, "on-off");
			}
			if (onOff != null && onOff.equals("true")) {
				log.info("loading "+file);
				loadComponent(file);
			} else {
				log.info(file + "turned off");
			}
		}

		GeawConfigObject.recreateHelpSets();
	}
		
	// this eventually should not be necessary if the cwb/resource is managed properly
	private String resourceFolder(File file) {
		String path = file.getAbsolutePath();
		int index = path.indexOf(componentsDirectory)+componentsDirectory.length()+1;
		return path.substring(index, path.indexOf(FILE_DEL, index));
	}
	
	/**
	 * Load a plugin component described in a .cwb.xml file. resoruceMap should have been initialized already.
	 */
	void loadComponent(File file) {
		String folder = resourceFolder(file);
		/* create component resource */
		ComponentResource componentResource = createComponentResource(folder);

		/* add resource to registry */
		Map<String, ComponentResource> resourceMap = ComponentRegistry
				.getRegistry().getComponentResourceMap();
		ComponentResource existingComponentResource = resourceMap.get(folder);
		if (existingComponentResource==null){
			resourceMap.put(folder, componentResource);
		}
		
		InputStream is = null;

		try {
			is = new FileInputStream(file);
			cwbDigester.parse(is);
			log.debug(file+" loaded");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
	public boolean removeComponent(String folderName, String filename) {

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

		// FIXME why parse again? can't we get this info another way?
		/* parse the ccm.xml file */
		PluginComponent ccmComponent = null;
		if(filename.endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
			ccmComponent = getPluginsFromFile(new File(
				filename));
		} else {
			return false;
		}

		if (ccmComponent == null) return false;
		
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
		// beginning of processing the plugin
		final String pluginClazzName = ccmComponent.getClazz();
		PluginDescriptor pluginDescriptor = PluginRegistry
					.getPluginDescriptor(ccmComponent.getPluginId());

		if (pluginDescriptor != null) {
			
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
			String id = ccmComponent.getPluginId();
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
			String pluginToRemove = ccmComponent.getPluginId();
			PluginDescriptor tempPluginDescriptor = idToDescriptor
					.get(pluginToRemove);
			if (tempPluginDescriptor != null) {
				idToDescriptor.remove(pluginToRemove);
			}

		}// end of processing the plugin

		/* ComponentRegistry.idToDescriptor */
		/* If other Plugins are using the same Component Resource, don't remove the Resource */
		int foldersInUse = 0;
		for (int i = 0; i < componentVector.size(); i++) {
			PluginDescriptor pd = componentVector.get(i);
			if (pd == null){
				continue;
			}
			
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

	PluginComponent getPluginsFromFile(File file) {
		if (!file.exists()) {
			return null;
		}

		PluginComponent ccmComponent = null;
		
		SAXBuilder builder = new SAXBuilder();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);

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
				if (root.getName().equals("component-descriptor")) {
					String name = null;
					String clazz = null;
					String version = null;
					String author = null;
					String authorUrl = null;
					String tutorialUrl = null;
					String toolUrl = null;
					String description = null;
					String license = null;
					boolean mustAccept = false;
					String documentation = null;
					boolean loadByDefault = false;
					boolean hidden = false;
					String pluginName = null;
					String pluginId = null;
					String resource = null;
					boolean isAnalysis = false;
					boolean isVisualizer = false;
					List<String> required = new ArrayList<String>();
					List<String> related = new ArrayList<String>();

					for (Object objElement: root.getChildren() ) {
						Element element = (Element)objElement;
						if (element.getName().equals("component")) {
							clazz = element
									.getAttributeValue("class");
							name = element
									.getAttributeValue("name");
							version = element
									.getAttributeValue("version");
							author = element
									.getAttributeValue("author");
							authorUrl = element
									.getAttributeValue("authorURL");
							tutorialUrl = element
									.getAttributeValue("tutorialURL");
							toolUrl = element
									.getAttributeValue("toolURL");
							description = element
									.getAttributeValue("description");
							String str = element.getAttributeValue("mustAccept"); 
							if(str!=null && str.equalsIgnoreCase("true"))
								mustAccept = true;
							documentation = element
									.getAttributeValue("documentation");
							str = element.getAttributeValue("loadByDefault");
							if(str!=null && str.equalsIgnoreCase("true"))
								loadByDefault = true;
							str = element.getAttributeValue("hidden");
							if(str!=null && str.equalsIgnoreCase("true"))
								hidden = true;
							
							for (Object obj: element.getChildren() ) {
								Element subElement = (Element)obj;
								String type = subElement.getName();
								String dependencyClass = subElement
										.getAttributeValue("class");
								if (type.equals("required-component")) {
									required.add(dependencyClass);
								} else if (type.equals("related-component")) {
									related.add(dependencyClass);
								} else if (type.equals("license")) {
									license = subElement.getTextTrim();
								}
							}

							str = element.getAttributeValue("analysis");
							if(str!=null && str.equalsIgnoreCase("true"))
								isAnalysis = true;
							str = element.getAttributeValue("visualizer");
							if(str!=null && str.equalsIgnoreCase("true"))
								isVisualizer = true;
						}

						if (element.getName().equals("plugin")) {
							pluginId = element.getAttributeValue("id");
							pluginName = element.getAttributeValue("name");
							String pluginClazz = element.getAttributeValue("class");
							if(!pluginClazz.equals(clazz)) {
								log.error("plugin element and component have different class names in "+file.getName());
							}
							resource = element.getAttributeValue("source");
						}
					}

					ccmComponent = new PluginComponent(name, clazz, version,
							author, authorUrl, tutorialUrl, toolUrl,
							description, license, mustAccept,
							documentation, loadByDefault, hidden,
							pluginName, pluginId,
							resource, isAnalysis, isVisualizer, required, related);
				} // end of if open element is correct
			}
		} catch (Exception e) {
			log.error("ERROR LOADING:"+file, e);
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error("ERROR LOADING:"+file, e);
				return null;
			}
		}

		return ccmComponent;
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

	private static Digester cwbDigester = null;
	static {
		cwbDigester = new Digester(new org.apache.xerces.parsers.SAXParser());

		cwbDigester.setUseContextClassLoader(true);

		// Instantiates a plugin and adds it in the PluginResgistry
		cwbDigester.addRule("component-descriptor/plugin", new PluginRule(
				"org.geworkbench.engine.config.rules.PluginObject"));

		// Registers a visual plugin with the top-level application GUI.
		cwbDigester.addCallMethod("component-descriptor/plugin/gui-area",
				"addGUIComponent", 1);
		cwbDigester.addCallParam("component-descriptor/plugin/gui-area", 0, "name");
	}
    
}