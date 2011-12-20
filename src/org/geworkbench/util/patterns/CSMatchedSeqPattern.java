package org.geworkbench.util.patterns;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqRegistration;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.util.BinaryEncodeDecode;

/**
 * 
 * @author not attributable
 * @version $Id$
 */

public final class CSMatchedSeqPattern extends
		CSMatchedPattern<DSSequence, CSSeqRegistration> implements
		DSMatchedSeqPattern, 
		Serializable {
	private static final long serialVersionUID = -8809856414947634034L;

	static private final java.util.regex.Pattern headerPattern = java.util.regex.Pattern
			.compile("\\[\\d+\\]\\s+(\\S+)\\s+\\[(\\d+)\\D*(\\d+)\\D*(\\d+)\\D*(\\d+\\.?\\d*E?\\d*)\\]");
	static private final java.util.regex.Pattern locusPattern = java.util.regex.Pattern
			.compile("\\[(\\d+)\\D*(\\d+)\\]");
	static private final java.util.regex.Pattern offsetPattern = java.util.regex.Pattern
			.compile("([a-zA-Z]|\\.|\\[[^\\]]+\\])");

	private int idNo = 0;
	private int seqNo = 0;
	private DSSequenceSet<DSSequence> seqDB = null;

	// locus is a byte-coded int array, where each group of 8 bytes
	// encode a locusId (4 bytes) and a locusOffset (4 bytes). This
	// is decoded using the get32BitInteger method and it is used to
	// dramatically speed up the transfer of patterns over a SOAP
	// connection.
	private byte[] locus = null;
	private ArrayList<PatternOfflet> offset = new ArrayList<PatternOfflet>();
	private String ascii = null;
	private int rand_hash;

	public void setLocus(byte[] locus) {
		this.locus = locus;
	}

	public void setOffset(ArrayList<PatternOfflet> offset) {
		this.offset = offset;
	}

	public CSMatchedSeqPattern(final DSSequenceSet<DSSequence> seqDB) {
		this.seqDB = seqDB;
		rand_hash = new java.util.Random().nextInt();
	}

	public String toString() {
		String s = new String();
		if (ascii != null) {
			s += ascii + "    ";
		}
		s += "(" + idNo + "," + offset.size() + ")";
		return s;
	}

	/**
	 * update the ascii representation. Method to replace PatternOperations.fill
	 * method.
	 */
	public void updateASCII() {
		String s = new String();

		int j = offset.get(0).getPosition();
		for (int i = 0; i < offset.size(); i++, j++) {
			int dx = offset.get(i).getPosition();
			for (; j < dx; j++) {
				s += '.';
			}
			String tokString = offset.get(i).getToken();
			if (tokString.length() > 1) {
				s += '[' + tokString + ']';
			} else {
				s += tokString;
			}
		}
		ascii = s;

	}

	public CSMatchedSeqPattern(String s) {
		Matcher m0 = CSMatchedSeqPattern.headerPattern.matcher(s);
		rand_hash = new java.util.Random().nextInt();
		if (m0.find()) {
			Matcher m1 = CSMatchedSeqPattern.locusPattern.matcher(s);
			String pat = m0.group(1);
			int seqNo = Integer.parseInt(m0.group(2));
			int idNo = Integer.parseInt(m0.group(3));

			double pVal = Double.parseDouble(m0.group(5));
			this.zScore = pVal;
			this.seqNo = seqNo;
			this.idNo = idNo;
			this.offset = new ArrayList<PatternOfflet>();
			int offDx = 0;
			int j = 0;
			Matcher m2 = offsetPattern.matcher(pat);
			while (m2.find()) {
				String token = m2.group(1);
				if (!token.equalsIgnoreCase(".")) {

					PatternOfflet patternOfflet = new PatternOfflet(offDx,
							token);
					if (this.offset.size() <= j) {
						this.offset.add(j, patternOfflet);
					} else {
						this.offset.set(j, patternOfflet);
					}
					j++;
				}
				offDx++;
			}
			this.ascii = pat;
			int index = m0.end();
			boolean found = m1.find(index);
			int locId = 0;
			locus = new byte[idNo * 8]; // 4 bytes per int for id and offset
			while (found) {
				int id = Integer.parseInt(m1.group(1));
				int dx = Integer.parseInt(m1.group(2));
				locus[locId++] = (byte) (id % 256);
				id /= 256;
				locus[locId++] = (byte) (id % 256);
				id /= 256;
				locus[locId++] = (byte) (id % 256);
				id /= 256;
				locus[locId++] = (byte) (id % 256);
				locus[locId++] = (byte) (dx % 256);
				dx /= 256;
				locus[locId++] = (byte) (dx % 256);
				dx /= 256;
				locus[locId++] = (byte) (dx % 256);
				dx /= 256;
				locus[locId++] = (byte) (dx % 256);
				found = m1.find();
			}
		}
	}

	private int getId(int i) {
		if (locus != null) {
			return BinaryEncodeDecode.decodeUnsignedInt32(locus, i * 2);
		}
		return 0;
	}

	public int getLength() {
		return offset.size();
	}

	public int getSupport() {
		return idNo != -1 ? idNo : 0;
	}

	public int getUniqueSupport() {
		return seqNo;
	}

	public int getMaxLength() {
		int baseOffset = this.offset.get(0).getPosition();
		int extent = offset.get(getLength() - 1).getPosition() + 1 - baseOffset;
		
		int maxLen = 0;
		if ((maxLen == 0) && (locus != null)) {
			for (int i = 0; i < idNo; i++) {
				// int id = getId(i);
				int dx = getOffset(i);
				int len = dx + extent;
				maxLen = Math.max(len, maxLen);
			}
		}
		return maxLen;
	}

	public String getASCII() {
		return ascii;
	}

	public void write(BufferedWriter writer) throws IOException {
		writer.write(ascii);
		writer.write("\t");
		writer.write("[" + seqNo + "," + getSupport() + "," + getLength()
				+ "," + getPValue() + "]\t");
		for (int j = 0; j < getSupport(); j++) {
			int id = getId(j);
			int dx = getOffset(j);
			writer.write(new String("[" + id + ',' + dx + ']'));
		}
		writer.newLine();
	}

	public int getOffset(int j) {
		int baseOffset = this.offset.get(0).getPosition();

		int absoluteOffset = 0;
		if (locus != null) {
			absoluteOffset = BinaryEncodeDecode.decodeUnsignedInt32(locus, j * 2 + 1);
		}

		return absoluteOffset + baseOffset;
	}

	private ArrayList<DSPatternMatch<DSSequence, CSSeqRegistration>> matches = new ArrayList<DSPatternMatch<DSSequence, CSSeqRegistration>>();

	public DSPatternMatch<DSSequence, CSSeqRegistration> get(int i) {
		if (matches.size() > i) {
			return matches.get(i);
		} else {
			DSSequence object = getObject(i);
			if (object != null) {
				CSSeqPatternMatch match = new CSSeqPatternMatch(object);
				CSSeqRegistration reg = match.getRegistration();
				reg.x1 = getOffset(i);
				reg.x2 = reg.x1 + getLength();

				return match;
			}
		}
		return null;
	}

	public List<DSPatternMatch<DSSequence, CSSeqRegistration>> matches() {
		if (matches.size() < 1) {
			for (int i = 0; i < this.getSupport(); i++) {
				matches.add(i, get(i));
			}
		}
		return matches;
	}

	public int hashCode() {
		return rand_hash;
	}

    private DSSequence getObject(int i)  {

		if ((seqDB != null) && (i < getSupport())) {
			return seqDB.getSequence(this.getId(i));
		} else {
			return null;
		}

	}

	public void setIdNo(int value) {
		idNo = value;
	}

	public void setSeqNo(int value) {
		seqNo = value;
	}

}
