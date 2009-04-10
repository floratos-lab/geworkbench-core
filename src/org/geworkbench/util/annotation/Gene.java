package org.geworkbench.util.annotation;

/**
 * 
 * @author yc2480
 * @version $Id: Gene.java,v 1.1 2009-04-10 19:09:04 chiangy Exp $
 */
public class Gene {
	/**
	 * Separator used for separate genes associate with one marker. ex:
	 * 31317_r_at has four genes associate with it IGHA1 /// IGHD /// IGHG1 ///
	 * IGHM So, When you call getShortName() on 31317_r_at, it will return
	 * "IGHA1 /// IGHD /// IGHG1 /// IGHM", and you'll need to use this
	 * separator to split it to get four gene names.
	 * 
	 * Used in AnnotationParser.java to merge the gene names.
	 */
	public static String genesSeparator = " /// ";
}
