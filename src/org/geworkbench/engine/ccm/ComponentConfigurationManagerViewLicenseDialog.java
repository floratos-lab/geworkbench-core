package org.geworkbench.engine.ccm;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * 
 * @author tg2321
 * @version 1.0
 */
class ComponentConfigurationManagerViewLicenseDialog extends JDialog {

	private static final long serialVersionUID = -2167359644403976588L;

	private JTextPane textPane;
	private JScrollPane scrollPane;

	/**
	 * 
	 * @param aFrame
	 * @param name
	 * @param license
	 */
	public ComponentConfigurationManagerViewLicenseDialog(Frame aFrame,
			String name, String license) {
		super(aFrame, true);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setText(license);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textPane);

		setTitle(name + " License");
		this.setContentPane(scrollPane);
	}

//	/**
//	 * 
//	 */
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//	}
//
//	/**
//	 * This method clears the dialog and hides it.
//	 * 
//	 * @param void
//	 * @return void
//	 */
//	public void clearAndHide() {
//		// textPane.setText(null);
//		setVisible(false);
//	}

}