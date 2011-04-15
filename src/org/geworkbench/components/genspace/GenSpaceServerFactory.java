//package genspace.ui;
package org.geworkbench.components.genspace;


import java.util.HashMap;
import java.util.List;

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
			System.setProperty("org.omg.CORBA.ORBInitialHost", RuntimeEnvironmentSettings.SERVER);
			System.setProperty("com.sun.CORBA.encoding.ORBEnableJavaSerialization","true");
			System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
//			System.setProperty("com.sun.CORBA.giop.ORBFragmentSize","1024000");
//			System.setProperty("com.sun.CORBA.giop.ORBBufferSize","1024000");
			System.setProperty("com.sun.corba.ee.transport.ORBMaximumReadByteBufferSize", "3000000");
			System.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
			System.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
			System.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			if(ctx == null)
			{
				System.out.println("getting context");
				ctx = new InitialContext();
				System.out.println("have context");
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
		user = getPublicFacade().register(u);
		if(user != null)
			return true;
		return false;
	}
	static ProgrammaticLogin pm = new ProgrammaticLogin();
	@SuppressWarnings("deprecation")
	public static boolean userLogin(String username, String password) {
		
		System.setProperty("java.security.auth.login.config", "components/genspace/src/org/geworkbench/components/genspace/login.conf");
		try {
			pm.login(username, password,"GELogin",true);
			InitialContext ctx = new InitialContext();
			ctx.lookup("org.geworkbench.components.genspace.server.UserFacadeRemote");
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

		getUserOps().updateUser(user);
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
		Friend f = user2.isFriendsWith(getUser());
		if(f != null && f.isVisible())
		{
			return true;
		}
		//Check the networks
		for(UserNetwork u1 : user2.getNetworks())
		{
			if(u1.isVisible())
				for(UserNetwork u2 : getUser().getNetworks())
				{
					if(u2.getNetwork().equals(u1.getNetwork()))
						return true;
				}
		}
		return false;
	}

	
}
