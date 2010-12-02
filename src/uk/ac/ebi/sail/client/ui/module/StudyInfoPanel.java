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
import java.util.Comparator;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.LinkClickListener;
import uk.ac.ebi.sail.client.LinkManager;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.Panel;

public class StudyInfoPanel extends Panel
{

 public StudyInfoPanel( final Study study)
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
    
    html.append("<div><table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='studyInfo roundBoxLT'></td>"
      +"<td class='studyInfo' rowspan=2 style='min-width: 300px; font-size: 20pt'><b>").append(study.getName());
    html.append("</b></td><td class='studyInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='studyInfo'></td><td class='studyInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='studyInfo roundBoxL'></td><td style='padding-top: 15px'>");

    html.append("<table style='border-collapse: collapse; width: 100%'><tr class='roundBoxHeader'><td class='studyTotalInfo roundBoxLT'></td>"
      +"<td class='studyTotalInfo' rowspan=2 style='min-width: 300px; font-size: 16pt'><b>Summary info</b>");
    html.append("</td><td class='studyTotalInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='studyTotalInfo'></td><td class='studyTotalInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='studyTotalInfo roundBoxL'></td><td style='padding-top: 10px; font-size: 14pt'>");

//    html.append("<div style='width: 700px'><span class='summaryTitle'>study summary</span><br><span class='summaryName'>")
//      .append(study.getName()).append("</span>"); 
    html.append("<span class='summaryTotal'>Total samples: ").append(studySummary.getCount()).append("</span>");
    
    Summary[] tagCounters = studySummary.getTagCounters();
    if( tagCounters != null )
    {
     for( Summary trc : tagCounters )
     {
      Parameter p = DataManager.getInstance().getParameter( trc.getId() );
      html.append("<br><span class='summaryTagged'>Samples with tag '").append(p.getName()).append("': ").append(trc.getCount()).append("</span>"); 
     }
    }
    

    html.append("</td><td class='studyTotalInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='studyTotalInfo roundBoxLB'></td>" +
      "<td class='studyTotalInfo roundBoxB'></td><td class='studyTotalInfo roundBoxRB'></td></tr></table><br>");

    html.append("<hr>");
    
    if( study.getAnnotations() != null )
    {
     for( Annotation ant : study.getAnnotations() )
     {
      Tag t = ant.getTag();
      
      String tName = t.getDescription();
      
      if( tName == null || tName.length() == 0 )
       tName = t.getName();
      
      html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='studyAnnot roundBoxLT'></td>"
        +"<td class='studyAnnot' rowspan=2 style='min-width: 300px'><b>").append(tName);
      html.append("</b></td><td class='studyAnnot roundBoxRT'></td></tr>" +
            "<tr style='height: 10px'><td class='studyAnnot'></td><td class='studyAnnot'></td></tr>"+
            "<tr style='min-height: 70px'><td class='studyAnnot roundBoxL'></td><td style='padding-top: 15px'>")
            .append(ant.getText()!=null?ant.getText().replaceAll("\\n", "<br>"):"")
            .append("</td><td class='studyAnnot roundBoxR'></td></tr>" +
                    "<tr class='roundBoxFooter'><td class='studyAnnot roundBoxLB'></td>"
        +"<td class='studyAnnot roundBoxB'></td><td class='studyAnnot roundBoxRB'></td></tr></table><br>");

      
      
     }
     html.append("<hr>");
    }
    
    
    
    Summary[] selection = result.getTagCounters();

     html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='studySamples roundBoxLT'></td>"
       +"<td class='studySamples' rowspan=2 style='min-width: 300px; font-size: 16pt'><b>Sample subsets</b></td><td class='studySamples roundBoxRT'></td></tr>" +
           "<tr style='height: 10px'><td class='studySamples'></td><td class='studySamples'></td></tr>"+
           "<tr style='min-height: 70px'><td class='studySamples roundBoxL'></td><td style='padding-top: 15px; font-size: 14pt'>");
     
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
//      Collection kh = DataManager.getInstance().getCollection(selection[j].getId());
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

     html.append("</td><td class='studySamples roundBoxR'></td></tr>" +
                   "<tr class='roundBoxFooter'><td class='studySamples roundBoxLB'></td>"
       +"<td class='studySamples roundBoxB'></td><td class='studySamples roundBoxRB'></td></tr></table><br>");

     html.append("<hr>");

    
    
    Summary[] collections = result.getRelatedCounters();
    if( collections!= null && collections.length > 0 )
    {

     html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='studyCollections roundBoxLT'></td>"
       +"<td class='studyCollections' rowspan=2 style='min-width: 300px; font-size: 16pt'><b>Collections</b></td><td class='studyCollections roundBoxRT'></td></tr>" +
           "<tr style='height: 10px'><td class='studyCollections'></td><td class='studyCollections'></td></tr>"+
           "<tr style='min-height: 70px'><td class='studyCollections roundBoxL'></td><td style='padding-top: 15px; font-size: 14pt'>");
     
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

     html.append("</td><td class='studyCollections roundBoxR'></td></tr>" +
                   "<tr class='roundBoxFooter'><td class='studyCollections roundBoxLB'></td>"
       +"<td class='studyCollections roundBoxB'></td><td class='studyCollections roundBoxRB'></td></tr></table><br>");

     html.append("<hr>");
    } 
    
    html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed'><tr class='roundBoxHeader'><td class='studyParamInfo roundBoxLT'></td>"
      +"<td class='studyParamInfo' rowspan=2 style='font-size: 16pt'><b>Measured parameters</b>");
    html.append("</td><td class='studyParamInfo roundBoxRT'></td></tr>" +
          "<tr style='height: 15px'><td class='studyParamInfo'></td><td class='studyParamInfo'></td></tr>"+
          "<tr style='min-height: 70px'><td class='studyParamInfo roundBoxL'></td><td style='padding-top: 10px; font-size: 14pt;'>");

    
    html.append("<div style='overflow: auto; padding: 1px'><table class='studyInfoParamTbl'><thead><tr><th class='studyInfoParamHdr'>Parameter</th>" +
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
    
    if( studySummary.getRelatedCounters() != null )
    {
     class Bind
     {
      Summary summ;
      Parameter p;
     }
     
     Bind[] list = new Bind[studySummary.getRelatedCounters().length];
     
     for(int i=0; i < studySummary.getRelatedCounters().length; i++)
     {
      Summary pc = studySummary.getRelatedCounters()[i];
      
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

      html.append("<tr><td class='studyInfoParamName'><b>").append(p.getCode()).append("</b><br>(").append(p.getName()).append(")</td><td class='studyInfoRecCount'>")
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
        
        html.append("<td class='studyInfoTagCount'>").append(count).append("</td>"); 
       }
      }

      html.append("</tr>");
     }
    }
    
    html.append("</tbody></table></div>");

    html.append("</td><td class='studyParamInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='studyParamInfo roundBoxLB'></td>" +
      "<td class='studyParamInfo roundBoxB'></td><td class='studyParamInfo roundBoxRB'></td></tr></table><br>");
    
    html.append("</td><td class='studyInfo roundBoxR'></td></tr>" +
      "<tr class='roundBoxFooter'><td class='studyInfo roundBoxLB'></td>" +
      "<td class='studyInfo roundBoxB'></td><td class='studyInfo roundBoxRB'></td></tr></table></div>");
    
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
