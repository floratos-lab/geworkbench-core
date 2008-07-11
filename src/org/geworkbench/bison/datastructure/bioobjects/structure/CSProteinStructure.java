package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.io.*;
import java.util.*;

/**
 * User: mhall
 * Date: Mar 13, 2006
 * Time: 11:43:22 AM
 */
public class CSProteinStructure extends CSAncillaryDataSet implements DSProteinStructure {

    File dataFile = null;
    int chainoffset = 21;

    public CSProteinStructure(DSDataSet parent, String label) {
        super(parent, label);
    }

    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
    public HashMap<String, Integer> getChains()
    {
    	///get chains from pdb file
    	File prtfile = this.getFile();
    	File pdbfile = prtfile.getAbsoluteFile();
    	HashMap<String, Integer> chainhm = new HashMap<String, Integer>();
    	try{
    		BufferedReader br = new BufferedReader(new FileReader(pdbfile));
    		String line = null;
    		while ((line = br.readLine())!= null)
    		{
    			if (line.startsWith("ATOM  ")|| line.startsWith("HETATM"))
    			{
    				chainhm.put(line.substring(chainoffset, chainoffset+1), 1);
    			}
    		}
    		br.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	if (chainhm.get(" ") != null) 
	{
	    chainhm.remove(" ");
	    chainhm.put("_", 1);
	}
	return chainhm;
    }
}
