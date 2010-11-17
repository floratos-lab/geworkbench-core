package org.geworkbench.builtin.projects.remoteresources;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id$
 */

/**
 * A simple wrapper class for resources
 */
public class RemoteResource {
    private static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";
	private boolean isDirty = true;
    private String username;
    private String password;
    private String connectProtocol;
    private String shortname;
    private String uri;
    private int portnumber = 80;
    private boolean editable = true;

    public RemoteResource(String url, String protocal, String user,
                          String passwd) {
        uri = url;
        connectProtocol = protocal;
        username = user;
        try {
			password = encrypt(passwd);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
        this.shortname = "Default";
    }

    public RemoteResource(String shortname, String url, String port,
                          String protocal,
                          String user, String passwd) {
        uri = url.trim();
        connectProtocol = protocal.trim();
        username = user.trim();
		password = passwd.trim();
        this.shortname = shortname.trim();
        try {
            if (new Integer(port.trim()).intValue() != 0) {
                portnumber = new Integer(port.trim()).intValue();
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            portnumber = 80;
        }
        this.editable = true;
        ;
    }

    //a simple 'key'
	private static final char[] simpleKey = (System.getProperty("user.name")).toCharArray();
	private static final int COUNT = 20;
	// Salt
    private static byte[] salt = {
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };

	// user user name as key to encrypt
	public static String encrypt(String value)
			throws GeneralSecurityException {
		if(value==null || value.equals(""))return "";
		
	    // Create PBE parameter set
	    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, COUNT);
	    PBEKeySpec pbeKeySpec = new PBEKeySpec(simpleKey);
	    SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

		Cipher cipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
		cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

		byte[] encrypted = cipher.doFinal(value.getBytes());
		return byteArrayToHexString(encrypted);
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

    public static RemoteResource createNewInstance(String[] columns) {
        if (columns.length == 7) {
            return new RemoteResource(columns[0], columns[1], columns[2],
                    columns[3], columns[4], columns[5], columns[6]);
        } else if (columns.length == 6) {
            return new RemoteResource(columns[0], columns[1], columns[2],
                    columns[3], columns[4], columns[5]);
        } else if (columns.length == 4) {
            return new RemoteResource(columns[0], columns[2], columns[3],
                    columns[1], "", "", "false");
        }
        return null;

    }

    /**
     * RemoteResource
     */
    private RemoteResource(String shortname, String url, String port,
                          String protocal,
                          String user, String passwd, String editableStr
    ) {
        this(shortname, url, port, protocal, user, passwd);

        this.editable = new Boolean(editableStr.trim()).booleanValue();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setConnectProtocal(String connectProtocol) {
        this.connectProtocol = connectProtocol;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public void setPortnumber(int portnumber) {
        this.portnumber = portnumber;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        try {
			return decrypt(password);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
    }

	private static String decrypt(String encryptedPwd)
			throws GeneralSecurityException {
		if(encryptedPwd==null || encryptedPwd.equals(""))return "";
		
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, COUNT);
	    PBEKeySpec pbeKeySpec = new PBEKeySpec(simpleKey);
	    SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBE_WITH_MD5_AND_DES);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

		Cipher cipher = Cipher.getInstance(PBE_WITH_MD5_AND_DES);
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
		
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(encryptedPwd));
		return new String(decrypted);
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public String getConnectProtocal() {
        return connectProtocol;
    }

    public String getShortname() {
        return shortname;
    }

    public String getUri() {
        return uri;
    }

    public int getPortnumber() {
        return portnumber;
    }

    public boolean isEditable() {
        return editable;
    }

    /**
     * update
     *
     * @param rResource RemoteResource
     */
    public void update(RemoteResource rResource) {
        shortname = rResource.shortname;
        uri = rResource.uri;
        username = rResource.username;
        password = rResource.password;
        connectProtocol = rResource.connectProtocol;
        editable = rResource.editable;
    }

    /**
     * Use shortname as the Key for every object.
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (obj instanceof RemoteResource) {
            return shortname.equals(((RemoteResource) obj).shortname)&& portnumber==(((RemoteResource) obj).portnumber)
					&& uri.equals(((RemoteResource) obj).uri) && username.equals(((RemoteResource) obj).username) && password.equals(((RemoteResource) obj).password) && connectProtocol.equals(((RemoteResource) obj).connectProtocol);
        } else {
            return false;
        }
    }

    public int compareTo(Object o) throws ClassCastException {
    	 if(o instanceof RemoteResource){
    		 return shortname.compareTo(((RemoteResource)o).getShortname());
    	 }else{
    		throw new ClassCastException("Expect a RemoteResource Object.");  
    	 }
    	 
    }

	public String getEncryptedPassword() {
		return password;
	}

}
