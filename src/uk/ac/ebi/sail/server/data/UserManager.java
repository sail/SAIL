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

package uk.ac.ebi.sail.server.data;


import java.beans.XMLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import uk.ac.ebi.sail.client.common.AlternativeRequestItem;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.AnnotationShadow;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClassifierManagementException;
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.CollectionManagementException;
import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterFormat;
import uk.ac.ebi.sail.client.common.ParameterManagementException;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.ParseException;
import uk.ac.ebi.sail.client.common.PartRequestItem;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.ProjectionManagementException;
import uk.ac.ebi.sail.client.common.ProjectionShadow;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Relation;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.StudyManagementException;
import uk.ac.ebi.sail.client.common.StudyShadow;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.User;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.common.Variable.Type;
import uk.ac.ebi.sail.client.ui.module.AdminPanel;
import uk.ac.ebi.sail.server.BackendConfigurationManager;
import uk.ac.ebi.sail.server.SSParameterInfo;
import uk.ac.ebi.sail.server.util.BCrypt;
import uk.ac.ebi.sail.server.util.Counter;
import uk.ac.ebi.sail.server.util.SetComparator;
import uk.ac.ebi.sail.server.util.StringUtil;

import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntList;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;
import com.pri.util.stream.StringInputStream;
import uk.ac.ebi.sail.server.util.BCrypt;

public class UserManager
{
	private static final String TBL_USER = "user";
	private static final String TBL_USER_COLLECTION = "user_collection";
	private static final String TBL_COLLECTION = "collection";
	
	private static final String FLD_USER_ID="ID";
	private static final String FLD_USERNAME="username";
	private static final String FLD_NAME="name";
	private static final String FLD_SURNAME="surname";
	private static final String FLD_INSTITUTE="institute";
	private static final String FLD_EMAIL="email";
	private static final String FLD_PASSWORD="password";
	private static final String FLD_COLLECTION_ID="ID";
	private static final String FLD_COLLECTION_NAME="Name";
	 
	private static final String insertUserCollectionSQL = "INSERT INTO " + TBL_USER_COLLECTION + " ("
	+FLD_USER_ID+','+ FLD_COLLECTION_ID + ") VALUES (?,?)";
	 
//	 private static final String selectUserCollectionSQL = "SELECT  FROM " + TBL_VARIANT + " WHERE "+FLD_ID+"=?";

//	 private List<RowProcessor> tagParameters = new ArrayList<RowProcessor>();
	 
	 static UserManager instance ;
	 
	 static Logger logger = 
	  Log.getLogger(DataManager.class);
	 
	 private DataSource dSrc;
	 
/*	 private IntMap<Parameter> params = new IntTreeMap<Parameter>();
	 private Map<String,Parameter> paramCodeMap = new TreeMap<String,Parameter>();
	 
	 private IntMap<Classifier> classifiers = new IntTreeMap<Classifier>();
	 private IntMap<ParameterPart> parts = new IntTreeMap<ParameterPart>();
	 private IntMap<Tag> tags = new IntTreeMap<Tag>();
	 private IntMap<SampleCollection> collections = new IntTreeMap<SampleCollection>();
	 private List<ExpressionRequestItem> expressions = new ArrayList<ExpressionRequestItem>();
	 
	 private Collection<ParameterShadow> paramList;
	 private Collection<Classifier> classifiersList;
	 private List<ProjectionShadow> projectionList=new ArrayList<ProjectionShadow>(20);
	 private List<CollectionShadow> collectionList=new ArrayList<CollectionShadow>(30);
	 private List<StudyShadow> studyList=new ArrayList<StudyShadow>(20);
	 private List<Record> data;
	 
	 private WeakHashMap<Integer, Summary> collectionSummaryCache=new WeakHashMap<Integer, Summary>();
	 private WeakHashMap<Integer, Summary> studySummaryCache=new WeakHashMap<Integer, Summary>();
*/	 
	 
	public static UserManager getInstance()
	{
	  return instance;
	}
	
	public static void setInstance(UserManager userManager)
	 {
	  instance = userManager;
	 }
	
	public UserManager(BackendConfigurationManager defaultCfg)
	 {
	  if( logger == null )
	   logger = Log.getLogger(DataManager.class);
	  
	  try
	  {
	   dSrc = setupDataSource( defaultCfg );
	 
	  }
	  catch(Exception e)
	  {
	   e.printStackTrace();
	  }
	 }

	private static DataSource setupDataSource(BackendConfigurationManager defaultCfg) 
	 {
	  BasicDataSource ds = new BasicDataSource();

	  ds.setDriverClassName(defaultCfg.getDBDriverClass());
	  ds.setUsername(defaultCfg.getDBUserName());
	  ds.setPassword(defaultCfg.getDBPassword());
	  ds.setUrl(defaultCfg.getConnectionURL());

	  ds.setTimeBetweenEvictionRunsMillis(10000);
	  
	  return ds;
	 }
	
	public boolean getUser(String userName)
	 {
		
		boolean userExists = false;
		
		String getUserSQL = "SELECT * FROM USER WHERE "+FLD_USERNAME+" = \""+userName+"\"";
		
		System.out.println("YOUR QUERY IS "+ getUserSQL);
		
		try
		  {

		   Connection conn = dSrc.getConnection();		   
		   PreparedStatement getUser = conn.prepareStatement(getUserSQL, PreparedStatement.RETURN_GENERATED_KEYS);
		    
		   ResultSet userResult = getUser.executeQuery();
		   
		   //ResultSet userResult = getUser.getGeneratedKeys(); 
		    
		    if (userResult != null && userResult.next()) { 
		    	String s = userResult.getString(1);
		    	System.out.println("THIS IS YOUR RESULT: "+s);

		        userExists = true;  
		    }

		    userResult.close();
		    getUser.close();
		    conn.close();
		    
		  }catch(SQLException e)
		  {
			  Log.error("SQL error", e);
			 
		  }
		  return userExists;
		
	 }
	
	public boolean createUser(Map userData)
	 {
		
		boolean created = false;
		
		/*
		System.out.println("YOUR USER IS: "+ userData.get("UserName").toString());
		System.out.println("YOUR NAME IS: "+ userData.get("Name").toString());
		System.out.println("YOUR SURNAME IS: "+ userData.get("Surname").toString());
		System.out.println("YOUR INSTITUTE IS: "+ userData.get("Institute").toString());
		System.out.println("YOUR EMAIL IS: "+ userData.get("email").toString());
		System.out.println("YOUR PASSWORD IS: "+ userData.get("Password").toString());
        */
		
		String hashed = BCrypt.hashpw(userData.get("Password").toString(), BCrypt.gensalt());
  	    userData.put("Password",hashed);
  	    
        // Here we store in the database;
  	  System.out.println("Trying to create user with password "+hashed);
  	  
		
		String params = "\""+ userData.get( "UserName" ).toString() +"\",\""+userData.get( "Name" ).toString()
		+"\",\""+userData.get( "Surname" ).toString() +"\",\""+userData.get( "Institute" ).toString() +"\",\""
		+userData.get( "email" ).toString() +"\",\""+userData.get( "Password" ).toString() +"\")";

		System.out.println("YOUR PARAMS: "+ params);
		
		String insertUserSQL = "INSERT INTO "+ TBL_USER +" ("+FLD_USERNAME+','+FLD_NAME+','
		+FLD_SURNAME+','+FLD_INSTITUTE+','+FLD_EMAIL+','+FLD_PASSWORD+") VALUES ("+params;
		
		System.out.println("YOUR QUERY IS "+ insertUserSQL);
		
		try
		  {
		   Connection conn = dSrc.getConnection();
		   Statement insert = conn.createStatement();

		   insert.executeUpdate(insertUserSQL, PreparedStatement.RETURN_GENERATED_KEYS);
		    
		    ResultSet result = insert.getGeneratedKeys(); 
		    if (result != null && result.next()) { 
		        int rsId = result.getInt(1);  
		    }
  
		    result.close();
		    insert.close();
		    conn.close();
		    
		    created = true;

		  }catch(SQLException e)
		  {
			  Log.error("SQL error", e);
			 
		  }
		  
		  return created;
	 }
	
	public boolean checkUserLogin(Map loginData)
	{
	boolean userExists = false;
	
	String checkUserSQL = "SELECT "+FLD_PASSWORD+" FROM USER WHERE "+FLD_USERNAME+
	" = \""+loginData.get( "UserName" ).toString()+"\"";
	
	System.out.println("YOUR QUERY IS "+ checkUserSQL);
	
	try
	  {

	   Connection conn = dSrc.getConnection();		   
	   PreparedStatement getUser = conn.prepareStatement(checkUserSQL, PreparedStatement.RETURN_GENERATED_KEYS);
	    
	   ResultSet userResult = getUser.executeQuery();
	   
	   //ResultSet userResult = getUser.getGeneratedKeys(); 
	    
	    if (userResult != null && userResult.next()) { 
	    	String pswd = userResult.getString(1);
	    	System.out.println("THIS IS YOUR RESULT: "+pswd);

	    	if(BCrypt.checkpw(loginData.get( "Password" ).toString(), pswd) )
	    	{
	    		userExists = true;  
	    	}
	    }

	    userResult.close();
	    getUser.close();
	    conn.close();
	    
	  }catch(SQLException e)
	  {
		  Log.error("SQL error", e);
		 
	  }
	  return userExists;
	}
 }

