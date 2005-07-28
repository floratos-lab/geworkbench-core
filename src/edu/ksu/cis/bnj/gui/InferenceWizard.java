/*
 * Created on 23 Jul 2003
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
package edu.ksu.cis.bnj.gui;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.inference.ApproximateInference;
import edu.ksu.cis.bnj.bbn.inference.Inference;
import edu.ksu.cis.bnj.bbn.inference.InferenceResult;
import edu.ksu.cis.bnj.bbn.inference.RMSECalculator;
import edu.ksu.cis.bnj.bbn.inference.approximate.sampling.MCMC;
import edu.ksu.cis.bnj.bbn.inference.ls.LS;
import edu.ksu.cis.bnj.gui.components.BNJFileDialogFactory;
import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import edu.ksu.cis.kdd.util.gui.PlotterPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

/**
 * @author Roby Joehanes, Charlie Thornton, Julie Thornton
 */
public class InferenceWizard extends JFrame implements ActionListener, WindowListener {
    private GUI owner;

    // GUI variables
    private JPanel fileSelectionPanel = null;
    private JPanel inferenceSelectionPanel = null, inferenceOptionPanel = null, resultPanel = null;
    private JPanel mainPanel = null, rmsePlotPanel = null;
    private JButton prevButton = null, nextButton = null, cancelButton = null, browseNetButton = null, browseEvidenceButton = null, browseOutputButton = null, browseRMSEButton = null, browseSamplesButton = null, seeRMSEPlotButton = null;
    private JRadioButton loadFromFileRadioButton = null, useScreenRadioButton = null, exactRadioButton = null, approxRadioButton = null, exactInfRadioButton[] = null, approxInfRadioButton[] = null;
    private JCheckBox evidenceButton = null, outputButton = null, rmseButton = null, dumpRMSEButton = null, generateSamplesButton = null, markovBlanketButton = null;
    private JTextField loadFileText = null, evidenceFileText = null, outputFileText = null, rmseFileText = null, samplesFileText = null;
    private JComboBox outputComboBox = null;
    private JSpinner numSamples = null;
    private JLabel numSamplesLabel = null, outputSampleLabel = null;

    // Screen state
    private int sectionNo = 1, numExactInf, numApproxInf;

    // Inference engines // <-- Note: Guys, this should go to the config file!
    private String[][] exactEngines = {{"Lauritzen-Spiegelhalter (LS) / junction tree", "edu.ksu.cis.bnj.bbn.inference.ls.LS"}, {"Variable elimination (elimbel)", "edu.ksu.cis.bnj.bbn.inference.elimbel.ElimBel"}, {"Loop Cutset Conditioning", "edu.ksu.cis.bnj.bbn.inference.cutset.BoundedCutset"}, {"Pearl's propagation (for tree only)", "edu.ksu.cis.bnj.bbn.inference.pearl.Pearl"}};

    // This, too, should go to the config file
    private String[][] approxEngines = {{"Logic Sampling", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.LogicSampling"}, {"Forward Sampling", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.ForwardSampling"}, {"Likelihood Weighting", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.LikelihoodWeighting"}, {"Self-Importance Sampling", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.SelfImportance"}, {"Adaptive-Importance Sampling", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.AIS"}, {"Pearl MCMC Method", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.PearlMCMC"}, {"Chavez MCMC Method", "edu.ksu.cis.bnj.bbn.inference.approximate.sampling.ChavezMCMC"}};

    // Other states
    private BBNGraph graph = null;
    private Inference inference = null;
    private BNJFileDialogFactory fcFactory = null;


    public InferenceWizard(GUI owner) {
        super();
        this.owner = owner;
        if (owner != null) {
            // Gain control of program while running the wizard.
            owner.setEnabled(false);
        }
        init();
    }

    private void init() {
        Settings.setLanguage(Locale.ENGLISH, true);
        setSize(new Dimension(800, 600));
        GUIUtil.centerToScreen(this);
        fcFactory = new BNJFileDialogFactory(this);

        setTitle("Inference Wizard");
        outputComboBox = GUIUtil.createComboBox(Settings.getDataDescriptionList());

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        //lay out the top level
        GUIUtil.gbAdd(dialogPanel, mainPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(dialogPanel, buttonPanel, 0, 1, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);

        //make buttons
        prevButton = new JButton("< Prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(false);
        nextButton = new JButton("Next >");
        nextButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        //add the buttons to the button panel
        GUIUtil.gbAdd(buttonPanel, prevButton, 0, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(buttonPanel, nextButton, 1, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(buttonPanel, cancelButton, 2, 0, 1, 1, 0, 0, 6, 6, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        // ---------------------------------
        // | File selection panel
        // ---------------------------------

        fileSelectionPanel = new JPanel();
        fileSelectionPanel.setLayout(new GridBagLayout());
        fileSelectionPanel.setBorder(new TitledBorder("Network Selection"));
        GUIUtil.gbAdd(mainPanel, fileSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);

        JLabel label = new JLabel("Which Bayesian Network do you want to infer?");
        GUIUtil.gbAdd(fileSelectionPanel, label, 0, 0, 3, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        loadFromFileRadioButton = new JRadioButton("Load from file");
        loadFromFileRadioButton.addActionListener(this);
        loadFromFileRadioButton.setSelected(true);
        useScreenRadioButton = new JRadioButton("Use the one loaded in the GUI");
        useScreenRadioButton.addActionListener(this);
        useScreenRadioButton.setEnabled(owner != null);
        loadFileText = new JTextField(30);
        browseNetButton = new JButton("Browse");
        browseNetButton.addActionListener(this);

        ButtonGroup group = new ButtonGroup();
        group.add(loadFromFileRadioButton);
        group.add(useScreenRadioButton);

        GUIUtil.gbAdd(fileSelectionPanel, loadFromFileRadioButton, 0, 1, 1, 1, 0, 18, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, loadFileText, 0, 2, 1, 1, 0, 36, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, browseNetButton, 1, 2, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, useScreenRadioButton, 0, 3, 1, 1, 0, 18, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        evidenceButton = new JCheckBox("Evidence file");
        evidenceButton.addActionListener(this);
        evidenceFileText = new JTextField(30);
        evidenceFileText.setEnabled(false);
        browseEvidenceButton = new JButton("Browse");
        browseEvidenceButton.addActionListener(this);
        browseEvidenceButton.setEnabled(false);

        GUIUtil.gbAdd(fileSelectionPanel, evidenceButton, 0, 4, 1, 1, 0, 18, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, evidenceFileText, 0, 5, 1, 1, 0, 36, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, browseEvidenceButton, 1, 5, 1, 1, 0, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(fileSelectionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        // ---------------------------------
        // | Inference selection panel
        // ---------------------------------
        inferenceSelectionPanel = new JPanel();
        inferenceSelectionPanel.setLayout(new GridBagLayout());
        inferenceSelectionPanel.setBorder(new TitledBorder("Inference Selection"));
        label = new JLabel("Which inference method do you prefer?");
        GUIUtil.gbAdd(inferenceSelectionPanel, label, 0, 0, 3, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        //make buttons
        exactRadioButton = new JRadioButton("Exact Inference");
        exactRadioButton.addActionListener(this);
        exactRadioButton.setSelected(true);
        approxRadioButton = new JRadioButton("Approximate Inference");
        approxRadioButton.addActionListener(this);
        approxRadioButton.setSelected(false);

        group = new ButtonGroup();
        group.add(exactRadioButton);
        group.add(approxRadioButton);

        numExactInf = exactEngines.length;
        exactInfRadioButton = new JRadioButton[numExactInf];

        group = new ButtonGroup();
        for (int i = 0; i < numExactInf; i++) {
            exactInfRadioButton[i] = new JRadioButton(exactEngines[i][0]);
            exactInfRadioButton[i].addActionListener(this);
            exactInfRadioButton[i].setSelected(false);
            group.add(exactInfRadioButton[i]);
        }
        exactInfRadioButton[0].setSelected(true);

        numApproxInf = approxEngines.length;
        approxInfRadioButton = new JRadioButton[numApproxInf];

        group = new ButtonGroup();
        for (int i = 0; i < numApproxInf; i++) {
            approxInfRadioButton[i] = new JRadioButton(approxEngines[i][0]);
            approxInfRadioButton[i].addActionListener(this);
            approxInfRadioButton[i].setEnabled(false);
            approxInfRadioButton[i].setSelected(false);
            group.add(approxInfRadioButton[i]);
        }
        approxInfRadioButton[0].setSelected(true);

        GUIUtil.gbAdd(inferenceSelectionPanel, exactRadioButton, 0, 1, 1, 1, 0, 18, 0, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceSelectionPanel, approxRadioButton, 0, 3 + numExactInf, 1, 1, 6, 18, 0, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        for (int i = 0; i < numExactInf; i++) {
            GUIUtil.gbAdd(inferenceSelectionPanel, exactInfRadioButton[i], 0, i + 2, 1, 1, 0, 36, 0, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        }

        for (int i = 0; i < numApproxInf; i++) {
            GUIUtil.gbAdd(inferenceSelectionPanel, approxInfRadioButton[i], 0, i + numExactInf + 4, 1, 1, 0, 36, 0, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        }

        GUIUtil.gbAdd(inferenceSelectionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        // ---------------------------------
        // | Inference option panel
        // ---------------------------------
        inferenceOptionPanel = new JPanel();
        inferenceOptionPanel.setLayout(new GridBagLayout());
        inferenceOptionPanel.setBorder(new TitledBorder("Inference Options"));

        outputButton = new JCheckBox("Output result to file");
        outputButton.addActionListener(this);
        outputFileText = new JTextField(30);
        outputFileText.setEnabled(false);
        browseOutputButton = new JButton("Browse");
        browseOutputButton.addActionListener(this);
        browseOutputButton.setEnabled(false);

        GUIUtil.gbAdd(inferenceOptionPanel, outputButton, 0, 0, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, outputFileText, 0, 1, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, browseOutputButton, 2, 1, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        numSamplesLabel = new JLabel("Number of samples:");
        numSamples = new JSpinner();
        numSamples.setValue(new Integer(1000));

        GUIUtil.gbAdd(inferenceOptionPanel, numSamplesLabel, 0, 3, 1, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, numSamples, 1, 3, 2, 1, 12, 0, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        generateSamplesButton = new JCheckBox("Generate Samples");
        generateSamplesButton.addActionListener(this);
        samplesFileText = new JTextField(30);
        samplesFileText.setEnabled(false);
        browseSamplesButton = new JButton("Browse");
        browseSamplesButton.addActionListener(this);
        browseSamplesButton.setEnabled(false);
        outputSampleLabel = new JLabel("Output Sample Format");

        GUIUtil.gbAdd(inferenceOptionPanel, generateSamplesButton, 0, 4, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, samplesFileText, 0, 5, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, browseSamplesButton, 2, 5, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, outputSampleLabel, 0, 6, 2, 1, 12, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, outputComboBox, 0, 7, 3, 1, 0, 24, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

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

        GUIUtil.gbAdd(inferenceOptionPanel, rmseButton, 0, 8, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, dumpRMSEButton, 0, 9, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, rmseFileText, 0, 10, 2, 1, 0, 36, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, browseRMSEButton, 2, 10, 1, 1, 0, 0, 12, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

        markovBlanketButton = new JCheckBox("Use Markov Blanket Score (may help or may hurt)");
        markovBlanketButton.addActionListener(this);

        GUIUtil.gbAdd(inferenceOptionPanel, markovBlanketButton, 0, 11, 1, 1, 0, 12, 6, 12, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
        GUIUtil.gbAdd(inferenceOptionPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        seeRMSEPlotButton = new JButton("Plot");
        seeRMSEPlotButton.addActionListener(this);
        seeRMSEPlotButton.setPreferredSize(prevButton.getSize());
        seeRMSEPlotButton.setMaximumSize(prevButton.getSize());

        this.setContentPane(dialogPanel);
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private Inference getSelectedInferenceMethod() {
        if (exactRadioButton.isSelected()) {
            for (int i = 0; i < numExactInf; i++) {
                if (exactInfRadioButton[i].isSelected()) {
                    return Inference.load(exactEngines[i][1], graph);
                }
            }
        } else {
            for (int i = 0; i < numApproxInf; i++) {
                if (approxInfRadioButton[i].isSelected()) {
                    return Inference.load(approxEngines[i][1], graph);
                }
            }
        }
        return null;
    }

    private void changeScreen() {
        mainPanel.removeAll();
        switch (sectionNo) {
            case 1:
                GUIUtil.gbAdd(mainPanel, fileSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 2:
                GUIUtil.gbAdd(mainPanel, inferenceSelectionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 3:
                GUIUtil.gbAdd(mainPanel, inferenceOptionPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            case 4:
                GUIUtil.gbAdd(mainPanel, resultPanel, 0, 0, 1, 1, 12, 12, 12, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                break;
            default:
                throw new RuntimeException("Bug!");
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void selected_BrowseNet() {
        File fc = fcFactory.openNetFiles();
        if (fc == null) return;
        loadFileText.setText(fc.getAbsolutePath());
    }

    private void selected_BrowseSamples() {
        File fc = fcFactory.saveDataFiles();
        if (fc == null) return;
        samplesFileText.setText(fc.getAbsolutePath());
    }

    private void selected_BrowseEvidence() {
        File fc = fcFactory.openEvidenceFiles();
        if (fc == null) return;
        evidenceFileText.setText(fc.getAbsolutePath());
    }

    private void selected_BrowseOutput() {
        File fc = fcFactory.saveOutputFiles();
        if (fc == null) return;
        outputFileText.setText(fc.getAbsolutePath());
    }

    private void selected_BrowseRMSE() {
        File fc = fcFactory.saveRMSEPlotFiles();
        if (fc == null) return;
        rmseFileText.setText(fc.getAbsolutePath());
    }

    private void selected_Next() {
        switch (sectionNo) {
            case 1:
                // Load the graph or fetch the graph from the main GUI
                if (loadFromFileRadioButton.isSelected()) {
                    String filename = loadFileText.getText();
                    if (filename == null || filename.length() == 0) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "You must fill in the file name first!");
                        return;
                    }
                    try {
                        graph = BBNGraph.load(filename);
                    } catch (Exception e) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", Messages.getString("Error.CantOpenFile")); // $NON-NLS-1$
                        return;
                    }
                } else {
                    assert (owner != null);
                    graph = owner.getMainPanel().getGraph();
                }

                // If user wants to include the evidence, load it as well
                if (evidenceButton.isSelected()) {
                    try {
                        graph.loadEvidence(evidenceFileText.getText());
                    } catch (Exception e) {
                        DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot load evidence file!");
                        return;
                    }
                }
                prevButton.setEnabled(true);
                break;
            case 2:
                inference = getSelectedInferenceMethod();
                boolean visible = inference instanceof ApproximateInference;
                numSamples.setVisible(visible);
                numSamplesLabel.setVisible(visible);
                rmseButton.setVisible(visible);
                dumpRMSEButton.setVisible(visible);
                browseRMSEButton.setVisible(visible);
                rmseFileText.setVisible(visible);
                generateSamplesButton.setVisible(visible);
                samplesFileText.setVisible(visible);
                browseSamplesButton.setVisible(visible);
                outputSampleLabel.setVisible(visible);
                outputComboBox.setVisible(visible);
                markovBlanketButton.setVisible(visible);
                if (!visible) {
                    rmseButton.setSelected(false);
                    dumpRMSEButton.setSelected(false);
                    generateSamplesButton.setSelected(false);
                    markovBlanketButton.setSelected(false);
                }
                nextButton.setText("Infer!");
                break;
            case 3:
                execute();
                break;
        }
        sectionNo++;
        changeScreen();
        if (sectionNo == 4) nextButton.setEnabled(false);
    }

    private void selected_Prev() {
        sectionNo--;
        if (sectionNo < 3) nextButton.setText("Next >");
        nextButton.setEnabled(true);
        changeScreen();
        if (sectionNo == 1) prevButton.setEnabled(false);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == prevButton) {
            selected_Prev();
        } else if (source == nextButton) {
            selected_Next();
        } else if (source == cancelButton) {
            selected_Close();
        } else if (source == loadFromFileRadioButton) {
            browseNetButton.setEnabled(true);
            loadFileText.setEnabled(true);
        } else if (source == useScreenRadioButton) {
            browseNetButton.setEnabled(false);
            loadFileText.setEnabled(false);
        } else if (source == approxRadioButton) {
            for (int i = 0; i < numApproxInf; i++) {
                approxInfRadioButton[i].setEnabled(true);
            }
            for (int i = 0; i < numExactInf; i++) {
                exactInfRadioButton[i].setEnabled(false);
            }
        } else if (source == exactRadioButton) {
            for (int i = 0; i < numApproxInf; i++) {
                approxInfRadioButton[i].setEnabled(false);
            }
            for (int i = 0; i < numExactInf; i++) {
                exactInfRadioButton[i].setEnabled(true);
            }
        } else if (source == browseNetButton) {
            selected_BrowseNet();
        } else if (source == evidenceButton) {
            boolean enabled = evidenceButton.isSelected();
            evidenceFileText.setEnabled(enabled);
            browseEvidenceButton.setEnabled(enabled);
        } else if (source == browseEvidenceButton) {
            selected_BrowseEvidence();
        } else if (source == rmseButton || source == dumpRMSEButton) {
            boolean enabled = rmseButton.isSelected();
            boolean enabled2 = enabled && dumpRMSEButton.isSelected();
            dumpRMSEButton.setEnabled(enabled);
            rmseFileText.setEnabled(enabled2);
            browseRMSEButton.setEnabled(enabled2);
        } else if (source == outputButton) {
            boolean enabled = outputButton.isSelected();
            outputFileText.setEnabled(enabled);
            browseOutputButton.setEnabled(enabled);
        } else if (source == generateSamplesButton) {
            boolean enabled = generateSamplesButton.isSelected();
            samplesFileText.setEnabled(enabled);
            browseSamplesButton.setEnabled(enabled);
            outputSampleLabel.setEnabled(enabled);
            outputComboBox.setEnabled(enabled);
        } else if (source == browseOutputButton) {
            selected_BrowseOutput();
        } else if (source == browseRMSEButton) {
            selected_BrowseRMSE();
        } else if (source == browseSamplesButton) {
            selected_BrowseSamples();
        } else if (source == seeRMSEPlotButton) {
            if (rmsePlotPanel == null) return; // shouldn't be
            JDialog jd = new JDialog(this);
            jd.setTitle("RMSE Plot");
            jd.setSize(new Dimension(640, 480));
            GUIUtil.centerToScreen(jd);
            jd.setContentPane(rmsePlotPanel);
            jd.setVisible(true);
        }
    }

    private void selected_Close() {
        if (owner == null) ;// System.exit(0);
        setVisible(false);
        //reenable the owner component to continue program.
        owner.setEnabled(true);
        owner = null;
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

    /**
     * Main brain of this GUI
     */
    private void execute() {
        rmsePlotPanel = null;
        JTree resultTree = new JTree();

        //resultTree.putClientProperty("JTree.lineStyle", "Angled"); // $NON-NLS-1$ // $NON-NLS-2$
        resultTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        Insets inset = new Insets(0, 5, 0, 0);
        resultTree.setBorder(new EmptyBorder(inset));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        resultTree.setCellRenderer(renderer);

        // TODO: Must set up the inference options here!
        // The ones that left:
        // --> Save RMSE plot (done -- Julie)
        if (inference instanceof MCMC) {
            MCMC mcmc = (MCMC) inference;
            int ns = ((Integer) numSamples.getValue()).intValue();
            mcmc.setMaxIteration(ns);
            mcmc.generateData(generateSamplesButton.isSelected());
            mcmc.setUseMarkovBlanketScore(markovBlanketButton.isSelected());
        }

        if (dumpRMSEButton.isSelected() && inference instanceof ApproximateInference) {
            try {
                FileOutputStream outfile = new FileOutputStream(rmseFileText.getText());
                ((ApproximateInference) inference).setRMSEfile(outfile);
            } catch (IOException ioe) {
                System.out.println("error writing to rmse file");
            }
        }

        InferenceResult result = inference.getMarginals();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(graph.getName());
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        int numRows = 0;
        DecimalFormat format = new DecimalFormat("##0.00'%'"); // $NON-NLS-1$

        for (Enumeration e = result.keys(); e.hasMoreElements(); numRows++) {
            String nodeName = (String) e.nextElement();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeName);
            root.add(node);
            Hashtable values = (Hashtable) result.get(nodeName);
            for (Enumeration f = values.keys(); f.hasMoreElements(); numRows++) {
                String val = (String) f.nextElement();
                double percent = Double.parseDouble(values.get(val).toString()) * 100;
                DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(val + " = " + format.format(percent)); // $NON-NLS-1$
                node.add(leaf);
            }
        }

        resultTree.setModel(treeModel);
        resultTree.revalidate();

        for (int i = 0; i < numRows; i++) {
            resultTree.expandRow(i);
        }

        if (outputButton.isSelected()) {
            try {
                result.save(outputFileText.getText());
            } catch (Exception e) {
                DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot save inference output!");
            }
        }

        if (inference instanceof MCMC && generateSamplesButton.isSelected()) {
            MCMC mcmc = (MCMC) inference;
            try {
                Table table = mcmc.getData();
                String outputFormat = Settings.getDataExtensionFromDescription((String) outputComboBox.getSelectedItem());
                table.save(samplesFileText.getText(), outputFormat);
            } catch (Exception e) {
                e.printStackTrace();
                DialogFactory.getOKDialog(this, DialogFactory.ERROR, "Error!", "Cannot save samples output!");
            }
        }

        resultPanel = new JPanel();
        resultPanel.setLayout(new GridBagLayout());
        resultPanel.setBorder(new TitledBorder(new EtchedBorder(), "Inference Result"));
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 2;
        constraint.gridheight = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 2.0;
        constraint.weighty = 15.0;
        constraint.insets = new Insets(12, 12, 12, 12);
        resultPanel.add(new JScrollPane(resultTree), constraint);

        if (rmseButton.isSelected() && inference instanceof ApproximateInference) {
            LS ls = new LS(graph);
            InferenceResult exact = ls.getMarginals();
            JLabel rmseLabel = new JLabel("RMSE = " + exact.computeRMSE(result));
            constraint.gridx = 0;
            constraint.gridy = 1;
            constraint.gridwidth = 1;
            constraint.gridheight = 1;
            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1.0;
            constraint.weighty = 1.0;
            resultPanel.add(rmseLabel, constraint);
            if (dumpRMSEButton.isSelected()) {
                MCMC mcmc = (MCMC) inference;
                RMSECalculator r = mcmc.getRMSEWriter();
                rmsePlotPanel = new PlotterPanel(r.getRMSEPoints());
                constraint.gridx = 1;
                constraint.gridy = 1;
                constraint.gridwidth = 1;
                constraint.gridheight = 1;
                constraint.fill = GridBagConstraints.BOTH;
                constraint.weightx = 1.0;
                constraint.weighty = 1.0;
                constraint.anchor = GridBagConstraints.EAST;
                resultPanel.add(seeRMSEPlotButton, constraint);
            }
        }
    }

    public static void main(String[] args) {
        Settings.loadEnglishGUISettings();
        new InferenceWizard(null).show();
    }

}
