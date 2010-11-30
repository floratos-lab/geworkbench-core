package org.geworkbench.engine.config.rules;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

import org.geworkbench.engine.config.ComponentMetadata;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.util.Debug;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version $Id$
 */

/**
 * Describes the object that is pushed on the <code>UILauncher</code> stack
 * when processing the pattern "geaw-config/plugin". It will create the
 * <code>PluginDescriptor</code> for the plugin and register it with the
 * <code>PluginRegistry</code>. It will also handle all other registration
 * tasks associated with this plugin, including:
 * <UI>
 * <LI> register the event listeners (if any) associated with the plugin
 * with the appropriate event sources.</LI>
 * <LI> associate any <code>ActionListener</code>s declared by the plugin
 * with the requested menu items.
 * </UI>
 */
public class PluginObject {
    // ---------------------------------------------------------------------------
    // --------------- Constants
    // ---------------------------------------------------------------------------

    public static final String COMPONENT_DESCRIPTOR_EXTENSION = ".cwb.xml";

    // ---------------------------------------------------------------------------
    // --------------- Instance and static variables
    // ---------------------------------------------------------------------------
    /**
     * The plugin component that last had the focus.
     */
    private static PluginDescriptor lastInFocus = null;

    /**
     * Used to count up as we load plugins because of the digester.
     */
    private static int loadOrderTracker = 0;

    /**
     * The <code>PluginDescriptor</code> created for this plugin.
     */
    private PluginDescriptor compDes = null;

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public PluginObject() {

    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	public static ComponentMetadata processComponentDescriptor(String resourceName, Class<?> type) throws IOException, JDOMException, NotMenuListenerException, MalformedMenuItemException, NotVisualPluginException {
        // Look for descriptor file
        ComponentMetadata metadata = new ComponentMetadata(type, resourceName);
        String filename = type.getSimpleName() + COMPONENT_DESCRIPTOR_EXTENSION;
        SAXBuilder builder = new SAXBuilder();
        InputStream in = type.getResourceAsStream(filename);
        if (in != null) {
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            if (root.getName().equals("component-descriptor")) {
                root = root.getChild("component");
                if (root != null) {
                    // Check for optional attributes
                    String iconName = root.getAttributeValue("icon");
                    if (iconName != null) {
                        URL url = type.getResource(iconName);
                        if (url != null) {
                            ImageIcon icon = new ImageIcon(url);
                            if (icon != null) {
                                metadata.setIcon(icon);
                            }
                        } else {
                            System.out.println("Icon for component '" + type + "' not found: " + iconName);
                        }
                    }
                    String commonName = root.getAttributeValue("name");
                    if (commonName != null) {
                        metadata.setName(commonName);
                    }
                    String version = root.getAttributeValue("version");
                    if (version != null) {
                        metadata.setVersion(version);
                    }
                    String description = root.getText().trim();
                    if ((description != null) && (description.length() > 0)) {
                        metadata.setDescription(description);
                    }
                    java.util.List<Element> elements = root.getChildren();
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        if (element.getName().equals("menu-item")) {
                            metadata.addMenuInfo(
                                    element.getAttributeValue("path"),
                                    element.getAttributeValue("mode"),
                                    element.getAttributeValue("var"),
                                    element.getAttributeValue("icon"),
                                    element.getAttributeValue("accelerator")
                            );
                        } else if (element.getName().equals("online-help")) {
                            metadata.setHelpSet(element.getAttributeValue("helpSet"));
                        }
                    }
                    if (metadata.getName() == null) {
                        metadata.setName(type.getSimpleName());
                    }
                    return metadata;
                }
            }
        }
        ComponentMetadata defaultData = new ComponentMetadata(type, resourceName);
        defaultData.setName(type.getSimpleName());
        return defaultData;
    }
    
    /**
     * Creates a new plugin in and adds it to the <code>PluginRegistry</code>.
     * The plugin is instantiated by a call to its default constructor.
     *
     * @param id           Id to be used for the new plugin.
     * @param name         Name to be used for the new plugin.
     * @param className    Class name for the new plugin.
     * @param resourceName Resource from which to load the plugin.
     */
    public void createPlugin(String id, String name, String className, String resourceName) {
    	PluginRegistry.setNameMap(className, name); // mark's fix bug 1963
        compDes = new PluginDescriptor(className, id, name, resourceName, loadOrderTracker);
        loadOrderTracker++;
        Debug.debug("PluginObject::createPlugIn --> Creating id = " + id + " name = " + name + " className = " + className + " resourceName = " + resourceName);
        PluginRegistry.addPlugin(compDes);
        // Digest component descriptor
        try {
            ComponentMetadata metadata = processComponentDescriptor(resourceName, compDes.getPluginClass());
            compDes.setComponentMetadata(metadata);
        } catch (Exception e) {
            System.out.println("Problem parsing component descriptor for component: " + compDes.getPluginClass() + ".");
        }
    }

    /**
     * Invoked by the Digester. Associates the plugin described by this
     * <code>PluginObject</code> with the designated extension point.
     *
     * @param extPoint Name of the extesion point
     */
    public void addExtensionPoint(String extPoint) {
        // @todo - watkin - Can probably be phased out and replaced with @Module-annotated interfaces.
        PluginRegistry.addPlugInAtExtension(compDes, extPoint);
        org.geworkbench.util.Debug.debug("PluginObject::addExtensionPoint --> Adding plugin id = " + compDes.getID() + " to extension point " + extPoint);
    }

    /**
     * Invoked by the Digester. Adds the plugin (which must implement the
     * <code>VisualPlugin</code> interface) to the top-level GUI window, at the
     * desiganted GUI area.
     *
     * @param guiAreaName The area where to add the visual component.
     */
    public void addGUIComponent(String guiAreaName) {
        // Make sure that the plugin has a visual representation
        if (!compDes.isVisualPlugin()) {
            System.err.println("PluginObject::addGUIComponent - Attempt to add as " + "GUI component the plugin with id = " + compDes.getID() + ", which does not implement " + "interface VisualPlugin.\n");
            return;
        }
        compDes.setVisualLocation(guiAreaName);
        GeawConfigObject.getGuiWindow().addToContainer(guiAreaName, ((VisualPlugin) compDes.getPlugin()).getComponent(), compDes.getLabel(), compDes.getPluginClass());
        PluginRegistry.addVisualAreaInfo(guiAreaName, (VisualPlugin) compDes.getPlugin());
    }

    /**
     * Invoked by the Digester. Links a module name with an ID in the plugin descriptor.
     *
     * @param name
     * @param id
     */
    public void addModule(String name, String id) {
        compDes.setModuleID(name.toLowerCase(), id);
    }

    public void handleSubscription(String typeName, String enabled) {
        try {
            Class<?> type = Class.forName(typeName);
            // Todo- ensure that there is a @subscribe method corresponding to this type or complain if there is not.
            if ("No".equalsIgnoreCase(enabled)) {
                compDes.addTypeToSubscriptionIgnoreSet(type);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class '" + typeName + "'.");
        }
    }

    /**
     * Invoked by the Digester. Registers an <code>ActionListener</code> with
     * a menu item.
     *
     * @param path
     * @param mode
     * @param var
     * @param icon
     * @param accelerator
     */
    public static void registerMenuItem(PluginDescriptor descriptor, String path, String mode, String var, String icon, String accelerator) throws NotMenuListenerException, NotVisualPluginException, MalformedMenuItemException {
        JMenuItem menuItem;
        ActionListener menuListener;
        int i;
        // Allowed values for the 'mode' parameter.
        final String[] menuModifiers = {"onFocus", "always"};
        // First, check that the plugin wishing to register the ActionListener
        // implements MenuListener.
        if (!descriptor.isMenuListener())
            throw new NotMenuListenerException("PluginObject::registerMenuItem - " + "Attempt to register a menu item listener by component with ID = " + descriptor.getID() + ", that is not a MenuListener.");
        // Then, verify that the 'mode' variable takes one among the values that
        // are permitted.
        // Just in case
        for (i = 0; i < menuModifiers.length; ++i)
            if (mode.compareTo(menuModifiers[i]) == 0)
                break;
        if (i == menuModifiers.length)
            throw new MalformedMenuItemException("PluginObject::registerMenuItem - " + "Invalid value found for argument 'mode' = " + mode);
        // Then, if the mode of listening is "onFocus", check that the plugin has
        // a visual representation.
        if ((mode.compareTo("onFocus") == 0) && (!descriptor.isVisualPlugin()))
            throw new NotVisualPluginException("PluginObject::registerMenuItem - " + "Attempt to register a 'onFocus' menu item listener by component with ID = " + descriptor.getID() + ", which is not a VisualPlugin.");
        // Return the menu item corresponding to the designated path. If the menu
        // item does not already exist, it gets created.
        try {
            menuItem = getMenuItem(descriptor, path, icon, accelerator);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return;
        }

        // Query the plugin to get the ActionListener that should be
        // used with this menu item.
        menuListener = ((MenuListener) descriptor.getPlugin()).getActionListener(var);
        if (menuListener != null)
            descriptor.addMenuListener(menuItem, menuListener, mode);
        // If the 'mode' for the listener is "always", go on and add the listener
        // to the menu item.
        if (mode.compareTo("always") == 0) {
            menuItem.addActionListener(menuListener);
            menuItem.setEnabled(true);
        }

        //stores the menu item info into the PluginDescriptor
        descriptor.addMenuItemInfo(path, mode, var, icon, accelerator);
    }

    /**
     * Invoked at the end of parsing, for:
     * <UI>
     * <LI> setting up the coupled event relationships, </LI>
     * <LI> attach a focus handler to the GUI representation of this plugin.</LI>
     * </UI>
     */
    public void finish() {
        ComponentRegistry registry = ComponentRegistry.getRegistry();
        registry.registerSubscriptions(compDes.getPlugin(), compDes);

        // If the compDes component is a visual plugin, add this object as a listener
        // to focus events from the plugin.
        if (compDes.isVisualPlugin())
            attachFocusListener(((VisualPlugin) compDes.getPlugin()).getComponent());
    }

    /**
     * Returns the menu item specified by the <code>path</code> parameter. E.g.,
     * if <code>path = "File.Save.Workspace"</code>, the menu item will be
     * found within the "Save" submenu of the main "File" menu. Arbitrarily deep
     * menu nesting is allowed. If such a menu item does not already exist in the
     * menu structure, it is created. If possible, the <code>setIcon</code> and
     * <code>setAccelerator</code> methods of the returned menu item will have been
     * set using the parameters <code>icon</code> and <code>accelerator</code>.
     *
     * @param path        Describes the position within the menu structure of the menu item.
     * @param icon        Optional icon to be associated with the menu item.
     * @param accelerator Optional accelerator to be associated with the menu item.
     * @return The designated menu item.
     * @throws MalformedMenuPathException
     * @throws DynamicMenuItemException
     * @throws ClassNotFoundException
     */
    private static JMenuItem getMenuItem(PluginDescriptor descriptor, String path, String icon, String accelerator) throws MalformedMenuItemException, DynamicMenuItemException, ClassNotFoundException {
        StringTokenizer tokens; // Breaks up 'path' into its parts.
        JMenu[] mainMenuItems;
        String topLevelMenuText;
        JMenu newMenu, topMenu;
        int len;
        int i;
        // Identify the various sub-menus that lead to the new/existing menu item.
        // There must be at least one such sub-menu (the top-level one).
        tokens = new StringTokenizer(path, GeawConfigObject.menuItemDelimiter);
        if (tokens.countTokens() <= 1)
            throw new MalformedMenuItemException("PluginObject::getMenuItem - " + "The menu path '" + path + "' is not properly formed.");
        topLevelMenuText = tokens.nextToken();
        // Initialize 'mainMenuItems' with the top-level menu items
        MenuElement[] temp = GeawConfigObject.getMenuBar().getSubElements();
        mainMenuItems = new JMenu[temp.length];
        len = mainMenuItems.length;
        for (i = 0; i < len; ++i)
            mainMenuItems[i] = (JMenu) temp[i];
        // Find (or create, if not already there) the top-level sub-menu designated
        // in the 'path' argument.
        for (i = 0; i < len; ++i)
            if (mainMenuItems[i].getText().compareTo(topLevelMenuText) == 0)
                break;
        // If the top-level submenu is not there, create it.
        if (i == len) {
            newMenu = new JMenu();
            // newMenu.setFont(GeawConfigObject.menuItemFont);
            newMenu.setText(topLevelMenuText);
            // Make sure that the "Help" menu remains at the end.
            GeawConfigObject.getMenuBar().remove(GeawConfigObject.getHelpMenu());
            GeawConfigObject.getMenuBar().add(newMenu);
            GeawConfigObject.getMenuBar().add(GeawConfigObject.getHelpMenu());
            topMenu = newMenu;
        } else
            topMenu = mainMenuItems[i];
        return addMenuItem(descriptor, tokens, topMenu, icon, accelerator);
    }

    /**
     * Recursively navigates the existing menu structure until it finds the
     * spot where the menu item specified by parameter <code>tokens</code> should
     * reside. If no such menu item already exists, it is being created.
     *
     * @param tokens      A list of successive menu titles desribing the (remaining)
     *                    hierarchical menu structure of the menu item being processed.
     * @param parentMenu  The current root of the explored menu structure.
     * @param icon        An optional icon to be associated with the menu item.
     * @param accelerator An optional accelerator to be associated with the menu item.
     * @return The found (or created) menu item that is placed at the requested point
     *         within the menu structure.
     * @throws DynamicMenuItemException
     * @throws ClassNotFoundException
     */
    private static JMenuItem addMenuItem(PluginDescriptor descriptor, StringTokenizer tokens, JMenu parentMenu, String icon, String accelerator) throws DynamicMenuItemException, ClassNotFoundException {
        JMenuItem[] parentMenuItems;
        String menuText;
        JMenu newMenu;
        JMenuItem theMenuItem;
        int len;
        int i;
        menuText = tokens.nextToken();
        // Initialize 'parentMenuItems' with the menu items of the 'parentMenu'.
        MenuElement[] temp = parentMenu.getPopupMenu().getSubElements();
        parentMenuItems = new JMenuItem[temp.length];
        len = parentMenuItems.length;
        for (i = 0; i < len; ++i)
            parentMenuItems[i] = (JMenuItem) temp[i];
        // Check if there is already an existing menu item with the text we are
        // currently ptocessing.
        for (i = 0; i < len; ++i)
            if (parentMenuItems[i].getText().compareTo(menuText) == 0)
                break;
        if (i == len)
            // Check if this is the final part of the original processing path.
            // In that case, create and return the new menu item.
            if (tokens.countTokens() == 0) {
                theMenuItem = new JMenuItem(menuText);
                theMenuItem.setEnabled(false);
                if (icon != null)
                    theMenuItem.setIcon(new ImageIcon(descriptor.getPlugin().getClass().getResource(icon)));
                parentMenu.add(theMenuItem);
                return theMenuItem;
            } else {
                newMenu = new JMenu();
                // newMenu.setFont(GeawConfigObject.menuItemFont);
                newMenu.setText(menuText);
                parentMenu.add(newMenu);
                return addMenuItem(descriptor, tokens, newMenu, icon, accelerator);
            }

        else {
            if (Class.forName("javax.swing.JMenu").isAssignableFrom(parentMenuItems[i].getClass())) {
                if (tokens.countTokens() == 0)
                    throw new DynamicMenuItemException("PluginObject::addMenuItem - " + "Final menu item conflict with existing submenu.");
                else
                    return addMenuItem(descriptor, tokens, (JMenu) parentMenuItems[i], icon, accelerator);
            } else {
                if (tokens.countTokens() != 0)
                    throw new DynamicMenuItemException("PluginObject::addMenuItem - " + "A subpath in a new menu item conflict with an existing terminal " + "menu item.");
                else {
                    // If the existing menu item does not have an associate icon,
                    // add the icon requested by the <menu-item> currently processed.
                    if (parentMenuItems[i].getIcon() == null && icon != null)
                        parentMenuItems[i].setIcon(new ImageIcon(descriptor.getPluginClass().getResource(icon)));
                    return parentMenuItems[i];
                }

            }

        }

    }

    /**
     * Focus broker, delegating focus to the plugin that was most recently selected
     * by the user via a mouse click. Component **WITHOUT** focus loose access
     * to the menu items they have registered (in the application configuration
     * file) with the <code>onFocus</code> modifier.
     */
    private MouseAdapter focusHandler = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            if (lastInFocus != compDes) {
                if (lastInFocus != null)
                    lastInFocus.disableFocusMenuItems();
                compDes.enableFocusMenuItems();
                lastInFocus = compDes;
            }

        }

    };

    /**
     * Recursively add <code>focusHandler</code> as a mouse listener to all
     * components contained in the GUI container for this plugin.
     *
     * @param m The component/container when to attach <code>focusHandler</code>.
     */
    private void attachFocusListener(Component m) {
        m.addMouseListener(focusHandler);
        /*
         * This exceptional case is to handle the problem that cytospace's tabbed pane not responsive inside geWorkbench. 
         */
        Container parent = m.getParent(); 
        if (parent!=null && parent.getClass().getName().equals(
				"cytoscape.view.cytopanels.CytoPanelImp")
				&& m.getClass().getName().equals("javax.swing.JTabbedPane")) {
			return;
		}
        
        if (m instanceof Container) {
            Component[] compList = ((Container) m).getComponents();
            for (int i = 0; i < compList.length; ++i)
                attachFocusListener(compList[i]);
        }

    }

}

