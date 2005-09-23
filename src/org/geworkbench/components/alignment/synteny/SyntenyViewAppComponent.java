package org.geworkbench.components.alignment.synteny;

import javax.swing.JPanel;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.*;
import javax.swing.JTabbedPane;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.config.events.EventSource;
import org.geworkbench.engine.config.MenuListener;
import java.awt.event.ActionListener;

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
public class SyntenyViewAppComponent extends EventSource
    implements VisualPlugin, MenuListener {
    JPanel jPanel1 = new JPanel();
    HashMap listeners = new HashMap();
    BorderLayout borderLayout1 = new BorderLayout();
    JTabbedPane jTabbedPane1 = new JTabbedPane();
    SyntenyMapViewWidget SMVW = new SyntenyMapViewWidget();
    DotMatrixViewWidget DMVW = new DotMatrixViewWidget();

    public SyntenyViewAppComponent() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        jPanel1.setLayout(borderLayout1);
        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
        jTabbedPane1.add(DMVW,"Dotmatrix");
        jTabbedPane1.add(SMVW,"Synteny Map");
    }

    public Component getComponent(){
        return jPanel1;
}
    public ActionListener getActionListener(String key){
      return (ActionListener)listeners.get(key);
  }
}


