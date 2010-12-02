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

package uk.ac.ebi.sail.client.ui.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.Panel;

public class CollectionInfoPanel extends Panel
{

 private Summary[] tagCounters;
 
 public CollectionInfoPanel(final SampleCollection collec)
 {
  setCls("infoPanel");
  setFrame(false);
  setBorder(false);
  
  DataManager.getInstance().getCollectionSummary(collec.getId(), new AsyncCallback<Summary>()
  {
   
   @Override
   public void onSuccess(Summary result)
   {
    final StringBuilder html = new StringBuilder();
    
    
    html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed; max-width: 800px'><tr class='roundBoxHeader'><td class='collectionInfo roundBoxLT'></td>"
      +"<td class='collectionInfo' rowspan=2 style='font-size: 20pt'><b>").append(collec.getName());
    html.append("</b></td><td class='collectionInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='collectionInfo'></td><td class='collectionInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='collectionInfo roundBoxL'></td><td style='padding-top: 15px'>");

    html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='collectionTotalInfo roundBoxLT'></td>"
      +"<td class='collectionTotalInfo' rowspan=2 style='font-size: 16pt'><b>Summary info</b>");
    html.append("</td><td class='collectionTotalInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='collectionTotalInfo'></td><td class='collectionTotalInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='collectionTotalInfo roundBoxL'></td><td style='padding-top: 10px; font-size: 14pt'>");

//    html.append("<div style='width: 700px'><span class='summaryTitle'>Collection summary</span><br><span class='summaryName'>")
//      .append(collection.getName()).append("</span>"); 
    html.append("<span class='summaryTotal'>Total samples: ").append(result.getCount()).append("</span>");
    
    tagCounters = result.getTagCounters();
    if( tagCounters != null )
    {
     for( Summary trc : tagCounters )
     {
      Parameter p = DataManager.getInstance().getParameter( trc.getId() );
      html.append("<br><span class='summaryTagged'>Samples with tag '").append(p.getName()).append("': ").append(trc.getCount()).append("</span>"); 
     }
    }
    
    if( collec.getId() > 0 )
     html.append("<br><span class='summaryUpdate'>Last update: ").append(new Date(collec.getUpdateTime()).toLocaleString()).append("</span>");

    html.append("</td><td class='collectionTotalInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='collectionTotalInfo roundBoxLB'></td>" +
      "<td class='collectionTotalInfo roundBoxB'></td><td class='collectionTotalInfo roundBoxRB'></td></tr></table><br>");

    html.append("<hr>");
    
    if( collec.getAnnotations() != null )
    {
     for( Annotation ant : collec.getAnnotations() )
     {
      Tag t = ant.getTag();
      
      String tName = t.getDescription();
      
      if( tName == null || tName.length() == 0 )
       tName = t.getName();
      
      html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='collectionAnnot roundBoxLT'></td>"
        +"<td class='collectionAnnot' rowspan=2 style=''><b>").append(tName);
      html.append("</b></td><td class='collectionAnnot roundBoxRT'></td></tr>" +
            "<tr style='height: 10px'><td class='collectionAnnot'></td><td class='collectionAnnot'></td></tr>"+
            "<tr style='min-height: 70px'><td class='collectionAnnot roundBoxL'></td><td style='padding-top: 15px'>")
            .append(ant.getText()!=null?ant.getText().replaceAll("\\n", "<br>"):"")
            .append("</td><td class='collectionAnnot roundBoxR'></td></tr>" +
            		"<tr class='roundBoxFooter'><td class='collectionAnnot roundBoxLB'></td>"
        +"<td class='collectionAnnot roundBoxB'></td><td class='collectionAnnot roundBoxRB'></td></tr></table><br>");

      
      
     }
     html.append("<hr>");
    }
    
    if( collec.getId() < 0 )
    {
     html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='selectionCollectionInfo roundBoxLT'></td>"
       +"<td class='selectionCollectionInfo' rowspan=2 style='font-size: 16pt'><b>Distribution across collections</b>");
     html.append("</td><td class='selectionCollectionInfo roundBoxRT'></td></tr>" +
           "<tr style='height: 15px'><td class='selectionCollectionInfo'></td><td class='selectionCollectionInfo'></td></tr>"+
           "<tr style='min-height: 70px'><td class='selectionCollectionInfo roundBoxL'></td><td style='padding-top: 10px; font-size: 14pt'>");

     
     html.append("<div style='overflow: auto; padding: 1px'><table class='collectionInfoCollectionTbl'><thead><tr><th class='collectionInfoCollectionHdr'>Parameter</th>" +
             "<th class='collectionInfoRecHdr'>Records</th>");
     
     if( tagCounters != null )
     {
      for( Summary trc : tagCounters )
      {
       Parameter p = DataManager.getInstance().getParameter( trc.getId() );
       html.append("<th class='collectionInfoTagRecHdr'>").append(p.getName()).append("</th>"); 
      }
     }

     html.append("</tr></thead><tbody>");
     
     if( result.getRelatedCounters() != null )
     {
      for( Summary pc : result.getRelatedCounters() )
      {
       if( pc.getId() > 0 )
        continue;
       
       SampleCollection p = DataManager.getInstance().getCollection( -pc.getId() );
       html.append("<tr><td class='collectionInfoCollectionName'><b><a href='javascript:linkClicked(\"collectionInfo\",\"").append(-pc.getId()).append("\")'>")
      .append(p.getName()).append("</a></b></td><td class='collectionInfoRecCount'>")
       .append( pc.getCount()).append("</td>");
       
       if( tagCounters != null )
       {
        for( Summary trc : tagCounters )
        {
         int count=0;
         
         if( pc.getTagCounters() != null )
         {
          for( Summary ptc : pc.getTagCounters() )
          {
           if( ptc.getId() == trc.getId() )
           {
            count = ptc.getCount();
            break;
           }
          }
         }
         
         html.append("<td class='collectionInfoTagCount'>").append(count).append("</td>"); 
        }
       }

       html.append("</tr>");
      }
     }
     
     html.append("</tbody></table></div>");

     html.append("</td><td class='selectionCollectionInfo roundBoxR'></td></tr>" +
       "<tr class='roundBoxFooter'><td class='selectionCollectionInfo roundBoxLB'></td>" +
       "<td class='selectionCollectionInfo roundBoxB'></td><td class='selectionCollectionInfo roundBoxRB'></td></tr></table><br>");

    }
    
    html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='collectionParamInfo roundBoxLT'></td>"
      +"<td class='collectionParamInfo' rowspan=2 style='font-size: 16pt'><table style='width: 100%'><tr><td><b>Measured parameters</b></td>" +
      		"<td style='text-align: right'>" +
      		"<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='Excel XML export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
      		GWT.getModuleBaseURL()+"CollectionSummaryExport?CollectionID="
      		+collec.getId()+"\"; return false;'><img ext:qtitle='Export' ext:qtip='Excel XML export' src='images/icons/page_white_excel.png'/></button>" +
            "&nbsp;<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='CSV export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
            GWT.getModuleBaseURL()+"CollectionSummaryExport?CollectionID="
            +collec.getId()+"&format=csv\"; return false;'><img ext:qtitle='Export' ext:qtip='CSV export' src='images/icons/table.png'/></button>" +
      				"</td></tr></table>");
    html.append("</td><td class='collectionParamInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='collectionParamInfo'></td><td class='collectionParamInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='collectionParamInfo roundBoxL'></td><td style='padding-top: 10px; font-size: 14pt'>");

    
    html.append("<div style='overflow: auto; padding: 1px'><table class='collectionInfoParamTbl'><thead><tr>" +
    		"<th class='collectionInfoParamHdr' rowspan=2>Parameter</th><th colspan=2 class='collectionInfoParamHdrFiller'>&nbsp;</th>" +
            "<th class='collectionInfoRecHdr' rowspan=2>Records</th>");
    
    if( tagCounters != null )
    {
     for( Summary trc : tagCounters )
     {
      Parameter p = DataManager.getInstance().getParameter( trc.getId() );
      html.append("<th class='collectionInfoTagRecHdr' rowspan=2>").append(p.getName()).append("</th>"); 
     }
    }

    html.append("</tr><tr>" +
      "<th class='collectionInfoParamPartHdr'>Variable or<br>Qualifier</th>" +
      "<th class='collectionInfoParamVariHdr'>Variant</th>" +

    		"</tr></thead><tbody>");
    
    if( result.getRelatedCounters() != null )
    {
     class Bind
     {
      Summary summ;
      Parameter p;
     }
     
     Bind[] list = new Bind[result.getRelatedCounters().length];
     
     for(int i=0; i < result.getRelatedCounters().length; i++)
     {
      Summary pc = result.getRelatedCounters()[i];
      
      list[i]=new Bind();
      
      list[i].summ=pc;
      list[i].p = pc.getId() <= 0?null:DataManager.getInstance().getParameter(pc.getId());
     }
     
     Arrays.sort(list, new Comparator<Bind>()
     {
      @Override
      public int compare(Bind arg0, Bind arg1)
      {
       if( arg0.p == null )
        return arg0.p == null?0:-1;
       
       return arg0.p.getName().compareToIgnoreCase(arg1.p.getName());
      }
     });

     
     for(Bind bd : list)
     {
      if(bd.p == null )
       continue;

      Parameter p = bd.p;
      Summary pc = bd.summ;

      int span = 1 + (pc.getRelatedCounters() != null ? pc.getRelatedCounters().length : 0);

      html.append("<tr><td class='collectionInfoParamName' rowspan=").append(span).append("><b>").append(p.getCode()).append("</b><br>(").append(p.getName())
        .append(")</td><td colspan=2 class='collectionInfoParameterFiller'>&nbsp;</td>");
      html.append("<td class='collectionInfoParameterCount'>").append(pc.getCount()).append("</td>");

      drawTagCounters(html, pc, "collectionInfoParameterTagCount");
      
      html.append("</tr>");

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
        
        html.append("<tr><td class='collectionInfoPartName' rowspan=").append(vsms.size()+(undisc!=null?1:0)).append(">").append(pp.getName()).append("</td>");
        
        boolean tail=false;
        
        if( undisc != null )
        {
         tail=true;
         drawRow(html, "[Undisclosed]", undisc, "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
        }
        
        for( Map.Entry<String,Summary> me: vsms.entrySet() )
        {
         if( tail )
          html.append("<tr>");
         else
          tail=true;
         
         drawRow(html, me.getKey(), me.getValue(), "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
        }
       }
      }
      
     }
    }
    
    html.append("</tbody></table></div>");

    html.append("</td><td class='collectionParamInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='collectionParamInfo roundBoxLB'></td>" +
      "<td class='collectionParamInfo roundBoxB'></td><td class='collectionParamInfo roundBoxRB'></td></tr></table><br>");

    
    
    html.append("</td><td class='collectionInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='collectionInfo roundBoxLB'></td>" +
      "<td class='collectionInfo roundBoxB'></td><td class='collectionInfo roundBoxRB'></td></tr></table><br>");
    
    setHtml(html.toString());
   }
   
   @Override
   public void onFailure(Throwable caught)
   {
    ErrorBox.showError("There is a system error:<br>"+caught.getMessage());
   }
  });
  
 }

 private void drawRow(StringBuilder html, String name, Summary vcn, String nameCls, String cntClass, String tagClass)
 {
  html.append("<td class='").append(nameCls).append("'>").append(name).append("</td>");
  
  html.append("<td class='").append(cntClass).append("'>").append(vcn.getCount()).append("</td>");

  drawTagCounters(html, vcn, tagClass);

  html.append("</tr>");
 }
 
 private void drawTagCounters(StringBuilder html, Summary vcn, String tagClass)
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

    html.append("<td class='").append(tagClass).append("'>").append(count).append("</td>");
   }
  }
 }

}
