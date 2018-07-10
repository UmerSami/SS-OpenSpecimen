package com.krishagni.catissueplus.core.biospecimen.repository.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import com.krishagni.catissueplus.core.administrative.domain.Site;
import com.krishagni.catissueplus.core.biospecimen.repository.SpecimenListCriteria;
import com.krishagni.catissueplus.core.common.Pair;
import com.krishagni.catissueplus.core.common.access.SiteCpPair;

public class BiospecimenDaoHelper {

	private static final BiospecimenDaoHelper instance = new BiospecimenDaoHelper();

	private BiospecimenDaoHelper() {
	}

	public static BiospecimenDaoHelper getInstance() {
		return instance;
	}

	public void addSiteCpsCond(Criteria query, SpecimenListCriteria crit) {
		//
		// TODO: fix specimens API
		//
		addSiteCpsCond(query, null, crit.useMrnSites(), query.getAlias().equals("visit") ? "cpr" : "visit");
	}

	public void addSiteCpsCond(Criteria query, Collection<SiteCpPair> siteCps, boolean useMrnSites, String startAlias) {
		if (CollectionUtils.isEmpty(siteCps)) {
			return;
		}

		switch (startAlias) {
			case "visit":
				query.createAlias("specimen.visit", "visit");

			case "cpr":
				query.createAlias("visit.registration", "cpr");

			case "cp":
				query.createAlias("cpr.collectionProtocol", "cp");
		}

		query.createAlias("cp.sites", "cpSite")
			.createAlias("cpSite.site", "site")
			.createAlias("cpr.participant", "participant")
			.createAlias("participant.pmis", "pmi", JoinType.LEFT_OUTER_JOIN)
			.createAlias("pmi.site", "mrnSite", JoinType.LEFT_OUTER_JOIN);

		Disjunction cpSitesCond = Restrictions.disjunction();
		for (SiteCpPair siteCp : siteCps) {
			Junction siteCond = Restrictions.disjunction();
			if (useMrnSites) {
				//
				// When MRNs exist, site ID should be one of the MRN site
				//
				Junction mrnSite = Restrictions.conjunction()
					.add(Restrictions.isNotEmpty("participant.pmis"))
					.add(getSiteIdRestriction("mrnSite.id", siteCp));

				//
				// When no MRNs exist, site ID should be one of CP site
				//
				Junction cpSite = Restrictions.conjunction()
					.add(Restrictions.isEmpty("participant.pmis"))
					.add(getSiteIdRestriction("site.id", siteCp));

				siteCond.add(mrnSite).add(cpSite);
			} else {
				//
				// Site ID should be either MRN site or CP site
				//
				siteCond
					.add(getSiteIdRestriction("mrnSite.id", siteCp))
					.add(getSiteIdRestriction("site.id", siteCp));
			}

			Junction cond = Restrictions.conjunction().add(siteCond);
			if (siteCp.getCpId() != null) {
				cond.add(Restrictions.eq("cp.id", siteCp.getCpId()));
			}

			cpSitesCond.add(cond);
		}

		query.add(cpSitesCond);
	}

	public String getSiteCpsCondAql(List<Pair<Long, Long>> siteCps, boolean useMrnSites) {
		if (CollectionUtils.isEmpty(siteCps)) {
			return StringUtils.EMPTY;
		}

		Set<String> cpSitesCond = new LinkedHashSet<>(); // joined by or
		for (Pair<Long, Long> siteCp : siteCps) {
			Long siteId = siteCp.first();
			Long cpId = siteCp.second();

			List<String> siteCond = new ArrayList<>(); // joined by or
			if (useMrnSites) {
				//
				// When MRNs exist, site ID should be one of the MRN site
				//
				String mrnSite =
					"Participant.medicalRecord.mrnSiteId exists and " +
					"Participant.medicalRecord.mrnSiteId = " + siteId;

				//
				// When no MRNs exist, site ID should be one of CP site
				//
				String cpSite =
					"Participant.medicalRecord.mrnSiteId not exists and " +
					"CollectionProtocol.cpSites.siteId = " + siteId;

				siteCond.add(mrnSite);
				siteCond.add(cpSite);
			} else {
				//
				// Site ID should be either MRN site or CP site
				//
				siteCond.add("Participant.medicalRecord.mrnSiteId = " + siteId);
				siteCond.add("CollectionProtocol.cpSites.siteId = " + siteId);
			}

			String cond = "(" + StringUtils.join(siteCond, " or ") + ")";
			if (cpId != null) {
				cond += " and CollectionProtocol.id = " + cpId;
			}

			cpSitesCond.add("(" + cond + ")");
		}

		return "(" + StringUtils.join(cpSitesCond, " or ") + ")";
	}

	private Criterion getSiteIdRestriction(String property, SiteCpPair siteCp) {
		if (siteCp.getSiteId() != null) {
			return Restrictions.eq(property, siteCp.getSiteId());
		}

		DetachedCriteria subQuery = DetachedCriteria.forClass(Site.class, "site")
			.createAlias("site.institute", "institute")
			.add(Restrictions.eq("institute.id", siteCp.getInstituteId()));
		return Subqueries.propertyIn(property, subQuery);
	}
}
