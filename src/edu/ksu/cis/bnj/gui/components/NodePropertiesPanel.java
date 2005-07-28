/*
 * Created on Oct 22, 2003
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
import edu.ksu.cis.bnj.bbn.BBNDiscreteValue;
import edu.ksu.cis.bnj.bbn.BBNNode;
import edu.ksu.cis.bnj.gui.event.*;
import edu.ksu.cis.kdd.util.gui.GUIUtil;
import salvo.jesus.graph.visual.VisualVertex;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author Roby Joehanes
 */
public class NodePropertiesPanel extends JPanel implements ActionListener, DocumentListener, NodeManagerListener {

    protected static final String noEvidenceString = "<< No Evidence >>"; // Special value

    protected NodeManager manager;
    protected VisualVertex shownNode = null;

    protected JPanel[] blankPanel = {new JPanel(), new JPanel()}; // Repeat as many as the number of tabs available
    protected JTabbedPane tabPane;
    protected JPanel labelValuePanel, cpfPanel, labelValueTab, cpfTab;
    protected JComboBox evidenceCombo;
    protected JList stateList;
    protected DefaultListModel stateListModel;
    protected JButton addStateButton, removeStateButton;
    protected JTextField nameField, labelField, addStateField;
    protected Document nameFieldDocument, labelFieldDocument;
    protected boolean graphEditable = true;

    // a li'l hack
    private boolean ignoreEvent = false;

    public NodePropertiesPanel(NodeManager nm) {
        super();
        manager = nm;
        init();
    }

    protected void init() {
        manager.addListener(this);
        setLayout(new GridBagLayout());
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new GridBagLayout());
        JScrollPane mainScroll = new JScrollPane(mainPane);
        JScrollBar sb = mainScroll.getVerticalScrollBar();
        sb.setUnitIncrement(16); // Faster scroll, please
        sb.setBlockIncrement(16);
        sb = mainScroll.getHorizontalScrollBar();
        sb.setUnitIncrement(16); // Faster scroll, please
        sb.setBlockIncrement(16);

        tabPane = new JTabbedPane();
        GUIUtil.gbAdd(mainPane, tabPane, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(this, mainScroll, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        //
        // Label & Value panel
        //
        labelValuePanel = new JPanel();
        labelValuePanel.setLayout(new GridBagLayout());
        labelValueTab = new JPanel();
        labelValueTab.setLayout(new GridBagLayout());
        labelValueTab.setBorder(new EtchedBorder());
        tabPane.addTab("Values", labelValueTab);

        nameField = new JTextField(30);
        nameFieldDocument = nameField.getDocument();
        nameFieldDocument.addDocumentListener(this);
        labelField = new JTextField(30);
        labelFieldDocument = labelField.getDocument();
        labelFieldDocument.addDocumentListener(this);
        addStateField = new JTextField(16);
        addStateField.addActionListener(this);
        stateList = new JList();
        stateList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        stateListModel = new DefaultListModel();
        stateList.setModel(stateListModel);
        evidenceCombo = new JComboBox();
        evidenceCombo.addActionListener(this);
        JScrollPane stateScroll = new JScrollPane(stateList);
        Dimension dim = new Dimension(140, 80);
        stateScroll.setPreferredSize(dim);
        stateScroll.setMaximumSize(dim);
        dim = new Dimension(140, 20);
        addStateField.setPreferredSize(dim);
        addStateField.setMaximumSize(dim);
        dim = new Dimension(80, 20);
        addStateButton = new JButton("Add");
        addStateButton.addActionListener(this);
        addStateButton.setPreferredSize(dim);
        addStateButton.setMaximumSize(dim);
        removeStateButton = new JButton("Remove");
        removeStateButton.addActionListener(this);
        removeStateButton.setPreferredSize(dim);
        removeStateButton.setMaximumSize(dim);
        dim = new Dimension(140, 20);
        evidenceCombo.setPreferredSize(dim);
        evidenceCombo.setMaximumSize(dim);

        GUIUtil.gbAdd(labelValuePanel, new JLabel("Name"), 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.2, 1.0);
        GUIUtil.gbAdd(labelValuePanel, nameField, 1, 0, 4, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1.0, 1.0);

        GUIUtil.gbAdd(labelValuePanel, new JLabel("Label"), 0, 1, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.2, 1.0);
        GUIUtil.gbAdd(labelValuePanel, labelField, 1, 1, 4, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 1.0, 1.0);

        GUIUtil.gbAdd(labelValuePanel, new JLabel("States"), 0, 2, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.2, 1.0);
        GUIUtil.gbAdd(labelValuePanel, addStateField, 1, 2, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.05, 1.0);
        GUIUtil.gbAdd(labelValuePanel, addStateButton, 2, 2, 1, 1, 4, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 1.0, 1.0);
        GUIUtil.gbAdd(labelValuePanel, stateScroll, 1, 3, 1, 5, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.05, 1.0);
        GUIUtil.gbAdd(labelValuePanel, removeStateButton, 2, 3, 1, 1, 4, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 1.0, 1.0);

        GUIUtil.gbAdd(labelValuePanel, new JLabel("Evidence"), 0, 8, 1, 1, 0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.2, 1.0);
        GUIUtil.gbAdd(labelValuePanel, evidenceCombo, 1, 8, 1, 1, 2, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.1, 1.0);
        GUIUtil.gbAdd(labelValuePanel, Box.createGlue(), 2, 8, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 0.1, 1.0);

        GUIUtil.gbAdd(labelValuePanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 10.0, 10.0);
        GUIUtil.gbAdd(labelValueTab, labelValuePanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);

        //
        // CPT panel
        //
        cpfPanel = new JPanel();
        cpfPanel.setLayout(new GridBagLayout());
        cpfTab = new JPanel();
        cpfTab.setLayout(new GridBagLayout());
        cpfTab.setBorder(new EtchedBorder());
        tabPane.addTab("CPT", cpfTab);
        GUIUtil.gbAdd(cpfPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        GUIUtil.gbAdd(cpfTab, cpfPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
    }

    public void setNode(VisualVertex node) {
        if (shownNode == node) return;
        shownNode = node;
        if (shownNode == null) {
            cpfTab.removeAll();
            GUIUtil.gbAdd(cpfTab, blankPanel[0], 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
            labelValueTab.removeAll();
            GUIUtil.gbAdd(labelValueTab, blankPanel[1], 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
            tabPane.setEnabled(false);
            return;
        } else {
            cpfTab.removeAll();
            GUIUtil.gbAdd(cpfTab, cpfPanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
            labelValueTab.removeAll();
            GUIUtil.gbAdd(labelValueTab, labelValuePanel, 0, 0, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
            tabPane.setEnabled(true);
        }
        addStateField.setText(""); // $NON-NLS-1$
        BBNNode bbnNode = (BBNNode) node.getVertex();
        ignoreEvent = true;
        labelField.setText(node.getLabel());
        nameField.setText(bbnNode.getLabel());
        ignoreEvent = false;

        if (!bbnNode.isUtility()) {
            BBNDiscreteValue dval = (BBNDiscreteValue) bbnNode.getValues();
            stateListModel.removeAllElements();
            Object evidence = bbnNode.getEvidenceValue();
            evidenceCombo.removeAllItems(); // Somehow this resets the evidence to null... strange -- RJ
            evidenceCombo.addItem(noEvidenceString); // Special value
            if (dval != null) {
                for (Iterator i = dval.iterator(); i.hasNext();) {
                    Object val = i.next();
                    stateListModel.addElement(val);
                    evidenceCombo.addItem(val);
                }
            }
            if (evidence != null) {
                evidenceCombo.setSelectedItem(evidence);
            }
            stateList.setModel(stateListModel);
            stateList.revalidate();
            stateList.repaint();

            stateList.setEnabled(true);
            addStateButton.setEnabled(graphEditable);
            addStateField.setEnabled(graphEditable);
            removeStateButton.setEnabled(graphEditable);
            evidenceCombo.setEnabled(graphEditable);
        } else {
            stateListModel.removeAllElements();
            evidenceCombo.addItem(noEvidenceString); // Special value

            stateList.setEnabled(false);
            addStateButton.setEnabled(false);
            addStateField.setEnabled(false);
            removeStateButton.setEnabled(false);
            evidenceCombo.setEnabled(false);
        }
        prepareCPFDisplay();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (shownNode == null) return; // must be a bug
        if (!graphEditable) return; // if the graph isn't editable, deny access
        BBNNode bbnNode = (BBNNode) shownNode.getVertex();
        BBNDiscreteValue dval = (BBNDiscreteValue) bbnNode.getValues();
        NodePropertiesEvent event = null;
        if (source == addStateButton || source == addStateField) {
            String value = addStateField.getText();
            if (value == null) return;
            value = value.trim();
            if (value.length() == 0) return;
            addStateField.setText(""); // $NON-NLS-1$

            if (dval == null) {
                dval = new BBNDiscreteValue();
                bbnNode.setValues(dval);
            }
            dval.add(value);
            evidenceCombo.addItem(value);
            stateListModel.addElement(value);
            stateList.setModel(stateListModel);
            stateList.revalidate();
            stateList.repaint();
            updateCPF();
            event = new NodePropertiesEvent(this, NodePropertiesEvent.STATES_CHANGED, shownNode);
        } else if (source == removeStateButton) {
            if (dval == null) return;
            Object[] values = stateList.getSelectedValues();
            if (values == null) return;

            int max = values.length;
            for (int i = 0; i < max; i++) {
                stateListModel.removeElement(values[i]);
                dval.remove(values[i]);
                evidenceCombo.removeItem(values[i]);
            }
            stateList.setModel(stateListModel);
            stateList.revalidate();
            stateList.repaint();
            updateCPF();
            event = new NodePropertiesEvent(this, NodePropertiesEvent.STATES_CHANGED, shownNode);
        } else if (source == evidenceCombo) {
            if (bbnNode.isUtility()) return; // We can't set an evidence in utility nodes
            Object evidence = evidenceCombo.getSelectedItem();
            if (evidence == noEvidenceString) {
                bbnNode.setEvidenceValue(null);
            } else {
                bbnNode.setEvidenceValue(evidence);
            }
            event = new NodePropertiesEvent(this, NodePropertiesEvent.EVIDENCE_CHANGED, shownNode);
        }

        if (event != null) manager.fireNodePropertiesEvent(event);
    }

    protected void updateCPF() {
        // Update the CPF of the current node and its children
        // For now, let's reset everything
        BBNNode node = (BBNNode) shownNode.getVertex();
        node.resetCPF();
        List children = node.getChildren();
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext();) {
                BBNNode child = (BBNNode) i.next();
                child.resetCPF();
            }
        }
        prepareCPFDisplay(); // Update the cpf display
    }

    protected void prepareCPFDisplay() {
        BBNNode node = (BBNNode) shownNode.getVertex();
        if (node.isDecision()) {
            setInvalidCPFMessage("Decision node cannot have a CPT!");
            return;
        }
        LinkedList parents = new LinkedList();
        parents.addAll(node.getParents());
        if (node.isUtility()) {
            // Must replace current node with one of the parents because
            // utility nodes can't have values.
            if (parents == null || parents.size() == 0) {
                setInvalidCPFMessage("Utility nodes cannot be a root node!");
                return;
            }
            node = (BBNNode) parents.remove(0);
        }

        // TODO: When the new optimization scheme takes place, this section
        // will need a major revamp -- RJ
        BBNDiscreteValue nodeValue = (BBNDiscreteValue) node.getValues();
        String nodeName = node.getLabel();
        cpfPanel.removeAll();
        cpfPanel.repaint();
        if (nodeValue == null) return; // Has no value, so, don't bother...
        LinkedList eligibleParents = new LinkedList();
        LinkedList headerWidth = new LinkedList();
        int row = nodeValue.getArity();
        int headerRows = 0;
        int col = 1;

        for (Iterator i = parents.iterator(); i.hasNext();) {
            BBNNode parent = (BBNNode) i.next();
            BBNDiscreteValue parentValue = (BBNDiscreteValue) parent.getValues();
            if (!parent.isUtility() && parentValue != null) {
                headerWidth.add(new Integer(col));
                col *= parentValue.getArity();
                eligibleParents.add(parent);
                row++;
                headerRows++;
            }
        }
        col++; // extra column for the leftmost column (to print values)

        BBNCPF cpf = node.getCPF();

        // Paint up the columns
        int index = headerRows;
        for (Iterator i = nodeValue.iterator(); i.hasNext(); index++) {
            JLabel label = new JLabel(i.next().toString());
            label.setBorder(new EtchedBorder());
            GUIUtil.gbAdd(cpfPanel, label, 0, index, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
        }

        if (headerRows > 0) {
            // Put the headers first
            index = headerRows - 1;
            for (Iterator i = eligibleParents.iterator(); i.hasNext(); index--) {
                BBNNode parent = (BBNNode) i.next();
                BBNDiscreteValue parentValue = (BBNDiscreteValue) parent.getValues();
                JLabel label = new JLabel(parent.getLabel());
                label.setBorder(new EtchedBorder());
                GUIUtil.gbAdd(cpfPanel, label, 0, index, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 1.0, 1.0);
                int valueWidth = ((Integer) headerWidth.get(headerRows - index - 1)).intValue();
                int arity = parentValue.getArity();
                int numTimes = (col - 1) / (arity * valueWidth);
                int horizIndex = 1;
                for (int j = 0; j < numTimes; j++) {
                    for (Iterator k = parentValue.iterator(); k.hasNext(); horizIndex += valueWidth) {
                        label = new JLabel(k.next().toString());
                        label.setBorder(new EtchedBorder());
                        label.setHorizontalAlignment(JLabel.CENTER);
                        GUIUtil.gbAdd(cpfPanel, label, horizIndex, index, valueWidth, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);
                    }
                }
            }
        }

        // Now, put the textfields
        Hashtable query = new Hashtable();
        index = headerRows;
        for (Iterator i = nodeValue.iterator(); i.hasNext(); index++) {
            Object value = i.next();
            query.put(nodeName, value);
            if (headerRows > 0) {
                layoutCPTFields(eligibleParents, headerWidth, 1, index, query, cpf);
            } else {
                CPFEntry entry = new CPFEntry(cpf, query);
                entry.setEditable(graphEditable);
                GUIUtil.gbAdd(cpfPanel, entry, 1, index, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);
            }
        }

        GUIUtil.gbAdd(cpfPanel, Box.createGlue(), 50, 50, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST, 10.0, 10.0);
    }

    protected void layoutCPTFields(LinkedList parents, LinkedList widths, int column, int row, Hashtable query, BBNCPF cpf) {
        BBNNode parent = (BBNNode) parents.removeFirst();
        Integer widthInt = (Integer) widths.removeFirst();
        BBNDiscreteValue parentValue = (BBNDiscreteValue) parent.getValues();
        int width = widthInt.intValue();

        boolean isLast = parents.size() == 0;
        for (Iterator i = parentValue.iterator(); i.hasNext();) {
            Object value = i.next();
            query.put(parent.getLabel(), value);
            if (isLast) {
                CPFEntry entry = new CPFEntry(cpf, query);
                entry.setEditable(graphEditable);
                GUIUtil.gbAdd(cpfPanel, entry, column, row, 1, 1, 0, 0, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);
            } else {
                layoutCPTFields(parents, widths, column, row, query, cpf);
            }
            column += width;
        }
        widths.addFirst(widthInt);
        parents.addFirst(parent);
    }

    protected void setInvalidCPFMessage(String message) {
        cpfPanel.removeAll();
        cpfPanel.add(new JLabel(message));
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent evt) {
        if (ignoreEvent) return;
        Object source = evt.getDocument();
        BBNNode bbnNode = (BBNNode) shownNode.getVertex();
        NodePropertiesEvent event = null;

        if (source == nameFieldDocument) {
            String text = nameField.getText();
            //System.out.println("Rename to " + text);
            bbnNode.setName(text);
            event = new NodePropertiesEvent(this, NodePropertiesEvent.NAME_CHANGED, shownNode);
        } else if (source == labelFieldDocument) {
            String text = labelField.getText();
            //System.out.println("Relabel to " + text);
            shownNode.setLabel(text);
            shownNode.getVisualGraph().repaint();
            event = new NodePropertiesEvent(this, NodePropertiesEvent.LABEL_CHANGED, shownNode);
        }
        if (event != null) manager.fireNodePropertiesEvent(event);
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

    /**
     * @see edu.ksu.cis.bnj.gui.components.NodeManagerListener#selectedNodesChanged(edu.ksu.cis.bnj.gui.components.NodeSelectionEvent)
     */
    public void selectedNodesChanged(NodeSelectionEvent event) {
        if (!isVisible()) return; // don't bother if the properties panel isn't visible
        Set selected = event.getSelection();
        if (selected == null || selected.size() != 1) {
            setNode(null);
        } else {
            setNode((VisualVertex) selected.iterator().next());
        }
    }

    /**
     * @see edu.ksu.cis.bnj.gui.components.NodeManagerListener#selectedNodesDeleted(edu.ksu.cis.bnj.gui.components.NodeDeletionEvent)
     */
    public void selectedNodesDeleted(NodeDeletedEvent event) {
    }

    /**
     * @see edu.ksu.cis.bnj.gui.components.NodeManagerListener#nodePropertyChanged(salvo.jesus.graph.visual.VisualVertex)
     */
    public void nodePropertyChanged(NodePropertiesEvent evt) {
        if (evt.getSource() == this) return; // Avoid loopback
        if (evt.getNode() != shownNode) return; // If it's not about the node shown here, we're not interested
        switch (evt.getType()) {
            case NodePropertiesEvent.EVIDENCE_CHANGED: // Maybe the evidence has been changed from the tree
                BBNNode node = (BBNNode) shownNode.getVertex();
                Object evidence = node.getEvidenceValue();
                if (evidence != null) {
                    evidenceCombo.setSelectedItem(evidence);
                } else {
                    evidenceCombo.setSelectedIndex(0); // No Evidence
                }
                break;
        }
    }

    /**
     * @see edu.ksu.cis.bnj.gui.components.NodeManagerListener#graphChanged(salvo.jesus.graph.visual.VisualGraph)
     */
    public void graphChanged(GraphChangedEvent event) {
    }

    /**
     * @see edu.ksu.cis.bnj.gui.components.NodeManagerListener#nodeAdded(salvo.jesus.graph.visual.VisualVertex)
     */
    public void nodeAdded(NodeAddedEvent event) {
    }

    public void edgeAdded(EdgeAddedEvent event) {
    }

    public void edgeRemoved(EdgeDeletedEvent event) {
    }

    /**
     * Whether or not the user are allowed to edit the properties (by default = allowed)
     *
     * @param b
     */
    public void setGraphEditable(boolean b) {
        graphEditable = b;
        addStateButton.setEnabled(b);
        addStateField.setEnabled(b);
        removeStateButton.setEnabled(b);
        evidenceCombo.setEnabled(b);
        labelField.setEditable(b);
        nameField.setEditable(b);
    }

    public boolean isGraphEditable() {
        return graphEditable;
    }
}
