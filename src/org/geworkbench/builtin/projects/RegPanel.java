package org.geworkbench.builtin.projects;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.geworkbench.util.FilePathnameUtils;
import org.geworkbench.util.ProgressDialog;
import org.geworkbench.util.ProgressItem;
import org.geworkbench.util.ProgressTask;

/**
 * User Registration GUI
 * $Id$
 */
public class RegPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 7446637434069686093L;
	private ProgressDialog pdnonmodal = ProgressDialog.create(ProgressDialog.NONMODAL_TYPE);
	private JFrame jframe;
	private JTextField userId;
	private JPasswordField password;
	private JPasswordField passwordDup;
	private JTextField fname;
	private JTextField lname;
	private JTextField labaff;
	private JTextField email;
	private JTextField phone;
	private JTextField addr1;
	private JTextField addr2;
	private JTextField city;
	private JTextField state;
	private JTextField zipcode;
	private JButton save, reset, b_login;
	private JLabel jp, jpc, jpo = new JLabel("");
	private JPasswordField passwordo = new JPasswordField(10);
	private static final String space = "        ";

	public RegPanel() {
		this.setSize(500, 500);
		this.setLayout(new  GridLayout(8, 4));
		JLabel j1 = new JLabel(space+"Enter sign in user id *");
		userId = new JTextField("", 10);
		add(j1);
		add(userId);

		jp =        new JLabel(space+"Select your password *");
		password = new JPasswordField(10);
		add(jp);
		add(password);

		jpc =       new JLabel(space+"Confirm your password *");
		passwordDup = new JPasswordField(10);
		add(jpc);
		add(passwordDup);

		JLabel j2 = new JLabel(space+"First Name *");
		fname = new JTextField("", 10);
		add(j2);
		add(fname);

		JLabel j3 = new JLabel(space+"Last Name *");
		lname = new JTextField("", 10);
		add(j3);
		add(lname);

		JLabel j4 = new JLabel(space+"Lab Affiliation *");
		labaff= new JTextField("", 10);
		add(j4);
		add(labaff);

		JLabel em = new JLabel(space+"Email Address");
		email= new JTextField("", 10);
		add(em);
		add(email);

		JLabel ph = new JLabel(space+"Phone");
		phone = new JTextField("", 10);
		add(ph);
		add(phone);


		JLabel j5 = new JLabel(space+"Address 1");
		addr1 = new JTextField("", 10);
		add(j5);
		add(addr1);

		JLabel j6 = new JLabel(space+"Address 2");
		addr2 = new JTextField("", 10);
		add(j6);
		add(addr2);

		JLabel j7 = new JLabel(space+"City");
		city = new JTextField("", 10);
		add(j7);
		add(city);

		JLabel j9 = new JLabel(space+"State");
		state = new JTextField("", 10);
		add(j9);
		add(state);

		JLabel j8 = new JLabel(space+"ZIP Code");
		zipcode = new JTextField("", 10);
		add(j8);
		add(zipcode);
		add(jpo);add(passwordo);passwordo.setVisible(false);

		save = new JButton("Register");
		save.addActionListener(this);
		reset = new JButton("Reset");
		reset.addActionListener(this);
		b_login = new JButton("Login");
		b_login.addActionListener(this);		

		add(new JLabel(""));
		add(save);
		add(reset);
		add(b_login);

		save.setEnabled(true);
	}

	private RegisterBean getBean()
	{
		RegisterBean bean = new RegisterBean();
		bean.setUName(userId.getText());
		bean.setPassword(getEncodedChars(password.getPassword()));
		bean.setFName(fname.getText());
		bean.setLName(lname.getText());
		bean.setLabAffiliation(labaff.getText());
		bean.setEmail(email.getText());
		bean.setPhoneNumber(phone.getText());
		bean.setAddr1(addr1.getText());
		bean.setAddr2(addr2.getText());
		bean.setCity(city.getText());
		bean.setState(state.getText());
		bean.setZipcode(zipcode.getText());
		if (passwordo.isVisible())
			bean.setOldPasswd(getEncodedChars(passwordo.getPassword()));
		return bean;
	}

	public void changeForUpdate(){
		//remove(b_login);
		userId.setEditable(false);
		jp.setText (space+"Select new password *");
		jpc.setText(space+"Confirm new password *");
		jpo.setText(space+"Enter old password *");
		passwordo.setVisible(true);
		save.setText("Update Profile");
		b_login.setText("Remove Account");
	}

	private void reset(){
		userId.setText("");
		password.setText("");
		passwordDup.setText("");
		passwordo.setText("");
		fname.setText("");
		lname.setText("");
		labaff.setText("");
		email.setText("");
		phone.setText("");
		addr1.setText("");
		addr2.setText("");
		city.setText("");
		state.setText("");
		zipcode.setText("");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b_login){
			if(b_login.getText().equals("Login")){
				jframe.dispose();
				RWspHandler ws = new RWspHandler();
				ws.listWsp(false);
			}else{
				int t = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user account?");
				if (t==JOptionPane.CANCEL_OPTION || t==JOptionPane.NO_OPTION) return;

				RemoveUserTask removeTask = new RemoveUserTask(ProgressItem.INDETERMINATE_TYPE, 
						"Remote workspace user account is being removed.");
				pdnonmodal.executeTask(removeTask);
			}
		} else if (e.getSource() == reset){
			reset();
		} else {
			StringBuffer errMsg = new StringBuffer();
			if (isValid(errMsg)){
				RegisterUserTask openTask = new RegisterUserTask(ProgressItem.INDETERMINATE_TYPE, 
						"Remote workspace user profile is being uploaded.");
				pdnonmodal.executeTask(openTask);
			}else {
				JOptionPane.showMessageDialog(null, errMsg.toString(),
						"Error Information", JOptionPane.INFORMATION_MESSAGE);

				revalidate();
			}
		}

	}
	
	private boolean empty(String str)
	{
		if(str == null || str.equals(""))
			return true;
		else
			return false;
	}

	public boolean isValid(StringBuffer msg) {
		String id = userId.getText();

		char input[] = password.getPassword();
		String pw = new String(input);
		String confirm = new String(passwordDup.getPassword());
		String pwo = new String(passwordo.getPassword());

		String labaffStr = labaff.getText();
		String fn = fname.getText();
		String ln = lname.getText();

		String pho = phone.getText();
		String em = email.getText();

		boolean valid = true;
		if(empty(id))
		{
			msg.append("UserId cannot be empty\n");
			valid = false;
		}
		if(empty(pw))
		{
			msg.append("Password cannot be empty\n");
			valid = false;
		}
		if(empty(confirm))
		{
			msg.append("Confirm password field cannot be empty\n");
			valid = false;
		}
		if(passwordo.isVisible() && empty(pwo))
		{
			msg.append("Old password cannot be empty\n");
			valid = false;
		}
		if(empty(labaffStr))
		{
			msg.append("Lab affiliation cannot be empty\n");
			valid = false;
		}
		if(empty(fn))
		{
			msg.append("First name cannot be empty\n");
			valid = false;
		}
		if(empty(ln))
		{
			msg.append("Last name cannot be empty\n");
			valid = false;
		}
		if(!empty(pw) && !empty(confirm))
		{
			if(!pw.equals(confirm))
			{
				msg.append("Password confirmation does not match password\n");
				valid = false;

			}
		}

		Pattern pattern;
		Matcher matcher;

		// user name special character validation
		if(!empty(id))
		{
			pattern = Pattern.compile("[^0-9a-zA-Z()-_]");

			matcher = pattern.matcher(id);

			if(matcher.find()) 
			{
				msg.append("Invalid user name.\n");
				valid = false;
			}
		}

		// Phone number validation
		if(!empty(pho))
		{
			pattern = Pattern.compile("[^+0-9a-zA-Z()-]");

			matcher = 
				pattern.matcher(pho);

			if(matcher.find()) 
			{
				msg.append("Phone number contains invalid characters\n");
				valid = false;
			}
		}

		// email validation
		if(!empty(em))
		{
			pattern = Pattern.compile("[0-9a-zA-Z()-_.]+@[0-9a-zA-Z()-_.]+");

			matcher = pattern.matcher(em);

			if(!matcher.find()) 
			{
				msg.append("Invalid Email.\n");
				valid = false;
			}
		}
		return valid;
	}

	private class RegisterUserTask extends ProgressTask<String, Void> {
		RegisterUserTask(int pbtype, String message) {
			super(pbtype, message);
		}
		@Override
		protected String doInBackground() throws FileNotFoundException, IOException {
			String res = "";
			try {
				RegisterBean rb = getBean();
				String tmpfile = FilePathnameUtils.getTemporaryFilesDirectoryPath()+"register.bean";
				FileOutputStream os = new FileOutputStream(tmpfile);
				os.write(rb.write());
				os.close();
				res = UploadClient.registerUser(new File(tmpfile));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
				"GeWorkbench cannot register user for remote workspace.\n"+
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
						"Exception: could not register user\n",
						"Register User Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not register user\n"+e,
						"Register User Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				pdnonmodal.removeTask(this);
			}
			if (res!=null && res.contains("success")){
				if (jframe!=null) jframe.dispose();
				JOptionPane.showMessageDialog(null, res);
				password.setText("");
				passwordDup.setText("");
				passwordo.setText("");
			}
			else
				JOptionPane.showMessageDialog(null, "Could not register user ");
		}
	}

	private class RemoveUserTask extends ProgressTask<String, Void> {
		RemoveUserTask(int pbtype, String message) {
			super(pbtype, message);
		}
		@Override
		protected String doInBackground() throws FileNotFoundException, IOException {
			String res = "";
			try {
				res = DownloadClient.delUserFromWorkspace(RWspHandler.userInfo);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage()+".\n\n"+
				"GeWorkbench cannot remove remote workspace user.\n"+
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
						"Exception: could not remove remote workspace user.",
						"Remove Remote Workspace User Error", JOptionPane.ERROR_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception: could not remove remote workspace user.\n"+e,
						"Remove Remote Workspace User Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				pdnonmodal.removeTask(this);
			}
			if (res!=null && res.contains("success")) {
				JOptionPane.showMessageDialog(null, res);
				RWspHandler.listDialog.dispose();
			} else
				JOptionPane.showMessageDialog(null, "Could not remove remote workspace user.");
		}
	}

	public void initFrame()
	{
		jframe = new JFrame();
		jframe.add(this);
		//jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(RWspHandler.LDWidth, RWspHandler.SLDHeight);
		jframe.setLocationRelativeTo(null);
	}    

	public void showFrame()
	{
		jframe.setVisible(true); 	
	} 

	public void hideFrame()
	{
		jframe.setVisible(false); 	
	}     

    private static final String hash = "MD5";    
    /**
     * Encode clear text char[] to digested char[]
     * @param b
     * @return
     */
    public static char[] getEncodedChars(char[] clearText) {
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		byte[] b = null;
    	try {
	    	MessageDigest md = MessageDigest.getInstance(hash);
	    	md.update(new String(clearText).getBytes());
	    	b = md.digest();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	if (b == null)  return null;
		char[] buf = new char[b.length*2];
		int i = 0;
		for (int j=0; j<b.length; j++) {
			buf[i++] = hexDigit[(b[j] >> 4) & 0x0f];
			buf[i++] = hexDigit[b[j] & 0x0f];
		}
    	return buf;
	}

}