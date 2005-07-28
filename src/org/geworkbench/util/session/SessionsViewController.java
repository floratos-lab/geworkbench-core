package org.geworkbench.util.session;

import org.geworkbench.events.SessionConnectEvent;
import org.geworkbench.util.session.dialog.SessionsViewDialog;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Controls the actions of a SessionsViewDialog.
 * The class also holds all the Models for the dialog</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano lab</p>
 *
 * @author Aner
 * @version 1.0
 */

public class SessionsViewController {
    //the login model
    private LoginPanelModel loginPanelModel = null;

    //session table model
    private SessionTableModel sessionTableModel = new SessionTableModel();
    private SessionsViewDialog viewDialog;

    //the current query object
    SessionQuery sessionQuery;

    //Idendify action in the popup menu
    public final static String POPUPMENU_SWITCH = "popup_switch";
    public final static String POPUPMENU_DELETE = "popup_delete";
    public final static String POPUPMENU_DELETE_ALL = "popup_delete_all";

    //Dialogs for communicating info to the user
    private MessageDialog messageDialog = new MessageDialog();
    private ConfirmDialog confirmDialog = new ConfirmDialog();

    //pulls/deletes sessions from the server
    private SessionTableModelWorker worker = null;

    private SessionAppComponent component = null;

    public SessionsViewController(LoginPanelModel lModel, SessionAppComponent component) {
        this.loginPanelModel = lModel;
        this.component = component;
        messageDialog.setTitle("Session viewer");
    }

    /**
     * This method adds the SessionsViewDialog object that this controller
     * controls.
     *
     * @param dialog
     */
    public void addSessionsViewDialog(SessionsViewDialog dialog) {
        viewDialog = dialog;
    }

    public void popupMenuActionPerformed(java.awt.event.ActionEvent evt, int rowIndex) {
        String actionCommand = evt.getActionCommand();
        if (actionCommand.equals(POPUPMENU_DELETE)) {
            if (rowIndex == -1) {
                showMessageDialog("Please select a session.");
            } else {
                showConfirmDialog("Delete the selected session?");
                if (confirmDialog.approve()) {
                    deleteSession(rowIndex);
                }
            }
        } else if (actionCommand.equals(POPUPMENU_DELETE_ALL)) {
            showConfirmDialog("Delete all sessions?");
            if (confirmDialog.approve()) {
                deleteAllSession();
            }
        } else if (actionCommand.equals(POPUPMENU_SWITCH)) {
            if (rowIndex == -1) {
                showMessageDialog("Please select a session.");
            } else {

                int sid = sessionTableModel.getSessionID(rowIndex);
                String sName = sessionTableModel.getSessionName(rowIndex);
                int port = Integer.parseInt(loginPanelModel.getPort());
                // String service = "splash"; //to be changed....parse from file...
                String service = loginPanelModel.getCurrentType();
                component.publishSessionConnectEvent(new org.geworkbench.events.SessionConnectEvent(loginPanelModel.getUserName(), loginPanelModel.getPassword(), loginPanelModel.getHostName(), port, service, sid, sName));
            }
        }
    }

    public void doneButtonActionPerformed() {
        //
        worker = null;

        viewDialog.setVisible(false);
    }

    /**
     * This method is responsible for the action of the view button.
     *
     * @return
     */
    public void viewButtonActionPerformed() {
        if (!verify()) {
            return;
        }
        int port = Integer.parseInt(loginPanelModel.getPort());
        try {
            String className = loginPanelModel.getHostName();
            //SessionQueryConnection con = new SplashSessionQueryFactory();//to be modified
            String serverType = loginPanelModel.getCurrentType();
            String serverClassName = System.getProperties().getProperty(serverType);

            SessionQueryConnection con = (SessionQueryConnection) Class.forName(serverClassName).newInstance();

            sessionQuery = con.getSessionQuery(loginPanelModel.getUserName(), new String(loginPanelModel.getPassword()), loginPanelModel.getHostName(), port);

            System.out.println(sessionQuery.getClass().getName());

            pullSession();
        } catch (SessionOperationException ex) {
            showMessageDialog(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            showMessageDialog(ex.getMessage());

        } catch (Exception ex) {
            showMessageDialog(ex.getMessage());

        }

    }

    /**
     * Return the LoginPanel model
     *
     * @return login panel model
     */
    public LoginPanelModel getLoginPanelModel() {
        return loginPanelModel;
    }

    /**
     * Pops a info message on the dialog.
     *
     * @param message
     * @param title
     */
    public void showMessageDialog(String details) {
        showMessageDialog(details, "Sessions view");
    }

    private void showMessageDialog(String message, String title) {
        messageDialog.setMessage(message);
        messageDialog.setTitle(title);
        //check the the user still has the dialog open
        if (viewDialog.isVisible()) {
            viewDialog.showDialog(messageDialog);
        }
    }

    private void showConfirmDialog(String details) {
        confirmDialog.setMessage(details);
        confirmDialog.setTitle("Sessions view");
        //check the the user still has the dialog open
        if (viewDialog.isVisible()) {
            viewDialog.showDialog(confirmDialog);
        }
    }

    public SessionTableModel getSessionTableModel() {
        return sessionTableModel;
    }

    private boolean verifyUserName() {
        if (loginPanelModel.getUserName().trim().equals("")) {
            showMessageDialog("Please enter a user name.");
            return false;
        }
        return true;
    }

    private boolean verifyPort() {
        try {
            Integer.parseInt(loginPanelModel.getPort());
        } catch (NumberFormatException exp) {
            showMessageDialog("Please enter a number for the port.");
            return false;
        }
        return true;
    }

    private boolean verifyhost() {
        String selected = (String) loginPanelModel.getHostName();
        selected = selected.trim();
        if (selected.equals("")) {
            showMessageDialog("Please enter host name.");
            return false;
        }
        return true;
    }

    private boolean verify() {
        if (!verifyhost()) {
            return false;
        }
        if (!verifyPort()) {
            return false;
        }
        if (!verifyUserName()) {
            return false;
        }
        return true;
    }

    /**
     * This method is called to retrieve the sessions from the server.
     * The sessions are added to the model.
     */
    private synchronized void pullSession() {
        //Only 1 (one) SessionTableModelWorker is allowed to be alive
        //at any giving time. Ensure that this is true.
        if (worker != null && !worker.isDone()) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //clear the model
        while (sessionTableModel.getRowCount() != 0) {
            sessionTableModel.removeSession(0);
        }
        sessionTableModel.fireTableDataChanged();

        worker = new SessionTableModelPullSession(this, sessionTableModel, sessionQuery);
        worker.start();
    }

    public void setCursor(Cursor cursor) {
        viewDialog.setCursor(cursor);
    }

    /**
     * Delete a session from the model. The session is deleted from the server.
     *
     * @param rowIndex session id to delete
     */
    private synchronized void deleteSession(int rowIndex) {
        //Only 1 (one) SessionTableModelWorker is allowed to be alive
        //at any giving time. Ensure that this is true.
        if (worker != null && !worker.isDone()) {
            return;
        }

        worker = new SessionTableModelDeleteSession(this, sessionTableModel, sessionQuery, rowIndex);
        worker.start();
    }

    public synchronized void deleteAllSession() {
        //Only 1 (one) SessionTableModelWorker is allowed to be alive
        //at any giving time. Ensure that this is true.
        if (worker != null && !worker.isDone()) {
            return;
        }
        worker = new SessionTableModelDeleteSession(this, sessionTableModel, sessionQuery);
        worker.start();
    }
}

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Class for showing message dialogs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 */
abstract class BaseDialog implements DisplayDialog {
    protected String theMessage = "";
    protected String title = "";

    public void setMessage(String message) {
        this.theMessage = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Class for showing message dialogs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 */

class MessageDialog extends BaseDialog {

    public void show(Component parent) {
        JOptionPane.showMessageDialog(parent, theMessage, title, JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Class for showing a yes/no dialogs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 */
class ConfirmDialog extends BaseDialog {
    private int result = JOptionPane.NO_OPTION;

    public void show(Component parent) {
        result = JOptionPane.showConfirmDialog(parent, theMessage, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public boolean approve() {
        return (result == JOptionPane.YES_OPTION);
    }
}
