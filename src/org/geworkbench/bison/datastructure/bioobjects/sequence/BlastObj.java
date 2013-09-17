package org.geworkbench.bison.datastructure.bioobjects.sequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class to hold information about each individual hit of a Blast database
 * search.
 * 
 * @author zji
 * @version $Id$
 */
public class BlastObj implements Serializable{
	 
	private static final long serialVersionUID = -7617429744234644109L;	 

	private static Log log = LogFactory.getLog(BlastObj.class);
	
	/**
	 * The Database ID of the protein sequence hit in this BlastObj.
	 */
	final private String databaseID;

	/**
	 * The accession number of the protein sequence hit in this BlastObj.
	 */
	final private String name;
	/**
	 * The description of the protein sequence hit in this BlastObj.
	 */
	final private String description;
	/**
	 * The percentage of the query that's aligned with protein sequence hit in
	 * this BlastObj.
	 */
	final private int percentAligned;

	/**
	 * The score of the alignment of query and hit in this BlastObj.
	 */
	final private String evalue;

	/**
	 * whether this BlastObj is included in the MSA analysis
	 */
	private boolean include = false;

	final private String gi;

	final private String detailedAlignment;
	final private int startPoint;
	final private int alignmentLength;
	final private String alignedParts;
	
	final private String efetchDb;

	public BlastObj(String databaseID, String name, String description,
			String evalue, int startPoint, int alignmentLength, int percentage,
			String alignedParts, String detailedAlignment, String gi, String efetchDb) {
		this.databaseID = databaseID;
		this.description = description;
		this.name = name;
		this.evalue = evalue;
		this.startPoint = startPoint;
		this.alignmentLength = alignmentLength;
		this.percentAligned = percentage;

		this.alignedParts = alignedParts;
		this.detailedAlignment = detailedAlignment;
		
		this.gi = gi;
		this.efetchDb = efetchDb;
	}

	/* Get methods for class variables. */

	/**
	 * Returns the database name of the hit sequence stored in this BlastObj.
	 * 
	 * @return the accession number as a String.
	 */
	public String getDatabaseID() {
		return databaseID;
	}

	/**
	 * Returns the accession number of the hit sequence stored in this BlastObj.
	 * 
	 * @return the accession number as a String.
	 */

	/**
	 * Returns the description of the hit protein sequence stored in this
	 * BlastObj.
	 * 
	 * @return the description as a String.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the percentage of the query that's aligned with protein sequence
	 * hit in this BlastObj.
	 * 
	 * @return percentAligned as an int.
	 */
	public int getPercentAligned() {
		return percentAligned;
	}

	/**
	 * Returns the e-value of the alignment of query and protein sequence hit in
	 * this BlastObj.
	 * 
	 * @return evalue as a String.
	 */
	public String getEvalue() {
		return evalue;
	}

	/**
	 * Returns whether this BlastObj is included to add to the project.
	 * 
	 * @return include as a Boolean.
	 */
	public boolean getInclude() {
		return include;
	}

	/**
	 * Sets whether this BlastObj is included to add to the project.
	 * 
	 * @param b,
	 *            the new include Boolean of this BlastObj.
	 */
	public void setInclude(boolean b) {
		include = b;
	}

	public String getName() {
		return name;
	}

	public String getDetailedAlignment() {
		return detailedAlignment;
	}

	public String getAlignedSeq() {
		return alignedParts;
	}
	
	/**
	 * Get the whole sequence from NIH's E-utilities.
	 * 
	 * Document about E-utilities and EFetch particular can be found at
	 * http://www.ncbi.nlm.nih.gov/books/NBK25499/#chapter4.EFetch
	 * 
	 * @return String
	 */
	public String getWholeSeq() {

		HttpClient client = new HttpClient();
		DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(
				10, true);
		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				retryhandler);
		client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		/* In the older code, efetchDb is either protein or nucleotide, but it seems have not effect in my tests:
		 * either one works for both protein sequence or nucleotide sequence, but other value does not work. */
		// Create Entrez Programming Utilities query URL.
		String url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db="
				+ efetchDb + "&id=" + gi + "&rettype=fasta";
		System.out.println(url);
		
		GetMethod getMethod = new GetMethod(url);
		String sequenceLabel = null, sequence = null;
		try {
			int statusCode = client.executeMethod(getMethod);

			if (statusCode == HttpStatus.SC_OK) {
				InputStream stream = getMethod.getResponseBodyAsStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						stream));
				sequenceLabel = in.readLine();
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = in.readLine()) != null) {
					if (line.trim().length() > 0)
						sb.append(line).append('\n');
				}
				stream.close();
				sequence = sb.toString();
			} else {
				log.error("E Utilties failed for " + url);
				log.error("status code=" + statusCode);
				return null;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			getMethod.releaseConnection();
		}

		log.debug("label\n" + sequenceLabel);
		log.debug("sequence\n" + sequence);
		return sequenceLabel+"\n"+sequence;
	}

	public int getStartPoint() {

		return startPoint;
	}

	public int getAlignmentLength() {
		return alignmentLength;
	}
	
}
