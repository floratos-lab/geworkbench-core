package edu.ksu.cis.bnj.gui.components;

import edu.ksu.cis.bnj.bbn.BBNGraph;
import edu.ksu.cis.bnj.bbn.converter.ConverterFactory;
import edu.ksu.cis.bnj.i18n.Messages;
import edu.ksu.cis.kdd.util.Settings;
import edu.ksu.cis.kdd.util.gui.DialogFactory;
import salvo.jesus.graph.visual.VisualVertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author Roby Joehanes
 */
public class BNJMainPanel extends JPanel implements ActionListener {
    protected BBNGraphPanel graphPanel = null;
    protected BBNTree treePanel = null;
    protected BNJFileDialogFactory fcFactory = null;
    protected JSplitPane divider = null, horizDivider = null;
    protected JPanel mainPanel = null;
    protected NodePropertiesPanel propertiesPanel = null;
    protected int defaultDividerSize = 4, defaultDividerLoc = 200;
    protected boolean propertiesVisible = false;
    protected boolean graphEditable = true;

    /**
     * The path name of the currently opened file *
     */
    protected File openedFile = null;
    protected NodeManager nodeManager = null;
    protected Hashtable settings = null;

    public BNJMainPanel() {
        super();
        init();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand().toLowerCase();
        //System.out.println(cmd);  //debug
        if (cmd.equals("file new")) { //$NON-NLS-1$
            newGraph();
        } else if (cmd.equals("file open")) { //$NON-NLS-1$
            open();
        } else if (cmd.equals("file save")) { //$NON-NLS-1$
            save();
        } else if (cmd.equals("file save as")) { //$NON-NLS-1$
            saveAs();
        } else if (cmd.equals("select")) {
            graphPanel.changeToNormalState();
        } else if (cmd.equals("add node")) {
            if (graphEditable) graphPanel.changeToChanceVarState();
        } else if (cmd.equals("add decision")) {
            if (graphEditable) graphPanel.changeToDecisionVarState();
        } else if (cmd.equals("add utility")) {
            if (graphEditable) graphPanel.changeToUtilityVarState();
        } else if (cmd.equals("add edge")) {
            if (graphEditable) graphPanel.changeToEdgeState();
        } else if (cmd.equals("delete")) {
            if (graphEditable) nodeManager.deleteSelectedNodes();
        } else if (cmd.equals("auto layout")) { //$NON-NLS-1$
            autoLayout();
        } else if (cmd.equals("delete node")) { // $NON-NLS-1$
            nodeManager.deleteSelectedNodes();
        } else if (cmd.equals("delete edge")) { // $NON-NLS-1$
            try {
                nodeManager.deleteSelectedEdge();
            } catch (Exception ex) {
                // Not sure why this should fire an exception, but...
                DialogFactory.getOKDialog(null, DialogFactory.ERROR, "Error!", "Cannot delete this edge!");
            }
        } else if (cmd.equals("node properties")) { // $NON-NLS-1$
            Set selectedNodes = nodeManager.getSelectedNodes();
            if (!propertiesVisible && selectedNodes != null && selectedNodes.size() == 1) {
                VisualVertex vVertex = (VisualVertex) selectedNodes.iterator().next();
                propertiesPanel.setNode(vVertex);
                showPropertiesPanel();
            } else {
                hidePropertiesPanel();
            }
        }
    }

    public void hidePropertiesPanel() {
        if (!propertiesVisible) return; // already hidden
        int curLoc = horizDivider.getDividerLocation();
        if (curLoc > 0) defaultDividerLoc = curLoc;
        propertiesPanel.setVisible(false);
        horizDivider.setDividerSize(0);
        horizDivider.setDividerLocation(0);
        propertiesVisible = false;
    }

    public void showPropertiesPanel() {
        if (propertiesVisible) return; // already visible
        propertiesPanel.setVisible(true);
        horizDivider.setDividerSize(defaultDividerSize);
        horizDivider.setDividerLocation(defaultDividerLoc);
        propertiesVisible = true;
    }

    public BBNGraph getGraph() {
        return graphPanel.getGraph();
    }

    protected void buildMenu(JMenu menu) {
        int maxj = menu.getItemCount();
        for (int j = 0; j < maxj; j++) {
            JMenuItem item = menu.getItem(j);
            if (item instanceof JMenu)
                buildMenu((JMenu) item);
            else if (item != null) item.addActionListener(this);
        }
    }

    protected JPopupMenu initPopup(String name) {
        JPopupMenu popup = (JPopupMenu) settings.get(name); // $NON-NLS-1$
        MenuElement[] menuElements = popup.getSubElements();
        int max = menuElements.length;
        for (int i = 0; i < max; i++) {
            if (menuElements[i] instanceof JMenu) {
                buildMenu((JMenu) menuElements[i]);
            } else {
                if (menuElements[i] instanceof JMenuItem) {
                    ((JMenuItem) menuElements[i]).addActionListener(this);
                }
            }
        }
        return popup;
    }

    protected void init() {
        setLayout(new BorderLayout());
        settings = Settings.getWindowSettings("MAIN"); //$NON-NLS-1$

        fcFactory = new BNJFileDialogFactory(this);

        graphPanel = new BBNGraphPanel(this);
        nodeManager = graphPanel.getNodeManager();

        treePanel = new BBNTree(this, nodeManager);
        propertiesPanel = new NodePropertiesPanel(nodeManager);

        horizDivider = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        horizDivider.setLeftComponent(propertiesPanel);
        horizDivider.setRightComponent(graphPanel);
        propertiesPanel.setVisible(false);
        defaultDividerSize = horizDivider.getDividerSize();
        horizDivider.setDividerSize(0);
        horizDivider.setDividerLocation(0);

        divider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        divider.setOneTouchExpandable(true);
        divider.setDividerLocation(150);
        divider.setLeftComponent(new JScrollPane(treePanel));
        divider.setRightComponent(horizDivider);
        setGraph(new BBNGraph());
        add(divider, "Center");  //$NON-NLS-1$
        initPopup("NodeDeleteWithPropertiesPopup"); // $NON-NLS-1$
        initPopup("NodeDeletePopup"); // $NON-NLS-1$
        initPopup("EdgeDeletePopup"); // $NON-NLS-1$
        initPopup("NormalPopup"); // $NON-NLS-1$
    }

    /**
     * Returns the modified.
     *
     * @return boolean
     */
    public boolean isModified() {
        return graphPanel.isModified();
    }

    protected boolean open(File file) {
        try {
            BBNGraph graph = BBNGraph.load(file.getAbsolutePath());
            setGraph(graph);
            return true;
        } catch (Exception e) {
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, Messages.getString("Error.FileNotExist"), // $NON-NLS-1$
                    Messages.getString("Error.CantOpenFile")); // $NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }

    public void setGraph(BBNGraph g) {
        nodeManager.setGraph(g);
        propertiesPanel.setNode(null);
    }

    protected boolean save(File file) {
        try {
            BBNGraph bbnGraph = (BBNGraph) graphPanel.getGraph();
            String filename = file.getAbsolutePath();
            int i = filename.lastIndexOf('.');
            String ext = null;

            if (i > 0 && i < filename.length() - 1) {
                ext = filename.substring(i + 1).toLowerCase();
            }
            if (ConverterFactory.isKnownFormat(ext))
                bbnGraph.save(filename, ext);
            else
                bbnGraph.save(filename);
            return true;
        } catch (Exception e) {
            DialogFactory.getOKDialog(this, DialogFactory.ERROR, Messages.getString("Error.FileNotExist"), // $NON-NLS-1$
                    Messages.getString("Error.CantOpenFile")); // $NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }

    public void autoLayout() {
        graphPanel.autoLayout();
    }

    public void newGraph() {
        if (isModified()) {
            int result = DialogFactory.getYesNoDialog(this, DialogFactory.WARNING, Messages.getString("Dialog.OpenNewGraph"), // $NON-NLS-1$
                    Messages.getString("Dialog.DiscardChanges")); // $NON-NLS-1$
            if (result == 1) return;
        }
        openedFile = null;
        setGraph(new BBNGraph());
    }

    public void open() {
        if (isModified()) {
            int result = DialogFactory.getYesNoDialog(this, DialogFactory.WARNING, Messages.getString("Dialog.OpenNewGraph"), // $NON-NLS-1$
                    Messages.getString("Dialog.DiscardChanges")); // $NON-NLS-1$
            if (result == 1) return;
        }

        File fc = fcFactory.openNetFiles();
        if (fc == null) return;
        openedFile = fc;
        open(fc);
    }

    public void save() {
        if (openedFile == null)
            saveAs();
        else if (save(openedFile)) graphPanel.setModified(false);
    }

    public void saveAs() {
        File fc = fcFactory.saveNetFiles();
        if (fc == null) return;
        openedFile = fc;
        if (save(fc)) graphPanel.setModified(false);
    }

    /**
     * Whether or not the user are allowed to edit the graph (by default = allowed)
     *
     * @param b
     */
    public void setGraphEditable(boolean b) {
        graphEditable = b;
        if (!graphEditable) graphPanel.changeToNormalState(); // if graph isn't editable, only normal state is allowed
        propertiesPanel.setGraphEditable(b);
        treePanel.setGraphEditable(b);
    }

    public boolean isGraphEditable() {
        return graphEditable;
    }
}
