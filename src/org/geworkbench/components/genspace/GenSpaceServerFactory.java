//package genspace.ui;
package org.geworkbench.components.genspace;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;


import org.apache.log4j.Logger;
import org.geworkbench.components.genspace.entity.Friend;
import org.geworkbench.components.genspace.entity.Tool;
import org.geworkbench.components.genspace.entity.User;
import org.geworkbench.components.genspace.entity.UserNetwork;
import org.geworkbench.components.genspace.server.FriendFacadeRemote;
import org.geworkbench.components.genspace.server.NetworkFacadeRemote;
import org.geworkbench.components.genspace.server.PublicFacadeRemote;
import org.geworkbench.components.genspace.server.ToolInformationProvider;
import org.geworkbench.components.genspace.server.UsageInformationRemote;
import org.geworkbench.components.genspace.server.UserFacadeRemote;
import org.geworkbench.components.genspace.server.WorkflowRepositoryRemote;


import com.sun.appserv.security.ProgrammaticLogin;

public class GenSpaceServerFactory {

	private static User user;
	public static Logger logger = Logger.getLogger(GenSpaceServerFactory.class);
	private static UserFacadeRemote userFacade;
	private static UsageInformationRemote usageFacade;
	private static FriendFacadeRemote friendFacade;
	private static NetworkFacadeRemote networkFacade;
	private static PublicFacadeRemote publicFacade;
	private static WorkflowRepositoryRemote workflowFacade;
	private static InitialContext ctx;

	
	private synchronized static Object getRemote(String remoteName)
	{
		if(Thread.currentThread().getName().contains("AWT-EventQueue"))
		{
			throw new IllegalThreadStateException("You may not attempt to access the remote server from an AWT/Swing worker thread");
		}	
		try {
			Properties props = new Properties();
			props.setProperty("org.omg.CORBA.ORBInitialHost", RuntimeEnvironmentSettings.SERVER);
//			props.setProperty("com.sun.corba.ee.encoding.ORBEnableJavaSerialization","true");
//			props.setProperty("com.sun.CORBA.encoding.ORBEnableJavaSerialization", "true");
			props.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
//			props.setProperty("com.sun.CORBA.giop.ORBFragmentSize","1024000");
//			props.setProperty("com.sun.CORBA.giop.ORBBufferSize","1024000");
//			props("com.sun.corba.ee.transport.ORBMaximumReadByteBufferSize", "3000000");
			props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
			props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
			props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			if(ctx == null)
			{
				ctx = new InitialContext(props);
			}
			if(RuntimeEnvironmentSettings.tools == null)
			{
				RuntimeEnvironmentSettings.tools = new HashMap<Integer, Tool>();
				for(Tool t : ((PublicFacadeRemote) ctx.lookup("org.geworkbench.components.genspace.server.PublicFacadeRemote")).getAllTools())
				{
					RuntimeEnvironmentSettings.tools.put(t.getId(), t);
				}
			}
			return ctx.lookup("org.geworkbench.components.genspace.server."+remoteName+"Remote");
		} catch (NamingException e) {
			logger.fatal("Unable find remote object for " + remoteName,e);
		}
		return null;
	}
	public synchronized static WorkflowRepositoryRemote getWorkflowOps()
	{
		if(workflowFacade == null)
			workflowFacade = (WorkflowRepositoryRemote) GenSpaceServerFactory.getRemote("WorkflowRepository");
		return workflowFacade;
	}
	public synchronized static UserFacadeRemote getUserOps()
	{
		if(userFacade == null)
			userFacade = (UserFacadeRemote) GenSpaceServerFactory.getRemote("UserFacade");
		return userFacade;
	}
	
	public synchronized static PublicFacadeRemote getPublicFacade()
	{
		if(publicFacade == null)
			publicFacade = (PublicFacadeRemote) GenSpaceServerFactory.getRemote("PublicFacade");
		return publicFacade;
	}
	
	public synchronized static UsageInformationRemote getPrivUsageFacade()
	{
		if(user == null)
			return null;
		if(usageFacade == null)
			usageFacade = (UsageInformationRemote) GenSpaceServerFactory.getRemote("UsageInformation");
		return usageFacade;
	}
	public synchronized static ToolInformationProvider getUsageOps()
	{
		if(user != null)
			return getPrivUsageFacade();
		else
			return getPublicFacade();
	}

	public synchronized static FriendFacadeRemote getFriendOps()
	{
		if(friendFacade == null)
			friendFacade = (FriendFacadeRemote) GenSpaceServerFactory.getRemote("FriendFacade");
		return friendFacade;
	}
	public synchronized static NetworkFacadeRemote getNetworkOps()
	{
		if(networkFacade == null)
			networkFacade = (NetworkFacadeRemote) GenSpaceServerFactory.getRemote("NetworkFacade");
		return networkFacade;
	}
	
	
	public GenSpaceServerFactory() {
		super();
	}

	
	public static User getUser() {
		return user;
	}
	public static boolean userRegister(User u) {
		System.out.println("Sending up user of size " + getObjectSize(u));
		user = getPublicFacade().register(RuntimeEnvironmentSettings.writeObject(u));
		System.out.println("Sent info");
		if(user != null)
			return true;
		return false;
	}
	public static String getObjectSize(Serializable s)
	{
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(s);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		 
		 return " " + ((double) baos.size())/(1024) + " KB";
	}
	static ProgrammaticLogin pm = new ProgrammaticLogin();
	@SuppressWarnings("deprecation")
	public static boolean userLogin(String username, String password) {
		
		System.setProperty("java.security.auth.login.config", "components/genspace/src/org/geworkbench/components/genspace/login.conf");
		try {
			pm.login(username, password,"GELogin",true);
			user = getUserOps().getMe();
		} catch (Exception e) {
			return false;
		}
		return true;
	
	}
	public static void updateCachedUser()
	{
		user = getUserOps().getMe();
	}
	public static boolean userUpdate() {

		getUserOps().updateUser(RuntimeEnvironmentSettings.writeObject(user));
		return true;
	}

	public static List<UserNetwork> getAllNetworks() {
		return user.getNetworks();
	}

	public static String getUsername() {
		if(user == null)
			return null;
		return user.getUsername();
	}

	public static boolean isLoggedIn() {
		return user != null;
	}

	public static void logout() {
		try {
			pm.logout(true);
			userFacade = null;
			usageFacade = null;
			friendFacade = null;
			networkFacade = null;
			publicFacade = null;
			workflowFacade = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		user = null;
	}

	/**
	 * Returns if the currently logged in user may view the profile of the specified user
	 * @param user2
	 * @return
	 */
	public static boolean isVisible(User user2) {
		return user2.isVisible();
	}

	
}
