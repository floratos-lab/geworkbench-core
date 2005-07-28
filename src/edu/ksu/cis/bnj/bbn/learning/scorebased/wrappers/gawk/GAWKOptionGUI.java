package edu.ksu.cis.bnj.bbn.learning.scorebased.wrappers.gawk;

/*
 * Created on Aug 5, 2003
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

import edu.ksu.cis.bnj.bbn.learning.scorebased.k2.K2OptionGUI;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * @author Roby Joehanes
 */
public class GAWKOptionGUI extends K2OptionGUI {
    protected JSpinner numPopulations, numGenerations;

    /**
     * @param o
     */
    public GAWKOptionGUI(Optionable o) {
        super(o);
    }

    /**
     * @param o
     * @param owner
     */
    public GAWKOptionGUI(Optionable o, JFrame owner) {
        super(o, owner);
    }

    protected void init() {
        super.init();
        mainPanel.remove(orderListScrollPane);
        mainPanel.remove(orderingButton);
        mainPanel.remove(upButton);
        mainPanel.remove(downButton);

        Dimension dim = new Dimension(60, 20);
        JLabel label = new JLabel("Number of generations");
        numGenerations = new JSpinner();
        numGenerations.setValue(new Integer(GAWK.defaultPopulationSize));
        numGenerations.setPreferredSize(dim);
        GUIUtil.gbAdd(mainPanel, label, 0, 11, 1, 1, 6, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, numGenerations, 1, 11, 1, 1, 6, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        label = new JLabel("Number of populations");
        numPopulations = new JSpinner();
        numPopulations.setValue(new Integer(GAWK.defaultGenerations));
        numPopulations.setPreferredSize(dim);

        GUIUtil.gbAdd(mainPanel, label, 0, 12, 1, 1, 6, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, numPopulations, 1, 12, 1, 1, 6, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        //if (source == numGenerations) {} else
        super.actionPerformed(evt);
    }

    /**
     * @see edu.ksu.cis.bnj.gui.GenericOptionGUI#applyOptions()
     */
    protected void applyOptions() {
        GAWK gawk = (GAWK) optionableOwner;
        if (outputButton.isSelected()) {
            gawk.setOutputFile(outputFileText.getText());
        } else {
            gawk.setOutputFile(""); // $NON-NLS-1$
        }
        gawk.setCalculateRMSE(rmseButton.isSelected());
        gawk.setGenerations(((Integer) numGenerations.getValue()).intValue());
        gawk.setPopulationSize(((Integer) numPopulations.getValue()).intValue());
    }


    public static void main(String[] args) {
        Table t = Table.load("examples/asia/asia1000data.arff");
        GAWK gawk = new GAWK(t);
        gawk.getOptionsDialog().setVisible(true);
    }
}
