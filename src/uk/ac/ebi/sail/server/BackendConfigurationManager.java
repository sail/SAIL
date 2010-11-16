
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

package uk.ac.ebi.sail.server;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BackendConfigurationManager implements ServletContextListener
{
 static final String CLIENT_PARAM_PREFIX = "Client:";
 
 static final String DB_URL_PARAM = "SAIL_DBURL";
 static final String DB_DRIVER_PARAM = "SAIL_DBDriver";
 static final String DB_USERNAME_PARAM = "SAIL_DBUserName";
 static final String DB_PASSWORD_PARAM = "SAIL_DBPassword";
 
// static final String DEFAULT_DB_URL = "jdbc:mysql://mysql-sail.ebi.ac.uk:4188/sail1";
//static final String DEFAULT_DB_URL = "jdbc:mysql://darksite/sail";
//static final String DEFAULT_DB_URL = "jdbc:mysql://localhost/sail";
// static final String DEFAULT_DB_URL = "jdbc:mysql://localhost/sail_test3";
 static final String DEFAULT_DB_URL = "jdbc:mysql://localhost/sail";
// static final String DEFAULT_DB_URL = "jdbc:mysql://localhost/SAIL";
//static final String DEFAULT_DB_URL = "jdbc:mysql://mysql-sail.ebi.ac.uk:4188/sail_julio";
//static final String DEFAULT_DB_URL = "jdbc:mysql://mysql-sail.ebi.ac.uk:4188/sail_sep";

 static final String DEFAULT_DB_Driver = "com.mysql.jdbc.Driver";
 static final String DEFAULT_DB_UserName = "root";
// static final String DEFAULT_DB_Password = "joern";
 static final String DEFAULT_DB_Password = "Pal3*bq";
// static final String DEFAULT_DB_UserName = "sail_test";
// static final String DEFAULT_DB_Password = "kadabra";
 
 static private BackendConfigurationManager instance;
 
 private ServletContext context;
 private Map<String,String> clientConfig;
 
 public BackendConfigurationManager(ServletContext servletContext)
 {
  context=servletContext;
 }

 public String getConnectionURL()
 {
  String url = context.getInitParameter(DB_URL_PARAM);
  
  if( url == null )
   url = DEFAULT_DB_URL;
  
  return url;
 }
 
 public String getDBDriverClass()
 {
  String driver = context.getInitParameter(DB_DRIVER_PARAM);
  
  if( driver == null )
   driver = DEFAULT_DB_Driver;
  
  return driver;
 }

 public String getDBUserName()
 {
  String uname = context.getInitParameter(DB_USERNAME_PARAM);
  
  if( uname == null )
   uname = DEFAULT_DB_UserName;
  
  return uname;
 }
 
 public String getDBPassword()
 {
  String passwd = context.getInitParameter(DB_PASSWORD_PARAM);
  
  if( passwd == null )
   passwd = DEFAULT_DB_Password;
  
  return passwd;
 }

 public String getConfirationParameter( String paramName )
 {
  return context.getInitParameter(paramName);
 }
 
 
 public void contextDestroyed(ServletContextEvent arg0)
 {
 }

 public void contextInitialized(ServletContextEvent arg0)
 {
  context=arg0.getServletContext();
 }

 public static void setInstance(BackendConfigurationManager defaultCfg)
 {
  instance = defaultCfg;
 }
 
 public static BackendConfigurationManager getInstance()
 {
  return instance;
 }

 public Map<String, String> getClientConfiguration()
 {
  if( clientConfig == null )
  {
   clientConfig = new TreeMap<String, String>();
   
   Enumeration<?> enm = context.getInitParameterNames();
   
   while( enm.hasMoreElements() )
   {
    String pName = (String)enm.nextElement();
    
    if( pName.startsWith(CLIENT_PARAM_PREFIX) )
     clientConfig.put(pName.substring(CLIENT_PARAM_PREFIX.length()), context.getInitParameter(pName) );
   }
  }
  
  return clientConfig;
 }
}
