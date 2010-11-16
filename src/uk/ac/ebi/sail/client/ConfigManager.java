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

package uk.ac.ebi.sail.client;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.common.M1codec;

import com.google.gwt.user.client.Cookies;

public class ConfigManager
{
 public static final String TAG_FILTER_PARAM = "ParamTagFilter";
 public static final String TUTORIAL_URL_PARAM = "-ParamTutorialURL";
 
 public static final String SAIL_RESOURCE_PATH="";//"sail/";
 
 private static final String cookieName =  "SAILConfig";
 
 private static Map<String,String> cfg = new TreeMap<String,String>();
 
 static
 {
  String cookie = Cookies.getCookie(cookieName);
  
  if( cookie != null )
  {
   String[] vals = cookie.split(";");
   
   for( String pair : vals )
   {
    int eq = pair.indexOf('=');
    
    cfg.put(M1codec.decode(pair.substring(0,eq)), M1codec.decode(pair.substring(eq+1)));
   }
  }
  
 // cfg.put("SAIL_CLIENT_ParameterTagFilter","Test3Classif:B");
 }
 
 public static String getParameterTagFilter()
 {
  return cfg.get(TAG_FILTER_PARAM);
 }
 
 public static String getTutorialURL()
 {
  return cfg.get(TUTORIAL_URL_PARAM);
 }

 
 public static void setParameterTagFilter( String tf )
 {
  setConfigParameter(TAG_FILTER_PARAM, tf);
 }
 
 
 public static String getConfigParameter(String key)
 {
  return cfg.get(key);
 }

 public static void setConfigParameter(String key, String value)
 {
  cfg.put(key,value);
  
  sync();
 }

 public static void sync()
 {
  StringBuilder sb = new StringBuilder(1000);
  
  for( Map.Entry<String, String> me : cfg.entrySet() )
  {
   if( ! me.getKey().startsWith("-") )
    sb.append(M1codec.encode(me.getKey(), "=;")).append('=').append(M1codec.encode(me.getValue(), "=;")).append(';');
  }
  
  if(sb.length() < 2 )
   return;
  
  sb.setLength(sb.length()-1);
  
  Cookies.setCookie(cookieName, sb.toString(), new Date(System.currentTimeMillis()+365L*24L*3600L*1000L));
 }

 public static void setDefaultConfiguration(Map<String, String> map)
 {
  for( Map.Entry<String, String> me : map.entrySet() )
  {
   if( ! cfg.containsKey(me.getKey()) )
    cfg.put(me.getKey(), me.getValue() );
    
  }
  
 }

 
}
