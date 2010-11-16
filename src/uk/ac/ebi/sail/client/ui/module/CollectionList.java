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

package uk.ac.ebi.sail.client.ui.module;

import java.util.Collection;

import uk.ac.ebi.sail.client.CollectionManager;
import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.CollectionProvider;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class CollectionList extends ObjectList<SampleCollection> implements
		CollectionProvider, DataChangeListener {
	private static RecordDef recordDef = new RecordDef(new FieldDef[] {
			new StringFieldDef("name"), new ObjectFieldDef("recs"),
			new ObjectFieldDef("obj") });

	private static ColumnModel columnModel = new ColumnModel(
			new ColumnConfig[] {
					new ColumnConfig("Collection", "name", 130, true, null,
							"name"),
					new ColumnConfig("Records", "recs", 130, true, null, "recs"), });

	private CollectionManager manager;

	public CollectionList(Selection single, Action[] buttons,
			CollectionManager mngr) {
		super(single, buttons, recordDef, columnModel, "name");

		setTitle("Collection list");
		getGrid().setAutoExpandColumn("name");
		manager = mngr;

		manager.addDataChangeListener(this);

		loadData();
	}

	public void loadData() {
		getStore().removeAll();

		addObjects(manager.getCollections());
	}

	public void addObject(SampleCollection ct) {
		Object[] data = new Object[3];

		data[0] = ct.getName();
		data[1] = ct.getSampleCount();
		data[2] = ct;

		getStore().add(recordDef.createRecord(data));
	}

	public void addObjects(Collection<SampleCollection> ctl) {
		for (SampleCollection p : ctl) {

			// joern
			Object[] data = new Object[3];
			if (AdminPanel.user.getRole().equalsIgnoreCase("ADMIN")) {
				data[0] = p.getName();
				data[1] = p.getSampleCount();
				data[2] = p;
				getStore().add(recordDef.createRecord(data));
			} else {
				// joern
				if (AdminPanel.user.getCollectionId() == null
						& p.getUserId() != 0) {
				} else if (p.getUserId() == 0) {
					data[0] = p.getName();
					data[1] = p.getSampleCount();
					data[2] = p;
					getStore().add(recordDef.createRecord(data));
				} else if (AdminPanel.user.getCollectionId().contains(
						Integer.toString(p.getId()))) {
					data[0] = p.getName();
					data[1] = p.getSampleCount();
					data[2] = p;
					getStore().add(recordDef.createRecord(data));
				}
			}
		}
	}

	public void dataChanged() {
		loadData();
	}

	public void destroy() {
		manager.removeDataChangeListener(this);

		super.destroy();
	}

	public int[] getCollections() {
		Collection<SampleCollection> rs = getSelection();

		if (rs == null)
			return null;

		int[] rIds = new int[rs.size()];

		int i = 0;
		for (SampleCollection r : rs)
			rIds[i++] = r.getId();

		return rIds;
	}
}
