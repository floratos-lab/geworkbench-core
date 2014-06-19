package org.geworkbench.util.network;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: xiaoqing
 * Date: Jan 5, 2007
 * Time: 4:27:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * The deatil of one interaction.
 * 
 * @version $Id$
 */
public class InteractionDetail implements Serializable {

	private static final long serialVersionUID = -4163326138016520667L;
	public static final String ENTREZ_GENE = "Entrez Gene";

	private List<InteractionParticipant> participantList;
	private String interactionType;
	private Short evidenceId;
	private List<Confidence> confidenceList;

	public InteractionDetail(InteractionParticipant participant,
			String interactionType, Short evidenceId) {

		participantList = new ArrayList<InteractionParticipant>();
		participantList.add(participant);
		this.interactionType = interactionType;
		this.evidenceId = evidenceId;

	}

	public void addParticipant(InteractionParticipant participant) {
		if (participantList == null)
			participantList = new ArrayList<InteractionParticipant>();
		participantList.add(participant);
	}

	public List<InteractionParticipant> getParticipantList() {
		return participantList;
	}

	public double getConfidenceValue(int usedConfidenceType) {
		for (int i = 0; i < confidenceList.size(); i++)
			if (confidenceList.get(i).getType() == usedConfidenceType)
				return confidenceList.get(i).getScore();
		// if usedConfidenceType is not found, return 0.
		return 0;
	}

	public List<Short> getConfidenceTypes() {
		List<Short> types = new ArrayList<Short>();
		for (int i = 0; i < confidenceList.size(); i++)
			types.add(confidenceList.get(i).getType());
		return types;
	}

	public void addConfidence(double score, short type) {
		if (confidenceList == null)
			confidenceList = new ArrayList<Confidence>();
		confidenceList.add(new Confidence(score, type));
	}

	public String getInteractionType() {
		return this.interactionType;
	}

	public void setInteraactionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public Short getEvidenceId() {
		return this.evidenceId;
	}

	private class Confidence implements Serializable {

		private static final long serialVersionUID = 4151510293677929250L;
		private double score;
		private short type;

		Confidence(double score, short type) {
			this.score = score;
			this.type = type;
		}

		public double getScore() {
			return score;
		}

		public short getType() {
			return type;
		}

	}

}
