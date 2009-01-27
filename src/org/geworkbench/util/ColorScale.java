package org.geworkbench.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Color scale shown for heat-map type display.
 * 
 * @author zji
 * 
 */
public class ColorScale extends JPanel {
	private static final long serialVersionUID = 2426481070361078667L;

	/**
	 * Constructor with three color points to control the gradient along the scale.
	 * 
	 * @param minColor - the color at the most left side of the scale
	 * @param centerColor - the color at the center of the scale
	 * @param maxColor - the color at the most right side of the scale
	 */
	public ColorScale(Color minColor, Color centerColor, Color maxColor) {
		super();
		this.minColor = minColor;
		this.maxColor = maxColor;
		this.centerColor = centerColor;

		add(new JLabel("-"));
		add(Box.createHorizontalStrut(2), null);
		add(new ColorGradient());
		add(Box.createHorizontalStrut(2), null);
		add(new JLabel("+"));
	}

	private Color minColor, maxColor, centerColor;

	public Color getMinColor() {
		return minColor;
	}

	public void setMinColor(Color minColor) {
		this.minColor = minColor;
	}

	public Color getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(Color maxColor) {
		this.maxColor = maxColor;
	}

	public Color getCenterColor() {
		return centerColor;
	}

	public void setCenterColor(Color centerColor) {
		this.centerColor = centerColor;
	}

	private class ColorGradient extends JComponent {
		private static final long serialVersionUID = -2812620840276271928L;
		
		private Dimension d = new Dimension(100, 20);

		private ColorGradient() {
			this.setMaximumSize(d);
			this.setPreferredSize(d);
			this.setMinimumSize(d);
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(new GradientPaint(0, 0, minColor, getWidth() / 2, 0,
					centerColor));
			g2d.fillRect(0, 0, getWidth() / 2, getHeight());
			g2d.setPaint(new GradientPaint(getWidth() / 2, 0, centerColor,
					getWidth(), 0, maxColor));
			g2d.fillRect(getWidth() / 2, 0, getWidth(), getHeight());
		}

	}
}