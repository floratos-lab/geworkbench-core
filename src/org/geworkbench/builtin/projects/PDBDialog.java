package org.geworkbench.builtin.projects;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class PDBDialog extends JFrame implements ActionListener
{
    private JTextField jt = new JTextField(4);
    public ProjectPanel pp = null;
    public void actionPerformed(ActionEvent e)
    {
	String pdbid = jt.getText();
	System.out.println("entered pdb: "+pdbid);
	String url = "http://www.rcsb.org/pdb/files/"+pdbid+".pdb";
	String contents = getContent(url);

	String dir = LoadData.getLastDataDirectory()+"/webpdb/";
	File nd = new File(dir);
	if (!nd.exists()) { nd.mkdir(); }
	String downloaded = dir+pdbid+".pdb";
	System.out.println("download to: "+ downloaded);
			    
	try{
	    BufferedWriter bw = new BufferedWriter(new FileWriter(downloaded));
	    bw.write(contents);
	    bw.close();
				
	    File df = new File(downloaded);
	    File[] fs = new File[]{df};
	    pp.fileOpenAction(fs, new org.geworkbench.components.parsers.PDBFileFormat(), false);
	}catch(Exception ex){
	    ex.printStackTrace();
	}

	setVisible(false);
	dispose();
    }

    public PDBDialog(ProjectPanel mainpp)
    {
	super("Open RCSB PDB File");
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	pp = mainpp;

	JButton btn = new JButton("Search RCSB PDB");
	btn.addActionListener(this);

	Container contentPane = getContentPane();
	contentPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
	contentPane.add(new JLabel("Enter PDB ID:  "));

	contentPane.add(jt);
	contentPane.add(btn);
	pack();
	setVisible(true);
    }

    private String getContent(String fname)
    {
	StringBuffer contents=null;
	try {
	    URL url = new URL(fname);
	    URLConnection uc = url.openConnection();
	    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

	    contents = new StringBuffer();
	    String line; 
	    while((line = br.readLine()) != null) {
		contents.append(line);
		contents.append("\n");
	    }
	    br.close();
	} catch (Exception e) {
	    //	    e.printStackTrace(); 
	    System.out.println("getContent notconnected error: "+fname);
	}
	return contents.toString();
    }

}
