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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.server.data.DataManager;

import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntList;

/**
 * Servlet implementation class DataTemplateSvc
 */
public class DataTemplateSvc extends HttpServlet
{
 private static final long serialVersionUID = 1L;

 private static int        reqCount         = 1;

 /**
  * @see HttpServlet#HttpServlet()
  */
 public DataTemplateSvc()
 {
  super();
  // TODO Auto-generated constructor stub
 }

 /**
  * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
  *      response)
  */
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
 {
  String idsstr = request.getParameter("request");
  
  IntList ids = new ArrayIntList();
  
  int pos=0;
  
  while( pos < idsstr.length() )
  {
   int cpos = idsstr.indexOf(',',pos);
   
   String intStr;
   
   if( cpos < 0 )
    intStr = idsstr.substring(pos);
   else
    intStr = idsstr.substring(pos,cpos);
   
   try
   {
    ids.add(Integer.parseInt(intStr));
   }
   catch (Exception e) 
   {
   }
   
   if( cpos < 0 )
    break;
   
   pos=cpos+1;
  }
  
//  List<Parameter> pLst = DataManager.getInstance().getParameters(ids);
  
  String fileName = "SAIL_data_template_" + (reqCount++) + ".txt";

  response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
  response.addHeader("Content-Type", "text/plain; name=\"" + fileName + "\"");
  response.setCharacterEncoding("UTF-8");

  PrintWriter out = response.getWriter();
  
  out.print("SAMPLE.ID");
  
  IntIterator iiter = ids.listIterator();
  
  while( iiter.hasNext() )
  {
   Parameter p = DataManager.getInstance().getParameter(iiter.next());
   
   if( p != null )
   {
    Collection<Variable> vrbls = p.getAllVariables();
    
    if( vrbls != null )
    {
     for( Variable v : vrbls )
     {
      out.print(",\"");
      out.print(p.getCode());
      out.print('.');
      out.print(v.getName());
      out.print('"');
     }
    }
    
    Collection<Qualifier> qls = p.getAllQualifiers();
    
    if( qls != null )
    {
     for( Qualifier q : qls )
     {
      out.print(",\"");
      out.print(p.getCode());
      out.print('.');
      out.print(q.getName());
      out.print('"');
     }
    }
   }
  }
  
//  out.print("\n");
  
 }

 /**
  * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
  *      response)
  */
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
 {
  // TODO Auto-generated method stub
 }

}
