package org.geworkbench.builtin.projects.remoteresources;

import java.awt.BorderLayout;

import javax.swing.*;

import com.borland.jbcl.layout.*;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RemoteResourceDialog extends JDialog {
    private static RemoteResourceManager remoteResourceManager = new
            RemoteResourceManager();
    private static RemoteResourceDialog dialog;
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int EDIT = 2;
    public static int currentOption = 0;
    public static String currentResourceName;
    public RemoteResourceDialog() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getItem() {
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


    }

    private void jbInit() throws Exception {
        jLabel1.setText("Below is the information:");
        jLabel2.setText("Port:");
        jLabel3.setText("User Name:");
        jPanel2.setLayout(boxLayout21);
        boxLayout21.setAxis(BoxLayout.Y_AXIS);
        jTextField1.setToolTipText("");
        jTextField1.setText("Manju");
        jLabel4.setText("URL:");
        jPasswordField1.setText("jPasswordField1");
        this.getContentPane().setLayout(xYLayout1);
        jPanel1.setLayout(borderLayout1);
        jButton3.setText("Delete");
        jButton4.setText("Add");
        jButton4.addActionListener(new
                                   RemoteResourceDialog_jButton4_actionAdapter(this));
        jLabel5.setText("Password: ");
        jTextField2.setText("www.columbia.edu");
        jTextField3.setText("80");
        jButton1.setText("Update");
        jButton1.addActionListener(new
                                   RemoteResourceDialog_jButton1_actionAdapter(this));
        jButton2.setText("Yes");
        jButton5.setText("No");
        jButton6.setText("Cancel");
        jPanel4.setBorder(border1);
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel6.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel6.setText("Protocol:");
        jComboBox1.addActionListener(new
                                     RemoteResourceDialog_jComboBox1_actionAdapter(this));

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);
        jPanel3.add(jLabel2);
        jPanel3.add(jTextField3);
        jPanel3.add(jLabel6);
        jPanel3.add(jComboBox1);
        jPanel2.add(jPanel5);
        jPanel5.add(jLabel4);
        jPanel5.add(jTextField2);
        jPanel2.add(jPanel3);
        jPanel2.add(jPanel4);
        jPanel4.add(jLabel3);
        jPanel4.add(jTextField1);
        jPanel4.add(jLabel5);
        jPanel4.add(jPasswordField1);
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);
        this.getContentPane().add(jPanel6, new XYConstraints(1, 185, 255, 67));
        jPanel6.add(jButton4);
        jPanel6.add(jButton3);
        jPanel6.add(jButton1);
        jPanel6.add(jButton2);
        jPanel6.add(jButton5);
        jPanel6.add(jButton6);
        this.getContentPane().add(jPanel1, new XYConstraints(0, 0, -1, -1));
        pack();
    }

    JLabel jLabel1 = new JLabel();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JPanel jPanel3 = new JPanel();
    JPanel jPanel4 = new JPanel();
    BoxLayout2 boxLayout21 = new BoxLayout2();
    JTextField jTextField1 = new JTextField();
    JPanel jPanel5 = new JPanel();
    JLabel jLabel4 = new JLabel();
    JPasswordField jPasswordField1 = new JPasswordField();
    XYLayout xYLayout1 = new XYLayout();
    JPanel jPanel6 = new JPanel();
    JButton jButton3 = new JButton();
    JButton jButton4 = new JButton();
    JLabel jLabel5 = new JLabel();
    JTextField jTextField2 = new JTextField();
    JTextField jTextField3 = new JTextField();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JButton jButton5 = new JButton();
    JButton jButton6 = new JButton();
    Border border1 = BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
            Color.white, new Color(165, 163, 151));
    JLabel jLabel6 = new JLabel();
    JComboBox jComboBox1 = new JComboBox(new String[] {"HTTP", "RMI"});

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

    public void jComboBox1_actionPerformed(ActionEvent e) {

    }

    public void jButton4_actionPerformed(ActionEvent e) {
        remoteResourceManager.addRemoteResource(collectResourceInfo());
    }

    public RemoteResource collectResourceInfo() {
        RemoteResource rr = new RemoteResource();
        rr.setConnectProtocal(jComboBox1.getSelectedItem().toString());
        rr.setPassword(jPasswordField1.getSelectedText());
        rr.setUsername(jTextField1.getText());

        rr.setUri( jTextField2.getText());
//        try {
//            rr.setUri(new URL(jTextField2.getText()));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        rr.setShortname("now");
        return rr;
    }


    public void setFields(RemoteResource rr) {
        jPasswordField1.setText(rr.getPassword());
        jTextField1.setText(rr.getUsername());
        jTextField2.setText(rr.getUri().toString());
        jComboBox1.setSelectedItem(rr.getConnectProtocal());
    }

    public void jButton1_actionPerformed(ActionEvent e) {
        //remoteResourceManager.update();
    }

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static String showDialog(Component frameComp,

                                    String title,
                                    int option,
                                    String initialValue
                                ) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new RemoteResourceDialog(frame,
                                title,
                                option,
                                initialValue);
        dialog.setVisible(true);
        return null;
    }

}


class RemoteResourceDialog_jButton1_actionAdapter implements ActionListener {
    private RemoteResourceDialog adaptee;
    RemoteResourceDialog_jButton1_actionAdapter(RemoteResourceDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}


class RemoteResourceDialog_jButton4_actionAdapter implements ActionListener {
    private RemoteResourceDialog adaptee;
    RemoteResourceDialog_jButton4_actionAdapter(RemoteResourceDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButton4_actionPerformed(e);
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
