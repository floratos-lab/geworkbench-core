package org.geworkbench.builtin.projects.remoteresources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


/**
 * <p>Title: </p>
 *
 * <p>The GUI to add/edit a remoteResource. </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author unknown
 * @version $Id$
 */
public class RemoteResourceDialog extends JDialog {
	private static final long serialVersionUID = -4311812466234532113L;
	private static RemoteResourceManager remoteResourceManager = new
            RemoteResourceManager();
    private static RemoteResourceDialog dialog;
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int EDIT = 2;
    public static int currentOption = 0;
    private boolean isSourceDirty = true;
    private static String currentResourceName;
    private String currentURL;
    private int currentPortnumber;
    private String currentUser;
    private String currentPassword;
    private static String previousResourceName;
    private static boolean dirty = false;
    public RemoteResourceDialog() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String[] getResourceNames() {
        return remoteResourceManager.getItems();
    }

    private RemoteResourceDialog(Frame frame,
                                 String title,
                                 int option,
                                 String initialName) {
        super(frame, title, true);
        currentOption = option;
        currentResourceName = initialName;

        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (option == ADD) {
            clearFields();
            repaint();
        } else if (option == EDIT) {
            setFields(remoteResourceManager.getSelectedResouceByName(
                    initialName));

        }

    }

    public static RemoteResourceManager getRemoteResourceManager() {
        return remoteResourceManager;
    }

    public static void setRemoteResourceManager(RemoteResourceManager remoteResourceManager) {
        RemoteResourceDialog.remoteResourceManager = remoteResourceManager;
    }

    private void jbInit() throws Exception {
        jLabel1.setText("Details:");
        jLabel2.setPreferredSize(new Dimension(100, 20));
        jLabel2.setText("Port:");
        jLabel2.setHorizontalAlignment(JLabel.RIGHT);
        jLabel3.setText("Username:");
        boxLayout21 = new BoxLayout(jPanel2, BoxLayout.Y_AXIS);

        jPanel2.setLayout(boxLayout21);

        jTextField1.setToolTipText("");
        jLabel4.setPreferredSize(new Dimension(100, 20));
        jLabel4.setText("Hostname:");
        jLabel4.setHorizontalAlignment(JLabel.RIGHT);
        this.getContentPane().setLayout(xYLayout1);
        jPanel1.setLayout(borderLayout1);

        jLabel5.setText("Password: ");
        jTextField2.setText("www.columbia.edu");
        jTextField3.setText("80");
        jButton1.setText("OK");
        jButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		        jButton1_actionPerformed(e);
			}
        	
        });

        jButton6.setText("Cancel");
        jButton6.addActionListener(new
                                   RemoteResourceDialog_jButton6_actionAdapter(this));
        jPanel4.setBorder(null);
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel6.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel6.setText("Protocol:");
        jComboBox1.addActionListener(new
                                     RemoteResourceDialog_jComboBox1_actionAdapter(this));
        shortnameLabel.setText("Profile name:");
        shortnameTextField.setText("NCI_CaArray");
        jPanel7.setBorder(BorderFactory.createLineBorder(Color.black));
        boxLayout22 = new BoxLayout(jPanel7, BoxLayout.Y_AXIS);
        jPanel7.setLayout(boxLayout22);

        jPanel3.add(jLabel3);
        jPanel3.add(jTextField1);
        JTextArea informative = new JTextArea("In case you wish to access non-public data, you need to supply your caArray Username and Password");
        informative.setLineWrap(true);
        informative.setWrapStyleWord(true);
        informative.setEditable(false);
        jPanel2.add(informative);
        jPanel2.add(jPanel3);
        jPanel2.add(jPanel4);
        jPanel2.setPreferredSize(new Dimension(120, 150));
        jPanel4.add(jLabel5);
        jPanel4.add(jPasswordField1);
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);
        jPanel7.add(jPanel5);
        jPanel7.add(jPanel8);
        jPanel7.add(jPanel9);
        JPanel portPanel = new JPanel();
        jPanel7.add(portPanel);
        jPanel8.add(jLabel6);
        jPanel8.add(jComboBox1);
        jPanel5.add(shortnameLabel);
        jPanel5.add(shortnameTextField);
        jPanel7.add(jPanel2);
        jPanel9.add(jLabel4);
        jPanel9.add(jTextField2);
        portPanel.add(jLabel2);
        portPanel.add(jTextField3);
        jPanel6.add(jButton1);
        jPanel6.add(jButton6);
        this.getContentPane().add(jPanel1, BorderLayout.NORTH);
        this.getContentPane().add(jPanel7, BorderLayout.CENTER);
        this.getContentPane().add(jPanel6, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    JLabel jLabel1 = new JLabel();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JPanel jPanel3 = new JPanel();
    JPanel jPanel4 = new JPanel();
    BoxLayout boxLayout21;
    JTextField jTextField1 = new JTextField(10);
    JLabel jLabel4 = new JLabel();
    JPasswordField jPasswordField1 = new JPasswordField(10);
    BorderLayout xYLayout1 = new BorderLayout();
    JPanel jPanel6 = new JPanel();

    JLabel jLabel5 = new JLabel();
    JTextField jTextField2 = new JTextField(10);
    JTextField jTextField3 = new JTextField(10);
    JButton jButton1 = new JButton();
    JButton jButton6 = new JButton();
    Border border1 = BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
            Color.white, new Color(165, 163, 151));
    JLabel jLabel6 = new JLabel();
    JComboBox jComboBox1 = new JComboBox(new String[] {"HTTP", "HTTPS", "RMI"});
    JPanel jPanel7 = new JPanel();
    JLabel shortnameLabel = new JLabel();
    JTextField shortnameTextField = new JTextField(10);
    JPanel jPanel5 = new JPanel();
    JPanel jPanel8 = new JPanel();
    JPanel jPanel9 = new JPanel();
    BoxLayout boxLayout22;
    /**
     * setOption
     *
     * @param option int
     */
    public void setOption(int option) {
        currentOption = option;
    }

    public void setCurrentResourceName(String name) {
        RemoteResource rr = remoteResourceManager.getSelectedResouceByName(name);
        if (rr != null) {
            setFields(rr);
        }
    }

    public void setCurrentURL(String currentURL) {
        this.currentURL = currentURL;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setDirty(boolean dirty) {
    	RemoteResourceDialog.dirty = dirty;
    }

    public void setUser(String user) {
        this.currentUser = user;
    }

    public int getCurrentPortnumber() {
		return currentPortnumber;
	}

	public void setCurrentPortnumber(int currentPortnumber) {
		this.currentPortnumber = currentPortnumber;
	}

	public void jComboBox1_actionPerformed(ActionEvent e) {

    }

    private RemoteResource collectResourceInfo() {

        String shortname = shortnameTextField.getText().trim();
        String url = jTextField2.getText().trim();
        if (shortname.length() == 0) {
            JOptionPane.showMessageDialog(null, "Profile name can not be empty.",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (url.length() == 0) {
            JOptionPane.showMessageDialog(null, "URL can not be empty.",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
       
       RemoteResource rr = new RemoteResource(
    		   url,
    		   jComboBox1.getSelectedItem().toString().trim(),
    		   jTextField1.getText().trim(),
    		   new String(jPasswordField1.getPassword()).trim()
    		   );

        if(jTextField2.getToolTipText()!=null && jTextField2.getToolTipText().length()>10){


            rr.setEditable(false);
        }
        try {
            int portnum = new Integer(jTextField3.getText().trim()).intValue();
            rr.setPortnumber(portnum);

        } catch (NumberFormatException e) {
            rr.setPortnumber(80);
        }

        rr.setShortname(shortname);
        currentResourceName = shortname;
        rr.setDirty(true);
        return rr;
    }

    public void clearFields() {
        jPasswordField1.setText("");
        jTextField1.setText("");
        jTextField2.setText("");
        shortnameTextField.setText("");
        jTextField3.setText("80");

    }

    public void setFields(RemoteResource rr) {
        if (rr != null) {
            isSourceDirty = rr.isDirty();
            jPasswordField1.setText(rr.getPassword());
            jTextField1.setText(rr.getUsername());
            jTextField2.setText(rr.getUri().toString());
            jComboBox1.setSelectedItem(rr.getConnectProtocal());
            shortnameTextField.setText(rr.getShortname());
            jTextField3.setText(new Integer(rr.getPortnumber()).toString());
            boolean editable = rr.isEditable();

            jTextField3.setEditable(editable);
            jTextField2.setEditable(editable);
            shortnameTextField.setEditable(editable);
            jComboBox1.setEditable(editable);
            if (!editable) {
                jLabel1.setText("The Details Come From the caARRAY Index Service.");
                shortnameTextField.setToolTipText(
                        "Shortname cannot be changed if it is from Index service");
                jTextField2.setToolTipText(
                        "URL cannot be changed if it is from Index service");
                jTextField3.setToolTipText(
                        "Port number cannot be changed if it is from Index service");
            }else{
                jLabel1.setText("The Details Come From Local Users.");
            }
        }
    }

    private void jButton1_actionPerformed(ActionEvent e) {
        /*
		 * if this method been called, it means user clicked OK. Let's set
		 * executed to true, so the dialog will return true.
		 */
        executed = true;

        RemoteResource rr = collectResourceInfo();
        if (rr != null) {
            if(previousResourceName != null && previousResourceName.equals(currentResourceName)){
                RemoteResource previousRR = remoteResourceManager.getSelectedResouceByName(previousResourceName);
                if(previousRR.equals(rr)){
                   rr.setDirty(previousRR.isDirty());
                }
                remoteResourceManager.deleteRemoteResource(previousResourceName);
            }
            remoteResourceManager.addRemoteResource(rr);
            remoteResourceManager.saveToFile();
            dirty = true;
            dispose();
        }

    }

    private static boolean executed = false; /* executed means user click OK */
    /**
     * Set up and show the dialog.
     */
    public static boolean showDialog(Component frameComp,

                                    String title,
                                    int option,
                                    String initialValue
            ) {
    	executed = false;
        if(initialValue!=null){
            previousResourceName = initialValue;
        }
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new RemoteResourceDialog(frame,
                                          title,
                                          option,
                                          initialValue);

        dialog.setVisible(true);
        return executed;
    }

    public static String getPreviousResourceName() {
        return previousResourceName;
    }

    public static void setPreviousResourceName(String previousResourceName) {
        RemoteResourceDialog.previousResourceName = previousResourceName;
    }

    public String getCurrentResourceName() {
        return currentResourceName;
    }

    public String getCurrentURL() {
        return currentURL;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void updateCurrentResourceStatus(String resourceName, boolean status){
         RemoteResource rr = remoteResourceManager.getSelectedResouceByName(
                resourceName);
        if (rr != null) {
        	currentURL =  rr.getUri(); 
            currentPortnumber = rr.getPortnumber();
            currentUser = rr.getUsername();
            currentPassword = rr.getPassword();
            isSourceDirty =  status;
            rr.setDirty(status);
    } }

    /**Set up system.property
     * setupCurrentResource
     */
    public void setupSystemPropertyForCurrentResource(String resourceName) {
        RemoteResource rr = remoteResourceManager.getSelectedResouceByName(
                resourceName);
        if (rr != null) {
            currentURL =  rr.getUri(); 
            currentPortnumber = rr.getPortnumber();
            currentUser = rr.getUsername();
            currentPassword = rr.getPassword();
            isSourceDirty =  rr.isDirty();
        }
    }

    public boolean isSourceDirty() {
        return isSourceDirty;
    }

    public void setSourceDirty(boolean sourceDirty) {

        isSourceDirty = sourceDirty;
        RemoteResource rr = remoteResourceManager.getSelectedResouceByName(currentResourceName);
        if (rr != null) {
            rr.setDirty(sourceDirty);
        }
    }

    public void jButton6_actionPerformed(ActionEvent e) {
        //dirty = false;
        currentResourceName = previousResourceName;
        dispose();
    }

    /**
     * removeResourceByName
     *
     * @param deleteResourceStr String
     */
    public void removeResourceByName(String deleteResourceStr) {
        remoteResourceManager.deleteRemoteResource(deleteResourceStr);
        remoteResourceManager.getFirstItemName();

    }


}


class RemoteResourceDialog_jButton6_actionAdapter implements ActionListener {
    private RemoteResourceDialog adaptee;
    RemoteResourceDialog_jButton6_actionAdapter(RemoteResourceDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton6_actionPerformed(e);
    }
}

class RemoteResourceDialog_jComboBox1_actionAdapter implements ActionListener {
    private RemoteResourceDialog adaptee;
    RemoteResourceDialog_jComboBox1_actionAdapter(RemoteResourceDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jComboBox1_actionPerformed(e);
    }
}