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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import java.util.Map;


public interface AdminUserService extends RemoteService
{
	 public static final String SERVICE_URI = "/adminUser";

	   public static class Util {
	      public static AdminUserServiceAsync getInstance() {

	         AdminUserServiceAsync instance = (AdminUserServiceAsync) GWT.create(AdminUserService.class);
	         ServiceDefTarget target = (ServiceDefTarget) instance;
	         target.setServiceEntryPoint(GWT.getModuleBaseURL() + SERVICE_URI);
	         return instance;
	      }
	   }

	   public boolean createUser( Map userData );
	   public boolean createUserCollection( Map ucData );

}
