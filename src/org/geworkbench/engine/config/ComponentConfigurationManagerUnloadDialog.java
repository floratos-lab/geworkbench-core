package org.geworkbench.engine.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.config.ComponentConfigurationManagerWindow.CCMTableModel;

class ComponentConfigurationManagerUnloadDialog extends JDialog implements
		ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = 8621083207798090210L;

	private Log log = LogFactory.getLog(this.getClass());

	private JTextPane textPane;
	private JScrollPane scrollPane;

	private CCMTableModel ccmTableModel;
	private int unselectedRow = -1;
	private List<Integer> dependentPlugins = null;

	private JOptionPane optionPane;

	private String btnString1 = "Continue";
	private String btnString2 = "Cancel";

	/**
	 * Constructor
	 * 
	 * @author tg2321
	 * @version $Id: ComponentConfigurationManagerUnloadDialog.java,v 1.2 2009-06-09 20:27:28 keshav Exp $
	 */
	public ComponentConfigurationManagerUnloadDialog(Frame aFrame,
			int unselectedRow, List<Integer> dependentPlugins,
			CCMTableModel parent) {

		super(aFrame, true);
		this.ccmTableModel = parent;
		this.unselectedRow = unselectedRow;
		this.dependentPlugins = dependentPlugins;

		setTitle("Plugin unload dialog");
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFocusable(false);

		String unselectedPluginName = "Plugin is missing a Name descriptor";
		if (unselectedRow >= 0) {
			unselectedPluginName = (String) ccmTableModel.getModelValueAt(
					unselectedRow, CCMTableModel.NAME_INDEX);
		}

		String message = "The following is a list of plugins known to be\n";
		message += "dependent on the plugin you have chosen to unload\n";
		message += "(" + unselectedPluginName + "):\n\n";
		
		for (int i = 0; i < dependentPlugins.size(); i++) {
			Integer dependentRow = dependentPlugins.get(i);
			int row = dependentRow.intValue();
			String dependentName = (String) ccmTableModel.getModelValueAt(row,
					CCMTableModel.NAME_INDEX);

			message += "* " + dependentName + "\n";
		}
		message += "\nIf you choose the \"Continue\" button below, \n";
		message += "the listed plugins will be automatically be unselected\n";
		message += "in your Component Configuration Manager window\n";
		message += "and will be unloaded in the application along\n";
		message += "with the plugin you unselected.\n";
		textPane.setText(message);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textPane);
		Object[] array = { scrollPane };

		/*
		 * Create an array specifying the number of dialog buttons and their
		 * text.
		 */
		Object[] options = { btnString1, btnString2 };

		/* Create the JOptionPane. */
//		optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
//				JOptionPane.YES_NO_OPTION, null, options, options[0]);

		// Put default back in after keyboard events are captured.
		optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options, null);

		/* Make this dialog display the JOptionPane */
		setContentPane(optionPane);

		// Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to change
				 * the JOptionPane's value property.
				 */

				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		optionPane.addPropertyChangeListener(this);
	}

	// TODO check this listener out to see if it captures events that
	// propertyChanges
	// is not catching
	/**
	 * @param ActionEvent
	 * @return void
	 */
	public void actionPerformed(ActionEvent e) {
		// optionPane.setValue(btnString1);

//		log.debug("UnloadDialog.actionPerformed");
	}

	/**  
	 * This method reacts to state changes in the option pane.
	 * 
	 * @param PropertyChangeEvent
	 * @return void
	 */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (!isVisible()) {
			return;
		}
		if (e.getSource() != optionPane) {
			return;
		}
		if (!(JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
				.equals(prop))) {
			return;
		}

		Object value = optionPane.getValue();

		if (value == JOptionPane.UNINITIALIZED_VALUE) {
			// ignore reset
			return;
		}

		// Reset the JOptionPane's value.
		// If you don't do this, then if the user
		// presses the same button next time, no
		// property change event will be fired.
		optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

		if (btnString1.equals(value)) {

			for (int i = 0; i < this.dependentPlugins.size(); i++) {
				Integer dependentRow = this.dependentPlugins.get(i);
				int row = dependentRow.intValue();
				ccmTableModel.setModelValueAt(new Boolean(false), row,
						CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);
			}
		} else { // user closed dialog or clicked cancel
			if (this.unselectedRow > 0) {
				Boolean selection = (Boolean) ccmTableModel.getValueAt(
						this.unselectedRow, CCMTableModel.SELECTION_INDEX);
				Boolean reset = new Boolean(!selection.booleanValue());
				ccmTableModel.setModelValueAt(reset, this.unselectedRow,
						CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);
			} else {
				log.error("Missing Class in Plugin");
			}
		}
		clearAndHide();
		// } //if
	}

	/**  
	 * This method clears the dialog and hides it.
	 * 
	 * @param void
	 * @return void
	 */
	public void clearAndHide() {
		textPane.setText(null);
		setVisible(false);
	}

}