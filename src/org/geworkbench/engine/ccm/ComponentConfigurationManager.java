package org.geworkbench.engine.ccm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
	String[] files = null;

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
	public void loadAllComponentFolders(File componentDirectory) {
    	cwbFile = new ArrayList<File>();
    	if(!componentDirectory.isDirectory()) {
    		log.error("component directory is not a directory");
    	}

        for (File componentDir: componentDirectory.listFiles()) {
        	log.info("searching resource "+componentDir);

        	File resourceDir = new File(componentDir+"/classes");
        	try {
        		searchCwb(resourceDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        Collections.sort(cwbFile);
        log.info(cwbFile.size()+" cwb files found under all resource directories");
	}
	
	public void removeOutVersionedFoldersFromCwbFileList() {
		List<File> tmpCwbFiles = new ArrayList<File>();
		boolean foundNewerVersion = false;
		for (int i = 0; i<cwbFile.size(); i++) {
			foundNewerVersion = false;
			File file1 = cwbFile.get(i);
			String cwbFileName1 = file1.getName();
			for (int j = 0; j<cwbFile.size(); j++) {
				if (i==j){
					continue;
				}
				File file2 = cwbFile.get(j);
				String cwbFileName2 = file2.getName();
				if (cwbFileName1.equals(cwbFileName2)){
					String folder1 = resourceFolder(file1);
					int versionIndex1 = folder1.indexOf(".");
					Double version1 = new Double(0.0);
					int folder1length = folder1.length();
					if (versionIndex1 > -1 && folder1length >= versionIndex1+1){
						try{
							version1 = new Double(folder1.substring(versionIndex1+1));							
						}catch (Exception e) {
							version1 = new Double(0.0);
						}
					}

					String folder2 = resourceFolder(file2);
					int versionIndex2 = folder2.indexOf(".");
					Double version2 = new Double(0.0);
					int folder2length = folder2.length();
					if (versionIndex2 > -1 && folder2length >= versionIndex2+1){
						try{
							String ver = folder2.substring(versionIndex2+1);
							version2 = new Double(ver);
						}catch (Exception e) {
							version2 = new Double(0.0);
						}
					}
					
					if (version2.compareTo(version1) > 0){
						foundNewerVersion = true;
						break;
					}
				}
			}
			
			if (foundNewerVersion){
				continue;
			}
			tmpCwbFiles.add(file1);
		}		
		cwbFile = tmpCwbFiles;
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
		ComponentRegistry.getRegistry().addComponentResource(folder, componentResource);
		
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
				Class<?> clazz = pluginDescriptor1.getClass();
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

			componentRegistry.removeComponent(pluginClazzName);
			componentRegistry.removePlugin( ccmComponent.getPluginId() );

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
			componentRegistry.removeComponentResource(folderName);
		}
		
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
					String[] category = new String[0];
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

							str = element.getAttributeValue("category");
							if(str!=null) {
								category = str.trim().split(",");
								for(int i=0; i<category.length; i++) {
									category[i] = category[i].trim().toLowerCase();
								}
							}
							
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
							resource, category, required, related);
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
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			cwbDigester = new Digester(saxParser);

			cwbDigester.setUseContextClassLoader(true);

			// Instantiates a plugin and adds it in the PluginResgistry
			cwbDigester.addRule("component-descriptor/plugin", new PluginRule(
					"org.geworkbench.engine.config.rules.PluginObject"));

			// Registers a visual plugin with the top-level application GUI.
			cwbDigester.addCallMethod("component-descriptor/plugin/gui-area",
					"addGUIComponent", 1);
			cwbDigester.addCallParam("component-descriptor/plugin/gui-area", 0, "name");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}