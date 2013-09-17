/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.geworkbench.builtin.projects;

import java.io.File;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axis2.Constants;
import org.apache.ws.axis2.upload.UploadServiceUploadSOAP11Port_httpStub;
import org.geworkbench.engine.preferences.GlobalPreferences;

/**
 * Client for uploading remote workspace
 * @author mw2518
 * $Id$
 */
public class UploadClient {

	public static String registerUser(File file) throws RemoteException {
		return transferFile(file, "REGUSR", null, null, null, null, null);
	}

	public static String transferFile(File file, String type, String title, String desc, String remoteId, String checkoutstr, String userInfo)
			throws RemoteException {
		// uncomment the following if you need to capture the messages from
		// TCPMON. Please look at http://ws.apache.org/commons/tcpmon/tcpmontutorial.html
		// to learn how to setup tcpmon
		UploadServiceUploadSOAP11Port_httpStub serviceStub = new UploadServiceUploadSOAP11Port_httpStub(
				GlobalPreferences.getInstance().getRWSP_URL()+"/UploadService"
				//"http://localhost:8080/axis2/services/MTOMSample"
		);

		// Enable MTOM in the client side
		serviceStub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		//Increase the time out when sending large attachments
		serviceStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(600000);

		// Uncomment and fill the following if you want to have client side file
		// caching switched ON.
		
		 serviceStub._getServiceClient().getOptions().setProperty(
		 Constants.Configuration.CACHE_ATTACHMENTS, Constants.VALUE_TRUE);
		 serviceStub._getServiceClient().getOptions().setProperty(
		 Constants.Configuration.ATTACHMENT_TEMP_DIR, "axis2cache");
		 serviceStub._getServiceClient().getOptions().setProperty(
		 Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");
		

		// Populating the code generated beans
		UploadServiceUploadSOAP11Port_httpStub.AttachmentRequest attachmentRequest = new UploadServiceUploadSOAP11Port_httpStub.AttachmentRequest();
		UploadServiceUploadSOAP11Port_httpStub.AttachmentType attachmentType = new UploadServiceUploadSOAP11Port_httpStub.AttachmentType();
		UploadServiceUploadSOAP11Port_httpStub.Base64Binary base64Binary = new UploadServiceUploadSOAP11Port_httpStub.Base64Binary();

		if (file!=null){
			// Creating a javax.activation.FileDataSource from the input file.
			FileDataSource fileDataSource = new FileDataSource(file);
	
			// Create a dataHandler using the fileDataSource. Any implementation of
			// javax.activation.DataSource interface can fit here.
			DataHandler dataHandler = new DataHandler(fileDataSource);
			base64Binary.setBase64Binary(dataHandler);
			UploadServiceUploadSOAP11Port_httpStub.ContentType_type0 param = new UploadServiceUploadSOAP11Port_httpStub.ContentType_type0();
	        param.setContentType_type0(dataHandler.getContentType());
	        base64Binary.setContentType(param);
			attachmentType.setBinaryData(base64Binary);
		}
		attachmentType.setType(type);
		attachmentType.setTitle(title);
		attachmentType.setDesc(desc);
		attachmentType.setRemoteId(remoteId);
		attachmentType.setCheckoutstr(checkoutstr);
		attachmentType.setUserInfo(userInfo);
		attachmentRequest.setAttachmentRequest(attachmentType);

		UploadServiceUploadSOAP11Port_httpStub.AttachmentResponse response = serviceStub.attachment(attachmentRequest);
		//System.out.println(response.getAttachmentResponse());
		return response.getAttachmentResponse();
	}

}
