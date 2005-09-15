package org.geworkbench.util.session.dialog;

import org.geworkbench.util.session.DisplayDialog;
import org.geworkbench.util.session.SessionTableModel;
import org.geworkbench.util.session.SessionsViewController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class SessionsViewDialog extends JDialog {
    //controls all of the action for the UI
    SessionsViewController controller = null;
    //pop up menu on the table
    private JPopupMenu sessionMenu = new JPopupMenu();
    JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    Border border1;
    TitledBorder titledBorder1;
    Border border2;
    TitledBorder titledBorder2;
    Border border3;
    Border border4;
    JPanel actionPanel = new JPanel();
    JButton doneButton = new JButton();
    Border border5;
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel jPanel2 = new JPanel();
    Border border6;
    Border border7;
    Border border8;
    Border border9;
    Border border10;
    Border border11;
    JScrollPane sessionScrollPane = new JScrollPane();
    JTable sessionTable = null;
    LoginPanel loginPanel = null;
    JPanel jPanel1 = new JPanel();
    JButton viewButton = new JButton();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();

    public SessionsViewDialog(Frame frame, String title, SessionsViewController controller, boolean modal) {
        super(frame, title, modal);
        this.controller = controller;
        this.controller.addSessionsViewDialog(this);
        prePackDialog();

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        postPackDialog();
    }

    public SessionsViewDialog(SessionsViewController controller) {
        this(null, "All Sessions", controller, true);
    }

    private void jbInit() throws Exception {
        border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(165, 163, 151));
        titledBorder1 = new TitledBorder(border1, "My Sessions on");
        border2 = BorderFactory.createCompoundBorder(titledBorder1, BorderFactory.createEmptyBorder(2, 2, 2, 2));
        titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153), 2), "Session");
        border3 = BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(142, 142, 142)), "Sessions"), BorderFactory.createEmptyBorder(2, 2, 2, 2));
        border4 = BorderFactory.createEmptyBorder();
        border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        border6 = BorderFactory.createEmptyBorder();
        border7 = BorderFactory.createCompoundBorder(border6, border1);
        panel1.setLayout(borderLayout1);
        panel1.setBorder(border3);
        doneButton.setText("Close");
        doneButton.addActionListener(new SessionsViewDialog_doneButton_actionAdapter(this));
        actionPanel.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        jPanel2.setLayout(borderLayout3);
        viewButton.setAlignmentY((float) 1.0);
        viewButton.setToolTipText("View sessions on the server");
        viewButton.setMargin(new Insets(2, 14, 2, 14));
        viewButton.setText("View");
        viewButton.setVerticalAlignment(SwingConstants.BOTTOM);
        viewButton.addActionListener(new SessionsViewDialog_viewButton_actionAdapter(this));
        jPanel1.setLayout(borderLayout2);
        sessionScrollPane.setPreferredSize(new Dimension(400, 300));
        getContentPane().add(panel1);
        jPanel2.add(loginPanel, BorderLayout.CENTER);
        jPanel2.add(jPanel1, BorderLayout.EAST);
        panel1.add(actionPanel, BorderLayout.SOUTH);
        actionPanel.add(doneButton, null);
        panel1.add(jPanel2, BorderLayout.NORTH);
        panel1.add(sessionScrollPane, BorderLayout.CENTER);
        sessionScrollPane.getViewport().add(sessionTable, null);
        jPanel1.add(viewButton, BorderLayout.SOUTH);
    }

    /**
     * Initialize models and UI components.
     */
    private void prePackDialog() {
        loginPanel = new LoginPanel(controller.getLoginPanelModel());
        SessionTableModel sessionTableModel = controller.getSessionTableModel();
        sessionTable = new JTable(sessionTableModel);
        sessionTableModel.addTableModelListener(sessionTable);
    }

    /**
     * Initialize general Dialog behavors.
     */
    private void postPackDialog() {
        //center the dialog by default
        super.setLocationRelativeTo(null);
        //Handle window closing correctly.
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent evt) {
                doneButton_actionPerformed(null);
            }
        });
        initSessionTable();
    }

    private void initSessionTable() {
        //add context menu
        initPopupMenu();
        sessionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                sessionTable_mouseReleased(e);
            }
        });
        sessionTable.setBackground(Color.pink);
    }

    private void initPopupMenu() {
        sessionMenu.add(getPopupMenuItem("Switch to...", SessionsViewController.POPUPMENU_SWITCH));
        sessionMenu.add(getPopupMenuItem("Delete", SessionsViewController.POPUPMENU_DELETE));
        sessionMenu.addSeparator();
        sessionMenu.add(getPopupMenuItem("Delete all", SessionsViewController.POPUPMENU_DELETE_ALL));
    }

    private JMenuItem getPopupMenuItem(String name, String actionCommand) {
        JMenuItem item = new JMenuItem(name);
        item.setActionCommand(actionCommand);
        item.addActionListener(new PopupMenuActionAdapter(this));
        return item;
    }

    /**
     * Pops up an info dialog.
     *
     * @param message the message.
     * @param title   title of the window.
     */
    public void showDialog(DisplayDialog dialog) {
        dialog.show(this);
    }

    public void sessionTable_mouseReleased(MouseEvent e) {
        if (e.isMetaDown()) {
            sessionMenu.show(sessionTable, e.getX(), e.getY());
        }
    }

    void doneButton_actionPerformed(ActionEvent e) {
        controller.doneButtonActionPerformed();
    }

    void viewButton_actionPerformed(ActionEvent e) {
        //write the login panel data to the model.
        loginPanel.write();
        controller.viewButtonActionPerformed();
    }

    void popupMenu_actionPerformed(ActionEvent evt) {
        int rowIndex = sessionTable.getSelectedRow();
        controller.popupMenuActionPerformed(evt, rowIndex);
    }
}

class PopupMenuActionAdapter implements java.awt.event.ActionListener {
    SessionsViewDialog adaptee;

    PopupMenuActionAdapter(SessionsViewDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.popupMenu_actionPerformed(e);
    }
}

class SessionsViewDialog_doneButton_actionAdapter implements java.awt.event.ActionListener {
    SessionsViewDialog adaptee;

    SessionsViewDialog_doneButton_actionAdapter(SessionsViewDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.doneButton_actionPerformed(e);
    }
}

class SessionsViewDialog_viewButton_actionAdapter implements java.awt.event.ActionListener {
    SessionsViewDialog adaptee;

    SessionsViewDialog_viewButton_actionAdapter(SessionsViewDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.viewButton_actionPerformed(e);
    }
}
