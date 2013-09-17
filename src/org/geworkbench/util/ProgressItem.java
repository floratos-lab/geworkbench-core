package org.geworkbench.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * Implements a generic progress item to show progress of an application task.
 * $Id$
 */

public class ProgressItem extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -4287056285516981445L;
	/**
     * Defines a ProgressBar that has bounds and values shown increment from a
     * minimum to a maximum
     */
    public static final int BOUNDED_TYPE = 0;
    /**
     * Defines a ProgressBar that animates cyclically till asked to stop
     */
    public static final int INDETERMINATE_TYPE = 1;

    /**
     * Instance type
     */
    final private int type;
    /**
     * Visual widget
     */
    private JProgressBar jProgressBar;

    private JLabel jLabel1;

    private ProgressTask<?,?> task;
    
    /**
     * Constructor for internal creation of instances. 
     * @param t type of the <code>ProgressBar</code>
     * @param sw task that this progress bar is monitoring
     */
    public ProgressItem(int t, ProgressTask<?,?> sw) {
    	task = sw;
    	jProgressBar = new JProgressBar();
        if (t == BOUNDED_TYPE) {
            type = t;
            jProgressBar.setIndeterminate(false);
        } else if (t == INDETERMINATE_TYPE) {
            type = t;
            jProgressBar.setIndeterminate(true);
            jProgressBar.setStringPainted(false);
        } else { // this should not happen
        	type = BOUNDED_TYPE;
        }

        try {
            jbInit();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method for constructing GUI
     * @throws Exception exception encountered while GUI construction
     */
    private void jbInit() throws Exception {
        jProgressBar.setBorder(BorderFactory.createLoweredBevelBorder());
        jProgressBar.setMinimumSize(new Dimension(300, 22));
        jProgressBar.setPreferredSize(new Dimension(300, 22));
        jProgressBar.setRequestFocusEnabled(true);
        jProgressBar.setBorderPainted(true);

        JButton cancelButton = new JButton(new ImageIcon("classes/images/ButtonClose.png"));
        cancelButton.setToolTipText("Cancel");
        cancelButton.setContentAreaFilled(false);
        cancelButton.setRequestFocusEnabled(true);

        //this.setLayout(new GridBagLayout());
        jLabel1 = new JLabel();
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Message");
        jLabel1.setVerticalTextPosition(SwingConstants.CENTER);

        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        jp1.add(jProgressBar);
        jp1.add(cancelButton);
        jp2.add(jLabel1);
        this.setLayout(new BorderLayout());
        this.add(jp1, BorderLayout.PAGE_START);
        this.add(jp2, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(300, 100));

        cancelButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		task.cancel(true);
        		task = null;
        	}
        });
    }

    /**
     * Updates the ProgressBar to an amount specified
     * @param value value that the <code>ProgressBar</code> has to be set to
     */
    private void updateTo(float value) {
        if (type == BOUNDED_TYPE) {
            if ((int) value <= jProgressBar.getMaximum()) {
                jProgressBar.setValue((int) value);
                jProgressBar.setString(Integer.toString((int) value));
            }
        }
    }

    /**
     * Starts animation and shows the <code>ProgressBar</code>
     */
    public void start() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
            (dim.width - this.getWidth()) / 2,
            (dim.height - this.getHeight()) / 2);
        this.setVisible(true);
        updateTo(jProgressBar.getMinimum());
    }

    /**
     * Sets the message
     * @param message to be shown on the <code>ProgressBar</code>
     */
    public void setMessage(String message) {
        jProgressBar.setToolTipText(message);
        jLabel1.setText(message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt){
		if("progress" == evt.getPropertyName()) {
			int progress = (Integer)evt.getNewValue();
			updateTo(progress);
		}
	}
}
