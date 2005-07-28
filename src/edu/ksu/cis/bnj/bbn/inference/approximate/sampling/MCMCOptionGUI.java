package edu.ksu.cis.bnj.bbn.inference.approximate.sampling;

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
 * @author Roby Joehanes
 */
public class MCMCOptionGUI extends GenericOptionGUI {

    protected JCheckBox rmseButton, outputButton, markovBlanketButton, generateSamplesButton, dumpRMSEButton;
    protected JButton browseSamplesButton, browseRMSEButton, browseOutputButton;
    protected JTextField rmseFileText, outputFileText, samplesFileText;
    protected JLabel outputSampleLabel, numSamplesLabel;
    protected JSpinner numSamples;
    protected JComboBox outputComboBox;
    protected JPanel mainPanel;

    private BNJFileDialogFactory fcFactory = null;

    /**
     * @param o
     */
    public MCMCOptionGUI(Optionable o) {
        super(o);
    }

    /**
     * @param o
     * @param owner
     */
    public MCMCOptionGUI(Optionable o, JFrame owner) {
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
        mainPanel.setBorder(new TitledBorder("Inference Options"));
        outputComboBox = GUIUtil.createComboBox(Settings.getDataDescriptionList());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //lay out the top level
        GUIUtil.gbAdd(dialogPanel, mainPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(dialogPanel, buttonPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);

        outputButton = new JCheckBox("Output result to file");
        outputButton.addActionListener(this);
        outputFileText = new JTextField(30);
        outputFileText.setEnabled(false);
        browseOutputButton = new JButton("Browse");
        browseOutputButton.addActionListener(this);
        browseOutputButton.setEnabled(false);

        //GUIUtil.gbAdd(mainPanel, outputButton,       0,0, 2,1, 12,12,6,12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        //GUIUtil.gbAdd(mainPanel, outputFileText,     0,1, 2,1, 0,36,12,12,  GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        //GUIUtil.gbAdd(mainPanel, browseOutputButton, 2,1, 1,1, 0,0,12,12,  GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        numSamplesLabel = new JLabel("Number of samples:");
        numSamples = new JSpinner();
        numSamples.setValue(new Integer(1000));

        GUIUtil.gbAdd(mainPanel, numSamplesLabel, 0, 3, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, numSamples, 1, 3, 2, 1, 12, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        generateSamplesButton = new JCheckBox("Generate Samples");
        generateSamplesButton.addActionListener(this);
        samplesFileText = new JTextField(30);
        samplesFileText.setEnabled(false);
        browseSamplesButton = new JButton("Browse");
        browseSamplesButton.addActionListener(this);
        browseSamplesButton.setEnabled(false);
        outputSampleLabel = new JLabel("Output Sample Format");

        GUIUtil.gbAdd(mainPanel, generateSamplesButton, 0, 4, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, samplesFileText, 0, 5, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, browseSamplesButton, 2, 5, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputSampleLabel, 0, 6, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, outputComboBox, 0, 7, 3, 1, 0, 24, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        rmseButton = new JCheckBox("Calculate RMSE");
        rmseButton.addActionListener(this);
        dumpRMSEButton = new JCheckBox("Dump RMSE Plot");
        dumpRMSEButton.addActionListener(this);
        dumpRMSEButton.setEnabled(false);
        rmseFileText = new JTextField(30);
        rmseFileText.setEnabled(false);
        browseRMSEButton = new JButton("Browse");
        browseRMSEButton.addActionListener(this);
        browseRMSEButton.setEnabled(false);

        GUIUtil.gbAdd(mainPanel, rmseButton, 0, 8, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, dumpRMSEButton, 0, 9, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, rmseFileText, 0, 10, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, browseRMSEButton, 2, 10, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        markovBlanketButton = new JCheckBox("Use Markov Blanket Score (may help or may hurt)");
        markovBlanketButton.addActionListener(this);

        GUIUtil.gbAdd(mainPanel, markovBlanketButton, 0, 11, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(mainPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        this.setContentPane(mainPanel);
        super.init();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == rmseButton || source == dumpRMSEButton) {
            boolean enabled = rmseButton.isSelected();
            boolean enabled2 = enabled && dumpRMSEButton.isSelected();
            dumpRMSEButton.setEnabled(enabled);
            rmseFileText.setEnabled(enabled2);
            browseRMSEButton.setEnabled(enabled2);
        } else if (source == generateSamplesButton) {
            boolean enabled = generateSamplesButton.isSelected();
            samplesFileText.setEnabled(enabled);
            browseSamplesButton.setEnabled(enabled);
            outputSampleLabel.setEnabled(enabled);
            outputComboBox.setEnabled(enabled);
        } else if (source == browseRMSEButton) {
            selected_BrowseRMSE();
        } else if (source == browseSamplesButton) {
            selected_BrowseSamples();
        }
    }

    protected void selected_BrowseRMSE() {
        File fc = fcFactory.saveRMSEPlotFiles();
        if (fc == null) return;
        rmseFileText.setText(fc.getAbsolutePath());
    }

    protected void selected_BrowseSamples() {
        File fc = fcFactory.saveDataFiles();
        if (fc == null) return;
        samplesFileText.setText(fc.getAbsolutePath());
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
        MCMC mcmc = (MCMC) optionableOwner;
        mcmc.setMaxIteration(((Integer) numSamples.getValue()).intValue());
        mcmc.generateData(generateSamplesButton.isSelected());
        mcmc.setUseMarkovBlanketScore(markovBlanketButton.isSelected());
    }

    public static void main(String[] args) {
        AIS ais = new AIS();
        ais.getOptionsDialog().setVisible(true);
    }
}
