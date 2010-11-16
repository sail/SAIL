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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import java.util.HashMap;
import java.util.Map;

public class ManageUserPanel extends Panel {
	public ManageUserPanel() {

		Panel nuPanel = new Panel();
		nuPanel.setTitle("Create User");
		nuPanel.setShadow(true);

		final TextBox userName = new TextBox();
		final PasswordTextBox password = new PasswordTextBox();
		final PasswordTextBox confirmPassword = new PasswordTextBox();
		final TextBox name = new TextBox();
		final TextBox surname = new TextBox();
		final TextBox institute = new TextBox();
		final TextBox email = new TextBox();

		// Error Label
		Label lblError = new Label();

		// Grid main = new Grid(2,2);

		Grid grid = new Grid(9, 2);
		// Set the error label
		grid.setWidget(0, 1, lblError);
		// Add the Label for the username
		grid.setWidget(1, 0, new Label("Username"));
		// Add the UserName textBox
		grid.setWidget(1, 1, userName);
		// Add the label for password
		grid.setWidget(2, 0, new Label("Password"));
		// Add the password widget
		grid.setWidget(2, 1, password);
		// Add label for confirm password
		grid.setWidget(3, 0, new Label("Confirm Password"));
		// Add the confirm password widget
		grid.setWidget(3, 1, confirmPassword);
		// Add the Label for the name
		grid.setWidget(4, 0, new Label("Name"));
		// Add the Name textBox
		grid.setWidget(4, 1, name);
		// Add the Label for the surname
		grid.setWidget(5, 0, new Label("Surname"));
		// Add the surName textBox
		grid.setWidget(5, 1, surname);
		// Add the Label for the institute
		grid.setWidget(6, 0, new Label("Institute"));
		// Add the institute textBox
		grid.setWidget(6, 1, institute);
		// Add the Label for the email
		grid.setWidget(7, 0, new Label("email"));
		// Add the UserName email
		grid.setWidget(7, 1, email);
		// Create a button
		Button create = new Button("Create User");
		// Add the Login button to the form
		grid.setWidget(8, 1, create);

		final AdminUserServiceAsync AdminUserService = (AdminUserServiceAsync) GWT
				.create(AdminUserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) AdminUserService;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "adminUser";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		final AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				boolean ok = Boolean.valueOf(result.toString()).booleanValue();
				if (ok) {
					MessageBox.alert("Success", "Successfully created user!");
				} else {
					MessageBox
							.alert("Invalid",
									"Username already exists or password don't match confirm password");
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.alert("Error",
						"Error while creating user " + caught.getMessage());
			}
		};

		create.addListener(new ButtonListenerAdapter() {
			public void onClick(final Button button, EventObject e) {

				String unText = userName.getText();
				String passwordText = password.getText();
				String cPasswordText = confirmPassword.getText();
				String nText = name.getText();
				String sText = surname.getText();
				String iText = institute.getText();
				String eText = email.getText();

				Map userData = new HashMap();
				userData.put("UserName", unText);
				userData.put("Password", passwordText);
				userData.put("Confirm Password", cPasswordText);
				userData.put("Name", nText);
				userData.put("Surname", sText);
				userData.put("email", eText);
				userData.put("Institute", iText);

				AdminUserService.createUser(userData, callback);
			}
		});

		nuPanel.add(grid);

		Panel ucPanel = new Panel();
		ucPanel.setTitle("Add Collections to User");
		Grid ucGrid = new Grid(2, 2);

		ScrollPanel uScroller = new ScrollPanel();
		uScroller.setSize("400px", "100px");
		ucGrid.setWidget(0, 0, uScroller);

		ScrollPanel cScroller = new ScrollPanel();
		cScroller.setSize("400px", "100px");
		ucGrid.setWidget(0, 1, cScroller);

		Button link = new Button("Asign collection to user");
		ucGrid.setWidget(1, 1, link);

		final AsyncCallback ucCallback = new AsyncCallback() {
			public void onSuccess(Object result) {
				boolean ok = Boolean.valueOf(result.toString()).booleanValue();
				if (ok) {
					MessageBox.alert("Success", "Successfully created link!");
				} else {
					MessageBox.alert("Invalid",
							"Was not able to create connexion");
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.alert("Error",
						"Error while creating link " + caught.getMessage());
			}
		};

		link.addListener(new ButtonListenerAdapter() {
			public void onClick(final Button button, EventObject e) {

				String user = ""; //userValue.getText();
				String collection = "";  //collectionValue.getText();

				Map ucData = new HashMap();
				ucData.put("user", user);
				ucData.put("collection", collection);

				AdminUserService.createUserCollection(ucData, ucCallback);
			}
		});

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.add(nuPanel);
		vPanel.add(ucPanel);
		add(vPanel);

	}

}
