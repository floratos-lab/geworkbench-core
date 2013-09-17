package org.geworkbench.util.associationdiscovery.statistics;

/**
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: First Genetic Trust Inc.
 * </p>
 * 
 * @author Manjunath Kustagi
 * @version $Id$
 */

public class ClusterStatistics {
	public ClusterStatistics() {
		for (int i = 0; i < 101; i++) {
			factCache[i] = 0;
		}
	}

	static private double factCache[] = new double[101];

	static private void error(String errorText) {
		System.out.println(errorText);
	}

	static private double gammaLN(double p) {
		double x, tmp, ser;

		x = p;
		tmp = x + 5.5;
		tmp = tmp - (x + .5) * Math.log(tmp);
		ser = 1.000000000190015 + 76.18009172947146 / (p + 1.0);
		ser -= 86.50532032941678 / (p + 2.0);
		ser += 24.01409824083091 / (p + 3.0);
		ser -= 1.231739572450155 / (p + 4.0);
		ser += .001208650973866179 / (p + 5.0);
		ser -= 5.395239384953E-06 / (p + 6.0);
		return (Math.log(2.506628274631001 * ser / x) - tmp);
	}

	/*
	 * The only public method in this class. Used in
	 * org.geworkbench.components.promoter.PromoterViewPanel
	 */
	static public double logBinomialDistribution(int nj, int j, double p) {
		double logBico = logBinomialCoeff(nj, j);
		double result = 0.0;
		if (p < 1.0) {
			result = logBico + (double) j * Math.log(p) + (double) (nj - j)
					* Math.log(1 - p);
		}
		return result;
	}

	static private double factLN(double n) {
		if (n < 0) {
			error("Negative factorial in routine FACTLN");
		}
		if (n <= 1) {
			return 0.0;
		}
		if (n <= 100) {
			return (factCache[(int) n] != 0) ? factCache[(int) n]
					: (factCache[(int) n] = gammaLN(n + 1.0));
		} else {
			return gammaLN(n + 1.0);
		}
	}

	static private double logBinomialCoeff(int n, int k) {
		double bico = factLN(n) - factLN(k) - factLN(n - k);
		return bico;
	}
}
