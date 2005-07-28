/*
 * Created on Sep 4, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package edu.ksu.cis.bnj.bbn.learning.scorebased.gradient;

import edu.ksu.cis.kdd.data.Table;
import edu.ksu.cis.kdd.util.gui.Optionable;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * @author Silpan Patel
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GreedySLOptionsGUI extends BaseOptionsGUI {


    /* (non-Javadoc)
     * @see edu.ksu.cis.bnj.gui.GenericOptionGUI#init()
     */
    protected void init() {

        super.init();
        mainPanel.setBorder(new TitledBorder("GreedySL options"));
        this.setContentPane(mainPanel);

    }

    public GreedySLOptionsGUI(Optionable o) {
        super(o);
    }

    public GreedySLOptionsGUI(Optionable o, JFrame owner) {
        super(o, owner);
    }


    public static void main(String[] args) {
        Table t = Table.load("examples/asia/asia1000data.arff");
        GreedySL sl = new GreedySL(t);
        sl.getOptionsDialog().setVisible(true);

    }

}
