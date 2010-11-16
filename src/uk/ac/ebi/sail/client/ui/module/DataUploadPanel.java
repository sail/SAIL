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

// This is the module for data availability upload by users.

package uk.ac.ebi.sail.client.ui.module;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.HTMLPanel;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.TableLayout;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.dd.DD;
import com.gwtext.client.widgets.*;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

public class DataUploadPanel extends Panel {
	public DataUploadPanel() {
		setPaddings(20);

		Button button = new Button("Login", new ButtonListenerAdapter() {
			public void onClick(final Button button, EventObject e) {
				System.out.println("here I am");
				PopupPanel popup = new PopupPanel(true);
				String text = "Click <i>outside</i> popup<br/>to close it";
				// VerticalPanel vPanel = makeVerticalPanel(text, 2);
				// popup.setWidget(vPanel);
				// UIObject button2 = (UIObject)event.getSource();
				// int x = button2.getAbsoluteLeft() + 100;
				// int y = button2.getAbsoluteTop() - 100;
				// popup.setPopupPosition(x, y);
				// popup.setAnimationEnabled(true);
				// popup.show();

			}
		});

		addButton(button);
	}

}
