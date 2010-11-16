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
import uk.ac.ebi.sail.client.ui.module.AdminUserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import uk.ac.ebi.sail.server.data.UserManager;

public class AdminUserServiceImpl extends RemoteServiceServlet implements AdminUserService
{
	   public boolean createUser( Map userData )
	   {
		   
		  boolean userCreated = false;
	      //boolean userExists = false;
	      //boolean accepted = true;
	      
	      String userName = userData.get( "UserName" ).toString();
	      String pswd = userData.get( "Password" ).toString();
	      //String pswd_confirm = userData.get( "Confirm Password").toString();
	      //String name = userData.get( "Name" ).toString();
	      //String surname = userData.get( "Surname" ).toString();
          //String email = userData.get( "email" ).toString();
	      //String institute = userData.get( "Institute" ).toString();

	      if (!pswd.equals(userData.get( "Confirm Password").toString())){
	    	  //userCreated = false;
	    	  System.out.println("Password failed with values" + pswd + " and " + userData.get( "Confirm Password").toString() );
	    	  return userCreated;
	      }
	      System.out.println("checking if user exists");  
	      if(UserManager.getInstance().getUser(userName) )
	      {
	    	  System.out.println("The user already exists");
	    	  return userCreated;
	      }
	      else
	      {
	    	  
	    	  userCreated = UserManager.getInstance().createUser(userData); 
	    	  System.out.println("The user creating return value:" + userCreated);
	    	  
	      }
	      return userCreated;
	   }
	   
	   public boolean createUserCollection(Map ucData)
	   {
		   boolean linkCreated = false;
		   
		   return linkCreated;
	   }
}
