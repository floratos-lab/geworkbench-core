package org.geworkbench.engine.ccm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.events.ComponentConfigurationManagerUpdateEvent;

/**
 * 
 * A menu for the Component Configuration Manager
 * 
 * @author keshav
 * @author tg2321
 * @version $Id: ComponentConfigurationManagerMenu.java,v 1.1.2.4 2009/03/06
 *          16:27:29 keshav Exp $
 */
public class ComponentConfigurationManagerMenu implements MenuListener {

	public ActionListener getActionListener(String var) {

		final ComponentConfigurationManagerMenu m = this;

		if (var.equalsIgnoreCase("Tools.Component Configuration")) {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ComponentConfigurationManagerWindow.load(m);
				}
			};
		}
		return null;
	}
}
