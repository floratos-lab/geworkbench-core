package org.geworkbench.builtin.projects;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.geworkbench.builtin.projects.WorkspaceHandler.OpenTask;
import org.geworkbench.builtin.projects.WorkspaceHandler.SaveTask;
import org.geworkbench.engine.preferences.GlobalPreferences;
import org.geworkbench.engine.properties.PropertiesManager;
import org.geworkbench.util.FilePathnameUtils;
import org.geworkbench.util.ProgressDialog;
import org.geworkbench.util.ProgressItem;
import org.geworkbench.util.ProgressTask;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Remote Workspace Handler
 * @author mw2518
 * $Id$
 */
public class RWspHandler {
	private static final String USER_INFO_DELIMIETER = "==";
	private static final String USER_INFO = "userinfo";
	private static final String META_DELIMIETER = "::";
	private static String userInfo = null;
	private JDialog loginDialog;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private static final String wsproot = FilePathnameUtils
			.getTemporaryFilesDirectoryPath()
			+ "wsp" + FilePathnameUtils.FILE_SEPARATOR;
	protected static String wspdir = wsproot;
	private static ProgressDialog pdmodal = ProgressDialog.create(ProgressDialog.MODAL_TYPE);
	private static ProgressDialog pdnonmodal = ProgressDialog.create(ProgressDialog.NONMODAL_TYPE);
	private WorkspaceHandler ws = new WorkspaceHandler();

	private JTextArea jtaDesc = new JTextArea();
	private JTextArea jtaAnno = new JTextArea();
	private JButton descbtn = new JButton("Update");
	private JButton addannobtn = new JButton("Add Annotation");
	private JButton adduserbtn = new JButton("Add User Access");
	private JButton addgroupbtn = new JButton("Add Group Access");
	private JTextField jtfUsername = new JTextField();
	private JComboBox grpnames = new JComboBox();
	private JComboBox grpjcb = new JComboBox(new String[]{"remove", "read", "write"});
	private JComboBox jcb = new JComboBox(new String[]{"remove", "read", "write", "admin"});
	private JTable jthist = new JTable();
	private JTable jtuser = new JTable();
	private JTable jtanno = new JTable();
	private RegPanel rp= new RegPanel();
	private JTable jtgroup = new JTable();
	private JTextField groupName = new JTextField(20);
	private JTextField groupUser = new JTextField(20);
	protected static final int LocalID = 0, IdID = 1, TitleID = 2, LockID=5, LkUsrID=6, DirtyID=7, SyncID=8, LastSyncID=9;
	protected static final int LDWidth = 790, LDHeight = 330, SLDHeight = 300; 

	protected static String checkoutstr = "";
	protected static String lastchange = "";
	protected static boolean dirty = false;
	protected static int wspId = 0;
	private JTable jt;
	private JTextField name;
	private JTextField desc;
	private JDialog newDialog;
	private JDialog listDialog;

	protected void getUserInfo() {
		FormLayout layout = new FormLayout("left:max(25dlu;pref), 5dlu, 70dlu");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		loginDialog = new JDialog();
		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
		builder.append("Username", usernameField);
		builder.append("Password", passwordField);
		JButton login = new JButton("OK");
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String username = usernameField.getText();
				String passwd = new String(passwordField.getPassword());
				if (username.trim().equals("")) {
					userInfo = "";
				} else {
					userInfo = username + USER_INFO_DELIMIETER + passwd;
					PropertiesManager properties = PropertiesManager
							.getInstance();
					try {
						properties.setProperty(this.getClass(), USER_INFO,
								String.valueOf(userInfo));
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				loginDialog.dispose();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e ){
				userInfo = null;
				loginDialog.dispose();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(login);
		buttons.add(cancel);
		JButton register = new JButton("Register");
		register.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				userInfo = null;
				loginDialog.dispose();
				RegPanel reg = new RegPanel();
				reg.initFrame();
				reg.showFrame();
			}
		});
		builder.append(register, buttons);
		
		PropertiesManager pm = PropertiesManager.getInstance();
		String savedUserInfo = null;
		try {
			savedUserInfo = pm.getProperty(this.getClass(), USER_INFO, "");
			if (!StringUtils.isEmpty(savedUserInfo)) {
				String s[] = savedUserInfo.split(USER_INFO_DELIMIETER, 2);
				if (s.length >= 2) {
					usernameField.setText(s[0]);
					passwordField.setText(s[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		loginDialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				userInfo = null;
				loginDialog.dispose();
			}
		});
		
		loginDialog.getRootPane().setDefaultButton(login);
		loginDialog.add(builder.getPanel());
		loginDialog.setTitle("User Login");
		loginDialog.setModal(true);
		loginDialog.pack();
		loginDialog.setLocationRelativeTo(null);
		loginDialog.setVisible(true);
	}

	public void listWsp(boolean listOnly) {
		if (userInfo == null || !listOnly) getUserInfo();
		if (userInfo == null) return;
		if (userInfo == "") {
			JOptionPane
					.showMessageDialog(
							null,
							"Please make sure you entered valid username and password",
							"Invalid User Account",
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		ListRemoteTask profileTask = new ListRemoteTask(ProgressItem.INDETERMINATE_TYPE, "Remote workspace list is being retrieved.", listOnly);
		pdnonmodal.executeTask(profileTask);
	}

	private class ListRemoteTask extends ProgressTask<HashMap<String, String[][]>, Void> {
		private boolean listOnly = false;

		ListRemoteTask(int pbtype, String message, boolean lo) {
			super(pbtype, message);
			listOnly = lo;
		}
		@Override
		protected HashMap<String, String[][]> doInBackground() throws Exception {
			HashMap<String, String[][]> hm = new HashMap<String, String[][]>();

			try {
				hm = WorkspaceServiceClient.getSavedWorkspaceList("LIST"+userInfo);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
    					"GeWorkbench cannot retrieve remote workspace info from axis2 web service.\n" +
    					"Please try again later or report the problem to geWorkbench support team.\n",
    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}
			return hm;
		}
		@Override
		protected void done(){
			if (isCancelled()){
				pdnonmodal.removeTask(this);
				return;
			}

			HashMap<String, String[][]> hm = new HashMap<String, String[][]>();
			String[][] profiles = null, groups = null, dldwspname = null;
			try {
				hm = get();
			} catch (ExecutionException e) {
				e.getCause().printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not retrieve remote workspace.",
						"List Remote Workspace Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not retrieve remote workspace.\n"+e,
						"List Remote Workspace Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				pdnonmodal.removeTask(this);
			}
			if (hm == null || isCancelled()) return;

			profiles = hm.get("PROFILE");
			if (profiles != null){
				int i=0;
				for (java.awt.Component c: rp.getComponents()){
					if(c instanceof JTextField && !(c instanceof JPasswordField) && i<profiles[0].length){
						JTextField t = (JTextField)c;
						t.setText(profiles[0][++i]);
					} else if (c instanceof JPasswordField){
						JPasswordField p = (JPasswordField)c;
						p.setText("");

					}
				}
			}

			groups = hm.get("GROUP");
			if (groups != null){
				jtgroup.setModel(new DetailTableModel(groups, WorkspaceServiceClient.colgroup));
				String[] names=new String[groups.length];
				for (int i=0; i<groups.length; i++)
					names[i]=groups[i][0];
				grpnames.setModel(new DefaultComboBoxModel(names));
			}

			dldwspname = hm.get("LIST");
			if (isCancelled()) return;

			if (dldwspname != null){// && dldwspname.length > 0)
				listWspDialog(dldwspname, listOnly);
			}else 
    			JOptionPane.showMessageDialog(null, "No remote workspace available");
			//insert else newWspDialog();
		}
	}

	private void addUserGroup(){
		String usr = groupUser.getText();
		if (usr.equals("")) return;
		int grpId = jtgroup.getSelectedRow();
		if (grpId < 0) return;
		String grp = (String)jtgroup.getModel().getValueAt(grpId, 0);
		String res = "";
		try{
			getUserInfo();
			if (userInfo == null) return;
			if (userInfo == "") {
				JOptionPane
						.showMessageDialog(
								null,
								"Please make sure you entered valid username and password",
								"Invalid User Account",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			res = WorkspaceServiceClient.modifySavedWorkspace("USERGROUP"+grp+META_DELIMIETER+usr+META_DELIMIETER+userInfo);
		}catch(Exception e1){
			JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    			"GeWorkbench cannot add user to group for remote workspace via axis2 web service.\n" +
	    			"Please try again later or report the problem to geWorkbench support team.\n",
	    			"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
		}
		if (res!=null && res.contains("success")){
			JOptionPane.showMessageDialog(null, res);
			listDialog.dispose();
		} else
			JOptionPane.showMessageDialog(null, "Could not add user to group to remote workspace "+usr);

	}
	private void addGroup(){
		String grp = groupName.getText();
		if (grp.equals("")) return;
		String res = "";
		try{
			getUserInfo();
			if (userInfo == null) return;
			if (userInfo == "") {
				JOptionPane
						.showMessageDialog(
								null,
								"Please make sure you entered valid username and password",
								"Invalid User Account",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			res = WorkspaceServiceClient.modifySavedWorkspace("GROUP"+grp+META_DELIMIETER+userInfo);
		}catch(Exception e1){
			JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    			"GeWorkbench cannot add user group for remote workspace via axis2 web service.\n" +
	    			"Please try again later or report the problem to geWorkbench support team.\n",
	    			"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
		}
		if (res!=null && res.contains("success")){
			JOptionPane.showMessageDialog(null, res);
			listDialog.dispose();
		} else
			JOptionPane.showMessageDialog(null, "Could not add user group to remote workspace "+grp);

	}

	private class DetailTableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 2482447217249689117L;
		protected String[][] data;
		private String[] header;

		public DetailTableModel(String[][] tabledata, String[] tableheader){
			data = tabledata;
			header = tableheader;
		}
		public int getColumnCount() {
			return header.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return header[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
	}
	
	private class WspTableModel extends DetailTableModel
	{
		private static final long serialVersionUID = -5175211782386134949L;

		public WspTableModel(String[][] tabledata, String[] tableheader){
			super(tabledata, tableheader);
		}
		/*public boolean isCellEditable(int row, int col) {
			if (col == localID)	return true;
			else	return false;
		}
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = (String)value;
			fireTableCellUpdated(row, col);
		}*/

		public Object getValueAt(int row, int col) {
			if (col==LockID || col==DirtyID||col==SyncID) return Boolean.valueOf(data[row][col]);
			return data[row][col];
		}

		public Class<?> getColumnClass(int col) {
			return (col==LockID || col==DirtyID||col==SyncID)?Boolean.class:String.class;
		}
	}

	private JButton downloadBtn = new JButton("Open remote");
	private JButton openLocalBtn = new JButton("Open local");
	private JButton renameLocalBtn = new JButton("Rename local");
	private JButton saveLocalBtn = new JButton("Save local");
	private JButton releaselockBtn = new JButton("Release lock");
	private JButton breaklockBtn = new JButton("Break lock");
	private JButton removeBtn = new JButton("Remove");
	private void listWspDialog(String[][] dldwspname, boolean listOnly){
		listDialog = new JDialog();
		listDialog.setTitle("Remote workspace List ("+userInfo.split(USER_INFO_DELIMIETER)[0]+")");
		JPanel jpw = new JPanel(new GridLayout(2, 0));

		if (listOnly)
			listDialog.add(jpw);
		else{
			JTabbedPane tp = new JTabbedPane();
			listDialog.add(tp);

			rp.changeForUpdate();
			tp.addTab("Profile", rp);
			tp.addTab("Workspaces", jpw);

			JPanel mgp = new JPanel(new GridLayout(2, 0));
			JButton createBtn = new JButton("Create Group");
			createBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addGroup();
				}
			});
			JButton addBtn = new JButton("Add Group User");
			addBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addUserGroup();
				}
			});
			FormLayout layout = new FormLayout("left:max(30dlu;pref), 10dlu, left:max(30dlu;pref), 10dlu, left:max(30dlu;pref)");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
			builder.setDefaultDialogBorder();
			builder.append("Group Name:", groupName, createBtn);
			builder.append("User Name:", groupUser, addBtn);
			mgp.add(builder.getPanel());
	
			jtgroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jtgroup.setPreferredScrollableViewportSize(jtgroup.getPreferredSize());
			mgp.add(new JScrollPane(jtgroup));
			tp.addTab("Manage Groups", mgp);

		}

		JPanel jp = new JPanel(new BorderLayout());
		jt = new JTable(new WspTableModel(dldwspname, WorkspaceServiceClient.colnames)){
			private static final long serialVersionUID = -477589541697891130L;

			public String getToolTipText(MouseEvent e) {
	             java.awt.Point p = e.getPoint();
	             int rowIndex = rowAtPoint(p);
	             int colIndex = columnAtPoint(p);
	             int realColumnIndex = convertColumnIndexToModel(colIndex);

	             if (realColumnIndex != IdID && realColumnIndex != LockID &&
	            		 realColumnIndex != DirtyID && realColumnIndex != SyncID)
	            	 return (String)getModel().getValueAt(rowIndex, realColumnIndex);
	             else	return null;
			 }
		};
		jt.removeColumn(jt.getColumnModel().getColumn(jt.getColumnCount()-1));
	    jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    jt.getColumnModel().getColumn(1).setMaxWidth(30);
	    jt.getColumnModel().getColumn(4).setMaxWidth(50);
	    for (int i=5; i<9; i++)
	    	if (i!=6) jt.getColumnModel().getColumn(i).setMaxWidth(40);
	    jt.getColumnModel().getColumn(9).setPreferredWidth(140);
	    jt.getColumnModel().getColumn(10).setPreferredWidth(140);

	    jt.setPreferredScrollableViewportSize(jt.getPreferredSize());
	    ListSelectionListener listener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedRow = jt.getSelectedRow();
					if (selectedRow > -1)
						selectionChanged(selectedRow);
				}
			}	
	    };
	    jt.getSelectionModel().addListSelectionListener(listener);
	    JScrollPane jsp = new JScrollPane(jt);
		jp.add(jsp, BorderLayout.CENTER);

		JPanel bp = new JPanel();
		downloadBtn.setEnabled(false);
		downloadBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int selectedRow = jt.getSelectedRow();
				listDialog.dispose();
				wspId = Integer.valueOf(jt.getModel().getValueAt(selectedRow, IdID).toString());
				String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
				String remoteName = jt.getModel().getValueAt(selectedRow, TitleID).toString();
				String selectedco = jt.getModel().getValueAt(selectedRow, LastSyncID).toString();
				Boolean localdirty = Boolean.valueOf(jt.getModel().getValueAt(selectedRow, DirtyID).toString());
				if (localdirty){
					int n = JOptionPane.showConfirmDialog(null, 
							"This remote workspace differs from your local copy. Do you want to overwrite your local copy?",
							"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (n == JOptionPane.CANCEL_OPTION || n == JOptionPane.NO_OPTION)
						return;
				}
				//select
				openWsp(remoteName, remoteId+".wsp", selectedco);
			}
		});
		bp.add(downloadBtn);
		openLocalBtn.setEnabled(false);
		openLocalBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int selectedRow = jt.getSelectedRow();
				String localwspname = jt.getModel().getValueAt(selectedRow, LocalID).toString();
				listDialog.dispose();
				String wsFilename = wspdir+localwspname;
				OpenTask openTask = ws.new OpenTask(ProgressItem.INDETERMINATE_TYPE, "Workspace is being loaded.", wsFilename);
				pdnonmodal.executeTask(openTask);
	
				wspId = Integer.valueOf(jt.getModel().getValueAt(selectedRow, IdID).toString());
			}
		});
		bp.add(openLocalBtn);
		renameLocalBtn.setEnabled(false);
		renameLocalBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int selectedRow = jt.getSelectedRow();
				String localwspname = jt.getValueAt(selectedRow, LocalID).toString();
				String wsFilename = wspdir+localwspname;
				File f = new File(wsFilename);
				String newName = JOptionPane.showInputDialog("Enter new local workspace name:");
				if (newName!=null){
					if (!newName.endsWith(".wsp"))
						newName = newName+".wsp";
					File nf = new File(wspdir+newName);
					if (nf.exists()){
						int n = JOptionPane.showConfirmDialog(null, 
								"This local workspace name already exists. Do you want to overwrite it?",
								"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (n == JOptionPane.CANCEL_OPTION || n == JOptionPane.NO_OPTION)
							return;
					}
					f.renameTo(nf);
					listDialog.dispose();
					int id = Integer.valueOf(jt.getValueAt(selectedRow, IdID).toString());
					WorkspaceServiceClient.wsprenames.put(id, newName);
				}
			}
		});
		bp.add(renameLocalBtn);
		saveLocalBtn.setEnabled(false);
		saveLocalBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int selectedRow = jt.getSelectedRow();
				String localwspname = jt.getValueAt(selectedRow, LocalID).toString();
				String wsFilename = wspdir+localwspname;
				SaveTask task = new WorkspaceHandler().new SaveTask(ProgressItem.INDETERMINATE_TYPE, "Workspace is being saved.", wsFilename, false);
				pdmodal.executeTask(task);
			}
		});
		bp.add(saveLocalBtn);
		//release lock
		releaselockBtn.setEnabled(false);
		releaselockBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				releaseLock("RELEASE");
			}
		});
		bp.add(releaselockBtn);
		//break lock
		breaklockBtn.setEnabled(false);
		breaklockBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				releaseLock("BREAK");
			}
		});
		bp.add(breaklockBtn);
		//delete
		removeBtn.setEnabled(false);
		removeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
						removeWsp();
				
			}
		});
		bp.add(removeBtn);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				listDialog.dispose();
			}
		});
		bp.add(cancel);
		jp.add(bp, BorderLayout.SOUTH);
		jpw.add(jp);

		JTabbedPane jtp = new JTabbedPane();
		JPanel descpanel = new JPanel(new BorderLayout());
		descpanel.add(jtaDesc, BorderLayout.CENTER);
		descbtn.setEnabled(false);
		descbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addDescription();
			}
		});
		descpanel.add(descbtn, BorderLayout.LINE_END);
		jtp.addTab("Description", descpanel);
		jtanno.setPreferredScrollableViewportSize(jtanno.getPreferredSize());
		jtp.addTab("Annotation", new JScrollPane(jtanno));
		jthist.setPreferredScrollableViewportSize(jthist.getPreferredSize());
		jtp.addTab("History", new JScrollPane(jthist));
		jtuser.setPreferredScrollableViewportSize(jtuser.getPreferredSize());
		jtp.addTab("Users", new JScrollPane(jtuser));
		JPanel addannopanel = new JPanel(new BorderLayout());
		addannopanel.add(jtaAnno, BorderLayout.CENTER);
		addannobtn.setEnabled(false);
		addannobtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addAnnotation();
			}
		});
		addannopanel.add(addannobtn, BorderLayout.LINE_END);
		jtp.addTab("Add Annotation", addannopanel);
		JPanel adduserpanel = new JPanel(new GridLayout(3, 2));
		adduserpanel.add(new JLabel("Enter username: "));
		adduserpanel.add(jtfUsername);
		adduserpanel.add(new JLabel("Choose user access priviledge: "));
		adduserpanel.add(jcb);
		adduserpanel.add(new JLabel("Give user access to this workspace: "));
		adduserbtn.setEnabled(false);
		adduserbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addUser();
			}
		});
		adduserpanel.add(adduserbtn);
		jtp.addTab("Add User Access", adduserpanel);
		JPanel addgrouppanel = new JPanel(new GridLayout(3, 2));
		addgrouppanel.add(new JLabel("Choose group: "));
		addgrouppanel.add(grpnames);
		addgrouppanel.add(new JLabel("Choose group access priviledge: "));
		addgrouppanel.add(grpjcb);
		addgrouppanel.add(new JLabel("Give group access to this workspace: "));
		addgroupbtn.setEnabled(false);
		addgroupbtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addGroupAccess();
			}
		});
		addgrouppanel.add(addgroupbtn);
		jtp.addTab("Add Group Access", addgrouppanel);
		jpw.add(jtp);

		// else JOptionPane.showMessageDialog(null, "No user profiles for remote workspace available");

		if (wspId > 0){
			for (int i = 0; i < jt.getRowCount(); i++){
				if (Integer.valueOf(jt.getValueAt(i, IdID).toString()) == wspId){
					jt.getSelectionModel().setSelectionInterval(i, i);
				}
			}
		}

		listDialog.pack();
		listDialog.setSize(LDWidth, LDHeight);
		if (listOnly) listDialog.setSize(LDWidth, SLDHeight);
		listDialog.setLocationRelativeTo(null);
		listDialog.setVisible(true);
	}

	private void selectionChanged(int selectedRow){
		String localwspname = jt.getValueAt(selectedRow, LocalID).toString();
		if (!localwspname.equals("")){
			openLocalBtn.setEnabled(true);
			renameLocalBtn.setEnabled(true);
			saveLocalBtn.setEnabled(true);
		}
		if (((Boolean)jt.getValueAt(selectedRow, LockID))==Boolean.TRUE){
			releaselockBtn.setEnabled(true);
			breaklockBtn.setEnabled(true);
		} else
			removeBtn.setEnabled(true);

		String lkusr = jt.getValueAt(selectedRow, LkUsrID).toString();
		if (!lkusr.equals("")){
			downloadBtn.setEnabled(true);
			descbtn.setEnabled(true);
			addannobtn.setEnabled(true);
			adduserbtn.setEnabled(true);
			addgroupbtn.setEnabled(true);
			String desc = (String) jt.getModel().getValueAt(selectedRow, jt.getColumnCount());
			jtaDesc.setText(desc);
	
			String wspid = (String) jt.getValueAt(selectedRow, IdID);
			
			HashMap<String, String[][]> hm = new HashMap<String, String[][]>();
			String[][] listhist = null, listuser = null, listanno = null;
			try {
				hm = WorkspaceServiceClient.getSavedWorkspaceInfo("INFO"+wspid);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+"\n\n"+
						"Exception: could not retrieve information for remote workspace "+ wspid,
						"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
			}
			if (hm!=null){
				listhist = hm.get("HIST");
				if (listhist != null){
					jthist.setModel(new DetailTableModel(listhist, WorkspaceServiceClient.colhist));
				}
				listuser = hm.get("GETUSER");
				if (listuser!=null){
					jtuser.setModel(new DetailTableModel(listuser, WorkspaceServiceClient.coluser));
				}
				listanno = hm.get("GETANNO");
				if (listanno != null){
					jtanno.setModel(new DetailTableModel(listanno, WorkspaceServiceClient.colanno));
				}
			}
		}
	}

	private void releaseLock(String type){
		int selectedRow = jt.getSelectedRow();
		String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
		String res = "";
		try{
			getUserInfo();
			if (userInfo == null) return;
			if (userInfo == "") {
				JOptionPane
						.showMessageDialog(
								null,
								"Please make sure you entered valid username and password",
								"Invalid User Account",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			res = WorkspaceServiceClient.modifySavedWorkspace(type+remoteId+META_DELIMIETER+userInfo);
		}catch(Exception e1){
			JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    			"GeWorkbench cannot release lock for remote workspace via axis2 web service.\n" +
	    			"Please try again later or report the problem to geWorkbench support team.\n",
	    			"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
		}
		if (res!=null && res.contains("success")){
			JOptionPane.showMessageDialog(null, res);
			listDialog.dispose();
		} else
			JOptionPane.showMessageDialog(null, "Could not release remote workspace "+remoteId);
	}
	
	private void addDescription(){
		int selectedRow = jt.getSelectedRow();
		if (selectedRow > -1){
			String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
			//if (!Boolean.valueOf(jt.getModel().getValueAt(selectedRow, LockedID).toString()))
			{
				String res = "";
				try{
					getUserInfo();
					if (userInfo == null) return;
					if (userInfo == "") {
						JOptionPane
								.showMessageDialog(
										null,
										"Please make sure you entered valid username and password",
										"Invalid User Account",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String desc = jtaDesc.getText();
					if (desc.contains(META_DELIMIETER)) desc = desc.replaceAll(META_DELIMIETER, "_");
					res = WorkspaceServiceClient.modifySavedWorkspace("DESC"+remoteId+META_DELIMIETER+desc+META_DELIMIETER+userInfo);
				}catch(Exception e1){
					JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    					"GeWorkbench cannot change description for remote workspace via axis2 web service.\n" +
	    					"Please try again later or report the problem to geWorkbench support team.\n",
	    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
				}
				if (res!=null && res.contains("success")){
					JOptionPane.showMessageDialog(null, res);
					listDialog.dispose();
				} else
					JOptionPane.showMessageDialog(null, "Could not change description for remote workspace "+remoteId);
			}
		}
	}

	private void addAnnotation(){
		int selectedRow = jt.getSelectedRow();
		if (selectedRow > -1){
			String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
			//if (!Boolean.valueOf(jt.getModel().getValueAt(selectedRow, LockedID).toString()))
			{
				String res = "";
				try{
					getUserInfo();
					if (userInfo == null) return;
					if (userInfo == "") {
						JOptionPane
								.showMessageDialog(
										null,
										"Please make sure you entered valid username and password",
										"Invalid User Account",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String anno = jtaAnno.getText();
					if (anno.contains(META_DELIMIETER)) anno = anno.replaceAll(META_DELIMIETER, "_");
					res = WorkspaceServiceClient.modifySavedWorkspace("ADDANNO"+remoteId+META_DELIMIETER+anno+META_DELIMIETER+userInfo);
				}catch(Exception e1){
					JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    					"GeWorkbench cannot add annotation for remote workspace via axis2 web service.\n" +
	    					"Please try again later or report the problem to geWorkbench support team.\n",
	    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
				}
				if (res!=null && res.contains("success")){
					JOptionPane.showMessageDialog(null, res);
					listDialog.dispose();
				} else
					JOptionPane.showMessageDialog(null, "Could not add annotation for remote workspace "+remoteId);
			}
		}
	}

	private void addUser(){
		int selectedRow = jt.getSelectedRow();
		if (selectedRow > -1){
			String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
			//if (!Boolean.valueOf(jt.getModel().getValueAt(selectedRow, LockedID).toString()))
			{
				String res = "";
				try{
					getUserInfo();
					if (userInfo == null) return;
					if (userInfo == "") {
						JOptionPane
								.showMessageDialog(
										null,
										"Please make sure you entered valid username and password",
										"Invalid User Account",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String uname = jtfUsername.getText();
					String group = (String)jcb.getSelectedItem();
					res = WorkspaceServiceClient.modifySavedWorkspace("ADDUSER"+remoteId+META_DELIMIETER+uname+META_DELIMIETER+group+META_DELIMIETER+userInfo);
				}catch(Exception e1){
					JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    					"GeWorkbench cannot give user access to remote workspace via axis2 web service.\n" +
	    					"Please try again later or report the problem to geWorkbench support team.\n",
	    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
				}
				if (res!=null && res.contains("success")) {
					JOptionPane.showMessageDialog(null, res);
					listDialog.dispose();
				} else
					JOptionPane.showMessageDialog(null, "Could not give user access to remote workspace "+remoteId);
			}
		}
	}

	private void addGroupAccess(){
		int selectedRow = jt.getSelectedRow();
		if (selectedRow > -1){
			String remoteId = jt.getModel().getValueAt(selectedRow, IdID).toString();
			//if (!Boolean.valueOf(jt.getModel().getValueAt(selectedRow, LockedID).toString()))
			{
				String res = "";
				try{
					getUserInfo();
					if (userInfo == null) return;
					if (userInfo == "") {
						JOptionPane
								.showMessageDialog(
										null,
										"Please make sure you entered valid username and password",
										"Invalid User Account",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String uname = (String)grpnames.getSelectedItem();
					String group = (String)grpjcb.getSelectedItem();
					res = WorkspaceServiceClient.modifySavedWorkspace("ADDGROUP"+remoteId+META_DELIMIETER+uname+META_DELIMIETER+group+META_DELIMIETER+userInfo);
				}catch(Exception e1){
					JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    					"GeWorkbench cannot give group access to remote workspace via axis2 web service.\n" +
	    					"Please try again later or report the problem to geWorkbench support team.\n",
	    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
				}
				if (res!=null && res.contains("success")) {
					JOptionPane.showMessageDialog(null, res);
					listDialog.dispose();
				} else
					JOptionPane.showMessageDialog(null, "Could not give group access to remote workspace "+remoteId);
			}
		}
	}


	private void newWspDialog(){

		FormLayout layout = new FormLayout("left:max(25dlu;pref), 10dlu, left:max(50dlu;pref)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		newDialog = new JDialog();
		name = new JTextField(20);
		desc = new JTextField(20);
		builder.append("Workspace title", name);
		builder.append("Description", desc);
		JButton upload = new JButton("Upload");
		upload.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String wsFilename = name.getText();
				if( wsFilename==null || wsFilename.length()==0 ){
					JOptionPane.showMessageDialog(newDialog, "Workspace name cannot be blank.");
					return;
				}
				newDialog.dispose();

				if (wsFilename.contains(META_DELIMIETER)) wsFilename = wsFilename.replaceAll(META_DELIMIETER, "_");
				String description = desc.getText();
				if (description.contains(META_DELIMIETER))	description = description.replaceAll(META_DELIMIETER, "_");

				uploadWsp(wsFilename, description, false);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e ){
				newDialog.dispose();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(upload);
		buttons.add(cancel);
		builder.append("", buttons);
		newDialog.getRootPane().setDefaultButton(upload);
		newDialog.add(builder.getPanel());
		newDialog.setTitle("Upload New Workspace");
		newDialog.pack();
		newDialog.setVisible(true);
		newDialog.setLocationRelativeTo(null);
	}

	private void openWsp(String remoteName, String wsFilename, String selectedco) {
		//pdnonmodal.cancelAllTasks();
		OpenRemoteTask openTask = new OpenRemoteTask(ProgressItem.INDETERMINATE_TYPE, 
				"Remote workspace "+remoteName+" is being retrieved and loaded.", wsFilename, selectedco);
		pdnonmodal.executeTask(openTask);
	}

	private class OpenRemoteTask extends OpenTask {
		private String lastSync = "";
		OpenRemoteTask(int pbtype, String message, String filename, String selectedco) {
			ws.super(pbtype, message, filename);
			lastSync = selectedco;
		}
		@Override
		protected Void doInBackground() throws Exception {
			String dldwspname = "";
			try {
				dldwspname = WorkspaceServiceClient.getSavedWorkspace("DOWNLOAD"+filename);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, 
    					"Exception: could not retrieve remote workspace "+filename,
    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}
			WorkspaceServiceClient.cleanCache();

			filename = dldwspname;
			super.doInBackground();

			return null;
		}
		@Override
		protected void done(){
			super.done();
			checkoutstr = lastSync;
			dirty = false;
		}
	}
	protected static boolean checkWspdir(){
		File dir = new File(wsproot);
		if (!dir.exists()){
			if (!dir.mkdir()){
				JOptionPane.showMessageDialog(null, "Cannot create wsp dir "+wsproot);
				return false;
			}
		}

		URL url = null;
		try{
			url = new URL(GlobalPreferences.getInstance().getRWSP_URL());
		}catch(MalformedURLException e){
			JOptionPane.showMessageDialog(null, "Malformed RWSP URL detected -- replaced by default RWSP URL.");
			try{
				url = new URL(GlobalPreferences.DEFAULT_RWSP_URL);
			}catch(Exception e1){
				e1.printStackTrace();
				return false;
			}
		}
		wspdir = wsproot + url.getHost() + url.getPort() + FilePathnameUtils.FILE_SEPARATOR;
		dir = new File(wspdir);
		if (!dir.exists()){
			if (!dir.mkdir()){
				JOptionPane.showMessageDialog(null, "Cannot create wsp dir "+wspdir);
				return false;
			}
		}
		
		WorkspaceServiceClient.cachedir = wspdir+"axis2cache";
		return true;
	}
	protected void uploadWsp(){
		if(!checkWspdir()) return;

		if (wspId == 0) //insert
			newWspDialog();
		else //update
			updateWsp(wspId, false);
	}
	
	private void uploadWsp(String wsFilename, String desc, boolean terminating) {
		getUserInfo();
		if (userInfo == null) return;
		if (userInfo == "") {
			JOptionPane
					.showMessageDialog(
							null,
							"Please make sure you entered valid username and password",
							"Invalid User Account",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		UploadNewTask saveTask = new UploadNewTask(ProgressItem.INDETERMINATE_TYPE, 
				"Remote workspace "+wsFilename+" is being saved and uploaded.", 
				"0"+META_DELIMIETER+wsFilename+META_DELIMIETER+desc+META_DELIMIETER+userInfo, terminating);
		pdmodal.executeTask(saveTask);
	}

	private void updateWsp(int id, boolean terminating){
		if (!dirty)
			JOptionPane.showMessageDialog(null, "The workspace hasn't been changed");
		else{
			getUserInfo();
			if (userInfo == null) return;
			if (userInfo == "") {
				JOptionPane
						.showMessageDialog(
								null,
								"Please make sure you entered valid username and password",
								"Invalid User Account",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			String fname = id+".wsp";
			String rename = WorkspaceServiceClient.wsprenames.get(id); 
			if (rename!=null) fname=rename;
			UpdateRemoteTask saveTask = new UpdateRemoteTask(ProgressItem.INDETERMINATE_TYPE, 
					"Remote workspace "+id+" is being saved and uploaded.", wspdir+fname, 
					wspId+META_DELIMIETER+checkoutstr+META_DELIMIETER+userInfo, terminating);
			pdmodal.executeTask(saveTask);
		}
	}
	
	private class UpdateRemoteTask extends SaveTask {
		protected String meta = "";
		UpdateRemoteTask(int pbtype, String message, String filename, String metadata, boolean terminating) {
			ws.super(pbtype, message, filename, terminating);
			meta = metadata;
		}
		@Override
		protected Void doInBackground() throws FileNotFoundException, IOException {
			//get server time as checkoutstr
			try {
				checkoutstr = Client.transferFile(null, meta);
			} catch (RemoteException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    				"GeWorkbench cannot upload to remote workspace via axis2 web service.\n" +
	    				"Please try again later or report the problem to geWorkbench support team.\n",
	    				"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
			}
			dirty = false;
			//save wsp
			super.doInBackground();
			//upload saved wsp file
			try {
				Client.transferFile(new File(filename), wspId+META_DELIMIETER+userInfo);
			} catch (RemoteException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
    					"GeWorkbench cannot upload to remote workspace via axis2 web service.\n" +
    					"Please try again later or report the problem to geWorkbench support team.\n",
    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}
			dirty = false;

			int a = JOptionPane.showConfirmDialog(null, "The workspace was successfully uploaded.\n"+
					"You are the only one who can modify the uploaded workspace.\n" + "Do you wish to release the lock?", 
					"Release Workspace Lock", JOptionPane.YES_NO_OPTION);
			if (a == JOptionPane.NO_OPTION ||a == JOptionPane.CLOSED_OPTION)
				return null;
			String res = "";
			try{
				res = WorkspaceServiceClient.modifySavedWorkspace("RELEASE"+wspId+META_DELIMIETER+userInfo);
			}catch(Exception e1){
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
    					"GeWorkbench cannot release lock for remote workspace via axis2 web service.\n" +
    					"Please try again later or report the problem to geWorkbench support team.\n",
    					"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}
			if (res!=null && res.contains("success"))
				JOptionPane.showMessageDialog(null, res);
			else
				JOptionPane.showMessageDialog(null, "Could not release remote workspace "+wspId);
			return null;
		}
	}
	
	private class UploadNewTask extends UpdateRemoteTask {
		UploadNewTask(int pbtype, String message, String metadata, boolean terminating) {
			super(pbtype, message, "", metadata, terminating);
		}
		@Override
		protected Void doInBackground() throws FileNotFoundException, IOException {
			String id = "0";
			// get auto-increment id in workspace table
			try {
				id = Client.transferFile(null, meta);
			} catch (RemoteException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
	    				"GeWorkbench cannot upload to remote workspace via axis2 web service.\n" +
	    				"Please try again later or report the problem to geWorkbench support team.\n",
	    				"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
			}
			// upload wsp file with just created id, no lock/sync check
			wspId = Integer.valueOf(id);
			filename = wspdir+id+".wsp";
			meta = wspId+META_DELIMIETER+userInfo;
			super.doInBackground();
			return null;
		}
	}
	
	private void removeWsp(){
		int selectedRow = jt.getSelectedRow();
		String wsFilename = jt.getModel().getValueAt(selectedRow, IdID).toString();

		getUserInfo();
		if (userInfo == null) return;
		if (userInfo == "") {
			JOptionPane
					.showMessageDialog(
							null,
							"Please make sure you entered valid username and password",
							"Invalid User Account",
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		RemoveRemoteTask removeTask = new RemoveRemoteTask(ProgressItem.INDETERMINATE_TYPE,
				"Remote workspace "+wsFilename+" is being removed.", wsFilename);
		pdnonmodal.executeTask(removeTask);
	}

	private class RemoveRemoteTask extends ProgressTask<String, Void> {
		private String filename;
		RemoveRemoteTask(int pbtype, String message, String fname) {
			super(pbtype, message);
			filename = fname;
		}
		@Override
		protected String doInBackground() throws FileNotFoundException, IOException {
			String res = "";
			try {
				res = WorkspaceServiceClient.modifySavedWorkspace("REMOVE"+filename+META_DELIMIETER+userInfo);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
				"GeWorkbench cannot remove remote workspace" +filename+".\n"+
				"Please try again later or report the problem to geWorkbench support team.\n",
				"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}

			return res;
		}
		@Override
		protected void done(){
			if (isCancelled()){
				pdnonmodal.removeTask(this);
				return;
			}
			String res = "";
			try {
				res = get();
			} catch (ExecutionException e) {
				e.getCause().printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not remove remote workspace "+filename,
						"Remove Remote Workspace Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not remove remote workspace "+filename+"\n"+e,
						"Remove Remote Workspace Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				pdnonmodal.removeTask(this);
			}
			if (res!=null && res.contains("success")) {
				JOptionPane.showMessageDialog(null, res);
				listDialog.dispose();
				wspId = 0;
			} else
				JOptionPane.showMessageDialog(null, "Could not remove remote workspace "+filename);
		}
	}

	protected static void treeModified(){
		if (wspId > 0){
			Timestamp now = new Timestamp(new Date().getTime());
			now.setNanos(0);
			lastchange = now.toString();

			if (dirty == false){
				dirty = true;

				AccessRemoteTask accessTask = new AccessRemoteTask(ProgressItem.INDETERMINATE_TYPE,
				"Remote workspace user access is being retrieved.");
				pdnonmodal.executeTask(accessTask);
			}
		}
	}
	
	private static class AccessRemoteTask extends ProgressTask<String, Void> {
		AccessRemoteTask(int pbtype, String message) {
			super(pbtype, message);
		}
		@Override
		protected String doInBackground() throws FileNotFoundException, IOException {
			String res = "";
			try {
				res = WorkspaceServiceClient.modifySavedWorkspace("ACCESS"+wspId+META_DELIMIETER+userInfo.split(USER_INFO_DELIMIETER)[0]);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
				"It is possible that changes made to the local workspace copy may not be transferable to the server.\n"+
				"GeWorkbench cannot retrieve user access for remote workspace " +wspId+".\n"+
				"Please try again later or report the problem to geWorkbench support team.\n",
				"Database connection/data transfer error", JOptionPane.ERROR_MESSAGE);
    			return null;
			}

			return res;
		}
		@Override
		protected void done(){
			if (isCancelled()){
				pdnonmodal.removeTask(this);
				return;
			}
			String res = "";
			try {
				res = get();
			} catch (ExecutionException e) {
				e.getCause().printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not retrieve user access for remote workspace "+wspId,
						"Retrieve User Access Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not retrieve user access for remote workspace "+wspId+"\n"+
						"It is possible that changes made to the local workspace copy may not be transferable to the server.\n"+e,
						"Remove User Access Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				pdnonmodal.removeTask(this);
			}
			if (res!=null) {
				JOptionPane.showMessageDialog(null, res);
			} else
				JOptionPane.showMessageDialog(null, "Could not retrieve user access for remote workspace "+wspId);
		}
	}
}