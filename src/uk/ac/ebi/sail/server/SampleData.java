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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Report;
import uk.ac.ebi.sail.client.common.SimpleCounter;
import uk.ac.ebi.sail.client.common.Variable;


public class SampleData
{
 private static final String  avail     = "";

 private String[]             header;
 private Map<String, Integer> headerMap = new HashMap();
 private List<String[]>       data      = new ArrayList<String[]>(50);

 public SampleData()
 {
  try
  {
//   InputStream is = new FileInputStream("c:\\tmp\\saildb.csv");
   InputStream is = getClass().getResourceAsStream("/saildb.csv");

   ByteArrayOutputStream baos = new ByteArrayOutputStream();

   byte[] buf = new byte[100];

   int n;
   while((n = is.read(buf)) > 0)
    baos.write(buf, 0, n);

   is.close();
   baos.close();

   String txt = new String(baos.toByteArray());

   int cpos = 0;
   int len = txt.length();
   boolean first = true;
//   int ln=0;
   while(cpos < len)
   {
//    ln++;
//    System.out.println("Line: "+ln);
    int pos = txt.indexOf("\r\n", cpos);

    if(pos == -1)
     break;

    String[] parts = txt.substring(cpos, pos).split(",",-2);

    if(first)
    {
     header = parts;
     first = false;
     System.out.println("Header: "+header.length);
    }
    else
    {
     String[] row = new String[header.length];

     for(int i = 0; i < header.length; i++)
     {
      if(parts[i].length() > 0)
      {
       if(parts[i].equals("1"))
        row[i] = avail;
       else
        row[i] = new String(parts[i]);
      }
     }

     data.add(row);
    }

    cpos = pos + 2;
   }

   for(int i = 0; i < header.length; i++)
    headerMap.put(header[i], i);
  
   is.close();
  }
  catch(FileNotFoundException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }

 private Parameter findParameter(String s)
 {
  for(Parameter pr : ParameterManager.getInstance().getParameters())
  {
   if(pr.getCode().equals(s))
   {
    return pr;
   }
  }

  return null;
 }

 private int[] getParameterPattern(Parameter p)
 {
  Collection<Variable> v = p.getVariables();
  Collection<Qualifier> q = p.getQualifiers();

  int[] clms = new int[(v == null ? 0 : v.size()) + (q == null ? 0 : q.size())];

  int k = 0;
  if(v != null)
  {
   for(Variable vr : v)
   {
    Integer c = headerMap.get(p.getCode() + "." + vr.getName());
    if(c == null)
    {
     System.out.println("Column not found: " + p.getCode() + "." + vr.getName());
     return null;
    }

    clms[k++] = c;
   }
  }

  if(q != null)
  {
   for(Qualifier ql : q)
   {
    Integer c = headerMap.get(p.getCode() + "." + ql.getName());
    if(c == null)
    {
     System.out.println("Column not found: " + p.getCode() + "." + ql.getName());
     return null;
    }

    clms[k++] = c;
   }
  }

  return clms;
 }
 
 boolean matchPattern(int[] cls, String[] row )
 {
  for(int j = 0; j < cls.length; j++)
   if(row[cls[j]] == null)
    return false;

  
  return true;
 }

 static class GpPat
 {
  String name;
  int[]  pat;
 }

 public Report report(String[][] req)
 {
  Report chain = new Report();
  
  Object[] pat = new Object[req.length];
  for(int i = 0; i < req.length; i++)
  {
   if( req[i][0].equals("G") )
   {
    GpPat[] gpats = new GpPat[req[i].length-2];

    for(int j = 2; j < req[i].length; j++)
    {
     if(req[i][j] == null)
      break;
     GpPat gp = new GpPat();
     gp.name = req[i][j];
     gp.pat = getParameterPattern(findParameter(req[i][j]));
     gpats[j-2] = gp;
    }
    
    pat[i]=gpats;
    
    chain.addHeader(req[i][1]);

   }
   else
   {
    Parameter p = findParameter(req[i][1]);

    if(p == null)
    {
     System.out.println("Parameter not found. ID=" + req[i][0]);
     return null;
    }

    if(req[i][0].equals("P"))
    {
     pat[i] = getParameterPattern(p);
     chain.addHeader(p.getName());
    }
    else if( req[i][0].equals("Q") )
    {
     Qualifier q = null;

     Collection<Qualifier> qs = p.getQualifiers();

     if(qs == null)
     {
      System.out.println("Qualifier not found. pID=" + req[i][1] + " qID=" + req[i][2]);
      return null;
     }


     for(Qualifier ql : qs)
     {
      if(ql.getName().equals(req[i][2]))
      {
       q = ql;
       break;
      }
     }

     if(q == null)
     {
      System.out.println("Qualifier not found. pID=" + req[i][1] + " qID=" + req[i][2]);
      return null;
     }

     chain.addHeader(p.getName()+"."+q.getName());

     Integer c = headerMap.get(q.getParameter().getCode() + "." + q.getName());
     if(c == null)
     {
      System.out.println("Column not found: " + q.getParameter().getCode() + "." + q.getName());
      return null;
     }
     pat[i] = c;
    }
    else if( req[i][0].equals("V") )
    {
     Variable v = null;

     Collection<Variable> qs = p.getVariables();

     if(qs == null)
     {
      System.out.println("Variable not found. pID=" + req[i][1] + " qID=" + req[i][2]);
      return null;
     }

     for(Variable ql : qs)
     {
      if(ql.getName().equals(req[i][2]))
      {
       v = ql;
       break;
      }
     }

     if(v == null)
     {
      System.out.println("Variable not found. pID=" + req[i][1] + " qID=" + req[i][2]);
      return null;
     }

     chain.addHeader(p.getName()+"."+v.getName());

     Integer c = headerMap.get(v.getParameter().getCode() + "." + v.getName());
     if(c == null)
     {
      System.out.println("Column not found: " + v.getParameter().getCode() + "." + v.getName());
      return null;
     }
     pat[i] = c;
    }
   }
  }

  // Object chain=(req[0] instanceof Parameter)?new SimpleCounter():new
  // HashMap<String,SimpleCounter>() ;


  chain.setCount(data.size());

  SimpleCounter cpos = chain;

  int m=0;
  
  rowcycle: for(String[] row : data)
  {
   cpos = chain;

//   if( m == 25)
//    System.out.println("m="+m);
   
   processPatterns( pat, 0, chain, row );
   
   m++;
  }

  return chain;
 }

 private void processPatterns( Object[] pat, int offset, SimpleCounter cpos, String[] row)
 {
  for(int i = offset; i < pat.length; i++)
  {
   if(pat[i] instanceof int[])
   {
    if( ! matchPattern((int[]) pat[i], row))
    {
//     for( int k=i; k < pat.length; k++ )
//     {
//      if(cpos.getRef() == null)
//       cpos.setRef(new SimpleCounter());
//      
//      cpos=cpos.getRef();
//     }
     return;
    }
    
    if(cpos.getRef() == null)
     cpos.setRef(new SimpleCounter());

    cpos = cpos.getRef();
    cpos.inc();
   }
   else if( pat[i] instanceof GpPat[] )
   {
    GpPat[] gpp = (GpPat[])pat[i];

    boolean matched=false;
    for( int k=0; k < gpp.length; k++ )
    {
     if( gpp[k]==null )
      break;
     
     if( matchPattern(gpp[k].pat, row) )
     {
      matched=true;
      
      if( cpos.getRef() == null )
       cpos.setRef( new SimpleCounter() );

      SimpleCounter sc = cpos.getRef().getRef(gpp[k].name);
      sc.inc();
      processPatterns(pat,i+1,sc,row);
     }
     
    }

    if( matched )
    {
     SimpleCounter sc = cpos.getRef().getRef("[ANY]");
     sc.inc();
     processPatterns(pat,i+1,sc,row);
    }
    return;
   }
   else
   {
    int col = (Integer) pat[i];
//    String key = row[col]==null?"[not set]":row[col];
    String key = row[col];

    if(key != null)
    {
     if( cpos.getRef() == null )
      cpos.setRef( new SimpleCounter() );
     
     cpos=cpos.getRef();
     
     SimpleCounter sc = cpos.getRef(key);
     sc.inc();
     cpos = sc;
    }
    else
     break;

   }
  }  
 }
}
