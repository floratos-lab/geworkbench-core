package org.geworkbench.util.session;

import org.geworkbench.util.PropertiesMonitor;
import org.geworkbench.events.LoginPanelModelEvent;
import org.geworkbench.events.LoginPanelModelListener;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: This class acts as the data model for the LoginPanel.
 * This class does not have ANY policy on the data with 1 exceptions:
 * 1) It stores a set of hosts.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class LoginPanelModel {
    public LoginPanelModel() {
        //parse initial Model data from the properties monitor
        PropertiesMonitor pm = org.geworkbench.util.PropertiesMonitor.getPropertiesMonitor();
        hostSet = pm.getHosts();
        currentHost = pm.getHostSelected();
        normalizeHost(hostSet, currentHost);
        normalizePort(pm.getPort());
        normalizeUserName(pm.getUserName());
        //get the type info directly now.
        hostTypeList = getTypeList();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String userName;
    private char[] password;
    private Set hostSet;
    private String currentHost;
    private String port;
    private List hostTypeList;
    private String currentType;

    /**
     * List of listeners
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * This method add listener to LoginPanelModel events.
     *
     * @param l
     */
    public void addLoginPanelModelListener(org.geworkbench.events.LoginPanelModelListener l) {
        listenerList.add(org.geworkbench.events.LoginPanelModelListener.class, l);
    }

    /**
     * This method returns the listener to LoginPanelModel events.
     *
     * @param l
     */
    public void removeLoginPanelModelListener(org.geworkbench.events.LoginPanelModelListener l) {
        listenerList.remove(org.geworkbench.events.LoginPanelModelListener.class, l);
    }

    /**
     * This method fires an LoginPanelModelEvent to all listeners.
     */
    public void fireLoginPanelModelChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        LoginPanelModelEvent e = new LoginPanelModelEvent(this);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == org.geworkbench.events.LoginPanelModelListener.class) {
                ((org.geworkbench.events.LoginPanelModelListener) listeners[i + 1]).loginPanelChanged(e);
            }
        }
    }

    /**
     * parse the host and host set
     *
     * @param hostSet
     * @param host
     */
    protected void normalizeHost(Set hostSet, String currentHost) {
        this.hostSet = hostSet;
        this.currentHost = currentHost;

        if (this.currentHost == null) {
            this.currentHost = "";
        }
        //the intersection of currentHost and hostSet should be empty
        if (this.hostSet != null) {
            if (this.currentHost != null) {
                hostSet.remove(this.currentHost);
            }
        }
    }

    protected void normalizePort(String port) {
        this.port = port;
        if (this.port == null) {
            this.port = "";
        }
    }

    protected void normalizeUserName(String userName) {
        this.userName = userName;
        if (this.userName == null) {
            try {
                this.userName = System.getProperties().getProperty("user.name");
            } catch (SecurityException exp) {
                //we are not allowed to read properties...
                this.userName = "";
            }
        }
    }

    public void setCurrentHostName(String host) {
        if (hostSet != null && currentHost != null) {
            hostSet.add(currentHost);
        }
        normalizeHost(hostSet, host);
    }

    /**
     * This method retuns the host name.
     *
     * @return host name
     */
    public String getHostName() {
        return currentHost;
    }

    /**
     * Returns the host set.
     *
     * @return host set. May return null.
     */
    public Set getHostSet() {
        return hostSet;
    }

    /**
     * This method retuns the user name.
     *
     * @return user name
     */

    public String getUserName() {
        return userName;
    }

    /**
     * This method retuns the port number.
     *
     * @return port number
     */
    public String getPort() {
        return port;
    }

    /**
     * This method retuns the password.
     *
     * @return password
     */
    public char[] getPassword() {
        if (password == null) {
            return password;
        } else {
            char[] retPassword = new char[password.length];
            System.arraycopy(password, 0, retPassword, 0, password.length);
            return retPassword;
        }
    }

    /**
     * The method sets the password.
     *
     * @param hostSet
     * @param currentHost
     */
    public void setPassword(char[] newPassword) {
        if (newPassword == null) {
            this.password = null;
            return;
        }
        this.password = new char[newPassword.length];
        System.arraycopy(newPassword, 0, password, 0, newPassword.length);
    }

    /**
     * Set the host set.
     *
     * @param hostSet     the set of host
     * @param currentHost the default selected host
     */
    public void setHostNames(Set hostSet, String currentHost) {
        normalizeHost(hostSet, currentHost);
    }

    public void setPort(String port) {
        normalizePort(port);
    }

    public void setUserName(String name) {
        normalizeUserName(name);
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    public List getTypeList() {
        List hostList = new ArrayList();
        String hostListStr = System.getProperties().getProperty("supported.host.type");
        if (hostListStr != null) {
            StringTokenizer st = new StringTokenizer(hostListStr);

            while (st.hasMoreTokens()) {
                hostList.add(st.nextToken());
            }
            return (hostList);

        } else {
            hostList.add("splash");
            hostList.add("blast");

        }
        return hostList;
    }

    public List getHostTypeList() {
        return hostTypeList;
    }

    public void setHostTypeList(List hostTypeList) {
        this.hostTypeList = hostTypeList;
    }

    private void jbInit() throws Exception {
    }

}
