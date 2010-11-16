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

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Classifier.Target;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class CollectionEditPanel extends FormPanel {
	private ObjectAction<SampleCollection> listener;

	private SampleCollection repos;
	private AnnotatedEditor annEdit;
	private TextField nameField;

	public CollectionEditPanel() {
		setFrame(true);
		setPaddings(5, 5, 5, 0);
		setWidth(600);

		add(nameField = new TextField("Name"), new AnchorLayoutData("95%"));
		add(annEdit = new AnnotatedEditor(Target.COLLECTION_ANN),
				new AnchorLayoutData("95%"));

		BtListener lsn = new BtListener();
		Button bt = new Button("Save", lsn);
		bt.setStateId("ok");
		addButton(bt);

		bt = new Button("Cancel", lsn);
		bt.setStateId("cancel");
		addButton(bt);
	}

	public void setCollection(SampleCollection coll) {
		repos = coll;
		nameField.setValue(coll.getName());
		annEdit.setAnnotatedObject(repos);
	}

	// public void deleteCollection(SampleCollection coll)
	// {
	// repos = coll;
	// System.out.print(repos);
	// // nameField.setValue(coll.getName());
	// // annEdit.setAnnotatedObject(repos);
	// }

	public void setObjectActionListener(
			ObjectAction<SampleCollection> objectAction) {
		listener = objectAction;
	}

	private class BtListener extends ButtonListenerAdapter {
		public void onClick(Button button, EventObject e) {
			if (listener == null)
				return;

			String act = button.getStateId();

			if ("cancel".equals(act)) {
				listener.doAction(act, null);
				return;
			}

			repos.setName(nameField.getValueAsString());
			listener.doAction(act, repos);
		}
	}
}
