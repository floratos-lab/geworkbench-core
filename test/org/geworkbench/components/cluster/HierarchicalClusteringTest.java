package org.geworkbench.components.cluster;

import org.geworkbench.BaseTestCase;
import org.geworkbench.bison.model.clusters.HierCluster;
import org.geworkbench.components.clustering.HClustering;
import org.geworkbench.components.clustering.HierClustAnalysis;
import org.geworkbench.components.clustering.HierClusterFactory;
import org.geworkbench.components.clustering.SLink;
import org.geworkbench.components.clustering.SimpleClustering;
import org.geworkbench.util.ClusterUtils;
import org.geworkbench.util.CorrelationDistance;
import org.geworkbench.util.EuclideanDistance;
import org.geworkbench.util.SpearmanRankDistance;

//import java.io.File;

/**
 * Confirms that {@link HierClustAnalysis} and {@link FastHierClustAnalysis} return the same results.
 *
 * @author John Watkinson
 * @author Bernd Jagla
 */
public class HierarchicalClusteringTest extends BaseTestCase {
	//Examples for linkage testing
	double[][] data = { { -0.100001, 0 }, { -0.100002, 0 }, { 0.0100001, 0 },
			{ 0.0100002, 0 }, { 0.0200001, 0 }, { 0.0200002, 0 },
			{ 0.2000001, 0 }, { -0.200001, 0 }, { -0.200002, 0 },
			{ 0.4000001, 0 }, { 0.4000002, 0.1 }, { -0.900001, 0 },
			{ 1.0000001, 0 } };

	String[] items = { "class0_1", "class0_2", //0 1
			"class0_3", "class0_4", //2 3
			"class0_5", "class0_6", //4 5
			"class0_7", "class0_8", //6 7
			"class0_9", "class1_1", //8 9
			"class1_2", "class2_1", //10 11
			"class3_1" }; //12

	// Examples for distance measurements
	double[][] dataD = {
			{ 0.224325, 0.468666, 1.210881, 1.272600, 1.415378, 1.620722,
					1.768653, 1.882424, 2.070221, 2.153244 },
			{ 0.244539, 0.722503, 1.201393, 1.311953, 1.680502, 1.858208,
					2.172326, 2.274847, 2.256668, 2.271316 },
			{ 0.991929, 1.899524, 3.005665, 3.966271, 5.115155, 5.819132,
					6.851253, 7.825265, 9.051731, 10.083811 },
			{ 1.136540, 2.044084, 2.943068, 3.865459, 5.200454, 5.929232,
					7.116227, 8.125636, 9.115188, 10.057163 },
			{ -0.148299, -0.889854, -1.229082, -1.293516, -1.446460, -1.699588,
					-2.194255, -1.924763, -2.411487, -2.060893 },
			{ -0.175715, -0.683071, -1.113480, -1.414121, -1.784951, -1.646919,
					-2.185205, -1.998490, -2.334096, -2.177336 },
			{ -0.799574, -1.917727, -2.941115, -4.236243, -4.923679, -5.931000,
					-6.972086, -7.763728, -9.059879, -9.750474 },
			{ -1.217564, -1.901376, -3.186357, -4.123071, -4.895174, -5.750588,
					-6.945757, -7.948440, -8.956685, -9.857914 } };

	String[] itemsD = { "case1_1", "case1_2", //0 1
			"case2_1", "case2_2", //2 3
			"case3_1", "case3_2", //4 5
			"case4_1", "case4_2" }; //6 7

	public HierarchicalClusteringTest() {
		super();
	}

	HierCluster clusterE, clusterS, clusterC;

	HierCluster clusterEO, clusterSO, clusterCO;

	HierCluster cclusterE, cclusterS, cclusterC;

	HierCluster aclusterE, aclusterS, aclusterC;

	HierCluster clusterED, clusterSD, clusterCD;

	HierCluster clusterEOD, clusterSOD, clusterCOD;

	HierCluster cclusterED, cclusterSD, cclusterCD;

	HierCluster aclusterED, aclusterSD, aclusterCD;

	protected void setUp() throws Exception {
		// Testdata see "Hierarchical Clustering.doc" (in the GUI tests) for more information and how it looks like

		HierClusterFactory.Test factory = new HierClusterFactory.Test(items);
		//            ClusterConverter helper = new ClusterConverter(factory);
		//Single linkage
		SimpleClustering slinkage = new SimpleClustering(
				HClustering.Linkage.SINGLE);
		clusterE = slinkage.compute(null, data, factory,
				EuclideanDistance.instance);
		clusterS = slinkage.compute(null, data, factory,
				SpearmanRankDistance.instance);
		clusterC = slinkage.compute(null, data, factory,
				CorrelationDistance.instance);
		//Single linkage old algorithm
		SLink slinkageO = SLink.instance;
		clusterEO = slinkageO.compute(null, data, factory,
				EuclideanDistance.instance);
		clusterSO = slinkageO.compute(null, data, factory,
				SpearmanRankDistance.instance);
		clusterCO = slinkageO.compute(null, data, factory,
				CorrelationDistance.instance);
		//Complete linkage
		SimpleClustering clinkage = new SimpleClustering(
				HClustering.Linkage.COMPLETE);
		cclusterE = clinkage.compute(null, data, factory,
				EuclideanDistance.instance);
		cclusterS = clinkage.compute(null, data, factory,
				SpearmanRankDistance.instance);
		cclusterC = clinkage.compute(null, data, factory,
				CorrelationDistance.instance);
		//Average linkage
		SimpleClustering alinkage = new SimpleClustering(
				HClustering.Linkage.AVERAGE);
		aclusterE = alinkage.compute(null, data, factory,
				EuclideanDistance.instance);
		aclusterS = alinkage.compute(null, data, factory,
				SpearmanRankDistance.instance);
		aclusterC = alinkage.compute(null, data, factory,
				CorrelationDistance.instance);

		HierClusterFactory.Test factoryD = new HierClusterFactory.Test(itemsD);
		//          ClusterConverter helper = new ClusterConverter(factory);
		//Single linkage
		slinkage = new SimpleClustering(HClustering.Linkage.SINGLE);
		clusterED = slinkage.compute(null, dataD, factoryD,
				EuclideanDistance.instance);
		clusterSD = slinkage.compute(null, dataD, factoryD,
				SpearmanRankDistance.instance);
		clusterCD = slinkage.compute(null, dataD, factoryD,
				CorrelationDistance.instance);
		//Single linkage old algorithm
		slinkageO = SLink.instance;
		clusterEOD = slinkageO.compute(null, dataD, factoryD,
				EuclideanDistance.instance);
		clusterSOD = slinkageO.compute(null, dataD, factoryD,
				SpearmanRankDistance.instance);
		clusterCOD = slinkageO.compute(null, dataD, factoryD,
				CorrelationDistance.instance);
		//Complete linkage
		clinkage = new SimpleClustering(HClustering.Linkage.COMPLETE);
		cclusterED = clinkage.compute(null, dataD, factoryD,
				EuclideanDistance.instance);
		cclusterSD = clinkage.compute(null, dataD, factoryD,
				SpearmanRankDistance.instance);
		cclusterCD = clinkage.compute(null, dataD, factoryD,
				CorrelationDistance.instance);
		//Average linkage
		alinkage = new SimpleClustering(HClustering.Linkage.AVERAGE);
		aclusterED = alinkage.compute(null, dataD, factoryD,
				EuclideanDistance.instance);
		aclusterSD = alinkage.compute(null, dataD, factoryD,
				SpearmanRankDistance.instance);
		aclusterCD = alinkage.compute(null, dataD, factoryD,
				CorrelationDistance.instance);
	}

	/**
	 * 
	 * used for older version and commented function at the end of this class
	 */
	//public HierarchicalClusteringTest(String s) {
	//super(s);//
	//}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testLinkageClustering1() throws Exception {

		// create Single linkage, Euclidian Tree result
		HierCluster[] hcE = new HierCluster[12];
		HierClusterFactory.Test clusterEf = new HierClusterFactory.Test(items);
		for (int i = 0; i < 12; i++) {
			hcE[i] = clusterEf.newCluster();
		}
		hcE[0].addNode(clusterEf.newLeaf(11), 0);
		hcE[0].addNode(hcE[1], 1);
		hcE[1].addNode(hcE[2], 0);
		hcE[1].addNode(clusterEf.newLeaf(12), 1);
		hcE[2].addNode(hcE[3], 0);
		hcE[2].addNode(hcE[4], 1);
		hcE[3].addNode(clusterEf.newLeaf(6), 0);
		hcE[3].addNode(hcE[5], 1);
		hcE[4].addNode(clusterEf.newLeaf(10), 0);
		hcE[4].addNode(clusterEf.newLeaf(9), 1);
		hcE[5].addNode(hcE[6], 0);
		hcE[5].addNode(hcE[7], 1);
		hcE[6].addNode(hcE[8], 0);
		hcE[6].addNode(hcE[9], 1);
		hcE[7].addNode(hcE[10], 0);
		hcE[7].addNode(hcE[11], 1);
		hcE[8].addNode(clusterEf.newLeaf(2), 0);
		hcE[8].addNode(clusterEf.newLeaf(3), 1);
		hcE[9].addNode(clusterEf.newLeaf(4), 0);
		hcE[9].addNode(clusterEf.newLeaf(5), 1);
		hcE[10].addNode(clusterEf.newLeaf(0), 0);
		hcE[10].addNode(clusterEf.newLeaf(1), 1);
		hcE[11].addNode(clusterEf.newLeaf(7), 0);
		hcE[11].addNode(clusterEf.newLeaf(8), 1);
		hcE[0].setDepth(7);
		hcE[1].setDepth(6);
		hcE[2].setDepth(5);
		hcE[3].setDepth(4);
		hcE[4].setDepth(1);
		hcE[5].setDepth(3);
		hcE[6].setDepth(2);
		hcE[7].setDepth(2);
		hcE[8].setDepth(1);
		hcE[9].setDepth(1);
		hcE[10].setDepth(1);
		hcE[11].setDepth(1);

		//
		assertTrue("1", ClusterUtils.areClustersEqual(hcE[0], clusterEO));
		assertFalse("2", ClusterUtils.areClustersEqual(hcE[0], clusterSO));
		assertFalse("3", ClusterUtils.areClustersEqual(hcE[0], clusterCO));

		assertFalse("4", ClusterUtils.areClustersEqual(hcE[0], cclusterE));
		assertFalse("5", ClusterUtils.areClustersEqual(hcE[0], cclusterS));
		assertFalse("6", ClusterUtils.areClustersEqual(hcE[0], cclusterC));

		assertFalse("7", ClusterUtils.areClustersEqual(hcE[0], aclusterE));
		assertFalse("8", ClusterUtils.areClustersEqual(hcE[0], aclusterS));
		assertFalse("9", ClusterUtils.areClustersEqual(hcE[0], aclusterC));

	}

	public void testLinkageClustering2() throws Exception {
		// create Complete linkage, Euclidian Tree result
		HierCluster[] hcE2 = new HierCluster[12];
		HierClusterFactory.Test clusterE2f = new HierClusterFactory.Test(items);
		for (int i = 0; i < 12; i++) {
			hcE2[i] = clusterE2f.newCluster();
		}
		hcE2[0].addNode(hcE2[1], 0);
		hcE2[0].addNode(hcE2[2], 1);
		hcE2[1].addNode(hcE2[3], 0);
		hcE2[1].addNode(clusterE2f.newLeaf(11), 1);
		hcE2[2].addNode(hcE2[11], 0);
		hcE2[2].addNode(clusterE2f.newLeaf(12), 1);
		hcE2[11].addNode(clusterE2f.newLeaf(10), 0);
		hcE2[11].addNode(clusterE2f.newLeaf(9), 1);
		hcE2[3].addNode(hcE2[4], 0);
		hcE2[3].addNode(hcE2[6], 1);
		hcE2[4].addNode(hcE2[5], 0);
		hcE2[4].addNode(clusterE2f.newLeaf(6), 1);
		hcE2[5].addNode(hcE2[7], 0);
		hcE2[5].addNode(hcE2[8], 1);
		hcE2[6].addNode(hcE2[9], 0);
		hcE2[6].addNode(hcE2[10], 1);
		hcE2[7].addNode(clusterE2f.newLeaf(5), 0);
		hcE2[7].addNode(clusterE2f.newLeaf(4), 1);
		hcE2[8].addNode(clusterE2f.newLeaf(3), 0);
		hcE2[8].addNode(clusterE2f.newLeaf(2), 1);
		hcE2[9].addNode(clusterE2f.newLeaf(8), 0);
		hcE2[9].addNode(clusterE2f.newLeaf(7), 1);
		hcE2[10].addNode(clusterE2f.newLeaf(1), 0);
		hcE2[10].addNode(clusterE2f.newLeaf(0), 1);
		hcE2[0].setDepth(6);
		hcE2[1].setDepth(5);
		hcE2[2].setDepth(2);
		hcE2[3].setDepth(4);
		hcE2[4].setDepth(3);
		hcE2[5].setDepth(2);
		hcE2[6].setDepth(2);
		hcE2[7].setDepth(1);
		hcE2[8].setDepth(1);
		hcE2[9].setDepth(1);
		hcE2[10].setDepth(1);
		hcE2[11].setDepth(1);

		//
		assertTrue("10", ClusterUtils.areClustersEqual(hcE2[0], cclusterE));
		assertFalse("11", ClusterUtils.areClustersEqual(hcE2[0], cclusterS));
		assertFalse("12", ClusterUtils.areClustersEqual(hcE2[0], cclusterC));

		assertFalse("13", ClusterUtils.areClustersEqual(hcE2[0], clusterEO));
		assertFalse("14", ClusterUtils.areClustersEqual(hcE2[0], clusterSO));
		assertFalse("15", ClusterUtils.areClustersEqual(hcE2[0], clusterCO));

		assertFalse("16", ClusterUtils.areClustersEqual(hcE2[0], aclusterE));
		assertFalse("17", ClusterUtils.areClustersEqual(hcE2[0], aclusterS));
		assertFalse("18", ClusterUtils.areClustersEqual(hcE2[0], aclusterC));

	}

	public void testLinkageClustering3() throws Exception {
		// create Average linkage, Euclidian Tree result
		HierCluster[] hcE3 = new HierCluster[12];
		HierClusterFactory.Test clusterE3f = new HierClusterFactory.Test(items);
		for (int i = 0; i < 12; i++) {
			hcE3[i] = clusterE3f.newCluster();
		}
		hcE3[0].addNode(hcE3[1], 0);
		hcE3[0].addNode(clusterE3f.newLeaf(12), 1);
		hcE3[1].addNode(hcE3[2], 0);
		hcE3[1].addNode(clusterE3f.newLeaf(11), 1);
		hcE3[2].addNode(hcE3[4], 0);
		hcE3[2].addNode(hcE3[3], 1);
		hcE3[3].addNode(hcE3[5], 0);
		hcE3[3].addNode(hcE3[6], 1);
		hcE3[4].addNode(hcE3[7], 0);
		hcE3[4].addNode(clusterE3f.newLeaf(6), 1);
		hcE3[5].addNode(hcE3[8], 0);
		hcE3[5].addNode(hcE3[9], 1);
		hcE3[6].addNode(hcE3[10], 0);
		hcE3[6].addNode(hcE3[11], 1);
		hcE3[7].addNode(clusterE3f.newLeaf(10), 0);
		hcE3[7].addNode(clusterE3f.newLeaf(9), 1);
		hcE3[8].addNode(clusterE3f.newLeaf(8), 0);
		hcE3[8].addNode(clusterE3f.newLeaf(7), 1);
		hcE3[9].addNode(clusterE3f.newLeaf(0), 0);
		hcE3[9].addNode(clusterE3f.newLeaf(1), 1);
		hcE3[10].addNode(clusterE3f.newLeaf(5), 0);
		hcE3[10].addNode(clusterE3f.newLeaf(4), 1);
		hcE3[11].addNode(clusterE3f.newLeaf(3), 0);
		hcE3[11].addNode(clusterE3f.newLeaf(2), 1);
		hcE3[0].setDepth(6);
		hcE3[1].setDepth(5);
		hcE3[2].setDepth(4);
		hcE3[3].setDepth(3);
		hcE3[4].setDepth(2);
		hcE3[5].setDepth(2);
		hcE3[6].setDepth(2);
		hcE3[7].setDepth(1);
		hcE3[8].setDepth(1);
		hcE3[9].setDepth(1);
		hcE3[10].setDepth(1);
		hcE3[11].setDepth(1);

		//
		assertTrue("19", ClusterUtils.areClustersEqual(hcE3[0], aclusterE));
		assertFalse("20", ClusterUtils.areClustersEqual(hcE3[0], aclusterS));
		assertFalse("21", ClusterUtils.areClustersEqual(hcE3[0], aclusterC));

		assertFalse("22", ClusterUtils.areClustersEqual(hcE3[0], clusterEO));
		assertFalse("23", ClusterUtils.areClustersEqual(hcE3[0], clusterSO));
		assertFalse("24", ClusterUtils.areClustersEqual(hcE3[0], clusterCO));

		assertFalse("25", ClusterUtils.areClustersEqual(hcE3[0], cclusterE));
		assertFalse("26", ClusterUtils.areClustersEqual(hcE3[0], cclusterS));
		assertFalse("27", ClusterUtils.areClustersEqual(hcE3[0], cclusterC));

	}

	public void testLinkageClustering4() throws Exception {
		// create Average + Complete linkage, Spearman + Pearson Tree result
		HierCluster[] hcS = new HierCluster[12];
		HierClusterFactory.Test clusterSf = new HierClusterFactory.Test(items);
		for (int i = 0; i < 12; i++) {
			hcS[i] = clusterSf.newCluster();
		}
		hcS[0].addNode(hcS[1], 0);
		hcS[0].addNode(hcS[2], 1);
		hcS[1].addNode(hcS[3], 0);
		hcS[1].addNode(hcS[4], 1);
		hcS[2].addNode(hcS[5], 0);
		hcS[2].addNode(hcS[11], 1);
		hcS[3].addNode(hcS[6], 0);
		hcS[3].addNode(hcS[7], 1);
		hcS[4].addNode(hcS[8], 0);
		hcS[4].addNode(hcS[9], 1);
		hcS[5].addNode(hcS[10], 0);
		hcS[5].addNode(clusterSf.newLeaf(11), 1);
		hcS[6].addNode(clusterSf.newLeaf(12), 0);
		hcS[6].addNode(clusterSf.newLeaf(10), 1);
		hcS[7].addNode(clusterSf.newLeaf(9), 0);
		hcS[7].addNode(clusterSf.newLeaf(6), 1);
		hcS[8].addNode(clusterSf.newLeaf(5), 0);
		hcS[8].addNode(clusterSf.newLeaf(4), 1);
		hcS[9].addNode(clusterSf.newLeaf(3), 0);
		hcS[9].addNode(clusterSf.newLeaf(2), 1);
		hcS[10].addNode(clusterSf.newLeaf(1), 0);
		hcS[10].addNode(clusterSf.newLeaf(0), 1);
		hcS[11].addNode(clusterSf.newLeaf(8), 0);
		hcS[11].addNode(clusterSf.newLeaf(7), 1);
		hcS[0].setDepth(4);
		hcS[1].setDepth(3);
		hcS[2].setDepth(3);
		hcS[3].setDepth(2);
		hcS[4].setDepth(2);
		hcS[5].setDepth(2);
		hcS[6].setDepth(1);
		hcS[7].setDepth(1);
		hcS[8].setDepth(1);
		hcS[9].setDepth(1);
		hcS[10].setDepth(1);
		hcS[11].setDepth(1);

		//
		assertTrue("28", ClusterUtils.areClustersEqual(hcS[0], aclusterS));
		assertTrue("29", ClusterUtils.areClustersEqual(hcS[0], aclusterC));
		assertFalse("30", ClusterUtils.areClustersEqual(hcS[0], aclusterE));

		assertFalse("31", ClusterUtils.areClustersEqual(hcS[0], clusterEO));
		assertFalse("32", ClusterUtils.areClustersEqual(hcS[0], clusterCO));
		assertFalse("33", ClusterUtils.areClustersEqual(hcS[0], clusterSO));

		assertTrue("34", ClusterUtils.areClustersEqual(hcS[0], cclusterS));
		assertTrue("35", ClusterUtils.areClustersEqual(hcS[0], cclusterC));
		assertFalse("36", ClusterUtils.areClustersEqual(hcS[0], cclusterE));

		/*
		 System.out.println("test.");
		 ClusterUtils.printCluster(hcE[0]);
		 System.out.println("Eucledian.");
		 ClusterUtils.printCluster(clusterE);
		 System.out.println("SpearmanRank.");
		 ClusterUtils.printCluster(clusterS);
		 System.out.println("Correlation.");
		 ClusterUtils.printCluster(clusterC);
		 System.out.println("Done.");
		 */
	};

	/**
	 * 
	 * @throws Exception
	 */
	public void testDistanceClustering1() throws Exception {
		// Testdata see "Hierarchical Clustering.doc" (in the GUI tests) for more information and how it looks like

		// create Single linkage, Euclidian Tree result
		HierCluster[] hcE = new HierCluster[7];
		HierClusterFactory.Test clusterEf = new HierClusterFactory.Test(itemsD);
		for (int i = 0; i < 7; i++) {
			hcE[i] = clusterEf.newCluster();
		}
		hcE[0].addNode(hcE[1], 0);
		hcE[0].addNode(hcE[2], 1);
		hcE[1].addNode(hcE[3], 0);
		hcE[1].addNode(hcE[4], 1);
		hcE[2].addNode(clusterEf.newLeaf(6), 0);
		hcE[2].addNode(clusterEf.newLeaf(7), 1);
		hcE[3].addNode(clusterEf.newLeaf(2), 0);
		hcE[3].addNode(clusterEf.newLeaf(3), 1);
		hcE[4].addNode(hcE[5], 0);
		hcE[4].addNode(hcE[6], 1);
		hcE[5].addNode(clusterEf.newLeaf(0), 0);
		hcE[5].addNode(clusterEf.newLeaf(1), 1);
		hcE[6].addNode(clusterEf.newLeaf(4), 0);
		hcE[6].addNode(clusterEf.newLeaf(5), 1);
		hcE[0].setDepth(4);
		hcE[1].setDepth(3);
		hcE[2].setDepth(1);
		hcE[3].setDepth(1);
		hcE[4].setDepth(2);
		hcE[5].setDepth(1);
		hcE[6].setDepth(1);

		//
		assertTrue("D1", ClusterUtils.areClustersEqual(hcE[0], clusterEOD));
		assertFalse("D2", ClusterUtils.areClustersEqual(hcE[0], clusterSOD));
		assertFalse("D3", ClusterUtils.areClustersEqual(hcE[0], clusterCOD));

		assertFalse("D4", ClusterUtils.areClustersEqual(hcE[0], cclusterED));
		assertFalse("D5", ClusterUtils.areClustersEqual(hcE[0], cclusterSD));
		assertFalse("D6", ClusterUtils.areClustersEqual(hcE[0], cclusterCD));

		assertFalse("D7", ClusterUtils.areClustersEqual(hcE[0], aclusterED));
		assertFalse("D8", ClusterUtils.areClustersEqual(hcE[0], aclusterSD));
		assertFalse("D9", ClusterUtils.areClustersEqual(hcE[0], aclusterCD));

	}

	public void testDistanceClustering2() throws Exception {
		// create Single linkage, Pearsn Tree result
		HierCluster[] hcE2 = new HierCluster[7];
		HierClusterFactory.Test clusterE2f = new HierClusterFactory.Test(itemsD);
		for (int i = 0; i < 7; i++) {
			hcE2[i] = clusterE2f.newCluster();
		}
		hcE2[0].addNode(hcE2[1], 0);
		hcE2[0].addNode(hcE2[2], 1);
		hcE2[1].addNode(hcE2[3], 0);
		hcE2[1].addNode(hcE2[4], 1);
		hcE2[2].addNode(hcE2[5], 0);
		hcE2[2].addNode(hcE2[6], 1);
		hcE2[3].addNode(clusterE2f.newLeaf(0), 0);
		hcE2[3].addNode(clusterE2f.newLeaf(1), 1);
		hcE2[4].addNode(clusterE2f.newLeaf(2), 0);
		hcE2[4].addNode(clusterE2f.newLeaf(3), 1);
		hcE2[5].addNode(clusterE2f.newLeaf(4), 0);
		hcE2[5].addNode(clusterE2f.newLeaf(5), 1);
		hcE2[6].addNode(clusterE2f.newLeaf(6), 0);
		hcE2[6].addNode(clusterE2f.newLeaf(7), 1);
		hcE2[0].setDepth(3);
		hcE2[1].setDepth(2);
		hcE2[2].setDepth(2);
		hcE2[3].setDepth(1);
		hcE2[4].setDepth(1);
		hcE2[5].setDepth(1);
		hcE2[6].setDepth(1);

		//
		assertFalse("D10", ClusterUtils.areClustersEqual(hcE2[0], cclusterED));
		assertFalse("D11", ClusterUtils.areClustersEqual(hcE2[0], cclusterSD));
		assertTrue("D12", ClusterUtils.areClustersEqual(hcE2[0], cclusterCD));

		assertFalse("D13", ClusterUtils.areClustersEqual(hcE2[0], clusterEOD));
		assertFalse("D14", ClusterUtils.areClustersEqual(hcE2[0], clusterSOD));
		assertTrue("D15", ClusterUtils.areClustersEqual(hcE2[0], clusterCOD));

		assertFalse("D16", ClusterUtils.areClustersEqual(hcE2[0], aclusterED));
		assertFalse("D17", ClusterUtils.areClustersEqual(hcE2[0], aclusterSD));
		assertTrue("D18", ClusterUtils.areClustersEqual(hcE2[0], aclusterCD));

	}

	public void testDistanceClustering3() throws Exception {
		// create Single linkage, Spearman Tree result
		HierCluster[] hcE3 = new HierCluster[7];
		HierClusterFactory.Test clusterE3f = new HierClusterFactory.Test(itemsD);
		for (int i = 0; i < 7; i++) {
			hcE3[i] = clusterE3f.newCluster();
		}
		hcE3[0].addNode(hcE3[1], 0);
		hcE3[0].addNode(hcE3[1], 1);
		hcE3[1].addNode(clusterE3f.newLeaf(1), 0);
		hcE3[1].addNode(hcE3[1], 1);
		hcE3[2].addNode(hcE3[4], 0);
		hcE3[2].addNode(hcE3[3], 1);
		hcE3[3].addNode(clusterE3f.newLeaf(2), 0);
		hcE3[3].addNode(hcE3[6], 1);
		hcE3[4].addNode(clusterE3f.newLeaf(0), 0);
		hcE3[4].addNode(clusterE3f.newLeaf(3), 1);
		hcE3[5].addNode(clusterE3f.newLeaf(4), 0);
		hcE3[5].addNode(clusterE3f.newLeaf(5), 1);
		hcE3[6].addNode(clusterE3f.newLeaf(6), 0);
		hcE3[6].addNode(clusterE3f.newLeaf(7), 1);
		hcE3[0].setDepth(4);
		hcE3[1].setDepth(3);
		hcE3[2].setDepth(2);
		hcE3[3].setDepth(2);
		hcE3[4].setDepth(1);
		hcE3[5].setDepth(1);
		hcE3[6].setDepth(1);

		//
		assertFalse("D19", ClusterUtils.areClustersEqual(hcE3[0], aclusterED));
		//assertTrue("D20", ClusterUtils.areClustersEqual(hcE3[0], aclusterSD));
		assertFalse("D21", ClusterUtils.areClustersEqual(hcE3[0], aclusterCD));

		assertFalse("D22", ClusterUtils.areClustersEqual(hcE3[0], clusterEOD));
		//assertTrue("D23", ClusterUtils.areClustersEqual(hcE3[0], clusterSOD));
		assertFalse("D24", ClusterUtils.areClustersEqual(hcE3[0], clusterCOD));

		assertFalse("D25", ClusterUtils.areClustersEqual(hcE3[0], cclusterED));
		//assertTrue("D26", ClusterUtils.areClustersEqual(hcE3[0], cclusterSD));
		assertFalse("D27", ClusterUtils.areClustersEqual(hcE3[0], cclusterCD));
	};

	/*        // Create a microarray set and a view
	 //        CSExprMicroarraySet microarraySet = new CSExprMicroarraySet();
	 ExpressionFileFormat format = new ExpressionFileFormat();
	 CSExprMicroarraySet microarraySet = (CSExprMicroarraySet)format.getMArraySet(new File("C:/matlab/work/ge-workbench/test datasets/testdata2log.txt"));
	 /*
	 CSMarkerVector markers = microarraySet.getMarkerVector();
	 CSExpressionMarker m1 = new CSExpressionMarker(0);
	 m1.setLabel("M1");
	 CSExpressionMarker m2 = new CSExpressionMarker(1);
	 m2.setLabel("M2");
	 markers.add(m1);
	 markers.add(m2);
	 CSMicroarray a1 = new CSMicroarray(2);
	 a1.setLabel("A1");
	 a1.setSerial(0);
	 CSMicroarray a2 = new CSMicroarray(2);
	 a2.setLabel("A2");
	 a2.setSerial(1);
	 CSMicroarray a3 = new CSMicroarray(2);
	 a3.setLabel("A3");
	 a3.setSerial(2);
	 a1.setMarkerValue(0, new CSExpressionMarkerValue(0.5f));
	 a1.getMarkerValue(0).setMissing(false);
	 a1.setMarkerValue(1, new CSExpressionMarkerValue(2.0f));
	 a1.getMarkerValue(1).setMissing(false);
	 a2.setMarkerValue(0, new CSExpressionMarkerValue(0.1f));
	 a2.getMarkerValue(0).setMissing(false);
	 a2.setMarkerValue(1, new CSExpressionMarkerValue(2.2f));
	 a2.getMarkerValue(1).setMissing(false);
	 a3.setMarkerValue(0, new CSExpressionMarkerValue(3.5f));
	 a3.getMarkerValue(0).setMissing(false);
	 a3.setMarkerValue(1, new CSExpressionMarkerValue(1.0f));
	 a3.getMarkerValue(1).setMissing(false);
	 microarraySet.add(a1);
	 microarraySet.add(a2);
	 microarraySet.add(a3);
	 * /
	 DSMicroarraySetView view = new CSMicroarraySetView();
	 view.setDataSet(microarraySet);
	 // Run clustering with both algorithms
	 HierClustAnalysis slow = new HierClustAnalysis();
	 HierClustPanel slowPanel = ((HierClustPanel)slow.getParameterPanel());
	 slowPanel.setDimension(2);
	 FastHierClustAnalysis fast = new FastHierClustAnalysis();
	 HierClustPanel fastPanel = ((HierClustPanel)fast.getParameterPanel());
	 fastPanel.setDimension(2);
	 JniHierClustAnalysis jnic = new JniHierClustAnalysis();
	 HierClustPanel jnicPanel = ((HierClustPanel)jnic.getParameterPanel());
	 jnicPanel.setDimension(2);
	 System.out.println("----COMPARE JNI VS FAST-----");
	 for (int method = 0; method < 3; ++method) {
	 for (int metric = 0; metric < 3; ++metric) {
	 jnicPanel.setMethod(method);
	 jnicPanel.setDistanceMetric(metric);
	 System.out.print("method = " + method + "  metric = " + metric);
	 AlgorithmExecutionResults jnicAER = jnic.execute(view);
	 HierCluster jnicResults1 = ((HierCluster[])jnicAER.getResults())[0];
	 HierCluster jnicResults2 = ((HierCluster[])jnicAER.getResults())[1];
	 fastPanel.setMethod(method);
	 fastPanel.setDistanceMetric(metric);
	 AlgorithmExecutionResults fastAER = fast.execute(view);
	 HierCluster fastResults1 = ((HierCluster[])fastAER.getResults())[0];
	 HierCluster fastResults2 = ((HierCluster[])fastAER.getResults())[1];
	 if (ClusterUtils.areClustersEqual(jnicResults1, fastResults1)) {
	 System.out.print(":  Marker pass ");
	 } else {
	 System.out.print(":  Marker fail ");
	 }
	 if (ClusterUtils.areClustersEqual(jnicResults2, fastResults2)) {
	 System.out.println("Microarray pass ");
	 } else {
	 System.out.println("Microarray fail ");
	 }
	 }
	 }
	 System.out.println("----COMPARE SLOW VS FAST-----");
	 for (int method = 0; method < 3; ++method) {
	 for (int metric = 0; metric < 3; ++metric) {
	 slowPanel.setMethod(method);
	 slowPanel.setDistanceMetric(metric);
	 System.out.print("method = " + method + "  metric = " + metric);
	 AlgorithmExecutionResults slowAER = slow.execute(view);
	 HierCluster slowResults1 = ((HierCluster[])slowAER.getResults())[0];
	 HierCluster slowResults2 = ((HierCluster[])slowAER.getResults())[1];
	 fastPanel.setMethod(method);
	 fastPanel.setDistanceMetric(metric);
	 AlgorithmExecutionResults fastAER = fast.execute(view);
	 HierCluster fastResults1 = ((HierCluster[])fastAER.getResults())[0];
	 HierCluster fastResults2 = ((HierCluster[])fastAER.getResults())[1];
	 if (ClusterUtils.areClustersEqual(slowResults1, fastResults1)) {
	 System.out.print(":  Marker pass ");
	 } else {
	 System.out.print(":  Marker fail ");
	 }
	 if (ClusterUtils.areClustersEqual(slowResults2, fastResults2)) {
	 System.out.println("Microarray pass ");
	 } else {
	 System.out.println("Microarray fail ");
	 }
	 }
	 }
	 System.out.println("----COMPARE SLOW VS JNI-----");
	 for (int method = 0; method < 3; ++method) {
	 for (int metric = 0; metric < 3; ++metric) {
	 slowPanel.setMethod(method);
	 slowPanel.setDistanceMetric(metric);
	 System.out.print("method = " + method + "  metric = " + metric);
	 AlgorithmExecutionResults slowAER = slow.execute(view);
	 HierCluster slowResults1 = ((HierCluster[])slowAER.getResults())[0];
	 HierCluster slowResults2 = ((HierCluster[])slowAER.getResults())[1];
	 jnicPanel.setMethod(method);
	 jnicPanel.setDistanceMetric(metric);
	 AlgorithmExecutionResults jnicAER = jnic.execute(view);
	 HierCluster jnicResults1 = ((HierCluster[])jnicAER.getResults())[0];
	 HierCluster jnicResults2 = ((HierCluster[])jnicAER.getResults())[1];
	 if (ClusterUtils.areClustersEqual(slowResults1, jnicResults1)) {
	 System.out.print(":  Marker pass ");
	 } else {
	 System.out.print(":  Marker fail ");
	 }
	 if (ClusterUtils.areClustersEqual(slowResults2, jnicResults2)) {
	 System.out.println("Microarray pass ");
	 } else {
	 System.out.println("Microarray fail ");
	 }
	 }
	 }
	 //        if (slowResults1 != null) {
	 //            assertTrue(ClusterUtils.areClustersEqual(slowResults1, fastResults1));
	 //        }
	 //       if (slowResults2 != null) {
	 //            assertTrue(ClusterUtils.areClustersEqual(slowResults2, fastResults2));
	 //        }

	 //        System.out.println("SLOW:");
	 //        ClusterUtils.printCluster(slowResults1);
	 //        System.out.println("");
	 //        System.out.println("FAST:");
	 //        ClusterUtils.printCluster(fastResults1);
	 }
	 */
	/**
	 * @param String[]
	 */
	public static void main(String[] args) {
		HierarchicalClusteringTest t = new HierarchicalClusteringTest();
		try {
			t.testLinkageClustering1();
			t.testLinkageClustering2();
			t.testLinkageClustering3();
			t.testLinkageClustering4();
			t.testDistanceClustering1();
			t.testDistanceClustering2();
			t.testDistanceClustering3();
		} catch (Exception e) {
		}
		;

	}

	@Override
	protected void tearDown() throws Exception {

	}

}
