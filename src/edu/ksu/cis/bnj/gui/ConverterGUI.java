package edu.ksu.cis.bnj.gui;

/*
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
 *
 */

import edu.ksu.cis.bnj.bbn.converter.ConverterData;
import edu.ksu.cis.bnj.bbn.converter.ConverterFactory;
import edu.ksu.cis.kdd.data.Database;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.TableSet;
import edu.ksu.cis.kdd.util.gui.FileDialogFactory;
import edu.ksu.cis.kdd.util.gui.GUIUtil;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The GUI for Batch Converter
 *
 * @author Roby Joehanes
 */
public class ConverterGUI extends JFrame implements ActionListener, WindowListener {
    protected JFrame owner = null;
    protected JList fileList = null;
    protected JButton addButton = null, removeButton = null, convertButton = null, exitButton = null, browseButton = null;
    protected JComboBox outputComboBox = null, inputComboBox = null;
    protected JCheckBox autoDetect = null;
    protected JTextField outputDir = null;
    protected JFileChooser addFiles = null, selectDir = null;
    protected LinkedList fileListData = new LinkedList();
    protected JRadioButton netButton = null, dataButton = null;

    protected TableSet converterTable = null, dataConverterTable = null;
    protected Hashtable dataDesc2Format = null;
    protected List converterDescriptions = null, dataDescriptions = null;
    protected List converterExtensions = null, dataExtensions = null;
    protected boolean networkMode = true;
    protected JFileChooser netAddFiles = null, dataAddFiles = null;

    public ConverterGUI() {
        this(null);
    }

    public ConverterGUI(JFrame newOwner) {
        super();
        owner = newOwner;
        init();
    }

    private void init() {
        Settings.setLanguage(Locale.ENGLISH, true);
        setSize(new Dimension(300, 500));
        GUIUtil.centerToScreen(this);

        JPanel panel = new JPanel();

        setTitle("Batch Converter");
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;

        JPanel filePanel = new JPanel();
        filePanel.setBorder(new TitledBorder(new EtchedBorder(), "File to Convert"));
        filePanel.setLayout(new GridBagLayout());
        fileList = new JList();
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileList.setValueIsAdjusting(false);
        filePanel.add(new JScrollPane(fileList), constraint);
        panel.add(filePanel, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        filePanel.add(addButton, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 1;
        constraint.gridy = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;

        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        filePanel.add(removeButton, constraint);

        JPanel dataOrNetPanel = new JPanel();
        dataOrNetPanel.setBorder(new TitledBorder(new EtchedBorder(), "What to convert"));
        dataOrNetPanel.setLayout(new GridBagLayout());
        netButton = new JRadioButton("Network files");
        netButton.addActionListener(this);
        netButton.setSelected(true);
        dataButton = new JRadioButton("Data files");
        dataButton.addActionListener(this);
        dataButton.setSelected(false);
        ButtonGroup group = new ButtonGroup();
        group.add(netButton);
        group.add(dataButton);
        dataOrNetPanel.add(netButton);
        dataOrNetPanel.add(dataButton);

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Input"));
        inputPanel.setLayout(new GridBagLayout());
        autoDetect = new JCheckBox("Auto detect input format", true);
        autoDetect.addActionListener(this);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        inputPanel.add(autoDetect, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        inputPanel.add(new JLabel("Force input format to:"), constraint);

        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Output"));
        outputPanel.setLayout(new GridBagLayout());

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        outputPanel.add(new JLabel("Set output format to:"), constraint);

        precache();
        List ll = getNetworkFormatList();
        selectDir = FileDialogFactory.create("Select Directory");

        inputComboBox = new JComboBox(ll.toArray());
        inputComboBox.setEnabled(false);
        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        inputPanel.add(inputComboBox, constraint);

        outputComboBox = new JComboBox(ll.toArray());
        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        outputPanel.add(outputComboBox, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        outputPanel.add(new JLabel("Output directory:"), constraint);

        File f = new File(".");
        outputDir = new JTextField(f.getAbsolutePath());
        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        outputPanel.add(outputDir, constraint);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        constraint = new GridBagConstraints();
        constraint.gridx = 1;
        constraint.gridy = 3;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 0.8;
        constraint.weighty = 1.0;
        outputPanel.add(browseButton, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 1;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(dataOrNetPanel, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(inputPanel, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 3;
        constraint.gridwidth = 2;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        panel.add(outputPanel, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = 4;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;

        convertButton = new JButton("Convert");
        convertButton.addActionListener(this);
        panel.add(convertButton, constraint);

        constraint = new GridBagConstraints();
        constraint.gridx = 1;
        constraint.gridy = 4;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        panel.add(exitButton, constraint);

        setContentPane(panel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }

    private void precache() {
        converterTable = ConverterFactory.loadConfig();
        dataConverterTable = Settings.getDataConverterTable();
        if (converterTable == null || dataConverterTable == null) {
            JOptionPane.showOptionDialog(this, "Error! Cannot open converter configuration file!", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, "OK");
        }

        LinkedList formatExtensions = new LinkedList();
        LinkedList ll = new LinkedList();
        if (converterTable != null) {
            for (Enumeration e = converterTable.keys(); e.hasMoreElements();) {
                String ext = (String) e.nextElement();
                HashSet set = (HashSet) converterTable.get(ext);
                formatExtensions.add("*." + ext); // $NON-NLS-1$
                for (Iterator i = set.iterator(); i.hasNext();) {
                    ConverterData data = (ConverterData) i.next();
                    ll.add(data.getDescription());
                }
            }
        }
        converterExtensions = formatExtensions;
        converterDescriptions = ll;

        netAddFiles = FileDialogFactory.create(converterExtensions, "Bayesian Network File Formats");
        netAddFiles.setCurrentDirectory(new File(".")); // $NON-NLS-1$
        netAddFiles.setSize(450, 250);

        formatExtensions = new LinkedList();
        ll = new LinkedList();
        dataDesc2Format = new Hashtable();
        if (dataConverterTable != null) {
            for (Enumeration e = dataConverterTable.keys(); e.hasMoreElements();) {
                String ext = (String) e.nextElement();
                HashSet set = (HashSet) dataConverterTable.get(ext);
                formatExtensions.add("*." + ext); // $NON-NLS-1$
                for (Iterator i = set.iterator(); i.hasNext();) {
                    ConverterData data = (ConverterData) i.next();
                    dataDesc2Format.put(data.getDescription(), ext);
                    ll.add(data.getDescription());
                }
            }
        }
        dataExtensions = formatExtensions;
        dataDescriptions = ll;

        dataAddFiles = FileDialogFactory.create(dataExtensions, "Bayesian Data File Formats");
        dataAddFiles.setCurrentDirectory(new File(".")); // $NON-NLS-1$
        dataAddFiles.setSize(450, 250);
    }

    private List getNetworkFormatList() {
        addFiles = netAddFiles;
        return converterDescriptions;
    }

    private List getDataFormatList() {
        addFiles = dataAddFiles;
        return dataDescriptions;
    }

    private Dimension center(Dimension src, Dimension dest) {
        if (src.height > dest.height) src.height = dest.height;
        if (src.width > dest.width) src.width = dest.width;
        return new Dimension((dest.width - src.width) / 2, (dest.height - src.height) / 2);
    }

    protected void selectedQuit() {
        if (owner == null) {
            int answer = JOptionPane.showOptionDialog(this, "Are you sure you want to quit?", "Quitting", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Yes", "No", "Cancel"}, "No");
            if (answer != 0) return;
            //            System.exit(0);
        }
        setVisible(false);
        dispose();
    }

    protected void refreshList() {
        fileList.setValueIsAdjusting(true);
        fileList.setListData(fileListData.toArray());
        fileList.setValueIsAdjusting(false);
        fileList.repaint();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == exitButton) {
            selectedQuit();
        } else if (source == autoDetect) {
            inputComboBox.setEnabled(!autoDetect.isSelected());
        } else if (source == addButton) {
            File[] chosen = FileDialogFactory.displayMulti(addFiles, this, "Add files to convert");
            if (chosen == null) return;
            int max = chosen.length;
            for (int i = 0; i < max; i++) {
                fileListData.add(chosen[i].getAbsolutePath());
            }
            refreshList();
        } else if (source == removeButton) {
            Object[] data = fileList.getSelectedValues();
            if (data == null || data.length == 0) return;
            fileListData.removeAll(Arrays.asList(data));
            refreshList();
        } else if (source == browseButton) {
            String curDir = outputDir.getText();
            if (curDir == null || curDir.equals("")) curDir = ".";
            File curDirFile = new File(curDir);
            if (curDirFile.isFile()) {
                try {
                    curDirFile = curDirFile.getParentFile();
                } catch (Exception e) {
                    curDirFile = new File(".");
                }
            }
            selectDir.setCurrentDirectory(curDirFile);
            File chosen = FileDialogFactory.displayDir(selectDir, this, "Select");
            if (chosen != null) {
                outputDir.setText(chosen.getAbsolutePath());
            }
        } else if (source == netButton) {
            if (networkMode) return;
            networkMode = true;
            fileListData.clear();
            refreshList();
            netButton.setSelected(true);
            List ll = getNetworkFormatList();
            inputComboBox.removeAllItems();
            outputComboBox.removeAllItems();

            for (Iterator i = ll.iterator(); i.hasNext();) {
                Object item = i.next();
                inputComboBox.addItem(item);
                outputComboBox.addItem(item);
            }
        } else if (source == dataButton) {
            if (!networkMode) return;
            networkMode = false;
            fileListData.clear();
            refreshList();
            dataButton.setSelected(true);
            List ll = getDataFormatList();
            inputComboBox.removeAllItems();
            outputComboBox.removeAllItems();

            for (Iterator i = ll.iterator(); i.hasNext();) {
                Object item = i.next();
                inputComboBox.addItem(item);
                outputComboBox.addItem(item);
            }
        } else if (source == convertButton) {
            int max = fileList.getModel().getSize();
            if (max == 0) {
                JOptionPane.showOptionDialog(this, "No files to convert!", "Check", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
                return;
            }

            String outputFormat = (String) outputComboBox.getSelectedItem();
            String inputFormat = null;

            if (!autoDetect.isSelected()) {
                inputFormat = (String) inputComboBox.getSelectedItem();
            }
            String outputDir = this.outputDir.getText();
            if (outputDir != null) outputDir = outputDir.trim();
            if (outputDir.equals("")) outputDir = null;

            boolean allSuccess = true;

            if (networkMode) {
                for (int i = 0; i < max; i++) {
                    String filename = fileList.getModel().getElementAt(i).toString().trim();
                    try {
                        ConverterFactory.convert(filename, inputFormat, outputFormat, outputDir);
                    } catch (Exception e) {
                        allSuccess = false;
                        e.printStackTrace();
                    }
                }
            } else {
                outputFormat = (String) dataDesc2Format.get(outputFormat);
                assert (outputFormat != null);
                if (inputFormat != null) {
                    inputFormat = (String) dataDesc2Format.get(inputFormat);
                    assert (inputFormat != null);
                }
                for (int i = 0; i < max; i++) {
                    String filename = fileList.getModel().getElementAt(i).toString().trim();
                    try {
                        Database t = Database.load(filename, inputFormat);
                        int pathIndex = filename.lastIndexOf(File.separator);
                        if (pathIndex != -1) {
                            filename = filename.substring(pathIndex + 1); // cut of the path
                        }
                        String outFile = outputDir + File.separator + filename.substring(0, filename.lastIndexOf(".")) + "." + outputFormat; // $NON-NLS-1$ // $NON-NLS-2$
                        t.save(outFile, outputFormat);
                    } catch (Exception e) {
                        allSuccess = false;
                        e.printStackTrace();
                    }
                }
            }
            if (allSuccess) {
                JOptionPane.showOptionDialog(this, "Files are converted!", "Success", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"OK"}, "OK");
            } else {
                JOptionPane.showOptionDialog(this, "Files are converted, but some have errors!", "Success", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
            }
        }
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(WindowEvent)
     */
    public void windowClosing(WindowEvent evt) {
        Object source = evt.getSource();
        if (source == this) {
            selectedQuit();
        }
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
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
        ConverterGUI gui = new ConverterGUI();
        gui.setVisible(true);
    }

}
