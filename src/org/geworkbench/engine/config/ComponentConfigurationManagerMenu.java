package org.geworkbench.engine.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.geworkbench.builtin.ComponentConfigurationManager;

/**
 * A menu to trigger the loading/removing of components.
 * 
 * @author zji
 * @version $Id: ComponentConfigurationManagerMenu.java,v 1.1 2009-02-13 15:48:21 keshav Exp $
 * 
 */
public class ComponentConfigurationManagerMenu implements MenuListener {

	public ActionListener getActionListener(String var) {
		if (var.equalsIgnoreCase("Tools.ComponentConfigurationManager")) {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String s = (String) JOptionPane.showInputDialog(null,
							"Component Resource Name",
							"ComponentConfigurationManager Dialog",
							JOptionPane.PLAIN_MESSAGE);

					// If a string was returned, say so.
					if ((s != null) && (s.length() > 0)) {
						String componentsDir = System
								.getProperty("components.dir");
						ComponentConfigurationManager manager = new ComponentConfigurationManager(
								componentsDir);
						manager.loadComponent(s);
					}

				}
			};
		}
		return null;
	}

}
