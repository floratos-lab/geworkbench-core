package edu.ksu.cis.bnj.gui.components;

/*
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

import edu.ksu.cis.kdd.util.Settings;
import salvo.jesus.graph.visual.VisualGraphComponent;
import salvo.jesus.graph.visual.VisualVertex;
import salvo.jesus.graph.visual.drawing.VisualVertexPainterImpl;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Custom Vertex Painter
 *
 * @author Roby Joehanes
 */
public abstract class VariablePainter extends VisualVertexPainterImpl {
    private static String ln = System.getProperty("line.separator"); // $NON-NLS-1$
    private static Hashtable settings = Settings.getWindowSettings("MAIN"); //$NON-NLS-1$

    protected Font font = (Font) settings.get("NodeFont"); //$NON-NLS-1$
    protected Color backgroundColor = (Color) settings.get("NodeBackground"); //$NON-NLS-1$
    protected Color normalOutlineColor = (Color) settings.get("NodeOutline"); //$NON-NLS-1$
    protected Color highlightOutlineColor = (Color) settings.get("NodeHighlight"); //$NON-NLS-1$
    protected Color fontColor = (Color) settings.get("NodeFontColor"); //$NON-NLS-1$
    protected Color outlineColor = normalOutlineColor;

    protected transient BasicStroke origStroke = new BasicStroke();
    protected transient BasicStroke fatStroke = new BasicStroke(origStroke.getLineWidth() * 3);

    protected double topBottomMargin = 10;
    protected double leftRightMargin = 15;


    /**
     * Constructor for VertexPainter.
     */
    public VariablePainter() {
        super();
    }

    /**
     * @see salvo.jesus.graph.visual.drawing.Painter#paint(salvo.jesus.graph.visual.VisualGraphComponent, java.awt.Graphics2D)
     */
    public void paint(VisualVertex vVertex, Graphics2D g2d, Shape shape) {
        vVertex.setFont(font);
        vVertex.setOutlinecolor(outlineColor);
        vVertex.setFillcolor(backgroundColor);
        Rectangle bounds = vVertex.getBounds();
        vVertex.setGeneralPath(new GeneralPath(shape));

        g2d.setStroke(fatStroke);
        super.paint(vVertex, g2d);
        // Restore the stroke to the default
        g2d.setStroke(origStroke);
    }

    /**
     * Paints the text of the <tt>VisualVertex</tt>.
     * This is borrowed from VisualVertexPainterImpl.java. It's because we can't set
     * the margin.
     */
    public void paintText(VisualGraphComponent component, Graphics2D g2d) {
        FontMetrics fontMetrics = component.getFontMetrics();
        VisualVertex vv = (VisualVertex) component;
        StringTokenizer strTokenizer;
        int line = 1;
        int lineHeight;
        Rectangle2D.Float bounds;

        lineHeight = fontMetrics.getHeight();

        bounds = (Rectangle2D.Float) vv.getGeneralPath().getBounds2D();

        g2d.setFont(vv.getFont());
        g2d.setColor(vv.getFontcolor());
        strTokenizer = new StringTokenizer(vv.getLabel(), ln);
        while (strTokenizer.hasMoreTokens()) {
            g2d.drawString(strTokenizer.nextToken(), (float) (bounds.x + this.leftRightMargin + 1), (float) (bounds.y + this.topBottomMargin + lineHeight * line - 2));
            line++;
        }
    }

    public void setColorToHighlight() {
        setOutlineColor(highlightOutlineColor);
    }

    public void setColorToNormal() {
        setOutlineColor(normalOutlineColor);
    }

    public void setOutlineColor(Color c) {
        outlineColor = c;
    }

    /**
     * Rescales the VisualVertex. It determines the height of the text to be painted
     * and adjusts the size of the GeneralPath so that the entire text fits in
     * the VisualVertex.
     * <p/>
     * This is borrowed from VisualVertexPainterImpl.java. It's because we can't set
     * the margin.
     */
    public void rescale(VisualVertex vv) {
        FontMetrics fontMetrics = vv.getFontMetrics();
        GeneralPath drawPath = vv.getGeneralPath();
        StringTokenizer strTokenizer;
        AffineTransform transform = new AffineTransform();
        Rectangle2D originalLocation;
        double scalex, scaley;
        int lineHeight;
        int height = 0, width, maxWidth = 0;

        lineHeight = fontMetrics.getHeight();

        // Since there is no setSize() method (or something similar)
        // for the class GeneralPath, we will transform the shape by
        // scaling it. Because scaling will update the origin of the
        // GeneralPath, we need to save the original location before proceeding.
        originalLocation = drawPath.getBounds2D();
        strTokenizer = new StringTokenizer(vv.getLabel(), ln);
        while (strTokenizer.hasMoreTokens()) {
            height += lineHeight;
            width = fontMetrics.stringWidth(strTokenizer.nextToken());
            maxWidth = width > maxWidth ? width : maxWidth;
        }

        // Now scale the GeneralPath, effectively "resizing" it.
        scalex = (maxWidth + this.leftRightMargin * 2) / drawPath.getBounds2D().getWidth();
        scaley = (height + this.topBottomMargin * 2) / drawPath.getBounds2D().getHeight();
        transform.scale(scalex, scaley);

        // We have to draw the GeneralPath before the scaling takes effect.
        drawPath.transform(transform);

        // Set the shape back to its original location.
        // setToTranslation() is used to remove the transformation or scaling.
        // Otherwise, the shape will be scaled twice.
        transform.setToTranslation(originalLocation.getMinX() - drawPath.getBounds2D().getMinX(), originalLocation.getMinY() - drawPath.getBounds2D().getMinY());

        // Draw again.
        drawPath.transform(transform);
    }
}
