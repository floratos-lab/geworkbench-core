package edu.ksu.cis.bnj.preferences;

import edu.ksu.cis.bnj.bbn.inference.Inference;
import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.bnj.gui.GUI;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Registry;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.OptionGUI;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

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


/**
 * PreferencesGUI is a GUI to make changes to Defaults, Settings and Messages in BNJ.
 *
 * @author James Plummer
 */
public class PreferencesGUI extends JFrame implements ActionListener, WindowListener, MouseListener {
    private GUI owner = null;

    /**
     * Minimun Height and Width requirements. *
     */
    public static final double MINHEIGHT = 400, MINWIDTH = 400;
    /**
     * The main JPanel of this container *
     */
    private JPanel mainPanel = null;
    /**
     * The JPanel which holds the tabbedPane. *
     */
    private JPanel tabPanel = null;
    private JTabbedPane tabpane = new JTabbedPane();
    /**
     * Bottom prefered Heights and widths *
     */
    public static final double PREFBOTTOMHEIGHT = 50, PREFBOTTOMWIDTH = -1;
    /**
     * The JPanel which holds the action buttons on the bottom of the screen. *
     */
    private JPanel bottomPanel = null;
    /**
     * The Buttons at the bottom of the screen. *
     */
    private JButton okButton, cancelButton, applyButton;

    protected Hashtable panel2Object = new Hashtable();
    protected LinkedList optionableList = new LinkedList();

    /**
     * Constructor which can run standalone if owner == null or gain control of program
     * from owner and pass it back when functions are complete.
     *
     * @param owner - the GUI which created the PreferencesGUI.
     */
    public PreferencesGUI(GUI owner) {
        this.owner = owner;
        if (owner != null) {
            //Must gain control of program so changes can be properly implemented.
            this.owner.setEnabled(false);
        }
        init();
    }

    public void init() {
        GUIUtil.switchToNativeUI(this);
        Hashtable settings = Settings.getWindowSettings("PREF");
        this.setTitle((String) settings.get(".title"));
        Dimension size = (Dimension) settings.get(".size");
        if (size == null) {
            size = new Dimension();
            size.height = (int) MINHEIGHT;
            size.width = (int) MINWIDTH;
        }
        if (owner == null) {
            setSize(size);
            GUIUtil.centerToScreen(this);
        } else {
            Dimension ownersize = owner.getSize();
            setSize(size);
            GUIUtil.centerToComponent(this, this.owner);
        }

        //Create the Actual GUI.
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
        }
        //Create the tabbedPanel
        if (tabPanel == null) {
            tabPanel = new JPanel();
            tabPanel.setLayout(new GridBagLayout());
            GUIUtil.gbAdd(mainPanel, tabPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

            //			mainPanel.add(tabPanel, ); // this is for null layout.
            GUIUtil.gbAdd(tabPanel, tabpane, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

            LinkedList register = Settings.getRegisteredBBNs();
            ListIterator iterator = register.listIterator();
            Hashtable tabCache = new Hashtable();
            for (; iterator.hasNext();) {
                JPanel toadd = null;
                Registry element = (Registry) iterator.next();
                Table table = new Table();
                Optionable opt = null;
                OptionGUI optgui = null;
                if (element.type.equals(Registry.LEARNER)) {
                    opt = Learner.load(element.classpackage + "." + element.classname, null);
                    optgui = opt.getOptionsDialog();
                } else if (element.type.equals(Registry.INFERENCE)) {
                    opt = Inference.load(element.classpackage + "." + element.classname, null);
                    optgui = opt.getOptionsDialog();
                }

                if (optgui != null) {
                    optionableList.add(opt);
                    toadd = optgui.getMainPane();
                    JPanel placement = (JPanel) tabCache.get(element.type);
                    if (placement == null) {
                        placement = new JPanel();
                        placement.setLayout(new GridBagLayout());
                        tabpane.addTab(element.type, placement);
                        JTabbedPane tab = new JTabbedPane();
                        GUIUtil.gbAdd(placement, tab, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                        tabCache.put(element.type, placement);
                    }
                    JTabbedPane tpane = (JTabbedPane) placement.getComponent(0);
                    tpane.addTab(element.name, toadd);
                }
            }


        }
        //Create the Bottom.
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            //			GridBagConstraints bgbc = new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.SOUTH,0, new Insets(6,6,6,6), 0,0);
            GUIUtil.gbAdd(mainPanel, bottomPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);
            //			mainPanel.add(bottomPanel);
            bottomPanel.setLayout(new GridBagLayout());
            //			bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));

            okButton = new JButton("OK");
            okButton.addActionListener(this);
            GridBagConstraints g1 = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, 0, new Insets(6, 100, 6, 6), 0, 0);
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            GridBagConstraints g2 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, 0, new Insets(6, 6, 6, 6), 0, 0);
            applyButton = new JButton("Apply");
            applyButton.addActionListener(this);
            GridBagConstraints g3 = new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, 0, new Insets(6, 6, 6, 6), 0, 0);
            bottomPanel.add(okButton, g1);
            bottomPanel.add(cancelButton, g2);
            bottomPanel.add(applyButton, g3);
        }

        setContentPane(mainPanel);
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.checkSize();
    }

    public void selected_ApplyandClose() {
        selected_Apply();
        selected_Close();
    }

    public void selected_Apply() {
        // TODO must get finished by UAI
    }

    public void selected_Close() {
        if (owner == null) ;// System.exit(0);
        this.setVisible(false);
        owner.setEnabled(true);
        owner = null;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = (String) e.getActionCommand();
        System.out.println(cmd);
        if (cmd.toLowerCase().equals("ok")) {
            selected_ApplyandClose();
        } else if (cmd.toLowerCase().equals("cancel")) {
            selected_Close();
        } else if (cmd.toLowerCase().equals("apply")) {
            selected_Apply();
        }
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        selected_Close();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public static void main(String args[]) {
        new PreferencesGUI(null).show();
    }

    public void checkSize() {
        Dimension size = this.getSize();
        Dimension newsize = new Dimension();
        newsize.height = (int) (size.height > MINHEIGHT ? size.height : MINHEIGHT);
        newsize.width = (int) (size.width > MINWIDTH ? size.width : MINWIDTH);
        this.setSize(newsize);
        this.checkTabPanelSize();
        this.checkBottomSize();
    }

    private void checkBottomSize() {
        Dimension bottomsize = bottomPanel.getSize();
        if (bottomsize.height < PREFBOTTOMHEIGHT) {
            bottomsize.height = (int) PREFBOTTOMHEIGHT;
        }
        bottomsize.width = this.getWidth();
        bottomPanel.setSize(bottomsize);
        int ylocale = this.getHeight() - (int) (bottomPanel.getHeight() * (1.5));
        bottomPanel.setLocation(0, ylocale);
    }

    private void checkTabPanelSize() {
        Dimension size = tabPanel.getSize();
        Dimension newsize = new Dimension();
        newsize.height = mainPanel.getHeight() - (int) (bottomPanel.getHeight() * 1.5);
        newsize.width = mainPanel.getWidth();
        tabPanel.setSize(newsize);
        tabPanel.setLocation(0, 0);
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}

