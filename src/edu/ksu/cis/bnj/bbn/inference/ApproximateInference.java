package edu.ksu.cis.bnj.bbn.inference;

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

import edu.ksu.cis.bnj.bbn.BBNGraph;

import java.io.OutputStream;

/**
 * Approximate Inference abstract class
 *
 * @author Roby Joehanes
 */
public abstract class ApproximateInference extends Inference {

    protected RMSECalculator rmseWriter = null;

    public ApproximateInference() {
    }

    /**
     * Constructor for ApproximateInference.
     *
     * @param g
     */
    public ApproximateInference(BBNGraph g) {
        super(g);
    }

    /**
     * Sets the file to print the RMSE values of each sample to
     *
     * @param PrintWriter - the file for the RMSE values to be printed
     */
    public void setRMSEfile(OutputStream out) {
        rmseWriter = new RMSECalculator();
        rmseWriter.setOutputStream(out);
    }


    /**
     * Compute the Root Mean Square Error (RMSE) value between exact and inexact
     * inference. Does some validation
     *
     * @param exact The exact inference
     * @return double The RMSE result
     */
    public double computeRMSE(ExactInference exact) {
        InferenceResult eResult = exact.getMarginals();
        if (eResult == null)
            throw new RuntimeException("Error: Failed to fetch the exact inference result!");

        InferenceResult aResult = getMarginals();
        if (aResult == null)
            throw new RuntimeException("Error: Failed to fetch the approximate inference result!");

        return eResult.computeRMSE(aResult);
    }

    public RMSECalculator getRMSEWriter() {
        return rmseWriter;
    }
}
