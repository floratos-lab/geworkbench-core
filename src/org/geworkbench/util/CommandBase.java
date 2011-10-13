package org.geworkbench.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.geworkbench.builtin.projects.PendingTreeNode;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.config.rules.MalformedMenuItemException;
import org.geworkbench.engine.config.rules.NotMenuListenerException;
import org.geworkbench.engine.config.rules.NotVisualPluginException;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.skin.Skin;

/**
 * Base class for command panels: AnalysisPanel, FilteringPanel, NormalizationPanel
 * @author mw2518
 * $Id$
 */
public class CommandBase implements MenuListener{
	protected static final String topMenuItem = "Commands";
	protected String popMenuItem = "";
	protected HashMap<String, ActionListener> listeners = new HashMap<String, ActionListener>();
	protected JComboBox pluginComboBox = new JComboBox();

	protected void setMenuItem(String name) throws NotMenuListenerException, NotVisualPluginException, MalformedMenuItemException{
		String menuName = topMenuItem + GeawConfigObject.menuItemDelimiter + popMenuItem + GeawConfigObject.menuItemDelimiter + name;
		if (listeners.get(menuName)==null)
			listeners.put(menuName, new ActionListener(){
				public void actionPerformed(ActionEvent e){
					pluginComboBox.setSelectedItem(e.getActionCommand());
					Skin skin = (Skin) GeawConfigObject.getGuiWindow();
					skin.undockCommandPanel(popMenuItem, e.getActionCommand(), pluginComboBox);
				}
			});
		PluginDescriptor pluginDesc = ComponentRegistry.getRegistry().getDescriptorForPlugin(this);
		if (pluginDesc!=null)
			PluginObject.registerMenuItem(pluginDesc, menuName, "always", menuName, null, null);
	}

	protected void clearMenuItems(){
    	MenuElement[] elements = GeawConfigObject.getMenuBar().getSubElements();
    	for (MenuElement element: elements){
    		JMenu menu = (JMenu)element.getComponent();
    		if (menu.getText().equals(topMenuItem)){
    			JPopupMenu popMenu = menu.getPopupMenu();
    			MenuElement[] subelements = popMenu.getSubElements();
    			for (MenuElement subelement: subelements){
    				JMenuItem submenu = (JMenuItem)subelement.getComponent();
    				if (submenu.getText().equals(popMenuItem)){
    					popMenu.remove(submenu);
    				}
    			}
    			break;
    		}
    	}
    }

	protected boolean pendingNodeSelected(){
		return ProjectPanel.getInstance().getSelection().getSelectedNode() instanceof PendingTreeNode;
	}

	public ActionListener getActionListener(String var) {
		return listeners.get(var);
	}
}