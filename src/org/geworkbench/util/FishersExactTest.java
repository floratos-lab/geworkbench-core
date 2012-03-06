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
		FishersExactTest fet =new FishersExactTest( a + b + c + d + 1);
		return fet.calculateRightSideOneTailedP(a, b, c, d);
	}
	
	/** Calculate Right-side one-tailed p-value for Fisher's Exact Test. */
	public double calculateRightSideOneTailedP(int a, int b, int c, int d) {
		if(a+b+c+d>=logFactorial.length) { // handle unexpected case of going beyond the size
			FishersExactTest fet = new FishersExactTest( a + b + c + d + 1);
			return fet.calculateRightSideOneTailedP(a, b, c, d);
		}
		
		double p_sum = 0.0d;

		final int sum = Math.min(a + b, a + c);
		if (a > sum / 2) {
			while (a <= sum) {
				p_sum += fisherSub(a, b, c, d);
				++a;
				--b;
				--c;
				++d;
			}
		} else { // a <= sum/2
			/*
			 * The alternative way is only for efficiency. 
			 * It yields the same result.
			 */
			--a;
			++b;
			++c;
			--d;
			while (a >= 0 && d >= 0) {
				p_sum += fisherSub(a, b, c, d);
				--a;
				++b;
				++c;
				--d;
			}
			p_sum = 1 - p_sum;
		}

		return p_sum;
	}

	private double fisherSub(int a, int b, int c, int d) {
		return Math.exp(logFactorial[a + b] + logFactorial[c + d]
				+ logFactorial[a + c] + logFactorial[b + d]
				- logFactorial[a + b + c + d] - logFactorial[a]
				- logFactorial[b] - logFactorial[c] - logFactorial[d]);
	}

}
