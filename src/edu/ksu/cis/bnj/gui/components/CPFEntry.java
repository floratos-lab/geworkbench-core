/*
 * Created on Nov 5, 2003
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

package edu.ksu.cis.bnj.gui.components;

import edu.ksu.cis.bnj.bbn.BBNCPF;
import edu.ksu.cis.bnj.bbn.BBNPDF;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Hashtable;

/**
 * @author Roby Joehanes
 */
public class CPFEntry extends JTextField implements DocumentListener {

    protected BBNCPF cpf;
    protected Hashtable cpfKey;

    public CPFEntry(BBNCPF cpf, Hashtable key) {
        super();
        this.cpf = cpf;
        cpfKey = (Hashtable) key.clone();
        init();
    }


    /**
     * @param columns
     */
    public CPFEntry(int columns, BBNCPF cpf, Hashtable key) {
        super(columns);
        this.cpf = cpf;
        cpfKey = (Hashtable) key.clone();
        init();
    }

    /**
     * @param text
     */
    public CPFEntry(String text, BBNCPF cpf, Hashtable key) {
        super(text);
        this.cpf = cpf;
        cpfKey = (Hashtable) key.clone();
        init();
    }

    /**
     * @param text
     * @param columns
     */
    public CPFEntry(String text, int columns, BBNCPF cpf, Hashtable key) {
        super(text, columns);
        this.cpf = cpf;
        cpfKey = (Hashtable) key.clone();
        init();
    }

    protected void init() {
        double entry = 0.0;
        try {
            entry = cpf.get(cpfKey);
        } catch (Exception e) {
            cpf.put(cpfKey, new BBNPDF(entry)); // 0.0 if all else failed
        }
        setText(String.valueOf(entry));
        getDocument().addDocumentListener(this);
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent evt) {
        String text = getText();

        try {
            double d = Double.parseDouble(text); // TODO: Continuous value would require a revamp here
            cpf.put(cpfKey, new BBNPDF(d));
        } catch (Exception e) {
            // Ignore the exception
        }
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }
}
