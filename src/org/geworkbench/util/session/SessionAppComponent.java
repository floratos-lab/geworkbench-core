package org.geworkbench.util.session;

import org.geworkbench.engine.management.Publish;
import org.geworkbench.events.SessionConnectEvent;
import org.geworkbench.util.session.dialog.SessionsViewDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class SessionAppComponent implements org.geworkbench.engine.config.MenuListener {
    //Holds refrences to listeners of menu items for this component.
    private HashMap listeners = new HashMap();
    LoginPanelModel loginPanelModel = new LoginPanelModel();

    public SessionAppComponent() {
        //Register menu items listener - sessions dialog
        ActionListener allSession = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllSessionDialog(e);
            }
        };
        listeners.put("Commands.Sessions.ViewAll", allSession);
    }

    /**
     * Return a listener which registered with the var string.
     *
     * @param var - the name of the listener
     * @return - the listener
     */
    public ActionListener getActionListener(String var) {
        return (ActionListener) listeners.get(var);
    }

    @Publish public SessionConnectEvent publishSessionConnectEvent(org.geworkbench.events.SessionConnectEvent event) {
        return event;
    }

    /**
     * Display the sessions dialog.
     *
     * @param e
     */
    private void showAllSessionDialog(ActionEvent e) {
        SessionsViewDialog viewer = new SessionsViewDialog(new SessionsViewController(loginPanelModel, this));
        viewer.setVisible(true);
    }

}
