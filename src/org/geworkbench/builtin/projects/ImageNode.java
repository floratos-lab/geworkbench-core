package org.geworkbench.builtin.projects;

import java.awt.Component;

import javax.swing.ImageIcon;

import org.geworkbench.util.SaveImage;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>ImageNode</code> node which represents a 'Image' node in the Project
 * panel component
 *
 * @author First Genetic Trust
 * @version 1.0
 */
//ProjectTreeNode
public class ImageNode extends  DataSetSubNode {
    /**
     * 
     */
    private static final long serialVersionUID = 1741751707465515060L;
    /**
     * <code>ImageIcon</code> that this <code>ImageNode</code> refers to
     */
    public ImageIcon image;

    /**
     * Constructor
     *
     * @param im <code>ImageIcon</code> that this <code>ImageNode</code> refers to
     */
    public ImageNode(ImageData node) {
    	super(node);
    	image = node.getImageIcon();
        super.setUserObject(image.getDescription());
        node.setDescription(image.getDescription());
    }
    
    @Override
    protected void writeToFile(final boolean tabDelimited, final Component dialogParent) {
		new SaveImage(image.getImage()).save();
    }

}
