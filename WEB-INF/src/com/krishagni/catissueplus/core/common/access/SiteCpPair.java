package com.krishagni.catissueplus.core.common.access;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SiteCpPair that = (SiteCpPair) o;
		return Objects.equals(getInstituteId(), that.getInstituteId()) &&
			Objects.equals(getSiteId(), that.getSiteId()) &&
			Objects.equals(getCpId(), that.getCpId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getInstituteId(), getSiteId(), getCpId());
	}

	public static SiteCpPair make(Long instituteId, Long siteId, Long cpId) {
		SiteCpPair result = new SiteCpPair();
		result.setInstituteId(instituteId);
		result.setSiteId(siteId);
		result.setCpId(cpId);
		return result;
	}
}
