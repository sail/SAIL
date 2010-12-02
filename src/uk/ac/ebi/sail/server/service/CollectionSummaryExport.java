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
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.server.data.DataManager;

import com.pri.util.StringUtils;

/**
 * Servlet implementation class CollectionSummaryExport
 */
public class CollectionSummaryExport extends HttpServlet
{
 private static final long serialVersionUID = 1L;
 
 private static int reqCount=1;

 private static String xmlDocHeader=
 "<?xml version=\"1.0\"?>\n" + 
 "<?mso-application progid=\"Excel.Sheet\"?>\n" + 
 "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + 
 " xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" + 
 " xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n" + 
 " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n" + 
 " xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n" + 
 " <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n" + 
 "  <Author>EMBL-EBI</Author>\n" + 
 "  <LastAuthor>EMBL-EBI</LastAuthor>\n" + 
 "  <Created>2009-10-06T09:37:52Z</Created>\n" + 
 "  <Company>EMBL-EBI</Company>\n" + 
 "  <Version>12.00</Version>\n" + 
 " </DocumentProperties>\n" + 
 " <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n" + 
 "  <WindowHeight>12210</WindowHeight>\n" + 
 "  <WindowWidth>24735</WindowWidth>\n" + 
 "  <WindowTopX>240</WindowTopX>\n" + 
 "  <WindowTopY>120</WindowTopY>\n" + 
 "  <RefModeR1C1/>\n" + 
 "  <ProtectStructure>False</ProtectStructure>\n" + 
 "  <ProtectWindows>False</ProtectWindows>\n" + 
 " </ExcelWorkbook>\n" + 
 " <Styles>\n" + 
 "  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" + 
 "   <Alignment ss:Vertical=\"Bottom\"/>\n" + 
 "   <Borders/>\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n" + 
 "   <Interior/>\n" + 
 "   <NumberFormat/>\n" + 
 "   <Protection/>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParamHdr\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"18\" ss:Color=\"#000000\"/>\n" + 
 "   <Alignment ss:Vertical=\"Center\" ss:Horizontal=\"Center\"/>\n" + 
 "   <Interior ss:Color=\"#4b91c2\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParamName\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"12\" ss:Color=\"#000000\"/>\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\" ss:WrapText=\"1\"/>\n" + 
 "   <Interior ss:Color=\"#c3e1f6\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParamPartHdr\">\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\" ss:WrapText=\"1\"/>\n" + 
 "   <Interior ss:Color=\"#8ecbe7\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParameterFiller\">\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>\n" + 
 "   <Interior ss:Color=\"#c3e1f6\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParameterCount\">\n" + 
 "   <Interior ss:Color=\"#d4edfe\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"16\" ss:Color=\"#000000\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParameterTagCount\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"16\" ss:Color=\"#000000\"/>\n" + 
 "   <Interior ss:Color=\"#d4edfe\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoVariantName\">\n" + 
 "   <Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>\n" + 
 "   <Interior ss:Color=\"#c5def0\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoVariantRecCount\">\n" + 
 "   <Interior ss:Color=\"#e8eff9\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoVariantTagCount\">\n" + 
 "   <Interior ss:Color=\"#e8eff9\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoPartName\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"13\" ss:Color=\"#000000\"/>\n" + 
 "   <Interior ss:Color=\"#b5d4e9\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Alignment ss:Vertical=\"Center\" ss:WrapText=\"1\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParamVariHdr\">\n" + 
 "   <Interior ss:Color=\"#9bd4ee\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoParamHdrFiller\">\n" + 
 "   <Interior ss:Color=\"#4b91c2\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoRecHdr\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"18\" ss:Color=\"#000000\"/>\n" + 
 "   <Interior ss:Color=\"#4b7bc2\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "  <Style ss:ID=\"collectionInfoTagRecHdr\">\n" + 
 "   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"18\" ss:Color=\"#000000\"/>\n" + 
 "   <Interior ss:Color=\"#4b7bc2\" ss:Pattern=\"Solid\"/>\n" + 
 "   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n" + 
 "   <Borders>\n" + 
 "    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n" + 
 "   </Borders>\n" + 
 "  </Style>\n" + 
 "</Styles>\n" + 
 "<Worksheet ss:Name=\"SAIL\">\n"; 
 
 private static String xmlDocFooter=
 "  </Table>\n" + 
 "  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">\n" + 
 "   <PageSetup>\n" + 
 "    <Header x:Margin=\"0.3\"/>\n" + 
 "    <Footer x:Margin=\"0.3\"/>\n" + 
 "    <PageMargins x:Bottom=\"0.75\" x:Left=\"0.7\" x:Right=\"0.7\" x:Top=\"0.75\"/>\n" + 
 "   </PageSetup>\n" + 
 "   <Print>\n" + 
 "    <ValidPrinterInfo/>\n" + 
 "    <PaperSizeIndex>9</PaperSizeIndex>\n" + 
 "    <HorizontalResolution>600</HorizontalResolution>\n" + 
 "    <VerticalResolution>600</VerticalResolution>\n" + 
 "   </Print>\n" + 
 "   <Selected/>\n" + 
 "   <Panes>\n" + 
 "    <Pane>\n" + 
 "     <Number>3</Number>\n" + 
 "     <ActiveRow>8</ActiveRow>\n" + 
 "     <ActiveCol>8</ActiveCol>\n" + 
 "    </Pane>\n" + 
 "   </Panes>\n" + 
 "   <ProtectObjects>False</ProtectObjects>\n" + 
 "   <ProtectScenarios>False</ProtectScenarios>\n" + 
 "  </WorksheetOptions>\n" + 
 " </Worksheet>\n" + 
 "</Workbook>\n";

 /**
  * @see HttpServlet#HttpServlet()
  */
 public CollectionSummaryExport()
 {
 }

 /**
  * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
  *      response)
  */
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
 {
  Summary res = null;
  
  try
  {
   int collID=0;
   
   if( request.getParameter("CollectionID") != null )
   {
    collID = Integer.parseInt( request.getParameter("CollectionID") );
    res = DataManager.getInstance().getCollectionSummary(collID);
   }
   else if( request.getParameter("StudyID") != null )
   {
    collID = Integer.parseInt( request.getParameter("StudyID") );
    res = DataManager.getInstance().getStudySummary(collID).getTagCounters()[0];
   }
  
  }
  catch (Exception e) 
  {
   response.getWriter().print("<HTML><HEAD><TITLE>Error</TITLE></HEAD><BODY><H1 style='color: red'>"+e.getMessage()+"</H1></BODY></HTML>");
   return;
  }
  
  if( res == null )
   return;
  
  
  String format = request.getParameter("format");
  
  if(format != null && "csv".equals(format) )
   sendPlain(res,response,"csv");
  else
   sendXML(res,response);
 }

 protected void sendPlain( Summary res, HttpServletResponse response, String type) throws ServletException, IOException
 {
  String fileName = "SAIL_collection_export_"+(reqCount++)+"."+type;
  
  String sep = "csv".equals(type)?",":"\t";
  
  response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
  response.addHeader("Content-Type", "text/plain; name=\"" + fileName + "\"");

  PrintWriter out = response.getWriter();

  if( res.getRelatedCounters() != null )
  {
   for(Summary pc : res.getRelatedCounters())
   {
    if(pc.getId() <= 0)
     continue;

    Parameter p = DataManager.getInstance().getParameter(pc.getId());

    out.print("\""+p.getCode()+" ("+p.getName()+")\""+sep+sep+sep+pc.getCount());

    drawTagCountersPlain(out, pc, res.getTagCounters(), sep);
    
    out.print("\n");
    
    if(pc.getRelatedCounters() == null)
     continue;

    Collection< ? extends ParameterPart> parts = null;
    Map<String,Summary> vsms = new TreeMap<String,Summary>();
    for(int z = 0; z < 2; z++)
    {
     parts = z == 0 ? p.getAllVariables() : p.getAllQualifiers();

     if(parts == null)
      continue;

     for(ParameterPart pp : parts)
     {
      if( ! pp.isEnum() )
       continue;

      Summary undisc=null;
      
      vsms.clear();
      
      for(Summary vcn : pc.getRelatedCounters())
      {
       if( vcn.getId() == -pp.getId() )
       {
        undisc = vcn;
        break;
       }
      }
      
      
      if( pp.getVariants() != null )
      {
       for( Variant v: pp.getVariants() )
       {
        for(Summary vcn : pc.getRelatedCounters())
        {
         if( vcn.getId() == v.getId() )
         {
          vsms.put(v.getName(), vcn);
          break;
         }
        }
       }
      }
      
      if( undisc == null && vsms.size() == 0 )
       continue;
      
      if( undisc != null )
       drawRowPlain(out, sep+"\""+pp.getName()+"\","+"[Undisclosed]", undisc, res.getTagCounters(), sep);
      
      for( Map.Entry<String,Summary> me: vsms.entrySet() )
       drawRowPlain(out, sep+"\""+pp.getName()+"\","+me.getKey(), me.getValue(), res.getTagCounters(), sep);
     }
    }
    
   }
  }

 }
 
 protected void sendXML( Summary res, HttpServletResponse response) throws ServletException, IOException
 {
  String fileName = "SAIL_collection_export_"+(reqCount++)+".xml";
  
  
  int rows = 2;

  if( res.getRelatedCounters() != null )
  {
   for(Summary pc : res.getRelatedCounters())
   {
    if(pc.getId() <= 0)
     continue;

    rows += 1 + (pc.getRelatedCounters() != null ? pc.getRelatedCounters().length : 0);
   }
  }
  
  response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
  response.addHeader("Content-Type", "text/xml; name=\"" + fileName + "\"");

  PrintWriter out = response.getWriter();

  out.write(xmlDocHeader);
  
  int tagCount = res.getTagCounters() != null?res.getTagCounters().length:0;
  
  out.print("  <Table ss:ExpandedColumnCount=\""+(4+tagCount)+"\" ss:ExpandedRowCount=\""+rows+"\" x:FullColumns=\"1\" x:FullRows=\"1\">\n");
  out.print(
        "   <Column ss:AutoFitWidth=\"0\" ss:Width=\"115.5\"/>\n" + 
  		"   <Column ss:AutoFitWidth=\"0\" ss:Width=\"65\"/>\n" + 
  		"   <Column ss:Index=\"4\" ss:AutoFitWidth=\"0\" ss:Width=\"67.5\"/>\n");
  
  for( int k=0; k < tagCount; k++ )
   out.print(
        "   <Column ss:AutoFitWidth=\"0\" ss:Width=\"99.75\"/>\n");
  
  out.print(
  		"   <Row>\n" + 
  		"    <Cell ss:MergeDown=\"1\" ss:StyleID=\"collectionInfoParamHdr\"><Data ss:Type=\"String\">Parameter</Data></Cell>\n" + 
  		"    <Cell ss:MergeAcross=\"1\" ss:StyleID=\"collectionInfoParamHdrFiller\"/>\n" + 
  		"    <Cell ss:MergeDown=\"1\" ss:StyleID=\"collectionInfoRecHdr\"><Data ss:Type=\"String\">Records</Data></Cell>\n");
        
  if( tagCount > 0  )
  {
   for( Summary trc : res.getTagCounters() )
   {
    Parameter p = DataManager.getInstance().getParameter( trc.getId() );
    out.print(
        "    <Cell ss:MergeDown=\"1\" ss:StyleID=\"collectionInfoTagRecHdr\"><Data ss:Type=\"String\">"+p.getName()+"</Data></Cell>\n");
   }
  }
  
  out.print(
  		"   </Row>\n" + 
  		"   <Row ss:AutoFitHeight=\"1\" ss:Height=\"32\">\n" + 
  		"    <Cell ss:Index=\"2\" ss:StyleID=\"collectionInfoParamPartHdr\"><Data ss:Type=\"String\">Variable or qualifier</Data></Cell>\n" + 
  		"    <Cell ss:StyleID=\"collectionInfoParamVariHdr\"><Data ss:Type=\"String\">Variant</Data></Cell>\n" + 
  		"   </Row>\n"
  		);

  if( res.getRelatedCounters() != null )
  {
   for(Summary pc : res.getRelatedCounters())
   {
    if(pc.getId() <= 0)
     continue;

    Parameter p = DataManager.getInstance().getParameter(pc.getId());

    int span = pc.getRelatedCounters() != null ? pc.getRelatedCounters().length : 0;

    out.print(
      "   <Row ss:Height=\"19.5\">\n" + 
      "    <Cell "+(span>0?"ss:MergeDown=\""+span+"\"":"")+" ss:StyleID=\"collectionInfoParamName\"><Data ss:Type=\"String\">" +
      StringUtils.htmlEscaped(p.getCode()+" ("+p.getName()+")")+"</Data></Cell>\n" + 
      "    <Cell ss:MergeAcross=\"1\" ss:StyleID=\"collectionInfoParameterFiller\"/>\n" + 
      "    <Cell ss:StyleID=\"collectionInfoParameterCount\"><Data ss:Type=\"Number\">"+pc.getCount()+"</Data></Cell>\n" );

    drawTagCounters(out, pc, res.getTagCounters(), "collectionInfoParameterTagCount");
    
    out.print("   </Row>\n");

    if(pc.getRelatedCounters() == null)
     continue;

    Collection< ? extends ParameterPart> parts = null;
    Map<String,Summary> vsms = new TreeMap<String,Summary>();
    for(int z = 0; z < 2; z++)
    {
     parts = z == 0 ? p.getAllVariables() : p.getAllQualifiers();

     if(parts == null)
      continue;

     for(ParameterPart pp : parts)
     {
      if( ! pp.isEnum() )
       continue;

      Summary undisc=null;
      
      vsms.clear();
      
      for(Summary vcn : pc.getRelatedCounters())
      {
       if( vcn.getId() == -pp.getId() )
       {
        undisc = vcn;
        break;
       }
      }
      
      
      if( pp.getVariants() != null )
      {
       for( Variant v: pp.getVariants() )
       {
        for(Summary vcn : pc.getRelatedCounters())
        {
         if( vcn.getId() == v.getId() )
         {
          vsms.put(v.getName(), vcn);
          break;
         }
        }
       }
      }
      
      if( undisc == null && vsms.size() == 0 )
       continue;
      
      int mergeDown = vsms.size()+(undisc!=null?1:0)-1;
      
      out.print(
        "   <Row>\n" + 
        "    <Cell ss:Index=\"2\" "+(mergeDown>0?"ss:MergeDown=\""+mergeDown+"\"":"")+" ss:StyleID=\"collectionInfoPartName\"><Data ss:Type=\"String\">"
        +StringUtils.htmlEscaped(pp.getName())+"</Data></Cell>\n"
        );
      
      boolean tail=false;
      
      if( undisc != null )
      {
       tail=true;
       drawRow(out, "[Undisclosed]", undisc, res.getTagCounters(), 0, "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
      }
      
      for( Map.Entry<String,Summary> me: vsms.entrySet() )
      {
       if( tail )
        out.print("   <Row>\n");
       else
        tail=true;
       
       drawRow(out, me.getKey(), me.getValue(), res.getTagCounters(), 3, "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
      }
     }
    }
    
   }
  }

  
  out.write(xmlDocFooter);

 }
 
 private void drawTagCounters(PrintWriter out, Summary vcn, Summary[] tagCounters, String tagClass)
 {
  if(tagCounters != null)
  {
   for(Summary trc : tagCounters)
   {
    int count = 0;

    if(vcn.getTagCounters() != null)
    {
     for(Summary ptc : vcn.getTagCounters())
     {
      if(ptc.getId() == trc.getId())
      {
       count = ptc.getCount();
       break;
      }
     }
    }

    out.print("    <Cell ss:StyleID=\""+tagClass+"\"><Data ss:Type=\"Number\">"+count+"</Data></Cell>\n");
   }
  }
 }

 private void drawTagCountersPlain(PrintWriter out, Summary vcn, Summary[] tagCounters, String sep)
 {
  if(tagCounters != null)
  {
   for(Summary trc : tagCounters)
   {
    int count = 0;

    if(vcn.getTagCounters() != null)
    {
     for(Summary ptc : vcn.getTagCounters())
     {
      if(ptc.getId() == trc.getId())
      {
       count = ptc.getCount();
       break;
      }
     }
    }

    out.print(sep+count);
   }
  }
 }
 
 private void drawRow(PrintWriter out, String name, Summary vcn, Summary[] tagCounters, int index, String nameCls, String cntClass, String tagClass)
 {
  out.print(
    "    <Cell "+(index>0?"ss:Index=\""+index+"\" ":"")+"ss:StyleID=\"" + nameCls + "\"><Data ss:Type=\"String\">" + StringUtils.htmlEscaped(name) + "</Data></Cell>\n"
  + "    <Cell ss:StyleID=\"" + cntClass + "\"><Data ss:Type=\"Number\">" + vcn.getCount() + "</Data></Cell>\n");

  drawTagCounters(out, vcn, tagCounters, tagClass);

  out.print("   </Row>\n");
 }

 private void drawRowPlain(PrintWriter out, String name, Summary vcn, Summary[] tagCounters, String sep)
 {
 
  out.print(name+sep+vcn.getCount());

  drawTagCountersPlain(out, vcn, tagCounters, sep);

  out.print("\n");
 }

}
