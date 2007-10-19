package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NetBoostData implements Serializable {
	static Log log = LogFactory.getLog(NetBoostData.class);
	
	/* INTERNAL DATASTRUCTURE */
	double[] scores;
	double[] variances;
	double[][] confusions;
	double[][] trainloss;	
	double[][] testloss;
	String[] models;
	String s;
	
	/* CONSTRUCTORS */
	public NetBoostData(String classscores, String confusion, String traintestloss){
		log.info("NetBoostData constructing results with the following files...\nclassscores_target.txt file:\n" 
				+ classscores 
				+ "\nconfusion.txt file:\n" 
				+ confusion 
				+ "\nnetboost_traintestloss.txt file:\n" 
				+ traintestloss
				);
		
		
		StringBuilder ms = new StringBuilder("Models: ");		
		StringBuilder ss = new StringBuilder("Scores: ");
		StringBuilder vs = new StringBuilder("Variances: ");
		StringBuilder cs = new StringBuilder("Confusion Matrix:");
		StringBuilder trainls = new StringBuilder("Train loss points: ");
		StringBuilder testls = new StringBuilder("Test loss points: ");
		ArrayList<String> al = new ArrayList<String>(7);
		
		StringTokenizer st = new StringTokenizer(classscores.trim(), "\n");	
		while(st.hasMoreTokens()){
			String s = st.nextToken().trim();
			int plusMinusIndex = s.indexOf("+/-");
			int colonIndex = s.indexOf(":");
			if((colonIndex > 0) && (plusMinusIndex > 0)
					&& (colonIndex < plusMinusIndex)
					){
				al.add(s);
			}
		}
		al.trimToSize();
		int size = al.size();
		if(size > 0){			
			models = new String[size];
			scores = new double[size];
			variances = new double[size];
			for(int i = 0; i < size; i++){
				String s = al.get(i);
				int colonIndex = s.indexOf(":");
				int plusMinusIndex = s.indexOf("+/-");
				if((colonIndex >= 0) && (colonIndex <= s.length()) 
						&& (plusMinusIndex >= 0) && (plusMinusIndex <= s.length())
						&& (colonIndex < plusMinusIndex)
						){
					models[i] = s.substring(0, colonIndex).trim();
					ms.append(models[i]);
					ms.append(" ");
					scores[i] = Double.parseDouble(s.substring(colonIndex + 1, plusMinusIndex).trim());
					ss.append(scores[i]);
					ss.append(" ");
					variances[i] = Double.parseDouble(s.substring(plusMinusIndex + 3).trim());
					vs.append(variances[i]);
					vs.append(" ");
				}
			}
		}
		
		st = new StringTokenizer(confusion.trim(), "\n");
		al.clear();
		this.confusions = new double[7][7];
		int count = -1;
		while(st.hasMoreTokens()){
			String s = st.nextToken().trim();
			if((s.indexOf("=") > 0) && (s.indexOf("100") > 0)){
				StringTokenizer st2 = new StringTokenizer(s);
				cs.append("\n");
				count++;
				int count2 = 0;
				while(st2.hasMoreTokens() && (count < 7) && (count2 < 7)){
					this.confusions[count][count2] = Double.parseDouble(st2.nextToken().trim());
					cs.append(this.confusions[count][count2]);
					cs.append(" ");
					count2++;
				}
			}
		}
		
		st = new StringTokenizer(traintestloss.trim(), "\n");
		al.clear();
		while(st.hasMoreTokens()){
			String line = st.nextToken();
			int colonIndex = line.indexOf(":");
			int dotIndex = line.indexOf(".");
			if(line.startsWith(" ") 
					&& (colonIndex >= 0) && (dotIndex >=0)
					&& (colonIndex < dotIndex)
					)
				al.add(line.trim());			
		}
		testloss = new double[al.size()][2];
		trainloss = new double[al.size()][2];
		for(int i = 0; i < al.size(); i++){
			String line = al.get(i);
			if((line.indexOf(":") >= 0) && (line.indexOf(".") >=0)){
				StringTokenizer st2 = new StringTokenizer(line.trim());
				int numTokens = st2.countTokens();
				if(numTokens == 3){
					String token = st2.nextToken().trim();
					double num = Double.parseDouble(token.substring(0, token.indexOf(":")));
					double test = Double.parseDouble(st2.nextToken().trim());					
					double train = Double.parseDouble(st2.nextToken().trim());
					testloss[i][0] = num;
					testloss[i][1] = test;
					trainloss[i][0] = num;
					trainloss[i][1] = train;
					trainls.append("[");
					trainls.append(num);
					trainls.append(", ");
					trainls.append(train);
					trainls.append("] ");
					testls.append("[");
					testls.append(num);
					testls.append(", ");
					testls.append(test);
					testls.append("] ");
				}
			}
		}
		ms.append("\n");
		ss.append("\n");
		vs.append("\n");
		cs.append("\n");
		trainls.append("\n");
		testls.append("\n");
		s = ms.toString() + ss.toString() + vs.toString() + cs.toString() + trainls.toString() + testls.toString();
	}
	
	public boolean isEmpty(){
		if((this.models != null) && (this.models.length > 0)
				&& (this.testloss != null) && (this.testloss.length > 0)
				&& (this.trainloss != null) && (this.trainloss.length > 0)
				&& (this.scores != null) && (this.scores.length > 0)
				&& (this.variances != null) && (this.variances.length > 0)
				&& (this.confusions != null) && (this.confusions.length > 0)
				)
			return true;
		return false;
	}
	
	public double[] getScores(){
		return this.scores;
	}
	
	public double[] getVariances(){
		return this.variances;
	}
	
	public double[][] getConfusedData(){
		return this.confusions;
	}
	
	public String[] getModels(){
		return this.models;
	}
	
	public double[][] getTrainLoss(){
		return this.trainloss;
	}
	
	public double[][] getTestLoss(){
		return this.testloss;
	}
	
	public String toString(){
		return this.s;
	}
}

