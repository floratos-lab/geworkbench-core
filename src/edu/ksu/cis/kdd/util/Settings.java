package edu.ksu.cis.kdd.util;

/*
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import edu.ksu.cis.bnj.bbn.converter.ConverterData;
import edu.ksu.cis.bnj.bbn.converter.bif.BifParser;
import edu.ksu.cis.bnj.bbn.converter.dsl.DSLParser;
import edu.ksu.cis.bnj.bbn.converter.ent.EntParser;
import edu.ksu.cis.bnj.bbn.converter.libb.LibBParser;
import edu.ksu.cis.bnj.bbn.converter.net.NetParser;
import edu.ksu.cis.bnj.bbn.converter.spo.SPOParser;
import edu.ksu.cis.bnj.bbn.converter.xbn.XBNParser;
import edu.ksu.cis.bnj.bbn.converter.xmlbif.XMLBifParser;
import edu.ksu.cis.bnj.bbn.inference.approximate.sampling.AIS;
import edu.ksu.cis.bnj.bbn.inference.elimbel.ElimBel;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.bbn.learning.CIBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.bbn.learning.scorebased.gradient.*;
import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2;
import edu.ksu.cis.bnj.bbn.prm.PRMk2;
import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.kdd.classifier.ClassifierEngine;
import edu.ksu.cis.kdd.classifier.bayes.naive.NaiveBayes;
import edu.ksu.cis.kdd.classifier.validator.KFoldCrossValidator;
import edu.ksu.cis.kdd.data.converter.arff.ArffParser;
import edu.ksu.cis.kdd.data.converter.csf.CSFConverter;
import edu.ksu.cis.kdd.data.converter.dat.DATConverter;
import edu.ksu.cis.kdd.data.converter.excel.ExcelConverter;
import edu.ksu.cis.kdd.data.converter.libb.LibBDataParser;
import edu.ksu.cis.kdd.data.converter.text.TextParser;
import edu.ksu.cis.kdd.data.converter.xml.XMLDataParser;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * <P>Class for saving, loading, and querying the settings.
 * <p/>
 * <P>The setting is stored in an XML file with the following DTD:<br>
 * See config.xml for details.
 * <P>The XML parser is implemented using visitor pattern, which is natural for
 * recursive structures.
 * <p/>
 * <P>Consequently:
 * <UL>
 * <LI><tt>visitDocument</tt> is to visit the XML body.</li>
 * <LI><tt>visitWindow</tt> is to visit the &lt;WINDOW&gt;. It fetches up
 * settings about a particular window.</li>
 * <LI><tt>visitMenu</tt> is to visit the &lt;MENU&gt;. It produces a menu
 * bar.</li>
 * <LI><tt>visitMenuEntry</tt> is to visit the &lt;MENUENTRY&gt;. It produces a
 * first-order menu item, like File, Edit, etc.</li>
 * <LI><tt>visitMenuItem</tt> is to visit the &lt;MENUITEM&gt;. It produces a
 * menu item, like New, Open, etc.</li>
 * <LI><tt>visitSettings</tt> is to visit the &lt;SETTING&gt;. It fetches up
 * any arbitrary settings in a Hashtable.</li>
 * <LI><tt>visitConverters</tt> is to visit the &lt;CONVERTERS&gt;. It produces
 * a table of available Bayesian Network converters.</li>
 * <LI><tt>visitConverter</tt> is to visit the &lt;CONVERTER&gt;. It fetches
 * the setting of a particular Bayesian Network converter.</li>
 * </ul>
 * <p/>
 * <P>And so forth.
 * <p/>
 * <P>It is evident that a lot of this code is just copy-and-paste.
 *
 * @author Roby Joehanes
 * @version 0.00001
 */

public class Settings extends Hashtable {
    // Static data
    public static boolean DEBUG = false;
    public static MersenneTwisterFast random;
    public static long randomSeed;
    public static String versionString = "2.0-alpha-20031105"; // $NON-NLS-1$
    public static String ln = System.getProperty("line.separator"); // $NON-NLS-1$

    // Static data for initializations
    private static Settings instance = null;
    //private static String rootdir = "../../../../../"; //$NON-NLS-1$
    private static String rootdir = "./src/"; //$NON-NLS-1$
    private static String imagedir = rootdir + "images/"; //$NON-NLS-1$
    private static String formatConfig = new Settings().getClass().getResource(rootdir + "config.xml").getFile(); //$NON-NLS-1$
    protected static boolean isGUI = false;

    protected TableSet converterTable = new TableSet();
    protected TableSet dataConverterTable = new TableSet();
    protected Hashtable windowTable = new Hashtable();
    protected Hashtable BNJSettings = new Hashtable();
    protected LinkedList RegisteredBBNs = new LinkedList();
    protected Hashtable buttonGroups = new Hashtable();

    protected List netExtensionList = null;
    protected List dataExtensionList = null;
    protected List netDescriptionList = null;
    protected List dataDescriptionList = null;
    protected Hashtable dataDesc2Format = null;
    protected Hashtable netDesc2Format = null;

    protected Hashtable classOptions = new Hashtable();

    static {
        randomSeed = System.currentTimeMillis();
        random = new MersenneTwisterFast(randomSeed);

        // To force compilation
        assert (BifParser.class != null);
        assert (XMLBifParser.class != null);
        assert (XBNParser.class != null);
        assert (SPOParser.class != null);
        assert (NetParser.class != null);
        assert (LibBParser.class != null);
        assert (DSLParser.class != null);
        assert (EntParser.class != null);
        assert (ElimBel.class != null);
        assert (LS.class != null);
        assert (AIS.class != null);
        assert (ScoreBasedLearner.class != null);
        assert (CIBasedLearner.class != null);

        // My learner classes
        assert (ClassifierEngine.class != null);
        assert (NaiveBayes.class != null);
        assert (KFoldCrossValidator.class != null);
        assert (ArffParser.class != null);
        assert (TextParser.class != null);
        assert (LibBDataParser.class != null);
        assert (CSFConverter.class != null);
        assert (DATConverter.class != null);
        assert (ExcelConverter.class != null);
        assert (XMLDataParser.class != null);
        assert (K2.class != null);
        assert (PRMk2.class != null);
        assert (GreedySL.class != null);
        assert (HillClimbingSL.class != null);
        assert (HillClimbingARSL.class != null);
        assert (HillClimbingDPSL.class != null);
        assert (SimAnnealSL.class != null);
        //assert (SparseCandidate.class != null);
    }

    /**
     * Whether the settings has already loaded (for GUI)
     *
     * @return
     */
    public static boolean isLoaded() {
        return instance != null && instance.windowTable != null;
    }

    /**
     * Load the default configuration file
     */
    public static void load() {
        load(false);
    }

    /**
     * Load the default configuration file
     *
     * @param forGUI specify whether GUI elements should be loaded too or not
     */
    public static void load(boolean forGUI) {
        if (instance != null && (isGUI || (!isGUI && !forGUI))) return;
        try {
            instance = new Settings();
            instance.load(new FileInputStream(formatConfig.replaceAll("%20", " ")), forGUI);  // $NON-NLS-1$ // $NON-NLS-2$
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void load(String filename, boolean forGUI) {
        if (instance != null && (isGUI || (!isGUI && !forGUI))) return;
        try {
            instance = new Settings();
            instance.load(new FileInputStream(filename), forGUI);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the default configuration file
     *
     * @param stream The input stream.
     * @param forGUI specify whether GUI elements should be loaded too or not
     */
    public void load(InputStream stream, boolean forGUI) {
        Document doc;
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        isGUI = forGUI;

        //Parse the document
        try {
            parser = factory.newDocumentBuilder();
            doc = parser.parse(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        visitDocument(doc);
    }

    public static void setLanguage(Locale locale, boolean forGUI) {
        Messages.loadLocales(locale);
        instance = null;
        if (forGUI) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        load(forGUI); // force to reload
    }

    public static void loadEnglishGUISettings() {
        setLanguage(Locale.ENGLISH, true); // Load the default language
    }

    public static void save() {
        // TODO: save list
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(formatConfig.replaceAll("%20", " ")));  // $NON-NLS-1$ // $NON-NLS-2$
            // Save header

            // Save things here

            for (Enumeration e = instance.classOptions.elements(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                out.println("    <CLASSOPTIONS NAME=\"" + key + "\">"); // $NON-NLS-1$ // $NON-NLS-2$
                Hashtable t = (Hashtable) instance.classOptions.get(key);
                for (Enumeration f = t.elements(); f.hasMoreElements();) {
                    String tag = (String) f.nextElement();
                    String value = t.get(tag).toString();
                    out.println("        <SETTING NAME=\"" + tag + "\" VALUE=\"" + value + "\" />"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
                }
                out.println("    </CLASSOPTIONS>"); // $NON-NLS-1$
            }
        } catch (Exception e) {
            if (isDebug()) e.printStackTrace();
        }
    }

    public static Hashtable getClassOptions(String tag) {
        return (Hashtable) instance.classOptions.get(tag);
    }

    public static void setClassOptions(String tag, Hashtable t) {
        assert (t != null);
        instance.classOptions.put(tag, t);
    }

    public static void setRandomSeed(long seed) {
        randomSeed = seed;
        random = new MersenneTwisterFast(seed);
    }

    /**
     * Visit the configuration document body. Contains &lt;WINDOW&gt;,
     * &lt;CONVERTERS&gt;, &lt;SETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitDocument(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        BNJSettings = new Hashtable();
        RegisteredBBNs = new LinkedList();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("BNJCONFIG")) { //$NON-NLS-1$
                        visitDocument(node);
                    } else if (name.equals("WINDOW")) { //$NON-NLS-1$
                        if (isGUI) visitWindow(node);
                    } else if (name.equals("CONVERTERS")) { //$NON-NLS-1$
                        visitConverters(node);
                    } else if (name.equals("DATACONVERTERS")) { //$NON-NLS-1$
                        visitDataConverters(node);
                    } else if (name.equals("SETTING")) { //$NON-NLS-1$
                        visitSettings(node, BNJSettings);
                    } else if (name.equals("ICONSETTING")) { //$NON-NLS-1$
                        if (isGUI) visitIconSettings(node, BNJSettings);
                    } else if (name.equals("COLORSETTING")) { //$NON-NLS-1$
                        if (isGUI) visitColorSettings(node, BNJSettings);
                    } else if (name.equals("FONTSETTING")) { //$NON-NLS-1$
                        if (isGUI) visitFontSettings(node, BNJSettings);
                    } else if (name.equals("REGISTER")) {
                        registerBBN(node, RegisteredBBNs);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
    }

    /**
     * @param node
     * @param RegisteredBBNs
     */
    protected void registerBBN(Node parent, LinkedList list) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <REGISTER>");
        int max = attrs.getLength();
        String n = null, t = null, cp = null, cname = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) {
                n = value;
            } else if (name.equals("TYPE")) {
                t = value;
            } else if (name.equals("PACKAGENAME")) {
                cp = value;
            } else if (name.equals("CLASSNAME")) {
                cname = value;
            } else
                throw new RuntimeException("Superfluous attributes at <REGISTER>");
        }
        list.add(new Registry(t, n, cp, cname));
    }

    /**
     * Visit the &lt;WINDOW&gt;. Contains &lt;MENU&gt;, &lt;TOOLBAR&gt;, and
     * &lt;SETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitWindow(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        Hashtable settings = new Hashtable();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <WINDOW>");
        int max = attrs.getLength();
        String windowName = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                windowName = value;
                settings.put(".name", value); // $NON-NLS-1$
            } else if (name.equals("TITLE")) { //$NON-NLS-1$
                settings.put(".title", Messages.getString("GUI." + value)); // $NON-NLS-1$ $NON-NLS-2$
            } else if (name.equals("SIZE")) { //$NON-NLS-1$
                try {
                    StringTokenizer tok = new StringTokenizer(value, ", ");  // $NON-NLS-1$
                    int x = Integer.parseInt(tok.nextToken());
                    int y = Integer.parseInt(tok.nextToken());
                    settings.put(".size", new Dimension(x, y)); //$NON-NLS-1$
                } catch (Exception e) {
                }
            } else if (name.equals("COLOR")) { //$NON-NLS-1$
                try {
                    int x = Integer.parseInt(value, 16);
                    settings.put(".color", new Color(x)); //$NON-NLS-1$
                } catch (Exception e) {
                }
            } else
                throw new RuntimeException("Superfluous attributes at <WINDOW>");
        }

        NodeList l = parent.getChildNodes();
        if (l == null) return;
        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("MENU")) { //$NON-NLS-1$
                        settings.put(".menu", visitMenu(node)); //$NON-NLS-1$
                    } else if (name.equals("POPUPMENU")) { //$NON-NLS-1$
                        visitPopupMenu(node, settings);
                    } else if (name.equals("TOOLBAR")) { //$NON-NLS-1$
                        settings.put(".toolbar", visitToolBar(node)); //$NON-NLS-1$
                    } else if (name.equals("SETTING")) { //$NON-NLS-1$
                        visitSettings(node, settings);
                    } else if (name.equals("ICONSETTING")) { //$NON-NLS-1$
                        visitIconSettings(node, settings);
                    } else if (name.equals("COLORSETTING")) { //$NON-NLS-1$
                        visitColorSettings(node, settings);
                    } else if (name.equals("FONTSETTING")) { //$NON-NLS-1$
                        visitFontSettings(node, settings);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName()); //$NON-NLS-1$
            }
        }
        windowTable.put(windowName, settings);
    }

    /**
     * Visit the &lt;MENU&gt;. Contains &lt;MENUENTRY&gt;.
     *
     * @param parent The node to traverse
     */
    protected JMenuBar visitMenu(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        JMenuBar menubar = new JMenuBar();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("MENUENTRY")) { //$NON-NLS-1$
                        JMenu item = visitMenuEntry(node);
                        menubar.add(item);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        return menubar;
    }

    /**
     * Visit the &lt;MENUENTRY&gt;. Contains &lt;MENUENTRY&gt;, &lt;
     * MENUITEM&gt, &lt;SEPARATOR&gt;.
     *
     * @param parent The node to traverse
     */
    protected JMenu visitMenuEntry(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <MENUENTRY>");
        int max = attrs.getLength();
        String menuName = null, mnemonic = null, tooltiptext = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                menuName = Messages.getString("GUI." + value); // $NON-NLS-1$
                mnemonic = Messages.getString("GUI." + value + ".MNEMONIC"); //$NON-NLS-1$ $NON-NLS-2$
                if (mnemonic != null && mnemonic.indexOf(value) != -1) mnemonic = null;
                tooltiptext = Messages.getString("GUI." + value + ".TOOLTIP"); //$NON-NLS-1$ $NON-NLS-2$
                if (tooltiptext != null && tooltiptext.indexOf(value) != -1) tooltiptext = null;
            } else
                throw new RuntimeException("Superfluous attributes at <MENUENTRY>");
        }

        JMenu menuEntry = new JMenu(menuName);
        if (mnemonic != null && mnemonic.length() > 0) menuEntry.setMnemonic(mnemonic.charAt(0));
        if (tooltiptext != null) menuEntry.setToolTipText(tooltiptext);

        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("MENUENTRY")) { //$NON-NLS-1$
                        JMenu item = visitMenuEntry(node);
                        menuEntry.add(item);
                    } else if (name.equals("MENUITEM")) { //$NON-NLS-1$
                        JMenuItem item = visitMenuItem(node);
                        menuEntry.add(item);
                    } else if (name.equals("SEPARATOR")) { //$NON-NLS-1$
                        menuEntry.addSeparator();
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        return menuEntry;
    }

    /**
     * Visit the &lt;MENUITEM&gt;.
     *
     * @param parent The node to traverse
     */
    protected JMenuItem visitMenuItem(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <MENUITEM>");
        int max = attrs.getLength();
        String menuName = null, mnemonic = null, shortcut = null, cmdString = null, tooltiptext = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                menuName = Messages.getString("GUI." + value); //$NON-NLS-1$ $NON-NLS-2$
                mnemonic = Messages.getString("GUI." + value + ".MNEMONIC"); //$NON-NLS-1$ $NON-NLS-2$
                if (mnemonic != null && mnemonic.indexOf(value) != -1) mnemonic = null;
                tooltiptext = Messages.getString("GUI." + value + ".TOOLTIP"); //$NON-NLS-1$ $NON-NLS-2$
                if (tooltiptext != null && tooltiptext.indexOf(value) != -1) tooltiptext = null;
            } else if (name.equals("SHORTCUT")) { //$NON-NLS-1$
                shortcut = value;
            } else if (name.equals("CMDSTRING")) { //$NON-NLS-1$
                cmdString = value;
            } else
                throw new RuntimeException("Superfluous attributes at <MENUITEM>");
        }

        JMenuItem menuitem = new JMenuItem(menuName);
        if (mnemonic != null && mnemonic.length() > 0) menuitem.setMnemonic(mnemonic.charAt(0));
        if (tooltiptext != null) menuitem.setToolTipText(tooltiptext);
        if (shortcut != null) {
            KeyStroke key = GUIUtil.translateShortcut(shortcut); //KeyStroke.getKeyStroke(shortcut); // JDK has bugs. so this is not reliable -- RJ
            menuitem.setAccelerator(key);
        }
        menuitem.setActionCommand(cmdString);
        return menuitem;
    }

    /**
     * Visit the &lt;POPUPMENU&gt;. Contains &lt;MENUENTRY&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitPopupMenu(Node parent, Hashtable prop) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <POPUPMENU>");
        int max = attrs.getLength();
        String desc = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                desc = value;
                if (desc.startsWith(".")) //$NON-NLS-1$
                    throw new RuntimeException("Error: Settings name cannot starts with a dot!");
            } else
                throw new RuntimeException("Superfluous attributes at <POPUPMENU>");
        }
        assert (desc != null);

        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        max = l.getLength();
        JPopupMenu popup = new JPopupMenu();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("MENUENTRY")) { //$NON-NLS-1$
                        JMenu item = visitMenuEntry(node);
                        popup.add(item);
                    } else if (name.equals("MENUITEM")) { //$NON-NLS-1$
                        JMenuItem item = visitMenuItem(node);
                        popup.add(item);
                    } else if (name.equals("SEPARATOR")) { //$NON-NLS-1$
                        popup.addSeparator();
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        prop.put(desc, popup);
    }

    /**
     * Visit the &lt;TOOLBAR&gt;. Contains &lt;TOOLBUTTON&gt;.
     *
     * @param parent The node to traverse
     */
    protected JToolBar visitToolBar(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        JToolBar toolbar = new JToolBar();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("TOOLBUTTON")) { //$NON-NLS-1$
                        toolbar.add(visitToolBarButton(node));
                    } else if (name.equals("TOOLTOGGLEBUTTON")) { //$NON-NLS-1$
                        toolbar.add(visitToolBarToggleButton(node));
                    } else if (name.equals("SEPARATOR")) { //$NON-NLS-1$
                        toolbar.addSeparator();
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName());
            }
        }
        return toolbar;
    }

    /**
     * Visit the &lt;MENUITEM&gt;.
     *
     * @param parent The node to traverse
     */
    protected JButton visitToolBarButton(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <TOOLBUTTON>");
        int max = attrs.getLength();
        String cmdString = null, tooltiptext = null;
        ImageIcon icon = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("ICON")) { //$NON-NLS-1$
                icon = loadImage(value);
            } else if (name.equals("CMDSTRING")) { //$NON-NLS-1$
                cmdString = value;
            } else
                throw new RuntimeException("Superfluous attributes at <TOOLBUTTON>");
        }

        JButton button = new JButton(icon);
        button.setActionCommand(cmdString);
        return button;
    }

    protected JToggleButton visitToolBarToggleButton(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <TOOLTOGGLEBUTTON>");
        int max = attrs.getLength();
        String cmdString = null, tooltiptext = null, group = null;
        ImageIcon icon = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("ICON")) { //$NON-NLS-1$
                icon = loadImage(value);
            } else if (name.equals("CMDSTRING")) { //$NON-NLS-1$
                cmdString = value;
            } else if (name.equals("GROUP")) { //$NON-NLS-1$
                group = value;
            } else
                throw new RuntimeException("Superfluous attributes at <TOOLTOGGLEBUTTON>");
        }
        assert (group != null);
        ButtonGroup buttonGroup = (ButtonGroup) buttonGroups.get(group);
        JToggleButton button = new JToggleButton(icon);
        if (buttonGroup == null) {
            buttonGroup = new ButtonGroup();
            buttonGroups.put(group, buttonGroup);
            button.setSelected(true);
        }
        button.setActionCommand(cmdString);
        buttonGroup.add(button);
        return button;
    }

    /**
     * Visit the &lt;SETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitSettings(Node parent, Hashtable prop) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <SETTING>");
        int max = attrs.getLength();
        String desc = null, ext = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                desc = value;
                if (desc.startsWith(".")) //$NON-NLS-1$
                    throw new RuntimeException("Error: Settings name cannot starts with a dot!");
            } else if (name.equals("VALUE")) { //$NON-NLS-1$
                ext = value;
            } else
                throw new RuntimeException("Superfluous attributes at <SETTING>");
        }
        prop.put(desc, ext);
        desc = desc.toLowerCase();

        if (desc.equals("guistyle") && isGUI)
            GUIUtil.switchUI(ext); // $NON-NLS-1$
        else if (desc.equals("debug")) DEBUG = Boolean.getBoolean(ext.toLowerCase()); // $NON-NLS-1$
    }

    /**
     * Visit the &lt;COLORSETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitColorSettings(Node parent, Hashtable prop) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <COLORSETTING>");
        int max = attrs.getLength();
        String desc = null;
        Color color = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                desc = value;
                if (desc.startsWith(".")) //$NON-NLS-1$
                    throw new RuntimeException("Error: Settings name cannot starts with a dot!");
            } else if (name.equals("RGB")) { //$NON-NLS-1$
                color = new Color(Integer.parseInt(value, 16));
            } else
                throw new RuntimeException("Superfluous attributes at <COLORSETTING>");
        }
        prop.put(desc, color);
    }

    /**
     * Visit the &lt;ICONSETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitIconSettings(Node parent, Hashtable prop) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <ICONSETTING>");
        int max = attrs.getLength();
        String desc = null;
        ImageIcon icon = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                desc = value;
                if (desc.startsWith(".")) //$NON-NLS-1$
                    throw new RuntimeException("Error: Settings name cannot starts with a dot!");
            } else if (name.equals("ICON")) { //$NON-NLS-1$
                icon = loadImage(value);
            } else
                throw new RuntimeException("Superfluous attributes at <ICONSETTING>");
        }
        prop.put(desc, icon);
    }

    /**
     * Visit the &lt;FONTSETTING&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitFontSettings(Node parent, Hashtable prop) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <FONTSETTING>");
        int max = attrs.getLength();
        String desc = null, fontName = null;
        int size = 0, style = Font.PLAIN;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("NAME")) { //$NON-NLS-1$
                desc = value;
                if (desc.startsWith(".")) //$NON-NLS-1$
                    throw new RuntimeException("Error: Settings name cannot starts with a dot!");
            } else if (name.equals("FONTNAME")) { //$NON-NLS-1$
                fontName = value;
            } else if (name.equals("SIZE")) { //$NON-NLS-1$
                size = Integer.parseInt(value);
            } else if (name.equals("STYLE")) { //$NON-NLS-1$
                StringTokenizer tokenizer = new StringTokenizer(value, ", "); //$NON-NLS-1$
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken().toLowerCase();
                    if (token.equals("bold")) { //$NON-NLS-1$
                        style |= Font.BOLD;
                    } else if (token.equals("italic")) { //$NON-NLS-1$
                        style |= Font.ITALIC;
                    } else if (token.equals("plain")) { //$NON-NLS-1$
                        style |= Font.PLAIN;
                    }
                }
            } else
                throw new RuntimeException("Superfluous attributes at <FONTSETTING>");
        }
        assert (fontName != null && size > 0);
        Font font = new Font(fontName, style, size);
        prop.put(desc, font);
    }

    /**
     * Visit the &lt;CONVERTERS&gt;. Contains &lt;CONVERTER&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitConverters(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("CONVERTER")) { //$NON-NLS-1$
                        visitConverter(node);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName()); //$NON-NLS-1$
            }
        }
    }

    /**
     * Visit the &lt;DATACONVERTERS&gt;. Contains &lt;DATACONVERTER&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitDataConverters(Node parent) {
        NodeList l = parent.getChildNodes();
        if (l == null) throw new RuntimeException("Unexpected end of document!");
        int max = l.getLength();
        for (int i = 0; i < max; i++) {
            Node node = l.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    String name = node.getNodeName().toUpperCase();
                    if (name.equals("DATACONVERTER")) { //$NON-NLS-1$
                        visitDataConverter(node);
                    } else if (Settings.isDebug())
                        System.out.println("Unhandled element " + name);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                case Node.DOCUMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.TEXT_NODE:
                    //Ignore this
                    break;
                default:
                    if (Settings.isDebug())
                        System.out.println("Unhandled node " + node.getNodeName()); //$NON-NLS-1$
            }
        }
    }

    /**
     * Visit the &lt;CONVERTER&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitConverter(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <CONVERTER>");
        int max = attrs.getLength();
        String desc = null, ext = null, pkgname = null, clsname = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("DESCRIPTION")) { //$NON-NLS-1$
                desc = value;
            } else if (name.equals("EXTENSION")) { //$NON-NLS-1$
                ext = value;
            } else if (name.equals("PACKAGENAME")) { //$NON-NLS-1$
                pkgname = value;
            } else if (name.equals("CLASSNAME")) { //$NON-NLS-1$
                clsname = value;
            } else
                throw new RuntimeException("Superfluous attributes at <CONVERTER>");
        }
        converterTable.put(ext, new ConverterData(desc, ext, pkgname, clsname));
    }

    /**
     * Visit the &lt;DATACONVERTER&gt;.
     *
     * @param parent The node to traverse
     */
    protected void visitDataConverter(Node parent) {
        NamedNodeMap attrs = parent.getAttributes();
        if (attrs == null) throw new RuntimeException("Expecting attributes at <DATACONVERTER>");
        int max = attrs.getLength();
        String desc = null, ext = null, pkgname = null, clsname = null;
        for (int i = 0; i < max; i++) {
            Node attr = attrs.item(i);
            String name = attr.getNodeName();
            String value = attr.getNodeValue();
            if (name.equals("DESCRIPTION")) { //$NON-NLS-1$
                desc = value;
            } else if (name.equals("EXTENSION")) { //$NON-NLS-1$
                ext = value;
            } else if (name.equals("PACKAGENAME")) { //$NON-NLS-1$
                pkgname = value;
            } else if (name.equals("CLASSNAME")) { //$NON-NLS-1$
                clsname = value;
            } else
                throw new RuntimeException("Superfluous attributes at <DATACONVERTER>");
        }
        dataConverterTable.put(ext, new ConverterData(desc, ext, pkgname, clsname));
    }

    /**
     * Get the Converter Table
     *
     * @return TableSet
     */
    public static TableSet getConverterTable() {
        if (instance == null || instance.converterTable == null) load(); // lazy initialization
        return instance.converterTable;
    }

    /**
     * Get the Data Converter Table
     *
     * @return TableSet
     */
    public static TableSet getDataConverterTable() {
        if (instance == null || instance.dataConverterTable == null) load(); // lazy initialization
        return instance.dataConverterTable;
    }

    /**
     * Get window setting
     *
     * @param name Window ID
     * @return Hashtable The Settings
     */
    public static Hashtable getWindowSettings(String name) {
        if (instance == null || instance.windowTable == null) load(true); // lazy initialization
        return (Hashtable) instance.windowTable.get(name);
    }

    /**
     * Get BNJ general setting. See &lt;SETTING&gt; tags in config.xml
     *
     * @return Hashtable The settings
     */
    public static Hashtable getBNJSettings() {
        if (instance == null || instance.BNJSettings == null) load(); // lazy initialization
        return instance.BNJSettings;
    }

    public static List getNetExtensionList() {
        if (instance == null || instance.netExtensionList == null) instance.buildNetExtensionList();
        return instance.netExtensionList;
    }

    public static List getDataExtensionList() {
        if (instance == null || instance.dataExtensionList == null) instance.buildDataExtensionList();
        return instance.dataExtensionList;
    }

    public static List getNetDescriptionList() {
        if (instance == null || instance.netDescriptionList == null) instance.buildNetExtensionList();
        return instance.netDescriptionList;
    }

    public static List getDataDescriptionList() {
        if (instance == null || instance.dataDescriptionList == null) instance.buildDataExtensionList();
        return instance.dataDescriptionList;
    }

    public static String getNetExtensionFromDescription(String desc) {
        if (instance == null || instance.netDesc2Format == null) instance.buildNetExtensionList();
        return (String) instance.netDesc2Format.get(desc);
    }

    public static String getDataExtensionFromDescription(String desc) {
        if (instance == null || instance.dataDesc2Format == null) instance.buildDataExtensionList();
        return (String) instance.dataDesc2Format.get(desc);
    }

    /**
     * gets options from config.xml which set the layout of the Options GUI
     * for each BBN class.
     *
     * @param desc
     * @return
     */
    public static LinkedList getRegisteredBBNs() {
        if (instance == null || instance.RegisteredBBNs == null) load();
        return instance.RegisteredBBNs;
    }

    private void buildNetExtensionList() {
        netExtensionList = new LinkedList();
        netDescriptionList = new LinkedList();
        netDesc2Format = new Hashtable();
        for (Enumeration e = getConverterTable().keys(); e.hasMoreElements();) {
            String ext = (String) e.nextElement();
            HashSet set = (HashSet) converterTable.get(ext);
            netExtensionList.add("*." + ext); // $NON-NLS-1$
            for (Iterator i = set.iterator(); i.hasNext();) {
                ConverterData data = (ConverterData) i.next();
                netDesc2Format.put(data.getDescription(), ext);
                netDescriptionList.add(data.getDescription());
            }
        }
    }

    private void buildDataExtensionList() {
        dataExtensionList = new LinkedList();
        dataDescriptionList = new LinkedList();
        dataDesc2Format = new Hashtable();
        for (Enumeration e = getDataConverterTable().keys(); e.hasMoreElements();) {
            String ext = (String) e.nextElement();
            HashSet set = (HashSet) dataConverterTable.get(ext);
            dataExtensionList.add("*." + ext); // $NON-NLS-1$
            for (Iterator i = set.iterator(); i.hasNext();) {
                ConverterData data = (ConverterData) i.next();
                dataDesc2Format.put(data.getDescription(), ext);
                dataDescriptionList.add(data.getDescription());
            }
        }
    }

    protected ImageIcon loadImage(String url) {
        return new ImageIcon(getClass().getResource(imagedir + url));
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static final String license = // GNU GPL License MUST NOT be translated

            "The GNU General Public License (GPL)" + ln + // $NON-NLS-1$
            "Version 2, June 1991" + ln + // $NON-NLS-1$
            "Copyright (C) 1989, 1991 Free Software Foundation, Inc." + ln + // $NON-NLS-1$
            "59 Temple Place, Suite 330, Boston, MA 02111-1307 USA" + ln + // $NON-NLS-1$
            ln + "Everyone is permitted to copy and distribute verbatim copies" + ln + // $NON-NLS-1$
            "of this license document, but changing it is not allowed." + ln + // $NON-NLS-1$
            ln + "Preamble" + ln + // $NON-NLS-1$
            ln + "The licenses for most software are designed to take away your freedom" + ln + // $NON-NLS-1$
            "to share and change it. By contrast, the GNU General Public License" + ln + // $NON-NLS-1$
            "is intended to guarantee your freedom to share and change free" + ln + // $NON-NLS-1$
            "software--to make sure the software is free for all its users. This" + ln + // $NON-NLS-1$
            "General Public License applies to most of the Free Software" + ln + // $NON-NLS-1$
            "Foundation's software and to any other program whose authors commit" + ln + // $NON-NLS-1$
            "to using it. (Some other Free Software Foundation software is covered" + ln + // $NON-NLS-1$
            "by the GNU Library General Public License instead.) You can apply it" + ln + // $NON-NLS-1$
            "to your programs, too." + ln + // $NON-NLS-1$
            ln + "When we speak of free software, we are referring to freedom, not" + ln + // $NON-NLS-1$
            "price. Our General Public Licenses are designed to make sure that you" + ln + // $NON-NLS-1$
            "have the freedom to distribute copies of free software (and charge" + ln + // $NON-NLS-1$
            "for this service if you wish), that you receive source code or can" + ln + // $NON-NLS-1$
            "get it if you want it, that you can change the software or use pieces" + ln + // $NON-NLS-1$
            "of it in new free programs; and that you know you can do these" + ln + // $NON-NLS-1$
            "things." + ln + // $NON-NLS-1$
            ln + "To protect your rights, we need to make restrictions that forbid" + ln + // $NON-NLS-1$
            "anyone to deny you these rights or to ask you to surrender the" + ln + // $NON-NLS-1$
            "rights. These restrictions translate to certain responsibilities for" + ln + // $NON-NLS-1$
            "you if you distribute copies of the software, or if you modify it." + ln + // $NON-NLS-1$
            ln + "For example, if you distribute copies of such a program, whether" + ln + // $NON-NLS-1$
            "gratis or for a fee, you must give the recipients all the rights that" + ln + // $NON-NLS-1$
            "you have. You must make sure that they, too, receive or can get the" + ln + // $NON-NLS-1$
            "source code. And you must show them these terms so they know their" + ln + // $NON-NLS-1$
            "rights." + ln + // $NON-NLS-1$
            ln + "We protect your rights with two steps: (1) copyright the software," + ln + // $NON-NLS-1$
            "and (2) offer you this license which gives you legal permission to" + ln + // $NON-NLS-1$
            "copy, distribute and/or modify the software." + ln + // $NON-NLS-1$
            ln + "Also, for each author's protection and ours, we want to make certain" + ln + // $NON-NLS-1$
            "that everyone understands that there is no warranty for this free" + ln + // $NON-NLS-1$
            "software. If the software is modified by someone else and passed on," + ln + // $NON-NLS-1$
            "we want its recipients to know that what they have is not the" + ln + // $NON-NLS-1$
            "original, so that any problems introduced by others will not reflect" + ln + // $NON-NLS-1$
            "on the original authors' reputations." + ln + // $NON-NLS-1$
            ln + "Finally, any free program is threatened constantly by software" + ln + // $NON-NLS-1$
            "patents. We wish to avoid the danger that redistributors of a free" + ln + // $NON-NLS-1$
            "program will individually obtain patent licenses, in effect making" + ln + // $NON-NLS-1$
            "the program proprietary. To prevent this, we have made it clear that" + ln + // $NON-NLS-1$
            "any patent must be licensed for everyone's free use or not licensed" + ln + // $NON-NLS-1$
            "at all." + ln + // $NON-NLS-1$
            ln + "The precise terms and conditions for copying, distribution and" + ln + // $NON-NLS-1$
            "modification follow." + ln + // $NON-NLS-1$
            ln + "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION" + ln + // $NON-NLS-1$
            ln + "0. This License applies to any program or other work which contains a" + ln + // $NON-NLS-1$
            "notice placed by the copyright holder saying it may be distributed" + ln + // $NON-NLS-1$
            "under the terms of this General Public License. The \"Program\", below," + ln + // $NON-NLS-1$
            "refers to any such program or work, and a \"work based on the Program\"" + ln + // $NON-NLS-1$
            "means either the Program or any derivative work under copyright law:" + ln + // $NON-NLS-1$
            "that is to say, a work containing the Program or a portion of it," + ln + // $NON-NLS-1$
            "either verbatim or with modifications and/or translated into another" + ln + // $NON-NLS-1$
            "language. (Hereinafter, translation is included without limitation in" + ln + // $NON-NLS-1$
            "the term \"modification\".) Each licensee is addressed as \"you\"." + ln + // $NON-NLS-1$
            ln + "Activities other than copying, distribution and modification are not" + ln + // $NON-NLS-1$
            "covered by this License; they are outside its scope. The act of" + ln + // $NON-NLS-1$
            "running the Program is not restricted, and the output from the" + ln + // $NON-NLS-1$
            "Program is covered only if its contents constitute a work based on" + ln + // $NON-NLS-1$
            "the Program (independent of having been made by running the Program)." + ln + // $NON-NLS-1$
            "Whether that is true depends on what theProgram does." + ln + // $NON-NLS-1$
            ln + "1. You may copy and distribute verbatim copies of the Program's" + ln + // $NON-NLS-1$
            "source code as you receive it, in any medium, provided that you" + ln + // $NON-NLS-1$
            "conspicuously and appropriately publish on each copy an appropriate" + ln + // $NON-NLS-1$
            "copyright notice and disclaimer of warranty; keep intact all the" + ln + // $NON-NLS-1$
            "notices that refer to this License and to the absence of any" + ln + // $NON-NLS-1$
            "warranty; and give any other recipients of the Program a copy of this" + ln + // $NON-NLS-1$
            "License along with the Program." + ln + // $NON-NLS-1$
            ln + "You may charge a fee for the physical act of transferring a copy, and" + ln + // $NON-NLS-1$
            "you may at your option offer warranty protection in exchange for a" + ln + // $NON-NLS-1$
            "fee." + ln + // $NON-NLS-1$
            ln + "2. You may modify your copy or copies of the Program or any portion" + ln + // $NON-NLS-1$
            "of it, thus forming a work based on the Program, and copy and" + ln + // $NON-NLS-1$
            "distribute such modifications or work under the terms of Section 1" + ln + // $NON-NLS-1$
            "above, provided that you also meet all of these conditions:" + ln + // $NON-NLS-1$
            ln + "a) You must cause the modified files to carry prominent notices" + ln + // $NON-NLS-1$
            "stating that you changed the files and the date of any change." + ln + // $NON-NLS-1$
            ln + "b) You must cause any work that you distribute or publish, that in" + ln + // $NON-NLS-1$
            "whole or in part contains or is derived from the Program or any part" + ln + // $NON-NLS-1$
            "thereof, to be licensed as a whole at no charge to all third parties" + ln + // $NON-NLS-1$
            "under the terms of this License." + ln + // $NON-NLS-1$
            ln + "c) If the modified program normally reads commands interactively when" + ln + // $NON-NLS-1$
            "run, you must cause it, when started running for such interactive use" + ln + // $NON-NLS-1$
            "in the most ordinary way, to print or display an announcement" + ln + // $NON-NLS-1$
            "including an appropriate copyright notice and a notice that there is" + ln + // $NON-NLS-1$
            "no warranty (or else, saying that you provide a warranty) and that" + ln + // $NON-NLS-1$
            "users may redistribute the program under these conditions, and" + ln + // $NON-NLS-1$
            "telling the user how to view a copy of this License. (Exception: if" + ln + // $NON-NLS-1$
            "the Program itself is interactive but does not normally print such an" + ln + // $NON-NLS-1$
            "announcement, your work based on the Program is not required to print" + ln + // $NON-NLS-1$
            "an announcement.)" + ln + // $NON-NLS-1$
            ln + "These requirements apply to the modified work as a whole. If" + ln + // $NON-NLS-1$
            "identifiable sections of that work are not derived from the Program," + ln + // $NON-NLS-1$
            "and can be reasonably considered independent and separate works in" + ln + // $NON-NLS-1$
            "themselves, then this License, and its terms, do not apply to those" + ln + // $NON-NLS-1$
            "sections when you distribute them as separate works. But when you" + ln + // $NON-NLS-1$
            "distribute the same sections as part of a whole which is a work based" + ln + // $NON-NLS-1$
            "on the Program, the distribution of the whole must be on the terms of" + ln + // $NON-NLS-1$
            "this License, whose permissions for other licensees extend to the" + ln + // $NON-NLS-1$
            "entire whole, and thus to each and every part regardless of who wrote" + ln + // $NON-NLS-1$
            "it." + ln + // $NON-NLS-1$
            ln + "Thus, it is not the intent of this section to claim rights or contest" + ln + // $NON-NLS-1$
            "your rights to work written entirely by you; rather, the intent is to" + ln + // $NON-NLS-1$
            "exercise the right to control the distribution of derivative or" + ln + // $NON-NLS-1$
            "collective works based on the Program." + ln + // $NON-NLS-1$
            ln + "In addition, mere aggregation of another work not based on the" + ln + // $NON-NLS-1$
            "Program with the Program (or with a work based on the Program) on a" + ln + // $NON-NLS-1$
            "volume of a storage or distribution medium does not bring the other" + ln + // $NON-NLS-1$
            "work under the scope of this License." + ln + // $NON-NLS-1$
            ln + "3. You may copy and distribute the Program (or a work based on it," + ln + // $NON-NLS-1$
            "under Section 2) in object code or executable form under the terms of" + ln + // $NON-NLS-1$
            "Sections 1 and 2 above provided that you also do one of the" + ln + // $NON-NLS-1$
            "following:" + ln + // $NON-NLS-1$
            ln + "a) Accompany it with the complete corresponding machine-readable" + ln + // $NON-NLS-1$
            "source code, which must be distributed under the terms of Sections 1" + ln + // $NON-NLS-1$
            "and 2 above on a medium customarily used for software interchange;" + ln + // $NON-NLS-1$
            "or," + ln + // $NON-NLS-1$
            ln + "b) Accompany it with a written offer, valid for at least three years," + ln + // $NON-NLS-1$
            "to give any third party, for a charge no more than your cost of" + ln + // $NON-NLS-1$
            "physically performing source distribution, a complete" + ln + // $NON-NLS-1$
            "machine-readable copy of the corresponding source code, to be" + ln + // $NON-NLS-1$
            "distributed under the terms of Sections 1 and 2 above on a medium" + ln + // $NON-NLS-1$
            "customarily used for software interchange; or," + ln + // $NON-NLS-1$
            ln + "c) Accompany it with the information you received as to the offer to" + ln + // $NON-NLS-1$
            "distribute corresponding source code. (This alternative is allowed" + ln + // $NON-NLS-1$
            "only for noncommercial distribution and only if you received the" + ln + // $NON-NLS-1$
            "program in object code or executable form with such an offer, in" + ln + // $NON-NLS-1$
            "accord with Subsection b above.)" + ln + // $NON-NLS-1$
            ln + "The source code for a work means the preferred form of the work for" + ln + // $NON-NLS-1$
            "making modifications to it. For an executable work, complete source" + ln + // $NON-NLS-1$
            "code means all the source code for all modules it contains, plus any" + ln + // $NON-NLS-1$
            "associated interface definition files, plus the scripts used to" + ln + // $NON-NLS-1$
            "control compilation and installation of the executable. However, as a" + ln + // $NON-NLS-1$
            "special exception, the source code distributed need not include" + ln + // $NON-NLS-1$
            "anything that is normally distributed (in either source or binary" + ln + // $NON-NLS-1$
            "form) with the major components (compiler, kernel, and so on) of the" + ln + // $NON-NLS-1$
            "operating system on which the executable runs, unless that component" + ln + // $NON-NLS-1$
            "itself accompanies the executable." + ln + // $NON-NLS-1$
            ln + "If distribution of executable or object code is made by offering" + ln + // $NON-NLS-1$
            "access to copy from a designated place, then offering equivalent" + ln + // $NON-NLS-1$
            "access to copy the source code from the same place counts as" + ln + // $NON-NLS-1$
            "distribution of the source code, even though third parties are not" + ln + // $NON-NLS-1$
            "compelled to copy the source along with the object code." + ln + // $NON-NLS-1$
            ln + "4. You may not copy, modify, sublicense, or distribute the Program" + ln + // $NON-NLS-1$
            "except as expressly provided under this License. Any attempt" + ln + // $NON-NLS-1$
            "otherwise to copy, modify, sublicense or distribute the Program is" + ln + // $NON-NLS-1$
            "void, and will automatically terminate your rights under this" + ln + // $NON-NLS-1$
            "License. However, parties who have received copies, or rights, from" + ln + // $NON-NLS-1$
            "you under this License will not have their licenses terminated so" + ln + // $NON-NLS-1$
            "long as such parties remain in full compliance." + ln + // $NON-NLS-1$
            ln + "5. You are not required to accept this License, since you have not" + ln + // $NON-NLS-1$
            "signed it. However, nothing else grants you permission to modify or" + ln + // $NON-NLS-1$
            "distribute the Program or its derivative works. These actions are" + ln + // $NON-NLS-1$
            "prohibited by law if you do not accept this License. Therefore, by" + ln + // $NON-NLS-1$
            "modifying or distributing the Program (or any work based on the" + ln + // $NON-NLS-1$
            "Program), you indicate your acceptance of this License to do so, and" + ln + // $NON-NLS-1$
            "all its terms and conditions for copying, distributing or modifying" + ln + // $NON-NLS-1$
            "the Program or works based on it." + ln + // $NON-NLS-1$
            ln + "6. Each time you redistribute the Program (or any work based on the" + ln + // $NON-NLS-1$
            "Program), the recipient automatically receives a license from the" + ln + // $NON-NLS-1$
            "original licensor to copy, distribute or modify the Program subject" + ln + // $NON-NLS-1$
            "to these terms and conditions. You may not impose any further" + ln + // $NON-NLS-1$
            "restrictions on the recipients' exercise of the rights granted" + ln + // $NON-NLS-1$
            "herein. You are not responsible for enforcing compliance by third" + ln + // $NON-NLS-1$
            "parties to this License." + ln + // $NON-NLS-1$
            ln + "7. If, as a consequence of a court judgment or allegation of patent" + ln + // $NON-NLS-1$
            "infringement or for any other reason (not limited to patent issues)," + ln + // $NON-NLS-1$
            "conditions are imposed on you (whether by court order, agreement or" + ln + // $NON-NLS-1$
            "otherwise) that contradict the conditions of this License, they do" + ln + // $NON-NLS-1$
            "not excuse you from the conditions of this License. If you cannot" + ln + // $NON-NLS-1$
            "distribute so as to satisfy simultaneously your obligations under" + ln + // $NON-NLS-1$
            "this License and any other pertinent obligations, then as a" + ln + // $NON-NLS-1$
            "consequence you may not distribute the Program at all. For example," + ln + // $NON-NLS-1$
            "if a patent license would not permit royalty-free redistribution of" + ln + // $NON-NLS-1$
            "the Program by all those who receive copies directly or indirectly" + ln + // $NON-NLS-1$
            "through you, then the only way you could satisfy both it and this" + ln + // $NON-NLS-1$
            "License would be to refrain entirely from distribution of the" + ln + // $NON-NLS-1$
            "Program." + ln + // $NON-NLS-1$
            ln + "If any portion of this section is held invalid or unenforceable under" + ln + // $NON-NLS-1$
            "any particular circumstance, the balance of the section is intended" + ln + // $NON-NLS-1$
            "to apply and the section as a whole is intended to apply in other" + ln + // $NON-NLS-1$
            "circumstances." + ln + // $NON-NLS-1$
            ln + "It is not the purpose of this section to induce you to infringe any" + ln + // $NON-NLS-1$
            "patents or other property right claims or to contest validity of any" + ln + // $NON-NLS-1$
            "such claims; this section has the sole purpose of protecting the" + ln + // $NON-NLS-1$
            "integrity of the free software distribution system, which is" + ln + // $NON-NLS-1$
            "implemented by public license practices. Many people have made" + ln + // $NON-NLS-1$
            "generous contributions to the wide range of software distributed" + ln + // $NON-NLS-1$
            "through that system in reliance on consistent application of that" + ln + // $NON-NLS-1$
            "system; it is up to the author/donor to decide if he or she is" + ln + // $NON-NLS-1$
            "willing to distribute software through any other system and a" + ln + // $NON-NLS-1$
            "licensee cannot impose that choice." + ln + // $NON-NLS-1$
            ln + "This section is intended to make thoroughly clear what is believed to" + ln + // $NON-NLS-1$
            "be a consequence of the rest of this License." + ln + // $NON-NLS-1$
            ln + "8. If the distribution and/or use of the Program is restricted in" + ln + // $NON-NLS-1$
            "certain countries either by patents or by copyrighted interfaces, the" + ln + // $NON-NLS-1$
            "original copyright holder who places the Program under this License" + ln + // $NON-NLS-1$
            "may add an explicit geographical distribution limitation excluding" + ln + // $NON-NLS-1$
            "those countries, so that distribution is permitted only in or among" + ln + // $NON-NLS-1$
            "countries not thus excluded. In such case, this License incorporates" + ln + // $NON-NLS-1$
            "the limitation as if written in the body of this License." + ln + // $NON-NLS-1$
            ln + "9. The Free Software Foundation may publish revised and/or new" + ln + // $NON-NLS-1$
            "versions of the General Public License from time to time. Such new" + ln + // $NON-NLS-1$
            "versions will be similar in spirit to the present version, but may" + ln + // $NON-NLS-1$
            "differ in detail to address new problems or concerns." + ln + // $NON-NLS-1$
            ln + "Each version is given a distinguishing version number. If the Program" + ln + // $NON-NLS-1$
            "specifies a version number of this License which applies to it and" + ln + // $NON-NLS-1$
            "\"any later version\", you have the option of following the terms and" + ln + // $NON-NLS-1$
            "conditions either of that version or of any later version published" + ln + // $NON-NLS-1$
            "by the Free Software Foundation. If the Program does not specify a" + ln + // $NON-NLS-1$
            "version number of this License, you may choose any version ever" + ln + // $NON-NLS-1$
            "published by the Free Software Foundation." + ln + // $NON-NLS-1$
            ln + "10. If you wish to incorporate parts of the Program into other free" + ln + // $NON-NLS-1$
            "programs whose distribution conditions are different, write to the" + ln + // $NON-NLS-1$
            "author to ask for permission. For software which is copyrighted by" + ln + // $NON-NLS-1$
            "the Free Software Foundation, write to the Free Software Foundation;" + ln + // $NON-NLS-1$
            "we sometimes make exceptions for this. Our decision will be guided by" + ln + // $NON-NLS-1$
            "the two goals of preserving the free status of all derivatives of our" + ln + // $NON-NLS-1$
            "free software and of promoting the sharing and reuse of software" + ln + // $NON-NLS-1$
            "generally." + ln + // $NON-NLS-1$
            ln + "NO WARRANTY" + ln + // $NON-NLS-1$
            ln + "11. BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO" + ln + // $NON-NLS-1$
            "WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW." + ln + // $NON-NLS-1$
            "EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR" + ln + // $NON-NLS-1$
            "OTHER PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY" + ln + // $NON-NLS-1$
            "KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE" + ln + // $NON-NLS-1$
            "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR" + ln + // $NON-NLS-1$
            "PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE" + ln + // $NON-NLS-1$
            "PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME" + ln + // $NON-NLS-1$
            "THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION." + ln + // $NON-NLS-1$
            ln + "12. IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN" + ln + // $NON-NLS-1$
            "WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY" + ln + // $NON-NLS-1$
            "AND/OR REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU" + ln + // $NON-NLS-1$
            "FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR" + ln + // $NON-NLS-1$
            "CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE" + ln + // $NON-NLS-1$
            "PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING" + ln + // $NON-NLS-1$
            "RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A" + ln + // $NON-NLS-1$
            "FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF" + ln + // $NON-NLS-1$
            "SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF" + ln + // $NON-NLS-1$
            "SUCH DAMAGES." + ln + // $NON-NLS-1$
            ln + "END OF TERMS AND CONDITIONS" + ln; // $NON-NLS-1$

}
