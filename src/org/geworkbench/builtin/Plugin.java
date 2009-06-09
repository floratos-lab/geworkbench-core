package org.geworkbench.builtin;

/**
 * A java bean to hold the contents of a xxx.ccm.xml file.
 * 
 * @author tg2321
 * @version $Id: Plugin.java,v 1.2 2009-06-09 20:27:29 keshav Exp $
 * 
 */
public class Plugin {

	private String id = null;
	private String name = null;
	private String clazz = null;
	private String source = null;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param clazz
	 * @param source
	 */
	public Plugin(String id, String name, String clazz, String source) {
		this.id = id;
		this.name = name;
		this.clazz = clazz;
		this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}
}
