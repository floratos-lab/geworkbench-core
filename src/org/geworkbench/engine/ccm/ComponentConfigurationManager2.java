package org.geworkbench.engine.ccm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
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
import org.geworkbench.engine.preferences.GlobalPreferences;
import org.geworkbench.util.FilePathnameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

/**
 * Manages the dynamic loading and removal of components.
 * 
 * @author tg2321
 * @version $Id: ComponentConfigurationManager.java,v 1.1 2009/02/09 19:54:43
 *          keshav Exp $
 */
public class ComponentConfigurationManager2 {

	private static Log log = LogFactory.getLog(ComponentConfigurationManager2.class);
	String[] files = null;

	private static final String FILE_DEL = System.getProperty("file.separator");
	private static final String COMPONENT_DESCRIPTOR_EXTENSION = ".cwb.xml";

	private static String propertiesDirectory = FilePathnameUtils.getUserSettingDirectoryPath();
	
	private String componentsDirectory = UILauncher.getComponentsDirectory();
	
	private static ComponentConfigurationManager2 instance = null;
	
	public static ComponentConfigurationManager2 getInstance() {
		if(instance==null) {
			instance = new ComponentConfigurationManager2();
		}
		return instance;
	}
	
	/**
	 * Constructor
	 * 
	 * @param
	 */
	private ComponentConfigurationManager2() {
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
				String path = componentsDirectory + FILE_DEL + file.getPath();
				componentResource = new ComponentResource(path, false);
				log.debug("Created component resource " + file.getName());
			} catch (IOException e) {
				log.error("Could not initialize component resource '"  + file.getName() + "'.", e);
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
    	cwbFiles = new ArrayList<File>();
//    	Collection<ComponentResource> resources = ComponentRegistry.getRegistry().getAllComponentResources();
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
        log.info(cwbFiles.size()+" cwb files found under all resource directories");
	}
	
	
	public void removeOutVersionedFoldersFromCwbFileList() {
		List<File> tmpCwbFiles = new ArrayList<File>();
		boolean foundNewerVersion = false;
		for (int i = 0; i<cwbFiles.size(); i++) {
			foundNewerVersion = false;
			File file1 = cwbFiles.get(i);
			String cwbFileName1 = file1.getName();
			for (int j = 0; j<cwbFiles.size(); j++) {
				if (i==j){
					continue;
				}
				File file2 = cwbFiles.get(j);
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
		cwbFiles = tmpCwbFiles;
	}


	
    private void updateCwbFileList(File newDir) throws IOException {
       File[] newFiles = newDir.listFiles();
        if(newFiles==null || newFiles.length==0) return;
        for (File newFile : newFiles) {
            if (newFile.isDirectory()) {
            	updateCwbFileList(newFile);
            } else {
                if (newFile.getName().endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
                	
                	/* look for old components */
                	String newFileName = newFile.getName();
            		for (int i = 0; i<cwbFiles.size(); i++) {
            			File oldFile = cwbFiles.get(i);
            			String oldFileName = oldFile.getName();
            			if (newFileName.equals(oldFileName)){
            				cwbFiles.set(i, newFile);
            				break;
            			}
            		}
                }
            }
        }
    }

    File getCwbFile(String inFileName){
		for (int i = 0; i<cwbFiles.size(); i++) {
			File file = cwbFiles.get(i);
			String fileName = file.getName();
			if (inFileName.equals(fileName)){
				return file;
			}
		}
    	
    	return null; 
    }
    
    
    
    private void searchCwb(File dir) throws IOException {
        File[] files = dir.listFiles();
        if(files==null || files.length==0) return;
        for (File file : files) {
            if (file.isDirectory()) {
            	searchCwb(file);
            } else {
                if (file.getName().endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
                	cwbFiles.add(file);
                }
            }
        }
    }
    
	List<File> cwbFiles = null;

	public void loadSelectedComponents() {
		for (File file: cwbFiles) {
			// this is not really the right way to do it. just to support existing code. TODO
			String folder = resourceFolder(file);
			String ccmFileName = file.getName();
			
			String propFileName = ccmFileName.replace(COMPONENT_DESCRIPTOR_EXTENSION,
			".ccmproperties");

			String onOff = readProperty(folder, propFileName, "on-off");
			PluginComponent2 ccmComponent = getPluginsFromFile(file );
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
		/* parse the cwb.xml file */
		PluginComponent2 ccmComponent = null;
		if(filename.endsWith(COMPONENT_DESCRIPTOR_EXTENSION)) {
			ccmComponent = getPluginsFromFile(new File(filename));
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

	PluginComponent2 getPluginsFromFile(File file) {
		if (!file.exists()) {
			return null;
		}

		PluginComponent2 ccmComponent = null;
		
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
					String availableUpdate = null;
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

					ccmComponent = new PluginComponent2(name, clazz, version, availableUpdate,
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
   

	
	
	
	
	/*
	 * Download GEAR files from Tomcat server using apache's HttpClient
	 * 
	 * @param componentFolderName
	 * 
	 * @return boolean
	 */
	protected boolean downloadGEARFromServer(String componentFolderName) {

//remote_components_config_url=http://califano11.cgc.cpmc.columbia.edu:8080/componentRepository/deploycomponents.txt
//remote_components_url=http://califano11.cgc.cpmc.columbia.edu:8080/componentRepository/

		
//		String url = System.getProperty("remote_components_url");
		GlobalPreferences prefs = GlobalPreferences.getInstance();
		String url = prefs.getRCM_URL().trim();
		if (url == null || url == "") {
			log.info("No Remote Component Manager URL configured.");
			return false;
		}

		url += componentFolderName + ".gear";

		int beginIndex = url.lastIndexOf("/");
		String fileName = url.substring(beginIndex + 1);
		String tempFilePath = FilePathnameUtils
				.getTemporaryFilesDirectoryPath();

		String fullPathAndGEARName = tempFilePath + "\\components\\" + fileName;
		String fullComponentFolderName = tempFilePath + "\\components";
		File fullComponentFolder = new File(fullComponentFolderName);
		if (!fullComponentFolder.exists()) {
			fullComponentFolder.mkdirs();
		}

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(10, false));
		method.setFollowRedirects(true);

		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				return false;
			}

			InputStream inputStream = method.getResponseBodyAsStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					inputStream);
			FileOutputStream fileOutputStream = new FileOutputStream(
					fullPathAndGEARName);

			byte[] bytes = new byte[8192];
			int count = bufferedInputStream.read(bytes);
			while (count != -1 && count <= 8192) {
				fileOutputStream.write(bytes, 0, count);
				count = bufferedInputStream.read(bytes);
			}
			if (count != -1) {
				fileOutputStream.write(bytes, 0, count);
			}
			fileOutputStream.close();
			bufferedInputStream.close();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		File fullPathAndNameFile = new File(fullPathAndGEARName);
		if (!fullPathAndNameFile.exists()) {
			return false;
		}

		return true;
	}

	

	/*
	 * unzip GEAR file from the users temp
	 * 
	 * @param componentFolderName
	 * 
	 * @return void
	 */
	void installGEAR(String componentFolderName, String version) {
		File componentFolder = null;
		
		try {
			String tempFilePath = FilePathnameUtils.getTemporaryFilesDirectoryPath();
			tempFilePath += "\\components";
			File gearFile = new File(tempFilePath + "\\" + componentFolderName + ".gear");
			FileInputStream fileInputStream = new FileInputStream(gearFile);
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(fileInputStream));
			String outFilePath = UILauncher.getComponentsDirectory();
			String fullComponentPath = outFilePath
					+ FilePathnameUtils.FILE_SEPARATOR + componentFolderName
					+ "." + version;
			try {
				componentFolder = new File(fullComponentPath);
				if (componentFolder.exists()) {
					JOptionPane.showMessageDialog(null,
							"Could not install component "
									+ fullComponentPath
									+ ", please delete directory try again.",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				componentFolder.mkdir();
					
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Could not install component " + componentFolderName
								+ ", please restart geWorkbench",
						"Remote Component Update", JOptionPane.ERROR_MESSAGE);
				return;
			}

			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			final int BUFFER = 2048;
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				System.out.println("Extracting: " + zipEntry);
				int count;
				byte data[] = new byte[BUFFER];
				String zipEntryName = zipEntry.getName();
				zipEntryName.replaceFirst(zipEntryName, zipEntryName + "." + version);
				
				String fullPathAndName = fullComponentPath + FilePathnameUtils.FILE_SEPARATOR + zipEntryName;
				
				if (zipEntry.isDirectory()) {
					(new File(fullPathAndName)).mkdir();
					continue;
				}

				File outFile = new File(fullPathAndName);
				new File(outFile.getParent()).mkdirs();
				
				fileOutputStream = new FileOutputStream(outFile);
				bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER);
				while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}
				fileOutputStream.flush();
				bufferedOutputStream.flush();
				fileOutputStream.close();
				bufferedOutputStream.close();
			}
			zipInputStream.close();
			fileInputStream.close();

			//TODO Uncoment this line to delete gear files from temp folder 
			//gearFile.delete();

			updateCwbFileList(componentFolder);
			
			
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Http Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "IO Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} finally {
			// Release the connection.
		}
	}

	
	
	/*
	 * Look for GEAR files to install If found, install them.
	 * 
	 * @param void
	 * 
	 * @return void
	 */
	public void installComponents() {

		String componentsDirName = componentsDirectory;
		File componentsDir = new File(componentsDirName);
		if (!componentsDir.isDirectory()) {
			log.error("Component resource path is not a directory: "
					+ componentsDirName);
			return;
		}

		String tempFilePath = FilePathnameUtils.getTemporaryFilesDirectoryPath();
		String gearPathName = tempFilePath + "\\components";
		File gearFolder = new File(gearPathName);
		File[] gearFiles = gearFolder.listFiles();
		for (int i = 0; i < gearFiles.length; i++) {
			File gearFile = gearFiles[i];
			if (gearFile.isDirectory()) {
				continue;
			}
			
			String gearFileName = gearFile.getName();
			if (!gearFileName.endsWith(".gear")) {
				continue;
			}

			String componentFolderName = gearFileName.replace(".gear", "");
			String fullComponentFolderName = componentsDir + "\\"
					+ componentFolderName;
			File oldComponentFolder = new File(fullComponentFolderName);
			if (oldComponentFolder.exists()) {
				try {
					recursiveDelete(oldComponentFolder);
					oldComponentFolder.delete();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							" There was a problem removing the old component "
									+ componentFolderName
									+ ". Please restart again.",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (oldComponentFolder.exists()) {
				JOptionPane
						.showMessageDialog(
								null,
								"The old component "
										+ componentFolderName
										+ " has not been completely removed. Please restart again.",
								"Remote Component Update",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			installComponent(componentFolderName);
			gearFile.delete();
		}
	}

	/*
	 * Delete old component folder and unzip GEAR file from the users temp
	 * directory into the components folder
	 * 
	 * @param componentFolderName
	 * 
	 * @return void
	 */
	private void installComponent(String componentFolderName) {

		try {

			String tempFilePath = FilePathnameUtils
					.getTemporaryFilesDirectoryPath();
			tempFilePath += "\\components";
			File gearFile = new File(tempFilePath + "\\" + componentFolderName + ".gear");
			FileInputStream fileInputStream = new FileInputStream(gearFile);
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(fileInputStream));
			String outFilePath = UILauncher.getComponentsDirectory();
			String fullComponentPath = outFilePath + FilePathnameUtils.FILE_SEPARATOR + componentFolderName;
			try {
				File componentFolder = new File(fullComponentPath);
				if (componentFolder.exists()) {
					JOptionPane.showMessageDialog(null,
							"Could not install component "
									+ componentFolderName
									+ ", please restart geWorkbench",
							"Remote Component Update",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				componentFolder.mkdir();

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Could not install component " + componentFolderName
								+ ", please restart geWorkbench",
						"Remote Component Update", JOptionPane.ERROR_MESSAGE);
				return;
			}

			FileOutputStream fileOutputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			final int BUFFER = 2048;
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				System.out.println("Extracting: " + zipEntry);
				int count;
				byte data[] = new byte[BUFFER];
				String zipEntryName = zipEntry.getName();
				String fullPathAndName = fullComponentPath
						+ FilePathnameUtils.FILE_SEPARATOR + zipEntryName;

				if (zipEntry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					(new File(fullPathAndName)).mkdir();
					continue;
				}

				fileOutputStream = new FileOutputStream(fullPathAndName);
				bufferedOutputStream = new BufferedOutputStream(
						fileOutputStream, BUFFER);
				while ((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}
				fileOutputStream.flush();
				bufferedOutputStream.flush();
				fileOutputStream.close();
				bufferedOutputStream.close();
			}
			zipInputStream.close();
			fileInputStream.close();

			gearFile.delete();

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Http Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "IO Exception "
					+ componentFolderName + ", please restart geWorkbench",
					"Remote Component Update", JOptionPane.ERROR_MESSAGE);
		} finally {
			// Release the connection.
		}
	}

	/*
	 * Recursively delete folders and files under the dirPath
	 * 
	 * TODO move this to a utility class
	 * 
	 * @param dirPath
	 * 
	 * @return void
	 */
	private void recursiveDelete(File dirPath) {
		String[] ls = dirPath.list();

		for (int idx = 0; idx < ls.length; idx++) {
			File file = new File(dirPath, ls[idx]);
			if (file.isDirectory()) {
				recursiveDelete(file);
			}

			file.delete();
		}
	}
}