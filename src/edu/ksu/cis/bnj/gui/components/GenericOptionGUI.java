package edu.ksu.cis.bnj.gui.components;

/*
 * Created on 30 Jul 2003
 *
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

import edu.ksu.cis.kdd.util.gui.OptionGUI;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Roby Joehanes
 */
public abstract class GenericOptionGUI extends JDialog implements OptionGUI, ActionListener, WindowListener {
    protected JFrame owner;
    protected Optionable optionableOwner;

    public GenericOptionGUI(Optionable o) {
        this(o, null);
    }

    public GenericOptionGUI(Optionable o, JFrame owner) {
        optionableOwner = o;
        this.owner = owner;
        init();
    }

    protected void init() {
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void selected_Close() {
        if (owner == null) ; //System.exit(0);
        setVisible(false);
        owner.setVisible(true);
        owner = null;
    }

    /* (non-Javadoc)
     * @see edu.ksu.cis.kdd.bisonparsers.gui.OptionGUI#isModified()
     */
    public boolean isModified() {
        return false;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        //Object source = evt.getSource();
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowClosing(WindowEvent evt) {
        if (evt.getSource() == this) selected_Close();
    }

    public void windowDeactivated(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowOpened(WindowEvent evt) {
    }

    public Optionable getOptionableOwner() {
        applyOptions();
        return optionableOwner;
    }

    protected abstract void applyOptions();
}
