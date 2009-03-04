package org.geworkbench.builtin.projects;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A popup window for user to enter a 4-digit PDB id;
 * the PDB file from RCSB website will be downloaded and displayed
 * 
 * @author mw2518
 * @version $Id: PDBDialog.java,v 1.5 2009-03-04 22:14:48 wangm Exp $
 * 
 */
class PDBDialog extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(this.getClass());

	private JTextField jt = new JTextField(4);
	public ProjectPanel pp = null;
	String message = "<html><b>Invalid PDB ID!  Please try again.</b></html>\n"
			+ "<html>Each structure in the PDB is represented by a PDB ID with </html>\n"
			+ "<html><b>4 characters</b> in the form of <b>[0-9][a-z,0-9][a-z,0-9][a-z,0-9]</b>.</html>\n"
			+ "For example, 4HHB, 9INS are PDB IDs for hemoglobin and insulin.";

	/*
	 * save pdb file from rcsb website to local disk, 
	 * let project panel open the downloaded pdb file
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String pdbid = jt.getText();
		log.info("entered pdb: " + pdbid);
		if (pdbid.length() != 4 || !NumberUtils.isDigits(pdbid.substring(0, 1))) {
			JOptionPane.showMessageDialog(new JFrame(), message,
					"Invalid PDB ID", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String url = "http://www.rcsb.org/pdb/files/" + pdbid + ".pdb";
		String contents = getContent(url);
		if (contents == null)
			return;

		String dir = LoadData.getLastDataDirectory() + "/webpdb/";
		File nd = new File(dir);
		if (!nd.exists()) {
			nd.mkdir();
		}
		String downloaded = dir + pdbid + ".pdb";
		log.info("download to: " + downloaded);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(downloaded));
			bw.write(contents);
			bw.close();

			File df = new File(downloaded);
			File[] fs = new File[] { df };
			pp.fileOpenAction(fs,
					new org.geworkbench.components.parsers.PDBFileFormat(),
					false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		setVisible(false);
		dispose();
	}

	/**
	 * specify parent project panel
	 * 
	 * @param mainpp
	 */
	public PDBDialog(ProjectPanel mainpp) {
		super("Open RCSB PDB File");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pp = mainpp;
	}

	/*
	 * ask for user input and register with action listener
	 */
	public void create() {
		JButton btn = new JButton("Search RCSB PDB");
		btn.addActionListener(this);

		Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		contentPane.add(new JLabel("Enter PDB ID:  "));

		contentPane.add(jt);
		contentPane.add(btn);
		getRootPane().setDefaultButton(btn);
		pack();
		setVisible(true);
	}

	/**
	 * read web content from url fname, return as a string
	 * 
	 * @param fname
	 * @return
	 */
	private String getContent(String fname) {
		StringBuffer contents = null;
		try {
			URL url = new URL(fname);
			URLConnection uc = url.openConnection();

			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection huc = (HttpURLConnection) uc;
			huc.connect();

			if (huc.getResponseCode() != HttpURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(new JFrame(), message,
						"Invalid PDB ID", JOptionPane.ERROR_MESSAGE);
				huc.disconnect();
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(huc
					.getInputStream()));

			contents = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				contents.append(line);
				contents.append("\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getContent notconnected error: " + fname);
		}
		return contents.toString();
	}

}
