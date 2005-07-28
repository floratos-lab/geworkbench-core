package edu.ksu.cis.kdd.util.gui;

/*
 * Created on Aug 6, 2003
 *
 * This file is part of Bayesian Network for Java (BNJ).
 *
 * BNJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * BNJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BNJ in LICENSE.txt file; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import javax.swing.*;
import java.awt.*;

/**
 * @author Roby Joehanes
 */
public class PlotterPanel extends JPanel {
    protected double[] data;
    protected double min, max;
    protected int length, minpos, maxpos;
    protected Color plotColor = Color.RED;
    protected Color frameColor = Color.BLACK;
    protected Color labelColor = Color.BLACK;
    protected Insets insets = new Insets(15, 40, 25, 40);

    public PlotterPanel(double[] d) {
        setData(d);
        setBackground(Color.WHITE);

    }

    public void setData(double[] d) {
        data = d;
        analyzeData();
    }

    protected void analyzeData() {
        assert (data != null);
        length = data.length;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            if (data[i] > max) {
                max = data[i];
                maxpos = i;
            } else if (data[i] < min) {
                min = data[i];
                minpos = i;
            }
        }
    }

    public void paint(Graphics g) {
        int max_x = getWidth();
        int max_y = getHeight();
        Color oldColor = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, max_x, max_y);

        max_x -= (insets.left + insets.right);
        max_y -= (insets.top + insets.bottom);
        double threshold = max - min;
        int height_0_75 = (max_y * 3) / 4 + insets.top;
        int height_0_50 = max_y / 2 + insets.top;
        int height_0_25 = max_y / 4 + insets.top;

        g.setColor(getFrameColor());
        g.drawRect(insets.left, insets.top, max_x, max_y);
        g.drawLine(insets.left, height_0_75, max_x + insets.left, height_0_75);
        g.drawLine(insets.left, height_0_50, max_x + insets.left, height_0_50);
        g.drawLine(insets.left, height_0_25, max_x + insets.left, height_0_25);
        g.setColor(getLabelColor());
        g.drawString(String.valueOf(max), 10, insets.top);
        g.drawString(String.valueOf(min), 10, max_y + insets.top);
        g.drawString(String.valueOf(threshold * 0.25 + min), 10, height_0_75);
        g.drawString(String.valueOf(threshold / 2 + min), 10, height_0_50);
        g.drawString(String.valueOf(threshold * 0.75 + min), 10, height_0_25);
        g.drawString(String.valueOf(0), insets.left, max_y + insets.top + (insets.bottom / 2));
        g.drawString(String.valueOf(length), max_x + insets.left, max_y + insets.top + (insets.bottom / 2));

        int old_x = insets.left, old_y = max_y + insets.top;
        g.setColor(getPlotColor());
        for (int i = 0; i < length; i++) {
            int x = ((i + 1) * max_x) / length;
            int y = (int) Math.round(max_y - ((data[i] - min) * max_y) / threshold);
            x += insets.left;
            y += insets.top;
            g.drawLine(old_x, old_y, x, y);
            old_x = x;
            old_y = y;
        }
        g.setColor(oldColor);
    }

    /**
     * @return Plot Color (Default = RED)
     */
    public Color getPlotColor() {
        return plotColor;
    }

    /**
     * Sets the plot color (Default = RED)
     *
     * @param plotColor
     */
    public void setPlotColor(Color plotColor) {
        this.plotColor = plotColor;
    }

    /**
     * @return
     */
    public Color getFrameColor() {
        return frameColor;
    }

    /**
     * @param frameColor
     */
    public void setFrameColor(Color frameColor) {
        this.frameColor = frameColor;
    }

    /**
     * @return
     */
    public Color getLabelColor() {
        return labelColor;
    }

    /**
     * @param labelColor
     */
    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    //    public static void main(String[] args) {
    //        double[] d = { 1, 3, 2, 5, 2, 0, 4, 7, 5};
    //        PlotterPanel p = new PlotterPanel(d);
    //        JFrame j = new JFrame();
    //        j.setContentPane(p);
    //        j.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //        j.setVisible(true);
    //    }
}
