package org.geworkbench.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BrowserLauncher {

	static Log log = LogFactory.getLog(BrowserLauncher.class);

	/**
	 * Attempts to open the default web browser to the given URL.
	 * 
	 * @param url
	 *            The URL to open
	 * @throws IOException
	 *             If the web browser could not be located or does not run
	 */
	public static void openURL(String url) throws IOException {
		if (!java.awt.Desktop.isDesktopSupported()) {
			log.warn("Desktop doesn't support the browse action");
			javax.swing.JOptionPane.showMessageDialog(null,
					"Desktop doesn't support the browse action");
		}

		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			log.warn("Desktop doesn't support the browse action");
			javax.swing.JOptionPane.showMessageDialog(null,
					"Desktop doesn't support the browse action");
		}

		java.net.URI uri;
		try {
			uri = new java.net.URI(url);
			desktop.browse(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
