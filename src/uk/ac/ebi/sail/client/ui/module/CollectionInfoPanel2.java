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
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CollectionInfoPanel2 extends AbstractInfoPanel
{

 public CollectionInfoPanel2(final SampleCollection collec)
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
    
    drawBoxHeader(html, "collectionInfo", "<span style='font-size: 20pt'>"+collec.getName()+"</span>");

    drawCollectionSummary(html, "collectionTotalInfo", result, collec.getId() > 0?collec.getUpdateTime():0);

    html.append("<hr>");
    
    drawAnnotations(html, "collectionAnnot", collec.getAnnotations());
    
    Summary[] tagCounters = result.getTagCounters();
    
    if( collec.getId() < 0 )
    {
     drawBoxHeader(html, "selectionCollectionInfo", "<b>Distribution across collections</b>");
     
     html.append("<div style='overflow: auto; padding: 1px'><table class='collectionInfoCollectionTbl'><thead>" +
     		 "<tr><th class='collectionInfoCollectionHdr'>Collection</th>" +
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

     drawBoxFooter(html, "selectionCollectionInfo");
     
    }
    
    drawParametersSummary(html, "collectionParamInfo", "<table style='width: 100%'><tr><td><b>Measured parameters</b></td>" +
    "<td style='text-align: right'>" +
    "<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='Excel XML export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
    GWT.getModuleBaseURL()+"CollectionSummaryExport?CollectionID="
    +collec.getId()+"\"; return false;'><img ext:qtitle='Export' ext:qtip='Excel XML export' src='images/icons/page_white_excel.png'/></button>" +
    "&nbsp;<button style='width: 24px; padding: 0' ext:qtitle='Export' ext:qtip='CSV export' onClick='window.document.getElementById(\"__sail_downloadTarget\").src=\""+
    GWT.getModuleBaseURL()+"CollectionSummaryExport?CollectionID="
    +collec.getId()+"&format=csv\"; return false;'><img ext:qtitle='Export' ext:qtip='CSV export' src='images/icons/table.png'/></button>" +
            "</td></tr></table>", result);
    

    drawBoxFooter(html, "collectionInfo");
    
    setHtml(html.toString());
   }
   
   @Override
   public void onFailure(Throwable caught)
   {
    ErrorBox.showError("There is a system error:<br>"+caught.getMessage());
   }
  });
  
 }

}
