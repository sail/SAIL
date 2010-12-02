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

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.LinkClickListener;
import uk.ac.ebi.sail.client.LinkManager;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Summary;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StudyInfoPanel2 extends AbstractInfoPanel
{

 public StudyInfoPanel2( final Study study)
 {
  LinkManager.getInstance().addLinkClickListener("testLink", new LinkClickListener()
  {
   
   @Override
   public void linkClicked(String param)
   {
    System.out.println("Link clicked. Param: "+param);
   }
  });
  
//  setHtml("<div>Text<br><a href='javascript:window.linkClicked(\"testLink\",\"mike\")'>Link</a><br>MoreText");
  
  setCls("infoPanel");
  setFrame(false);
  setBorder(false);
  setAutoScroll(false);
  setAutoWidth(true);
//  setLayout(new AnchorLayout());
  
  DataManager.getInstance().getStudySummary(study.getId(), new AsyncCallback<Summary>()
  {
   
   @Override
   public void onSuccess(Summary result)
   {
    final StringBuilder html = new StringBuilder();
    
    Summary studySummary = result.getTagCounters()[0];
    Summary[] tagCounters = studySummary.getTagCounters();
    
    drawBoxHeader(html, "studyInfo", "<span style='font-size: 20pt'>"+study.getName()+"</span>");

    drawCollectionSummary(html, "studyTotalInfo", studySummary, study.getUpdateTime());

    html.append("<hr>");
    
    drawAnnotations(html, "studyAnnot", study.getAnnotations());

    Summary[] selection = result.getTagCounters();

    drawBoxHeader(html, "studySamples", "<b>Sample subsets</b>");
    
    html.append("<div style='overflow: auto; padding: 1px'><table class='studyInfoCollectionTbl'><thead><tr><th class='studyInfoCollectionHdr'><b>Collection</b></th>" +
     "<th class='studyInfoRecHdr'>Records</th>");

     if( tagCounters != null )
     {
      for( Summary trc : tagCounters )
      {
       Parameter p = DataManager.getInstance().getParameter( trc.getId() );
       html.append("<th class='studyInfoTagRecHdr'>").append(p.getName()).append("</th>"); 
      }
     }

     html.append("</tr></thead><tbody>");

     String[] subsNames = new String[]{"Eligible samples","Used samples"};
     
     for( int j=1; j < selection.length; j++ )
     {
      html.append("<tr><td class='studyInfoCollectionName'><b><a href='javascript:linkClicked(\"collectionInfo\",\"").append(selection[j].getId()).append("\")'>")
      .append(subsNames[j-1]).append("</a></b></td><td class='studyInfoRecCount'>").append(
        selection[j].getCount()).append("</td>");

      if(tagCounters != null)
      {
       for(Summary trc : tagCounters)
       {
        int count = 0;

        if(selection[j].getTagCounters() != null)
        {
         for(Summary ptc : selection[j].getTagCounters())
         {
          if(ptc.getId() == trc.getId())
          {
           count = ptc.getCount();
           break;
          }
         }
        }

        html.append("<td class='studyInfoTagCount'>").append(count).append("</td>");
       }
      }

      html.append("</tr>");
     }
     
     html.append("</tbody></table></div>");

     drawBoxFooter(html, "studySamples");

     html.append("<hr>");

    
    
    Summary[] collections = result.getRelatedCounters();
    if( collections!= null && collections.length > 0 )
    {
     drawBoxHeader(html, "studyCollections", "<b>Collections</b>");
     
     html.append("<div style='overflow: auto; padding: 1px'><table class='studyInfoCollectionTbl'><thead><tr><th class='studyInfoCollectionHdr'><b>Collection</b></th>" +
     "<th class='studyInfoRecHdr'>Records</th>");

     if( tagCounters != null )
     {
      for( Summary trc : tagCounters )
      {
       Parameter p = DataManager.getInstance().getParameter( trc.getId() );
       html.append("<th class='studyInfoTagRecHdr'>").append(p.getName()).append("</th>"); 
      }
     }

     html.append("</tr></thead><tbody>");

     for( int j=0; j < collections.length; j++ )
     {
      SampleCollection kh = DataManager.getInstance().getCollection(collections[j].getId());
      html.append("<tr><td class='studyInfoCollectionName'><b><a href='javascript:linkClicked(\"collectionInfo\",\"").append(collections[j].getId()).append("\")'>")
      .append(kh.getName()).append("</a></b></td><td class='studyInfoRecCount'>").append(
        collections[j].getCount()).append("</td>");

      if(tagCounters != null)
      {
       for(Summary trc : tagCounters)
       {
        int count = 0;

        if(collections[j].getTagCounters() != null)
        {
         for(Summary ptc : collections[j].getTagCounters())
         {
          if(ptc.getId() == trc.getId())
          {
           count = ptc.getCount();
           break;
          }
         }
        }

        html.append("<td class='studyInfoTagCount'>").append(count).append("</td>");
       }
      }

      html.append("</tr>");
     }
     
     html.append("</tbody></table></div>");

     drawBoxFooter(html, "studyCollections");

     html.append("<hr>");
    } 
    
    drawParametersSummary(html, "collectionParamInfo", "<table style='width: 100%'><tr><td><b>Measured parameters</b></td>" +
      "<td style='text-align: right'>" +
      "<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='Excel XML export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
      GWT.getModuleBaseURL()+"CollectionSummaryExport?StudyID="
      +study.getId()+"\"; return false;'><img ext:qtitle='Export' ext:qtip='Excel XML export' src='images/icons/page_white_excel.png'/></button>" +
      "&nbsp;<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='CSV export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
      GWT.getModuleBaseURL()+"CollectionSummaryExport?StudyID="
      +study.getId()+"&format=csv\"; return false;'><img ext:qtitle='Export' ext:qtip='CSV export' src='images/icons/table.png'/></button>" +
              "</td></tr></table>", studySummary);


    drawBoxFooter(html, "studyInfo");
    
    setHtml(html.toString());
   }
   
   @Override
   public void onFailure(Throwable caught)
   {
    // TODO Auto-generated method stub
    
   }
  });
  
 }

}
