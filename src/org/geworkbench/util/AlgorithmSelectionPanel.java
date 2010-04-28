package org.geworkbench.util;

import javax.swing.*;
import java.awt.*;

/**
 * Algorithm selection panel.
 * 
 * <p>Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @version $Id$
 */
public class AlgorithmSelectionPanel extends JPanel {
	private static final long serialVersionUID = -1469488867599091231L;
	
	//algorithm names
    public static final String DISCOVER = "discovery";
    public static final String EXHAUSTIVE = "exhaustive";

    private ButtonGroup algorithmGroup = new ButtonGroup();
    private JRadioButton discovery = new JRadioButton("Norm.");
    private JRadioButton exhaustive = new JRadioButton("Exhaust.");

    public AlgorithmSelectionPanel() {
        discovery.setActionCommand(DISCOVER);
        exhaustive.setActionCommand(EXHAUSTIVE);
        discovery.setSelected(true);

        algorithmGroup.add(discovery);
        algorithmGroup.add(exhaustive);

        add(discovery);
        add(exhaustive);
        
        this.setBorder(null);
        setMaximumSize(new Dimension(270, 20));
        setMinimumSize(new Dimension(270, 20));
        setPreferredSize(new Dimension(270, 20));
        this.setLayout(new FlowLayout());
        exhaustive.setBorder(null);
        discovery.setBorder(null);
    }

    public String getSelectedAlgorithmName() {
        return algorithmGroup.getSelection().getActionCommand();
    }

    public void setSelectedAlgorithm(String algorithmDescription) {
        if (algorithmDescription.equalsIgnoreCase(DISCOVER)) {
            discovery.setSelected(true);
        } else if (algorithmDescription.equalsIgnoreCase(EXHAUSTIVE)) {
            exhaustive.setSelected(true);
        }
    }
}
