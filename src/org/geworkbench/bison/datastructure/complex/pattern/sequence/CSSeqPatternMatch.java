package org.geworkbench.bison.datastructure.complex.pattern.sequence;

import java.io.Serializable;

import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;

/**
 * 
 * @author zji
 * @version $Id$
 *
 */
public class CSSeqPatternMatch extends
		CSPatternMatch<DSSequence, CSSeqRegistration> implements
		DSSeqPatternMatch, Serializable {
	private static final long serialVersionUID = 6705539541328990492L;

	// default constructor needed for serialization
	public CSSeqPatternMatch() {
		super();
	}
	
	public CSSeqPatternMatch(DSSequence seq) {
		super(seq);
	}

	@Override
	public CSSeqRegistration getRegistration() throws IndexOutOfBoundsException {
		if (registration == null) {
			registration = new CSSeqRegistration();
			return registration;
		}
		return registration;
	}

}
