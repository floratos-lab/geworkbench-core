package org.geworkbench.util.session.dialog;

import org.geworkbench.events.LoginPanelModelEvent;
import org.geworkbench.util.session.LoginPanelModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class LoginPanel extends JPanel implements Serializable, org.geworkbench.events.LoginPanelModelListener {
    JPasswordField password = new JPasswordField();
    JTextField userName = new JTextField();
    JTextField portName = new JTextField();
    JLabel jLabel2 = new JLabel();
    JComboBox hostName = new JComboBox();
    JLabel serverLabel = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel passwordLabel = new JLabel();
    Border border1;
    LoginPanelModel model;
    JComboBox serverTypeName = new JComboBox();
    JLabel jLabel4 = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    public LoginPanel(LoginPanelModel model) {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setModel(model);
        model.addLoginPanelModelListener(this);
        model.fireLoginPanelModelChanged();
    }

    /**
     * This method retuns the host name.
     *
     * @return host name
     */
    public Object getHostName() {
        return getModel().getHostName();
    }

    /**
     * Sets the model for this panel
     *
     * @param model the model
     */
    public void setModel(LoginPanelModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Cannot set a null LoginPanelModel");
        }
        this.model = model;
    }

    /**
     * Returns the model for this login panel
     *
     * @return model
     */
    public LoginPanelModel getModel() {
        return model;
    }

    public void loginPanelChanged(LoginPanelModelEvent evt) {
        LoginPanelModel lpm = (LoginPanelModel) evt.getSource();
        userName.setText(lpm.getUserName());
        portName.setText(lpm.getPort());

        showHostSet(lpm.getHostSet(), lpm.getHostName());
        showTypeList(lpm.getHostTypeList(), lpm.getCurrentType());
    }

    private void showTypeList(List host, String selected) {
        if (selected != null) {
            serverTypeName.addItem(selected);
        }
        if (host != null) {
            for (Iterator iter = host.iterator(); iter.hasNext();) {
                //for(int i= 0; i< host.size();  i; ){
                serverTypeName.addItem(iter.next());
            }
        }

    }

    private void showHostSet(Set host, String selected) {
        if (selected != null) {
            hostName.addItem(selected);
        }
        if (host != null) {
            for (Iterator iter = host.iterator(); iter.hasNext();) {
                hostName.addItem(iter.next());
            }
        }
    }

    /**
     * The method writes the information of the panel to the model.
     */
    public void write() {
        LoginPanelModel m = getModel();
        m.setCurrentHostName((String) hostName.getSelectedItem());
        m.setPort(portName.getText());
        m.setUserName(userName.getText());
        m.setPassword(password.getPassword());
        m.setCurrentType((String) serverTypeName.getSelectedItem());
    }

    /**
     * This method retuns the user name.
     *
     * @return user name
     */
    public String getUserName() {
        return getModel().getUserName();
    }

    /**
     * This method retuns the port number.
     *
     * @return port number
     */
    public String getPortNum() {
        return getModel().getPort();
    }

    /**
     * This method retuns the password.
     *
     * @return password
     */
    public char[] getPassword() {
        return getModel().getPassword();
    }

    public void setHostNames(Set hostSet, String first) {
        getModel().setHostNames(hostSet, first);
    }

    public void setPortNum(String port) {
        getModel().setPort(port);
    }

    public void setUserName(String name) {
        getModel().setUserName(name);
    }

    public LoginPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public LoginPanel(LayoutManager layout) {
        super(layout);
    }

    public LoginPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    private void jbInit() throws Exception {
        border1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        passwordLabel.setText("Password:");
        jLabel3.setText("User Name:");
        serverLabel.setText("Server:");
        hostName.setEditable(true);
        hostName.addActionListener(new LoginPanel_hostName_actionAdapter(this));
        hostName.setPreferredSize(new Dimension(150, 21));
        hostName.setMinimumSize(new Dimension(150, 21));
        jLabel2.setText("Port:");
        portName.setText("");
        portName.setPreferredSize(new Dimension(150, 21));
        portName.setMinimumSize(new Dimension(150, 21));
        userName.setScrollOffset(0);
        userName.setPreferredSize(new Dimension(150, 21));
        userName.setMinimumSize(new Dimension(150, 21));
        password.setToolTipText("");
        password.setPreferredSize(new Dimension(150, 21));
        password.setMinimumSize(new Dimension(150, 21));
        password.setText("");
        this.setLayout(gridBagLayout1);
        this.setBackground(new Color(204, 204, 204));
        this.setBorder(border1);
        serverTypeName.setMinimumSize(new Dimension(150, 21));
        serverTypeName.setPreferredSize(new Dimension(150, 21));
        serverTypeName.addActionListener(new LoginPanel_serverTypeName_actionAdapter(this));
        serverTypeName.setEditable(true);
        jLabel4.setText("Server Type:");
        this.add(password, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(userName, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(portName, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        this.add(hostName, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(serverLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        this.add(jLabel3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        this.add(passwordLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
        this.add(serverTypeName, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 3, 0));
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    void hostName_actionPerformed(ActionEvent e) {

    }
}

class LoginPanel_hostName_actionAdapter implements java.awt.event.ActionListener {
    LoginPanel adaptee;

    LoginPanel_hostName_actionAdapter(LoginPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.hostName_actionPerformed(e);
    }
}

class LoginPanel_serverTypeName_actionAdapter implements java.awt.event.ActionListener {
    LoginPanel adaptee;

    LoginPanel_serverTypeName_actionAdapter(LoginPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.hostName_actionPerformed(e);
    }
}


