package org.geworkbench.engine.config.rules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.GUIFramework;
import org.geworkbench.util.SplashBitmap;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Describes the object that is pushed on the <code>UILauncher</code> stack
 * when processing the top-most tag, namely the pattern "geaw-config". This
 * object creates and maintains the "global" variables of the application.
 */
public class GeawConfigObject {

    static Log log = LogFactory.getLog(GeawConfigObject.class);

    /**
     * The name of the property within <code>applications.properties</code> which
     * contains the location of the master help file.
     */
    final static String MASTER_HS_PROPERTY_NAME = "master.help.set";

    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    /**
     * The top-level window for the application.
     */
    private static GUIFramework guiWindow = null;
    /**
     * The menu bar of the main GUI window.
     */
    private static JMenuBar guiMenuBar = null;
    /**
     * Stores the help menu from the menu bar. Used to enforce that the
     * "Help" option always appears as the right-most entry at the menu bar.
     */
    private static JMenu helpMenu = null;
    /**
     * The master help set for the application. Help sets for individual
     * componenents will be appended to the master help in the order in which
     * they are encountered within the configuration file.
     */
    protected static HelpSet masterHelp = null;
    protected static TreeMap<String, HelpSet> sortedHelpSets = new TreeMap<String, HelpSet>();
    // ---------------------------------------------------------------------------
    // --------------- Properties
    // ---------------------------------------------------------------------------
    // This is a bad place to put these parameters. They should be assigned to
    // appropriate application properties.
    /**
     * The font to use for the menu items.
     */
    // static Font              menuItemFont = new java.awt.Font("Dialog", 1, 11);
	/**
     * The titles of the default top level menus. Menus will appear in the
     * order listed in this array.
     */
    static String[] topMenus = {"File", "Edit", "View", "Commands", "Tools", "Help"};
    /**
     * The character that delimits the menu items within the 'path' attribute
     * of the <code>&lt;menu-item&gt;</code> element in the application
     * configuration file.
     */
    public static String menuItemDelimiter = ".";

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public GeawConfigObject() {
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Return the top level window for the application.
     */
    public static GUIFramework getGuiWindow() {
        return guiWindow;
    }

    /**
     * Return the menu bar for the top level window of the application.
     */
    public static JMenuBar getMenuBar() {
        return guiMenuBar;
    }

    /**
     * Sets the top level application window and initializes that window's
     * menu bar.
     *
     * @param gui The top level window.
     */
    public static void setGuiWindow(GUIFramework gui) {
        guiWindow = gui;
        guiMenuBar = newMenuBar();
        gui.setJMenuBar(guiMenuBar);
    }

    /**
     * Returns a handle to the help menu.
     *
     * @return
     */
    public static JMenu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Append a new help set to the existing ones.
     *
     * @param hs
     */
    public static void addHelpSet(HelpSet hs) {
        if (hs == null)
            return;
        sortedHelpSets.put(hs.getTitle().toLowerCase(), hs);
    }

    /**
     * Remove the designated helpset, if it is in use.
     *
     * @param hs
     * @return
     */
    public static boolean removeHelpSet(HelpSet hs) {
        if (hs == null || masterHelp == null)
            return false;
        else
            return masterHelp.remove(hs);
    }

    /**
     * Executed at the end of parsing. Cleans up and makes the main application
     * window visible.
     */
    public static void finish() {
        // Enable online help.
        if (masterHelp == null) {
            String masterHSFileName = System.getProperty(MASTER_HS_PROPERTY_NAME);
            // If there is no designate master help, just use the argument in the
            // method call.
            if (masterHSFileName != null) {
                try {
                    ClassLoader cl = GeawConfigObject.class.getClassLoader();
                    URL url = HelpSet.findHelpSet(cl, masterHSFileName);
                    masterHelp = new HelpSet(cl, url);
                } catch (Exception ee) {
                    log.error("Master Help Set " + masterHSFileName + " not found. Will proceed without it.");
                }
            } else {
                log.error("Master Help Set property not found.");
            }


            for (Map.Entry<String, HelpSet> entry : sortedHelpSets.entrySet()) {
                log.debug("Adding help set: " + entry.getKey() + " | "+entry.getValue().getTitle());
                if (masterHelp == null) {
                    log.warn("Using first help set in map as master.");
                    masterHelp = entry.getValue();
                } else {
                    masterHelp.add(entry.getValue());
                }
            }
        }
        if (masterHelp != null) {
            JMenuItem menu_help = new JMenuItem("Help Topics");
            HelpBroker masterHelpBroker = masterHelp.createHelpBroker();
            //      masterHelpBroker.enableHelpKey(rootpane, "top", masterHelp);
            menu_help.addActionListener(new CSH.DisplayHelpFromSource(masterHelpBroker));
            helpMenu.add(menu_help);
            JMenuItem about = new JMenuItem("About");
            about.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SplashBitmap splash = new SplashBitmap(SplashBitmap.class.getResource("splashscreen.png"));
                    splash.hideOnClick();
                    splash.hideOnTimeout(15000);
                    splash.showSplash();
                }
            });
            helpMenu.add(about);
        }
        
        // ZJ 2008-05-01
        // exit menu is added here (instead of through configuration) so to be the last item regardless of individual components' menu items
        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// emulate a window-closing event to let ProjectPanel do its job
				guiWindow.dispatchEvent(new WindowEvent(guiWindow, WindowEvent.WINDOW_CLOSING));
            }
        });
        guiWindow.getJMenuBar().getMenu(0).add(exitMenu);

        // Display the main application window.
        // FIXME this line was commented out to prevent user see the main frame before loading components
        // this should be done in a more graceful way.
        //guiWindow.setVisible(true);
    }

    /**
     * Initializes the menubar for the application main GUI window.
     */
    private static JMenuBar newMenuBar() {
        JMenuBar appMenuBar = new JMenuBar();
        JMenu menuItem;
        // appMenuBar.setFont(menuItemFont);
        // appMenuBar.setBorder(BorderFactory.createLineBorder(Color.black));
        // Initialize the top-level menus
        for (int i = 0; i < topMenus.length; ++i) {
            menuItem = new JMenu();
            // menuItem.setFont(menuItemFont);
            menuItem.setText(topMenus[i]);
            appMenuBar.add(menuItem);
            if (topMenus[i].compareTo("Help") == 0)
                helpMenu = menuItem;
        }

        return appMenuBar;
    }

    public static HelpSet getMasterHelp() {
		return masterHelp;
	}

    public static void setMasterHelp(HelpSet helpSet) {
    	masterHelp = helpSet;
	}
    
	public static TreeMap<String, HelpSet> getSortedHelpSets() {
		return sortedHelpSets;
	}
}
