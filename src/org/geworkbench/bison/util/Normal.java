package org.geworkbench.bison.util;

import java.io.Serializable;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Columbia University</p>
 *
 * @author Andrea Califano
 * @version $Id$
 */

public class Normal implements Serializable {
	private static final long serialVersionUID = 815454764380381809L;
	
	private double mean;
	private double sigma;
	private double variance;
	private double n;
	private double sumX;
	private double sumXX;
	private boolean status;

    public void add(double v) {
        sumX += v;
        sumXX += v * v;
        n++;
        status = false;
    }

    private void compute() {
        if (!status) {
            if (n > 1) {
                mean = sumX / n;
                variance = (sumXX - n * mean * mean) / (n - 1);
                sigma = Math.sqrt(variance);

                status = true;
            } else {
                status = true;
            }
        }
    }

    public double getMean() {
        compute();
        return mean;
    }

    public double getSigma() {
        compute();
        return sigma;
    }

    public Normal() {
        mean = 0;
        n = 0;
        variance = 0;
        sigma = 0;
        sumX = 0;
        sumXX = 0;
        status = false;
    }

}
