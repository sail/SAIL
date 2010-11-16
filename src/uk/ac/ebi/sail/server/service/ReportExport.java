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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.server.data.DataManager;

/**
 * Servlet implementation class ReportExport
 */
public class ReportExport extends HttpServlet
{
 private static final long serialVersionUID = 1L;

 private static final String xmlDocHeader =
  "<?xml version=\"1.0\"?>\n" +
  "<?mso-application progid=\"Excel.Sheet\"?>\n" +
  "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" +
  "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
  "xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n" +
  "xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" +
  "xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n" +
  "<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n" +
  " <Author>EMBL-EBI</Author>\n" +
  " <LastAuthor>EMBL-EBI</LastAuthor>\n" +
  " <Created>2009-10-05T11:39:19Z</Created>\n" +
  " <Company>EMBL-EBI</Company>\n" +
  " <Version>12.00</Version>\n" +
  "</DocumentProperties>\n" +
  "<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n" +
  " <ProtectStructure>False</ProtectStructure>\n" +
  " <ProtectWindows>False</ProtectWindows>\n" +
  "</ExcelWorkbook>\n" +
  "<Styles>\n" +
  " <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" +
  "  <Alignment ss:Vertical=\"Bottom\"/>\n" +
  "  <Borders/>\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n" +
  "  <Interior/>\n" +
  "  <NumberFormat/>\n" +
  "  <Protection/>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellCollectionNameHeader\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#9ba9bf\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellCollectionCounterHeader\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#9bb3bf\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellParameterHeader\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#9babbf\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellResultHeader\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#345c8c\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellCollectionName\">\n" +
  "  <Interior ss:Color=\"#bacbe5\" ss:Pattern=\"Solid\"/>\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellSummaryHeader\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#FFFFFF\"/>\n" +
  "  <Interior ss:Color=\"#244061\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellCollectionSummary\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#FFFFFF\"/>\n" +
  "  <Interior ss:Color=\"#244061\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellParameterSummary\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#FFFFFF\"/>\n" +
  "  <Interior ss:Color=\"#244061\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellResultSummary\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#FFFFFF\"/>\n" +
  "  <Interior ss:Color=\"#1c334d\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  "  <Style ss:ID=\"cellCollectionCount\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#d2e3ec\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellParameterCount\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#dde3eb\" ss:Pattern=\"Solid\"/>\n" +
  "  <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "  </Borders>\n" +
  " </Style>\n" +
  " <Style ss:ID=\"cellResultCount\">\n" +
  "  <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"20\" ss:Color=\"#000000\"/>\n" +
  "  <Interior ss:Color=\"#86aedd\" ss:Pattern=\"Solid\"/>\n" +
  "   <Borders>\n" +
  "   <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "   <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"2\"/>\n" +
  "   <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" +
  "  </Borders>\n" +
  "</Style>\n" +
  "</Styles>\n" +
  "<Worksheet ss:Name=\"SAIL\">";
 
 private static final String xmlDocFooter =
  "  </Table>\n" + 
  "  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n" + 
  "   <PageSetup>\n" + 
  "    <Header x:Margin=\"0.3\"/>\n" + 
  "    <Footer x:Margin=\"0.3\"/>\n" + 
  "    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n" + 
  "   </PageSetup>\n" + 
  "   <Selected/>\n" + 
  "   <ProtectObjects>False</ProtectObjects>\n" + 
  "   <ProtectScenarios>False</ProtectScenarios>\n" + 
  "  </WorksheetOptions>\n" + 
  " </Worksheet>\n" + 
  "</Workbook>";
 
 private static int reqCount=1;
 /**
  * @see HttpServlet#HttpServlet()
  */
 public ReportExport()
 {
  super();
 }

 /**
  * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
  *      response)
  */
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
 {
  String reqStr = request.getParameter("request");
  ReportRequest nri = null;
  Summary res = null;
  try
  {
   nri= ReqStr2NReq.convert(reqStr);
   res = DataManager.getInstance().report2(nri);
  }
  catch(Exception e)
  {
   response.getWriter().print("<HTML><HEAD><TITLE>Error</TITLE></HEAD><BODY><H1 style='color: red'>"+e.getMessage()+"</H1></BODY></HTML>");
   return;
  }
  
  String format = request.getParameter("format");
  
  if(format != null && "csv".equals(format) )
   sendPlain(nri, res, response,"csv");
  else
   sendXML(nri, res, response);
 }
 
 protected void sendXML(ReportRequest nri, Summary res, HttpServletResponse response) throws ServletException, IOException
 {
  
  String fileName = "SAIL_result_export_"+(reqCount++)+".xml";
  
  response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
//  response.addHeader("Content-Type", "application/x-force-download; name=\"" + fileName + "\"");
  response.addHeader("Content-Type", "text/xml; name=\"" + fileName + "\"");

  PrintWriter out = response.getWriter();

  out.write(xmlDocHeader);
  
  int paramDim=0;
  
  List<ParameterRequestItem> plst = new ArrayList<ParameterRequestItem>(10);
  createParamList(nri.getRootGroup(), plst);
  
  paramDim = plst.size();
  
  int collectionDim = res.getRelatedCounters().length+2;
  paramDim += 3 ;
  
  out.print("  <Table ss:ExpandedColumnCount=\""+paramDim+"\" ss:ExpandedRowCount=\""+collectionDim+"\" x:FullColumns=\"1\"\n" + 
  		"   x:FullRows=\"1\">\r\n");
  
  out.print(
    "   <Column ss:AutoFitWidth=\"1\" ss:Width=\"153.75\"/>\n" + 
    "   <Column ss:AutoFitWidth=\"1\" ss:Width=\"153.75\"/>\n"
    );
  
  for( int i=3; i < paramDim; i++ )
   out.print("   <Column ss:AutoFitWidth=\"1\" ss:Width=\"100\"/>\n");
  
  out.print("   <Column ss:AutoFitWidth=\"1\" ss:Width=\"80\"/>\n");
  out.print(
        "   <Row ss:AutoFitHeight=\"1\">\r\n" + 
  		"    <Cell ss:StyleID=\"cellCollectionNameHeader\"><Data ss:Type=\"String\">Collections</Data></Cell>\n" + 
  		"    <Cell ss:StyleID=\"cellCollectionCounterHeader\"><Data ss:Type=\"String\">Records in collection</Data></Cell>\n");
  
//  IntList order = new ArrayIntList(paramDim-3);
  
//  for( RequestItem ri : nri.getRootGroup().getItems() )
//  {
//   if( ri instanceof GroupRequestItem )
//   {
//    for( RequestItem sri : ((GroupRequestItem)ri).getItems() )
//     order.add( sri.getId() );
//   }
//   else if( ri instanceof ParameterRequestItem )
//   {
//    order.add( ((ParameterRequestItem)ri).getParameterID() );
//   }
//  }
  
  Iterator<ParameterRequestItem> iit = plst.listIterator();
  
  while( iit.hasNext() )
  {
   out.print("    <Cell ss:StyleID=\"cellParameterHeader\"><Data ss:Type=\"String\">"
     +DataManager.getInstance().getParameter(iit.next().getParameterID()).getCode()+"</Data></Cell>\n");
  }
  
  out.print("    <Cell ss:StyleID=\"cellResultHeader\"><Data ss:Type=\"String\">Result</Data></Cell>\n" + 
  		    "   </Row>\n");
  
  for( Summary csmm : res.getRelatedCounters() )
  {
   if( csmm.getCount() == 0 )
    continue;

   String collectionName = csmm.getId() > 0 ? DataManager.getInstance().getCollection(csmm.getId()).getName():"[All]";
   
   out.print("   <Row ss:AutoFitHeight=\"1\">\n" + 
   		"    <Cell ss:StyleID=\"cellCollectionName\"><Data ss:Type=\"String\">"+collectionName+"</Data></Cell>\n" + 
   		"    <Cell ss:StyleID=\"cellCollectionCount\"><Data ss:Type=\"Number\">"+csmm.getCount()+"</Data></Cell>\n");
   
   iit = plst.listIterator();
   
   while( iit.hasNext() )
   {
    int count = 0 ;
    int pid = iit.next().getId();
    
    if( csmm.getRelatedCounters() != null )
    {
     for( Summary psmm : csmm.getRelatedCounters() )
     {
      if( psmm.getId() == pid )
      {
       count = psmm.getCount();
       break;
      }
     }
    }
    
    out.print("    <Cell ss:StyleID=\"cellParameterCount\"><Data ss:Type=\"Number\">"+count+"</Data></Cell>\n");
   }

   int resCnt = csmm.getRelatedCounters()!= null&&csmm.getRelatedCounters().length>0?csmm.getRelatedCounters()[0].getCount():0;
   out.print("    <Cell ss:StyleID=\"cellResultCount\"><Data ss:Type=\"Number\">"+resCnt+"</Data></Cell>\n" + 
   		"   </Row>\n");

  }
  
  out.print("   <Row ss:AutoFitHeight=\"1\">\r\n" + 
  		"    <Cell ss:StyleID=\"cellSummaryHeader\"><Data ss:Type=\"String\">Summary</Data></Cell>\n" + 
  		"    <Cell ss:StyleID=\"cellCollectionSummary\" ss:Formula=\"=SUM(R1C:R[-1]C)\"><Data ss:Type=\"Number\">-1</Data></Cell>\n");

  for( int i=3; i < paramDim; i++ )
   out.print("    <Cell ss:StyleID=\"cellParameterSummary\" ss:Formula=\"=SUM(R1C:R[-1]C)\"><Data ss:Type=\"Number\">-1</Data></Cell>\n");

  out.print("    <Cell ss:StyleID=\"cellResultSummary\" ss:Formula=\"=SUM(R1C:R[-1]C)\"><Data ss:Type=\"Number\">-1</Data></Cell>\n   </Row>\n");

  out.print(xmlDocFooter);
 }

 private void createParamList( GroupRequestItem grp, List<ParameterRequestItem> lst )
 {
  for( RequestItem ri : grp.getItems() )
  {
   if( ri instanceof GroupRequestItem )
    createParamList((GroupRequestItem)ri, lst);
   else if( ri instanceof ParameterRequestItem )
    lst.add( (ParameterRequestItem)ri );
  }
 }
 
 protected void sendPlain(ReportRequest nri, Summary res, HttpServletResponse response, String type) throws ServletException, IOException
 {
  
  String fileName = "SAIL_result_export_"+(reqCount++)+"."+type;
  
  String sep = "csv".equals(type)?",":"\t";
  
  response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
  response.addHeader("Content-Type", "text/plain; name=\"" + fileName + "\"");

  PrintWriter out = response.getWriter();
  
  out.print("Collection"+sep+"\"Records in collection\"");
  
  
  List<ParameterRequestItem> plst = new ArrayList<ParameterRequestItem>(10);
  createParamList(nri.getRootGroup(), plst);
  
//  IntList order = new ArrayIntList(10);
//  
//  for( RequestItem ri : nri.getRootGroup().getItems() )
//  {
//   if( ri instanceof GroupRequestItem )
//   {
//    for( RequestItem sri : ((GroupRequestItem)ri).getItems() )
//     order.add( sri.getId() );
//   }
//   else if( ri instanceof ParameterRequestItem )
//   {
//    order.add( ((ParameterRequestItem)ri).getParameterID() );
//   }
//  }
  

  int sums[] = new int[plst.size()+2];
  
  Iterator<ParameterRequestItem> iit = plst.listIterator();
  
  
  while( iit.hasNext() )
   out.print(sep+"\"" +DataManager.getInstance().getParameter(iit.next().getParameterID()).getCode()+"\"");
  
  out.print(sep+"Result\n");
  
  for( Summary csmm : res.getRelatedCounters() )
  {
   if( csmm.getCount() == 0 )
    continue;

   String collecName = csmm.getId() > 0 ? DataManager.getInstance().getCollection(csmm.getId()).getName():"[All]";
   
   out.print("\""+collecName+"\"" +sep+csmm.getCount());
   
   sums[0]+=csmm.getCount();

   
   int i=2;
   iit = plst.listIterator();
   while( iit.hasNext() )
   {
    int count = 0 ;
    int pid = iit.next().getId();
    
    if( csmm.getRelatedCounters() != null )
    {
     for( Summary psmm : csmm.getRelatedCounters() )
     {
      if( psmm.getId() == pid )
      {
       count = psmm.getCount();
       break;
      }
     }
    }
    
    out.print(sep+count);
    sums[i++]+=count;
   }

   int resCnt = csmm.getRelatedCounters()!= null&&csmm.getRelatedCounters().length>0?csmm.getRelatedCounters()[0].getCount():0;
   out.print(sep+resCnt+"\n");
   sums[1]+=resCnt;
  }
  
  out.print("Summary" + sep + sums[0]);

  for( int i=2; i < sums.length; i++ )
   out.print(sep+sums[i]);

  out.print(sep+sums[1]);

 }

}
