/*
 * Created on Sep 3, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

import edu.ksu.cis.bnj.bbn.learning.ScoreBasedLearner;
import edu.ksu.cis.bnj.gui.components.BNJFileDialogFactory;
import edu.ksu.cis.bnj.gui.components.GenericOptionGUI;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Silpan Patel
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BaseOptionsGUI extends GenericOptionGUI {
    protected JCheckBox outputFileButton;
    protected JButton browseOutputFileButton;
    protected JTextField outputFileText;
    protected JSpinner maxParents;
    protected JComboBox outputComboBox;
    protected JPanel mainPanel;
    protected JList orderList;
    protected JScrollPane orderListScrollPane;
    protected BNJFileDialogFactory fcFactory = null;

    public BaseOptionsGUI(Optionable o) {
        super(o);
    }

    public BaseOptionsGUI(Optionable o, JFrame owner) {
        super(o, owner);
    }

    protected void init() {
        GUIUtil.switchToNativeUI(this);
        setSize(new Dimension(800, 600));
        GUIUtil.centerToScreen(this);
        //System.out.println("creating fcFactory");
        //fcFactory = new BNJFileDialogFactory(this);

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new GridBagLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new TitledBorder("Base Options"));
        outputComboBox = GUIUtil.createComboBox(Settings.getNetDescriptionList());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //lay out the top level
        GUIUtil.gbAdd(dialogPanel, mainPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(dialogPanel, buttonPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);

        outputFileButton = new JCheckBox("Output learned network to file");
        outputFileButton.addActionListener(this);
        outputFileText = new JTextField(30);
        outputFileText.setEnabled(false);
        browseOutputFileButton = new JButton("Browse");
        browseOutputFileButton.addActionListener(this);
        browseOutputFileButton.setEnabled(false);
        outputComboBox.setEnabled(false);

        GUIUtil.gbAdd(mainPanel, outputFileButton, 0, 0, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputFileText, 0, 1, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, browseOutputFileButton, 2, 1, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputComboBox, 0, 2, 3, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        maxParents = new JSpinner();
        maxParents.setValue(new Integer(5));
        Dimension dim = new Dimension(60, 20);
        maxParents.setPreferredSize(dim);

        GUIUtil.gbAdd(mainPanel, new JLabel("Max number of parents:"), 0, 3, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, maxParents, 1, 3, 2, 1, 12, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == outputFileButton) {
            boolean enabled = outputFileButton.isSelected();
            outputFileText.setEnabled(enabled);
            browseOutputFileButton.setEnabled(enabled);
            outputComboBox.setEnabled(enabled);
        } else if (source == browseOutputFileButton) {
            if (fcFactory == null) {
                //System.out.println("fcFactory is null");
                fcFactory = new BNJFileDialogFactory(this);
            }
            File fc = fcFactory.saveNetFiles();
            if (fc == null)
                return;
            outputFileText.setText(fc.getAbsolutePath());
        }
        /*else if (source == orderingButton) {
            boolean enabled = orderingButton.isSelected();
            orderList.setEnabled(enabled);
            upButton.setEnabled(enabled);
            downButton.setEnabled(enabled);
        } else if (source == upButton) {
            int i = orderList.getSelectedIndex();
            if (i < 1) return;
            Object temp = attrArray[i];
            attrArray[i] = attrArray[i-1];
            attrArray[i-1] = temp;
            orderList.setListData(attrArray);
            orderList.repaint();
            orderList.setSelectedIndex(i-1);
        } else if (source == downButton) {
            int i = orderList.getSelectedIndex();
            if (i == attrArray.length-1 || i == -1) return;
            Object temp = attrArray[i];
            attrArray[i] = attrArray[i+1];
            attrArray[i+1] = temp;
            orderList.setListData(attrArray);
            orderList.repaint();
            orderList.setSelectedIndex(i+1);
        }  */
    }

    public JPanel getMainPane() {
        return mainPanel;
    }

    protected void applyOptions() {
        ScoreBasedLearner sbl = (ScoreBasedLearner) optionableOwner;
        if (outputFileButton.isSelected()) {
            sbl.setOutputFile(outputFileText.getText());
        } else {
            sbl.setOutputFile("");
        }
        sbl.setParentLimit(((Integer) maxParents.getValue()).intValue());
    }

}
