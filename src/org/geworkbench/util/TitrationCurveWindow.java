package org.geworkbench.util;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel; 
import java.awt.Graphics;
 
import java.awt.image.BufferedImage;
import java.awt.Dimension;  
import java.io.File;
import java.io.IOException; 
import javax.imageio.ImageIO;

   
 

/**
 * 
 * @author my2248
 * @version $Id: NetworkRedrawWindow.java 9734 2012-07-24 14:24:57Z zji $
 */
public class TitrationCurveWindow {

	private JFrame frame;
	private JPanel topPanel;
    private Long levelTwoId;
	
	public TitrationCurveWindow(Long levelTwoId) {
		this.levelTwoId = levelTwoId;
		initComponents();
	}

	/**
	 * Set up the GUI
	 * 
	 * @param void
	 * @return void
	 */
	private void initComponents() {
		frame = new JFrame("Titration Curve");
		String imageFile = "classes" + FilePathnameUtils.FILE_SEPARATOR + "images" + FilePathnameUtils.FILE_SEPARATOR
		+ "titration_curve.png";
		topPanel = new ImagePanel(imageFile);

		
		
		Container frameContentPane = frame.getContentPane();
		frameContentPane.setLayout(new BorderLayout());

		frameContentPane.add(topPanel);

		frame.pack();
		frame.setLocationRelativeTo(frame.getOwner());

		topPanel.setVisible(true);

		frame.setVisible(true);

	}	 
	
	
	private class ImagePanel extends JPanel{

	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage image;

	    public ImagePanel(String fileName) {
	       try {                
	          image = ImageIO.read(new File(fileName));
	          Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));  
	          setPreferredSize(size);  
	          setMinimumSize(size);  
	          setMaximumSize(size);  
	          setSize(size);  
	          setLayout(null);  
	          
	       } catch (IOException ex) {
	            // handle exception...
	       }
	    }

	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
	    }

	}

}
