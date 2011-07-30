package org.geworkbench.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
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
    private int type = BOUNDED_TYPE;
    /**
     * Visual widget
     */
    private JProgressBar jProgressBar;
    /**
     * Visual widget
     */
    private JButton cancelButton;
    private JLabel jLabel1;
    private JPanel jp1 = new JPanel();
    private JPanel jp2 = new JPanel();
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
            //jProgressBar.setStringPainted(true);
        }

        else if (t == INDETERMINATE_TYPE) {
            type = t;
            jProgressBar.setIndeterminate(true);
            jProgressBar.setStringPainted(false);
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

        cancelButton = new JButton(new ImageIcon("classes/images/ButtonClose.png"));
        cancelButton.setToolTipText("Cancel");
        cancelButton.setContentAreaFilled(false);
        cancelButton.setRequestFocusEnabled(true);

        //this.setLayout(new GridBagLayout());
        jLabel1 = new JLabel();
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Message");
        jLabel1.setVerticalTextPosition(SwingConstants.CENTER);

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
     * Sets the bounds if type == BOUNDED_TYPE
     * @param brm <code>BoundedRangeModel</code> encapsulating range and extent
     * of values to be used for animation
     */
    public void setBounds(BoundedRangeModel brm) {
        if (type == BOUNDED_TYPE) {
            jProgressBar.setModel(brm);
        }
    }

    /**
     * Updates the ProgressBar by an amount specified in the <code>
     * BoundedRangeModel</code>
     * @return status of updation
     */
    public boolean update() {
        if (type == BOUNDED_TYPE) {
            int inc = ( (IncrementModel) jProgressBar.getModel()).getIncrement();
            int value = jProgressBar.getValue();
            int nv = value + inc;
            if (nv <= jProgressBar.getMaximum()) {
                jProgressBar.setValue(value + inc);
                jProgressBar.setString(Integer.toString(value + inc));

                return true;
            }
        }

        return false;
    }

    /**
     * Updates the ProgressBar to an amount specified
     * @param value value that the <code>ProgressBar</code> has to be set to
     * @return status of updation if <code>value</code> is
     * outside the range in the <code> BoundedRangeModel</code>
     */
    public boolean updateTo(float value) {
        if (type == BOUNDED_TYPE) {
            if ((int) value <= jProgressBar.getMaximum()) {
                jProgressBar.setValue((int) value);
                jProgressBar.setString(Integer.toString((int) value));
                return true;
            }
        }
        return false;
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
     * Stops animation
     */
    public void stop() {
    	/*
    	JButton done = new JButton(new ImageIcon("classes/images/ButtonDone.png"));
    	done.setContentAreaFilled(false);
    	jp2.add(done);
    	jp2.revalidate();
    	this.remove(jp1);
    	*/
    	dispose();
    }
    public String getMessage(){
    	return jLabel1.getText();
    }

    /**
     * Resets the animation
     */
    public void reset() {
        updateTo(jProgressBar.getMinimum());
    }

    /**
     * Gets time period of animation
     * @return time period of animation in milli seconds
     */
    public double getDuration() {
        return 0d;
    }

    /**
     * Hides the <code>ProgressBar</code>
     */
    public void dispose() {
        this.setMessage("");
        this.setVisible(false);
    }

    /**
     * Sets the message
     * @param message to be shown on the <code>ProgressBar</code>
     */
    public void setMessage(String message) {
        jProgressBar.setToolTipText(message);
        jLabel1.setText(message);
    }

    /**
     * Bit to specify if the current value of the <code>ProgressBar</code> has
     * to be printed on the widget. Default is true.
     * @param show if current value and bounds have to printed
     */
    public void showValues(boolean show) {
        if (type == BOUNDED_TYPE) {
            jProgressBar.setStringPainted(show);
        }
    }

    /**
     * Utility class that encapsulates a step size for the
     * <code>ProgressBar</code> display
     */
    public static class IncrementModel
        extends DefaultBoundedRangeModel {
		private static final long serialVersionUID = 1L;
		private int increment = 0;
        public IncrementModel(int value, int extent, int min, int max, int inc) {
            super(value, extent, min, max);
            increment = inc;
        }

        public int getIncrement() {
            return increment;
        }

        public void setIncrement(int inc) {
            increment = inc;
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt){
		if("progress" == evt.getPropertyName()) {
			int progress = (Integer)evt.getNewValue();
			updateTo(progress);
		}
	}
}
