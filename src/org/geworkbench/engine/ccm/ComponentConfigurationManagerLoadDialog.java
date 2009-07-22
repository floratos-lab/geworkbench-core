package org.geworkbench.engine.ccm;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.engine.ccm.ComponentConfigurationManagerWindow.CCMTableModel;

/**
 * 
 * @author tg2321
 * @version $Id: ComponentConfigurationManagerLoadDialog.java,v 1.2 2009-07-22 15:34:32 jiz Exp $
 * 
 */
class ComponentConfigurationManagerLoadDialog extends JDialog implements
		ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = 8658994932771598295L;

	private Log log = LogFactory.getLog(this.getClass());

	private JTextPane textPane;
	private JScrollPane scrollPane;

	private CCMTableModel ccmTableModel;
	private List<String> required = null;
	private int selectedRow = -1;

	private JOptionPane optionPane;

	private String btnString1 = "Continue";
	private String btnString2 = "Cancel";

	/**
	 * Constructor
	 * 
	 * @param aFrame
	 * @param selectedRow
	 * @param required
	 * @param related
	 * @param parent
	 */
	public ComponentConfigurationManagerLoadDialog(Frame aFrame,
			int selectedRow, List<String> required, List<String> related,
			CCMTableModel parent) {

		super(aFrame, true);
		this.ccmTableModel = parent;
		this.required = required;
		this.selectedRow = selectedRow;

		String pluginName = "Plugin is missing a Name";
		if (selectedRow >= 0) {
			pluginName = (String) ccmTableModel.getModelValueAt(selectedRow,
					CCMTableModel.NAME_INDEX);
		}

		setTitle("Plugin upload dialog");

		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFocusable(false);

		ArrayList<String> requireAndRelated = new ArrayList<String>();
		/* Format Required Plugins */
		for (int i = 0; i < required.size(); i++) {
			String requiredClazz = required.get(i);
			int requiredRow = ccmTableModel.getModelRowByClazz(requiredClazz);

			String requiredName = "missing Class description";
			if (requiredRow >= 0) {
				requiredName = (String) ccmTableModel.getModelValueAt(requiredRow,
						CCMTableModel.NAME_INDEX);
			}

			String req = "";
			req += requiredName;
			req += " (required)";
			requireAndRelated.add(req);
		}
		/* Format Related Plugins */
		for (int i = 0; i < related.size(); i++) {
			String relatedClazz = related.get(i);
			int relatedRow = ccmTableModel.getModelRowByClazz(relatedClazz);

			String relatedName = "missing Class description";
			if (relatedRow >= 0) {
				relatedName = (String) ccmTableModel.getModelValueAt(relatedRow,
						CCMTableModel.NAME_INDEX);
			}
			requireAndRelated.add(relatedName);
		}
		String message = "The following is a list of plugins known to be\n";
		message += "compatible with the plugin you have chosen to load\n";
		message += "(" + pluginName + "):\n\n";

		for (int i = 0; i < requireAndRelated.size(); i++) {
			message += "* " + requireAndRelated.get(i) + "\n";
		}
		message += "\nIf you choose the \"Continue\" button below, those\n";
		message += "plugins marked as \"required\" will be automatically\n";
		message += "selected in your Component Configuration Manager\n";
		message += "window and will be uploaded in the application along\n";
		message += "with the plugin you selected.\n";
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

		/* Handle window closing correctly. */
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window, we're going to change
				 * the JOptionPane's value property.
				 */

				// TODO test this
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		optionPane.addPropertyChangeListener(this);
	}

	// MAYBE I SHOULD USE THIS LISTENER TO MAKE SURE ALL CHECK BOX CLICKS ARE
	// CAPTURED!
	// /** This method handles events for the text field. */
	/**
	 * @param e
	 * @return void
	 */
	public void actionPerformed(ActionEvent e) {
		// optionPane.setValue(btnString1);

		log.debug("LoadDialog.actionPerformed");
	}

	/**  
	 *	This method reacts to state changes in the option pane.
	 * 
	 * @param e
	 * @return void 
	 */
	public void propertyChange(PropertyChangeEvent e) {
		log.debug("LoadDialog.propertyChange");

		String prop = e.getPropertyName();

		if (!isVisible()) {
			return;
		}
		if ((e.getSource() != optionPane)) {
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

			for (int i = 0; i < this.required.size(); i++) {
				String requiredClazz = this.required.get(i);
				int requiredRow = ccmTableModel.getModelRowByClazz(requiredClazz);
				if (requiredRow < 0) {
					log.error("Missing Class in Plugin");
				}

				boolean successful = ccmTableModel.setModelValueAt(new Boolean(true), requiredRow,
						CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);

				/* If a license is not agreed to for a dependent component, then roll back all the selections for all components */
				if (!successful){
					// roll back selections
					if (this.selectedRow >= 0) {
						Boolean selection = (Boolean) ccmTableModel.getValueAt(
								this.selectedRow, CCMTableModel.SELECTION_INDEX);
						Boolean reset = new Boolean(!selection.booleanValue());
						ccmTableModel.setModelValueAt(reset, this.selectedRow,
								CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);
					} else {
						log.error("Missing Class in Plugin");
					}
				}
			}
		} else {
			if (this.selectedRow >= 0) {
				Boolean selection = (Boolean) ccmTableModel.getModelValueAt(
						this.selectedRow, CCMTableModel.SELECTION_INDEX);
				Boolean reset = new Boolean(!selection.booleanValue());
				ccmTableModel.setModelValueAt(reset, this.selectedRow,
						CCMTableModel.SELECTION_INDEX, CCMTableModel.NO_VALIDATION);
			} else {
				log.error("Missing Class in Plugin");
			}
		}
		clearAndHide();
	}

	/**  
	 * This method clears the dialog and hides it.
	 */
	public void clearAndHide() {
		textPane.setText(null);
		setVisible(false);
	}
}