/**
 * MTOMSampleMTOMSampleSOAP12Port_httpCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.3  Built on : Nov 12, 2010 (02:24:07 CET)
 */

package org.apache.ws.axis2.mtomsample;

/**
 * MTOMSampleMTOMSampleSOAP12Port_httpCallbackHandler Callback class, Users can
 * extend this class and implement their own receiveResult and receiveError
 * methods.
 */
public abstract class MTOMSampleMTOMSampleSOAP12Port_httpCallbackHandler {

	protected Object clientData;

	/**
	 * User can pass in any object that needs to be accessed once the
	 * NonBlocking Web service call is finished and appropriate method of this
	 * CallBack is called.
	 * 
	 * @param clientData
	 *            Object mechanism by which the user can pass in user data that
	 *            will be avilable at the time this callback is called.
	 */
	public MTOMSampleMTOMSampleSOAP12Port_httpCallbackHandler(Object clientData) {
		this.clientData = clientData;
	}

	/**
	 * Please use this constructor if you don't want to set any clientData
	 */
	public MTOMSampleMTOMSampleSOAP12Port_httpCallbackHandler() {
		this.clientData = null;
	}

	/**
	 * Get the client data
	 */

	public Object getClientData() {
		return clientData;
	}

	/**
	 * auto generated Axis2 call back method for attachment method override this
	 * method for handling normal response from attachment operation
	 */
	public void receiveResultattachment(
			org.apache.ws.axis2.mtomsample.MTOMSampleMTOMSampleSOAP12Port_httpStub.AttachmentResponse result) {
	}

	/**
	 * auto generated Axis2 Error handler override this method for handling
	 * error response from attachment operation
	 */
	public void receiveErrorattachment(java.lang.Exception e) {
	}

}
