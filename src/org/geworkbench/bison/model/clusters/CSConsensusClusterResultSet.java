package org.geworkbench.bison.model.clusters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;


public class CSConsensusClusterResultSet extends CSAncillaryDataSet<DSMicroarray> implements DSConsensusClusterResultSet{
	private static final long serialVersionUID = -2709788335680114673L;
	private ArrayList<File> cluFiles = new ArrayList<File>();
	private ArrayList<File> sortedGctFiles = new ArrayList<File>();
	private ArrayList<CCData> dataList = new ArrayList<CCData>();

	public CSConsensusClusterResultSet(DSMicroarraySet parent, String label) {
		super(parent, label);
	}
	
	public class CCData implements Comparable<CCData>, Serializable{
		private static final long serialVersionUID = -4604603182829056390L;
		private String type = "";
		private String name = "";
		private byte[] bytes = null;
		public CCData(File file){
			name = file.getName();
			type = name.substring(name.lastIndexOf(".")+1).toUpperCase();
			bytes = fileToBytes(file);
		}
		public String getType() { return type; }
		public String getName() { return name; }
		public byte[] getBytes(){ return bytes;}
		public String toString(){ return name; }

		public int compareTo(CCData o) {
			if(type.equals(o.type))
				return name.compareTo(o.name);
			else
				return type.compareTo(o.type);
		}
	}
	
	public void addFile(String fname){
		File file = new File(fname);
		if(file.exists()) {
			CCData ccData = new CCData(file);
			dataList.add(ccData);
			
			String type = ccData.getType();
			if(type.equals("CLU"))
				cluFiles.add(file);
			else if (type.equals("GCT") && ccData.getName().contains(".srt"))
				sortedGctFiles.add(file);
			file.deleteOnExit();
		}
	}
	
	public ArrayList<CCData> getDataList(){
		Collections.sort(dataList);
		return dataList;
	}
		
	private byte[] fileToBytes(File file){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = null;
		try{
			is = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while((bytes = is.read(buffer)) > -1){
				bos.write(buffer, 0, bytes);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(is != null) is.close();
				if(bos != null) bos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}
	
	public ArrayList<File> getCluFiles(){
		return cluFiles;
	}
	
	public ArrayList<File> getSortedGctFiles(){
		return sortedGctFiles;
	}

}
