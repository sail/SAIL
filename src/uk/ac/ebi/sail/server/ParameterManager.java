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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;

import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntList;

public class ParameterManager
{
 private static final String patternProperty="pattern";
 
 public static final String SEP="\t";
 
// private static final File inputFile = new File("c:\\tmp\\Phenotypes.txt");
 
 List<Parameter> params = new ArrayList<Parameter>(100);
 Map<String,Parameter> paramMap = new TreeMap<String,Parameter>();
 
 private static ParameterManager instance = new ParameterManager();
 
 
 public static ParameterManager getInstance()
 {
  return instance;
 }
 
 private ParameterManager()
 {
  try
  {
   InputStream is = this.getClass().getResourceAsStream("/Phenotypes.txt");
   
 //  FileInputStream is = new FileInputStream(inputFile);

   ByteArrayOutputStream baos = new ByteArrayOutputStream();

   byte[] buf = new byte[100];

   int n;
   while((n = is.read(buf)) > 0)
    baos.write(buf, 0, n);

   is.close();
   baos.close();

//   for( Charset cs : Charset.availableCharsets().values() )
//    System.out.println(cs.name());   
   
   String txt = new String(baos.toByteArray(),"UTF-16");

   int cpos = 0;
   int len = txt.length();
   
   Parameter cp = null;
   Qualifier cq = null;
   Variable  cv = null;
   
   while(cpos < len)
   {
    int pos = txt.indexOf("\r\n", cpos);

    if(pos == -1)
     break;

    String[] parts = txt.substring(cpos, pos).split(SEP);
    
    for( int j=0; j < parts.length; j++ )
    {
     if( parts[j].length() >= 2 && parts[j].charAt(0)=='"' && parts[j].charAt(parts[j].length()-1) == '"')
      parts[j] = parts[j].substring(1,parts[j].length()-1);
    }
    
    if( parts == null || parts.length < 2 )
    {
     cpos=pos+2;
     continue;
    }
    
    if( "Code".equals(parts[0]) )
    {
     if( cp != null )
     {
      if( cp.getDescription() == null )
       cp.setDescription(cp.getName());
      
      params.add(cp);
      paramMap.put(cp.getCode(),cp);
     }
     
     cp = new Parameter();
     cp.setCode(parts[1]);
     
    }
    else if( "Name".equals(parts[0]))
     cp.setName(parts[1]);
    else if( "Desc".equals(parts[0]))
     cp.setDescription(parts[1]);
    else if( "Inherit".equals(parts[0]) )
    {
     for( Parameter p : params )
     {
      if( p.getCode().equals(parts[1]))
      {
       cp.addInheritedParameter(p);
       break;
      }
     }
    }
    else if( "Variable".equals(parts[0]) )
    {
     cv = new Variable();
     cv.setName(parts[1]);
     cp.addVariable(cv);
     cq=null;
    }
    else if( "Type".equals(parts[0]) )
    {
     cv.setType( Variable.Type.valueOf(parts[1]) );
    }
    else if( "Qualifier".equals(parts[0]) )
    {
     cv=null;
     cq= new Qualifier();
     cq.setName(parts[1]);
     cp.addQualifier(cq);
    }
    else if( "Values".equals(parts[0]) )
    {
     for( int i=1; i<parts.length; i++ )
     {
      if( parts[i] == null || parts[i].length() == 0 )
       break;
      
      int espos = parts[i].indexOf("=");
      
      if( espos != -1 )
       parts[i] = parts[i].substring(0,espos).trim();
      
      if( cv != null )
       cv.addVariant( new Variant(parts[i],1,true) ); //TODO variant coding handling
      else
       cq.addVariant(new Variant(parts[i],1,true));
      
     }
    }
    else if( parts[0].length() > 0 )
     System.out.println("Invalid string: "+parts[0]+" -> "+parts[1]);
     

    cpos = pos + 2;
   }
   
   if( cp != null )
   {
    params.add(cp);
    paramMap.put(cp.getCode(), cp);
   }
   
   is.close();
  }
  catch(FileNotFoundException e)
  {
   e.printStackTrace();
  }
  catch(IOException e)
  {
   e.printStackTrace();
  }
  catch (Exception e) 
  {
   e.printStackTrace();
  }
  finally
  {
//   System.out.println("Loaded: "+params.size());
  }

  /*
  try
  {
   FileOutputStream fos = new FileOutputStream("c:\\tmp\\header.txt");
   
   for( Parameter p : params )
   {
    if( p.getVariables() != null )
    {
     for( Variable v : p.getVariables() )
     {
      fos.write((p.getCoding()+'.'+v.getName()).getBytes());
      fos.write('\t');
     }
    }
    
    if( p.getQualifiers() != null )
    {
     for( Qualifier v : p.getQualifiers() )
     {
      fos.write((p.getCoding()+'.'+v.getName()).getBytes());
      fos.write('\t');
     }
    }

   }

   fos.close();
  }
  catch(FileNotFoundException e)
  {
   e.printStackTrace();
  }
  catch(IOException e)
  {
   e.printStackTrace();
  }
*/  
 }
 
 public void generatePatterns()
 {
  int deferred1=0, deferred2=-1;
  
  while( deferred1 != deferred2 && deferred2 != 0 )
  {
   
  }
  
  for( Parameter p : params )
  {
//   if( p.getProperty(patternProperty) != null )
//    continue;
   
   try
   {
    generatePattern(p);
   }
   catch(UnusedParameterException e)
   {
    p.setRecordsCount(-1);
   }

  }
 }
 
 private void generatePattern( Parameter p ) throws UnusedParameterException
 {
  IntList patt = new ArrayIntList();
  
  if( p.getInheritedParameters() != null )
  {
   for( Parameter ip : p.getInheritedParameters() )
   {
//    IntList ptrn = (IntList)ip.getProperty(patternProperty);
    
//    if( ptrn == null )
//    {
//     generatePattern( ip );
//     ptrn = (IntList)ip.getProperty(patternProperty);
//    }
    
// TODO need to be implemented   patt.addAll( ptrn );
   }
  }
  
  if( p.getVariables() != null )
  {
   for( Variable v : p.getVariables() )
   {
    Column col = (Column)v.getProperty("column");
    
    if( col == null )
     throw new UnusedParameterException();
    
    patt.add(col.getColumnIndex() );
   }
  }
  
  if( p.getQualifiers() != null )
  {
   for( Qualifier v : p.getQualifiers() )
   {
    Column col = (Column)v.getProperty("column");
    
    if( col == null )
     throw new UnusedParameterException();
    
    patt.add( col.getColumnIndex() );
   }
  }

 }
 
 public List<Parameter> getParameters()
 {
  return params;
 }

 public Parameter getParameter(String paramId)
 {
  return paramMap.get(paramId);
 }
 
}
