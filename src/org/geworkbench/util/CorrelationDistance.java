package org.geworkbench.util;


/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Compute the Pearson Correlation <b>Distance</b>. Note that if
 * <code>r</code> is correlation, then Correlation Distance is <code>1-r</code></p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Frank Wei Guo
 * @version $Id$
 */
public class CorrelationDistance implements Distance {
    public final static CorrelationDistance instance = new CorrelationDistance();

    protected CorrelationDistance() {
    }

    public static double distance(double[] x, double[] y) {
        int N = Math.min(x.length, y.length);
        double sumSqX = 0;
        double sumSqY = 0;
        double sumCoProduct = 0;
        double meanX = x[0];
        double meanY = y[0];
        for (int i = 1; i < N; i++) {
            double sweep = ((double)i) / (i + 1);
            double deltaX = x[i] - meanX;
            double deltaY = y[i] - meanY;
            sumSqX += deltaX * deltaX * sweep;
            sumSqY += deltaY * deltaY * sweep;
            sumCoProduct += deltaX * deltaY * sweep;
            meanX += deltaX / (i + 1);
            meanY += deltaY / (i + 1);

        }
        double popSDX = Math.sqrt(sumSqX / N);
        double popSDY = Math.sqrt(sumSqY / N);
        double covXY = sumCoProduct / N;
        return (1 - (covXY / (popSDX * popSDY)));
    }

    public double compute(double[] a, double[] b) {
        return distance(a, b);
    }
}
