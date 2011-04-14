package org.geworkbench.components.genspace.entity;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.geworkbench.components.genspace.GenSpaceServerFactory;

@Entity
@Table(name="registration")
public class User implements Serializable, Comparable<User>{
	
	private static final long serialVersionUID = 2972043173442132575L;
	private int id;
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private String workTitle;
	private String phone;
	private String labAffiliation;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zipcode;
	private int onlineStatus;
	private String interests;
	private int logData;
	private int dataVisibility;
	private java.util.Date createdAt;
	private WorkflowFolder rootFolder;
	
	private List<Friend> friends = new ArrayList<Friend>(); 
	private List<UserNetwork> networks = new ArrayList<UserNetwork>();
	
	private Set<Workflow> workflows = new HashSet<Workflow>();
	private List<WorkflowFolder> folders = new ArrayList<WorkflowFolder>();
	private List<WorkflowComment> workflowComments = new ArrayList<WorkflowComment>();
	private List<ToolComment> toolComments = new ArrayList<ToolComment>();
	private List<Transaction> transactions = new ArrayList<Transaction>();
	private List<IncomingWorkflow> incomingWorkflows = new ArrayList<IncomingWorkflow>();
	private List<IncomingWorkflow> outgoingWorkflows = new ArrayList<IncomingWorkflow>();
	private Set<Network> myOwnedNetworks = new HashSet<Network>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(length=50, nullable = false)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(length=50, nullable = false)
	private String getPassword() {
		return password;
	}
	private void setPassword(String password) {
		this.password = password;
	}
	
	@Column(length=50)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name="first_name", length=50, nullable = false)
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Column(name="last_name", length=50, nullable = false)
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name="work_title", length=50)
	public String getWorkTitle() {
		return workTitle;
	}
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	
	@Column(length=50)
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column(name="lab_affiliation", length=100, nullable = false)
	public String getLabAffiliation() {
		return labAffiliation;
	}
	public void setLabAffiliation(String labAffiliation) {
		this.labAffiliation = labAffiliation;
	}
	
	@Column(length=50)
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	
	@Column(length=50)
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	
	@Column(length=50)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@Column(length=50)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(length=7)
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	@Column(name="online_status")
	public int getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	
	@Lob
	public String getInterests() {
		return interests;
	}
	public void setInterests(String interests) {
		this.interests = interests;
	}
	
	public int getLogData() {
		return logData;
	}
	public void setLogData(int logData) {
		this.logData = logData;
	}
	public int getDataVisibility() {
		return dataVisibility;
	}
	public void setDataVisibility(int dataVisibility) {
		this.dataVisibility = dataVisibility;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public java.util.Date getCreatedAt() { 
		return createdAt;
	}
	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@OneToMany(mappedBy="leftUser")
	public List<Friend> getFriends() { 
		return friends;
	}
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	@OneToMany(mappedBy="user")
	public List<UserNetwork> getNetworks() {
		return networks;
	}
	public void setNetworks(List<UserNetwork> networks) {
		this.networks = networks;
	}
	
	@OneToMany(mappedBy="creator")
	public Set<Workflow> getWorkflows() {
		return workflows;
	}
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows = workflows;
	}
	
	@OneToMany(mappedBy="owner")
	public List<WorkflowFolder> getFolders() {
		return folders;
	}
	public void setFolders(List<WorkflowFolder> folders) {
		this.folders = folders;
	}
	
	@OneToMany(mappedBy="creator")
	public List<WorkflowComment> getWorkflowComments() {
		return workflowComments;
	}
	public void setWorkflowComments(List<WorkflowComment> workflowComments) {
		this.workflowComments = workflowComments;
	}
	
	@OneToMany(mappedBy="creator")
	public List<ToolComment> getToolComments() {
		return toolComments;
	}
	public void setToolComments(List<ToolComment> toolComments) {
		this.toolComments = toolComments;
	}
	
	@OneToMany(mappedBy="user")
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	@OneToMany(mappedBy="receiver")
	public List<IncomingWorkflow> getIncomingWorkflows() {
		return incomingWorkflows;
	}
	public void setIncomingWorkflows(List<IncomingWorkflow> incomingWorkflows) {
		this.incomingWorkflows = incomingWorkflows;
	}
	
	@OneToMany(mappedBy="sender")
	public List<IncomingWorkflow> getOutgoingWorkflows() {
		return outgoingWorkflows;
	}
	public void setOutgoingWorkflows(List<IncomingWorkflow> outgoingWorkflows) {
		this.outgoingWorkflows = outgoingWorkflows;
	}
	
	
	@OneToMany(mappedBy="owner")
	public Set<Network> getMyOwnedNetworks() {
		return myOwnedNetworks;
	}
	public void setMyOwnedNetworks(Set<Network> myOwnedNetworks) {
		this.myOwnedNetworks = myOwnedNetworks;
	}
	
	public WorkflowFolder getRootFolder() {
		return rootFolder;
	}
	public void setRootFolder(WorkflowFolder rootFolder) {
		this.rootFolder = rootFolder;
	}
	
	@Override
	public String toString() {
		String result = "User - username: " + username;
		return result;
	}

	public void setPasswordClearText(String password)
	{
		this.setPassword(this.getEncryptedPassword(password.toCharArray()));

	}
	
	public boolean passwordMatches(String password)
	{
		return this.getEncryptedPassword(password.toCharArray()).equals(this.getPassword());
	}

	@Override
	public int hashCode() {
		return this.getId();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User)
		{
			return ((User) obj).getId() == this.getId();
		}
		return false;
	}
	
	protected final static String HEX_DIGITS = "0123456789abcdef";

	/**
	 * This method is used to encrypt the password using SHA encryption.
	 * 
	 * @param password
	 *            represents the password to be encrypted
	 * @return returns password in encrypted form
	 */
	private String getEncryptedPassword(char[] c_password) {
		String plaintext = new String(c_password);
		
		java.security.MessageDigest d =null;
				try {
					d = java.security.MessageDigest.getInstance("SHA-1");
				} catch (NoSuchAlgorithmException e) {
					GenSpaceServerFactory.logger.error("Error",e);
				}
				d.reset();
				d.update(plaintext.getBytes());
				byte[] hashedBytes =  d.digest();
				StringBuffer sb = new StringBuffer(hashedBytes.length * 2);
		        for (int i = 0; i < hashedBytes.length; i++) {
		             int b = hashedBytes[i] & 0xFF;
		             sb.append(HEX_DIGITS.charAt(b >>> 4)).append(HEX_DIGITS.charAt(b & 0xF));
		        }
		        return sb.toString();	
	}
	public String getFullName() {
		return this.getFirstName() + " " + this.getLastName();
	}
	public Friend isFriendsWith(User u) {
		for(Friend f: getFriends())
		{
			if(f.getRightUser().equals(u))
				return f;
		}
		return null;
	}
	public UserNetwork isInNetwork(Network n) {
		for(UserNetwork un : getNetworks())
		{
			if(un.getNetwork().equals(n))
				return un;
		}
		return null;
	}
	public boolean isVisibleTo(User other)
	{
		Friend f = this.isFriendsWith(other);
		if(f != null && f.isVisible())
		{
			return true;
		}
		//Check the networks
		for(UserNetwork u1 : this.getNetworks())
		{
			if(u1.isVisible())
				for(UserNetwork u2 : other.getNetworks())
				{
					if(u2.getNetwork().equals(u1.getNetwork()))
						return true;
				}
		}
		return false;
	}
	private String na(String s)
	{
		return (s == null ? "N/A" : s);
	}
	public String toHTML() {
		String r = "<html><body><b>" + getFirstName() + " "
		+ getLastName() + " (" + getUsername() + ")</b><br>";
		if (GenSpaceServerFactory.isVisible(this)) {
			r += "<i>"
					+ (getWorkTitle() != null
							&& getWorkTitle() != "" ? getWorkTitle() + " at " : "")
					+ (getLabAffiliation() != null ? getLabAffiliation():  " (affiliation not disclosed)") + "</i><br><br>";
			r += "<b>Research Interests:</b><br />"
					+(getInterests() == null ? "(not disclosed)" : getInterests()) + "<br><br>";
			r += "<b>Contact information:</b><br /><br>Phone: "
					+ na(getPhone()) + "<br>Email: "
					+ na(getEmail()) + "<br><br>Mailing Address:<br>"
					+ na(getAddr1()) + "<br>" + na(getAddr2())
					+ "<br>" + na(getCity()) + ", "
					+ na(getState()) + ", " + na(getZipcode());
		} else {
			r += "This user is not visible to you. Please add them as a friend or join one of their networks to see their profile.";
		}
		r += "</body>";
		r += "</html>";
		return r;
	}
	public String getShortName() {
		if(getFirstName() != null && !getFirstName().equals(""))
			return getFirstName();
		return getUsername();
	}
	public List<User> getFriendsProfiles() {
		ArrayList<User> ret = new ArrayList<User>();
		for(Friend f: getFriends())
			ret.add(f.getRightUser());
		return ret;
	}
	public boolean containsFolderByName(String folderName) {
		for(WorkflowFolder f : getFolders())
			if(f.getName().equals(folderName))
				return true;
		return false;
	}
	@Override
	public int compareTo(User o) {
		int r = this.getLastName().compareTo(o.getLastName());
		if(r == 0)
			return this.getFirstName().compareTo(o.getFirstName());
		return r;
	}
}
