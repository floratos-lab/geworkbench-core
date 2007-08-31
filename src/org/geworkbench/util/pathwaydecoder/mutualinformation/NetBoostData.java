package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.xy.XYSeries;

public class NetBoostData implements Serializable {
	static Log log = LogFactory.getLog(NetBoostData.class);
	
	public NetBoostData(){
		
	}
	
	public boolean isEmpty(){
		boolean result = false;
		
		return result;
	}
	
	public void fillIterChartData(XYSeries testloss, XYSeries trainloss){
		boolean readFile = DataFromMatLab.fillIterChartData(testloss, trainloss);
		if(!readFile){
			DataFromMatLab.fillIterChartDataHardCoded(testloss, trainloss);
			log.warn("Using hard coded data");
		}
	}
	
	public double[] getScores(){
		double[] result = DataFromMatLab.getScores();
		if((result == null) || (result.length <= 0)){
			result = DataFromMatLab.getScoresHardCoded();
			log.warn("Using hard coded data");
		}
		return result;
	}
	
	public double[] getVariances(){
		double[] result = DataFromMatLab.getVariances();
		if((result == null) || (result.length <= 0)){
			result = DataFromMatLab.getVariancesHardCoded();
			log.warn("Using hard coded data");
		}
		return result;
	}
	
	public double[][] getConfusedData(){
		double[][] result = DataFromMatLab.getConfusedData();
		if((result == null) || (result.length <= 0)){
			result = DataFromMatLab.getConfusedDataHardCoded();
			log.warn("Using hard coded data");
		}
		return result;
	}
	
	public String[] getModels(){
		String[] result = DataFromMatLab.getModels();
		if((result == null) || (result.length <= 0)){
			result = DataFromMatLab.getModelsHardCoded();
			log.warn("Using hard coded data");
		}
		return result;
	}
}

class DataFromMatLab {
	static final String DIR_PATH = "../netboost/test/";
	static final String CHART_DATA = "netboost_traintestloss.txt";
	static final String SCORES_DATA = "classscores_target.txt";
	static final String CONFUSED_DATA = "confusion.txt";
	static final String[] MODELS = {"DMC","DMR","LPA","RDG","AGV","SMW","RDS"};
	
	static ArrayList<String[]> scores = new ArrayList<String[]>();
	
	static{
		try{
			//File f = new File(".");
			//System.out.println("\t\tf=" + f.getCanonicalPath());
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(DIR_PATH + SCORES_DATA)));
			String line = null;
			while((line = br.readLine()) != null){
				if(line.trim().equals(""))
					continue;
				if(line.indexOf("+/-") >= 0){
					StringTokenizer st = new StringTokenizer(line);
					int numTokens = st.countTokens();
					if(numTokens == 4){
						String tokens[] = new String[3];
						String s = st.nextToken().trim();
						tokens[0] = s.substring(0, s.indexOf(":"));	// model
						tokens[1] = st.nextToken().trim();			// score
						st.nextToken();
						tokens[2] = st.nextToken().trim();			// variance
						scores.add(tokens);
					}
				}
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	static void fillIterChartDataHardCoded(XYSeries testloss, XYSeries trainloss){
		testloss.add(1.0, 0.857);
		testloss.add(2.0, 0.714);
		testloss.add(3.0, 0.4876);
		testloss.add(4.0, 0.441);
		testloss.add(5.0, 0.29);
		testloss.add(6.0, 0.2124);
		testloss.add(7.0, 0.1728);
		testloss.add(8.0, 0.1544);
		testloss.add(9.0, 0.153);
		testloss.add(10.0, 0.1476);
		testloss.add(11.0, 0.1324);
		
		trainloss.add(1.0, 0.857);
		trainloss.add(2.0, 0.714);
		trainloss.add(3.0, 0.4876);
		trainloss.add(4.0, 0.4412);
		trainloss.add(5.0, 0.2912);
		trainloss.add(6.0, 0.2136);
		trainloss.add(7.0, 0.1748);
		trainloss.add(8.0, 0.1588);
		trainloss.add(9.0, 0.1578);
		trainloss.add(10.0, 0.1522);
		trainloss.add(11.0, 0.141);
		
	}
	
	static boolean fillIterChartData(XYSeries testloss, XYSeries trainloss){
		BufferedReader br;
		boolean success = false;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(DIR_PATH + CHART_DATA)));
			String line = null;
			while((line = br.readLine()) != null){
				if(line.trim().equals(""))
					continue;
				if(line.startsWith(" ") && (line.indexOf(":") >= 0) && (line.indexOf(".") >=0)){
					StringTokenizer st = new StringTokenizer(line);
					int numTokens = st.countTokens();
					if(numTokens == 3){
						String token = st.nextToken().trim();
						double num = Double.parseDouble(token.substring(0, token.indexOf(":")));
						double test = Double.parseDouble(st.nextToken().trim());
						double train = Double.parseDouble(st.nextToken().trim());
						testloss.add(num, test);
						trainloss.add(num, train);
					}
				}
			}
			br.close();
			success = true;
		} catch (Exception e){
			e.printStackTrace();
		} 
		return success;
	}
	
	static String[] getModelsHardCoded(){
		return MODELS;
	}
	
	static String[] getModels(){
		String[] result = null;
		if(scores != null){
			result = new String[scores.size()];
			for(int i = 0; i < result.length; i++){
				result[i] = ((String[]) scores.get(i))[0];
			}
		}
		return result;
	}
	
	static double[] getScoresHardCoded(){
		final double[] result = {3.89149, -1.18759, -7.58099, -7.98297, -9.0903, -13.3463, -14.2465};
		return result;
	}
	
	static double[] getScores(){
		double[] result = null;
		if(scores != null){
			result = new double[scores.size()];
			for(int i = 0; i < result.length; i++){
				result[i] = Double.parseDouble(((String[]) scores.get(i))[1]);
			}
		}		
		return result;
	}
	
	static double[] getVariancesHardCoded(){
		final double[] result = {0, 0, 0, 0, 0, 8.88178e-16, 0};
		return result;
	}
	
	static double[] getVariances(){
		double[] result = null;
		if(scores != null){
			result = new double[scores.size()];
			for(int i = 0; i < result.length; i++){
				result[i] = Double.parseDouble(((String[]) scores.get(i))[2]);
			}
		}		
		return result;
	}
	
	static double[][] getConfusedDataHardCoded(){
		
		final double[][] result = new double[7][7];
		result[0][0] = 98.7;
		result[0][1] = 0;
		result[0][2] = 0;
		result[0][3] = 0;
		result[0][4] = 29.4;
		result[0][5] = 0;
		result[0][6] = 0;
		result[1][0] = 0;
		result[1][1] = 94.5;
		result[1][2] = 0;
		result[1][3] = 0;
		result[1][4] = 0;
		result[1][5] = 0;
		result[1][6] = 13.3;
		result[2][0] = 0;
		result[2][1] = 0;
		result[2][2] = 99.2;
		result[2][3] = 0;
		result[2][4] = 2.1;
		result[2][5] = 0;
		result[2][6] = 0;
		result[3][0] = 0;
		result[3][1] = 0;
		result[3][2] = 0;
		result[3][3] = 98.7;
		result[3][4] = 0;
		result[3][5] = 0;
		result[3][6] = 0;
		result[4][0] = 1.3;
		result[4][1] = 0;
		result[4][2] = 0.3;
		result[4][3] = 0;
		result[4][4] = 36.9;
		result[4][5] = 0;
		result[4][6] = 0;
		result[5][0] = 0;
		result[5][1] = 0;
		result[5][2] = 0.5;
		result[5][3] = 1.3;
		result[5][4] = 31.6;
		result[5][5] = 99.6;
		result[5][6] = 0;
		result[6][0] = 0;
		result[6][1] = 5.5;
		result[6][2] = 0;
		result[6][3] = 0;
		result[6][4] = 0;
		result[6][5] = 0;
		result[6][6] = 86.7;
		
		return result;
	}
	
	static double[][] getConfusedData(){
		double[][] result = null;
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(DIR_PATH + CONFUSED_DATA)));
			String line = null;
			int count = -1;
			while((line = br.readLine()) != null){
				if(line.trim().equals(""))
					continue;
				if(line.trim().endsWith("1000") && (line.indexOf("=") >= 0)){
					if (result == null) result = new double[7][7];
					StringTokenizer st = new StringTokenizer(line);
					int numTokens = st.countTokens();
					if(numTokens == 9){
						count++;
						result[count][0] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][1] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][2] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][3] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][4] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][5] = Double.parseDouble(st.nextToken().trim())/10;
						result[count][6] = Double.parseDouble(st.nextToken().trim())/10;
					}
				}
			}			
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
