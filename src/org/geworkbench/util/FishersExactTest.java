package org.geworkbench.util;

/**
 * Fisher's Exact Test
 * 
 * 
 * @author zji
 * @version $Id$
 * 
 */
public class FishersExactTest {

	private double[] logFactorial;

	public FishersExactTest(int size) {
		logFactorial = new double[size];
		logFactorial[0] = 0.0;
		for (int i = 1; i < size; i++) {
			logFactorial[i] = logFactorial[i - 1] + Math.log(i);
		}
	}

	public static double getRightSideOneTailedP(int a, int b, int c, int d) {
		FishersExactTest fet = new FishersExactTest(a + b + c + d + 1);
		return fet.calculateRightSideOneTailedP(a, b, c, d);
	}

	/** Calculate Right-side one-tailed p-value for Fisher's Exact Test. */
	public double calculateRightSideOneTailedP(int a, int b, int c, int d) {
		if (a + b + c + d >= logFactorial.length) { // handle unexpected case of
													// going beyond the size
			FishersExactTest fet = new FishersExactTest(a + b + c + d + 1);
			return fet.calculateRightSideOneTailedP(a, b, c, d);
		}

		double p_sum = 0.0d;

		final int sum = Math.min(a + b, a + c);

		while (a <= sum) {
			p_sum += fisherSub(a, b, c, d);
			++a;
			--b;
			--c;
			++d;
		}

		return Math.min(p_sum, 1.0);
	}

	private double fisherSub(int a, int b, int c, int d) {
		return Math.exp(logFactorial[a + b] + logFactorial[c + d]
				+ logFactorial[a + c] + logFactorial[b + d]
				- logFactorial[a + b + c + d] - logFactorial[a]
				- logFactorial[b] - logFactorial[c] - logFactorial[d]);
	}

}
