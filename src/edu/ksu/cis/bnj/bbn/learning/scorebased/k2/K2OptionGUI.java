package edu.ksu.cis.bnj.bbn.learning.scorebased.k2;

/*
 * Created on Aug 1, 2003
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

import edu.ksu.cis.bnj.bbn.learning.Learner;
import edu.ksu.cis.bnj.gui.components.BNJFileDialogFactory;
import edu.ksu.cis.bnj.gui.components.GenericOptionGUI;
import edu.ksu.cis.kdd.data.Data;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class K2OptionGUI extends GenericOptionGUI {

    protected JCheckBox rmseButton, outputButton, orderingButton;
    protected JButton browseOutputButton, upButton, downButton;
    protected JTextField outputFileText;
    protected JSpinner maxParents;
    protected JComboBox outputComboBox;
    protected JPanel mainPanel;
    protected JList orderList;
    protected JScrollPane orderListScrollPane;

    protected BNJFileDialogFactory fcFactory = null;
    protected Object[] attrArray;

    /**
     * @param o
     */
    public K2OptionGUI(Optionable o) {
        super(o);
    }

    /**
     * @param o
     * @param owner
     */
    public K2OptionGUI(Optionable o, JFrame owner) {
        super(o, owner);
    }

    protected void init() {
        GUIUtil.switchToNativeUI(this);
        setSize(new Dimension(800, 600));
        GUIUtil.centerToScreen(this);
        fcFactory = new BNJFileDialogFactory(this);

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new GridBagLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new TitledBorder("K2 Options"));
        outputComboBox = GUIUtil.createComboBox(Settings.getNetDescriptionList());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //lay out the top level
        GUIUtil.gbAdd(dialogPanel, mainPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(dialogPanel, buttonPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);

        outputButton = new JCheckBox("Output learned network to file");
        outputButton.addActionListener(this);
        outputFileText = new JTextField(30);
        outputFileText.setEnabled(false);
        browseOutputButton = new JButton("Browse");
        browseOutputButton.addActionListener(this);
        browseOutputButton.setEnabled(false);
        outputComboBox.setEnabled(false);

        GUIUtil.gbAdd(mainPanel, outputButton, 0, 0, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputFileText, 0, 1, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, browseOutputButton, 2, 1, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputComboBox, 0, 2, 3, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        maxParents = new JSpinner();
        maxParents.setValue(new Integer(5));
        Dimension dim = new Dimension(60, 20);
        maxParents.setPreferredSize(dim);

        GUIUtil.gbAdd(mainPanel, new JLabel("Max number of parents:"), 0, 3, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, maxParents, 1, 3, 2, 1, 12, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        orderingButton = new JCheckBox("Custom ordering");
        orderingButton.addActionListener(this);

        dim = new Dimension(80, 25);
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        upButton.setPreferredSize(dim);
        downButton = new JButton("Down");
        downButton.addActionListener(this);
        downButton.setPreferredSize(dim);
        Learner k2 = (Learner) optionableOwner;
        Data data = k2.getData();
        List attrs = data == null ? new LinkedList() : data.getRelevantAttributes();

        attrArray = attrs.toArray();
        orderList = new JList(attrArray);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderListScrollPane = new JScrollPane(orderList);
        dim = new Dimension(100, 120);
        orderListScrollPane.setPreferredSize(dim);
        orderListScrollPane.setMaximumSize(dim);
        orderListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderList.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);

        GUIUtil.gbAdd(mainPanel, orderingButton, 0, 4, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, orderListScrollPane, 0, 5, 1, 4, 6, 36, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, upButton, 1, 6, 1, 1, 18, 6, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, downButton, 1, 7, 1, 1, 6, 6, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        rmseButton = new JCheckBox("Calculate RMSE");
        rmseButton.addActionListener(this);

        GUIUtil.gbAdd(mainPanel, rmseButton, 0, 10, 1, 1, 6, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        GUIUtil.gbAdd(mainPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        this.setContentPane(mainPanel);
        super.init();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == outputButton) {
            boolean enabled = outputButton.isSelected();
            outputFileText.setEnabled(enabled);
            browseOutputButton.setEnabled(enabled);
            outputComboBox.setEnabled(enabled);
        } else if (source == orderingButton) {
            boolean enabled = orderingButton.isSelected();
            orderList.setEnabled(enabled);
            upButton.setEnabled(enabled);
            downButton.setEnabled(enabled);
        } else if (source == upButton) {
            int i = orderList.getSelectedIndex();
            if (i < 1) return;
            Object temp = attrArray[i];
            attrArray[i] = attrArray[i - 1];
            attrArray[i - 1] = temp;
            orderList.setListData(attrArray);
            orderList.repaint();
            orderList.setSelectedIndex(i - 1);
        } else if (source == downButton) {
            int i = orderList.getSelectedIndex();
            if (i == attrArray.length - 1 || i == -1) return;
            Object temp = attrArray[i];
            attrArray[i] = attrArray[i + 1];
            attrArray[i + 1] = temp;
            orderList.setListData(attrArray);
            orderList.repaint();
            orderList.setSelectedIndex(i + 1);
        } else if (source == browseOutputButton) {
            File fc = fcFactory.saveNetFiles();
            if (fc == null) return;
            outputFileText.setText(fc.getAbsolutePath());
        }
    }

    /**
     * @see edu.ksu.cis.kdd.util.gui.OptionGUI#getMainPane()
     */
    public JPanel getMainPane() {
        return mainPanel;
    }

    /**
     * @see edu.ksu.cis.bnj.gui.GenericOptionGUI#applyOptions()
     */
    protected void applyOptions() {
        K2 k2 = (K2) optionableOwner;
        if (outputButton.isSelected()) {
            k2.setOutputFile(outputFileText.getText());
        } else {
            k2.setOutputFile(""); // $NON-NLS-1$
        }
        k2.setCalculateRMSE(rmseButton.isSelected());
        if (orderingButton.isSelected()) {
            LinkedList order = new LinkedList();
            int max = attrArray.length;
            for (int i = 0; i < max; i++) {
                order.add(attrArray[i].toString());
            }
            k2.setOrdering(order);
        } else
            k2.setOrdering(null);
    }

    public static void main(String[] args) {
        Table t = Table.load("examples/asia/asia1000data.arff");
        K2 k2 = new K2(t);
        k2.getOptionsDialog().setVisible(true);
    }
}
