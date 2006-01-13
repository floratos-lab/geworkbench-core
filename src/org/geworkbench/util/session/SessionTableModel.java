package org.geworkbench.util.session;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.NumberFormat;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: A class to storing session's data.
 * The class has helper classes (workers) that retrieve the session's info from
 * the spalsh server. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Aner
 * @version 1.0
 */

public class SessionTableModel extends AbstractTableModel {
    private final int NO_COL = 0;
    private final int NAME_COL = 1;
    private final int TYPE_COL = 2;
    private final int PER_COMPLETE_COL = 3;
    private final int OTHER_COL = 4;

    //headers names for the columns
    private String[] sessionTableHeader = {"No.", "Name", "Type", "% Complete", "Other"};
    //holds sessions' information.
    //this may not the most efficient DS for the job, but good enough.
    private java.util.ArrayList sessionArray = new java.util.ArrayList();

    /**
     * See  javax.swing.table.TableModel
     *
     * @return number of rows in the model
     */
    public int getRowCount() {
        return sessionArray.size();
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @return number of columns in the model
     */
    public int getColumnCount() {
        return sessionTableHeader.length;
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @param columnIndex the index of the column
     * @return the name of the column
     */
    public String getColumnName(int columnIndex) {
        return sessionTableHeader[columnIndex];
    }

    /**
     * Add a session to the model
     *
     * @param value a session
     */
    public void addSession(SessionStat value) {
        if (value != null) {
            sessionArray.add(value);
        }
    }

    /**
     * Return a session's id as it known on the server.
     *
     * @param rowIndex of the session.
     * @return the session id.
     */
    public int getSessionID(int rowIndex) {
        return ((SessionStat) sessionArray.get(rowIndex)).getSessionId();
    }

    public String getSessionName(int rowIndex) {
        return ((SessionStat) sessionArray.get(rowIndex)).getName();
    }

    /**
     * Remove a session from the model.
     *
     * @param rowIndex of the session to be removded.
     *                 0<= rowIndex < getRowCound()
     */
    public void removeSession(int rowIndex) {
        sessionArray.remove(rowIndex);
    }

    /**
     * See  javax.swing.table.TableModel
     *
     * @param rowIndex    the index of the row
     * @param columnIndex the index of the column
     * @return the Object at (rowIndex, columnIndex)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (sessionArray.size() == 0) {
            return null;
        }

        switch (columnIndex) {
            case NO_COL:
                return new Integer(rowIndex + 1);
            case NAME_COL:
                String name = ((SessionStat) sessionArray.get(rowIndex)).getName();
                return (name == null) ? "---" : name;
            case TYPE_COL:
                String type = ((SessionStat) sessionArray.get(rowIndex)).getType();
                return (type == null) ? "---" : type;
            case PER_COMPLETE_COL:
                double percent = ((SessionStat) sessionArray.get(rowIndex)).getPercentComplete();
                return NumberFormat.getPercentInstance().format(percent);
            case OTHER_COL:
                String other = ((SessionStat) sessionArray.get(rowIndex)).getOther();
                return (other == null) ? "---" : other;
            default:
                return null;
        }
    }
}

/****************************************
 *Classes for doing work with the server*
 ****************************************/

/**
 * <p>Title: SessionTableModelWorker</p>
 * <p>Description: This class is the base class for communicating
 * with the server. </p>
 */
abstract class SessionTableModelWorker extends org.geworkbench.util.SwingWorker {
    protected SessionTableModel model;
    protected SessionQuery query;
    protected SessionsViewController controller;
    //indicates that the work is done.
    private boolean done = false;

    public SessionTableModelWorker(SessionsViewController controller, SessionTableModel model, SessionQuery query) {
        this.model = model;
        this.query = query;
        this.controller = controller;
    }


    public void finished() {
        controller.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        /*finish() runs on the event dispatching thread.
         *Some paths in customizeFinished() method may lead to the poping up
         *of a JDialog (through the controller). This is ok.
         *See notes in JDialog class.
         */
        customizeFinished();
        //update session table with the session
        model.fireTableDataChanged();
        setDone(true);
    }

    /**
     * Classes deriving from this class will override this method
     * to cutomize the behavior of finished().
     */
    protected void customizeFinished() {
    }

    protected void message(String message) {
        controller.showMessageDialog(message);
    }

    protected synchronized void setDone(boolean val) {
        done = val;
    }

    /**
     * Test if the work is finished.
     *
     * @return true if work is done, else false.
     */
    public synchronized boolean isDone() {
        return (done == true);
    }
}

/**
 * <p>Title: SessionTableModelWorker</p>
 * <p>Description: This class does session Revtieving.</p>
 */
class SessionTableModelPullSession extends SessionTableModelWorker {
    private boolean connectionSuccess = true;

    public SessionTableModelPullSession(SessionsViewController controller, SessionTableModel model, SessionQuery query) {
        super(controller, model, query);
    }

    public Object construct() {
        try {
            //pull sessions from the server
            SessionStat[] session = query.getSession();
            //add the sessions to the model
            for (int i = 0; i < session.length; ++i) {
                model.addSession(session[i]);
            }
        } catch (SessionOperationException exp) {
            connectionSuccess = false;
            return null;
        }
        return null;
    }

    protected void customizeFinished() {
        if (!connectionSuccess) {
            message("Connection was lost to the server while retrieving the sessions.");
        } else if (model.getRowCount() == 0) {
            //lest the user no there are no sessions for her
            message("No session were found on the server.");
        }
    }
}

/**
 * <p>Title: SessionTableModelWorker</p>
 * <p>Description: This class delete a session from the server.</p>
 */
class SessionTableModelDeleteSession extends SessionTableModelWorker {
    private boolean operationSuccess = true;
    private int index;
    private boolean deleteAll;
    //holds detail if the operation failed
    private String detail;

    /**
     * Deletes 1 session.
     *
     * @param controller
     * @param index
     */
    public SessionTableModelDeleteSession(SessionsViewController controller, SessionTableModel model, SessionQuery query, int index) {
        super(controller, model, query);
        this.index = index;
        deleteAll = false;
    }

    /**
     * Deletes all sesssions.
     *
     * @param controller
     * @param model
     */
    public SessionTableModelDeleteSession(SessionsViewController controller, SessionTableModel model, SessionQuery query) {
        super(controller, model, query);
        deleteAll = true;
    }

    public Object construct() {
        try {
            if (deleteAll) {
                //loop here to delete all the sessions.
                while (model.getRowCount() != 0) {
                    int kill = model.getRowCount() - 1;
                    query.deleteSession(model.getSessionID(kill));
                    model.removeSession(kill);
                }
            } else {
                //delete a single session.
                query.deleteSession(model.getSessionID(index));
                model.removeSession(index);
            }
        } catch (SessionOperationException exp) {
            operationSuccess = false;
            detail = exp.getMessage();
            return null;
        }
        return null;
    }

    protected void customizeFinished() {
        if (!operationSuccess) {
            message("Operation failed.\n" + detail);
        }
    }
}
