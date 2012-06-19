package org.geworkbench.engine.config;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.sun.java.swing.Painter;

public class SimpleCustomizedIndeterminateProgressBarPainter implements Painter<JComponent> {

	final private Color color;
	final private float percentage;

	SimpleCustomizedIndeterminateProgressBarPainter(Color color, float percentage) {
		this.color = color;
		this.percentage = percentage;
	}

	@Override
	public void paint(Graphics2D paramGraphics2D, JComponent paramT, int width,
			int height) {
		paramGraphics2D.setColor(color);
		paramGraphics2D.fillRect(0, 0, (int) (width * percentage), height);
	}

}
