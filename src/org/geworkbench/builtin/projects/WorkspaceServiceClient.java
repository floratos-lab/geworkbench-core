/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geworkbench.builtin.projects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.Constants;
import org.geworkbench.engine.preferences.GlobalPreferences;

/**
 * Client for retrieving remote workspace 
 * @author mw2518
 * $Id$
 */
public class WorkspaceServiceClient {

	private static EndpointReference targetEPR = new EndpointReference(
			GlobalPreferences.getInstance().getRWSP_URL()+"/WorkspaceService");
			//"http://localhost:8080/axis2/services/WorkspaceService");
	private static final String cachedir = RWspHandler.wspdir+"axis2cache";
	
	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			System.out.println(args[0]);
			getSavedWorkspace(args[0]);
		} else {
			throw new IllegalArgumentException("Please provide the project name as an argument.");
		}
	}

	public static String getSavedWorkspace(String projectName) throws Exception {

		Options options = new Options();
		options.setTo(targetEPR);
		options.setAction("urn:getWorkspace");
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		
		
		/*
		 * Uncomment to enable client side file caching for the response.
         */
		options.setProperty(Constants.Configuration.CACHE_ATTACHMENTS, Constants.VALUE_TRUE);
		options.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR, cachedir);
		options.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");

		options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		
		// Increase the time out to receive large attachments
		options.setTimeOutInMilliSeconds(600000);
		
		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);
		OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
        
        MessageContext mc = new MessageContext();
        SOAPEnvelope env = createEnvelope(projectName);
        mc.setEnvelope(env);
        
		mepClient.addMessageContext(mc);
		mepClient.execute(true);
		
		// Let's get the message context for the response
		MessageContext response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
		SOAPBody body = response.getEnvelope().getBody();
		OMElement element = body.getFirstChildWithName(new QName("http://service.sample/xsd","getWorkspaceResponse"));
        if (element!=null)
        {
        	return processResponse(response, element);
        }else{
            throw new Exception("Malformed response.");
        }
	}

	private static String processResponse(MessageContext response, OMElement element) throws Exception {
		String fname = element.getFirstChildWithName(new QName("http://service.sample/xsd","projectName")).getText();
		int id = Integer.valueOf(fname.substring(0, fname.indexOf(".wsp")));
		String rename = wsprenames.get(id); 
		if (rename!=null) fname=rename;
		String dldprojectName = RWspHandler.wspdir + fname;
		System.out.println("Project Name : " + dldprojectName);

		OMElement wspElement = element.getFirstChildWithName(new QName("http://service.sample/xsd","wsp"));
        //retrieving the ID of the attachment
		String wspBinID = wspElement.getAttributeValue(new QName("href"));
        //remove the "cid:" prefix
        wspBinID = wspBinID.substring(4);
		//Accesing the attachment from the response message context using the ID
        System.out.println(wspBinID);
		DataHandler dataHandler = response.getAttachment(wspBinID);
        if (dataHandler!=null){
			// Writing the attachment data (wsp binary) to a file
			File wspFile = new File(dldprojectName);
			FileOutputStream outputStream = new FileOutputStream(wspFile);
			dataHandler.writeTo(outputStream);
			outputStream.flush();
			System.out.println("Download workspace saved to " + wspFile.getAbsolutePath());
        }else
        {
            throw new Exception("Cannot find the data handler.");
        }
        return dldprojectName;
	}

	public static HashMap<String, String[][]> getSavedWorkspaceInfo(String projectName) throws Exception {

		Options options = new Options();
		options.setTo(targetEPR);
		options.setAction("urn:getWorkspace");
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

		//Uncomment to enable client side file caching for the response.
		options.setProperty(Constants.Configuration.CACHE_ATTACHMENTS, Constants.VALUE_TRUE);
		options.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR, cachedir);
		options.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");

		options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		
		// Increase the time out to receive large attachments
		options.setTimeOutInMilliSeconds(300000);
		
		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);
		OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
        
        MessageContext mc = new MessageContext();
        SOAPEnvelope env = createEnvelope(projectName);
        mc.setEnvelope(env);
        
		mepClient.addMessageContext(mc);
		mepClient.execute(true);
		
		// Let's get the message context for the response
		MessageContext response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
		SOAPBody body = response.getEnvelope().getBody();
		OMElement element = body.getFirstChildWithName(new QName("http://service.sample/xsd","getWorkspaceResponse"));
        if (element!=null)
        {
        	return processResponseInfo(response, element);
        }else{
            throw new Exception("Malformed response.");
        }
	}

	public static final String[] colhist = {"Username", "AccessedAt", "Type"};
	public static final String[] coluser = {"Username", "OnlineStatus", "Access", "LastAccessTime"};
	public static final String[] colanno = {"Annotation", "Creator", "CreatedAt"};
	@SuppressWarnings("unchecked")
	private static HashMap<String, String[][]> processResponseInfo(MessageContext response, OMElement element) throws Exception {
		HashMap<String, String[][]> hm = new HashMap<String, String[][]>();
		OMElement elm = element.getFirstChildWithName(new QName("http://service.sample/xsd","histcount"));
		if (elm!=null){
			int count = Integer.valueOf(elm.getText());
			String[][] list = new String[count][colhist.length];
			int i = 0;
			Iterator<OMElement> wspElementIt = element.getChildrenWithName(new QName("http://service.sample/xsd","histlist"));
			while(wspElementIt.hasNext()) {
				elm = wspElementIt.next();
				for (int j = 0; j < colhist.length; j++)
					list[i][j] = elm.getAttributeValue(new QName(colhist[j]));
				i++;
			}
			hm.put("HIST", list);
		}
		
		elm = element.getFirstChildWithName(new QName("http://service.sample/xsd","usercount"));
		if (elm!=null){
			int count = Integer.valueOf(elm.getText());
			String[][] list = new String[count][coluser.length];
			int i = 0;
			Iterator<OMElement> wspElementIt = element.getChildrenWithName(new QName("http://service.sample/xsd","userlist"));
			while(wspElementIt.hasNext()) {
				elm = wspElementIt.next();
				for (int j = 0; j < coluser.length; j++)
					list[i][j] = elm.getAttributeValue(new QName(coluser[j]));
				i++;
			}
			hm.put("GETUSER", list);
		}
		
		elm = element.getFirstChildWithName(new QName("http://service.sample/xsd","annocount"));
		if (elm!=null){
			int count = Integer.valueOf(elm.getText());
			String[][] list = new String[count][colanno.length];
			int i = 0;
			Iterator<OMElement> wspElementIt = element.getChildrenWithName(new QName("http://service.sample/xsd","annolist"));
			while(wspElementIt.hasNext()) {
				elm = wspElementIt.next();
				for (int j = 0; j < colanno.length; j++)
					list[i][j] = elm.getAttributeValue(new QName(colanno[j]));
				i++;
			}
			hm.put("GETANNO", list);
		}
		return hm;
	}

	
	public static HashMap<String, String[][]> getSavedWorkspaceList(String projectName) throws Exception {

		targetEPR.setAddress(GlobalPreferences.getInstance().getRWSP_URL()+"/WorkspaceService");
		Options options = new Options();
		options.setTo(targetEPR);
		options.setAction("urn:getWorkspace");
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

		//Uncomment to enable client side file caching for the response.
		options.setProperty(Constants.Configuration.CACHE_ATTACHMENTS, Constants.VALUE_TRUE);
		options.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR, cachedir);
		options.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");

		options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		
		// Increase the time out to receive large attachments
		options.setTimeOutInMilliSeconds(300000);
		
		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);
		OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
        
        MessageContext mc = new MessageContext();
        SOAPEnvelope env = createEnvelope(projectName);
        mc.setEnvelope(env);
        
		mepClient.addMessageContext(mc);
		mepClient.execute(true);
		
		// Let's get the message context for the response
		MessageContext response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
		SOAPBody body = response.getEnvelope().getBody();
		OMElement element = body.getFirstChildWithName(new QName("http://service.sample/xsd","getWorkspaceResponse"));
        if (element!=null)
        {
        	return processResponseList(response, element);
        }else{
            throw new Exception("Malformed response.");
        }
	}
	
	public static final String[] colprofile = {"id", "username", "fname", "lname", "labaff",
		"email", "phone", "addr1", "addr2", "city", "state", "zipcode"};
	public static final String[] colgroup = {"Group", "Owner"};
	public static final String[] colnames = {"Local", "ID", "Title", "Owner", "Access", "Lock", "LocUser", "Dirty", "Sync", "LastSync", "CreatedAt", "Description"};
	@SuppressWarnings("unchecked")
	private static HashMap<String, String[][]> processResponseList(MessageContext response, OMElement element) throws Exception {
		HashMap<String, String[][]> hm = new HashMap<String, String[][]>();
		String[][] list = new String[1][colprofile.length];
		int i = 0;
		OMElement wspElement = element.getFirstChildWithName(new QName("http://service.sample/xsd","profile"));
		if(wspElement!=null) {
			for (i = 0; i < colprofile.length; i++){
				list[0][i] = wspElement.getAttributeValue(new QName(colprofile[i]));
			}
		}
		hm.put("PROFILE", list);
		
		int count = Integer.valueOf(element.getFirstChildWithName(new QName("http://service.sample/xsd","groupcount")).getText());
		list = new String[count][colgroup.length];
		i = 0;
		Iterator<OMElement> wspElementIt = element.getChildrenWithName(new QName("http://service.sample/xsd","grouplist"));
		while(wspElementIt.hasNext()) {
			OMElement elm = wspElementIt.next();
			for (int j = 0; j < colgroup.length; j++)
				list[i][j] = elm.getAttributeValue(new QName(colgroup[j]));
			i++;
		}
		hm.put("GROUP", list);
		
		count = Integer.valueOf(element.getFirstChildWithName(new QName("http://service.sample/xsd","wspcount")).getText());
		list = new String[count][colnames.length];
		i = 0;
		wspElementIt = element.getChildrenWithName(new QName("http://service.sample/xsd","wsplist"));
		while(wspElementIt.hasNext()) {
			OMElement elm = wspElementIt.next();
			for (int j = 1; j < colnames.length; j++){
				if (j!=7 && j!=8)
					list[i][j] = elm.getAttributeValue(new QName(colnames[j]));
			}
			String[] ret = checklocal(Integer.valueOf(list[i][RWspHandler.IdID]), list[i][RWspHandler.LastSyncID]);
			list[i][RWspHandler.LocalID] = ret[0];
			list[i][RWspHandler.DirtyID] = ret[1];
			list[i][RWspHandler.SyncID] = ret[2];
			i++;
		}
		hm.put("LIST", list);
		return hm;
	}
	public static HashMap<Integer, String> wsprenames=new HashMap<Integer, String>();
	private static String[] checklocal(int id, String lastSync){
		File wspdir = new File(RWspHandler.wspdir);
		if (wspdir.exists() && wspdir.isDirectory()){
			for (File f : wspdir.listFiles()){
				if (f.isFile() && f.getName().endsWith(".wsp")){
					FileInputStream in = null;
					try {
						in = new FileInputStream(f);
						ObjectInputStream s = new ObjectInputStream(in);
						SaveTree saveTree = (SaveTree) s.readObject();
						if (saveTree!=null) {
							int wspid = saveTree.getWspId();
							boolean dirty = saveTree.getDirty();
							String co = saveTree.getCheckout();
							if (wspid == RWspHandler.wspId){
								dirty = RWspHandler.dirty;
								if (RWspHandler.checkoutstr!=null)
										co = RWspHandler.checkoutstr;
							}
							if (wspid == id){
								//System.out.println(id+": "+co+"; "+RWspHandler.checkoutstr+"; "+lastSync);
								return new String[]{f.getName(), Boolean.toString(dirty), 
										co==null?"false":Boolean.toString(co.equals(lastSync))};
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(in!=null){
							try{
								in.close();
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return new String[]{"", "", ""};
	}
	
	private static SOAPEnvelope createEnvelope(String destinationFile) {
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
		SOAPEnvelope env = fac.getDefaultEnvelope();
		OMNamespace omNs = fac.createOMNamespace("http://service.sample/xsd",
				"swa");
		OMElement wspElement = fac.createOMElement("getWorkspace", omNs);
		OMElement nameEle = fac.createOMElement("projectName", omNs);
		nameEle.setText(destinationFile);
		wspElement.addChild(nameEle);
		env.getBody().addChild(wspElement);
		return env;
	}

	public static void cleanCache(){
		System.gc();
		File cacheDir = new File(cachedir);
		if (cacheDir.exists() && cacheDir.isDirectory()){
			for (File f : cacheDir.listFiles())
				if (f.exists() && !f.delete())
					f.deleteOnExit();
		}
	}

	public static String modifySavedWorkspace(String projectName) throws Exception {
		Options options = new Options();
		options.setTo(targetEPR);
		options.setAction("urn:getWorkspace");
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		
		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);
		OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
        
        MessageContext mc = new MessageContext();
        SOAPEnvelope env = createEnvelope(projectName);
        mc.setEnvelope(env);
        
		mepClient.addMessageContext(mc);
		mepClient.execute(true);
		
		// Let's get the message context for the response
		MessageContext response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
		SOAPBody body = response.getEnvelope().getBody();
		OMElement element = body.getFirstChildWithName(new QName("http://service.sample/xsd","getWorkspaceResponse"));
        if (element!=null)
        {
        	return element.getFirstChildWithName(new QName("http://service.sample/xsd","projectName")).getText();
        }else{
            throw new Exception("Malformed response.");
        }
	}

}
