package edu.ksu.cis.bnj.gui;

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

import edu.ksu.cis.bnj.gui.components.BNJMainPanel;
import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.bnj.preferences.PreferencesGUI;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Locale;

/**
 * GUI
 *
 * @author Roby Joehanes
 */
public class GUI extends JFrame implements ActionListener, WindowListener {
    protected BNJMainPanel mainPanel = null;
    protected Hashtable settings = null;
    protected JFrame owner = null;

    public GUI() {
        this(null);
    }

    public GUI(JFrame newOwner) {
        super();
        init();
        owner = newOwner;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand().toLowerCase();
        //System.out.println(cmd);  //debug
        if (cmd.equals("options")) { //$NON-NLS-1$
            selected_Options();
        } else if (cmd.equals("inference wizard")) { //$NON-NLS-1$
            selected_InferenceWizard();
        } else if (cmd.equals("learning wizard")) { //$NON-NLS-1$
            selected_StructuredLearningWizard();
        } else if (cmd.equals("converter")) { //$NON-NLS-1$
            selected_Converter();
        } else if (cmd.equals("database gui")) { //$NON-NLS-1$
            selected_DBGUI();
        } else if (cmd.equals("language english")) { //$NON-NLS-1$
            setLanguage(Locale.ENGLISH);
        } else if (cmd.equals("language french")) { //$NON-NLS-1$
            setLanguage(Locale.FRENCH);
        } else if (cmd.equals("language japanese")) { //$NON-NLS-1$
            setLanguage(Locale.JAPANESE);
        } else if (cmd.equals("file exit")) { //$NON-NLS-1$
            selected_Quit();
        } else {
            mainPanel.actionPerformed(e);
        }
    }

    private void buildMenu(JMenu menu) {
        int maxj = menu.getItemCount();
        for (int j = 0; j < maxj; j++) {
            JMenuItem item = menu.getItem(j);
            if (item instanceof JMenu)
                buildMenu((JMenu) item);
            else if (item != null) item.addActionListener(this);
        }
    }

    private void buildMenuBar() {
        JMenuBar menubar = (JMenuBar) settings.get(".menu"); //$NON-NLS-1$
        if (menubar != null) {
            setJMenuBar(menubar);
            int max = menubar.getMenuCount();
            for (int i = 0; i < max; i++) {
                JMenu menu = menubar.getMenu(i);
                buildMenu(menu);
            }
            menubar.revalidate();
            menubar.repaint();
        }
    }

    private void buildToolBar() {
        JToolBar toolbar = (JToolBar) settings.get(".toolbar");  //$NON-NLS-1$
        if (toolbar != null) {
            Component[] components = toolbar.getComponents();
            int max = components.length;
            for (int i = 0; i < max; i++) {
                if (components[i] instanceof JButton) {
                    ((JButton) components[i]).addActionListener(this);
                } else if (components[i] instanceof JToggleButton) {
                    ((JToggleButton) components[i]).addActionListener(this);
                }
            }
            getContentPane().add(toolbar, "North"); //$NON-NLS-1$
        }
    }

    private void setLanguage(Locale locale) {
        Settings.setLanguage(locale, true); // force to reload

        settings = Settings.getWindowSettings("MAIN"); //$NON-NLS-1$

        setTitle((String) settings.get(".title")); // $NON-NLS-1$
        buildMenuBar();
        buildToolBar();
    }

    private void init() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        settings = Settings.getWindowSettings("MAIN"); //$NON-NLS-1$

        setTitle((String) settings.get(".title")); // $NON-NLS-1$
        mainPanel = new BNJMainPanel();
        contentPane.add(mainPanel, "Center"); // $NON-NLS-1$
        buildMenuBar();
        buildToolBar();

        Dimension size = (Dimension) settings.get(".size"); //$NON-NLS-1$
        if (size == null) size = new Dimension(800, 600);
        setSize(size);
        GUIUtil.centerToScreen(this);

        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void selected_Converter() {
        ConverterGUI conv = new ConverterGUI(this);
        conv.setVisible(true);
        conv = null;
    }

    private void selected_DBGUI() {
        DatabaseGUI dbGUI = new DatabaseGUI(this);
        dbGUI.setVisible(true);
        dbGUI = null;
    }

    private void selected_InferenceWizard() {
        InferenceWizard iwizard = new InferenceWizard(this);
        iwizard.setVisible(true);
        iwizard = null;
    }

    private void selected_Options() {
        PreferencesGUI ogui = new PreferencesGUI(this);
        ogui.setVisible(true);
        ogui = null;
    }

    private void selected_Quit() {
        int result = DialogFactory.getYesNoDialog(this, DialogFactory.WARNING, Messages.getString("Dialog.QuitBNJ"), // $NON-NLS-1$
                Messages.getString("Dialog.DoYouWantToQuit")); // $NON-NLS-1$
        if (result == 0) ; //System.exit(0);
    }

    public BNJMainPanel getMainPanel() {
        return mainPanel;
    }

    private void selected_StructuredLearningWizard() {
        LearningWizard lwizard = new LearningWizard(this);
        lwizard.setVisible(true);
        lwizard = null;
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowClosing(WindowEvent evt) {
        Object source = evt.getSource();
        if (source == this) selected_Quit();
    }

    public void windowDeactivated(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowOpened(WindowEvent evt) {
    }

    public static void main(String[] args) {
        Settings.loadEnglishGUISettings();
        GUI gui = new GUI();
        gui.setVisible(true);
    }

}
