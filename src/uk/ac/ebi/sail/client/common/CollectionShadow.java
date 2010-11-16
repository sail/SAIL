/**
 * SAIL - biological samples availability index
 * 
 * Copyright (C) 2008,2009 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *
 *  @author Mikhail Gostev <gostev@ebi.ac.uk>
 *
 */

package uk.ac.ebi.sail.client.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionShadow implements IsSerializable, Serializable {
	private int id;
	private String name;
	private Collection<AnnotationShadow> annts;
	private int samples = 0;
	private int individuals = 0;
	private long updateTime;

	// joern
	private int userId;
	private int logId;
	private String role;

	public CollectionShadow() {
	}

	public CollectionShadow(SampleCollection rp) {
		id = rp.getId();
		name = rp.getName();
		samples = rp.getSampleCount();
		individuals = rp.getIndividualCount();
		updateTime = rp.getUpdateTime();
		// joern
		// userId=rp.getUserId();
		userId = rp.getLogId();

		logId = rp.getLogId();
		role = rp.getRole();
		if (rp.getAnnotations() != null) {
			annts = new ArrayList<AnnotationShadow>(rp.getAnnotations().size());

			for (Annotation an : rp.getAnnotations()) {
				annts.add(new AnnotationShadow(an));
			}
		}
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public SampleCollection createCollection() {
		SampleCollection rp = new SampleCollection();

		rp.setId(id);
		rp.setName(name);
		rp.setSampleCount(samples);
		rp.setIndividualCount(individuals);
		rp.setUpdateTime(updateTime);
		rp.setUserId(userId);
		rp.setLogId(logId);
		return rp;
	}

	public Collection<AnnotationShadow> getAnnotations() {
		return annts;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	// joern
	public String getRole() {
		return role;
	}

	public void setRole() {
		this.role = role;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int userId) {
		this.logId = logId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAnotationShadow(AnnotationShadow ans) {
		if (annts == null)
			annts = new ArrayList<AnnotationShadow>(5);

		annts.add(ans);

	}

	public void setAnnotations(Collection<AnnotationShadow> annotations) {
		annts = annotations;
	}

	public int getSampleCount() {
		return samples;
	}

	public int getIndividualCount() {
		return individuals;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void incIndividualCount() {
		this.individuals++;
	}

	public void incSampleCount() {
		this.samples++;
	}

	public void setSampleCount(int i) {
		samples = i;
	}

	public void setIndividualCount(int i) {
		individuals = i;
	}
}
