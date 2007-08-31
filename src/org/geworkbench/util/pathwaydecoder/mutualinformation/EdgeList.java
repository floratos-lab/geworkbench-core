package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EdgeList implements Serializable {
	static Log log = LogFactory.getLog(EdgeList.class);
	
	private ArrayList<Edge> edgeList;
	
	public EdgeList(){
		edgeList = new ArrayList<Edge>();
	}
	
	public EdgeList(int number){
		edgeList = new ArrayList<Edge>(number);
	}
	
	public void addEdge(Edge e){
		this.edgeList.add(e);
	}
	
	public void addEdge(String start, String end){
		this.edgeList.add(new Edge(start, end));
	}
	
	public void removeEdge(String start, String end){
		for(int i = 0; i < this.edgeList.size(); i++){
			if(this.edgeList.get(i).equals(start, end)){
				this.edgeList.remove(i);
			}				
		}
	}
	
	public void removeEdge(Edge e){
		this.removeEdge(e.getStartNode(), e.getEndNode());
	}
	
	public Edge getEdge(int i){
		return this.edgeList.get(i);
	}
	
	public String print(String header){
		String result = header + "\n";
		for(Edge e: this.edgeList){
			result += e.getStartNode() + "\t" + e.getEndNode() + "\n";
		}		
		return result;
	}
	
	public boolean isEmpty(){
		if(this.edgeList.size() <= 0)
			return true;
		return false;
	}
	
	public int size(){
		return this.edgeList.size();
	}
}
