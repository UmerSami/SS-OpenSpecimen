package com.krishagni.catissueplus.core.biospecimen.events;

import com.krishagni.catissueplus.core.common.events.RequestEvent;

public class ReqClinicalDiagnosesEvent extends RequestEvent {
	private Long cpId;
	
	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}
}
