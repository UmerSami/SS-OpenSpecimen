
package com.krishagni.catissueplus.core.events.participants;

import com.krishagni.catissueplus.core.events.RequestEvent;

public class CreateParticipantEvent extends RequestEvent {

	private ParticipantDetails participantDetails;

	public ParticipantDetails getParticipantDetails() {
		return participantDetails;
	}

	public void setParticipantDetails(ParticipantDetails participantDetails) {
		this.participantDetails = participantDetails;
	}

}
