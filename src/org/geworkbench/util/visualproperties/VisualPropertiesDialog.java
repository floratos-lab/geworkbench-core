package org.geworkbench.util.visualproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * @author John Watkinson
 * @version $Id$
 */
public class VisualPropertiesDialog extends JDialog {

	private static final long serialVersionUID = -1324174157721236396L;

	private boolean propertiesChanged = false;

    private JShapeButton display;
    private PanelVisualProperties properties;
    private PanelVisualProperties defaultProperties;
    private boolean defaults = false;
    
    // these variables store the change until they are saved when OK is clicked
    private Color transientColor;
    private int transientShapeIndex;

    public VisualPropertiesDialog(Frame owner, String title, Object item, int defaultIndex) throws HeadlessException {
        super(owner, title, true);
        propertiesChanged = false;
        
        // Look for existing visual properties
        PanelVisualPropertiesManager manager = PanelVisualPropertiesManager.getInstance();
        properties = manager.getVisualProperties(item);
        defaultProperties = manager.getDefaultVisualProperties(defaultIndex);
        if (properties == null) {
            properties = new PanelVisualProperties(defaultProperties.getShapeIndex(), defaultProperties.getColor());
            defaults = true;
        }
        
        transientColor= properties.getColor();
        transientShapeIndex = properties.getShapeIndex();

        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        display = new JShapeButton(properties.getShape(), properties.getColor());
        content.add(display, BorderLayout.CENTER);
        JPanel spacingPanel = new JPanel();
        spacingPanel.setLayout(new BoxLayout(spacingPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        spacingPanel.add(buttonPanel);
        spacingPanel.add(Box.createVerticalStrut(6));
        content.add(spacingPanel, BorderLayout.SOUTH);
        JButton changeColorButton = new JButton("Change Color...");
        JButton changeShapeButton = new JButton("Change Shape...");
        JButton defaultButton = new JButton("Default Color/Shape");
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(Box.createGlue());
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(changeColorButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(changeShapeButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(defaultButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(Box.createGlue());
        // Add behavior
        changeColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transientColor = JColorChooser.showDialog(VisualPropertiesDialog.this, "Choose Color", properties.getColor());
                if (transientColor != null) {
                    display.setPaint(transientColor);
                    defaults = false;
                    display.repaint();
                }
            }
        });
        changeShapeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JShapeDialog dialog = new JShapeDialog(VisualPropertiesDialog.this, "Choose Shape", PanelVisualProperties.AVAILABLE_SHAPES, properties.getColor(), properties.getShapeIndex());
                dialog.pack();
                dialog.setSize(400, 400);
                dialog.setVisible(true);
                transientShapeIndex = dialog.getResult();
                display.setShape(PanelVisualProperties.AVAILABLE_SHAPES[transientShapeIndex]);
                defaults = false;
                display.repaint();
            }
        });
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transientShapeIndex = defaultProperties.getShapeIndex();
                transientColor = defaultProperties.getColor();
                display.setShape(PanelVisualProperties.AVAILABLE_SHAPES[properties.getShapeIndex()]);
                display.setPaint(properties.getColor());
                defaults = true;
                display.repaint();
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                propertiesChanged = true;
                properties.setColor(transientColor);
                properties.setShapeIndex(transientShapeIndex);
                dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                propertiesChanged = false;
                dispose();
            }
        });
    }

    public boolean isPropertiesChanged() {
        return propertiesChanged;
    }

    public PanelVisualProperties getVisualProperties() {
        if (defaults) {
            return null;
        } else {
            return properties;
        }
    }

}
