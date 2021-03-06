package com.krishagni.catissueplus.core.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.krishagni.catissueplus.core.biospecimen.WorkflowUtil;

public class ListUtil {
	public static ListConfig getSpecimensListConfig(String name, boolean fallbackToCommon) {
		ListConfig cfg = WorkflowUtil.getInstance().getSysWorkflow(name, ListConfig.class);
		if (cfg == null && fallbackToCommon) {
			cfg = WorkflowUtil.getInstance().getSysWorkflow("common-specimens-list-view", ListConfig.class);
		}

		if (cfg == null && fallbackToCommon) {
			cfg = WorkflowUtil.getInstance().getSysWorkflow("specimen-list-view", ListConfig.class);
		}

		return cfg;
	}

	public static ListConfig addHiddenFieldsOfSpecimen(ListConfig cfg) {
		if (cfg == null) {
			return null;
		}

		Column id = new Column();
		id.setExpr("Specimen.id");
		id.setCaption("specimenId");
		cfg.setPrimaryColumn(id);

		Column type = new Column();
		type.setExpr("Specimen.type");
		type.setCaption("specimenType");

		Column specimenClass = new Column();
		specimenClass.setExpr("Specimen.class");
		specimenClass.setCaption("specimenClass");

		List<Column> hiddenColumns = new ArrayList<>();
		hiddenColumns.add(id);
		hiddenColumns.add(type);
		hiddenColumns.add(specimenClass);
		cfg.setHiddenColumns(hiddenColumns);
		return cfg;
	}

	public static ListConfig setListLimit(ListConfig cfg, Map<String, Object> listReq) {
		Integer startAt = (Integer)listReq.get("startAt");
		if (startAt == null) {
			startAt = 0;
		}

		Integer maxResults = (Integer)listReq.get("maxResults");
		if (maxResults == null) {
			maxResults = 100;
		}

		cfg.setStartAt(startAt);
		cfg.setMaxResults(maxResults);
		return cfg;
	}
}
