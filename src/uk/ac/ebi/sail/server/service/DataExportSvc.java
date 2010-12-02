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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.server.data.DataManager;
import uk.ac.ebi.sail.server.data.ExportReport;
import uk.ac.ebi.sail.server.data.ExportReport2;
import uk.ac.ebi.sail.server.data.IntPartValue;
import uk.ac.ebi.sail.server.data.PartValue;
import uk.ac.ebi.sail.server.data.RealPartValue;
import uk.ac.ebi.sail.server.data.Record;
import uk.ac.ebi.sail.server.data.VariantPartValue;

import com.pri.util.collection.IntMap;

public class DataExportSvc extends HttpServlet
{

 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
 {
  if( req.getUserPrincipal() == null )
  {
   resp.getWriter().print("<HTML><HEAD><TITLE>Error</TITLE></HEAD><BODY><H1 style='color: red'>Permission denied</H1></BODY></HTML>");
   return;
  }
  
  String idsParam = req.getParameter("idsOnly");
  
  boolean idsOnly = idsParam!=null && ( "true".equalsIgnoreCase(idsParam) );
  
  String reqst = req.getParameter("request");
  
  ReportRequest nri = null;
  
  try
  {
   nri= ReqStr2NReq.convert(reqst);
  }
  catch(Exception e)
  {
   resp.getWriter().print("<HTML><HEAD><TITLE>Error</TITLE></HEAD><BODY><H1 style='color: red'>"+e.getMessage()+"</H1></BODY></HTML>");
   return;
  }

  
  PrintWriter out = resp.getWriter();
  
  ExportReport2 recs = DataManager.getInstance().getExportReport2( nri );

  out.print("<HTML><HEAD><TITLE>Result</TITLE>"
    +"<style>\n"
    +"td, th { border: 1px solid black;  empty-cells: show}\n"
    +"table { border-collapse: collapse; }\n"
    +"th { background-color: #FFFFDD}\n"
    +"</style>"
    +"</HEAD><BODY><H1 style='color: blue'>Result: "+recs.getRecordsCount()+"</H1>");
  
  
  for( IntMap.Entry<ExportReport> me : recs.getResult().entrySet() )
  {
   CollectionShadow ccsh = null;
   
   for( CollectionShadow csh : DataManager.getInstance().getCollections() )
   {
    if( csh.getId() == me.getKey() )
    {
     ccsh=csh;
     break;
    }
   }
   
   out.print("<br>Collection: "+ccsh.getName());
   
   out.print("<TABLE><THEAD><TR><TH>Collection</TH><TH>SAMPLE.ID</TH>");
   
   if( ! idsOnly )
   {

    for(ParameterPart pp : me.getValue().getParameterParts())
    {
     out.print("<TH>");
     out.print(pp.getParameter().getCode());
     out.print(".");

     if(pp.getParameter().getInheritedParameters() == null)
     {
      out.print(pp.getName());
     }
     else
     {
      // pp.getParameter().getAllVariables();
      out.print(pp.getName());
     }

     out.print("</TH>");
    }

   }
   
   out.print("</TR></THEAD><TBODY>");
   
   for( Record row : me.getValue().getRecords() )
   {
    out.print("<TR><TD>"+ccsh.getName()+"</TD><TD>");
    out.print(row.getCollectionRecordIDs());
    out.print("</TD>");

    if( ! idsOnly )
    {
     for(ParameterPart pp : me.getValue().getParameterParts())
     {
      out.print("<TD>");
      PartValue pv = row.getPartValue(pp.getId());

      if(pv != null)
      {
       if(!pp.isEnum())
       {
        if(pv instanceof IntPartValue)
         out.print(((IntPartValue) pv).getIntValue());
        else if(pv instanceof RealPartValue)
         out.print(((RealPartValue) pv).getRealValue());
        else
         out.print(ParameterPart.SECURED_VARIANT_SIGN);
       }
       else
       {
        Variant v = pp.getVariant(((VariantPartValue) pv).getVariant());
        out.print(v.getName());
       }
      }

      out.print("</TD>");
     }
    }
    
    out.print("</TR>");
   }

   out.print("</TBODY></TABLE>");
   
  }
  


  resp.getWriter().print("</BODY></HTML>");
 }
}
