package org.geworkbench.builtin.projects.remoteresources.query;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.geworkbench.engine.properties.PropertiesManager;

/**
 * Util class for connect with caArray RMI sever. Created by IntelliJ IDEA.
 * User: xiaoqing Date: Mar 1, 2007 Time: 12:21:55 PM To change this template
 * use File | Settings | File Templates.
 */
public class GeWorkbenchCaARRAYAdaptor {

	public final static String CAARRAY_USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String SERVERURL = "serverlocation";
	public final static String SERVERPORT = "serverport";

	private String piName;
	private String chipTypeName;
	private String tissueTypeName;
	private String organName;
	String password;

	public GeWorkbenchCaARRAYAdaptor() throws IOException {
		try {
			String newPassword = PropertiesManager.getInstance().getProperty(
					getClass(), PASSWORD, "Default Value");

			if (newPassword == null) {
				password = "";
			} else {
				password = newPassword;
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Report Error.
	 * 
	 * @param message
	 * @return
	 */
	public static boolean fail(String message) {
		JOptionPane.showMessageDialog(null, message);
		return false;
	}

	/**
	 * Now caArray only supports two match types for pull down menu. ChipType
	 * will be added later.
	 * 
	 * @param key
	 * @return
	 */
	public String matchCatagory(String key) {
		if (key.equalsIgnoreCase(CaARRAYQueryPanel.TISSUETYPE)) {
			return "OrganismPart";
		} else {
			return "Organism";
		}
	}

	public String getPiName() {
		return piName;
	}

	public void setPiName(String piName) {
		this.piName = piName;
	}

	public String getChipTypeName() {
		return chipTypeName;
	}

	public void setChipTypeName(String chipTypeName) {
		this.chipTypeName = chipTypeName;
	}

	public String getTissueTypeName() {
		return tissueTypeName;
	}

	public void setTissueTypeName(String tissueTypeName) {
		this.tissueTypeName = tissueTypeName;
	}

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}
}
