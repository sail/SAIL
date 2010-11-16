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

package uk.ac.ebi.sail.server.service;

import java.util.Map;
import uk.ac.ebi.sail.client.ui.module.LoginService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import uk.ac.ebi.sail.server.data.UserManager;


public class LoginServiceImpl extends RemoteServiceServlet implements LoginService
{
	   public boolean userIsValid( Map loginData )
	   {
	      boolean accepted = false;
	      //boolean accepted = true;
	      
	      //String name = loginData.get( "UserName" ).toString();
	      //String pswd = loginData.get( "Password" ).toString();

	      
	      // HERE WE IMPLEMENT A REAL CHECK FOR PASSWORD LOGIN
	      	   
	      accepted = UserManager.getInstance().checkUserLogin(loginData); 
	      System.out.println("The user creating return value:" + accepted);

	      return accepted;
	   }

}
