package com.krishagni.catissueplus.core.common;

import org.apache.commons.lang.StringUtils;



public class CommonValidator {
	public static boolean isValidPv(String value, String type) {
		PermissibleValuesManager pvManager = new PermissibleValuesManagerImpl();
		if (pvManager.validate(type, value)) {
			return true;
		}
		return false;
//		reportError(ParticipantErrorCode.INVALID_ATTR_VALUE, type);
	}
	
	public static boolean isBlank(String value)
	{
		return StringUtils.isBlank(value);
	}
	
	public static boolean isValidPv(String[] value, String type)
	{
		PermissibleValuesManager pvManager = new PermissibleValuesManagerImpl();
		if (pvManager.validate(type, value)) {
			return true;
		}
		return false;
//		reportError(ParticipantErrorCode.INVALID_ATTR_VALUE, type);
	}
	
	public static boolean isValidPv(String parentValue, String value, String type) {
		PermissibleValuesManager pvManager = new PermissibleValuesManagerImpl();
		if (pvManager.validate(type, parentValue,value)) {
			return true;
		}
		return false;
//		reportError(ParticipantErrorCode.INVALID_ATTR_VALUE, type);
	}
}