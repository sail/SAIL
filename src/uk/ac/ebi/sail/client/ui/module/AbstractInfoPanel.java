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
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variant;

import com.gwtext.client.widgets.Panel;

public abstract class AbstractInfoPanel extends Panel
{

// private Summary[] tagCounters;
 
// protected void setTagCounters( Summary[] tc )
// {
//  tagCounters=tc;
// }

 protected void drawCollectionSummary( StringBuilder html, String colorClass, Summary result, long lastUpdate )
 {
  drawBoxHeader(html, colorClass, "<b>Summary info</b>");

  
  html.append("<span class='summaryTotal'>Total samples: ").append(result.getCount()).append("</span>");
  
  Summary[] tagCounters = result.getTagCounters();
  if( tagCounters != null )
  {
   for( Summary trc : tagCounters )
   {
    Parameter p = DataManager.getInstance().getParameter( trc.getId() );
    html.append("<br><span class='summaryTagged'>Samples with tag '").append(p.getName()).append("': ").append(trc.getCount()).append("</span>"); 
   }
  }
  
  if( lastUpdate > 0 )
   html.append("<br><span class='summaryUpdate'>Last update: ").append(new Date(lastUpdate).toLocaleString()).append("</span>");

  drawBoxFooter(html, colorClass);
 }
 
 protected void drawParametersSummary( StringBuilder html, String colorClass, String title, Summary result )
 {
  drawBoxHeader(html, colorClass, title);

  html.append("<div style='overflow: auto; padding: 1px'><table class='collectionInfoParamTbl'><thead><tr>"
    + "<th class='collectionInfoParamHdr' rowspan=2>Parameter</th><th colspan=2 class='collectionInfoParamHdrFiller'>&nbsp;</th>"
    + "<th class='collectionInfoRecHdr' rowspan=2>Records</th>");

  Summary[] tagCounters = result.getTagCounters();
  
  if(tagCounters != null)
  {
   for(Summary trc : tagCounters)
   {
    Parameter p = DataManager.getInstance().getParameter(trc.getId());
    html.append("<th class='collectionInfoTagRecHdr' rowspan=2>").append(p.getName()).append("</th>");
   }
  }

  html.append("</tr><tr>" + "<th class='collectionInfoParamPartHdr'>Variable or<br>Qualifier</th>" + "<th class='collectionInfoParamVariHdr'>Variant</th>"
    +"</tr></thead><tbody>");

  if(result.getRelatedCounters() != null)
  {
   class Bind
   {
    Summary   summ;
    Parameter p;
   }

   Bind[] list = new Bind[result.getRelatedCounters().length];

   for(int i = 0; i < result.getRelatedCounters().length; i++)
   {
    Summary pc = result.getRelatedCounters()[i];

    list[i] = new Bind();

    list[i].summ = pc;
    list[i].p = pc.getId() <= 0 ? null : DataManager.getInstance().getParameter(pc.getId());
   }

   Arrays.sort(list, new Comparator<Bind>()
   {
    @Override
    public int compare(Bind arg0, Bind arg1)
    {
     if(arg0.p == null)
      return arg0.p == null ? 0 : -1;

     return arg0.p.getCode().compareToIgnoreCase(arg1.p.getCode());
    }
   });

   for(Bind bd : list)
   {
    if(bd.p == null)
     continue;

    Parameter p = bd.p;
    Summary pc = bd.summ;

    int span = 1 + (pc.getRelatedCounters() != null ? pc.getRelatedCounters().length : 0);

    html.append("<tr><td class='collectionInfoParamName' rowspan=").append(span).append("><b>").append(p.getCode()).append("</b><br>(")
      .append(p.getName()).append(")</td><td colspan=2 class='collectionInfoParameterFiller'>&nbsp;</td>");
    html.append("<td class='collectionInfoParameterCount'>").append(pc.getCount()).append("</td>");

    drawTagCounters(html, pc, tagCounters, "collectionInfoParameterTagCount");

    html.append("</tr>");

    if(pc.getRelatedCounters() == null)
     continue;

    Collection< ? extends ParameterPart> parts = null;
    Map<String, Summary> vsms = new TreeMap<String, Summary>();
    for(int z = 0; z < 2; z++)
    {
     parts = z == 0 ? p.getAllVariables() : p.getAllQualifiers();

     if(parts == null)
      continue;

     for(ParameterPart pp : parts)
     {
      if(!pp.isEnum())
       continue;

      Summary undisc = null;

      vsms.clear();

      for(Summary vcn : pc.getRelatedCounters())
      {
       if(vcn.getId() == -pp.getId())
       {
        undisc = vcn;
        break;
       }
      }

      if(pp.getVariants() != null)
      {
       for(Variant v : pp.getVariants())
       {
        for(Summary vcn : pc.getRelatedCounters())
        {
         if(vcn.getId() == v.getId())
         {
          vsms.put(v.getName(), vcn);
          break;
         }
        }
       }
      }

      if(undisc == null && vsms.size() == 0)
       continue;

      html.append("<tr><td class='collectionInfoPartName' rowspan=").append(vsms.size() + (undisc != null ? 1 : 0)).append(">").append(pp.getName())
        .append("</td>");

      boolean tail = false;

      if(undisc != null)
      {
       tail = true;
       drawRow(html, "[Undisclosed]", undisc, tagCounters, "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
      }

      for(Map.Entry<String, Summary> me : vsms.entrySet())
      {
       if(tail)
        html.append("<tr>");
       else
        tail = true;

       drawRow(html, me.getKey(), me.getValue(), tagCounters, "collectionInfoVariantName", "collectionInfoVariantRecCount", "collectionInfoVariantTagCount");
      }
     }
    }

   }
  }

  html.append("</tbody></table></div>");

  drawBoxFooter(html, colorClass);
 }
 
 protected void drawAnnotations( StringBuilder html, String colorClass, Collection<Annotation> annots )
 {
  if( annots == null )
   return;
  
   for( Annotation ant : annots )
   {
    Tag t = ant.getTag();
    
    String tName = t.getDescription();
    
    if( tName == null || tName.length() == 0 )
     tName = t.getName();
    
    drawBox(html,colorClass,tName,ant.getText()!=null?ant.getText().replaceAll("\\n", "<br>"):"");
    
   }
   html.append("<hr>");
 }
 
 protected void drawBoxHeader(StringBuilder html, String colorClass, String title)
 {
  html.append("<table style='border-collapse: collapse; width: 100%; table-layout: fixed; max-width: 800px'><tr class='roundBoxHeader'><td class='")
    .append(colorClass).append(" roundBoxLT'></td><td class='").append(colorClass).append("' rowspan=2 style='font-size: 17pt'><b>")
    .append(title);
  html.append("</b></td><td class='").append(colorClass).append(" roundBoxRT'></td></tr>" + "<tr style='height: 15px'><td class='").append(colorClass)
    .append("'></td><td class='").append(colorClass).append("'></td></tr>" + "<tr style='min-height: 70px'><td class='").append(colorClass).append(
      " roundBoxL'></td><td style='padding-top: 15px; font-size: 14pt'>");

 }
 
 protected void drawBoxFooter(StringBuilder html, String colorClass)
 {

  html.append("</td><td class='").append(colorClass).append(" roundBoxR'></td></tr>" + "<tr class='roundBoxFooter'><td class='").append(colorClass)
    .append(" roundBoxLB'></td>" + "<td class='").append(colorClass).append(" roundBoxB'></td><td class='").append(colorClass).append(
      " roundBoxRB'></td></tr></table><br>");

 }

 protected void drawBox(StringBuilder html, String colorClass, String title, String text)
 {
  drawBoxHeader(html,colorClass,title);

  html.append(text);

  drawBoxFooter(html, colorClass);
 }

 
 private void drawRow(StringBuilder html, String name, Summary vcn, Summary[] tagCounters, String nameCls, String cntClass, String tagClass)
 {
  html.append("<td class='").append(nameCls).append("'>").append(name).append("</td>");
  
  html.append("<td class='").append(cntClass).append("'>").append(vcn.getCount()).append("</td>");

  drawTagCounters(html, vcn, tagCounters, tagClass);

  html.append("</tr>");
 }
 
 private void drawTagCounters(StringBuilder html, Summary vcn, Summary[] tagCounters, String tagClass)
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
