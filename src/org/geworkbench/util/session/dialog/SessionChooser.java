package org.geworkbench.util.session.dialog;

import org.geworkbench.util.session.LoginPanelModel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>LogInDialog</p>
 * <p>Description: SessionChooser parses the user parameters for creating
 * a session.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:Califano Lab </p>
 *
 * @author Aner
 * @version 1.0
 */

public class SessionChooser {
    //value to be returned
    public static int CANCEL_OPTION = 0;
    public static int APPROVE_OPTION = 1;
    private int returnValue = CANCEL_OPTION;

    //these are the field of a session
    private String host = new String();
    private int port = -1;
    private String userName = new String();
    private char[] passWord;
    private String sessionName = new String();
    private CreateSessionDialog dialog = null;

    // added by xiaoqing for inhertence.
    public SessionChooser() {
    }

    public SessionChooser(Frame frame, String title, LoginPanelModel model) {
        dialog = new CreateSessionDialog(frame, title, model, true);
    }

    /**
     * This method shows a dialog with no field populated
     *
     * @return the return state of the dialog:
     *         <ul>
     *         <li>SessionChooser.CANCEL_OPTION
     *         <li>SessionChooser.APPROVE_OPTION
     *         </ul>
     */

    public int show() {
        returnValue = CANCEL_OPTION;

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                returnValue = CANCEL_OPTION;
            }
        });

        dialog.show();
        int ret = dialog.getReturnValue();

        if (ret == CreateSessionDialog.CONNECT_OPTION) {
            returnValue = APPROVE_OPTION;
            host = dialog.getHostName();
            this.port = dialog.getPortNum();
            this.userName = dialog.getUserName();
            char[] pWord = dialog.getPassWord();
            if (pWord != null) {
                this.passWord = new char[pWord.length];
                System.arraycopy(pWord, 0, this.passWord, 0, pWord.length);
            } else {
                this.passWord = new char[0];
            }
            this.sessionName = dialog.getSessionName();
        }

        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
        return returnValue;
    }

    /**
     * This method retuns the choosen host name.
     * Note: this method is valid only after the show method
     * returned APPROVE_OPTION.
     *
     * @return host name
     */
    public String getHostName() {
        return host;
    }

    /**
     * This method retuns the user name.
     * Note: this method is valid only after the show method
     * returned APPROVE_OPTION.
     *
     * @return user name
     */

    public String getUserName() {
        return userName;
    }

    /**
     * This method retuns the port number.
     * Note: this method is valid only after the show method
     * returned APPROVE_OPTION.
     *
     * @return port number
     */
    public int getPortNum() {
        return port;
    }

    /**
     * This method retuns the session.
     * Note: this method is valid only after the show method
     * returned APPROVE_OPTION.
     *
     * @return session name
     */
    public String getSession() {
        return sessionName;
    }

    /**
     * This method retuns the password.
     * Note: this method is valid only after the show method
     * returned APPROVE_OPTION. For security reason the caller
     * should set the array indices to 0 after the use of password.
     *
     * @return password
     */
    public char[] getPassWord() {
        return passWord;
    }
}
