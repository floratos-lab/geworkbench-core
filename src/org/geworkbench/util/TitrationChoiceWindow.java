package org.geworkbench.util;

 
import java.awt.BorderLayout; 
import java.awt.Container; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent; 

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JPanel;
import javax.swing.JRadioButton;
 
import org.geworkbench.util.TitrationCurveWindow;
 
/**
 * 
 * @author my2248
 * @version $Id: NetworkRedrawWindow.java 9734 2012-07-24 14:24:57Z zji $
 */
public class TitrationChoiceWindow {

	private JFrame frame;
	private JPanel topPanel;
	private JPanel bottompanel;
	private static JRadioButton titrationCurveRB = new JRadioButton("Titration Curve");
	private JRadioButton isobologramRB = new JRadioButton("Isobologram");
	private JButton continueButton = new JButton("Continue");

	private JButton cancelButton = new JButton("Cancel");
	private static Long levelTwoId;

	private static TitrationChoiceWindow titrationChoiceWindow = null;

	private TitrationChoiceWindow() {

		initComponents();
	}

	/**
	 * Load method
	 */
	public static void load(Long titrationId) {
		if (titrationChoiceWindow == null)
			titrationChoiceWindow = new TitrationChoiceWindow();
		levelTwoId = titrationId;
		titrationCurveRB.setSelected(true);
		titrationChoiceWindow.frame.toFront();
	}

	/**
	 * Set up the GUI
	 * 
	 * @param void
	 * @return void
	 */
	private void initComponents() {
		frame = new JFrame("Select Graph Type");

		topPanel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		group.add(titrationCurveRB);
		group.add(isobologramRB);

		topPanel.add(new JLabel("                     "));
		topPanel.add(titrationCurveRB);
		topPanel.add(new JLabel("       "));
		topPanel.add(isobologramRB);
		topPanel.add(new JLabel("                      "));

		bottompanel = new JPanel();
		bottompanel.add(continueButton);
		bottompanel.add(cancelButton);

		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				continueButtonActionPerformed();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				levelTwoId = null;
				titrationChoiceWindow = null;
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				titrationChoiceWindow.frame.dispose();
				levelTwoId = null;
				titrationChoiceWindow = null;
			}
		});

		// ======== frame ========
		{
			Container frameContentPane = frame.getContentPane();
			frameContentPane.setLayout(new BorderLayout());

			// ======== outerPanel ========
			{

				frameContentPane
						.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
							public void propertyChange(
									java.beans.PropertyChangeEvent e) {
								if ("border".equals(e.getPropertyName()))
									throw new RuntimeException();
							}
						});

				frameContentPane.add(topPanel, BorderLayout.NORTH);

				frameContentPane.add(bottompanel, BorderLayout.SOUTH);
			} // ======== outerPanel ========
			frame.pack();
			frame.setLocationRelativeTo(frame.getOwner());
		} // ============ frame ============

		topPanel.setVisible(true);

		bottompanel.setVisible(true);

		frame.setVisible(true);

	}

	/**
	 * redrawNetwork action
	 * 
	 * @param ActionEvent
	 * @return void
	 */
	private void continueButtonActionPerformed() {

		     new TitrationCurveWindow(levelTwoId);
	}

}