package com.krishagni.catissueplus.core.common.access;

public class SiteCpPair {
	private Long instituteId;

	private Long siteId;

	private Long cpId;

	public Long getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(Long instituteId) {
		this.instituteId = instituteId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public static SiteCpPair make(Long instituteId, Long siteId, Long cpId) {
		SiteCpPair result = new SiteCpPair();
		result.setInstituteId(instituteId);
		result.setSiteId(siteId);
		result.setCpId(cpId);
		return result;
	}
}
