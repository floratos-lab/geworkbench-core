package org.geworkbench.util;

import org.geworkbench.components.clustering.HierarchicalClusteringTest;

import junit.framework.TestCase;


public class SpearmanRankDistanceTest extends TestCase {

	public static void main(String[] args) {
		SpearmanRankDistanceTest t = new SpearmanRankDistanceTest();
		t.testDistanceDoubleArrayDoubleArray();
		t.testGetRankDoubleArray();
	}

	/*
	 * Test method for 'org.geworkbench.util.SpearmanRankDistance.distance(double[], double[])'
	 */
	public void testDistanceDoubleArrayDoubleArray() {
		double [] a = {28, 2, 3, 4, 5, 5, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1};
		double [] b = {0.2974, 0.0492, 0.6932, 0.6501, 0.9830, 0.5527, 0.4001, 0.1988, 0.6252, 0.7334, 0.3759, 0.0099, 0.4199, 0.7537, 0.7939, 0.9200, 0.8447, 0.3678, 0.6208, 0.7313};
		
		double r = SpearmanRankDistance.distance(a,b);
		assertEquals(r,0.969879,0.00001);
		//System.out.printf("Spearman distance: %f\n",r);
		
	}

	/*
	 * Test method for 'org.geworkbench.util.SpearmanRankDistance.getRank(double[])'
	 */
	public void testGetRankDoubleArray() {
		// TODO Auto-generated method stub
		//System.out.printf("RankOrder\n");
		double [] a = {28, 2, 3, 4, 5, 5, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1};
		double [] r = SpearmanRankDistance.getRank(a);
		double [] good_r = {19, 1, 2, 3, 5, 5, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 0};
		for (int i=0;i<20;i++){
			assertEquals(r[i],good_r[i],0.0000000000001);
		}
	}

}
