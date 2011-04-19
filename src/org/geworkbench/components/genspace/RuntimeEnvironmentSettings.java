package org.geworkbench.components.genspace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.geworkbench.components.genspace.entity.Tool;

public class RuntimeEnvironmentSettings {

	public static final String PROD_HOST = "genspace.cs.columbia.edu";
	public static final String DEVEL_HOST = "amos.cs.columbia.edu";
	public static final String LOCAL_HOST = "localhost";
	public static final String SERVER = PROD_HOST;
	public static final String XMPP_HOST = PROD_HOST;

	public static final String GS_WEB_ROOT_PROD = "http://genspace.cs.columbia.edu/";
	public static final String GS_WEB_ROOT_DEVEL = "http://lenox.cs.columbia.edu/genspace/";

	public static final String GS_WEB_ROOT = GS_WEB_ROOT_PROD;
	public static HashMap<Integer,Tool> tools = null;

	 public static Object readObject(byte[] data)
	    {
	    	ObjectInputStream is;
			try {
				is = new ObjectInputStream(new ByteArrayInputStream(data));
					return is.readObject(); 
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	    }
	 public static byte[] writeObject(Object o)
	    {
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(bos);
		    	oos.writeObject(o);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return bos.toByteArray();
	    }
}
